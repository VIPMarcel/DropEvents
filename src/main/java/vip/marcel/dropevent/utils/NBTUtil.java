package vip.marcel.dropevent.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NBTUtil {

    public static byte[] nbtToBinary(CompoundTag compound) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(compound, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            Logger.getLogger(NBTUtil.class.getSimpleName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static CompoundTag binaryToNBT(byte[] binary) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(binary)) {
            return NbtIo.readCompressed(inputStream);
        } catch (Exception e) {
            Logger.getLogger(NBTUtil.class.getSimpleName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return new CompoundTag();
    }

    public static byte[] itemstackToBinary(ItemStack[] items) {
        CompoundTag inventory = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStack itemStack : items) {
            net.minecraft.world.item.ItemStack craftItem = CraftItemStack.asNMSCopy(itemStack);
            list.add(craftItem.save(new CompoundTag()));
        }
        inventory.put("i", list);
        return nbtToBinary(inventory);
    }

    public static List<ItemStack> binaryToItemStack(byte[] binary) {
        CompoundTag nbt = binaryToNBT(binary);
        List<ItemStack> items = new ArrayList<>();
        if (nbt.contains("i", 9)) {
            ListTag list = nbt.getList("i", 10);
            for (Tag base : list) {
                if (base instanceof CompoundTag) {
                    items.add(CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.of((CompoundTag) base)));
                }
            }
        }
        return items;
    }

    public static byte[] entityToBinary(Entity entity) {
        CompoundTag compound = new CompoundTag();
        ((CraftEntity) entity).getHandle().save(compound);
        return nbtToBinary(compound);
    }

    public static Entity binaryToEntity(byte[] binary, Location location) {
        CompoundTag nbt = binaryToNBT(binary);
        nbt.putLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());
        nbt.putLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());
        nbt.remove("UUID");

        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        net.minecraft.world.entity.Entity entity = EntityType.loadEntityRecursive(nbt, worldServer, (created) -> {
            created.setPos(location.getX(), location.getY(), location.getZ());
            created.setYBodyRot(location.getYaw());
            created.setYHeadRot(location.getPitch());
            return created;
        });

        if (worldServer.addWithUUID(entity)) {
            return CraftEntity.getEntity((CraftServer) Bukkit.getServer(), entity);
        }
        return null;
    }
}
