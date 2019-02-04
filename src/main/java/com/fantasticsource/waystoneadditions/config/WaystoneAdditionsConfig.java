package com.fantasticsource.waystoneadditions.config;

import com.fantasticsource.waystoneadditions.WaystoneAdditions;
import net.minecraftforge.common.config.Config;

@Config(modid = WaystoneAdditions.MODID)
public class WaystoneAdditionsConfig
{
    @Config.Name("Server Settings")
    public static ServerSettings serverSettings = new ServerSettings();

    public static class ServerSettings
    {
        @Config.Name("Spawnstone")
        @Config.Comment("A global waystone that always generates at natural spawn")
        public SpawnstoneSettings spawnstone = new SpawnstoneSettings();
        @Config.Name("Placed Waystones")
        @Config.Comment("Options for waystones placed by players")
        public PlacedWaystones placed = new PlacedWaystones();
        @Config.Name("Natural Waystones")
        @Config.Comment("Options for naturally generated waystones")
        public NaturalWaystones natural = new NaturalWaystones();

        public class SpawnstoneSettings
        {
            @Config.Name("Block Protection Radius")
            @Config.Comment("Placing/destroying/moving blocks is disabled in this radius around the spawnstone")
            public int blockProtectionRadius = 75;

            @Config.Name("Damage Protection Radius")
            @Config.Comment("Damage is disabled in this radius around the spawnstone")
            public int damageProtectionRadius = 65;

            @Config.Name("Enable Spawnstone")
            @Config.Comment("If enabled, a global waystone always generates at natural spawn")
            public boolean enabled = true;

            @Config.Name("Spawnstone Name")
            @Config.Comment("The name of the spawnstone")
            public String spawnstoneName = "Spawn";
        }

        public class PlacedWaystones
        {
            @Config.Name("Global Waystones")
            @Config.Comment("Options for global waystones")
            public GlobalWaystones global = new GlobalWaystones();

            @Config.Name("Non-Global Waystones")
            @Config.Comment("Options for non-global waystones")
            public NonGlobalWaytones nonGlobal = new NonGlobalWaytones();

            public class GlobalWaystones
            {
                @Config.Name("Block Protection Radius")
                @Config.Comment("Placing/destroying/moving blocks is disabled in this radius around the spawnstone")
                public int blockProtectionRadius = 30;

                @Config.Name("Damage Protection Radius")
                @Config.Comment("Damage is disabled in this radius around the spawnstone")
                public int damageProtectionRadius = 20;

                @Config.Name("Owner Can Build")
                @Config.Comment("If set to true, the waystone owner can build inside the protected zone")
                public boolean ownerCanBuild = true;

                @Config.Name("Owner Can Kill")
                @Config.Comment("If set to true, the waystone owner can deal damage inside the protected zone")
                public boolean ownerCanKill = false;
            }

            public class NonGlobalWaytones
            {
                @Config.Name("Block Protection Radius")
                @Config.Comment("Placing/destroying/moving blocks is disabled in this radius around the spawnstone")
                public int blockProtectionRadius = 20;

                @Config.Name("Damage Protection Radius")
                @Config.Comment("Damage is disabled in this radius around the spawnstone")
                public int damageProtectionRadius = 0;

                @Config.Name("Owner Can Build")
                @Config.Comment("If set to true, the waystone owner can build inside the protected zone")
                public boolean ownerCanBuild = true;

                @Config.Name("Owner Can Kill")
                @Config.Comment("If set to true, the waystone owner can deal damage inside the protected zone")
                public boolean ownerCanKill = false;
            }
        }

        public class NaturalWaystones
        {
            @Config.Name("Mossy Waystones")
            @Config.Comment("Options for mossy waystones (if they are enabled in the config for the Waystones mod)")
            public MossyWaystones mossy = new MossyWaystones();
            @Config.Name("Smooth Waystones")
            @Config.Comment(
                    {
                            "Options for smooth waystones",
                            "",
                            "If mossy waystones are enabled in the config for the Waystones mod, this will pertain only to village waystones"
                    })
            public SmoothWaystones smooth = new SmoothWaystones();

            public class MossyWaystones
            {
                @Config.Name("Block Protection Radius")
                @Config.Comment("Placing/destroying/moving blocks is disabled in this radius around the spawnstone")
                public int blockProtectionRadius = 20;

                @Config.Name("Damage Protection Radius")
                @Config.Comment("Damage is disabled in this radius around the spawnstone")
                public int damageProtectionRadius = 0;

                @Config.Name("Finder Becomes Owner")
                @Config.Comment("If set to true, the first player to activate the waystone becomes its owner")
                public boolean finderBecomesOwner = true;

                @Config.Name("Owner Can Build")
                @Config.Comment("If set to true, the waystone owner can build inside the protected zone")
                public boolean ownerCanBuild = true;

                @Config.Name("Owner Can Kill")
                @Config.Comment("If set to true, the waystone owner can deal damage inside the protected zone")
                public boolean ownerCanKill = false;
            }

            public class SmoothWaystones
            {
                @Config.Name("Block Protection Radius")
                @Config.Comment("Placing/destroying/moving blocks is disabled in this radius around the spawnstone")
                public int blockProtectionRadius = 75;

                @Config.Name("Damage Protection Radius")
                @Config.Comment("Damage is disabled in this radius around the spawnstone")
                public int damageProtectionRadius = 0;

                @Config.Name("Finder Becomes Owner")
                @Config.Comment("If set to true, the first player to activate the waystone becomes its owner")
                public boolean finderBecomesOwner = true;

                @Config.Name("Owner Can Build")
                @Config.Comment("If set to true, the waystone owner can build inside the protected zone")
                public boolean ownerCanBuild = false;

                @Config.Name("Owner Can Kill")
                @Config.Comment("If set to true, the waystone owner can deal damage inside the protected zone")
                public boolean ownerCanKill = false;
            }
        }
    }
}
