package org.jglrxavpok.mods.mcdoom.client.render;

import com.udojava.evalex.Expression;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jglrxavpok.mods.mcdoom.common.weapons.EnumWeaponStates;

import java.math.BigDecimal;

public abstract class WeaponPredicate {

    public static final WeaponPredicate ALWAYS_TRUE = new WeaponPredicate() {
        @Override
        public boolean apply(ItemStack currentItem, EntityPlayer player, int frame) {
            return true;
        }
    };

    public abstract boolean apply(ItemStack currentItem, EntityPlayer player, int frame);

    public static WeaponPredicate createFromString(String condition) {
        if(condition.equalsIgnoreCase("always"))
            return ALWAYS_TRUE;
        final Expression expression = new Expression(condition);
        for (EnumWeaponStates state : EnumWeaponStates.values()) {
            expression.setVariable(state.name().toLowerCase(), BigDecimal.valueOf(state.ordinal()));
        }
        // MAYBE TODO: Cache BigDecimal values
        return new WeaponPredicate() {
            @Override
            public boolean apply(ItemStack currentItem, EntityPlayer player, int frame) {
                boolean rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
                EnumWeaponStates state = EnumWeaponStates.IDLE;
                int preFiringTick = 0;
                float preFiringPercent = 0f;
                if(currentItem.getItemDamage() != 0) {
                    state = EnumWeaponStates.COOLING_DOWN;
                } else if(currentItem == player.getActiveItemStack()) {
                    state = EnumWeaponStates.PREPARING_FIRE;

                    int count = player.getItemInUseCount();
                    preFiringTick = player.getItemInUseMaxCount()-count;
                    preFiringPercent = (float)preFiringTick/(float)player.getItemInUseMaxCount();
                    if(Float.isInfinite(preFiringPercent) || Float.isNaN(preFiringPercent))
                        preFiringPercent = 0f;
                }
                expression.setVariable("time", BigDecimal.valueOf(player.getEntityWorld().getTotalWorldTime()));
                expression.setVariable("cooldown", BigDecimal.valueOf(currentItem.getItemDamage()));
                expression.setVariable("state", BigDecimal.valueOf(state.ordinal()));
                expression.setVariable("triggerDelayTick", BigDecimal.valueOf(preFiringTick));
                expression.setVariable("triggerDelay", BigDecimal.valueOf(preFiringPercent));
                expression.setVariable("rightClick", rightClick ? BigDecimal.ONE : BigDecimal.ZERO);
                expression.setVariable("frame", BigDecimal.valueOf(frame));
                return !expression.eval().equals(BigDecimal.ZERO);
            }
        };
    }

}
