package me.lojosho.hibiscuscommons.config.serializer;

import me.lojosho.hibiscuscommons.items.ItemBuilder;
import me.lojosho.hibiscuscommons.items.LoreAppendMode;
import me.lojosho.hibiscuscommons.util.InventoryUtils;
import me.lojosho.hibiscuscommons.util.ServerUtils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ItemBuilderSerializer implements TypeSerializer<ItemBuilder> {

    public static final ItemBuilderSerializer INSTANCE = new ItemBuilderSerializer();
    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String NAME = "name";
    private static final String UNBREAKABLE = "unbreakable";
    private static final String GLOWING = "glowing";
    private static final String LORE = "lore";
    private static final String APPEND_LORE = "lore-append-mode";
    private static final String MODEL_DATA = "model-data";
    private static final String NBT_TAGS = "nbt-tag";
    private static final String ENCHANTS = "enchants";
    private static final String ITEM_FLAGS = "item-flags";
    private static final String TEXTURE = "texture";
    private static final String OWNER = "owner";
    private static final String COLOR = "color";
    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    private ItemBuilderSerializer() {}

    @Override
    public ItemBuilder deserialize(final Type type, final ConfigurationNode source)
            throws SerializationException {
        final ConfigurationNode materialNode = source.node(MATERIAL);
        final ConfigurationNode amountNode = source.node(AMOUNT);
        final ConfigurationNode nameNode = source.node(NAME);
        final ConfigurationNode unbreakableNode = source.node(UNBREAKABLE);
        final ConfigurationNode glowingNode = source.node(GLOWING);
        final ConfigurationNode loreNode = source.node(LORE);
        final ConfigurationNode appendLoreNode = source.node(APPEND_LORE);
        final ConfigurationNode modelDataNode = source.node(MODEL_DATA);
        final ConfigurationNode nbtNode = source.node(NBT_TAGS);
        final ConfigurationNode enchantsNode = source.node(ENCHANTS);
        final ConfigurationNode itemFlagsNode = source.node(ITEM_FLAGS);
        final ConfigurationNode textureNode = source.node(TEXTURE);
        final ConfigurationNode ownerNode = source.node(OWNER);
        final ConfigurationNode colorNode = source.node(COLOR);
        final ConfigurationNode redNode = colorNode.node(RED);
        final ConfigurationNode greenNode = colorNode.node(GREEN);
        final ConfigurationNode blueNode = colorNode.node(BLUE);

        if (materialNode.virtual()) return null;

        ItemBuilder builder = new ItemBuilder(materialNode.getString("AIR"));
        if (!amountNode.virtual()) builder.setAmount(amountNode.getInt(1));
        if (!nameNode.virtual()) builder.setDisplayName(nameNode.getString(""));
        if (!unbreakableNode.virtual()) builder.setUnbreakable(unbreakableNode.getBoolean());
        if (!glowingNode.virtual()) builder.setGlowing(glowingNode.getBoolean());
        if (!loreNode.virtual()) builder.setLoreUsingStrings(new ArrayList<>(loreNode.getList(String.class, new ArrayList<>())));
        if (!appendLoreNode.virtual()) {
            String loreAppendMode = appendLoreNode.getString("").toUpperCase();
            if (EnumUtils.isValidEnum(LoreAppendMode.class, loreAppendMode)) builder.setLoreAppendMode(LoreAppendMode.valueOf(loreAppendMode));
        }
        if (!modelDataNode.virtual()) builder.setModelData(modelDataNode.getInt());
        if (!nbtNode.virtual()) {
            for (ConfigurationNode nbtNodes : nbtNode.childrenMap().values()) {
                builder.addNBTData(NamespacedKey.minecraft(nbtNodes.key().toString()), nbtNodes.getString());
            }
        }
        if (!enchantsNode.virtual()) {
            for (ConfigurationNode enchantNode : enchantsNode.childrenMap().values()) {
                if (Enchantment.getByKey(NamespacedKey.minecraft(enchantNode.key().toString())) == null) continue;
                builder.addEnchantment(enchantNode.key().toString(), enchantNode.getInt(1));
            }
        }

        try {
            if (!itemFlagsNode.virtual()) {
                for (String itemFlag : itemFlagsNode.getList(String.class)) {
                    builder.addItemFlag(ItemFlag.valueOf(itemFlag));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ownerNode.virtual()) {
            String ownerString = ownerNode.getString();
            if (ownerString.contains("%")) {
                // This means it has PAPI placeholders in it
                builder.addNBTData(InventoryUtils.getSkullOwner(), ownerString);
            }
            builder.setSkullOwner(ownerString);
        }

        if (!textureNode.virtual()) {
            String textureString = textureNode.getString();
            if (textureString.contains("%")) {
                // This means it has PAPI placeholders in it
                builder.addNBTData(InventoryUtils.getSkullTexture(), textureString);
            }
            builder.setTexture(textureString);
        }

        if (!colorNode.virtual()) {
            if (!redNode.virtual()) {
                builder.setColor(Color.fromRGB(redNode.getInt(0), greenNode.getInt(0), blueNode.getInt(0)));
            } else {
                builder.setColor(ServerUtils.hex2Rgb(colorNode.getString("#FFFFFF")));
            }
        }

        return builder;
    }
    @Override
    public void serialize(final Type type, @Nullable final ItemBuilder obj, final ConfigurationNode node) throws SerializationException {
        // Empty
    }
}
