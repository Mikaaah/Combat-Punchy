package nl.tnk.combatpunchy;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Only supply absent defaults; an explicit empty capability is still an assignment. */
public final class CataclysmFallbackPolicy {
    public static final List<String> ITEMS = List.of(
            "cataclysm:infernal_forge", "cataclysm:void_forge", "cataclysm:brontes");

    private CataclysmFallbackPolicy() {}

    public static <I, C> int installMissing(Function<String, I> registry,
            Function<I, C> current, Function<I, C> factory, BiConsumer<I, C> register) {
        int added = 0;
        for (String id : ITEMS) {
            I item = registry.apply(id);
            if (item == null || current.apply(item) != null) continue;
            C capability = factory.apply(item);
            if (capability == null) continue;
            register.accept(item, capability);
            added++;
        }
        return added;
    }
}
