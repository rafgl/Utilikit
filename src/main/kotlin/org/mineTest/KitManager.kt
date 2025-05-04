package org.mineTest

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class KitManager {
    fun giveKit(player: Player) {
        val inventory = player.inventory
        
        // Clear inventory first
        inventory.clear()
        
        // Give armor
        inventory.helmet = createEnchantedItem(Material.NETHERITE_HELMET)
        inventory.chestplate = createEnchantedItem(Material.NETHERITE_CHESTPLATE)
        inventory.leggings = createEnchantedItem(Material.NETHERITE_LEGGINGS)
        inventory.boots = createEnchantedItem(Material.NETHERITE_BOOTS)
        
        // Give weapons and tools
        inventory.setItem(0, createEnchantedItem(Material.NETHERITE_SWORD))
        inventory.setItem(1, createEnchantedItem(Material.NETHERITE_AXE))
        inventory.setItem(2, createEnchantedItem(Material.NETHERITE_PICKAXE))
        inventory.setItem(3, createEnchantedItem(Material.NETHERITE_SHOVEL))
        
        // Give food
        inventory.setItem(8, ItemStack(Material.GOLDEN_APPLE, 64))
        
        player.sendMessage("§aVocê recebeu o kit completo!")
    }
    
    private fun createEnchantedItem(material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        
        when (material) {
            Material.NETHERITE_SWORD -> {
                meta.addEnchant(Enchantment.SHARPNESS, 5, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
                meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true)
                meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true)
                meta.addEnchant(Enchantment.KNOCKBACK, 2, true)
            }
            Material.NETHERITE_AXE -> {
                meta.addEnchant(Enchantment.SHARPNESS, 5, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
                meta.addEnchant(Enchantment.EFFICIENCY, 5, true)
            }
            Material.NETHERITE_PICKAXE -> {
                meta.addEnchant(Enchantment.EFFICIENCY, 5, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
                meta.addEnchant(Enchantment.FORTUNE, 3, true)
            }
            Material.NETHERITE_SHOVEL -> {
                meta.addEnchant(Enchantment.EFFICIENCY, 5, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
            }
            Material.NETHERITE_HELMET -> {
                meta.addEnchant(Enchantment.PROTECTION, 4, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
                meta.addEnchant(Enchantment.RESPIRATION, 3, true)
                meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true)
            }
            Material.NETHERITE_CHESTPLATE -> {
                meta.addEnchant(Enchantment.PROTECTION, 4, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
            }
            Material.NETHERITE_LEGGINGS -> {
                meta.addEnchant(Enchantment.PROTECTION, 4, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
            }
            Material.NETHERITE_BOOTS -> {
                meta.addEnchant(Enchantment.PROTECTION, 4, true)
                meta.addEnchant(Enchantment.UNBREAKING, 3, true)
                meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true)
            }
            else -> {} // Handle all other materials (no enchantments)
        }
        
        item.itemMeta = meta
        return item
    }
} 