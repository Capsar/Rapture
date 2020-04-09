package pq.rapture.module;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import pq.rapture.command.base.Command;
import pq.rapture.command.base.CommandManager;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.module.objects.WayPoint;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.FontUtil;
import pq.rapture.util.GameUtil;
import pq.rapture.util.RenderUtil;

public class WayPoints extends Module implements HasValues {

	private static CopyOnWriteArrayList<WayPoint> waypoints = new CopyOnWriteArrayList<WayPoint>();

	public WayPoints() {
		super(Protection.decrypt("B82B919C1DD8390AD3B7FB21B4B41647"), new String[] {
				Protection.decrypt("6B33EAF24B838ACD6D75CE27A74AD375"), Protection.decrypt("099BA300AC3EC334DF6ADBC093F69F8D"),
				Protection.decrypt("9F49151480059BE4ADFDF6EA2BA10F18") }, Protection
				.decrypt("1C3D1709983E1D236F5A64015C59F3151998A7E20748367A9BE8B67EC6F1C3ED"), Type.RENDER, "NONE", 0xFF10A1FF);
		CommandManager.commands.add(new Command(this.getAliases(), this.getDescription()) {
			@Override
			protected void onCommand(String[] args, String message) {
				if (args.length >= 3) {
					if (args[1].equalsIgnoreCase("rem")) {
						try {
							boolean found = false;
							for(WayPoint w : waypoints) {
								String preargs = args[0] + " " + args[1] + " ";
								String name = message.substring(preargs.length());
								if (w.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
									found = true;
									waypoints.remove(w);
									addChat(ChatEnum.NOTIFY, "Waypoint \"" + w.getName() + "\" has been removed from the list.");
									break;
								}
							}
							if (!found) {
								for(WayPoint w : waypoints) {
									String preargs = args[0] + " " + args[1] + " ";
									String name = message.substring(preargs.length());
									if (w.getName().replace(" ", "").contains(name.replace(" ", ""))) {
										found = true;
										waypoints.remove(w);
										addChat(ChatEnum.NOTIFY, "Waypoint \"" + w.getName() + "\" has been removed from the list.");
										break;
									}
								}
							}
							if (!found) {
								addChat(ChatEnum.ERROR, "No waypoint found with that name");
							}

						} catch (Exception e) {
							addChat(ChatEnum.ERROR, "You did something wrong.. ::#");
						}
					} else if (args[1].equalsIgnoreCase("add") && args.length == 5) {
						try {
							int x = Integer.parseInt(args[2]);
							int y = Integer.parseInt(args[3]);
							int z = Integer.parseInt(args[4]);
							int random = (new Random()).nextInt(1000);
							String name = "Unnamed WP (" + random + ")";
							String server = getMinecraft().getCurrentServerData().serverIP;
							WayPoint wp = new WayPoint(x, y, z, name, server);
							addChat(ChatEnum.NOTIFY, "Waypoint \"" + name + "\" has been added to the list.");
							waypoints.add(wp);
						} catch (Exception e) {
							addChat(ChatEnum.ERROR, "You did something wrong.. ::#");
						}
					} else if (args[1].equalsIgnoreCase("add") && args.length >= 6) {
						try {
							int x = Integer.parseInt(args[2]);
							int y = Integer.parseInt(args[3]);
							int z = Integer.parseInt(args[4]);
							String preargs = args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " ";
							String name = message.substring(preargs.length() + 1);
							String server = getMinecraft().getCurrentServerData().serverIP;
							WayPoint wp = new WayPoint(x, y, z, name, server);
							addChat(ChatEnum.NOTIFY, "Waypoint \"" + name + "\" has been added to the list.");
							waypoints.add(wp);
						} catch (Exception e) {
							addChat(ChatEnum.ERROR, "You did something wrong.. ::#");
						}
					}
				} else if (args.length == 2) {
					if (args[1].equalsIgnoreCase("list")) {
						if (!waypoints.isEmpty()) {
							String s = "";
							FontRenderer fr = getFontRenderer();
							for(WayPoint w : waypoints) {
								String wp = w.getIP() + " | " + w.getName() + " | <" + w.getX() + "> <" + w.getY() + "> <" + w.getZ() + ">";
								if (fr.getStringWidth(wp) > fr.getStringWidth(s)) s = wp;
							}

							String waypointslogo = "";

							for(String ss = ""; fr.getStringWidth(waypointslogo) < fr.getStringWidth(s); waypointslogo += "-") {
								if (fr.getStringWidth(waypointslogo) == fr.getStringWidth(s) / 2 - fr.getStringWidth("WayPoints") / 2) {
									waypointslogo += "WayPoints";
								}
							}

							addChat(ChatEnum.NOTIFY, waypointslogo);

							for(WayPoint w : waypoints) {
								addChat(ChatEnum.NOTIFY,
										w.getIP() + " | " + w.getName() + " | <" + w.getX() + "> <" + w.getY() + "> <" + w.getZ() + ">");
							}
						} else {
							addChat(ChatEnum.ERROR, "List is empty.");
						}
					} else if (args[1].equalsIgnoreCase("clear")) {
						waypoints.clear();
						addChat(ChatEnum.NOTIFY, "List has been cleared.");
					} else if (args[1].equalsIgnoreCase("here")) {
						try {
							int x = MathHelper.floor_double(getPlayer().posX);
							int y = MathHelper.floor_double(getPlayer().posY + 5);
							int z = MathHelper.floor_double(getPlayer().posZ);
							int random = (new Random()).nextInt(1000);
							String name = "Unnamed WP (" + random + ")";
							String server = getMinecraft().getCurrentServerData().serverIP;
							WayPoint wp = new WayPoint(x, y, z, name, server);
							addChat(ChatEnum.NOTIFY, "Waypoint \"" + name + "\" has been added to the list.");
							waypoints.add(wp);
						} catch (Exception e) {
							addChat(ChatEnum.ERROR, "You did something wrong.. ::#");
						}
					}

				} else {
					addChat(ChatEnum.NOTIFY, this.getCommand() + " add <x> <y> <z> <name> | adds a wp to the list.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " add <x> <y> <z> | quickly add a wp to the list.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " here | adds a wp at your current coords.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " rem <name> | Remove a certain waypoint.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " list | list all current Waypoints.");
					addChat(ChatEnum.NOTIFY, this.getCommand() + " clear | clear waypoint list.");

				}

			}
		});
	}

