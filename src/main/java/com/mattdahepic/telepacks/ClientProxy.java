package com.mattdahepic.telepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers () {
        ModelBakery.registerItemVariants(TelePacks.telepack, new ModelResourceLocation("telepacks:telepack", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TelePacks.telepack, 0, new ModelResourceLocation("telepacks:telepack", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TelePacks.telepack, 1, new ModelResourceLocation("telepacks:telepack", "inventory"));
    }
    @Override
    public void registerColorHandler () {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                try {
                    return EnumDyeColor.valueOf(stack.getTagCompound().getString(ItemTelePack.COLOR_KEY)).getMapColor().colorValue;
                } catch (Exception ex) {
                    return EnumDyeColor.GREEN.getMapColor().colorValue;
                }
            }
        }, TelePacks.telepack);
    }
}
