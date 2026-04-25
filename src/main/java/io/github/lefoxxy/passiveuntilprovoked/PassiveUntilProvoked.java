package io.github.lefoxxy.passiveuntilprovoked;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(PassiveUntilProvoked.MOD_ID)
public final class PassiveUntilProvoked {
    public static final String MOD_ID = "passiveuntilprovoked";

    public PassiveUntilProvoked() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PPConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(MobEvents.class);
    }
}
