package io.github.xxyy.dogetrampoline;

import io.github.xxyy.dogetrampoline.api.NMSBlockProxy;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory, which loves to produce {@link io.github.xxyy.dogetrampoline.api.NMSBlockProxy}s for Sponges
 * that work with the Minecraft server running the plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05.01.14
 */
public class NMSBlockProxyFactory {
    private final Plugin plugin;
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d)+\\.(\\d)+\\.(\\d)+");

    public NMSBlockProxy getProxy() {
        return proxy;
    }

    private NMSBlockProxy proxy;

    protected NMSBlockProxyFactory(final DogeTrampolinePlugin dogeTrampolinePlugin) {
        this.plugin = dogeTrampolinePlugin;
    }

    @SuppressWarnings("deprecation") //getServerVersion()
    public NMSBlockProxyFactory tryHook() {
        String packageVersion = getPackageVersion();
        if (packageVersion == null) {
            packageVersion = getServerVersion();
        }

        Class<?> clazz;
        try {
            clazz = Class.forName("io.github.xxyy.dogetrampoline.nms." + packageVersion + ".BlockSpongeProxy");
        } catch (ClassNotFoundException cnfe) {
            plugin.getLogger().severe("Sorry, could not find implementation for your Minecraft version :( " +
                    "Please try downgrading to a version that is known to work or wait for a new version of the " +
                    "plugin to be released. (Create an issue on GitHub if there is none)");
            throw new UnsupportedOperationException("Unsupported Minecraft version. (See log for what to do now)", cnfe);
        }

        if (!NMSBlockProxy.class.isAssignableFrom(clazz)) {
            throw new AssertionError("Targeted Class does not implement NMSBlockProxy: " + clazz.getName());
        }

        Constructor constructor;
        try {
            constructor = clazz.getDeclaredConstructor(DogeTrampolinePlugin.class);

            this.proxy = (NMSBlockProxy) constructor.newInstance(plugin);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        this.proxy.hook();

        return this;
    }

    private String getPackageVersion() {
        try {
            Class<?> serverClass = plugin.getServer().getClass();
            if (serverClass.getName().contains("CraftServer")) {
                String packageName = serverClass.getPackage().getName();
                int dotIndex = packageName.lastIndexOf('.');
                if (dotIndex != -1) {
                    return packageName.substring(dotIndex + 1);
                }
            }
        } catch (NoClassDefFoundError ignore) {
        }
        plugin.getLogger().warning("You seem to have a pretty modified version of CraftBukkit. " +
                "Could not get Package Version from CraftServer's package. Falling back to alternative implementation. " +
                "If you experience any further errors with this plugins, these will almost definitely be caused by this. " +
                "To fix this, get CraftBukkit or Spigot.");

        return null;
    }

    /**
     * Alternative logic to get the version string for packages.
     *
     * @return version String of NMS package.
     * @deprecated Unreliable!
     */
    @Deprecated
    private String getServerVersion() {
        String bukkitVersion = plugin.getServer().getBukkitVersion();
        int dashPosition = bukkitVersion.indexOf('-'); //e.g. 1.7.2-R0.2
        if (dashPosition != -1) {
            bukkitVersion = bukkitVersion.substring(0, dashPosition);
        } else {
            bukkitVersion = bukkitVersion.substring(0, 4); //e.g. 1.7.2-R0.2
        }

        Matcher matcher = VERSION_PATTERN.matcher(bukkitVersion);
        if (!matcher.find()) {
            plugin.getLogger().severe("The plugin is really having a hard time determining your Bukkit version. " +
                    "Also failed to get from Server#getBukkitVersion(). Get Spigot. I'm giving up.");
            throw new IllegalStateException("Logic failed");
        }
        StringBuilder resultBuilder = new StringBuilder('v');
        resultBuilder.append(matcher.group(1)).append('_')
                .append(matcher.group(2)).append("_R")
                .append(matcher.group(3)); //Note how this logic would fail for 1.7.2

        return resultBuilder.toString();
    }
}
