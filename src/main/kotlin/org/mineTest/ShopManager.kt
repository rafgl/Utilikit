package org.mineTest

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

data class Shop(
    val ownerId: UUID,
    val name: String,
    val description: String,
    val isVip: Boolean,
    val creationDate: Date,
    val ratings: MutableMap<UUID, Int>,
    val announcement: String? = null
) {
    fun getAverageRating(): Double {
        if (ratings.isEmpty()) return 0.0
        return ratings.values.average()
    }
}

class ShopManager(private val plugin: JavaPlugin) {
    private val playerShops = mutableMapOf<UUID, Shop>()
    private val config = plugin.config

    init {
        loadShops()
    }

    fun createShop(player: Player, name: String, description: String): Boolean {
        if (playerShops.containsKey(player.uniqueId)) {
            player.sendMessage("${ChatColor.RED}Você já possui uma loja!")
            return false
        }

        val isVip = player.hasPermission("atlaslojas.vip")
        val shop = Shop(
            ownerId = player.uniqueId,
            name = name,
            description = description,
            isVip = isVip,
            creationDate = Date(),
            ratings = mutableMapOf()
        )

        playerShops[player.uniqueId] = shop
        saveShops()
        player.sendMessage("${ChatColor.GREEN}Sua loja foi criada com sucesso!")
        return true
    }

    fun getPlayerShop(playerId: UUID): Shop? {
        return playerShops[playerId]
    }

    fun rateShop(raterId: UUID, shopOwnerId: UUID, rating: Int): Boolean {
        val shop = playerShops[shopOwnerId] ?: return false
        if (raterId == shopOwnerId) return false
        if (rating !in 1..5) return false

        shop.ratings[raterId] = rating
        saveShops()
        return true
    }

    fun setAnnouncement(player: Player, announcement: String): Boolean {
        val shop = playerShops[player.uniqueId] ?: return false
        if (!player.hasPermission("atlaslojas.vip")) {
            player.sendMessage("${ChatColor.RED}Apenas VIPs podem definir anúncios!")
            return false
        }

        playerShops[player.uniqueId] = shop.copy(announcement = announcement)
        saveShops()
        player.sendMessage("${ChatColor.GREEN}Anúncio atualizado com sucesso!")
        return true
    }

    fun getAllShops(): List<Shop> {
        return playerShops.values.toList()
    }

    fun getShopsByRating(): List<Shop> {
        return playerShops.values.sortedByDescending { it.getAverageRating() }
    }

    fun getShopsByDate(): List<Shop> {
        return playerShops.values.sortedByDescending { it.creationDate }
    }

    fun getVipShops(): List<Shop> {
        return playerShops.values.filter { it.isVip }
    }

    private fun saveShops() {
        // TODO: Implement shop saving to config/database
    }

    private fun loadShops() {
        // TODO: Implement shop loading from config/database
    }
} 