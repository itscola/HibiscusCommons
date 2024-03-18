package me.lojosho.hibiscuscommons.config.serializer;

import me.lojosho.hibiscuscommons.items.ItemBuilder;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;


public class SerializerManager {

    /**
     * @deprecated Use {@link #getItemBuilderDeserializer()} instead
     */
    @Deprecated (since = "0.2.7", forRemoval = true)
    public static ItemSerializer getItemSerializer() {
        return ItemSerializer.INSTANCE;
    }

    public static ItemBuilderSerializer getItemBuilderDeserializer() {
        return ItemBuilderSerializer.INSTANCE;
    }

    public static ItemBuilder deserializeItemBuilder(ConfigurationNode source) throws SerializationException {
        return ItemBuilderSerializer.INSTANCE.deserialize(ItemBuilder.class, source);
    }

    public static Location deserializeLocation(ConfigurationNode source) throws SerializationException {
        return LocationSerializer.INSTANCE.deserialize(Location.class, source);
    }
}
