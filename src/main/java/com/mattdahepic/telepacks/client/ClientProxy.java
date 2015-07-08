package com.mattdahepic.telepacks.client;

import com.mattdahepic.telepacks.CommonProxy;
import com.mattdahepic.telepacks.TelePacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers () {
        ModelBakery.addVariantName(TelePacks.telepack, "telepacks:telepack_basic", "telepacks:telepack_advanced");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TelePacks.telepack,0,new ModelResourceLocation("telepacks:telepack_basic","inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TelePacks.telepack,1,new ModelResourceLocation("telepacks:telepack_advanced","inventory"));
    }
}
