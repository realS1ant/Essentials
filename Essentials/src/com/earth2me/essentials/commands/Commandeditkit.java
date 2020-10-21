package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Kit;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandeditkit extends EssentialsCommand{
    public Commandeditkit() {
        super("editkit");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            final String kitList = ess.getKits().listKits(ess, null);
            sender.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
            throw new NoChargeException();
        } else {
            final String kitName = ess.getKits().matchKit(args[0]);
            final Kit kit = new Kit(kitName, ess);

            if (sender.getPlayer() != null) {
                kit.checkPerms(ess.getUser(sender.getPlayer()));
            }

            sender.sendMessage("editing kit here!!");
            sender.getPlayer().openInventory(getKitInventory(kit));
        }
    }

    public Inventory getKitInventory(Kit kit) {
        final Inventory inv = Bukkit.getServer().createInventory(null, 54, ChatColor.GREEN + "" +
                ChatColor.BOLD + "Editing Kit " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + kit.getName());

        ItemStack spacerItem;
        try {
            spacerItem = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1);
        } catch (NoSuchFieldError e) {
            spacerItem = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1);
            spacerItem.setDurability((short)2);
        }

        ItemStack cancelItem;
        try {
            cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        } catch (NoSuchFieldError e) {
            cancelItem = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1);
            cancelItem.setDurability((short)14);
        }
        final ItemMeta cancelItemMeta = cancelItem.getItemMeta();
        cancelItemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Stop Editing");
        cancelItem.setItemMeta(cancelItemMeta);

        ItemStack confirmItem;
        try {
            confirmItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        } catch (NoSuchFieldError e) {
            confirmItem = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1);
            confirmItem.setDurability((short)5);
        }
        final ItemMeta confirmItemMeta = confirmItem.getItemMeta();
        confirmItemMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save Kit");
        confirmItem.setItemMeta(confirmItemMeta);

        inv.setItem(0, spacerItem);
        inv.setItem(1, spacerItem);
        inv.setItem(2, spacerItem);
        inv.setItem(3, spacerItem);
        inv.setItem(4, spacerItem);
        inv.setItem(5, spacerItem);
        inv.setItem(6, spacerItem);
        inv.setItem(7, spacerItem);
        inv.setItem(8, spacerItem);

        inv.setItem(9, spacerItem);
        inv.setItem(17, spacerItem);

        inv.setItem(18, spacerItem);
        inv.setItem(26, spacerItem);

        inv.setItem(27, spacerItem);
        inv.setItem(35, spacerItem);

        inv.setItem(36, spacerItem);
        inv.setItem(44, spacerItem);

        inv.setItem(45, spacerItem);
        inv.setItem(46, spacerItem);
        inv.setItem(47, spacerItem);

        inv.setItem(48, confirmItem);

        inv.setItem(49, spacerItem);

        inv.setItem(50, cancelItem);

        inv.setItem(51, spacerItem);
        inv.setItem(52, spacerItem);
        inv.setItem(53, spacerItem);

        for(ItemStack item : kit.)



        return inv;
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList(ess.getKits().getKits().getKeys(false));
        }
        return Collections.emptyList();
    }
}
