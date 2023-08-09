package xyz.hackos.bettersuperflat.utils;

import net.minecraft.util.Identifier;

public class BetterSuperFlatIdentifier extends Identifier {
    public static final String NAMESPACE = "bettersuperflat";

    public BetterSuperFlatIdentifier(String path) {
        super(NAMESPACE, path);
    }
}
