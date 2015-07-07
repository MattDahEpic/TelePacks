package com.mattdahepic.telepacks.item;

import com.mattdahepic.mdecore.helpers.PlayerHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTelePack extends Item {
    public static final String NAME = "telepack";
    public static int cooldown_length = 100;
    public ItemTelePack () {
        super();
        this.setUnlocalizedName(NAME);
        this.setCreativeTab(CreativeTabs.tabAllSearch);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 40;
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        //onItemRightClick
        //onItemUseFinish
        if (stack.hasTagCompound()) {
            if (hasValidLocation(stack)) {
                int cooldown = stack.getTagCompound().getInteger("cooldown");
                if (cooldown > 0) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+"The pack is still warm to the touch. It cannot be used yet."));
                    return stack;
                } else {
                    int[] storedPos = getStoredLocation(stack);
                    player.setPositionAndUpdate(storedPos[0]+0.5,storedPos[1]+0.5,storedPos[2]+0.5);
                    stack.getTagCompound().setInteger("cooldown",cooldown_length); //reset cooldown
                    //TODO: dimensions too
                    //TODO: not instant teleport on click/hold down mouse
                    //TODO: particles
                    //TODO: sound, enderman tp
                    return stack;
                }
            }
        }
        if (player.isSneaking()) {
            int[] playerPos = PlayerHelper.getPlayerPosAsIntegerArray(player);
            setStoredLocation(player, stack, playerPos[0], playerPos[1], playerPos[2]);
        }
        return stack;
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("cooldown",0);
        }
        int cooldown = stack.getTagCompound().getInteger("cooldown");
        if (cooldown > 0) {
            cooldown--;
            stack.getTagCompound().setInteger("cooldown",cooldown);
        } else {
            stack.getTagCompound().setInteger("cooldown",0);
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (hasValidLocation(stack)) {
                tooltip.add("Location set at ("+tags.getInteger("locX")+","+tags.getInteger("locY")+","+tags.getInteger("locZ")+")");
                //TODO: dimension
                return;
            }
        }
        tooltip.add("No location set.");
    }
    public boolean hasValidLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags.hasKey("locX") && tags.hasKey("locY") && tags.hasKey("locZ")) return true;
        }
        return false;
    }
    public int[] getStoredLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            int locX = tags.getInteger("locX");
            int locY = tags.getInteger("locY");
            int locZ = tags.getInteger("locZ");
            return new int[]{locX,locY,locZ};
        }
        return null;
    }
    public void setStoredLocation (EntityPlayer player, ItemStack stack, int x, int y, int z) {
        NBTTagCompound tags = stack.getTagCompound();
        if (stack.hasTagCompound()) {
            tags.setInteger("locX", x);
            tags.setInteger("locY", y);
            tags.setInteger("locZ", z);
            player.addChatComponentMessage(new ChatComponentText("Location stored."));
        }
    }
}
