package vip.marcel.dropevent.utils.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import vip.marcel.dropevent.DropEvent;
import vip.marcel.dropevent.utils.NBTUtil;

import java.io.*;
import java.util.Base64;

public class JsonConfigManager {

    private final DropEvent plugin;

    private File file, directory;

    public JsonConfigManager(DropEvent plugin) {
        this.plugin = plugin;

        this.directory = new File("plugins/DropEvent/");
        this.file = new File(this.directory, "items.json");

        this.initConfigs();
    }

    private void initConfigs() {

        if(!this.directory.exists()) {
            this.directory.mkdir();
        }

        if(!this.file.exists()) {
            try {
                this.file.createNewFile();

                ItemStack stoneStack = new ItemStack(Material.STONE);
                ItemStack woodenSwordStack = new ItemStack(Material.WOODEN_SWORD);

                saveItemByte(NBTUtil.itemstackToBinary(new ItemStack[]{stoneStack, woodenSwordStack}));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public byte[] getItemByte() {
        try {
            String base64Encoded = this.plugin.getGson().fromJson(new FileReader(this.file), String.class);

            return Base64.getDecoder().decode(base64Encoded);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public void saveItemByte(byte[] itemByte) {

        String base64Decoded = Base64.getEncoder().encodeToString(itemByte);

        try {
            FileWriter writer = new FileWriter(this.file);

            writer.write(this.plugin.getGson().toJson(base64Decoded));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
