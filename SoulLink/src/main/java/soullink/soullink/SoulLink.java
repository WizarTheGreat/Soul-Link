package soullink.soullink;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import java.util.Map.Entry;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


import java.io.*;
import java.util.*;

public class SoulLink extends JavaPlugin implements Listener {

    static int deathLimit;
    static int free;
    public int freeExp;
    public int freexp;
    public int freePotions;
    public int freepotion;
    public int freeInven;
    public int freeinv;


    static Map<String, String> linked = new HashMap<>();
    static Map<NamespacedKey, Inventory> inven = new HashMap<>();

    static Map<String, Integer> killCounter = new HashMap<>();
    static Map<String, Integer> deathCounter = new HashMap<>();
    public Map<NamespacedKey, Float> xpCount = new HashMap<>();
    public Map<NamespacedKey, Integer> levelCount = new HashMap<>();
    public List<String> list = new ArrayList<>();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        deathLimit = getConfig().getInt("Death Limit");
        free = getConfig().getInt("Kills to freedom");
        freeExp = getConfig().getInt("Kills to xp");
        freePotions = getConfig().getInt("Kills to potion");
        freeInven = getConfig().getInt("Kills to inventory");
        freexp = free - freeExp;
        freepotion = free - freePotions;
        freeinv = free - freeInven;
        getCommand("link").setExecutor(new Commands());
        getCommand("unlink").setExecutor(new Commands());
        getCommand("linkinfo").setExecutor(new Commands());
        getCommand("setlinklevel").setExecutor(new Commands());
        getCommand("link").setTabCompleter(new TabComplete());
        getCommand("unlink").setTabCompleter(new TabComplete());
        getCommand("linkinfo").setTabCompleter(new TabComplete());
        getCommand("setlinklevel").setTabCompleter(new TabComplete());

        File file = new File(getDataFolder(), "linked.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                linked = (Map<String, String>) ois.readObject();
                Map<String, String> saveinven2 = (Map<String, String>) ois.readObject();
                Map<String, Inventory> saveinven = invenmap2(saveinven2);
                killCounter = (Map<String, Integer>) ois.readObject();
                deathCounter = (Map<String, Integer>) ois.readObject();
                Map<String, Float> savexp = (Map<String, Float>) ois.readObject();
                Map<String, Integer> savelevel = (Map<String, Integer>) ois.readObject();
                list = (List<String>) ois.readObject();
                inven = loadmap(saveinven);
                xpCount = loadmap(savexp);
                levelCount = loadmap(savelevel);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public void onDisable() {
        File file = new File(getDataFolder(), "linked.dat");
        Map<String, Inventory> saveinven1 = savemap(inven);
        Map<String, String> saveinven2 = invenmap(saveinven1);
        Map<String, Float> savexp = savemap(xpCount);
        Map<String, Integer> savelevel= savemap(levelCount);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(linked);
            oos.writeObject(saveinven2);
            oos.writeObject(killCounter);
            oos.writeObject(deathCounter);
            oos.writeObject(savexp);
            oos.writeObject(savelevel);
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public <V> Map<String, V> savemap(Map<NamespacedKey, V> m) {
            Map<String, V> newMap = new HashMap<>();
            for (Map.Entry<NamespacedKey, V> entry : m.entrySet()) {
                NamespacedKey namespacedKey = entry.getKey();
                V value = entry.getValue();
                String entryString = namespacedKey.getKey();

                newMap.put(entryString, value);
            }

            return newMap;
        }
    public <V> Map<NamespacedKey, V> loadmap(Map<String, V> loadmap) {
        Map<NamespacedKey, V> resultMap = new HashMap<>();

        for (Map.Entry<String, V> entry : loadmap.entrySet()) {
            String key = entry.getKey();
            V value = entry.getValue();

            NamespacedKey namespacedKey = new NamespacedKey(this, key);
            resultMap.put(namespacedKey, value);
        }

        return resultMap;
    }
    public <K> Map<K, String> invenmap(Map<K, Inventory> m) {
        Map<K, String> nMap = new HashMap<>();
        for (Map.Entry<K, Inventory> entry : m.entrySet()) {
            Inventory inventory = entry.getValue();
            K key = entry.getKey();
            ItemStack[] held = inventory.getContents();
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                dataOutput.writeObject(held);

                dataOutput.close();

                String mapheld =  Base64Coder.encodeLines(outputStream.toByteArray());
                nMap.put(key, mapheld);
            } catch (Exception e) {
                throw new IllegalStateException("Error whilst saving items, Please contact the developer", e);
            }
        }
        return nMap;
    }
    public <K> Map<K, Inventory> invenmap2(Map<K, String> m) throws IOException {
        Map<K, Inventory> nMap = new HashMap<>();
        for (Map.Entry<K, String> entry : m.entrySet()) {
            String held = entry.getValue();
            K key = entry.getKey();
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(held));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER);
                ItemStack[] itemArray = (ItemStack[]) dataInput.readObject();
                for(ItemStack is: itemArray) {
                    if(is != null) {
                        inventory.addItem(is);
                    }
                }
                dataInput.close();
                nMap.put(key, inventory);
            } catch (ClassNotFoundException e) {
                throw new IOException("Error whilst loading items, Please contact the developer", e);
            }

        }
        return nMap;
    }
    @EventHandler
    public void inventoryEvent(InventoryClickEvent event) {
        Player player1 = (Player) event.getWhoClicked();
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeinv) {
            String player2name = linked.get(player1.getName());
            Player player2 = Bukkit.getPlayer(linked.get(player1.getName()));
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player1.getName() + player2name);
            inven.remove(key);
            inven.put(key, player1.getInventory());
            Bukkit.getScheduler().runTaskLater(this, () -> {
                inven.put(key, player1.getInventory());
                if (player2 != null) {
                    player2.getInventory().setContents(inven.get(key).getContents());
                }
            }, 0L);
        }
    }
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
        Player player1 = event.getPlayer();
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeinv) {
            String player2name = linked.get(player1.getName());
            Player player2 = Bukkit.getPlayer(linked.get(player1.getName()));
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player1.getName() + player2name);
            inven.remove(key);
            inven.put(key, player1.getInventory());
            Bukkit.getScheduler().runTaskLater(this, () -> {
                inven.put(key, player1.getInventory());
                if (player2 != null) {
                    player2.getInventory().setContents(inven.get(key).getContents());
                }
            }, 0L);
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player1 = event.getPlayer();
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeinv) {
            String player2name = linked.get(player1.getName());
            Player player2 = Bukkit.getPlayer(linked.get(player1.getName()));
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player1.getName() + player2name);
            inven.remove(key);
            inven.put(key, player1.getInventory());
            Bukkit.getScheduler().runTaskLater(this, () -> {
                inven.put(key, player1.getInventory());
                if (player2 != null) {
                    player2.getInventory().setContents(inven.get(key).getContents());
                }
            }, 0L);
        }
    }
