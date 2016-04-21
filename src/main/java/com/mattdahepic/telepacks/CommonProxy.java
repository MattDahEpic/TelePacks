package com.mattdahepic.telepacks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void registerRenderers () {}
    public void registerColorHandler () {}
    public void registerItems () {
        GameRegistry.register(TelePacks.telepack.setRegistryName(ItemTelePack.NAME));
    }
    public void registerRecipes () {
        for (EnumDyeColor c : EnumDyeColor.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(ItemTelePack.COLOR_KEY,c.getName());
            //basic
            GameRegistry.addShapedRecipe(new ItemStack(TelePacks.telepack,1,0,tag),"eee","ewe","eee",'e',Items.ender_pearl,'w',new ItemStack(Blocks.wool,1,c.getMetadata()));
            //advanced
            GameRegistry.addShapedRecipe(new ItemStack(TelePacks.telepack,1,1,tag),"ene","ete","ede",'e',Items.ender_eye,'t',new ItemStack(TelePacks.telepack,1,0),'d',new ItemStack(Items.dye,1,c.getDyeDamage()));
            //redying
            GameRegistry.addShapelessRecipe(new ItemStack(TelePacks.telepack,1,0,tag),new ItemStack(TelePacks.telepack,1,0),new ItemStack(Items.dye,1,c.getDyeDamage()));
            GameRegistry.addShapelessRecipe(new ItemStack(TelePacks.telepack,1,1,tag),new ItemStack(TelePacks.telepack,1,1),new ItemStack(Items.dye,1,c.getDyeDamage()));
        }
        //remove location
        GameRegistry.addShapelessRecipe(new ItemStack(TelePacks.telepack,1,0),new ItemStack(TelePacks.telepack,1,0));
        GameRegistry.addShapelessRecipe(new ItemStack(TelePacks.telepack,1,1),new ItemStack(TelePacks.telepack,1,1));
    }
}
