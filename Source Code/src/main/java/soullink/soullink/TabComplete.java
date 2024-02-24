package soullink.soullink;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class TabComplete implements TabExecutor {

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Check which command is being tab completed
        if (command.getName().equalsIgnoreCase("link") || command.getName().equalsIgnoreCase("unlink")) {
            // Custom tab completion logic for "link" command

            if (args.length == 1) {
                // Complete with online player names
                List<String> onlinePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(onlinePlayer.getName());
                }

                String input = args[0].toLowerCase();

                for (String playerName : onlinePlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }

                Collections.sort(completions);
                return completions;
            }
            if (args.length == 2) {
                // Complete with online player names
                List<String> onlinePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.getName().equalsIgnoreCase(args[0])) {
                        onlinePlayers.add(onlinePlayer.getName());
                    }
                }

                String input = args[1].toLowerCase();

                for (String playerName : onlinePlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
                Collections.sort(completions);
                return completions;
            }
        } else if (command.getName().equalsIgnoreCase("linkinfo")) {
            if (args.length == 1) {
                // Complete with online player names
                List<String> onlinePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(onlinePlayer.getName());
                }

                String input = args[0].toLowerCase();

                for (String playerName : onlinePlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
                Collections.sort(completions);
                return completions;
            }

            return completions;
        }else if(command.getName().equalsIgnoreCase("setlinklevel")){
            if (args.length == 1) {
                // Complete with online player names
                List<String> onlinePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(onlinePlayer.getName());
                }

                String input = args[0].toLowerCase();

                for (String playerName : onlinePlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }

                Collections.sort(completions);
                return completions;
            }
            if (args.length == 2) {
                // Complete with online player names
                List<String> onlinePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.getName().equalsIgnoreCase(args[0])) {
                        onlinePlayers.add(onlinePlayer.getName());
                    }
                }

                String input = args[1].toLowerCase();

                for (String playerName : onlinePlayers) {
                    if (playerName.toLowerCase().startsWith(input)) {
                        completions.add(playerName);
                    }
                }
                Collections.sort(completions);
                return completions;
            }
        }else if(command.getName().equalsIgnoreCase("killvalue") || command.getName().equalsIgnoreCase("deathvalue")){
            return completions;
        }
        return completions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Your command logic here (if needed)
        return false;
    }
}