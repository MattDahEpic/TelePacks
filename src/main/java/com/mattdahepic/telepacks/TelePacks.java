package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.common.registries.ConfigRegistry;
import com.mattdahepic.mdecore.common.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("telepacks")
public class TelePacks extends ItemRegistry {
    public static final String MODID = "telepacks";

    public static Item telepack;
    public static Item advanced_telepack;

    public TelePacks() {
        //config
        ConfigRegistry.registerConfig(null, TelePacksConfig.COMMON_SPEC);

        //mod bus events
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(this);
    }

    @SubscribeEvent
    public void clientSetup (FMLClientSetupEvent event) {
        DistExecutor.safeCallWhenOn(Dist.CLIENT,() -> ClientProxy::new);
    }
    @SubscribeEvent
    public void register(RegistryEvent.Register<Item> register) {
        telepack = registerItem(new ItemTelePack(),"telepack");
        advanced_telepack = registerItem(new ItemTelePack(),"advanced_telepack");
    }
}
