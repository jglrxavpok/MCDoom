package org.jglrxavpok.mods.mcdoom.client.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.items.WeaponItem;

@SideOnly(Side.CLIENT)
public class MCDoomSoundEvents {

    private Item lastItem;
    private int changeSoundCooldown;
    private int soundCooldown;

    // TODO: cleanup
    @SubscribeEvent
    public void onClientTick(LivingEvent.LivingUpdateEvent evt) {
        if(evt.getEntityLiving() == Minecraft.getMinecraft().getRenderViewEntity()) {
            if(evt.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = ((EntityPlayer) evt.getEntityLiving());
                ItemStack stack = player.inventory.getCurrentItem();

                if(soundCooldown > 0) {
                    soundCooldown--;
                }

                if (stack != null && stack.getItem() != null) {
                    if(stack.getItem() instanceof WeaponItem) {
                        if(((WeaponItem) stack.getItem()).getDefinition().getId().equals("chainsaw")) {
                            if(soundCooldown == 0) {
                                evt.getEntityLiving().getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, MCDoom.instance.chainsawIdle, SoundCategory.PLAYERS, 1, 1);
                                soundCooldown = 5;
                            }
                        }
                    }
                    lastItem = stack.getItem();
                }
            }
        }
    }

}