	@ETarget(eventClasses = { EventRenderGlobal.class })
	public void onRender3D(EventRenderGlobal glob) {

		checkDistance();

		for(WayPoint waypoint : waypoints) {
			String server = getMinecraft().getCurrentServerData().serverIP;
			if (!server.equalsIgnoreCase(waypoint.getIP())) continue;

			double x = waypoint.getX();
			double y = waypoint.getY();
			double z = waypoint.getZ();
			double playerX = getPlayer().lastTickPosX + (getPlayer().posX - getPlayer().lastTickPosX)
					* getMinecraft().timer.renderPartialTicks;
			double playerY = getPlayer().lastTickPosY + (getPlayer().posY - getPlayer().lastTickPosY)
					* getMinecraft().timer.renderPartialTicks;
			double playerZ = getPlayer().lastTickPosZ + (getPlayer().posZ - getPlayer().lastTickPosZ)
					* getMinecraft().timer.renderPartialTicks;
			double renderX = x - playerX;
			double renderY = y - playerY;
			double renderZ = z - playerZ;

			double distance = MathHelper.sqrt_double(renderX * renderX + renderY * renderY + renderZ * renderZ);
			float[] angles = GameUtil.getAngles(renderX + getPlayer().posX, renderY + getPlayer().posY, renderZ + getPlayer().posZ,
					getPlayer());
			float distYaw = GameUtil.getDistanceBetweenAngle(getPlayer().rotationYaw, angles[0]);
			float distPitch = GameUtil.getDistanceBetweenAngle(getPlayer().rotationPitch, angles[1]);

			RenderUtil.preRender();
			drawPointer(renderX, renderY, renderZ, waypoint, angles);
			RenderUtil.postRender();

			RenderUtil.preRender();
			drawWayPoint(renderX, renderY, renderZ, distance, waypoint, (distYaw + distPitch) / 2);
			RenderUtil.postRender();
		}
	}

	private void drawPointer(double renderX, double renderY, double renderZ, WayPoint wp, float[] angles) {
		double distance = Math.sqrt(renderX * renderX + renderY * renderY + renderZ * renderZ);
		float yaw = angles[0] * 0.017453292F;
		float pitch = angles[1] * 0.017453292F;
		double motionX = -(double) (MathHelper.sin(yaw) * 0.61F) / 1.2;
		double motionZ = (double) (MathHelper.cos(yaw) * 0.61F) / 1.2;

		RenderUtil.prePointRender();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3d(0.2, 0.3, 0.8);
		GL11.glVertex3d(0, 2, 0);
		GL11.glVertex3d(motionX / 1.2, 2, motionZ / 1.2);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3d(0.8, 0.2, 0.2);
		GL11.glVertex3d(motionX / 1.2, 2, motionZ / 1.2);
		GL11.glVertex3d(motionX, 2, motionZ);
		GL11.glEnd();

		FontRenderer fr = getFontRenderer();
		float var14 = 0.005F;
		GL11.glTranslated(motionX * 1.2, 2, motionZ * 1.2);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glScaled(-var14, -var14, distance);
		FontUtil.drawStringWithShadow(wp.getName() + " [" + (int) getPlayer().getDistance(wp.getX(), wp.getY(), wp.getZ()) + "]",
				-fr.getStringWidth(wp.getName() + " [" + (int) getPlayer().getDistance(wp.getX(), wp.getY(), wp.getZ()) + "]") / 2, -7,
				0xFFFFFF);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		RenderUtil.postPointRender();
	}

