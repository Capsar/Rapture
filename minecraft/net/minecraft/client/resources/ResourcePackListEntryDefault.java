package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private final IResourcePack field_148320_d;
    private final ResourceLocation resourcePackIcon;


    public ResourcePackListEntryDefault(GuiScreenResourcePacks resourcePacksGUIIn)
    {
        super(resourcePacksGUIIn);
        this.field_148320_d = this.mc.getResourcePackRepository().rprDefaultResourcePack;
        DynamicTexture var2;

        try
        {
            var2 = new DynamicTexture(this.field_148320_d.getPackImage());
        }
        catch (IOException var4)
        {
            var2 = TextureUtil.missingTexture;
        }

        this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", var2);
    }

    protected String func_148311_a()
    {
        try
        {
            PackMetadataSection var1 = (PackMetadataSection)this.field_148320_d.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");

            if (var1 != null)
            {
                return var1.getPackDescription().getFormattedText();
            }
        }
        catch (JsonParseException var2)
        {
            logger.error("Couldn\'t load metadata info", var2);
        }
        catch (IOException var3)
        {
            logger.error("Couldn\'t load metadata info", var3);
        }

        return EnumChatFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
    }

    protected boolean func_148309_e()
    {
        return false;
    }

    protected boolean func_148308_f()
    {
        return false;
    }

    protected boolean func_148314_g()
    {
        return false;
    }

    protected boolean func_148307_h()
    {
        return false;
    }

    protected String func_148312_b()
    {
        return "Default";
    }

    protected void func_148313_c()
    {
        this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
    }

    protected boolean func_148310_d()
    {
        return false;
    }
}
