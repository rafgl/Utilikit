package org.mineTest

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class AtlasLojas : JavaPlugin() {
    private lateinit var shopManager: ShopManager
    private lateinit var menuManager: MenuManager
    private lateinit var chatListener: ChatListener
    private lateinit var inventoryListener: InventoryListener

    override fun onEnable() {
        // Initialize components
        shopManager = ShopManager(this)
        menuManager = MenuManager(this, shopManager)
        chatListener = ChatListener(this, shopManager, menuManager)
        inventoryListener = InventoryListener(this, shopManager, menuManager, chatListener)

        // Register command
        getCommand("loja")?.setExecutor(this)

        logger.info("${ChatColor.GREEN}AtlasLojas foi ativado!")
    }

    override fun onDisable() {
        logger.info("${ChatColor.RED}AtlasLojas foi desativado!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("loja", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("${ChatColor.RED}Este comando só pode ser usado por jogadores!")
                return true
            }

            when (args.size) {
                0 -> menuManager.openMainMenu(sender)
                1 -> {
                    val targetPlayer = Bukkit.getPlayer(args[0])
                    if (targetPlayer != null) {
                        val shop = shopManager.getPlayerShop(targetPlayer.uniqueId)
                        if (shop != null) {
                            menuManager.openShopMenu(sender, shop)
                        } else {
                            sender.sendMessage("${ChatColor.RED}Este jogador não possui uma loja!")
                        }
                    } else {
                        sender.sendMessage("${ChatColor.RED}Jogador não encontrado!")
                    }
                }
                else -> sender.sendMessage("${ChatColor.RED}Uso: /loja [jogador]")
            }
            return true
        }
        return false
    }
} 