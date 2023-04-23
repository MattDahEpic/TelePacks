package com.mattdahepic.telepacks;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ClientProxy {
    public static final ItemColor telepackColor = (stack, tintIndex) -> {
        if (tintIndex == 1) {
            return DyeColor.byName(stack.getOrCreateTag().getString(ItemTelePack.COLOR_KEY),DyeColor.WHITE).getMaterialColor().col;
        }
        return -1;
    };
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(telepackColor, TelePacks.TELEPACK_ITEM.get());
        event.register(telepackColor, TelePacks.ADVANCED_TELEPACK_ITEM.get());
    }
}
