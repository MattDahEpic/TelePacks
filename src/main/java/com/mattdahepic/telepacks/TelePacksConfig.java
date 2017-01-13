package com.mattdahepic.telepacks;

import com.mattdahepic.mdecore.config.sync.Config;
import com.mattdahepic.mdecore.config.sync.ConfigSyncable;

public class TelePacksConfig extends ConfigSyncable {
    public String getConfigVersion () {return "1";}
    public String getConfigName () {return TelePacks.MODID;}
    public Class getConfigClass () {return getClass();}
    
    @Config(comment = {"How long (in ticks) should the basic TelePack take to recharge after teleporting?","Default is 90 ticks."})
    public static int basicRechargeTicks = 90;
    @Config(comment = {"How long (in ticks) should the advanced TelePack take to recharge after teleporting?","Default is 240 ticks."})
    public static int advancedRechargeTicks = 240;
}
