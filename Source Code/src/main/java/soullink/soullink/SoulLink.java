package soullink.soullink;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


import java.io.*;
import java.util.*;

public class SoulLink extends JavaPlugin implements Listener {

    static int deathLimit;
    static int free;
    public int freeExpVal;
    public int freeXp;
    public int freePotionVal;
    public int freePotion;
    public int freeInvenVal;
    public int freeInv;
    public int freeDamageVal;
    public int freeDamage;
    public int freeFoodVal;
    public int freeFood;
    public boolean freeOn;
    public boolean xpOn;
    public boolean potionOn;
    public boolean invenOn;
    public boolean damageOn;
    public boolean foodOn;
    public boolean banOn;


    static Map<String, String> linked = Collections.synchronizedMap(new HashMap<>());
    static Map<NamespacedKey, Inventory> inven = new HashMap<>();
    static Map<String, Integer> killCounter = Collections.synchronizedMap(new HashMap<>());
    static Map<String, Integer> deathCounter = Collections.synchronizedMap(new HashMap<>());
    public Map<NamespacedKey, Float> xpCount = new HashMap<>();
    public Map<NamespacedKey, Integer> levelCount = new HashMap<>();
    public Map<NamespacedKey, Integer> foodCount = new HashMap<>();
    public List<String> list = new ArrayList<>();
    public List<String> pot = new ArrayList<>();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        deathLimit = getConfig().getInt("Death Limit", 5);
        freeOn = getConfig().getBoolean("Freed by kills", true);
        free = getConfig().getInt("Kills to freedom", 4);
        if(freeOn){
            free = getConfig().getInt("Kills to freedom", 4);
        }else{
            free = -1;
        }
        xpOn = getConfig().getBoolean("XP Linking On", true);
        if(xpOn){
            freeExpVal = getConfig().getInt("Kills to xp", 1);
        }else{
            freeExpVal = 0;
        }
        potionOn = getConfig().getBoolean("Potion Linking On", true);
        if(potionOn){
            freePotionVal = getConfig().getInt("Kills to potion", 2);
        }else{
            freePotionVal = 0;
        }
        invenOn = getConfig().getBoolean("Inventory Linking On", true);
        if(invenOn){
            freeInvenVal = getConfig().getInt("Kills to inventory", 3);
        }else{
            freeInvenVal = 0;
        }
        damageOn = getConfig().getBoolean("Damage Linking On", true);
        if(damageOn){
            freeDamageVal = getConfig().getInt("Kills to hp", 4);
        }else{
            freeDamageVal = 0;
        }
        foodOn = getConfig().getBoolean("Food Linking On", true);
        if(foodOn){
            freeFoodVal = getConfig().getInt("Kills to food", 1);
        }else{
            freeFoodVal = 0;
        }
        banOn = getConfig().getBoolean("Death Ban On", true);
        freeXp = free - freeExpVal;
        freePotion = free - freePotionVal;
        freeInv = free - freeInvenVal;
        freeDamage = free - freeDamageVal;
        freeFood =  free - freeFoodVal;
        getCommand("link").setExecutor(new Commands());
        getCommand("unlink").setExecutor(new Commands());
        getCommand("linkinfo").setExecutor(new Commands());
        getCommand("setlinklevel").setExecutor(new Commands());
        getCommand("killvalue").setExecutor(new Commands());
        getCommand("deathvalue").setExecutor(new Commands());
        getCommand("link").setTabCompleter(new TabComplete());
        getCommand("unlink").setTabCompleter(new TabComplete());
        getCommand("linkinfo").setTabCompleter(new TabComplete());
        getCommand("setlinklevel").setTabCompleter(new TabComplete());
        getCommand("killvalue").setTabCompleter(new TabComplete());
        getCommand("deathvalue").setTabCompleter(new TabComplete());

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
                Map<String, Integer> savefood = (Map<String, Integer>) ois.readObject();
                list = (List<String>) ois.readObject();
                inven = loadmap(saveinven);
                xpCount = loadmap(savexp);
                levelCount = loadmap(savelevel);
                foodCount = loadmap(savefood);
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
        Map<String, Integer> savelevel = savemap(levelCount);
        Map<String, Integer> savefood = savemap(foodCount);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(linked);
            oos.writeObject(saveinven2);
            oos.writeObject(killCounter);
            oos.writeObject(deathCounter);
            oos.writeObject(savexp);
            oos.writeObject(savelevel);
            oos.writeObject(savefood);
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
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeInv) {
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
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeInv) {
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
        if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeInv) {
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
    if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeInv) {
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
    if (linked.containsKey(player1.getName()) && killCounter.get(player1.getName()) > freeInv) {
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
        if (killCounter.get(player.getName()) > freeInv) {
            NamespacedKey key1 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player.getName() + player2name);
            NamespacedKey key2 = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "linked" + player2name + player.getName());
            if (inven.get(key1) != null) {
                player.getInventory().setContents(inven.get(key1).getContents());
            }
            if (inven.get(key2) != null) {
                player.getInventory().setContents(inven.get(key2).getContents());
            }
        }
            if (killCounter.get(player.getName()) > freeXp){
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
            if(deathCounter.get(player.getName()) <= 0){
                Date date = null;
                player.ban("You have run out of chances!", date, null);
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
        if (linked.containsKey(hurt.getName()) && killCounter.get(hurt.getName()) > freeDamage) {
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
        if (killer != null &&(linked.containsKey(killer.getName()) && !dead.getName().equals(linked.get(killer.getName()))) && free < 0){
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
            if (kCount == freeXp){
                killer.sendMessage(ChatColor.AQUA + "Your levels are no longer linked. Only " + freeXp + " more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your levels are no longer linked. Only " + freeXp + " more kills to go!");
                }
            } else if (kCount == freePotion){
                killer.sendMessage(ChatColor.AQUA + "Your potion effects are no longer linked. Only " + freePotion + " more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your potion effects are no longer linked. Only " + freePotion + " more kills to go!");
                }
            } else if (kCount == freeInv && freeInv == 1){
                killer.sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only 1 more kill to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only 1 more kill to go!");
                }
            } else if (kCount == freeInv){
                killer.sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeInv + "more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeInv + "more kills to go!");
                }
            } else if (kCount == freeFood){
                killer.sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeFood + "more kills to go!");
                if (Bukkit.getPlayer(linked.get(killer.getName())) != null){
                    Objects.requireNonNull(Bukkit.getPlayer(linked.get(killer.getName()))).sendMessage(ChatColor.AQUA + "Your inventories are no longer linked. Only " + freeFood + "more kills to go!");
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
        getLogger().info("detected");
        Player player = (Player) event.getEntity();
        Player player2 = Bukkit.getPlayer(linked.get(player.getName()));
        if (player2 != null && linked.containsKey(player.getName()) && killCounter.get(player.getName()) > freePotion) {
            if(!pot.contains(player.getName()) && !pot.contains(player2.getName())) {
                pot.add(player.getName());
                pot.add(player2.getName());
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    getLogger().info("pot");
                    player2.clearActivePotionEffects();
                    player2.addPotionEffects(player.getActivePotionEffects());
                }, 0L);
            }else if (pot.contains(player.getName()) || pot.contains(player2.getName())){
                getLogger().info("remove");
                pot.remove(player.getName());
                pot.remove(player2.getName());
            }
        }
    }
    @EventHandler
    public void xp(PlayerExpChangeEvent event){
        Player player = event.getPlayer();
        Player player2 = Bukkit.getPlayer(linked.get(player.getName()));
        getLogger().info(player2.getName());
        getLogger().info(killCounter.get(player.getName()).toString());
        getLogger().info(String.valueOf(freeXp));
        if(player2 != null && (linked.containsKey(player.getName()) && killCounter.get(player.getName()) > freeXp)){
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "xp" + player.getName() + linked.get(player.getName()));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                xpCount.put(key, player.getExp());
                levelCount.put(key, player.getLevel());
                player2.setExp(xpCount.get(key));
                player2.setLevel(levelCount.get(key));
            }, 0L);
        }
    }
    @EventHandler
    public void food(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Player player2 = Bukkit.getPlayer(linked.get(player.getName()));
        if (player2 != null && (linked.containsKey(player.getName()) && killCounter.get(player.getName()) > freeFood)) {
            NamespacedKey key = new NamespacedKey(SoulLink.getPlugin(SoulLink.class), "food" + player.getName() + linked.get(player.getName()));
            Bukkit.getScheduler().runTaskLater(this, () -> {
                foodCount.put(key,player.getFoodLevel());
                levelCount.put(key, player.getLevel());
                if (Bukkit.getPlayer(linked.get(player.getName())) != null) {
                    player2.setFoodLevel(foodCount.get(key));
                }
            }, 0L);
        }
    }
}
//Made by WizarTheGreat