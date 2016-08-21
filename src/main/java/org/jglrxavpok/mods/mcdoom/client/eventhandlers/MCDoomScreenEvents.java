package org.jglrxavpok.mods.mcdoom.client.eventhandlers;

import javafx.scene.shape.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.client.render.TextureRegion;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.items.BFGItem;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@SideOnly(Side.CLIENT)
public class MCDoomScreenEvents {

    private final ResourceLocation bfgSheet;
    private final TextureRegion idleRegion;
    private final TextureRegion preparingRegion;
    private final TextureRegion firingRegion;
    private final TextureRegion muzzleFlashSmall;
    private final TextureRegion muzzleFlashBig;

    public MCDoomScreenEvents() {
        idleRegion = new TextureRegion(0, 77f/163f, 171f/502f, 1f);
        preparingRegion = new TextureRegion(173f/502f, 77f/163f, (173f+171f)/502f, 1f);
        firingRegion = new TextureRegion((173f+172f)/502f, 77f/163f, 1f, 1f);
        muzzleFlashSmall = new TextureRegion(0,0,82/502f,77f/163f);
        muzzleFlashBig = new TextureRegion(82f/502f,0f,(82f+139f)/502f,77f/163f);
        bfgSheet = new ResourceLocation(MCDoom.modid, "textures/hud/bfgsheet.png");
    }

    @SubscribeEvent
    public void onItemDrawing(RenderHandEvent event) {
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e instanceof EntityPlayer) {
            ItemStack currentItem = ((EntityPlayer) e).inventory.getCurrentItem();
            if(currentItem != null && currentItem.getItem() != null) {
                Item actualItem = currentItem.getItem();
                if(actualItem instanceof BFGItem) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiDrawing(RenderGameOverlayEvent.Pre evt) {
        Minecraft mc = Minecraft.getMinecraft();
        if(evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && mc.gameSettings.thirdPersonView == 0) {
            Entity e = mc.getRenderViewEntity();
            if(e instanceof EntityPlayer) {
                ItemStack currentItem = ((EntityPlayer) e).inventory.getCurrentItem();
                if(currentItem != null && currentItem.getItem() != null) {
                    Item actualItem = currentItem.getItem();
                    if(actualItem instanceof BFGItem) {
                        renderBFG((EntityPlayer)e, currentItem, evt.getResolution(), evt.getPartialTicks());
                    }
                }
            }
        }
    }

    private void renderBFG(EntityPlayer player, ItemStack currentItem, ScaledResolution resolution, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        EntityPlayer renderView = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

        GlStateManager.pushMatrix();
        float walkedDistance = renderView.distanceWalkedModified - renderView.prevDistanceWalkedModified;
        float partialDistance = (renderView.distanceWalkedModified + walkedDistance * partialTicks);

        float delta = 10f;
        float halfDelta = delta/2f;
        float displacement = 20f;
        float f5 = ((partialDistance*2f% delta)/delta);
        float bobbingOffsetX;
        if(f5 > 0.5f) {
            bobbingOffsetX = (((partialDistance*2f% halfDelta)/halfDelta) *2f -1f);
        } else {
            bobbingOffsetX = ((1f-(partialDistance*2f% halfDelta)/halfDelta) *2f -1f);
        }
        float bobbingOffsetY = (pseudoBellCurve(bobbingOffsetX/2f+0.5f))*10f;
        float w = 171f;
        float h = 86f;
        float y = resolution.getScaledHeight()-h + bobbingOffsetY;
        float x = resolution.getScaledWidth()/2f-w/2f + bobbingOffsetX*displacement;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(bfgSheet);
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);


        float z = -900f;
        TextureRegion region = idleRegion;
        if(player.getActiveItemStack() == currentItem) {
            region = preparingRegion;
        }

        if(currentItem.getItemDamage() != 0) {
            region = player.ticksExisted/4 % 2 == 0 ? idleRegion : preparingRegion;
        }

        // render muzzle flash
        if(player.getActiveItemStack() == currentItem) {
            int count = player.getItemInUseCount();
            int deltaTime = player.getItemInUseMaxCount()-count;
            float percent = (float)deltaTime/(float)player.getItemInUseMaxCount();
            if(percent >= 0.7f && percent <= .90f) {
                TextureRegion muzzleFlashRegion = percent >= .80f ? muzzleFlashBig : muzzleFlashSmall;
                float muzzleOffset = 47f;
                float muzzleFlashW = (muzzleFlashRegion.getMaxU()-muzzleFlashRegion.getMinU())*502f;
                float muzzleFlashH = 77f;
                y-=muzzleOffset;
                buffer.pos(x+w/2f-muzzleFlashW/2f, y+muzzleFlashH, z).tex(muzzleFlashRegion.getMinU(), muzzleFlashRegion.getMaxV()).endVertex();
                buffer.pos(x+w/2f+muzzleFlashW/2f, y+muzzleFlashH, z).tex(muzzleFlashRegion.getMaxU(), muzzleFlashRegion.getMaxV()).endVertex();
                buffer.pos(x+w/2f+muzzleFlashW/2f, y, z).tex(muzzleFlashRegion.getMaxU(), muzzleFlashRegion.getMinV()).endVertex();
                buffer.pos(x+w/2f-muzzleFlashW/2f, y, z).tex(muzzleFlashRegion.getMinU(), muzzleFlashRegion.getMinV()).endVertex();
                y+=muzzleOffset;
            } else if(percent >= .90f) {
                region = firingRegion;
            }
        }

        buffer.pos(x, y+h, z).tex(region.getMinU(), region.getMaxV()).endVertex();
        buffer.pos(x+w, y+h, z).tex(region.getMaxU(), region.getMaxV()).endVertex();
        buffer.pos(x+w, y, z).tex(region.getMaxU(), region.getMinV()).endVertex();
        buffer.pos(x, y, z).tex(region.getMinU(), region.getMinV()).endVertex();



        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private float pseudoBellCurve(float normalizedX) {
        // Actual function:
        // pseudoBellCurve(normX) = 2.64^{-\frac{1}{2}\cdot \left(\frac{x^2-x+.25}{0.0625}\right)} (made with graph.tk)
        // with normX = x / width
        //
        // Based on the normal distribution density function
        float exponent = normalizedX*normalizedX - normalizedX + 0.25f; // expanded version of (x-0.5)² where 0.5 is the mean
        exponent /= 0.0325; // magic number (very roughly the equivalent of the standard deviation
        exponent *= -0.5f; // untouched from normal distribution
        return (float) Math.exp(exponent);
    }
}
