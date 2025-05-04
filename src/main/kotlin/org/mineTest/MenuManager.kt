package org.mineTest

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*

class MenuManager(private val plugin: JavaPlugin, private val shopManager: ShopManager) {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun openMainMenu(player: Player) {
        val inventory = Bukkit.createInventory(null, 54, "${ChatColor.BLUE}AtlasLojas")
        
        // Categorias
        setItem(inventory, 10, Material.DIAMOND, "${ChatColor.AQUA}Lojas VIP", listOf("${ChatColor.GRAY}Clique para ver as lojas VIP"))
        setItem(inventory, 12, Material.GOLD_INGOT, "${ChatColor.GOLD}Melhores Avaliações", listOf("${ChatColor.GRAY}Clique para ver as lojas mais bem avaliadas"))
        setItem(inventory, 14, Material.CLOCK, "${ChatColor.YELLOW}Mais Recentes", listOf("${ChatColor.GRAY}Clique para ver as lojas mais recentes"))
        setItem(inventory, 16, Material.BOOK, "${ChatColor.GREEN}Todas as Lojas", listOf("${ChatColor.GRAY}Clique para ver todas as lojas"))

        // Criar loja
        if (!shopManager.getPlayerShop(player.uniqueId).let { it != null }) {
            setItem(inventory, 31, Material.ANVIL, "${ChatColor.GREEN}Criar Loja", listOf("${ChatColor.GRAY}Clique para criar sua própria loja"))
        }

        player.openInventory(inventory)
    }

    fun openShopMenu(player: Player, shop: Shop) {
        val inventory = Bukkit.createInventory(null, 27, "${ChatColor.BLUE}Loja de ${Bukkit.getOfflinePlayer(shop.ownerId).name}")

        // Informações da loja
        val infoLore = mutableListOf<String>()
        infoLore.add("${ChatColor.GRAY}Descrição: ${ChatColor.WHITE}${shop.description}")
        infoLore.add("${ChatColor.GRAY}Data de criação: ${ChatColor.WHITE}${dateFormat.format(shop.creationDate)}")
        infoLore.add("${ChatColor.GRAY}Avaliação média: ${ChatColor.WHITE}${String.format("%.1f", shop.getAverageRating())} ⭐")
        if (shop.isVip) {
            infoLore.add("${ChatColor.GOLD}⭐ Loja VIP ⭐")
        }
        if (shop.announcement != null) {
            infoLore.add("")
            infoLore.add("${ChatColor.YELLOW}Anúncio:")
            infoLore.add("${ChatColor.WHITE}${shop.announcement}")
        }

        setItem(inventory, 13, Material.CHEST, "${ChatColor.YELLOW}${shop.name}", infoLore)

        // Avaliar loja
        if (player.uniqueId != shop.ownerId) {
            setItem(inventory, 15, Material.GOLD_INGOT, "${ChatColor.GOLD}Avaliar Loja", listOf("${ChatColor.GRAY}Clique para avaliar esta loja"))
        }

        player.openInventory(inventory)
    }

    fun openRatingMenu(player: Player, shop: Shop) {
        val inventory = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}Avaliar Loja")

        for (i in 1..5) {
            setItem(inventory, i + 1, Material.GOLD_INGOT, "${ChatColor.YELLOW}$i ⭐", listOf("${ChatColor.GRAY}Clique para dar $i estrelas"))
        }

        player.openInventory(inventory)
    }

    fun openCreateShopMenu(player: Player) {
        val inventory = Bukkit.createInventory(null, 9, "${ChatColor.BLUE}Criar Loja")
        
        setItem(inventory, 3, Material.NAME_TAG, "${ChatColor.YELLOW}Definir Nome", listOf("${ChatColor.GRAY}Clique para definir o nome da sua loja"))
        setItem(inventory, 5, Material.BOOK, "${ChatColor.YELLOW}Definir Descrição", listOf("${ChatColor.GRAY}Clique para definir a descrição da sua loja"))

        player.openInventory(inventory)
    }

    private fun setItem(inventory: Inventory, slot: Int, material: Material, name: String, lore: List<String>) {
        val item = ItemStack(material)
        val meta = item.itemMeta!!
        meta.setDisplayName(name)
        meta.lore = lore
        item.itemMeta = meta
        inventory.setItem(slot, item)
    }
} 