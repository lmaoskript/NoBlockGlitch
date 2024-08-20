package sk.addon.noBlockGlitch;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class BlockPlacement implements Listener {

    private final NoBlockGlitch plugin;

    public BlockPlacement(NoBlockGlitch plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            handleBlockPlacement(event);
        } else {
            Bukkit.getScheduler().runTaskLater(NoBlockGlitch.instance, new Runnable() {
                @Override
                public void run() {
                    Material type = event.getBlock().getType();
                    if (type.isAir()) {
                        handleBlockPlacement(event);
                    }
                }
            }, 1L);
        }
    }

    private void handleBlockPlacement(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation().clone().subtract(0, 0.3, 0); // Get block below player

        Block blockUnderPlayer = playerLocation.getBlock();
        Block placedBlock = event.getBlock();

        if (isBlockUnderPlayerSameAsPlacedBlock(blockUnderPlayer, placedBlock)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> handleBlockGlitch(player, playerLocation), 1L);
        }
    }

    private boolean isBlockUnderPlayerSameAsPlacedBlock(Block blockUnderPlayer, Block placedBlock) {
        Location blockUnderLoc = blockUnderPlayer.getLocation();
        Location placedBlockLoc = placedBlock.getLocation();

        return Math.round(blockUnderLoc.getX()) == Math.round(placedBlockLoc.getX()) &&
                Math.round(blockUnderLoc.getZ()) == Math.round(placedBlockLoc.getZ()) &&
                Math.round(blockUnderLoc.getY()) == Math.round(placedBlockLoc.getY());
    }

    private void handleBlockGlitch(Player player, Location location) {
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        location.setX(player.getLocation().getX());
        location.setZ(player.getLocation().getZ());

        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("use-velocity")) {
            int speed = config.getInt("pull-speed");
            Vector downwardVelocity = new Vector(0, -speed, 0);
            player.setVelocity(player.getVelocity().add(downwardVelocity));
        } else {
            player.teleport(location);
        }

        if (config.getBoolean("log-flags")) {
            Bukkit.getLogger().log(Level.WARNING, "[NoBlockGlitch] Teleported " + player.getName() + " due to block glitching!");
        }
    }

}
