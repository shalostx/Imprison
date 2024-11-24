package com.shalostx.imprison.events;

import com.shalostx.imprison.database.SQLManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class BlockBreaking implements Listener {

    private final SQLManager sqlManager;

    public BlockBreaking(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        // Получаем игрока
        Player player = event.getPlayer();
        String playerName = player.getName();
        String blockName = event.getBlock().getType().toString();

        // Отменяем ванильное выпадение дропа
        event.setDropItems(false);

        // Получаем ванильный дроп
        ItemStack drops = event.getBlock().getDrops().iterator().next();

        // Получаем предмет в руке

        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();

        // Изначальный бонус в 0

        int bonus = 0;

        // Проверка, есть ли у предмета мета

        if (mainHand.hasItemMeta()) {
            ItemMeta meta = mainHand.getItemMeta();

            // Проверка, есть ли зачарования на предмете

            if (meta.hasEnchants()) {

                // Получаем зачарования предмета
                Map<Enchantment, Integer> enchants = meta.getEnchants();
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {

                    if (entry.getKey().equals(Enchantment.FORTUNE)) {
                        int fortuneLevel = entry.getValue(); // Уровень зачарования Удачи

                        if (Math.random() < 0.15) {
                            bonus = 1;
                        }
                    }
                    if (entry.getKey().equals(Enchantment.SILK_TOUCH)) {
                        // Если есть щелк, то заменяем drops на материал
                        drops = new ItemStack(event.getBlock().getType());
                    }
                }

            }
        }

        drops.setAmount(drops.getAmount() + bonus);
        player.getInventory().addItem(drops);
        ItemStack finalDrops = drops;

        sqlManager.updateCache(playerName, blockName, finalDrops.getAmount());
    }
}

