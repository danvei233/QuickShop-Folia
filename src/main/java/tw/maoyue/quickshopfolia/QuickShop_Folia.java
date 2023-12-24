package tw.maoyue.quickshopfolia;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class QuickShop_Folia extends JavaPlugin {

    private Economy economy;

    @Override
    public void onEnable() {
        if (!isFolia()) {
            getLogger().warning("We only support Folia!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("QuickShop-Folia has been enabled!");

    }

    @Override
    public void onDisable() {
        getLogger().info("QuickShop-Folia has been disabled!");
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("qs")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        createShop(player);
                        return true;
                    }
                }
            } else {
                sender.sendMessage("Only players can use this command!");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("qs")) {
            if (args.length == 1) {

                completions.add("create");

                return completions;
            }
        }

        return null;
    }

    private void createShop(@NotNull Player player) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if (targetBlock.getType() == Material.CHEST) {
            Chest chest = (Chest) targetBlock.getState();
            double price = 10.0; // Set your desired shop price

            EconomyResponse response = economy.withdrawPlayer(player, price);
            if (response.transactionSuccess()) {
                // Chest is the shop, save necessary data to your configuration or database
                // For example, save chest location, price, item information, etc.

                // Generate a sign in front of the chest
                generateShopSign(chest.getLocation(), price);

                player.sendMessage("Shop created successfully!");
            } else {
                player.sendMessage("Failed to create shop. Insufficient funds.");
            }
        } else {
            player.sendMessage("Look at a chest to create a shop!");
        }
    }

    private void generateShopSign(Location chestLocation, double price) {
        Block signBlock = chestLocation.clone().add(0, 1, 1).getBlock(); // Adjust the relative position as needed
        signBlock.setType(Material.OAK_SIGN);

        Sign sign = (Sign) signBlock.getState();
        sign.setLine(0, ChatColor.BOLD + "[Shop]");
        sign.setLine(1, "Price: $" + price);
        sign.setLine(2, "Items");
        sign.setLine(3, "Here");

        sign.update();
    }
}
