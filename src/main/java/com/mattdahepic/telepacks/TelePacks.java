package com.mattdahepic.telepacks;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TelePacks.MODID,name = TelePacks.NAME,version = TelePacks.VERSION,dependencies = TelePacks.DEPENDENCIES,updateJSON = TelePacks.UPDATE_JSON)
public class TelePacks {
    static final String MODID = "telepacks";
    static final String NAME = "TelePacks";
    static final String VERSION = "@VERSION@";
    static final String DEPENDENCIES = "required-after:mdecore@[1.9-1.0,);";
    static final String UPDATE_JSON = "https://raw.githubusercontent.com/MattDahEpic/Version/master/"+MODID+".json";

    @SidedProxy(clientSide = "com.mattdahepic.telepacks.ClientProxy",serverSide = "com.mattdahepic.telepacks.CommonProxy")
    public static CommonProxy proxy;

    static final Logger logger = LogManager.getLogger(MODID);

    static ItemTelePack telepack = new ItemTelePack();

    @Mod.EventHandler
    public void load (FMLPreInitializationEvent e) {
        proxy.registerItems();
        proxy.registerRenderers();
    }
    @Mod.EventHandler
    public void init (FMLInitializationEvent e) {
        proxy.registerRecipes();
        proxy.registerColorHandler();
    }
}
