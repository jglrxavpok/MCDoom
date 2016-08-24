package org.jglrxavpok.mods.mcdoom.client.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.client.MCDoomClientProxy;
import org.jglrxavpok.mods.mcdoom.client.render.TextureRegion;
import org.jglrxavpok.mods.mcdoom.client.render.WeaponRenderer;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;

import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class MCDoomScreenEvents {

    private final MCDoomClientProxy proxy;

    public MCDoomScreenEvents(MCDoomClientProxy proxy) {
        this.proxy = proxy;
    }

    @SubscribeEvent
    public void onItemDrawing(RenderHandEvent event) {
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e instanceof EntityPlayer) {
            ItemStack currentItem = ((EntityPlayer) e).inventory.getCurrentItem();
            if(currentItem != null && currentItem.getItem() != null) {
                Item actualItem = currentItem.getItem();
                if(actualItem instanceof WeaponItem) {
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
                    if(actualItem instanceof WeaponItem) {
                        WeaponRenderer renderer = proxy.getRenderer(((WeaponItem) actualItem).getDefinition().getId());
                        if(renderer != null)
                            renderer.renderWeapon((EntityPlayer)e, currentItem, evt.getResolution(), evt.getPartialTicks());
                    }
                }
            }
        }
    }

}
