package io.github.lefoxxy.passiveuntilprovoked;

import net.minecraftforge.common.ForgeConfigSpec;

public final class PPConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue EXCLUDE_BOSSES;
    public static final ForgeConfigSpec.BooleanValue AFFECT_MODDED_MOBS;
    public static final ForgeConfigSpec.BooleanValue GROUP_AGGRO;
    public static final ForgeConfigSpec.IntValue AGGRO_RADIUS;
    public static final ForgeConfigSpec.BooleanValue AFFECT_CREEPERS;
    public static final ForgeConfigSpec.BooleanValue AFFECT_ENDERMEN;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("passive_until_provoked");

        EXCLUDE_BOSSES = builder
                .comment("If true, boss mobs keep vanilla aggression.")
                .define("excludeBosses", true);

        AFFECT_MODDED_MOBS = builder
                .comment("If true, hostile mobs from other mods are affected when they use vanilla hostile mob contracts.")
                .define("affectModdedMobs", true);

        GROUP_AGGRO = builder
                .comment("If true, nearby affected hostile mobs may retaliate when one hostile mob is attacked.")
                .define("groupAggro", false);

        AGGRO_RADIUS = builder
                .comment("Radius used by groupAggro.")
                .defineInRange("aggroRadius", 16, 0, 128);

        AFFECT_CREEPERS = builder
                .comment("If true, creepers do not swell near players until provoked.")
                .define("affectCreepers", true);

        AFFECT_ENDERMEN = builder
                .comment("If true, endermen are passive toward players until provoked. If false, vanilla enderman behavior is preserved.")
                .define("affectEndermen", false);

        builder.pop();

        SPEC = builder.build();
    }

    private PPConfig() {
    }
}
