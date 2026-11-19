package nl.tnk.combatpunchy;

import java.util.HashMap;
import java.util.Map;

public final class CataclysmFallbackPolicyTest {
    private static int checks;
    public static void main(String[] args) {
        var registry = Map.of("cataclysm:infernal_forge", "infernal",
                "cataclysm:void_forge", "void", "cataclysm:brontes", "brontes");
        Map<String, String> assigned = new HashMap<>();
        check(CataclysmFallbackPolicy.ITEMS.size() == 3, "Fallback scope is exactly three known weapons");
        check(install(registry, assigned) == 3, "All existing unassigned weapons receive a real preset");
        check(assigned.equals(Map.of("infernal", "pickaxe:infernal", "void", "pickaxe:void", "brontes", "pickaxe:brontes")),
                "The native factory receives the actual registered item");
        check(install(registry, assigned) == 0, "Repeated reload completion is idempotent");
        assigned.put("infernal", "explicit-empty");
        assigned.put("void", "pack-greatsword");
        assigned.put("brontes", "addon-axe");
        check(install(registry, assigned) == 0, "Empty/custom/native assignments all take priority");
        check(assigned.get("infernal").equals("explicit-empty"), "An explicit empty capability is never replaced");
        check(assigned.get("void").equals("pack-greatsword"), "User pack movesets remain authoritative");
        check(assigned.get("brontes").equals("addon-axe"), "Addon movesets remain authoritative");
        assigned.clear();
        check(install(Map.of(), assigned) == 0 && assigned.isEmpty(), "Cataclysm absence makes no registrations");
        check(install(Map.of("minecraft:diamond_pickaxe", "ordinary"), assigned) == 0,
                "Ordinary pickaxes and unrelated IDs are outside fallback scope");
        check(install(Map.of("cataclysm:infernal_forge", "infernal"), assigned) == 1,
                "Loader versions with only some known items register only existing entries");
        assigned.clear();
        check(CataclysmFallbackPolicy.installMissing(registry::get, assigned::get, item -> null, assigned::put) == 0,
                "Unavailable factories never install null or invent capabilities");
        System.out.println("PASS: " + checks + " Cataclysm fallback precedence/scope checks");
    }
    private static int install(Map<String, String> registry, Map<String, String> assigned) {
        return CataclysmFallbackPolicy.installMissing(registry::get, assigned::get,
                item -> "pickaxe:" + item, assigned::put);
    }
    private static void check(boolean condition, String description) {
        checks++;
        if (!condition) throw new AssertionError(description);
    }
}
