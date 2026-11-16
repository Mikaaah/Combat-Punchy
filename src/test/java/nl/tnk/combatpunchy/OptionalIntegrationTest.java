package nl.tnk.combatpunchy;

import java.io.IOException;
import java.net.URL;

/** Real class-loading regression: optional entry points must work with neither mod installed. */
public final class OptionalIntegrationTest {
    public static void main(String[] args) throws Exception {
        ClassLoader isolated = new ClassLoader(OptionalIntegrationTest.class.getClassLoader()) {
            @Override
            public URL getResource(String name) {
                return optionalResource(name) ? null : super.getResource(name);
            }

            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                synchronized (getClassLoadingLock(name)) {
                    if (name.startsWith("punchy.") || name.startsWith("punchymoves.")) {
                        throw new AssertionError("Optional type was resolved without its mod: " + name);
                    }
                    if (!name.startsWith("nl.tnk.combatpunchy.compat.")) return super.loadClass(name, resolve);
                    Class<?> type = findLoadedClass(name);
                    if (type == null) {
                        try (var source = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                            if (source == null) throw new ClassNotFoundException(name);
                            byte[] bytes = source.readAllBytes();
                            type = defineClass(name, bytes, 0, bytes.length);
                        } catch (IOException error) {
                            throw new ClassNotFoundException(name, error);
                        }
                    }
                    if (resolve) resolveClass(type);
                    return type;
                }
            }

            private boolean optionalResource(String name) {
                return name.startsWith("punchy/") || name.startsWith("punchymoves/");
            }
        };
        for (String name : new String[]{"PunchyApiCompat", "MovesApiCompat", "PunchyBackAnimation"}) {
            Class<?> entry = Class.forName("nl.tnk.combatpunchy.compat." + name, true, isolated);
            entry.getMethod("register").invoke(null);
        }
        Class.forName("nl.tnk.combatpunchy.compat.PunchyBackAnimation", true, isolated)
                .getMethod("reset").invoke(null);
        System.out.println("PASS: 4 optional-mod absence class-loading checks");
    }
}
