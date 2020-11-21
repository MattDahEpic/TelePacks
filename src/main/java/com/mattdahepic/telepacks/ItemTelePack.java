package com.mattdahepic.telepacks;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.List;
import java.util.function.Function;

public class ItemTelePack extends Item {
    public static final String COLOR_KEY = "color";
    public ItemTelePack () {
        super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }
    @Override
    public boolean hasEffect(ItemStack stack) {
        return canCrossDimensions(stack);
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS,player.getHeldItem(hand));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (!world.isRemote && entity instanceof PlayerEntity) {
            if (entity.isSneaking() && timeLeft != 0) {
                setStoredLocation((PlayerEntity)entity,stack,new TelePackLocation(entity.getPosX(),entity.getPosY(), entity.getPosZ(),entity.world.dimension.location));
            } else {
                ((ServerPlayerEntity)entity).connection.sendPacket(new SPlaySoundPacket(new ResourceLocation("telepacks:items.telepack.fizz"),SoundCategory.MASTER, new Vector3d(entity.getPosX(),entity.getPosY(),entity.getPosZ()),1f,1f));
                ((PlayerEntity)entity).getCooldownTracker().setCooldown(this,3);
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
        if (stack.hasTag() && !world.isRemote && entity instanceof ServerPlayerEntity) {
            if (!entity.isSneaking()) {
                if (hasValidLocation(stack)) {
                    TelePackLocation loc = getStoredLocation(stack);
                    if (!loc.dimension.equals(entity.world.dimension.location)) { //need to move dimensions
                        if (canCrossDimensions(stack)) {
                            PlayerList playerList = entity.getServer().getPlayerList();
                            ServerWorld newWorld = entity.getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, loc.dimension));
                            try {
                                entity.changeDimension(newWorld, new ITeleporter() {
                                    @Override
                                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                        return repositionEntity.apply(false); //dont spawn a nether portal
                                    }
                                });
                            } catch (NullPointerException ignored) {} //ignore because portalinfo is null and cant set the location/position, but we'll do that
                            entity.setPositionAndUpdate(loc.x + .5f, loc.y + .25f, loc.z + .5f);
                            //the following from ServerPlayerEntity.changeDimension since the null pointer doesn't allow us to run this
                            //TODO remove if ServerPlayerEntity.setDimension doesn't error out (remove trycatch above to test)
                            ((ServerPlayerEntity)entity).interactionManager.setWorld(newWorld);
                            ((ServerPlayerEntity)entity).connection.sendPacket(new SPlayerAbilitiesPacket(((ServerPlayerEntity)entity).abilities));
                            playerList.sendWorldInfo(((ServerPlayerEntity)entity), newWorld);
                            playerList.sendInventory(((ServerPlayerEntity)entity));
                            for(EffectInstance effectinstance : ((ServerPlayerEntity)entity).getActivePotionEffects()) {
                                ((ServerPlayerEntity)entity).connection.sendPacket(new SPlayEntityEffectPacket(((ServerPlayerEntity)entity).getEntityId(), effectinstance));
                            }
                            //end patch code
                        } else {
                            ((PlayerEntity) entity).sendStatusMessage(new TranslationTextComponent("telepacks.no_cross_dimension"),true);
                        }
                    } else { //same dimension, just move
                        entity.setPositionAndUpdate(loc.x+.5f, loc.y+.25f, loc.z+.5f);
                    }
                    ((PlayerEntity)entity).getCooldownTracker().setCooldown(this, canCrossDimensions(stack) ? TelePacksConfig.COMMON.advancedRechargeTicks.get() : TelePacksConfig.COMMON.basicRechargeTicks.get());
                    ((ServerPlayerEntity)entity).connection.sendPacket(new SPlaySoundPacket(new ResourceLocation("telepacks:items.telepack.teleport"),SoundCategory.MASTER, new Vector3d(entity.getPosX(),entity.getPosY(),entity.getPosZ()),1f,1f));
                } else {
                    ((PlayerEntity) entity).sendStatusMessage(new TranslationTextComponent("telepacks.no_location"),true);
                }
            }
        }
        return stack;
    }

    @Override
    public ITextComponent getDisplayName (ItemStack stack) {
        return new TranslationTextComponent(canCrossDimensions(stack) ? "items.telepacks.telepack_advanced" : "items.telepacks.telepack_basic");
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (stack.hasTag()) {
            if (hasValidLocation(stack)) {
                TelePackLocation loc = getStoredLocation(stack);
                tooltip.add(new TranslationTextComponent("telepacks.tooltip.location",loc.x,loc.y,loc.z));
                tooltip.add(new TranslationTextComponent("telepacks.tooltip.dimension",loc.dimension));
                return;
            }
        }
        tooltip.add(new TranslationTextComponent("telepacks.no_location"));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
        }
    }

    /* HELPER METHODS */

    private static boolean hasValidLocation (ItemStack stack) {
        CompoundNBT tags = stack.getOrCreateTag();
        if (tags.contains("locX") && tags.contains("locY") && tags.contains("locZ") && tags.contains("dimension")) return true;
        return false;
    }
    private static TelePackLocation getStoredLocation (ItemStack stack) {
        CompoundNBT tags = stack.getOrCreateTag();
        int locX = tags.getInt("locX");
        int locY = tags.getInt("locY");
        int locZ = tags.getInt("locZ");
        ResourceLocation dimension = new ResourceLocation(tags.getString("dimension"));
        return new TelePackLocation(locX,locY,locZ,dimension);
    }
    private static void setStoredLocation (PlayerEntity player, ItemStack stack, TelePackLocation loc) {
        CompoundNBT tags = stack.getOrCreateTag();
        tags.putInt("locX", loc.x);
        tags.putInt("locY", loc.y);
        tags.putInt("locZ", loc.z);
        tags.putString("dimension",loc.dimension.toString());
        player.sendStatusMessage(new TranslationTextComponent("telepacks.location_stored"),true);
    }
    private static boolean canCrossDimensions (ItemStack stack) {
        return stack.getItem() == TelePacks.advanced_telepack;
    }
    private static ItemStack basicPack () {
        return new ItemStack(TelePacks.telepack);
    }
    private static ItemStack advancedPack () {
        return new ItemStack(TelePacks.advanced_telepack);
    }

    private static class TelePackLocation {
        final int x;
        final int y;
        final int z;
        final ResourceLocation dimension;

        TelePackLocation (int x, int y, int z, ResourceLocation dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
        TelePackLocation (double x, double y, double z, ResourceLocation dimension) {
            this((int) x, (int) y, (int) z, dimension);
        }
    }
}
