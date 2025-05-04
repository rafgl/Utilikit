package org.mineTest

import org.bukkit.entity.Player
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.ChatColor
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.plugin.java.JavaPlugin

class HealthDisplay(private val plugin: JavaPlugin) : Listener {
    private val healthStands = mutableMapOf<Player, ArmorStand>()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        createHealthStand(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        healthStands[player]?.remove()
        healthStands.remove(player)
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            updateHealthDisplay(event.entity as Player)
        }
    }

    @EventHandler
    fun onEntityRegainHealth(event: EntityRegainHealthEvent) {
        if (event.entity is Player) {
            updateHealthDisplay(event.entity as Player)
        }
    }

    private fun createHealthStand(player: Player) {
        val location = player.location.clone().add(0.0, 2.5, 0.0)
        val stand = player.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
        
        stand.apply {
            isVisible = false
            isSmall = true
            isMarker = true
            isInvulnerable = true
            setGravity(false)
            customName = getHealthDisplay(player.health)
            isCustomNameVisible = true
        }
        
        healthStands[player] = stand
        
        // Update health display every tick
        object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    return
                }
                updateHealthDisplay(player)
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    private fun updateHealthDisplay(player: Player) {
        val stand = healthStands[player] ?: return
        stand.customName = getHealthDisplay(player.health)
        stand.teleport(player.location.clone().add(0.0, 2.5, 0.0))
    }

    private fun getHealthDisplay(health: Double): String {
        val color = when {
            health >= 15 -> ChatColor.GREEN
            health >= 10 -> ChatColor.YELLOW
            health >= 5 -> ChatColor.GOLD
            else -> ChatColor.RED
        }
        
        return "$color❤ ${ChatColor.WHITE}${health.toInt()}"
    }

    fun cleanup() {
        healthStands.values.forEach { it.remove() }
        healthStands.clear()
    }
} 