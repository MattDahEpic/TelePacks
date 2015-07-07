package com.mattdahepic.telepacks;

import com.mattdahepic.telepacks.item.ItemTelePack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void registerRenderers () {}
    public void registerItems () {
        GameRegistry.registerItem(TelePacks.telepack, ItemTelePack.NAME);
    }
    public void registerRecipes () {

    }
}
