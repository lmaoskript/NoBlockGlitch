package sk.addon.noBlockGlitch;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoBlockGlitch extends JavaPlugin {

    static NoBlockGlitch instance;
    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getPluginManager().registerEvents(new BlockPlacement(this), this);

        saveDefaultConfig();
        getCommand("noblockglitch").setExecutor(new MainCommandExecutor());
    }

    @Override
    public void onDisable() {

    }

    private class MainCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length > 0 && "reload".equalsIgnoreCase(args[0])) {
                if (sender.hasPermission("noblockglitch.reload")) {
                    reloadConfig();
                    sender.sendMessage("Configuration reloaded successfully!");
                    return true;
                } else {
                    sender.sendMessage("You don't have permission to use this command.");
                    return false;
                }
            } else {
                sender.sendMessage("Usage: /noblockglitch reload");
                return false;
            }
        }
    }

}
