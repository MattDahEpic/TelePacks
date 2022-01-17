package com.mattdahepic.telepacks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;

public class ClientProxy {
    public final ItemColor telepackColor = (stack, tintIndex) -> {
        if (tintIndex == 1) {
            return DyeColor.byName(stack.getOrCreateTag().getString(ItemTelePack.COLOR_KEY),DyeColor.WHITE).getMaterialColor().col;
        }
        return -1;
    };
    public ClientProxy () {
        //item color handler
        Minecraft.getInstance().getItemColors().register(telepackColor,TelePacks.telepack);
        Minecraft.getInstance().getItemColors().register(telepackColor,TelePacks.advanced_telepack);
    }
}
