package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.SimpleTextInput;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import net.ess3.api.events.KitClaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
            sender.getPlayer().openInventory(getKitInventory(kit, sender));
        }
    }

    public Inventory getKitInventory(Kit kit, CommandSource user) {
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

        /*inv.setItem(0, spacerItem);
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
        inv.setItem(25, spacerItem);

        inv.setItem(27, spacerItem);
        inv.setItem(35, spacerItem);

        inv.setItem(36, spacerItem);
        inv.setItem(44, spacerItem);*/

        inv.setItem(45, spacerItem);
        inv.setItem(46, spacerItem);
        inv.setItem(47, spacerItem);

        inv.setItem(48, confirmItem);

        inv.setItem(49, spacerItem);

        inv.setItem(50, cancelItem);

        inv.setItem(51, spacerItem);
        inv.setItem(52, spacerItem);
        inv.setItem(53, spacerItem);

        //Add Items here
        try {
            addKitItems(inv, user, kit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inv;
    }

    public void addKitItems(Inventory inv, final CommandSource user, final Kit kit) throws Exception {
        try {

            final List<String> items = kit.getItems();
            final IText input = new SimpleTextInput(items);
            final IText output = new KeywordReplacer(input, user, ess, true, true);

            final KitClaimEvent event = new KitClaimEvent(user.getUser(ess), kit);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments();
            final boolean currencyIsSuffix = ess.getSettings().isCurrencySymbolSuffixed();
            final List<ItemStack> itemList = new ArrayList<>();
            final List<String> commandQueue = new ArrayList<>();
            final List<String> moneyQueue = new ArrayList<>();
            for (final String kitItem : output.getLines()) {
                if (!currencyIsSuffix ? kitItem.startsWith(ess.getSettings().getCurrencySymbol()) : kitItem.endsWith(ess.getSettings().getCurrencySymbol())) {
                    moneyQueue.add(NumberUtil.sanitizeCurrencyString(kitItem, ess));
                    continue;
                }

                if (kitItem.startsWith("/")) {
                    commandQueue.add(kitItem);
                    continue;
                }

                final String[] parts = kitItem.split(" +");
                final ItemStack parseStack = ess.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);

                if (parseStack.getType() == Material.AIR) {
                    continue;
                }

                final MetaItemStack metaStack = new MetaItemStack(parseStack);

                if (parts.length > 2) {
                    // We pass a null sender here because kits should not do perm checks
                    metaStack.parseStringMeta(null, allowUnsafe, parts, 2, ess);
                }

                itemList.add(metaStack.getItemStack());
            }

            for(ItemStack item : itemList) {
                inv.addItem(item);
            }

            final ArrayList<ItemStack> specialItems = new ArrayList<>();

            for(String string : moneyQueue) {
                final ItemStack moneyItem = new ItemStack(Material.PAPER, 1);
                final ItemMeta moneyMeta = moneyItem.getItemMeta();
                moneyMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Money Item");
                final ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + string);
                moneyMeta.setLore(lore);
                moneyItem.setItemMeta(moneyMeta);
                specialItems.add(moneyItem);
            }

            for(String string : commandQueue) {
                final ItemStack commandItem = new ItemStack(Material.BOOK, 1);
                final ItemMeta commandMeta = commandItem.getItemMeta();
                commandMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Command Item");
                final ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + string);
                commandMeta.setLore(lore);
                commandItem.setItemMeta(commandMeta);
                specialItems.add(commandItem);
            }

            int slot = 36;
            for(ItemStack item : specialItems) {
                if(slot > 44) break;
                if(inv.getItem(slot) != null) {
                    slot++;
                    break;
                }
                System.out.println(slot + " made it: " + item.toString());
                inv.setItem(slot, item);
                slot++;
            }

            /*
            final Map<Integer, ItemStack> overfilled;
            final boolean allowOversizedStacks = user.getUser(ess).isAuthorized("essentials.oversizedstacks");
            final boolean isDropItemsIfFull = ess.getSettings().isDropItemsIfFull();
            if (isDropItemsIfFull) {
                if (allowOversizedStacks) {
                    assert user.getPlayer() != null;
                    overfilled = InventoryWorkaround.addOversizedItems(user.getPlayer().getInventory(), ess.getSettings().getOversizedStackSize(), itemList.toArray(new ItemStack[0]));
                } else {
                    assert user.getPlayer() != null;
                    overfilled = InventoryWorkaround.addItems(user.getPlayer().getInventory(), itemList.toArray(new ItemStack[0]));
                }
                for (final ItemStack itemStack : overfilled.values()) {
                    int spillAmount = itemStack.getAmount();
                    if (!allowOversizedStacks) {
                        itemStack.setAmount(Math.min(spillAmount, itemStack.getMaxStackSize()));
                    }
                    while (spillAmount > 0) {
                        user.getWorld().dropItemNaturally(user.getLocation(), itemStack);
                        spillAmount -= itemStack.getAmount();
                    }
                    spew = true;
                }
            } else {
                if (allowOversizedStacks) {
                    overfilled = InventoryWorkaround.addAllOversizedItems(user.getBase().getInventory(), ess.getSettings().getOversizedStackSize(), itemList.toArray(new ItemStack[0]));
                } else {
                    overfilled = InventoryWorkaround.addAllItems(user.getBase().getInventory(), itemList.toArray(new ItemStack[0]));
                }
                if (overfilled != null) {
                    user.sendMessage(tl("kitInvFullNoDrop"));
                    return false;
                }
            }
            user.getBase().updateInventory();

            // Process money & command queues
            // Done after all items have been processed so commands are not run and money is not given if
            // an error occurs during the item giving process
            for (final String valueString : moneyQueue) {
                final BigDecimal value = new BigDecimal(valueString.trim());
                final Trade t = new Trade(value, ess);
                t.pay(user, Trade.OverflowType.DROP);
            }

            for (final String cmd : commandQueue) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

            if (spew) {
                user.sendMessage(tl("kitInvFull"));
            }*/
        } catch (final Exception e) {
            user.getPlayer().updateInventory();
            ess.getLogger().log(Level.WARNING, e.getMessage());
            throw new Exception(tl("kitError2"), e);
        }

    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList(ess.getKits().getKits().getKeys(false));
        }
        return Collections.emptyList();
    }
}
