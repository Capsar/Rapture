package pq.rapture.module;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import com.ibm.icu.impl.duration.impl.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.command.base.Command;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.render.GUIUtil;
import pq.rapture.rxdy.EventBlockRender;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.EventSendChatMessage;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.Colors;
import pq.rapture.util.GameUtil;
import pq.rapture.util.NumberUtil;
import pq.rapture.util.RenderUtil;

public class BlockESP extends Module implements HasValues {

	private int range = 80;
	private static List<Integer> blocks = new ArrayList();
	private ArrayDeque<BlockObj> cache = new ArrayDeque<>();
	Framebuffer overlayFBO = new Framebuffer(getMinecraft().displayWidth, getMinecraft().displayHeight, true);
	Comparator<BlockObj> comparator = new Comparator<BlockESP.BlockObj>() {
		@Override
		public int compare(BlockObj o1, BlockObj o2) {
			double x = getPlayer().posX - o1.x;
			double y = (getPlayer().getEntityBoundingBox().maxY - 0.4) - o1.y;
			double z = getPlayer().posZ - o1.z;
			double dist = MathHelper.sqrt_double(x * x + y * y + z * z);

			double x2 = getPlayer().posX - o2.x;
			double y2 = (getPlayer().getEntityBoundingBox().maxY - 0.4) - o2.y;
			double z2 = getPlayer().posZ - o2.z;
			double dist2 = MathHelper.sqrt_double(x2 * x2 + y2 * y2 + z2 * z2);

			if (dist < dist2) return 1;
			else return -1;
		}
	};

