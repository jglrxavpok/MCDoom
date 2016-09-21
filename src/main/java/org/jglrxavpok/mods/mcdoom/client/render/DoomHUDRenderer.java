package org.jglrxavpok.mods.mcdoom.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.items.FunWeaponItem;
import org.jglrxavpok.mods.mcdoom.common.items.ItemAmmo;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DoomHUDRenderer {

    private static final int INFINITE_AMMO = -1;
    private final ResourceLocation hudLocation;

    private final static String allowedChars = "0123456789%\u221E"; // \u221E is the Unicode value for the infinity symbol: ∞
    private final static int[] bigFontCharWidth = {16,12,15,15,15,15,15,15,15,15,14,15,16};
    private final static int[] bigFontCharX = {0,16,28,43,58,73,88,103,118,133,148,162};
    private final ResourceLocation bigFontLocation;
    private final ResourceLocation inventoryFontLocation;
    private final ModelPlayer playerModel;

    public DoomHUDRenderer() {
        hudLocation = new ResourceLocation(MCDoom.modid, "textures/hud/doomHUD.png");
        bigFontLocation = new ResourceLocation(MCDoom.modid, "textures/hud/bigFont.png");
        inventoryFontLocation = new ResourceLocation(MCDoom.modid, "textures/hud/invFont.png");
        playerModel = new ModelPlayer(1, false);
    }

    public float draw(ScaledResolution resolution, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.renderEngine;


        float screenW = resolution.getScaledWidth();
        float screenH = resolution.getScaledHeight();
        float w = screenW;
        float yScale = (screenW/262f);
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
        float healthX = 102f-getTextWidth(health+"%")*0.9f;
        float healthY = 25;
        renderText(health+"%", healthX*yScale, screenH-healthY*yScale, yScale*.9f);

        int armor = (int) (player.getTotalArmorValue()*10);
        float armorX = 198f-getTextWidth(armor+"")*0.9f;
        float armorY = 25;
        renderText(""+armor, armorX*yScale, screenH-armorY*yScale, yScale*.9f);

        int currentAmmo = countAmmo(player);
        String currentAmmoText;
        switch (currentAmmo) {
            case INFINITE_AMMO:
                currentAmmoText = "\u221E";
                break;

            default:
                currentAmmoText = String.valueOf(currentAmmo);
        }
        float ammoX = 44f-getTextWidth(currentAmmoText)*0.9f;
        float ammoY = 25;
        renderText(currentAmmoText, ammoX*yScale, screenH-ammoY*yScale, yScale*.9f);

//        renderPlayerHead(screenW, screenH, yScale);

        renderInventory(player, screenW, screenH, yScale);

        return h;
    }

    private int countAmmo(EntityPlayerSP player) {
        ItemStack current = player.inventory.getCurrentItem();
        if(current != null) {
            if(current.getItem() instanceof ItemBow) {
                int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, current);
                if(enchant > 0) {
                    return INFINITE_AMMO;
                }
                return countItem(player.inventory, ItemArrow.class);
            } else if(current.getItem() instanceof FunWeaponItem) {
                return INFINITE_AMMO;
            } else if(current.getItem() instanceof WeaponItem) {
                return countAmmo(player.inventory, ((WeaponItem) current.getItem()).getDefinition().getAmmoType());
            }
        }
        return INFINITE_AMMO;
    }

    private int countAmmo(InventoryPlayer inventory, String ammoType) {
        int counter = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(stack != null && stack.getItem() instanceof ItemAmmo) {
                ItemAmmo ammo = (ItemAmmo) stack.getItem();
                if(ammo.getAmmoType().equals(ammoType))
                    counter += stack.stackSize;
            }
        }
        return counter;
    }

    private int countItem(InventoryPlayer inventory, Class<? extends Item> soughtItemClass) {
        int counter = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(stack != null && stack.getItem() != null) {
                Class<? extends Item> itemClass = stack.getItem().getClass();
                if(soughtItemClass.isAssignableFrom(itemClass)) {
                    counter += stack.stackSize;
                }
            }
        }
        return counter;
    }

    private void renderInventory(EntityPlayerSP player, float screenW, float screenH, float yScale) {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.renderEngine;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        textureManager.bindTexture(inventoryFontLocation);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory[i];

            final float singleCharW = 4f;
            boolean equipped = player.inventory.currentItem == i;
            if(stack != null || equipped) { // makes sure the player allows know where the hotbar cursor is
                float minU = Math.min(1, ((singleCharW + 1) * (i + 1)) / (49f));
                float maxU = Math.min(1, minU + (singleCharW / 49f));
                float minV = 0f;
                float maxV = 6f / (13f);

                if (equipped) {
                    minV = 7f / (13f);
                    maxV = 1f;
                }

                float y = screenH - yScale * (17f + ((i % 5 >= i) ? 10f : 0));
                float w = singleCharW * yScale;
                float h = 6f * yScale;
                float x = yScale * (204f + (i % 5) * (12f) + 2f);
                buffer.pos(x, y + h, 0).tex(minU, maxV).endVertex();
                buffer.pos(x + w, y + h, 0).tex(maxU, maxV).endVertex();
                buffer.pos(x + w, y, 0).tex(maxU, minV).endVertex();
                buffer.pos(x, y, 0).tex(minU, minV).endVertex();
            }
        }

        if(!isOffHandEmpty(player)) { // something in off hand inventory
            float minU = 0f;
            float maxU = 4f/49f;
            float minV = 0f;
            float maxV = 6f/(13f);

            float y = screenH-yScale*(17f);
            float w = 4f*yScale;
            float h = 6f*yScale;
            float x = yScale*(204f+12f*4f +2f);

            buffer.pos(x,y+h,0).tex(minU,maxV).endVertex();
            buffer.pos(x+w,y+h,0).tex(maxU,maxV).endVertex();
            buffer.pos(x+w,y,0).tex(maxU,minV).endVertex();
            buffer.pos(x,y,0).tex(minU,minV).endVertex();
        }

        tessellator.draw();
    }

    private void renderPlayerHead(float screenW, float screenH, float yScale) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        float posX = screenW/2f;
        float posY = screenH/2f;
        float scale = 10f;
        float mouseX = 0f;
        float mouseY = 0f;
        EntityPlayer ent = Minecraft.getMinecraft().thePlayer;
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.limbSwing = 0;
        ent.limbSwingAmount = 0;
        ent.renderYawOffset = 0;
        ent.rotationYaw = 0;
        ent.rotationPitch = 0;
        ent.rotationYawHead = 0;
        ent.prevRotationYawHead = ent.rotationYaw;
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
        float minU = (bigFontCharX[index])/(178f);
        float maxU = 1f;
        if(index+1 < allowedChars.length()) {
            maxU = (bigFontCharX[index+1]-1)/(178f);
        }
        float minV = 0f;
        float maxV = 1f;

        buffer.pos(x,y+h,0).tex(minU,maxV).endVertex();
        buffer.pos(x+w,y+h,0).tex(maxU,maxV).endVertex();
        buffer.pos(x+w,y,0).tex(maxU,minV).endVertex();
        buffer.pos(x,y,0).tex(minU,minV).endVertex();
    }

    public boolean isOffHandEmpty(EntityPlayer player) {
        for (ItemStack s : player.inventory.offHandInventory) {
            if(s != null)
                return false;
        }
        return true;
    }
}
