package com.etfl.rules4worlds;

public class Rules4Worlds /*implements ModInitializer*/ {
    /*public static final String MOD_ID = "rules4worlds";
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
    public static int getCooldown() {
        return cooldown.get();
    }
    //endregion

    //region Delay
    private static final IntConfigSetting delay = new IntConfigSetting(
            "delay",
            TimeArgumentType.time(-1),
            -1,
            value -> value >= -1
    );
    public static int getDelay() {
        return delay.get();
    }
    //endregion

    //region MaxHomeCount
    private static final IntConfigSetting maxHomeCount = new IntConfigSetting(
            "maxHomeCount",
            IntegerArgumentType.integer(0),
            3,
            value -> value >= 0
    );
    public static int getMaxHomeCount() {
        return maxHomeCount.get();
    }
    //endregion

    //region DisableHomes
    private static final BoolConfigSetting disableHomes = new BoolConfigSetting(
            "disableHomes",
            BoolArgumentType.bool(),
            false
    );
    public static boolean areHomesDisabled() {
        return disableHomes.get();
    }
    //endregion

    public static final ConfigCategory category = new SimpleConfigCategory("homes")
            .addComponent(cooldown)
            .addComponent(delay)
            .addComponent(maxHomeCount)
            .addComponent(disableHomes);*/
}
