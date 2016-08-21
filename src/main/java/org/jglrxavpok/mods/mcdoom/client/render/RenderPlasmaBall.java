package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;

import java.util.List;

public class RenderPlasmaBall extends Render<PlasmaBallEntity> {
    private final ResourceLocation sheet;
    private final TextureRegion[] aliveRegions;
    private final TextureRegion[] deadRegions;
    private final ResourceLocation beamTexture;

    public RenderPlasmaBall(RenderManager manager) {
        super(manager);
        sheet = new ResourceLocation(MCDoom.modid, "textures/entity/plasma_ball.png");
        beamTexture = new ResourceLocation("minecraft", "textures/entity/guardian_beam.png");

        aliveRegions = new TextureRegion[2];
        aliveRegions[0] = new TextureRegion(0,0,63f/512f,64f/256f);
        aliveRegions[1] = new TextureRegion(64f/512f,0,127f/512f,64f/256f);

        deadRegions = new TextureRegion[6];
        deadRegions[0] = new TextureRegion(128f/512f,0,(162f+128f-1f)/512f,128f/256f);
        deadRegions[1] = new TextureRegion(291f/512f,0,(291f+162f-1f)/512f,128f/256f);
        deadRegions[2] = new TextureRegion(0f/512f,128f/256f,161/512f,1f);
        deadRegions[3] = new TextureRegion(163f/512f,128f/256f,(163f+162f-1f)/512f,1f);
        deadRegions[4] = new TextureRegion(326f/512f,129f/256f,(326f+150f-1f)/512f,(129f+105f)/256f);
        deadRegions[5] = new TextureRegion(47f/512f,65/256f,(47f+74)/512f,(65+62f-1f)/256f);
    }

    @Override
    public void doRender(PlasmaBallEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float scale = 2f;
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(scale, scale, scale);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        TextureRegion region;
        if(entity.isDying()) {
            int index = (int) ((float)(PlasmaBallEntity.MAX_DEATH_COUNTER-entity.getDeathCounter()) / (float)PlasmaBallEntity.MAX_DEATH_COUNTER * deadRegions.length);
            index %= deadRegions.length;
            if(index < 0)
            index = deadRegions.length-1;
            region = deadRegions[index];
        } else {
            region = aliveRegions[entity.ticksExisted % aliveRegions.length];
        }
        float minU = region.getMinU();
        float maxU = region.getMaxU();
        float minV = region.getMinV();
        float maxV = region.getMaxV();
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-0.5D, -0.5D, 0.0D).tex((double)minU, (double)maxV).endVertex();
        vertexbuffer.pos(0.5D, -0.5D, 0.0D).tex((double)maxU, (double)maxV).endVertex();
        vertexbuffer.pos(0.5D, 0.5D, 0.0D).tex((double)maxU, (double)minV).endVertex();
        vertexbuffer.pos(-0.5D, 0.5D, 0.0D).tex((double)minU, (double)minV).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        renderTargets(entity, x, y, z);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderTargets(PlasmaBallEntity entity, double x, double y, double z) {
        bindTexture(beamTexture);
        List<Entity> targets = entity.getTargets();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        double renderPosX = getRenderManager().renderViewEntity.posX;
        double renderPosY = getRenderManager().renderViewEntity.posY;
        double renderPosZ = getRenderManager().renderViewEntity.posZ;
        float w = 0.5f;
        float minV = -(entity.getEntityWorld().getTotalWorldTime() /6f);

        float r = 88f/255f;
        float g = 188f/255f;
        float b = 88f/255f;
        float a = 1f;

        final float beamHeight = 0.75f;

        for(Entity t : targets) {
            float maxV = t.getDistanceToEntity(entity)/5f + minV;
            vertexbuffer.pos(x+w, y, z).tex(0.5, minV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(t.posX - renderPosX+w, t.posY - renderPosY, t.posZ - renderPosZ).tex(0.5, maxV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(t.posX - renderPosX+w, t.posY+beamHeight - renderPosY, t.posZ - renderPosZ).tex(0, maxV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(x+w, y+beamHeight, z).tex(0, minV).color(r,g,b,a).endVertex();

            vertexbuffer.pos(x+w, y+beamHeight, z).tex(0, minV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(t.posX - renderPosX+w, t.posY+beamHeight - renderPosY, t.posZ - renderPosZ).tex(0, maxV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(t.posX - renderPosX+w, t.posY - renderPosY, t.posZ - renderPosZ).tex(0.5, maxV).color(r,g,b,a).endVertex();
            vertexbuffer.pos(x+w, y, z).tex(0.5, minV).color(r,g,b,a).endVertex();
        }
        tessellator.draw();
    }

    protected void renderLivingLabel0(Entity e, String str, double x, double y, double z)
    {
        float f = this.renderManager.playerViewY;
        float f1 = this.renderManager.playerViewX;
        boolean flag1 = this.renderManager.options.thirdPersonView == 2;
        float f2 = e.height + 0.5F;
        EntityRenderer.func_189692_a(this.getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, 0, f, f1, flag1, false);
    }

    @Override
    protected ResourceLocation getEntityTexture(PlasmaBallEntity entity) {
        return sheet;
    }

}
