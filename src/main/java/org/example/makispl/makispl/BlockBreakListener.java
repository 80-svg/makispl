package org.example.makispl.makispl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block=e.getBlock();
        ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
        if (tool.containsEnchantment(Enchantment.FORTUNE)){
            tool.removeEnchantment(Enchantment.FORTUNE);
            e.setExpToDrop(6);
        }
//            getLogger().info("Block broken: " + block.getType() + " by " + e.getPlayer().getName() + " at " + block.getLocation());
        switch (block.getType()) {
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                ItemStack ironIngot = new ItemStack(Material.IRON_INGOT, 1);
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(ironIngot);
                e.setExpToDrop(2);
                break;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 1);
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(goldIngot);
                e.setExpToDrop(2);
                break;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                ItemStack copperIngot = new ItemStack(Material.COPPER_INGOT, 1);
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(copperIngot);
                e.setExpToDrop(500);
            default:
                break;
        }
    }
}
