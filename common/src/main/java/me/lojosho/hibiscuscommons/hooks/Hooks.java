package me.lojosho.hibiscuscommons.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.hooks.items.*;
import me.lojosho.hibiscuscommons.hooks.misc.*;
import me.lojosho.hibiscuscommons.hooks.placeholders.HookPlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Hooks {

    private static final HashMap<String, Hook> hooks = new HashMap<>();
    private static final HookOraxen ORAXEN_HOOK = new HookOraxen();
    private static final HookItemAdder ITEMADDER_HOOK = new HookItemAdder();
    private static final HookGeary GEARY_HOOK = new HookGeary();
    private static final HookMythic MYTHIC_HOOK = new HookMythic();
    private static final HookDenizen DENIZEN_HOOK = new HookDenizen();
    private static final HookPremiumVanish PREMIUM_VANISH_HOOK = new HookPremiumVanish();
    private static final HookSuperVanish SUPER_VANISH_HOOK = new HookSuperVanish();
    private static final HookHMCColor HMC_COLOR_HOOK = new HookHMCColor();
    private static final HookCMI CMI_HOOK = new HookCMI();
    private static final HookLibsDisguises LIBS_DISGUISES_HOOK = new HookLibsDisguises();
    private static final HookModelEngine MODEL_ENGINE_HOOK = new HookModelEngine();
    private static final HookMMOItems MMO_ITEMS_HOOK = new HookMMOItems();
    private static final HookEco ECO_ITEMS_HOOK = new HookEco();
    private static final HookPlaceholderAPI PAPI_HOOK = new HookPlaceholderAPI();
    private static final HookCustomFishing CF_HOOK = new HookCustomFishing();


    public static Hook getHook(@NotNull String id) {
        return hooks.get(id.toLowerCase());
    }

    public static boolean isItemHook(@NotNull String id) {
        return hooks.containsKey(id.toLowerCase());
    }

    public static void addHook(Hook hook) {
        hooks.put(hook.getId().toLowerCase(), hook);
    }

    public static void addPlaceholderAPI(PlaceholderExpansion expansion) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            HookPlaceholderAPI hook = (HookPlaceholderAPI) getHook("PlaceholderAPI");
            hook.registerPlaceholder(expansion);
        }
    }

    /**
     * Processing the placeholders using PlaceholderAPI (if possible)
     * @param player The player to process the placeholders for
     * @param raw The string to process
     * @return
     */
    @NotNull
    public static String processPlaceholders(@NotNull OfflinePlayer player, @NotNull String raw) {
        if (isActiveHook("PlaceholderAPI")) return PlaceholderAPI.setPlaceholders(player, raw);
        return raw;
    }

    /**
     * This setups the hooks and registers the events for the hooks. It'll also load the hooks if they are active.
     */
    public static void setup() {
        for (Hook hook : hooks.values()) {
            if (Bukkit.getPluginManager().getPlugin(hook.getId()) != null) {
                HibiscusCommonsPlugin.getInstance().getServer().getPluginManager().registerEvents(hook, HibiscusCommonsPlugin.getInstance());
                hook.setActive(true);
                hook.load();
                HibiscusCommonsPlugin.getInstance().getLogger().info("Successfully hooked into " + hook.getId());
            }
        }
    }

    /**
     * Get the item from the string. Using the format of "plugin:item"
     * @param item The item string to parse
     * @return The itemstack if found, null if not found
     */
    @Nullable
    public static ItemStack getItem(@NotNull String item) {
        if (!item.contains(":")) {
            Material mat = Material.getMaterial(item.toUpperCase());
            if (mat == null) return null;
            return new ItemStack(mat);
        }
        // Ex. Oraxen:BigSword
        // split[0] is the plugin name
        // split[1] is the item name
        String[] split = item.split(":", 2);

        if (!isItemHook(split[0])) return null;
        Hook hook = getHook(split[0]);
        if (!hook.hasEnabledItemHook()) return null;
        if (!hook.isActive()) return null;
        return hook.getItem(split[1]);
    }

    /**
     * Get the string of the item. Do keep in mind that it'll only use hooks that are active. If no plugin responses, it returns the material.
     * @param itemStack
     * @return
     */
    @NotNull
    public static String getStringItem(@NotNull ItemStack itemStack) {
        for (Hook hook : hooks.values()) {
            if (hook.isActive() && hook.hasEnabledItemHook()) {
                String stringyItem = hook.getItemString(itemStack);
                if (stringyItem == null) continue;
                return hook.getId() + ":" + stringyItem;
            }
        }
        return itemStack.getType().toString();
    }

    /**
     * Get the entity from the string. Using the format of "plugin:entity"
     * @param entity
     * @return
     */
    public static String getStringEntity(@NotNull Entity entity) {
        for (Hook hook : hooks.values()) {
            if (hook.isActive() && hook.hasEnabledEntityHook()) {
                String stringyEntity = hook.getEntityString(entity);
                if (stringyEntity != null) return hook.getId() + ":" + stringyEntity;
            }
        }

        return entity.getType().toString().toUpperCase();
    }

    /**
     * Checks if a hook is active. If the hook is not found, it returns false.
     * @param id the plugin id it'll look for. It's case insensitive.
     * @return true if the hook is active, false if not (or is not a valid hook).
     */
    public static boolean isActiveHook(@NotNull String id) {
        Hook hook = getHook(id);
        if (hook == null) return false;
        return hook.isActive();
    }
}
