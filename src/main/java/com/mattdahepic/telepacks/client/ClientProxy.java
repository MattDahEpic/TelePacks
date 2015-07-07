package com.mattdahepic.telepacks.client;

import com.mattdahepic.telepacks.CommonProxy;
import com.mattdahepic.telepacks.TelePacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void registerRenderers () {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TelePacks.telepack, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                switch (stack.getMetadata()) {
                    case 0:
                        return new ModelResourceLocation("telepacks:telepack","inventory");
                    case 1:
                        return null; //TODO: fix dis
                }
                return null;
            }
        });
    }
}
