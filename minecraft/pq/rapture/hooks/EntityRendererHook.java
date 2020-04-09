package pq.rapture.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.EventManager;

public class EntityRendererHook extends EntityRenderer {
	Minecraft mc;

	public EntityRendererHook(Minecraft p_i45076_1_, IResourceManager p_i45076_2_) {
		super(p_i45076_1_, p_i45076_2_);
		mc = p_i45076_1_;
	}

	protected void renderHand(float par1, int par2) {
		super.renderHand(par1, par2);
	}
}
