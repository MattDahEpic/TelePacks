package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.helpers.ItemHelper;
import com.mattdahepic.mdecore.recipe.NBTRespectingShapedOreRecipe;
import com.mattdahepic.mdecore.recipe.NBTRespectingShapelessOreRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class CommonProxy {
    public void registerRenderers () {}
    public void registerColorHandler () {}
    public void registerItems () {
        GameRegistry.register(TelePacks.telepack.setRegistryName(ItemTelePack.NAME));
    }
    public void registerRecipes () {
        RecipeSorter.register("telepacks:colormaintainshapeless",ColorMaintainPackShapelessRecipe.class, RecipeSorter.Category.SHAPELESS,"after:mdecore:nbtshapelessore");

        for (EnumDyeColor c : EnumDyeColor.values()) {
            ItemStack basic = new ItemStack(TelePacks.telepack,1,0);
            ItemStack advanced = new ItemStack(TelePacks.telepack,1,1);

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(ItemTelePack.COLOR_KEY,c.name());
            basic.setTagCompound(tag);
            advanced.setTagCompound(tag);
            //basic
            GameRegistry.addShapedRecipe(basic,"eee","ewe","eee",'e',Items.ENDER_PEARL,'w',new ItemStack(Blocks.WOOL,1,c.getMetadata()));
            //advanced

            GameRegistry.addRecipe(new NBTRespectingShapedOreRecipe(advanced,"ene","ete","eee",'e',Items.ENDER_EYE,'t',basic,'n',Items.NETHER_STAR));
            //redying
            GameRegistry.addShapelessRecipe(basic,new ItemStack(TelePacks.telepack,1,0),new ItemStack(Items.DYE,1,c.getDyeDamage()));
            GameRegistry.addShapelessRecipe(advanced,new ItemStack(TelePacks.telepack,1,1),new ItemStack(Items.DYE,1,c.getDyeDamage()));
            //remove location
            GameRegistry.addRecipe(new ColorMaintainPackShapelessRecipe(basic,basic));
            GameRegistry.addRecipe(new ColorMaintainPackShapelessRecipe(advanced,advanced));
        }
    }
    public class ColorMaintainPackShapelessRecipe extends NBTRespectingShapelessOreRecipe {
        public ColorMaintainPackShapelessRecipe (ItemStack out, Object... recipe) {
            super(out,recipe);
        }

        @Override
        public boolean isItemSame (ItemStack item1, ItemStack item2) {
            return ItemHelper.isSameIgnoreStackSize(item1,item2,false) && item1.getTagCompound().getString(ItemTelePack.COLOR_KEY).equals(item2.getTagCompound().getString(ItemTelePack.COLOR_KEY));
        }
    }
}
