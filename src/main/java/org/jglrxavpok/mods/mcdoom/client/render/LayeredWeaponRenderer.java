package org.jglrxavpok.mods.mcdoom.client.render;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class LayeredWeaponRenderer extends WeaponRenderer {

    private final static Comparator<WeaponLayer> layerComparator = new Comparator<WeaponLayer>() {
        @Override
        public int compare(WeaponLayer o1, WeaponLayer o2) {
            int locationComparison = o1.getTexture().getResourcePath().compareTo(o2.getTexture().getResourcePath());
            if(locationComparison != 0)
                return locationComparison;
            return Double.compare(o1.getZLevelOffset(), o2.getZLevelOffset());
        }
    };

    private final List<WeaponLayer> layers;
    private final Map<WeaponLayer, WeaponPredicate> layerPredicates;

    public LayeredWeaponRenderer() {
        layers = Lists.newArrayList();
        layerPredicates = Maps.newHashMap();
    }

    @Override
    public void renderWeapon(EntityPlayer player, ItemStack currentItem, ScaledResolution resolution, float partialTicks) {
        if(Minecraft.getMinecraft().gameSettings.keyBindSwapHands.isKeyDown()) {
            Collections.sort(layers, layerComparator);
            System.out.println("reordering!");
            // TODO: remove
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        EntityPlayer renderView = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

        float xDisplacement = 20f;
        float yDisplacement = 10f;
        float bobbingX = getBobbingX(renderView, partialTicks) * xDisplacement;
        float bobbingY = getBobbingY(renderView, partialTicks) * yDisplacement;

        ResourceLocation previousTexture = null;
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        TextureManager texManager = Minecraft.getMinecraft().getTextureManager();

        for (WeaponLayer l : layers) {
            if(!layerPredicates.get(l).apply(currentItem, player))
                continue;
            if(previousTexture == null) {
                texManager.bindTexture(l.getTexture());
            }
            if(previousTexture != null && l.getTexture() != previousTexture) {
                tessellator.draw(); // flush already drawn regions
                texManager.bindTexture(l.getTexture());
                buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR); // restart drawing
            }

            TextureRegion region = l.getRegion();
            float w = (region.getMaxU()-region.getMinU()) * l.getTextureWidth();
            float h = (region.getMaxV()-region.getMinV()) * l.getTextureHeight();
            float y = resolution.getScaledHeight()-h + l.getOffsetY();
            float x = resolution.getScaledWidth()/2f-w/2f + l.getOffsetX();
            int z = -900 + l.getZLevelOffset();
            if(l.isBobbing()) {
                x += bobbingX;
                y += bobbingY;
            }

            float r = 1f;
            float g = 1f;
            float b = 1f;
            float a = 1f;

            buffer.pos(x, y+h, z).tex(region.getMinU(), region.getMaxV()).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).tex(region.getMaxU(), region.getMaxV()).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).tex(region.getMaxU(), region.getMinV()).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).tex(region.getMinU(), region.getMinV()).color(r, g, b, a).endVertex();

            previousTexture = l.getTexture();
        }
        tessellator.draw();
    }

    public void addLayer(WeaponPredicate predicate, WeaponLayer layer) {
        layers.add(layer);
        layerPredicates.put(layer, predicate);

        Collections.sort(layers, layerComparator);
    }

    public static class WeaponLayer {
        private final boolean bobbing;
        private final ResourceLocation texture;
        private final TextureRegion region;
        private final int offsetX;
        private final int offsetY;
        private final int zLevelOffset;
        private final float texW;
        private final float texH;

        public WeaponLayer(boolean bobbing, ResourceLocation texture, TextureRegion region, int offsetX, int offsetY, int zLevelOffset, float texW, float texH) {
            this.bobbing = bobbing;
            this.texture = texture;
            this.region = region;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.zLevelOffset = zLevelOffset;
            this.texW = texW;
            this.texH = texH;
        }

        public float getTextureWidth() {
            return texW;
        }

        public float getTextureHeight() {
            return texH;
        }

        public boolean isBobbing() {
            return bobbing;
        }

        public TextureRegion getRegion() {
            return region;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public int getZLevelOffset() {
            return zLevelOffset;
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }
}
