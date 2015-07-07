package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.update.UpdateChecker;
import com.mattdahepic.telepacks.item.ItemTelePack;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = TelePacks.MODID,name = TelePacks.NAME,version = TelePacks.VERSION,dependencies = TelePacks.DEPENDENCIES)
public class TelePacks {
    @Mod.Instance(TelePacks.MODID)
    public static TelePacks instance;

    public static final String MODID = "telepacks";
    public static final String NAME = "TelePacks";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:mdecore@[1.8-1.2.1,);";
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/MattDahEpic/TelePacks1.8/master/version.txt";

    @SidedProxy(clientSide = "com.mattdahepic.telepacks.client.ClientProxy",serverSide = "com.mattdahepic.telepacks.CommonProxy")
    public static CommonProxy proxy;

    //item
    public static Item telepack;

    @Mod.EventHandler
    public void load (FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(instance);
    }
    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        telepack = new ItemTelePack();
        proxy.registerItems();
        proxy.registerRecipes();
        proxy.registerRenderers();
    }
    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {}
    @Mod.EventHandler
    public void loadComplete (FMLLoadCompleteEvent event) {
        UpdateChecker.updateCheck(MODID,NAME,UPDATE_URL,VERSION,false,null);
    }
    @SubscribeEvent
    public void onPlayerJoinServer (PlayerEvent.PlayerLoggedInEvent event) {
        UpdateChecker.updateCheck(MODID,NAME,UPDATE_URL,VERSION,true,event.player);
    }
}
