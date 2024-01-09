package dev.nevergodz.specplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpecCommand implements CommandExecutor {
    private final Plugin plugin;
    private final Map<UUID, String> previousListNames = new HashMap<>();
    private final Map<UUID, Location> previousLocations = new HashMap<>();
    private final Map<UUID, GameMode> previousGameModes = new HashMap<>();

    public SpecCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp()) {
                player.sendMessage("У вас нет прав для этой команды, даже не пытайтесь.");
                return true;
            }

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("false")) {
                    stopSpectating(player);
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        startSpectating(player, target);
                    } else {
                        player.sendMessage("Игрок не найден.");
                    }
                }
            }
        } else {
            sender.sendMessage("Только игроки могут использовать эту команду.");
        }
        return true;
    }

    private void startSpectating(Player player, Player target) {
        previousLocations.put(player.getUniqueId(), player.getLocation());
        previousGameModes.put(player.getUniqueId(), player.getGameMode());
        previousListNames.put(player.getUniqueId(), player.getPlayerListName());

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(target);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            otherPlayer.hidePlayer(player);
        }


        player.setPlayerListName(null);

        player.sendMessage("Вы следите за игроком " + target.getName());
    }

    private void stopSpectating(Player player) {
        player.setGameMode(previousGameModes.getOrDefault(player.getUniqueId(), GameMode.SURVIVAL));
        player.teleport(previousLocations.getOrDefault(player.getUniqueId(), Bukkit.getWorlds().get(0).getSpawnLocation()));

        player.setPlayerListName(previousListNames.get(player.getUniqueId()));
        previousListNames.remove(player.getUniqueId());

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            otherPlayer.showPlayer(player);
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        previousLocations.remove(player.getUniqueId());
        previousGameModes.remove(player.getUniqueId());

        player.sendMessage("Вы вышли с режима слежки.");
    }
}
