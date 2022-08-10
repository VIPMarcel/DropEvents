package vip.marcel.dropevent.utils.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import vip.marcel.dropevent.DropEvent;

import java.io.File;
import java.io.IOException;

public class LocationManager {

    private final DropEvent plugin;

    private File file, directory;

    private YamlConfiguration configuration;

    public LocationManager(DropEvent plugin) {
        this.plugin = plugin;

        this.directory = new File("plugins/DropEvent/");
        this.file = new File(this.directory, "locations.yml");

        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        this.initConfigs();
    }

    private void initConfigs() {

        if(!this.directory.exists()) {
            this.directory.mkdir();
        }

        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
                this.configuration.save(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void loadLocations() {

        this.plugin.getDropLocationList().clear();

        for(int i = 0; i < 11; i++) {
            if(doesLocationExists(i)) {
                this.plugin.getDropLocationList().add(getLocation(i));
            }
        }

    }

    public boolean doesLocationExists(int number) {
        return this.configuration.getString("Location." + number + ".Worldname") != null;
    }

    public Location getLocation(int number) {
        final String worldName = this.configuration.getString("Location." + number + ".Worldname");
        final double x = this.configuration.getDouble("Location." + number + ".X");
        final double y = this.configuration.getDouble("Location." + number + ".Y");
        final double z = this.configuration.getDouble("Location." + number + ".Z");

        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void saveLocation(int number, Location location) {
        this.configuration.set("Location." + number + ".Worldname", location.getWorld().getName());
        this.configuration.set("Location." + number + ".X", location.getX());
        this.configuration.set("Location." + number + ".Y", location.getY());
        this.configuration.set("Location." + number + ".Z", location.getZ());
        try {
            this.configuration.save(this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
