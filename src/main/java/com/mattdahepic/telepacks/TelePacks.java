package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.update.UpdateChecker;
import com.mattdahepic.telepacks.item.ItemTelePack;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
    public static final String DEPENDENCIES = "required-after:mdecore@[1.8.8-1.5,);";
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/MattDahEpic/Version/master/"+ MinecraftForge.MC_VERSION+"/"+MODID+".txt";

    @SidedProxy(clientSide = "com.mattdahepic.telepacks.client.ClientProxy",serverSide = "com.mattdahepic.telepacks.CommonProxy")
    public static CommonProxy proxy;

    //item
    public static Item telepack;

    @Mod.EventHandler
    public void load (FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(instance);
        telepack = new ItemTelePack();
        proxy.registerItems();
    }
    @Mod.EventHandler
    public void init (FMLInitializationEvent e) {
        proxy.registerRecipes();
        proxy.registerRenderers();
        UpdateChecker.checkRemote(MODID,UPDATE_URL);
    }
    @SubscribeEvent
    public void onPlayerJoinServer (PlayerEvent.PlayerLoggedInEvent e) {
        UpdateChecker.printMessageToPlayer(MODID,e.player);
    }
}
