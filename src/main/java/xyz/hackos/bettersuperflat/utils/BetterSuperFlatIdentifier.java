package xyz.hackos.bettersuperflat.utils;

public class BetterSuperFlatIdentifier extends net.minecraft.util.Identifier {
    public static final String NAMESPACE = "bettersuperflat";

    public BetterSuperFlatIdentifier(String path) {
        super(NAMESPACE, path);
    }
}
