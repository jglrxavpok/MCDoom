package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DoomHUDRenderer {

    private final ResourceLocation hudLocation;
    private final static String allowedChars = "0123456789%";
    private final static int[] bigFontCharWidth = {16,12,15,15,15,15,15,15,15,15,14};
    private final static int[] bigFontCharX = {0,16,28,43,58,73,88,103,118,133,148};
    private final ResourceLocation bigFontLocation;

    public DoomHUDRenderer() {
        hudLocation = new ResourceLocation(MCDoom.modid, "textures/hud/doomHUD.png");
        bigFontLocation = new ResourceLocation(MCDoom.modid, "textures/hud/bigFont.png");
    }

    public float draw(ScaledResolution resolution, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.renderEngine;


        float screenW = resolution.getScaledWidth();
        float screenH = resolution.getScaledHeight();
        float w = screenW;
        float yScale = (screenW/320f);
        float h = 32f*yScale;
        float y = screenH-h;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        textureManager.bindTexture(hudLocation);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(0,y+h,0).tex(0,1).endVertex();
        buffer.pos(w,y+h,0).tex(1,1).endVertex();
        buffer.pos(w,y,0).tex(1,0).endVertex();
        buffer.pos(0,y,0).tex(0,0).endVertex();

        tessellator.draw();

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        int health = (int) (player.getHealth()/player.getMaxHealth() * 100f);
        float healthX = 103f-getTextWidth(health+"%")*0.9f;
        float healthY = 25;
        renderPercentage(health, healthX*yScale, screenH-healthY*yScale, yScale*.9f);

        int armor = (int) (player.getTotalArmorValue()*10);
        float armorX = 235f-getTextWidth(armor+"")*0.9f;
        float armorY = 25;
        renderText(""+armor, armorX*yScale, screenH-armorY*yScale, yScale*.9f);

        return h;
    }

    private void renderPercentage(int number, float x, float y, float scale) {
        renderText(number+"%", x, y, scale);
    }

    private void renderText(String text, float x, float y, float scale) {
        char[] chars = text.toCharArray();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        Minecraft.getMinecraft().renderEngine.bindTexture(bigFontLocation);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        float deltaX = 0;
        for(char c : chars) {
            renderChar(c, x+deltaX, y, buffer, scale);
            deltaX += bigFontCharWidth[allowedChars.indexOf(c)]*scale;
        }

        tessellator.draw();
    }

    private float getTextWidth(String text) {
        char[] chars = text.toCharArray();
        float w = 0;
        for(char c : chars) {
            w += bigFontCharWidth[allowedChars.indexOf(c)];
        }
        return w;
    }

    private void renderChar(char c, float x, float y, VertexBuffer buffer, float scale) {
        int index = allowedChars.indexOf(c);
        if(index < 0)
            return;

        float w = bigFontCharWidth[index]*scale;
        float h = 16f*scale;
        float minU = (bigFontCharX[index])/(162f);
        float maxU = 1f;
        if(index+1 < allowedChars.length()) {
            maxU = (bigFontCharX[index+1]-1)/(162f);
        }
        float minV = 0f;
        float maxV = 1f;

        buffer.pos(x,y+h,0).tex(minU,maxV).endVertex();
        buffer.pos(x+w,y+h,0).tex(maxU,maxV).endVertex();
        buffer.pos(x+w,y,0).tex(maxU,minV).endVertex();
        buffer.pos(x,y,0).tex(minU,minV).endVertex();
    }
}
