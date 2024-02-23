package me.lojosho.hibiscuscommons.items;

import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.util.InventoryUtils;
import me.lojosho.hibiscuscommons.util.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Colorable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.*;

public class ItemBuilder {

    @Getter
    private String material;
    @Getter
    private String display;
    @Getter
    private String texture;
    @Getter
    private String skullOwner;
    private ArrayList<String> lore = new ArrayList<>();
    private final ArrayList<String> itemFlags = new ArrayList<>();
    private final HashMap<String, Integer> enchantments = new HashMap<>();
    private final HashMap<NamespacedKey, String> nbtData = new HashMap<>();
    @Getter
    private int amount = 1;
    @Getter
    private int model = -1;
    @Getter
    private LoreAppendMode loreAppendMode;
    @Getter
    private boolean unbreakable = false;
    @Getter
    private boolean glowing = false;
    @Getter
    private Color color;

    public ItemBuilder(String material) {
        ItemStack generatedItem = Hooks.getItem(material);
        if (generatedItem == null) throw new IllegalArgumentException("Item String " + material + " is not valid");
        new ItemBuilder(generatedItem);
    }

    public ItemBuilder(Material material) {
        ItemStack generatedItem = Hooks.getItem(material.toString());
        if (generatedItem == null) throw new IllegalArgumentException("Item String " + material + " is not valid");
        new ItemBuilder(generatedItem);
    }

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.material = Hooks.getStringItem(itemStack);
        this.amount = itemStack.getAmount();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        if (meta.hasDisplayName()) this.display = meta.getDisplayName();
        if (meta.hasCustomModelData()) this.model = meta.getCustomModelData();
        if (meta.isUnbreakable()) this.unbreakable = true;
        if (meta instanceof SkullMeta skullMeta) {
            if (skullMeta.hasOwner()) this.skullOwner = skullMeta.getOwningPlayer().getName();
            if (skullMeta.getPersistentDataContainer().has(InventoryUtils.getSkullTexture(), PersistentDataType.STRING)) {
                this.texture = skullMeta.getPersistentDataContainer().get(InventoryUtils.getSkullTexture(), PersistentDataType.STRING);
            }
        }
        if (meta.hasLore()) this.lore = new ArrayList<>(meta.getLore());
        if (meta instanceof Colorable colorable) {
            this.color = colorable.getColor().getColor();
        }
        if (meta.hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                this.enchantments.put(entry.getKey().getKey().getKey(), entry.getValue());
            }
        }
        if (!meta.getItemFlags().isEmpty()) {
            for (ItemFlag flag : meta.getItemFlags()) {
                this.itemFlags.add(flag.toString());
            }
        }
        if (!meta.getPersistentDataContainer().isEmpty()) {
            for (NamespacedKey key : meta.getPersistentDataContainer().getKeys()) {
                this.nbtData.put(key, meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
            }
        }
    }

    /**
     * Sets the material of the item
     * @param material
     * @return
     */
    public ItemBuilder setMaterial(@NotNull String material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setDisplayName(@NotNull String display) {
        this.display = display;
        return this;
    }

    public ItemBuilder setModelData(int modelData) {
        this.model = modelData;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder setTexture(@NotNull String texture) {
        this.texture = texture;
        return this;
    }

    public ItemBuilder setSkullOwner(@NotNull String username) {
        this.skullOwner = username;
        return this;
    }

    public ItemBuilder setLore(@Nullable ArrayList<String> lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Adds an itemflag to the item
     * @param flags
     * @return
     */
    public ItemBuilder addItemFlag(ItemFlag... flags) {
        for (ItemFlag flag : flags) this.itemFlags.add(flag.toString());
        return this;
    }

    /**
     * Adds an itemflag to the item
     * @param flags
     * @return
     */
    public ItemBuilder addItemFlag(String... flags) {
        this.itemFlags.addAll((Arrays.asList(flags)));
        return this;
    }

    /**
     * Sets the lore append mode on how to add extra lore to an existing item
     * So if you get an item from Oraxen, where do you want the additional lore to be added
     * @param mode
     * @return
     */
    public ItemBuilder setLoreAppendMode(LoreAppendMode mode) {
        this.loreAppendMode = mode;
        return this;
    }

    /**
     * Sets the color of the item
     * @param color
     * @return
     */
    public ItemBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Sets the color of the item
     * @param color
     * @return
     */
    public ItemBuilder setColor(DyeColor color) {
        this.color = color.getColor();
        return this;
    }

    /**
     * Adds an enchantment to the item. If the enchantment is not valid, it will be ignored. Default value is 1
     * @param enchants
     * @return
     */
    public ItemBuilder addEnchantment(String... enchants) {
        for (String enchant : enchants) enchantments.put(enchant, 1);
        return this;
    }

    /**
     * Adds an enchantment to the item. If the enchantment is not valid, it will be ignored
     * @param enchant
     * @param level
     * @return
     */
    public ItemBuilder addEnchantment(String enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    /**
     * Adds an enchantment to the item. If the enchantment is not valid, it will be ignored.
     * @param enchant
     * @param level
     * @return
     */
    public ItemBuilder addEnchantment(Enchantment enchant, int level) {
        enchantments.put(enchant.getKey().getKey(), level);
        return this;
    }

    /**
     * Sets the glowing of the item (Adds the luck and hide enchant item flag to the item when you build the itemstack)
     * @param glowing
     * @return
     */
    public ItemBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    /**
     * Adds NBT data to the item
     * @param key
     * @param value
     * @return
     */
    public ItemBuilder addNBTData(NamespacedKey key, String value) {
        nbtData.put(key, value);
        return this;
    }

    /**
     * Removes the NBT data from the item
     * @param key
     * @return
     */
    public ItemBuilder removeNBTData(NamespacedKey key) {
        nbtData.remove(key);
        return this;
    }

    /**
     * This clears the extra NBT data that has been added. This will not clear the default NBT data that is added by the game.
     * So if you're materially getting an oraxen item, it will not clear the oraxen NBT data.
     * @return
     */
    public ItemBuilder clearNBTData() {
        nbtData.clear();
        return this;
    }

    /**
     * Returns the lore of the item. Use #setLore to set the lore!
     * @return
     */
    public List<String> getLore() {
        return List.copyOf(lore);
    }

    /**
     * Returns all the item flags of the item. Use #addItemFlag to add item flags!
     * @return
     */
    public List<String> getItemFlags() {
        return List.copyOf(itemFlags);
    }

    public List<NamespacedKey> getNBTDataKeys() {
        return List.copyOf(nbtData.keySet());
    }

    /**
     * Check to see if a item has any enchantments
     * @return
     */
    public boolean isEnchanted() {
        return !enchantments.isEmpty();
    }

    public boolean hasEnchantment(String enchantment) {
        return enchantments.containsKey(enchantment);
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return enchantments.containsKey(enchantment.getKey().getKey());
    }

    public boolean hasItemFlag(ItemFlag flag) {
        return itemFlags.contains(flag.toString());
    }

    public boolean hasItemFlag(String flag) {
        return itemFlags.contains(flag);
    }

    public boolean hasNBTData(NamespacedKey key) {
        return nbtData.containsKey(key);
    }

    public ItemStack build() {
        ItemStack itemStack = Hooks.getItem(material);
        if (itemStack == null) return null;
        itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        if (display != null) meta.setDisplayName(StringUtils.parseStringToString(display));
        if (model >= 0) meta.setCustomModelData(model);
        if (!lore.isEmpty()) {
            meta.setUnbreakable(unbreakable);
            List<String> itemLore = Objects.requireNonNullElse(meta.getLore(), new ArrayList<>());
            List<String> lore = new ArrayList<>();
            LoreAppendMode mode = loreAppendMode;

            if (!meta.hasLore() && loreAppendMode != null) mode = LoreAppendMode.IGNORE;
            switch (mode) {
                case IGNORE: // DM lore is not added at all
                    lore.addAll(itemLore);
                    break;
                case TOP: // DM lore is added at the top
                    lore.addAll(this.lore);
                    lore.addAll(itemLore);
                    break;
                case BOTTOM: // DM lore is bottom at the bottom
                    lore.addAll(itemLore);
                    lore.addAll(this.lore);
                    break;
                case OVERRIDE: // Lore from DM overrides the lore from the item
                    lore.addAll(this.lore);
                    break;
            }

            lore.forEach(StringUtils::parseStringToString);
            meta.setLore(lore);
        }

        if (meta instanceof SkullMeta skullMeta) {
            if (skullOwner != null) {
                if (skullOwner.contains("%")) {
                    // This means it has PAPI placeholders in it
                    skullMeta.getPersistentDataContainer().set(InventoryUtils.getSkullOwner(), PersistentDataType.STRING, skullOwner);
                }
                OfflinePlayer player = Bukkit.getOfflinePlayer(skullOwner);
                skullMeta.setOwningPlayer(player);
            }
            if (texture != null) {
                if (texture.contains("%")) {
                    // This means it has PAPI placeholders in it
                    skullMeta.getPersistentDataContainer().set(InventoryUtils.getSkullTexture(), PersistentDataType.STRING, texture);
                }
                // Decodes the texture string and sets the texture url to the skull
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                String decoded = new String(Base64.getDecoder().decode(texture));
                URL url = null;
                try {
                    url = new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (url != null) {
                    textures.setSkin(url);
                    profile.setTextures(textures);
                    skullMeta.setOwnerProfile(profile);
                }
            }
        }
        if (itemFlags != null && !itemFlags.isEmpty()) {
            for (String flag : itemFlags) {
                if (!EnumUtils.isValidEnum(ItemFlag.class, flag)) continue;
                ItemFlag iFlag = ItemFlag.valueOf(flag);
                meta.addItemFlags(iFlag);
            }
        }

        if (glowing) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (enchantments != null && !enchantments.isEmpty()) {
            for (String enchantment : enchantments.keySet()) {
                if (Enchantment.getByKey(NamespacedKey.minecraft(enchantment)) == null) continue;
                meta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantment)), enchantments.get(enchantment), true);
            }
        }

        if (!nbtData.isEmpty()) {
            for (NamespacedKey key : nbtData.keySet()) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, nbtData.get(key));
            }
        }

        if (meta instanceof Colorable colorable) {
            if (color != null) colorable.setColor(DyeColor.getByColor(color));
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

}
