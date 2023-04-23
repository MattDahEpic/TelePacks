package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.common.registries.ConfigRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("telepacks")
public class TelePacks {
    public static final String MODID = "telepacks";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> TELEPACK_ITEM = ITEMS.register("telepack", ItemTelePack::new);
    public static final RegistryObject<Item> ADVANCED_TELEPACK_ITEM = ITEMS.register("advanced_telepack", ItemTelePack::new);

    public TelePacks() {
        //config
        ConfigRegistry.registerConfig(null, TelePacksConfig.COMMON_SPEC);

        //mod bus events
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(ClientProxy::registerItemColors);
        }
    }
}
