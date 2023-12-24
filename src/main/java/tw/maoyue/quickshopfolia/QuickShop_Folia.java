package tw.maoyue.quickshopfolia;

import org.bukkit.plugin.java.JavaPlugin;

public final class QuickShop_Folia extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!isFolia()) {
            getLogger().warning("We only support Folia!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
