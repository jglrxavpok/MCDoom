package org.jglrxavpok.mods.mcdoom.client.render;

import com.udojava.evalex.Expression;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jglrxavpok.mods.mcdoom.common.weapons.EnumWeaponStates;

import java.math.BigDecimal;

public abstract class WeaponPredicate {

    public static final WeaponPredicate ALWAYS_TRUE = new WeaponPredicate() {
        @Override
        public boolean apply(ItemStack currentItem, EntityPlayer player) {
            return true;
        }
    };

    public abstract boolean apply(ItemStack currentItem, EntityPlayer player);

    public static WeaponPredicate createFromString(String condition) {
        final Expression expression = new Expression(condition);
        for (EnumWeaponStates state : EnumWeaponStates.values()) {
            expression.setVariable(state.name().toLowerCase(), new BigDecimal(state.ordinal()));
        }
        return new WeaponPredicate() {
            @Override
            public boolean apply(ItemStack currentItem, EntityPlayer player) {
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
                expression.setVariable("time", new BigDecimal(player.getEntityWorld().getTotalWorldTime()));
                expression.setVariable("cooldown", new BigDecimal(currentItem.getItemDamage()));
                expression.setVariable("state", new BigDecimal(state.ordinal()));
                expression.setVariable("triggerDelayTick", new BigDecimal(preFiringTick));
                expression.setVariable("triggerDelay", new BigDecimal(preFiringPercent));
                return !expression.eval().equals(BigDecimal.ZERO);
            }
        };
    }

}
