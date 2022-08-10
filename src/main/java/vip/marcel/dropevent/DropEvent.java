package vip.marcel.dropevent;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vip.marcel.dropevent.commands.DropEventCommand;
import vip.marcel.dropevent.utils.NBTUtil;
import vip.marcel.dropevent.utils.managers.JsonConfigManager;
import vip.marcel.dropevent.utils.managers.LocationManager;

import java.util.List;

public final class DropEvent extends JavaPlugin {

    private String prefix;

    private List<ItemStack> dropItemList;
    private List<Location> dropLocationList;

    private Gson gson;

    private Inventory inventory;

    private BukkitRunnable eventRunnable;

    private JsonConfigManager jsonConfigManager;
    private LocationManager locationManager;

    // Other Events: ReaktionsEvent, ChatQuiz-Event, Items suchen, Location suchen, ...

    @Override
    public void onEnable() {
        this.init();
    }

    @Override
    public void onDisable() {
        this.jsonConfigManager.saveItemByte(NBTUtil.itemstackToBinary(this.inventory.getContents()));
    }

    private void init() {
        this.prefix = "§d§lDropEvent §8» §7";

        this.dropItemList = Lists.newArrayList();
        this.dropLocationList = Lists.newArrayList();

        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.inventory = Bukkit.createInventory(null, 54, "§aDrop Items");

        this.eventRunnable = null;

        this.jsonConfigManager = new JsonConfigManager(this);
        this.locationManager = new LocationManager(this);
        this.locationManager.loadLocations();

        final List<ItemStack> itemStacks = NBTUtil.binaryToItemStack(this.jsonConfigManager.getItemByte());
        this.inventory.setContents(itemStacks.stream().toArray(ItemStack[]::new));

        getCommand("dropevent").setExecutor(new DropEventCommand(this));
    }

    public String getPrefix() {
        return this.prefix;
    }

    public List<ItemStack> getDropItemList() {
        return this.dropItemList;
    }

    public List<Location> getDropLocationList() {
        return this.dropLocationList;
    }

    public Gson getGson() {
        return this.gson;
    }

    public Inventory getDropItemsInventory() {
        return this.inventory;
    }

    public BukkitRunnable getEventRunnable() {
        return this.eventRunnable;
    }

    public void setEventRunnable(BukkitRunnable eventRunnable) {
        this.eventRunnable = eventRunnable;
    }

    public JsonConfigManager getJSONConfigManager() {
        return this.jsonConfigManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

}
