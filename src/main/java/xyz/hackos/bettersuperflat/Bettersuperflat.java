package xyz.hackos.bettersuperflat;

import net.fabricmc.api.ModInitializer;
import xyz.hackos.bettersuperflat.gen.BetterSuperFlatWorldPresets;

public class Bettersuperflat implements ModInitializer {
    @Override
    public void onInitialize() {
        BetterSuperFlatWorldPresets.registerAll();
    }
}
