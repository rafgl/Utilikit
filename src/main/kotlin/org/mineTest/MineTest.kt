package org.mineTest

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.Location
import org.bukkit.ChatColor
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable

class MineTest : JavaPlugin(), Listener {

    private val healthStands = mutableMapOf<Player, ArmorStand>()
    private lateinit var healthDisplay: HealthDisplay
    private lateinit var kitManager: KitManager
    private lateinit var rankManager: RankManager

    override fun onEnable() {
        System.out.printf("onEnable")
        healthDisplay = HealthDisplay(this)
        kitManager = KitManager()
        rankManager = RankManager(this)
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        System.out.printf("onDisable")
        // Remove all health stands when plugin is disabled
        healthStands.values.forEach { it.remove() }
        healthStands.clear()
        healthDisplay.cleanup()
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
        }.runTaskTimer(this, 0L, 1L)
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

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (label.lowercase()) {
            "hello" -> {
                if (sender is Player) {
                    sender.sendMessage("§aHello, world!")
                } else {
                    sender.sendMessage("Comando só pode ser usado no jogo.")
                }
                return true
            }
            "kit" -> {
                if (sender is Player) {
                    kitManager.giveKit(sender)
                } else {
                    sender.sendMessage("§cEste comando só pode ser usado por jogadores!")
                }
                return true
            }
            "rank" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("§cUso: /rank <jogador> <cargo>")
                    sender.sendMessage("§cCargos disponíveis: MASTER, DONO, ADMIN, JOGADOR")
                    return true
                }

                if (args.size != 2) {
                    sender.sendMessage("§cUso: /rank <jogador> <cargo>")
                    return true
                }

                val target = server.getPlayer(args[0])
                if (target == null) {
                    sender.sendMessage("§cJogador não encontrado!")
                    return true
                }

                try {
                    val rank = Rank.valueOf(args[1].uppercase())
                    rankManager.setRank(target, rank)
                    sender.sendMessage("§aCargo de ${target.name} alterado para ${rank.getFormattedName()}")
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage("§cCargo inválido! Cargos disponíveis: MASTER, DONO, ADMIN, JOGADOR")
                }
                return true
            }
        }
        return false
    }
}
