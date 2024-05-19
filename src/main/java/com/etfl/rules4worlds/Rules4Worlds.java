package com.etfl.rules4worlds;

import com.etfl.rules4worlds.categories.ConfigCategory;
import com.etfl.rules4worlds.categories.SimpleConfigCategory;
import com.etfl.rules4worlds.settings.BoolConfigSetting;
import com.etfl.rules4worlds.settings.IntConfigSetting;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.argument.TimeArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Rules4Worlds implements ModInitializer {
    public static final String MOD_ID = "rules4worlds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        new ConfigManager(MOD_ID, "test123456789")
                .add(category)
                .initialize();
    }

    //region Cooldown
    private static final IntConfigSetting cooldown = new IntConfigSetting(
            "cooldown",
            TimeArgumentType.time(-1),
            -1,
            value -> value >= -1
    );
    //endregion

    //region Delay
    private static final IntConfigSetting delay = new IntConfigSetting(
            "delay",
            TimeArgumentType.time(-1),
            -1,
            value -> value >= -1
    );
    //endregion

    //region MaxHomeCount
    private static final IntConfigSetting maxHomeCount = new IntConfigSetting(
            "maxHomeCount",
            IntegerArgumentType.integer(0),
            3,
            value -> value >= 0
    );
    //endregion

    //region DisableHomes
    private static final BoolConfigSetting disableHomes = new BoolConfigSetting(
            "disableHomes",
            BoolArgumentType.bool(),
            false
    );
    //endregion

    public static final ConfigCategory category = new SimpleConfigCategory("homes")
            .addComponent(cooldown)
            .addComponent(delay)
            .addComponent(maxHomeCount)
            .addComponent(disableHomes);
}
