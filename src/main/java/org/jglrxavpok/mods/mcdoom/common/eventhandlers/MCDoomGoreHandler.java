package org.jglrxavpok.mods.mcdoom.common.eventhandlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jglrxavpok.mods.mcdoom.common.MCDoom;
import org.jglrxavpok.mods.mcdoom.common.network.MessageSpawnGoreParticles;

public class MCDoomGoreHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {
        EntityLivingBase ent = evt.getEntityLiving();
        World world = ent.getEntityWorld();
        if(!world.isRemote) {
            int count = ent.getRNG().nextInt(2) + (int)(evt.getAmount()*1.5f);
            float x = (float) ent.posX;
            float y = (float) ent.posY+ ent.height/2f;
            float z = (float) ent.posZ;
            MCDoom.network.sendToAll(new MessageSpawnGoreParticles(count, x, y, z));
        }
    }
}
