package com.mattdahepic.telepacks;

import com.mattdahepic.telepacks.item.ItemTelePack;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void registerRenderers () {}
    public void registerItems () {
        GameRegistry.registerItem(TelePacks.telepack, ItemTelePack.NAME);
    }
    public void registerRecipes () {
        GameRegistry.addShapelessRecipe(new ItemStack(TelePacks.telepack),TelePacks.telepack); //remove location
        GameRegistry.addShapedRecipe(new ItemStack(TelePacks.telepack, 1, 0), "eee", "ewe", "eee", 'e', Items.ender_pearl, 'w', Blocks.wool); //regular telepack
        GameRegistry.addShapedRecipe(new ItemStack(TelePacks.telepack,1,1),"ene","ete","eee",'e',Items.ender_eye,'n',Items.nether_star,'t',new ItemStack(TelePacks.telepack,0)); //advanced telepack
    }
}
