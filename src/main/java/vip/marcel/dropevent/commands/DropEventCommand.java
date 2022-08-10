package vip.marcel.dropevent.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import vip.marcel.dropevent.DropEvent;
import vip.marcel.dropevent.utils.NBTUtil;

import java.util.concurrent.ThreadLocalRandom;

public record DropEventCommand(DropEvent plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {

        if(sender instanceof Player player) {

            if(arguments.length == 0) {

                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent alert §8» §7Sende eine Ankündigungsnachricht");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent start §8» §7Startet das DropEvent");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent stop §8» §7Stoppt das DropEvent");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent items §8» §7Verwalte die Items");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent setPos [1-10] §8» §7Setze die Spawnpositionen");
                return true;

            }else if(arguments.length == 1) {

                if(arguments[0].equalsIgnoreCase("alert")) {

                    Bukkit.broadcastMessage(this.plugin.getPrefix() + "§e§lEin neues Event startet in Kürze..");

                    Bukkit.getOnlinePlayers().forEach(players -> {
                        players.playSound(players.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 0.5F, 0.5F);
                        players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);
                    });

                } else if(arguments[0].equalsIgnoreCase("start")) {

                    if(this.plugin.getEventRunnable() == null) {

                        if(this.plugin.getDropLocationList().isEmpty()) {
                            player.sendMessage(this.plugin.getPrefix() + "§cEs wurden noch keine Positionen gesetzt.");
                            return true;
                        }

                        this.plugin.setEventRunnable(new BukkitRunnable() {
                            @Override
                            public void run() {
                                // Useless - Nur damit man das Event nicht mehrmals starten kann.
                            }
                        });
                        Bukkit.broadcastMessage(this.plugin.getPrefix() + "§e§lEin neues Event beginnt!");

                        Bukkit.getOnlinePlayers().forEach(players -> {
                            players.playSound(players.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_4, 0.5F, 0.5F);
                            players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F);
                        });

                        Bukkit.getServer().getScheduler().runTaskLater(this.plugin, task -> {

                            this.plugin.setEventRunnable(new BukkitRunnable() {
                                @Override
                                public void run() {

                                    Location dropLocation = DropEventCommand.this.plugin.getDropLocationList().get(ThreadLocalRandom.current().nextInt(DropEventCommand.this.plugin.getDropLocationList().size()));

                                    ItemStack dropItem = getRandomItem();

                                    if(dropItem.getType().equals(Material.EXPERIENCE_BOTTLE)) {
                                        Bukkit.getWorld(dropLocation.getWorld().getName()).spawnEntity(dropLocation, EntityType.THROWN_EXP_BOTTLE);
                                    } else {
                                        Bukkit.getWorld(dropLocation.getWorld().getName()).dropItemNaturally(dropLocation, dropItem);
                                    }

                                    Bukkit.getOnlinePlayers().forEach(players -> {
                                        players.playSound(dropLocation, Sound.ENTITY_ITEM_PICKUP, 0.1F, 0.1F);
                                    });

                                    Bukkit.getWorld(dropLocation.getWorld().getName()).spawnParticle(Particle.FLASH, dropLocation, 1);

                                }
                            });

                            this.plugin.getEventRunnable().runTaskTimer(this.plugin, 20 * 2, 20 * 2);

                        }, 20 * 3);

                    } else {
                        player.sendMessage(this.plugin.getPrefix() + "§cDu kannst das Event nicht starten, weil es bereits läuft.");
                        return true;
                    }

                } else if(arguments[0].equalsIgnoreCase("stop")) {

                    if(this.plugin.getEventRunnable() != null) {

                        this.plugin.getEventRunnable().cancel();
                        this.plugin.setEventRunnable(null);

                        Bukkit.broadcastMessage(this.plugin.getPrefix() + "§e§lDas Event wurde beendet.");

                        Bukkit.getOnlinePlayers().forEach(players -> {
                            players.playSound(players.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2F, 0.2F);
                            players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 0.2F);
                        });

                    } else {
                        player.sendMessage(this.plugin.getPrefix() + "§cDerzeit ist kein Event am laufen.");
                        return true;
                    }

                } else if(arguments[0].equalsIgnoreCase("items")) {

                    this.plugin.getJSONConfigManager().saveItemByte(NBTUtil.itemstackToBinary(this.plugin.getDropItemsInventory().getContents()));
                    player.openInventory(this.plugin.getDropItemsInventory());
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 0.7F);
                    return true;

                } else {
                    player.sendMessage(this.plugin.getPrefix() + "§e/dropevent alert §8» §7Sende eine Ankündigungsnachricht");
                    player.sendMessage(this.plugin.getPrefix() + "§e/dropevent start §8» §7Startet das DropEvent");
                    player.sendMessage(this.plugin.getPrefix() + "§e/dropevent stop §8» §7Stoppt das DropEvent");
                    player.sendMessage(this.plugin.getPrefix() + "§e/dropevent items §8» §7Verwalte die Items");
                    player.sendMessage(this.plugin.getPrefix() + "§e/dropevent setPos [1-10] §8» §7Setze die Spawnpositionen");
                    return true;
                }

            } else if(arguments.length == 2 && arguments[0].equalsIgnoreCase("setPos")) {

                int number;

                try {
                    number = Integer.parseInt(arguments[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(this.plugin.getPrefix() + "§e" + arguments[1] + " §cist gültige keine Zahl.");
                    return true;
                }

                if(number < 1 || number > 10) {
                    player.sendMessage(this.plugin.getPrefix() + "§e" + arguments[1] + " §cist gültige keine Zahl.");
                    return true;
                }

                this.plugin.getLocationManager().saveLocation(number, player.getLocation());
                this.plugin.getLocationManager().loadLocations();

                player.sendMessage(this.plugin.getPrefix() + "Position §e" + number + " §7gesetzt, Positionen werden neu geladen.");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3F, 0.3F);
                return true;


            } else {

                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent alert §8» §7Sende eine Ankündigungsnachricht");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent start §8» §7Startet das DropEvent");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent stop §8» §7Stoppt das DropEvent");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent items §8» §7Verwalte die Items");
                player.sendMessage(this.plugin.getPrefix() + "§e/dropevent setPos [1-10] §8» §7Setze die Spawnpositionen");
                return true;

            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler.");
            return true;
        }

        return true;
    }

    private ItemStack getRandomItem() {
        ItemStack dropItem = DropEventCommand.this.plugin.getDropItemsInventory().getItem(ThreadLocalRandom.current().nextInt(DropEventCommand.this.plugin.getDropItemsInventory().getSize()));

        if(dropItem == null || dropItem.getType() == Material.AIR) {
            return getRandomItem();
        } else {
            return dropItem;
        }
    }

}
