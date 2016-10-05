package org.jglrxavpok.mods.mcdoom.client.eventhandlers;

import fr.minecraftforgefrance.sfd.common.item.WeaponItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;

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
                boolean rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
                EntityPlayer player = ((EntityPlayer) evt.getEntityLiving());
                if(Minecraft.getMinecraft().gameSettings.keyBindUseItem.isPressed()) {
                    player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, MCDoom.instance.chainsawUp, SoundCategory.PLAYERS, 1, 1f);
                    soundCooldown = 6;
                }
                ItemStack stack = player.inventory.getCurrentItem();

                if(soundCooldown > 0) {
                    soundCooldown--;
                }

                if (stack != null && stack.getItem() != null) {
                    if(stack.getItem() instanceof WeaponItem) {
                        if(((WeaponItem) stack.getItem()).getDefinition().getId().equals("chainsaw")) {
                            if(soundCooldown == 0) {
                                float pitch = (float) (Math.random()*0.1f+0.9f);
                                if(rightClick) {
                                    player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, MCDoom.instance.chainsawFull, SoundCategory.PLAYERS, 1, pitch);
                                    soundCooldown = 8;
                                } else {
                                    player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, MCDoom.instance.chainsawIdle, SoundCategory.PLAYERS, 1, pitch);
                                    soundCooldown = 4;
                                }
                            }
                        }
                    }
                    lastItem = stack.getItem();
                }
            }
        }
    }

}
