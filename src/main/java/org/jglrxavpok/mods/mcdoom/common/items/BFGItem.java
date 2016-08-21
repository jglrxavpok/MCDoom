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

import javax.annotation.Nullable;

public class BFGItem extends Item {

    public BFGItem() {
        setMaxStackSize(1);
        setMaxDamage(100);
        setCreativeTab(CreativeTabs.COMBAT);
        setUnlocalizedName("bfg9000");
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
        if(!world.isRemote) {
            PlasmaBallEntity entity = new PlasmaBallEntity(world, shooter);
            world.spawnEntityInWorld(entity);
        }
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        fire(worldIn, entityLiving);
        entityLiving.setHeldItem(entityLiving.getActiveHand(), stack);
        entityLiving.resetActiveHand();
        stack.setItemDamage(getMaxDamage());
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 25;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

}
