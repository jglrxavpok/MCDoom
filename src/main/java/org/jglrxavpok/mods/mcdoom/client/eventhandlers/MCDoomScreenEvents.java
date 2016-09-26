package org.jglrxavpok.mods.mcdoom.client.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy;
import org.jglrxavpok.mods.mcdoom.client.particle.EntityGoreFX;
import org.jglrxavpok.mods.mcdoom.client.render.DoomHUDRenderer;
import org.jglrxavpok.mods.mcdoom.client.render.WeaponRenderer;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;

import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class MCDoomScreenEvents {

    private final MCDoomClientProxy proxy;
    private DoomHUDRenderer hudRenderer;

    public MCDoomScreenEvents(MCDoomClientProxy proxy) {
        this.proxy = proxy;
        hudRenderer = new DoomHUDRenderer();
    }

    @SubscribeEvent
    public void onItemDrawing(RenderHandEvent event) {
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(MCDoom.instance.getDoomItemPoseProperty().getBoolean()) {
            if (e instanceof EntityPlayer) {
                ItemStack currentItem = ((EntityPlayer) e).inventory.getCurrentItem();
                if (currentItem != null && currentItem.getItem() != null) {
                    Item actualItem = currentItem.getItem();
                    if (actualItem instanceof WeaponItem) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiDrawing(RenderGameOverlayEvent.Pre evt) {
        Minecraft mc = Minecraft.getMinecraft();
        float verticalOffset = 0;
        if (evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (MCDoom.instance.getDoomUIProperty().getBoolean()) {
                verticalOffset = hudRenderer.draw(evt.getResolution(), evt.getPartialTicks());
                evt.setCanceled(true);
            }

            if (mc.gameSettings.thirdPersonView == 0) {
                if(MCDoom.instance.getDoomItemPoseProperty().getBoolean()) {
                    renderWeapon(mc, evt.getResolution(), evt.getPartialTicks(), -verticalOffset);
                }
            }
        }

        if (MCDoom.instance.getDoomUIProperty().getBoolean()) {
            switch (evt.getType()) {
                case ARMOR:
                case EXPERIENCE:
                case HEALTH:
                case HOTBAR:
                    evt.setCanceled(true);
            }
        }
    }

    private void renderWeapon(Minecraft mc, ScaledResolution resolution, float partialTicks, float verticalOffset) {
        Entity e = mc.getRenderViewEntity();
        if(e instanceof EntityPlayer) {
            ItemStack currentItem = ((EntityPlayer) e).inventory.getCurrentItem();
            if(currentItem != null && currentItem.getItem() != null) {
                Item actualItem = currentItem.getItem();
                if(actualItem instanceof WeaponItem) {
                    WeaponRenderer renderer = proxy.getRenderer(((WeaponItem) actualItem).getDefinition().getId());
                    if(renderer != null) {
                        renderer.renderWeapon((EntityPlayer)e, currentItem, resolution, partialTicks, verticalOffset);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent evt) {
        Entity entityIn = Minecraft.getMinecraft().getRenderViewEntity();
        float partialTicks = evt.getPartialTicks();

        float f = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f2 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();

        Particle.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        Particle.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        Particle.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        Particle.field_190016_K = entityIn.getLook(partialTicks);

        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 1f/255f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        Minecraft.getMinecraft().renderEngine.bindTexture(EntityGoreFX.GORE_PARTICLE_LOCATION);
        vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        for (final EntityGoreFX particle : proxy.getGoreParticles())
        {
            try
            {
                particle.renderParticle0(vertexbuffer, entityIn, partialTicks, f, f4, f1, f2, f3);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Gore Particles");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Gore Particles being rendered");
                crashreportcategory.setDetail("Particle", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return particle.toString();
                    }
                });
                crashreportcategory.setDetail("Particle Type", new ICrashReportDetail<String>()
                {
                    public String call() throws Exception
                    {
                        return "MCDoom - Gore";
                    }
                });
                throw new ReportedException(crashreport);
            }
        }

        tessellator.draw();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
    }

}
