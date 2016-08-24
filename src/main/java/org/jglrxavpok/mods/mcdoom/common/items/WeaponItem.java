package org.jglrxavpok.mods.mcdoom.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jglrxavpok.mods.mcdoom.common.entity.PlasmaBallEntity;
import org.jglrxavpok.mods.mcdoom.common.weapons.WeaponDefinition;

import javax.annotation.Nullable;

public class WeaponItem extends Item {

    private final WeaponDefinition definition;

    public WeaponItem(WeaponDefinition definition) {
        this.definition = definition;
        setMaxStackSize(1);
        setMaxDamage(definition.getCooldown());
        setCreativeTab(CreativeTabs.COMBAT);
        setUnlocalizedName(definition.getId());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(itemStackIn.getItemDamage() == 0) {
            playerIn.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

    }

    private void fire(World world, EntityLivingBase shooter) {
        // TODO: Change method depending on weapon
        if(!world.isRemote) {
            PlasmaBallEntity entity = new PlasmaBallEntity(world, shooter);
            world.spawnEntityInWorld(entity);
        }
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        fire(worldIn, entityLiving);
        stack.setItemDamage(getMaxDamage());
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return definition.getPreFiringPause();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public WeaponDefinition getDefinition() {
        return definition;
    }
}