@EventHandler
public void onRightClick(PlayerInteractEvent event){
    Player player1 = event.getPlayer();
    if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeinv) {
        String player2name = linked.get(player1.getName());
        Player player2 = Bukkit.getPlayer(linked.get(player1.getName()));
        NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player1.getName() + player2name);
        inven.remove(key);
        inven.put(key, player1.getInventory());
        Bukkit.getScheduler().runTaskLater(this, () -> {
            inven.put(key, player1.getInventory());
            if (player2 != null) {
                player2.getInventory().setContents(inven.get(key).getContents());
            }
        }, 0L);

    }
}
@EventHandler
public void onArmorChange(PlayerArmorChangeEvent event){
    Player player1 = event.getPlayer();
    if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeinv) {
        String player2name = linked.get(player1.getName());
        Player player2 = Bukkit.getPlayer(linked.get(player1.getName()));
        NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player1.getName() + player2name);
        inven.remove(key);
        inven.put(key, player1.getInventory());
        Bukkit.getScheduler().runTaskLater(this, () -> {
            inven.put(key, player1.getInventory());
            if (player2 != null) {
                player2.getInventory().setContents(inven.get(key).getContents());
            }
        }, 0L);

    }
}
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String player2name = linked.get(player.getName());
        if (linked.containsKey(player.getName())){
        if (killCounter.get(player.getName()) > freeinv) {
            NamespacedKey key1 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player.getName() + player2name);
            NamespacedKey key2 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player2name + player.getName());
            if (inven.get(key1) != null) {
                player.getInventory().setContents(inven.get(key1).getContents());
            }
            if (inven.get(key2) != null) {
                player.getInventory().setContents(inven.get(key2).getContents());
            }
        }
            if (killCounter.get(player.getName()) > freexp){
                NamespacedKey key1 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "xp" + player.getName() + player2name);
                NamespacedKey key2 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "xp" + player2name + player.getName());
                if (xpCount.get(key1) != null) {
                    player.setExp(xpCount.get(key1));
                    player.setLevel(levelCount.get(key1));
                }
                if (xpCount.get(key2) != null) {
                    player.setExp(xpCount.get(key2));
                    player.setLevel(levelCount.get(key2));
                }
                }

        }
        if(list.contains(player.getName())){
            player.sendMessage("You have been freed from the Soul Link!");
            list.remove(player.getName());
        }
    }
    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
        Player hurt = (Player) event.getEntity();
        if (linked.containsKey(hurt.getName())) {
            double damage = event.getFinalDamage();
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Objects.requireNonNull(Bukkit.getPlayer(linked.get(hurt.getName()))).damage(damage);
            }, 1L);
        }

        }
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event){
        Player dead = event.getEntity();
        Player killer = dead.getKiller();
        Player player2 = Bukkit.getPlayer(linked.get(dead.getName()));
        if (killer != null &&(linked.containsKey(killer.getName()) && !dead.getName().equals(linked.get(killer.getName())))){
            int kCount = killCounter.get(killer.getName());
            kCount -= 1;
            killCounter.remove(killer.getName());
            killCounter.remove(linked.get(killer.getName()));
            killCounter.put(killer.getName(), kCount);
            killCounter.put(linked.get(killer.getName()), kCount);
            if (kCount <= 0){
                killer.sendMessage(ChatColor.AQUA + "Your hp is no longer linked.");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null) {
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage("You have been freed from the Soul Link!");
                }else{
                    list.add(linked.get(killer.getName()));
                }
                linked.remove(killer.getName());
                linked.remove(linked.get(killer.getName()));
                return;
            }
            if (kCount == freexp){
                killer.sendMessage(ChatColor.AQUA + "Your levels are no longer linked. Only " + freexp + " more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your levels are no longer linked. Only " + freexp + " more kills to go!");
                }
            } else if (kCount == freepotion){
                killer.sendMessage(ChatColor.AQUA + "Your potion effects are no longer linked. Only " + freepotion + " more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your potion effects are no longer linked. Only " + freepotion + " more kills to go!");
                }
            } else if (kCount == freeinv && freeinv == 1){
                killer.sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only 1 more kill to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only 1 more kill to go!");
                }
            } else if (kCount == freeinv){
                killer.sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeinv + "more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeinv + "more kills to go!");
                }
            }
        }
        if (linked.containsKey(dead.getName())){
            int dCount = deathCounter.get(dead.getName());
            dCount -=1;
            deathCounter.remove(dead.getName());
            deathCounter.put(dead.getName(), dCount);
            if (deathCounter.get(dead.getName()) <= 0){
                Date date = null;
                dead.ban("You have run out of chances!", date, null);
            }
            else if (dCount > 1){
                dead.sendMessage("You have " + dCount + " chances left!");
            } else if (dCount == 1){
                dead.sendMessage(ChatColor.RED + "You only have 1 chance left!");
            }
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + dead.getName() + linked.get(dead.getName()));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                inven.put(key, dead.getInventory());
                if (player2 != null) {
                    player2.getInventory().setContents(inven.get(key).getContents());
                }
            }, 0L);
        }
    }
    @EventHandler
    public void potion(EntityPotionEffectEvent event){
        Player player = (Player) event.getEntity();
        if (linked.containsKey(player.getName()) && killCounter.get(player.getName()) > freepotion){
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Objects.requireNonNull(Bukkit.getPlayer(linked.get(player.getName()))).addPotionEffects(player.getActivePotionEffects());
            }, 0L);
        }
    }
    @EventHandler
    public void xp(PlayerExpChangeEvent event){
        Player player = event.getPlayer();
        Player player2 = Bukkit.getPlayer(linked.get(player.getName()));
        getLogger().info("kills" + killCounter.get(player.getName()));
        getLogger().info("freexp" + freexp);
        getLogger().info("level" + freeExp);
        getLogger().info("free" + free);
        if(player2 != null && (linked.containsKey(player.getName()) && killCounter.get(player.getName()) > freexp)){
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "xp" + player.getName() + linked.get(player.getName()));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                xpCount.put(key, player.getExp());
                levelCount.put(key, player.getLevel());
                if (Bukkit.getPlayer(linked.get(player.getName())) != null) {
                    player2.setExp(xpCount.get(key));
                    player2.setLevel(levelCount.get(key));
                }
            }, 0L);
        }
    }
}
//Made by WizarTheGreat