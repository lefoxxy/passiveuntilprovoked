package io.github.lefoxxy.passiveuntilprovoked;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MobEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !isAffectedHostile(mob)) {
            return;
        }

        Player player = getResponsiblePlayer(event.getSource());
        if (player == null) {
            return;
        }

        AggroMemory.allowRetaliation(mob, player);
        if (PPConfig.GROUP_AGGRO.get()) {
            allowNearbyRetaliation(mob, player);
        }
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !(event.getNewTarget() instanceof Player player)) {
            return;
        }

        if (!mayTargetPlayer(mob, player)) {
            event.setNewTarget(null);
            event.setCanceled(true);
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (mob.getTarget() instanceof Player player && !mayTargetPlayer(mob, player)) {
            mob.setTarget(null);
        }

        if (mob instanceof Creeper creeper && PPConfig.AFFECT_CREEPERS.get()
                && creeper.getTarget() instanceof Player player && !mayTargetPlayer(creeper, player)) {
            creeper.setSwellDir(-1);
            creeper.setTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            AggroMemory.forgetMob(mob);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        AggroMemory.forgetPlayer(event.getEntity());
    }

    private static boolean mayTargetPlayer(Mob mob, Player player) {
        return !isAffectedHostile(mob) || AggroMemory.canRetaliate(mob, player);
    }

    private static boolean isAffectedHostile(Mob mob) {
        if (!(mob instanceof Enemy)) {
            return false;
        }

        if (mob instanceof NeutralMob && !(mob instanceof EnderMan)) {
            return false;
        }

        if (mob instanceof EnderMan && !PPConfig.AFFECT_ENDERMEN.get()) {
            return false;
        }

        if (mob instanceof Creeper && !PPConfig.AFFECT_CREEPERS.get()) {
            return false;
        }

        if (PPConfig.EXCLUDE_BOSSES.get() && isBoss(mob)) {
            return false;
        }

        if (!PPConfig.AFFECT_MODDED_MOBS.get() && !isMinecraftEntity(mob)) {
            return false;
        }

        return true;
    }

    private static boolean isBoss(Mob mob) {
        return mob instanceof EnderDragon
                || mob instanceof WitherBoss
                || mob.getAttribute(Attributes.MAX_HEALTH) != null && mob.getMaxHealth() >= 200.0F;
    }

    private static boolean isMinecraftEntity(Mob mob) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        return "minecraft".equals(id.getNamespace());
    }

    private static Player getResponsiblePlayer(DamageSource source) {
        Entity direct = source.getDirectEntity();
        if (direct instanceof Player player) {
            return player;
        }

        Entity owner = source.getEntity();
        if (owner instanceof Player player) {
            return player;
        }

        return null;
    }

    private static void allowNearbyRetaliation(Mob attackedMob, Player player) {
        Level level = attackedMob.level();
        double radius = PPConfig.AGGRO_RADIUS.get();
        if (radius <= 0.0D) {
            return;
        }

        level.getEntitiesOfClass(Mob.class, attackedMob.getBoundingBox().inflate(radius), MobEvents::isAffectedHostile)
                .forEach(mob -> AggroMemory.allowRetaliation(mob, player));
    }

    private MobEvents() {
    }
}
