package me.lojosho.hibiscuscommons.config.serializer;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ItemSerializer implements TypeSerializer<ItemStack> {

    public static final ItemSerializer INSTANCE = new ItemSerializer();

    private ItemSerializer() {}

    @Override @Deprecated
    public ItemStack deserialize(final Type type, final ConfigurationNode source) throws SerializationException {
        return SerializerManager.serializeItemBuilder(source).build();
    }
    @Override
    public void serialize(final Type type, @Nullable final ItemStack obj, final ConfigurationNode node) throws SerializationException {}
}

