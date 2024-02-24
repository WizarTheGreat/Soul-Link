package soullink.soullink;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static soullink.soullink.SoulLink.*;

public class Commands implements CommandExecutor {

    private final TextColor grey = TextColor.color(88, 88, 88);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("linkinfo")) {
            if (args.length < 1 || !linked.containsKey(args[0])) {
                sender.sendMessage("No linked player selected.");
                return true;
            } else {
                sender.sendMessage("This player is linked with " + linked.get(args[0]) + ".");
                return true;
            }

        } else if (cmd.getName().equalsIgnoreCase("killvalue") && linked.containsKey(sender.getName())) {
            sender.sendMessage("You need " + killCounter.get(sender.getName()) + " more kills to be freed!");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("deathvalue") && linked.containsKey(sender.getName())) {
            sender.sendMessage("You are " + (deathCounter.get(sender.getName())) + " more chances away from a deathban!");
            return true;
        } else {
            if (!(sender.isOp())) {
                if (cmd.getName().equalsIgnoreCase("linkinfo")) {
                    if (args.length < 1 || !linked.containsKey(args[0])) {
                        sender.sendMessage("No linked player selected.");
                    } else {
                        sender.sendMessage("This player is linked with " + linked.get(args[0]) + ".");
                    }

                } else if (cmd.getName().equalsIgnoreCase("killvalue") && linked.containsKey(sender.getName())) {
                    sender.sendMessage("You need " + killCounter.get(sender.getName()) + " more kills to be freed!");
                } else if (cmd.getName().equalsIgnoreCase("deathvalue") && linked.containsKey(sender.getName())) {
                    sender.sendMessage("You are " + (deathCounter.get(sender.getName())) + " more chances away from a deathban!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Only admins can deal such curses.");
                    return true;
                }
            }

            if (cmd.getName().equalsIgnoreCase("link")) {
                if (args.length < 2) {
                    sender.sendMessage("Not enough players selected.");
                    return true;
                } else if (args[0].equalsIgnoreCase(args[1])) {
                    sender.sendMessage("The player cannot be linked to themself.");
                } else if (Bukkit.getPlayer(args[0]) == null || Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage("One or more players are offline or do not exist.");
                    return true;
                } else {
                    Player player1 = Bukkit.getPlayer(args[0]);
                    Player player2 = Bukkit.getPlayer(args[1]);
                    assert player1 != null;
                    assert player2 != null;
                    if (linked.containsKey(player1.getName()) && linked.get(player1.getName()).equals(player2.getName()) ||
                            linked.containsKey(player2.getName()) && linked.get(player2.getName()).equals(player1.getName())) {
                        sender.sendMessage("These players are already linked!");
                    } else {
                        sender.sendMessage(Component.text("Linked Players!", grey));
                        player1.sendMessage("You have been Soul Linked to " + player2.getName() + "!");
                        player1.sendMessage(Component.text("Your levels, potion effects, inventories, and health bars have been linked together! You must kill to be freed.", grey));
                        player2.sendMessage("You have been Soul Linked to " + player1.getName() + "!");
                        player2.sendMessage(Component.text("Your levels, potion effects, inventories, and health bars have been linked together! You must kill to be freed.", grey));
                        linked.put(player1.getName(), player2.getName());
                        linked.put(player2.getName(), player1.getName());
                        deathCounter.put(player1.getName(), deathLimit);
                        killCounter.put(player2.getName(), free);
                        deathCounter.put(player2.getName(), deathLimit);
                        killCounter.put(player1.getName(), free);
                    }
                    return true;
                }
                return true;
            } else if (cmd.getName().equalsIgnoreCase("unlink")) {
                if (args.length < 2) {
                    sender.sendMessage("Not enough players selected.");
                    return true;
                } else if (linked.containsKey(Objects.requireNonNull(Bukkit.getPlayer(args[0])).getName()) && linked.get(Objects.requireNonNull(Bukkit.getPlayer(args[0])).getName()).equalsIgnoreCase(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getName()) ||
                        linked.containsKey(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getName()) && linked.get(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getName()).equalsIgnoreCase(Objects.requireNonNull(Bukkit.getPlayer(args[0])).getName())) {
                    Player player1 = Bukkit.getPlayer(args[0]);
                    Player player2 = Bukkit.getPlayer(args[1]);
                    sender.sendMessage("These players are unlinked.");
                    player1.sendMessage("You have been unlinked!");
                    player2.sendMessage("You have been unlinked!");
                    linked.remove(player1.getName());
                    linked.remove(player2.getName());
                    deathCounter.remove(player1.getName());
                    killCounter.remove(player1.getName());
                    deathCounter.remove(player2.getName());
                    killCounter.remove(player2.getName());
                } else {
                    sender.sendMessage("These players are not linked!");
                }
            } else if (cmd.getName().equalsIgnoreCase("setlinklevel")) {
                if (args.length < 3) {
                    sender.sendMessage("You need to put both player names and the number of 'kills' they have gotten in.");
                } else if (!linked.containsKey(args[0]) || !linked.containsKey(args[1]) || !Objects.equals(linked.get(args[0]), args[1])) {
                    sender.sendMessage("These players are not linked!");
                } else if (args[2].matches("\\d+")) {
                    int num = Integer.parseInt(args[2]);
                    killCounter.remove(args[0]);
                    killCounter.remove(args[1]);
                    killCounter.put(args[0], free - num);
                    killCounter.put(args[1], free - num);
                } else {
                    sender.sendMessage("You didn't input a number.");
                }
            }
            return true;
        }
    }
}
