package org.mineTest

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ChatListener(private val plugin: JavaPlugin, private val shopManager: ShopManager, private val menuManager: MenuManager) : Listener {
    private val waitingForInput = mutableMapOf<UUID, InputType>()

    enum class InputType {
        SHOP_NAME,
        SHOP_DESCRIPTION
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val inputType = waitingForInput[player.uniqueId] ?: return

        event.isCancelled = true
        waitingForInput.remove(player.uniqueId)

        when (inputType) {
            InputType.SHOP_NAME -> {
                if (event.message.length > 32) {
                    player.sendMessage("${ChatColor.RED}O nome da loja não pode ter mais de 32 caracteres!")
                    return
                }
                val shopData = mutableMapOf("name" to event.message)
                waitingForInput[player.uniqueId] = InputType.SHOP_DESCRIPTION
                player.sendMessage("${ChatColor.YELLOW}Digite a descrição da sua loja no chat:")
            }
            InputType.SHOP_DESCRIPTION -> {
                if (event.message.length > 128) {
                    player.sendMessage("${ChatColor.RED}A descrição não pode ter mais de 128 caracteres!")
                    return
                }
                val shopData = mutableMapOf("description" to event.message)
                if (shopManager.createShop(player, shopData["name"]!!, event.message)) {
                    player.sendMessage("${ChatColor.GREEN}Sua loja foi criada com sucesso!")
                } else {
                    player.sendMessage("${ChatColor.RED}Erro ao criar sua loja!")
                }
            }
        }
    }

    fun waitForInput(player: Player, type: InputType) {
        waitingForInput[player.uniqueId] = type
    }
} 