package io.github.lefoxxy.passiveuntilprovoked;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AggroMemory {
    private static final ConcurrentHashMap<UUID, Set<UUID>> ALLOWED_TARGETS = new ConcurrentHashMap<>();

    public static void allowRetaliation(Mob mob, Player player) {
        ALLOWED_TARGETS
                .computeIfAbsent(mob.getUUID(), ignored -> ConcurrentHashMap.newKeySet())
                .add(player.getUUID());
    }

    public static boolean canRetaliate(Mob mob, Player player) {
        return ALLOWED_TARGETS
                .getOrDefault(mob.getUUID(), Collections.emptySet())
                .contains(player.getUUID());
    }

    public static void forgetMob(Mob mob) {
        ALLOWED_TARGETS.remove(mob.getUUID());
    }

    public static void forgetPlayer(Player player) {
        UUID playerId = player.getUUID();
        ALLOWED_TARGETS.values().forEach(players -> players.remove(playerId));
        ALLOWED_TARGETS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private AggroMemory() {
    }
}