	public BlockESP() {
		super(Protection.decrypt("4EBC6D9B6C6F3F34547DAD6F58497624"), new String[] { Protection.decrypt("414E8903D6E3D519BE0BF0FC1311A732") }, Protection
				.decrypt("43FFF5169211E5F8CABAD08596D9DB4DEBC417ABB26CE70B3F5F7911715F5535E94A82F0B0BDAD0FE65D79CEBB265524"), Type.RENDER, "NONE", 0xFFe6eb94);
		getCommandManager().commands.add(new Command(this.getAliases(), this.getDescription()) {
			@Override
			protected void onCommand(String[] args, String message) {
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("list")) {
						String s = "Blocks in the list:";
						if (!blocks.isEmpty()) {
							for (Integer b : blocks) {
								s += (" " + Block.getBlockById(b).getLocalizedName() + ",");
							}
							if (s.endsWith(",")) {
								s = s.substring(0, s.length() - 1) + ";";
							}
						} else {
							s = "List is empty.";
						}
						addChat(ChatEnum.NOTIFY, s);
					} else if (args[1].equalsIgnoreCase("clear")) {
						blocks.clear();
						cache.clear();
						addChat(ChatEnum.NOTIFY, "List has been cleared.");
					} else if (args[1].equalsIgnoreCase("reset")) {
						cache.clear();
						addChat(ChatEnum.NOTIFY, "Cache has been cleared.");
					} else {

						String input = args[1];
						Block block = null;

						if (NumberUtil.isInteger(input)) {
							block = Block.getBlockById(Integer.parseInt(input));
						} else {
							for (Iterator localIterator = Block.blockRegistry.iterator(); localIterator.hasNext();) {
								Block blockk = (Block) localIterator.next();
								if (blockk.getUnlocalizedName().replace("tile.", "").toLowerCase().equals(input.toLowerCase())) {
									block = blockk;
									break;
								}
							}
						}

						if (block == null) {
							String possibleBlocks = "";
							int amountFound = 0;
							for (Iterator localIterator = Block.blockRegistry.iterator(); localIterator.hasNext();) {
								Block blockk = (Block) localIterator.next();
								String blockName = blockk.getUnlocalizedName().replace("tile.", "").toLowerCase();
								if (blockName.contains(input.toLowerCase())) {
									amountFound++;
									possibleBlocks += "\"" + blockName + ", " + GameUtil.getBlockID(blockk) + "\" | ";
								}
							}

							addChat(ChatEnum.ERROR, "No block found, try it's ID.");
							if (amountFound != 0) {
								addChat(ChatEnum.NOTIFY, "You might be looking for: " + possibleBlocks.substring(0, possibleBlocks.length() - 3));
							}
							return;
						}

						if (blocks.contains(GameUtil.getBlockID(block))) {
							blocks.remove(blocks.indexOf(GameUtil.getBlockID(block)));
							cache.clear();
						} else {
							blocks.add(GameUtil.getBlockID(block));
							cache.clear();
						}

						GameUtil.loadRenderers();
						addChat(ChatEnum.NOTIFY, "Searching for " + getMod(BlockESP.class).blocks.size() + " block type(s)");

					}

				} else if (args.length == 3) {
					if (args[1].equals("look")) {
						String input = args[2];
						String possibleBlocks = "";
						int amountFound = 0;
						for (Iterator localIterator = Block.blockRegistry.iterator(); localIterator.hasNext();) {
							Block blockk = (Block) localIterator.next();
							String blockName = blockk.getUnlocalizedName().replace("tile.", "").toLowerCase();
							if (blockName.contains(input.toLowerCase())) {
								amountFound++;
								possibleBlocks += "\"" + blockName + ", " + GameUtil.getBlockID(blockk) + "\" | ";
							}
						}

						if (amountFound == 0) {
							addChat(ChatEnum.ERROR, "No block founds");
						} else {
							addChat(ChatEnum.NOTIFY, "Found: " + possibleBlocks.substring(0, possibleBlocks.length() - 3));
						}
					} else if (args[1].equals("all")) {
						String input = args[2];
						String possibleBlocks = "";
						int amountFound = 0;
						for (Iterator localIterator = Block.blockRegistry.iterator(); localIterator.hasNext();) {
							Block blockk = (Block) localIterator.next();
							String blockName = blockk.getUnlocalizedName().replace("tile.", "").toLowerCase();
							if (blockName.contains(input.toLowerCase())) {
								amountFound++;
								if (blocks.contains(GameUtil.getBlockID(blockk))) {
									possibleBlocks += "\"" + Colors.RED + blockName + Colors.WHITE + "\" | ";
									blocks.remove(blocks.indexOf(GameUtil.getBlockID(blockk)));
									cache.clear();
								} else {
									possibleBlocks += "\"" + Colors.GREEN + blockName + Colors.WHITE + "\" | ";
									blocks.add(GameUtil.getBlockID(blockk));
									cache.clear();
								}
							}
						}

						if (amountFound == 0) {
							addChat(ChatEnum.ERROR, "No block founds");
						} else {
							addChat(ChatEnum.NOTIFY, "Modified: " + possibleBlocks.substring(0, possibleBlocks.length() - 3));
						}
						
					} else {
						addChat(ChatEnum.NOTIFY, this.getCommand() + " all <name> | search all blocks containing <name>.");
						addChat(ChatEnum.NOTIFY, this.getCommand() + " look <name> | lookup blocknames containing <name>.");
						addChat(ChatEnum.NOTIFY, this.getCommand() + " <id/name> | toggles a block in the list.");
						addChat(ChatEnum.NOTIFY, this.getCommand() + " list | lists all the blocks.");
						addChat(ChatEnum.NOTIFY, this.getCommand() + " clear | clears the xraylist.");
					}
				} else {
					addChat(ChatEnum.NOTIFY, this.getCommand() + " all <name> | search all blocks containing <name>.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " look <name> | lookup blocknames containing <name>.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " <id/name> | toggles a block in the list.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " list | lists all the blocks.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " clear | clears the xraylist.");
				}
			}
		});

	}

	@Override
	public boolean onEnable() {
		if (getPlayer() == null) return false;

		displayListNormalBlock = GL11.glGenLists(1);
		GL11.glNewList(displayListNormalBlock, GL11.GL_COMPILE);
		Block bl = Blocks.stone;
		RenderUtil.drawSearchBlock(new AxisAlignedBB(bl.getBlockBoundsMinX(), bl.getBlockBoundsMinY(), bl.getBlockBoundsMinZ(), bl.getBlockBoundsMaxX(), bl.getBlockBoundsMaxY(), bl.getBlockBoundsMaxZ()));
		GL11.glEndList();

		displayListChest = GL11.glGenLists(1);
		GL11.glNewList(displayListChest, GL11.GL_COMPILE);
		Block chest = Blocks.chest;
		RenderUtil.drawSearchBlock(new AxisAlignedBB(chest.getBlockBoundsMinX(), chest.getBlockBoundsMinY(), chest.getBlockBoundsMinZ(), chest.getBlockBoundsMaxX(), chest.getBlockBoundsMaxY(), chest.getBlockBoundsMaxZ()));
		GL11.glEndList();

		displayListDoubleChest = GL11.glGenLists(1);
		GL11.glNewList(displayListDoubleChest, GL11.GL_COMPILE);
		RenderUtil.drawSearchBlock(new AxisAlignedBB(chest.getBlockBoundsMinX(), chest.getBlockBoundsMinY(), chest.getBlockBoundsMinZ(), 1 + chest.getBlockBoundsMaxX(), chest.getBlockBoundsMaxY(), chest.getBlockBoundsMaxZ()));
		GL11.glEndList();

		this.cache.clear();
		GameUtil.loadRenderers();
		return super.onEnable();
	}

	private int displayListNormalBlock = -1;
	private int displayListChest = -1;
	private int displayListDoubleChest = -1;

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventRenderGlobal.class, EventBlockRender.class })
	public void onEvent(EventRenderGlobal global, EventBlockRender blockrender) {
		if (global != null) {
			List temp = new ArrayList<>();

			Object[] a = cache.toArray();
			Arrays.sort(a, (Comparator) comparator);
			cache.clear();
			for (Object ob : a) {
				cache.addFirst((BlockObj) ob);
			}

			GlStateManager.enableBlend();
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GL11.glDisable(GL11.GL_DITHER);
			GL11.glDisable(GL11.GL_STENCIL_TEST);

			Frustrum frustrum = new Frustrum();
			frustrum.setPosition(getRenderManager().viewerPosX, getRenderManager().viewerPosY, getRenderManager().viewerPosZ);
			for (Iterator<BlockObj> iterator = this.cache.iterator(); iterator.hasNext();) {
				BlockObj bloo = iterator.next();
				if (!frustrum.isBoundingBoxInFrustum(new AxisAlignedBB(new BlockPos(bloo.x, bloo.y, bloo.z), new BlockPos(bloo.x, bloo.y, bloo.z).add(1, 1, 1)))) continue;

				float dx = (float) (getPlayer().posX - bloo.x);
				float dz = (float) (getPlayer().posZ - bloo.z);
				float dist = MathHelper.sqrt_double(dx * dx + dz * dz);
				Block bl = GameUtil.getBlock(bloo.x, bloo.y, bloo.z);
				if (dist > this.range || !isBlockEnabled(bl)) {
					temp.add(bloo);
				} else {
					double x = bloo.x - getRenderManager().viewerPosX;
					double y = bloo.y - getRenderManager().viewerPosY;
					double z = bloo.z - getRenderManager().viewerPosZ;
					int color = getBlockColor(bl);
					float red = (color >> 16 & 0xFF) / 255.0F;
					float green = (color >> 8 & 0xFF) / 255.0F;
					float blue = (color & 0xFF) / 255.0F;
					GL11.glPushMatrix();
					GL11.glColor4f(red, green, blue, 0.4F);
					GL11.glTranslated(x, y, z);
					if (bl == Blocks.chest || bl == Blocks.ender_chest || bl == Blocks.trapped_chest) {
						double xo = 1.0D;
						double zo = 1.0D;
						boolean found = false;
						for (EnumFacing side : EnumFacing.values()) {
							if (side.getAxisDirection().getOffset() == 0) continue;

							int xOffset = 0;
							int zOffset = 0;
							if (side.getAxis() == Axis.X) {
								xOffset += side.getAxisDirection().getOffset();
							} else if (side.getAxis() == Axis.Z) {
								zOffset += side.getAxisDirection().getOffset();
							}
							Block block = GameUtil.getBlock(new BlockPos(bloo.x, bloo.y, bloo.z).add(xOffset, 0, zOffset));
							if (block == bl) {
								xo = xOffset > 0 ? 2.0F : xOffset < 0 ? -0.9F : 1.0F;
								zo = zOffset > 0 ? 2.0F : zOffset < 0 ? -0.9F : 1.0F;
								found = true;
							}
						}
						if (found) {
							if (xo > 1.0D) {
								GL11.glCallList(displayListDoubleChest);
								GL11.glPopMatrix();
								continue;
							} else if (zo > 1.0D) {
								GL11.glTranslated(1, 0, 0);
								GL11.glRotated(270, 0, 1, 0);
								GL11.glCallList(displayListDoubleChest);
								GL11.glPopMatrix();
								continue;
							} else if (xo == 1.0D && zo == 1.0D) {
								GL11.glCallList(displayListChest);
								GL11.glPopMatrix();
								continue;
							}
						}
					} else {
						GL11.glCallList(displayListNormalBlock);
					}

					GL11.glPopMatrix();
				}
			}
			this.cache.removeAll(temp);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GL11.glEnable(GL11.GL_DITHER);
			GL11.glEnable(GL11.GL_STENCIL_TEST);

			GL11.glColor3d(1, 1, 1);

		} else if (blockrender != null) {
			if (this.blocks.isEmpty() || this.cache.size() >= 10000) return;
			int x = blockrender.getPos().getX();
			int y = blockrender.getPos().getY();
			int z = blockrender.getPos().getZ();
			int block = GameUtil.getBlockID(blockrender.getBlock());
			if ((!isCached(x, y, z)) && isBlockEnabled(blockrender.getBlock())) {
				this.cache.add(new BlockObj(new BlockPos(x, y, z)));
			}
		}
	}

	public boolean isBlockEnabled(Block blo) {
		return blocks.contains(GameUtil.getBlockID(blo));
	}

	private boolean isCached(double x, double y, double z) {
		for (BlockObj bloo : this.cache) {
			if ((bloo.x == x) && (bloo.y == y) && (bloo.z == z)) { return true; }
		}
		return false;
	}

	private int getBlockColor(Block block) {
		int color = block.getMaterial().getMaterialMapColor().colorValue;
		if (block == Blocks.mob_spawner) {
			color = 0xb77fd7;
		} else if (block == Blocks.quartz_ore) {
			color = 0x89293a;
		}

		switch (Block.getIdFromBlock(block)) {
		case 15:
			color = 0xe5b295;
			break;
		case 56:
		case 57:
		case 116:
			color = 6155509;
			break;
		case 129:
		case 133:
			color = 1564002;
			break;
		case 14:
		case 41:
			color = 16576075;
			break;
		case 16:
		case 173:
			color = 3618615;
			break;
		case 21:
		case 22:
			color = 1525445;
			break;
		case 73:
		case 74:
		case 152:
			color = 16711680;
			break;
		case 61:
		case 62:
			color = 16658167;
			break;
		case 49:
			color = 3944534;
			break;
		case 146:
			color = 13474867;
			break;
		case 54:
			color = 13483059;
			break;
		case 130:
			color = 14614999;
			break;
		}

		return color == 0 ? 1216104 : color;
	}

	private class BlockObj {
		private int x;
		private int y;
		private int z;
		private Block block;

		public BlockObj(BlockPos blockPos) {
			this.x = blockPos.getX();
			this.y = blockPos.getY();
			this.z = blockPos.getZ();
		}
	}

	private final static String RANGE = Protection.decrypt("2C65158B9673A42B0F22377D94599C4E");
	private final static String BLOCKLIST = Protection.decrypt("B92781A8815C00050C82FC434603F8B6");
	private static final Value[] PARAMETERS = new Value[] { new Value(RANGE, 10, 256, 5), new Value(BLOCKLIST, blocks) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(RANGE)) return range;
		else if (n.equals(BLOCKLIST)) {
			String s = ",";
			if (!blocks.isEmpty()) {
				for (Integer i : blocks) {
					s += i + ",";
				}
			}
			return s;
		}

		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(RANGE)) range = (Integer) v;
		else if (n.equals(BLOCKLIST)) {
			blocks.clear();
			String[] obj = String.valueOf(v).split(",");
			for (String s : obj) {
				if (!s.equals("")) {
					try {
						int i = Integer.parseInt(s);
						blocks.add(i);
					} catch (Exception oi) {
						oi.printStackTrace();
						break;
					}
				}
			}
		}
	}
}