package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillager extends RenderLiving
{
    private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
    private static final ResourceLocation farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
    private static final ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
    private static final ResourceLocation priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
    private static final ResourceLocation smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
    private static final ResourceLocation butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");


    public RenderVillager(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelVillager(0.0F), 0.5F);
        this.addLayer(new LayerCustomHead(this.func_177134_g().villagerHead));
    }

    public ModelVillager func_177134_g()
    {
        return (ModelVillager)super.getMainModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityVillager entity)
    {
        switch (entity.getProfession())
        {
            case 0:
                return farmerVillagerTextures;

            case 1:
                return librarianVillagerTextures;

            case 2:
                return priestVillagerTextures;

            case 3:
                return smithVillagerTextures;

            case 4:
                return butcherVillagerTextures;

            default:
                return villagerTextures;
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityVillager entitylivingbaseIn, float partialTickTime)
    {
        float var3 = 0.9375F;

        if (entitylivingbaseIn.getGrowingAge() < 0)
        {
            var3 = (float)(var3 * 0.5D);
            this.shadowSize = 0.25F;
        }
        else
        {
            this.shadowSize = 0.5F;
        }

        GlStateManager.scale(var3, var3, var3);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    @Override
	protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime)
    {
        this.preRenderCallback((EntityVillager)entitylivingbaseIn, partialTickTime);
    }

    @Override
	public ModelBase getMainModel()
    {
        return this.func_177134_g();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
	protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityVillager)entity);
    }
}
