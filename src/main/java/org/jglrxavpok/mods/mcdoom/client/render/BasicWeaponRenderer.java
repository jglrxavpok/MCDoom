package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class BasicWeaponRenderer extends WeaponRenderer {

    private final ResourceLocation bfgSheet;
    private final TextureRegion idleRegion;
    private final TextureRegion preparingRegion;
    private final TextureRegion firingRegion;
    private final TextureRegion muzzleFlashSmall;
    private final TextureRegion muzzleFlashBig;

    public BasicWeaponRenderer() {
        idleRegion = new TextureRegion(0, 77f/163f, 171f/502f, 1f);
        preparingRegion = new TextureRegion(173f/502f, 77f/163f, (173f+171f)/502f, 1f);
        firingRegion = new TextureRegion((173f+172f)/502f, 77f/163f, 1f, 1f);
        muzzleFlashSmall = new TextureRegion(0,0,82/502f,77f/163f);
        muzzleFlashBig = new TextureRegion(82f/502f,0f,(82f+139f)/502f,77f/163f);
        bfgSheet = new ResourceLocation(MCDoom.modid, "textures/hud/bfgsheet.png");
    }

    @Override
    public void renderWeapon(EntityPlayer player, ItemStack currentItem, ScaledResolution resolution, float partialTicks, float verticalOffset) {
        // TODO: Remove old code from BFG renderer
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        EntityPlayer renderView = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

        GlStateManager.pushMatrix();
        float xDisplacement = 20f;
        float yDisplacement = 10f;
        float w = 171f;
        float h = 86f;
        float y = resolution.getScaledHeight()-h + getBobbingY(renderView, partialTicks)*yDisplacement;
        float x = resolution.getScaledWidth()/2f-w/2f + getBobbingX(renderView, partialTicks)*xDisplacement;
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

}