	private void drawWayPoint(double renderX, double renderY, double renderZ, double distance, WayPoint wp, double distAngle) {
		renderX += 0.5;
		renderY += 1;
		renderZ += 0.5;
		boolean looking = false;
		double size = distance / 80;
		double ysize = distance / 75;

		if (distAngle < 3) {
			looking = true;
			size *= 3;
			ysize *= 3.4;
		}

		double bounce = ysize * Math.sin((int) getMinecraft().getSystemTime() / 200F) / 5 + 0.4;
		GL11.glColor4d(0.4, 0.2, 0.8, 0.4);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);

		// //
		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glEnd();

		GL11.glColor4d(0.4, 0.2, 0.8, 1);
		GL11.glLineWidth(1.5F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);

		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);

		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);

		GL11.glVertex3d(renderX, renderY + 1.30 + ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ - 0.5 - size);

		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX + 0.5 + size, renderY + bounce, renderZ + 0.5 + size);

		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ - 0.5 - size);

		GL11.glVertex3d(renderX, renderY - 1.30 - ysize + bounce, renderZ);
		GL11.glVertex3d(renderX - 0.5 - size, renderY + bounce, renderZ + 0.5 + size);
		GL11.glEnd();
		drawTag(renderX, renderY, renderZ, wp, looking);
	}

	private void drawTag(double renderX, double renderY, double renderZ, WayPoint wp, boolean looking) {
		if (looking) {
			FontRenderer fr = getFontRenderer();
			double distance = Math.sqrt(renderX * renderX + renderY * renderY + renderZ * renderZ);
			float var14 = 0.016666668F * 1.6F;
			RenderUtil.prePointRender();
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glTranslated(renderX, renderY + 5, renderZ);
			GL11.glRotatef(-getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glScaled(-var14 * distance / 6, -var14 * distance / 6, distance);
			FontUtil.drawStringWithShadow(wp.getName(), -fr.getStringWidth(wp.getName()) / 2, -30, 0xFFFFFF);
			FontUtil.drawStringWithShadow("X: " + wp.getX(), -fr.getStringWidth(wp.getName()) / 2, -20, 0xFFFFFF);
			FontUtil.drawStringWithShadow("Y: " + wp.getY(), -fr.getStringWidth(wp.getName()) / 2, -10, 0xFFFFFF);
			FontUtil.drawStringWithShadow("Z: " + wp.getZ(), -fr.getStringWidth(wp.getName()) / 2, -0, 0xFFFFFF);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			RenderUtil.postPointRender();
		}
	}

	private void checkDistance() {
		for(WayPoint w : waypoints) {
			if (getPlayer().getDistance(w.getX(), w.getY(), w.getZ()) < 3) {
				waypoints.remove(w);
				addChat(ChatEnum.NOTIFY, "Waypoint \"" + w.getName() + "\" reached.");
			}
		}
	}

	private final static String WAYPOINTS = "WayPoints";

	private static final Value[] PARAMETERS = new Value[] { new Value(WAYPOINTS, waypoints) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(WAYPOINTS)) {
			String s = ",";
			if (!waypoints.isEmpty()) {
				for(WayPoint wp : waypoints) {
					String wpdata = wp.getX() + ";" + wp.getY() + ";" + wp.getZ() + ";" + wp.getName() + ";" + wp.getIP();
					s += wpdata + ",";
				}
			}
			return s;
		}

		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(WAYPOINTS)) {
			waypoints.clear();
			String[] obj = String.valueOf(v).split(",");
			for(String s : obj) {
				if (!s.equals("")) {
					try {
						String[] wpdata = s.split(";");
						WayPoint newwp = new WayPoint(wpdata[0], wpdata[1], wpdata[2], wpdata[3], wpdata[4]);
						waypoints.add(newwp);
					} catch (Exception oi) {
						break;
					}
				}
			}
		}
	}

}
