package com.mattdahepic.telepacks;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.ITeleporter;

import java.util.List;
import java.util.function.Function;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class ItemTelePack extends Item {
    public static final String COLOR_KEY = "color";
    public ItemTelePack () {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return canCrossDimensions(stack);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS,player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        if (!world.isClientSide && entity instanceof Player) {
            if (entity.isShiftKeyDown() && timeLeft != 0) {
                setStoredLocation((Player)entity,stack,new TelePackLocation(entity.getX(),entity.getY(), entity.getZ(),entity.level.dimension.location));
            } else {
                ((ServerPlayer)entity).connection.send(new ClientboundCustomSoundPacket(new ResourceLocation("telepacks:items.telepack.fizz"),SoundSource.MASTER, new Vec3(entity.getX(),entity.getY(),entity.getZ()),1f,1f));
                ((Player)entity).getCooldowns().addCooldown(this,3);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (stack.hasTag() && !world.isClientSide && entity instanceof ServerPlayer) {
            if (!entity.isShiftKeyDown()) {
                if (hasValidLocation(stack)) {
                    TelePackLocation loc = getStoredLocation(stack);
                    if (!loc.dimension.equals(entity.level.dimension.location)) { //need to move dimensions
                        if (canCrossDimensions(stack)) {
                            PlayerList playerList = entity.getServer().getPlayerList();
                            ServerLevel newWorld = entity.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, loc.dimension));
                            try {
                                entity.changeDimension(newWorld, new ITeleporter() {
                                    @Override
                                    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                        return repositionEntity.apply(false); //dont spawn a nether portal
                                    }
                                });
                            } catch (NullPointerException ignored) {} //ignore because portalinfo is null and cant set the location/position, but we'll do that
                            entity.teleportTo(loc.x + .5f, loc.y + .25f, loc.z + .5f);
                            //the following from ServerPlayerEntity.changeDimension since the null pointer doesn't allow us to run this
                            //TODO remove if ServerPlayerEntity.setDimension doesn't error out (remove trycatch above to test)
                            ((ServerPlayer)entity).gameMode.setLevel(newWorld);
                            ((ServerPlayer)entity).connection.send(new ClientboundPlayerAbilitiesPacket(((ServerPlayer)entity).getAbilities()));
                            playerList.sendLevelInfo(((ServerPlayer)entity), newWorld);
                            playerList.sendAllPlayerInfo(((ServerPlayer)entity));
                            for(MobEffectInstance effectinstance : ((ServerPlayer)entity).getActiveEffects()) {
                                ((ServerPlayer)entity).connection.send(new ClientboundUpdateMobEffectPacket(((ServerPlayer)entity).getId(), effectinstance));
                            }
                            //end patch code
                        } else {
                            ((Player) entity).displayClientMessage(new TranslatableComponent("telepacks.no_cross_dimension"),true);
                        }
                    } else { //same dimension, just move
                        entity.teleportTo(loc.x+.5f, loc.y+.25f, loc.z+.5f);
                    }
                    ((Player)entity).getCooldowns().addCooldown(this, canCrossDimensions(stack) ? TelePacksConfig.COMMON.advancedRechargeTicks.get() : TelePacksConfig.COMMON.basicRechargeTicks.get());
                    ((ServerPlayer)entity).connection.send(new ClientboundCustomSoundPacket(new ResourceLocation("telepacks:items.telepack.teleport"),SoundSource.MASTER, new Vec3(entity.getX(),entity.getY(),entity.getZ()),1f,1f));
                } else {
                    ((Player) entity).displayClientMessage(new TranslatableComponent("telepacks.no_location"),true);
                }
            }
        }
        return stack;
    }

    @Override
    public Component getName (ItemStack stack) {
        return new TranslatableComponent(canCrossDimensions(stack) ? "items.telepacks.telepack_advanced" : "items.telepacks.telepack_basic");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag()) {
            if (hasValidLocation(stack)) {
                TelePackLocation loc = getStoredLocation(stack);
                tooltip.add(new TranslatableComponent("telepacks.tooltip.location",loc.x,loc.y,loc.z));
                tooltip.add(new TranslatableComponent("telepacks.tooltip.dimension",loc.dimension));
                return;
            }
        }
        tooltip.add(new TranslatableComponent("telepacks.no_location"));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }

    /* HELPER METHODS */

    private static boolean hasValidLocation (ItemStack stack) {
        CompoundTag tags = stack.getOrCreateTag();
        if (tags.contains("locX") && tags.contains("locY") && tags.contains("locZ") && tags.contains("dimension")) return true;
        return false;
    }
    private static TelePackLocation getStoredLocation (ItemStack stack) {
        CompoundTag tags = stack.getOrCreateTag();
        int locX = tags.getInt("locX");
        int locY = tags.getInt("locY");
        int locZ = tags.getInt("locZ");
        ResourceLocation dimension = new ResourceLocation(tags.getString("dimension"));
        return new TelePackLocation(locX,locY,locZ,dimension);
    }
    private static void setStoredLocation (Player player, ItemStack stack, TelePackLocation loc) {
        CompoundTag tags = stack.getOrCreateTag();
        tags.putInt("locX", loc.x);
        tags.putInt("locY", loc.y);
        tags.putInt("locZ", loc.z);
        tags.putString("dimension",loc.dimension.toString());
        player.displayClientMessage(new TranslatableComponent("telepacks.location_stored"),true);
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
