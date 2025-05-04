package org.mineTest

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class InventoryListener(
    private val plugin: JavaPlugin,
    private val shopManager: ShopManager,
    private val menuManager: MenuManager,
    private val chatListener: ChatListener
) : Listener {
    private val creatingShops = mutableMapOf<UUID, MutableMap<String, String>>()
    private val ratingShops = mutableMapOf<UUID, UUID>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        val clickedItem = event.currentItem ?: return
        val inventory = event.clickedInventory ?: return
        val title = event.view.title

        event.isCancelled = true

        when {
            title == "${ChatColor.BLUE}AtlasLojas" -> handleMainMenuClick(player, clickedItem)
            title == "${ChatColor.BLUE}Criar Loja" -> handleCreateShopMenuClick(player, clickedItem)
            title.startsWith("${ChatColor.BLUE}Loja de ") -> handleShopMenuClick(player, clickedItem, title)
            title == "${ChatColor.BLUE}Avaliar Loja" -> handleRatingMenuClick(player, clickedItem)
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        val title = event.view.title

        if (title == "${ChatColor.BLUE}Criar Loja") {
            creatingShops.remove(player.uniqueId)
        }
    }

    private fun handleMainMenuClick(player: Player, item: org.bukkit.inventory.ItemStack) {
        when (item.type) {
            Material.DIAMOND -> {
                val shops = shopManager.getVipShops()
                // TODO: Implement VIP shops list menu
            }
            Material.GOLD_INGOT -> {
                val shops = shopManager.getShopsByRating()
                // TODO: Implement top rated shops list menu
            }
            Material.CLOCK -> {
                val shops = shopManager.getShopsByDate()
                // TODO: Implement newest shops list menu
            }
            Material.BOOK -> {
                val shops = shopManager.getAllShops()
                // TODO: Implement all shops list menu
            }
            Material.ANVIL -> {
                menuManager.openCreateShopMenu(player)
                creatingShops[player.uniqueId] = mutableMapOf()
            }
            else -> return
        }
    }

    private fun handleCreateShopMenuClick(player: Player, item: org.bukkit.inventory.ItemStack) {
        when (item.type) {
            Material.NAME_TAG -> {
                player.closeInventory()
                chatListener.waitForInput(player, ChatListener.InputType.SHOP_NAME)
                player.sendMessage("${ChatColor.YELLOW}Digite o nome da sua loja no chat:")
            }
            Material.BOOK -> {
                player.closeInventory()
                chatListener.waitForInput(player, ChatListener.InputType.SHOP_DESCRIPTION)
                player.sendMessage("${ChatColor.YELLOW}Digite a descrição da sua loja no chat:")
            }
            else -> return
        }
    }

    private fun handleShopMenuClick(player: Player, item: org.bukkit.inventory.ItemStack, title: String) {
        if (item.type == Material.GOLD_INGOT) {
            val ownerName = title.replace("${ChatColor.BLUE}Loja de ", "")
            val owner = Bukkit.getOfflinePlayer(ownerName)
            val shop = shopManager.getPlayerShop(owner.uniqueId) ?: return
            
            ratingShops[player.uniqueId] = owner.uniqueId
            menuManager.openRatingMenu(player, shop)
        }
    }

    private fun handleRatingMenuClick(player: Player, item: org.bukkit.inventory.ItemStack) {
        if (item.type != Material.GOLD_INGOT) return
        
        val shopOwnerId = ratingShops[player.uniqueId] ?: return
        val rating = item.itemMeta?.displayName?.replace("${ChatColor.YELLOW}", "")?.replace(" ⭐", "")?.toIntOrNull() ?: return
        
        if (shopManager.rateShop(player.uniqueId, shopOwnerId, rating)) {
            player.sendMessage("${ChatColor.GREEN}Você avaliou a loja com $rating estrelas!")
        } else {
            player.sendMessage("${ChatColor.RED}Erro ao avaliar a loja!")
        }
        
        player.closeInventory()
        ratingShops.remove(player.uniqueId)
    }
} 