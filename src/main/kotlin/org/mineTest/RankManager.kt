package org.mineTest

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

enum class Rank(
    val displayName: String,
    val color: ChatColor,
    val permission: String
) {
    MASTER("MASTER", ChatColor.DARK_RED, "minetest.rank.master"),
    DONO("DONO", ChatColor.RED, "minetest.rank.dono"),
    ADMIN("ADMIN", ChatColor.GOLD, "minetest.rank.admin"),
    JOGADOR("JOGADOR", ChatColor.GRAY, "minetest.rank.jogador");

    fun getFormattedName(): String {
        return "${ChatColor.BOLD}${color}[$displayName]"
    }

    fun getTabPrefix(): String {
        return "${ChatColor.BOLD}${color}[$displayName]${ChatColor.RESET} "
    }
}

class RankManager(private val plugin: JavaPlugin) : Listener {
    private val playerRanks = mutableMapOf<UUID, Rank>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun setRank(player: Player, rank: Rank) {
        playerRanks[player.uniqueId] = rank
        player.sendMessage("${ChatColor.GREEN}Seu cargo foi alterado para ${rank.getFormattedName()}")
        updatePlayerTabName(player)
    }

    fun getRank(player: Player): Rank {
        return playerRanks.getOrDefault(player.uniqueId, Rank.JOGADOR)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayerTabName(event.player)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val rank = getRank(player)
        
        event.format = "${rank.getFormattedName()} ${ChatColor.WHITE}${player.name}: ${event.message}"
    }

    private fun updatePlayerTabName(player: Player) {
        val rank = getRank(player)
        val tabName = "${rank.getTabPrefix()}${ChatColor.WHITE}${player.name}"
        player.setPlayerListName(tabName)
        player.setDisplayName(tabName)
    }
} 