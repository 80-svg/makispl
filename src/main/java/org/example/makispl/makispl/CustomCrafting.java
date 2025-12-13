package org.example.makispl.makispl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomCrafting {
    Makispl plugin;
    public CustomCrafting(Makispl plugin) {
        this.plugin = plugin;
    }
    public ShapedRecipe emeraldShovel() {
        ItemStack item = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta meta = item.getItemMeta();
//        if (meta.hasCustomName()) {
//            meta.customName(Component.text("[EMERALD SHOVEL]").color(NamedTextColor.GREEN));
//        }
        meta.addEnchant(Enchantment.EFFICIENCY, 2 , true);
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(plugin, "emerald_shovel");
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape(" E ",
                     " S ",
                     " S ");

        recipe.setIngredient('S',Material.STICK);
        recipe.setIngredient('E', Material.EMERALD);

        return recipe;
    }
    public ShapedRecipe emeraldHoe() {
        ItemStack item = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.EFFICIENCY, 2, true);
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(plugin, "emerald_hoe");
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape(" EE",
                     " S ",
                     " S ");

        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('E', Material.EMERALD);

        return recipe;
    }
}
