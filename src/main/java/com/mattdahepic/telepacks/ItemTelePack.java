package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.helpers.PlayerHelper;
import com.mattdahepic.mdecore.helpers.TeleportHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTelePack extends Item {
    public static final String NAME = "telepack";
    public static final String COLOR_KEY = "packColor";
    public ItemTelePack () {
        super();
        this.setUnlocalizedName(NAME);
        this.setCreativeTab(CreativeTabs.SEARCH);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 7;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return canCrossDimensions(stack);
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,stack);
    }
    @Override
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            if (entity.isSneaking() && timeLeft != 0) {
                setStoredLocation((EntityPlayer)entity,stack,new TelePackLocation(entity.posX,entity.posY, entity.posZ,entity.dimension));
            } else {
                entity.playSound(new SoundEvent(new ResourceLocation("minecraft","random.fizz")),1f,1f);
                ((EntityPlayer)entity).getCooldownTracker().setCooldown(this,3);
            }
        }
    }
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (stack.hasTagCompound() && !world.isRemote && entity instanceof EntityPlayer) {
            if (!entity.isSneaking()) {
                if (hasValidLocation(stack)) {
                    TelePackLocation loc = getStoredLocation(stack);
                    if (canCrossDimensions(stack) && loc.dimension != ((EntityPlayer)entity).dimension) {
                        PlayerList list = entity.getServer().getPlayerList();
                        TeleportHelper.transferPlayerToDimension(PlayerHelper.getPlayerFromUsername(((EntityPlayer)entity).getDisplayNameString()), loc.dimension, list);
                    }
                    if (loc.dimension == entity.dimension) {
                        entity.setPositionAndUpdate(loc.x+.5f, loc.y+.25f, loc.z+.5f);
                        ((EntityPlayer)entity).getCooldownTracker().setCooldown(this, canCrossDimensions(stack) ? 240 : 90);
                        entity.playSound(new SoundEvent(new ResourceLocation("minecraft:mob.enderman.portal")), 1f, 1f);
                    } else {
                        ((EntityPlayer)entity).addChatComponentMessage(new TextComponentString(TextFormatting.YELLOW + "The technology in this pack is too primitive to cross the dimensional gap."));
                    }
                } else {
                    ((EntityPlayer)entity).addChatComponentMessage(new TextComponentString("No location set!"));
                }
            }
        }
        return stack;
    }
    @Override
    public String getUnlocalizedName (ItemStack stack) {
        switch (stack.getMetadata()) {
            case 0:
                return super.getUnlocalizedName(stack)+"_basic";
            case 1:
                return super.getUnlocalizedName(stack)+"_advanced";
        }
        return null;
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        if (stack.hasTagCompound()) {
            if (hasValidLocation(stack)) {
                TelePackLocation loc = getStoredLocation(stack);
                tooltip.add("Location set at ("+loc.x+","+loc.y+","+loc.z+")");
                tooltip.add("In the dimension "+loc.dimension+".");
                return;
            }
        }
        tooltip.add("No location set.");
    }
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item,1,0));
        list.add(new ItemStack(item,1,1));
    }

    /* HELPER METHODS */

    private static boolean hasValidLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags.hasKey("locX") && tags.hasKey("locY") && tags.hasKey("locZ") && tags.hasKey("dimension")) return true;
        }
        return false;
    }
    private static TelePackLocation getStoredLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            int locX = tags.getInteger("locX");
            int locY = tags.getInteger("locY");
            int locZ = tags.getInteger("locZ");
            int dimension = tags.getInteger("dimension");
            return new TelePackLocation(locX,locY,locZ,dimension);
        }
        return null;
    }
    private static void setStoredLocation (EntityPlayer player, ItemStack stack, TelePackLocation loc) {
        NBTTagCompound tags = stack.getTagCompound();
        if (stack.hasTagCompound()) {
            tags.setInteger("locX", loc.x);
            tags.setInteger("locY", loc.y);
            tags.setInteger("locZ", loc.z);
            tags.setInteger("dimension",loc.dimension);
            player.addChatComponentMessage(new TextComponentString("Location stored."));
        }
    }
    private static boolean canCrossDimensions (ItemStack stack) {
        return stack.getMetadata() == 1;
    }

    private static class TelePackLocation {
        final int x;
        final int y;
        final int z;
        final int dimension;

        TelePackLocation (int x, int y, int z, int dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
        TelePackLocation (double x, double y, double z, int dimension) {
            this((int) x, (int) y, (int) z, dimension);
        }
    }
}
