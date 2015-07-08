package com.mattdahepic.telepacks.item;

import com.mattdahepic.mdecore.helpers.PlayerHelper;
import com.mattdahepic.mdecore.helpers.TeleportHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTelePack extends Item {
    public static final String NAME = "telepack";
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
        return 32;
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack,this.getMaxItemUseDuration(stack));
        return stack;
    }
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        //TODO: sound on use cancel
        //TODO: debuff based on time left?
        if (player.isSneaking()) {
            int[] playerPos = PlayerHelper.getPlayerPosAsIntegerArray(player);
            int dimension = player.dimension;
            setStoredLocation(player, stack, playerPos[0], playerPos[1], playerPos[2], dimension);
            return;
        }
    }
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
        if (stack.hasTagCompound()) {
            if (player.isSneaking()) {
                return stack;
            } else if (hasValidLocation(stack)) {
                int[] storedPos = getStoredLocation(stack);
                if (storedPos[3] == player.dimension) {
                    player.setPositionAndUpdate(storedPos[0] + 0.5, storedPos[1] + 0.025, storedPos[2] + 0.5);
                } else {
                    if (canCrossDimensions(stack)) {
                        player.setPositionAndUpdate(storedPos[0] + 0.5, storedPos[1] + 0.025, storedPos[2] + 0.5);
                        ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
                        TeleportHelper.transferPlayerToDimension(manager.getPlayerByUsername(player.getName()),storedPos[3],manager);
                        return stack;
                    } else {
                        player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.YELLOW+"The technology in this pack is too primitive to cross the dimensional gap."));
                        return stack;
                    }
                }
                //TODO: particles
                //TODO: sound on teleport: enderman
                return stack;
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
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (hasValidLocation(stack)) {
                tooltip.add("Location set at ("+tags.getInteger("locX")+","+tags.getInteger("locY")+","+tags.getInteger("locZ")+")");
                tooltip.add("In the dimension "+tags.getInteger("dimension")+".");
                return;
            }
        }
        tooltip.add("No location set.");
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item,1,0));
        list.add(new ItemStack(item,1,1));
    }
    public boolean hasValidLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            if (tags.hasKey("locX") && tags.hasKey("locY") && tags.hasKey("locZ") && tags.hasKey("dimension")) return true;
        }
        return false;
    }
    public int[] getStoredLocation (ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tags = stack.getTagCompound();
            int locX = tags.getInteger("locX");
            int locY = tags.getInteger("locY");
            int locZ = tags.getInteger("locZ");
            int dimension = tags.getInteger("dimension");
            return new int[]{locX,locY,locZ,dimension};
        }
        return null;
    }
    public void setStoredLocation (EntityPlayer player, ItemStack stack, int x, int y, int z, int dimension) {
        NBTTagCompound tags = stack.getTagCompound();
        if (stack.hasTagCompound()) {
            tags.setInteger("locX", x);
            tags.setInteger("locY", y);
            tags.setInteger("locZ", z);
            tags.setInteger("dimension",dimension);
            player.addChatComponentMessage(new ChatComponentText("Location stored."));
        }
    }
    public boolean canCrossDimensions (ItemStack stack) {
        return stack.getMetadata() == 1;
    }
}
