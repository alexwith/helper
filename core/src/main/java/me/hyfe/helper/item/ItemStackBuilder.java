package me.hyfe.helper.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hyfe.helper.config.Config;
import me.hyfe.helper.text.Text;
import me.hyfe.helper.text.replacer.Replacer;
import me.hyfe.helper.version.ServerVersion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class ItemStackBuilder {
    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[]{
            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS,
            ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON
    };

    private final ItemStack itemStack;

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material)).hideAttributes();
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAttributes();
    }

    public static ItemStackBuilder of(Config config, String path) {
        UnaryOperator<String> pathFunc = (string) -> path + "." + string;
        ItemStackBuilder builder = new ItemStackBuilder(new ItemStack(Material.DIRT)).hideAttributes();
        String[] type = config.<String>tryGet(pathFunc.apply("type")).split(":");
        if (type[0].equalsIgnoreCase("head")) {
            builder.head(type[1]);
        } else {
            builder.type(Material.valueOf(type[0].toUpperCase()));
            if (type.length > 1) {
                builder.data(Integer.parseInt(type[1]));
            }
        }
        if (config.has(pathFunc.apply("name"))) {
            builder.name(config.tryGet(pathFunc.apply("name")));
        }
        if (config.has(pathFunc.apply("lore"))) {
            builder.lore(config.<List<String>>tryGet(pathFunc.apply("lore")));
        }
        if (config.has(pathFunc.apply("amount"))) {
            builder.amount(config.tryGet(pathFunc.apply("amount")));
        } else {
            builder.amount(1);
        }
        if (config.has(pathFunc.apply("glow")) && config.<Boolean>tryGet(pathFunc.apply("glow"))) {
            builder.glow();
        }
        return builder;
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public ItemStackBuilder transform(Consumer<ItemStack> itemStack) {
        itemStack.accept(this.itemStack);
        return this;
    }

    public ItemStackBuilder transformMeta(Consumer<ItemMeta> meta) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta != null) {
            meta.accept(itemMeta);
            this.itemStack.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemStackBuilder name(String name) {
        return transformMeta(meta -> meta.setDisplayName(Text.colorize(name)));
    }

    public ItemStackBuilder type(Material material) {
        return transform(itemStack -> itemStack.setType(material));
    }

    public ItemStackBuilder head(String tag) {
        boolean isNew = ServerVersion.isCurrentOver(ServerVersion.MC1_12_R1);
        this.type(Material.valueOf(isNew ? "PLAYER_HEAD" : "SKULL_ITEM"));
        this.data(3);
        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
        if (tag.length() > 16) { // is base64
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", tag));
            Field profileField;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        } else {
            skullMeta.setOwner(tag);
        }
        this.itemStack.setItemMeta(skullMeta);
        return this;
    }

    public ItemStackBuilder lore(String line) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Text.colorize(line));
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemStackBuilder lore(Iterable<String> lines) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(Text.colorize(line));
            }
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder clearLore() {
        return transformMeta(meta -> meta.setLore(new ArrayList<>()));
    }

    public ItemStackBuilder durability(int durability) {
        return transform(itemStack -> itemStack.setDurability((short) durability));
    }

    public ItemStackBuilder data(int data) {
        return durability(data);
    }

    public ItemStackBuilder amount(int amount) {
        return transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder enchant(Enchantment enchantment) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, 1));
    }

    public ItemStackBuilder clearEnchantments() {
        return transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
    }

    public ItemStackBuilder flag(ItemFlag... flags) {
        return transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemStackBuilder unflag(ItemFlag... flags) {
        return transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemStackBuilder hideAttributes() {
        return flag(ALL_FLAGS);
    }

    public ItemStackBuilder showAttributes() {
        return unflag(ALL_FLAGS);
    }

    public ItemStackBuilder glow() {
        this.enchant(Enchantment.DURABILITY, 0);
        return this.flag(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemStackBuilder color(Color color) {
        return transform(itemStack -> {
            Material type = itemStack.getType();
            if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            }
        });
    }

    public ItemStackBuilder breakable(boolean flag) {
        return transformMeta(meta -> meta.spigot().setUnbreakable(!flag));
    }

    public ItemStackBuilder apply(Consumer<ItemStackBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ItemStack build() {
        return this.build(null);
    }

    public ItemStack build(Replacer replacer) {
        return this.itemStack;
    }
}