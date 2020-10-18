package com.mattdahepic.telepacks;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;

public class ClientProxy {
    public final IItemColor telepackColor = (stack, tintIndex) -> {
        if (tintIndex == 1) {
            return DyeColor.byTranslationKey(stack.getOrCreateTag().getString(ItemTelePack.COLOR_KEY),DyeColor.WHITE).getMapColor().colorValue;
        }
        return -1;
    };
    public ClientProxy () {
        //item color handler
        Minecraft.getInstance().getItemColors().register(telepackColor,TelePacks.telepack);
        Minecraft.getInstance().getItemColors().register(telepackColor,TelePacks.advanced_telepack);
    }
}
