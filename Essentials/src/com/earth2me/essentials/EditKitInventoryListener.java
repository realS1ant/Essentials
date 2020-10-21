package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class EditKitInventoryListener implements Listener {
    private final transient IEssentials ess;

    public EditKitInventoryListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.GREEN + "" + ChatColor.BOLD + "Editing Kit " +
                ChatColor.DARK_AQUA + "" + ChatColor.BOLD) && Objects.requireNonNull(event.getClickedInventory()).getHolder() == null) {
            event.getWhoClicked().sendMessage("Triggered Click Event!");
            event.setCancelled(true);
        }
    }
}
