package com.earth2me.essentials;

import com.earth2me.essentials.utils.DateUtil;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.ess3.api.IEssentials;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.earth2me.essentials.I18n.tl;

public class EditKitInventoryListener implements Listener {
    private final transient IEssentials ess;
    private static final String PASTE_URL = "https://hastebin.com/";
    private static final String PASTE_UPLOAD_URL = PASTE_URL + "documents";
    private static final Gson GSON = new Gson();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final YamlConstructor yamlConstructor = new YamlConstructor();

    public EditKitInventoryListener(final IEssentials ess) {
        this.ess = ess;
    }

    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent event) {
        if(event.getClickedInventory() == null || !ess.getKits().getKitEditGUIS().contains(event.getClickedInventory())) {
            return;
        }

        if(event.getSlot() >= 45 && !(event.getSlot() == 48 ||event.getSlot() == 50)){
            event.setCancelled(true);
            return;
        }
        final String kitName = event.getView().getTitle().replace(tl("editkitGUITitle").replace("{0}", ""), "");
        final long delay = (int) ess.getKits().getKit(kitName).get("delay");
        final User user = ess.getUser(event.getWhoClicked().getUniqueId());
        if(event.getSlot() == 50) {
            //Cancelled editing!
            ess.getKits().removeKitEditGUI(event.getClickedInventory());
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(tl("cancelKitEdit"));
            return;
        } else if(event.getSlot() != 48) return;
        event.setCancelled(true);
        //Save kit here

        final ArrayList<ItemStack> contents = new ArrayList<>();
        final ItemStack[] invItems = event.getClickedInventory().getStorageContents();
        for(ItemStack item : invItems) {
            if(item != null) {
                if(item.getType() == Material.AIR) break;
                if(item.hasItemMeta() && item.getItemMeta() != null) {
                    final String itemName = item.getItemMeta().getDisplayName();
                    if(itemName == null) {
                        contents.add(item);
                    } else if(!((itemName.equalsIgnoreCase(tl("kitEditSpacerItemName", kitName)))
                            || (itemName.equalsIgnoreCase(tl("confirmKitEditItemName", kitName)))
                            || (itemName.equalsIgnoreCase(tl("cancelKitEditItemName", kitName))))) {
                        contents.add(item);
                    }
                } else {
                    contents.add(item);
                }
            }
        }
        //Working up to this point!



        /*if (event.getView().getTitle().startsWith(ChatColor.GREEN + "" + ChatColor.BOLD + "Editing Kit " +
                ChatColor.DARK_AQUA + "" + ChatColor.BOLD) && Objects.requireNonNull(event.getClickedInventory()).getHolder() == null) {
            final String kitName = event.getView().getTitle().replace(ChatColor.GREEN + "" + ChatColor.BOLD + "Editing Kit " +
                    ChatColor.DARK_AQUA + "" + ChatColor.BOLD, "");
            final long delay = (int) ess.getKits().getKit(kitName).get("delay");
            final User user = ess.getUser(event.getWhoClicked().getUniqueId());
            if(event.getSlot() >= 45) {
                event.setCancelled(true);
                if(event.getSlot() == 48) {

                    final ArrayList<ItemStack> contents = new ArrayList<>();
                    System.out.println(Arrays.toString(event.getClickedInventory().getStorageContents()));
                    for(ItemStack item : event.getClickedInventory().getStorageContents()) {
                        if(item != null) {
                            if(item.hasItemMeta() && item.getItemMeta() != null) {
                                final String itemName = item.getItemMeta().getDisplayName();
                                if(!((itemName.equalsIgnoreCase(tl("kitEditSpacerItemName", kitName)))
                                        || (itemName.equalsIgnoreCase(tl("confirmKitEditItemName", kitName)))
                                        || (itemName.equalsIgnoreCase("cancelKitEditItemName", kitName)))) {

                                    contents.add(item);
                                }
                            } else {
                                contents.add(item);
                            }
                        }

                        /*if(item.getItemMeta() == null) {
                            //System.out.println("item.getitemmeta: " + item.getItemMeta());
                            contents.add(item);
                            break;
                        }
                        final String itemName = item.getItemMeta().getDisplayName();
                        if(itemName == null) {
                            contents.add(item);
                            break;
                        }

                        if(!((itemName.equalsIgnoreCase(tl("kitEditSpacerItemName", kitName)))
                                || (itemName.equalsIgnoreCase(tl("confirmKitEditItemName")))
                                || (itemName.equalsIgnoreCase("cancelKitEditItemName")))) {
                            //System.out.println("contents: " + item);
                            contents.add(item);
                        }

                        //System.out.println("leftover item: " + item);
                    }

                    System.out.println("\n\n\n\n\n"+contents);
                    ess.getKits().removeKit(kitName);
                    //ess.getKits().addKit(kitName, (List<String>) kit.get("items"), (int) kit.get("delay"));

                    final List<String> list = new ArrayList<>();
                    for (final ItemStack is : contents) {
                        if (is != null && is.getType() != Material.AIR) {
                            final String serialized = ess.getItemDb().serialize(is);
                            list.add(serialized);
                        }
                    }
                    // Some users might want to directly write to config knowing the consequences. *shrug*
                    if (!ess.getSettings().isPastebinCreateKit()) {
                        System.out.println("\n\n\nkitname: " + kitName + "\nlist: " + list + "\ndelay: " + delay + "\n\n");
                        ess.getKits().addKit(kitName, list, delay);
                        event.getWhoClicked().sendMessage(tl("updateKit", kitName));
                        event.getWhoClicked().closeInventory();
                    } else {
                        final ConfigurationSection config = new MemoryConfiguration();
                        config.set("kits." + kitName + ".delay", delay);
                        config.set("kits." + kitName + ".items", list);

                        final Yaml yaml = new Yaml(yamlConstructor, yamlRepresenter, yamlOptions);
                        String fileContents = "# Copy the kit code below into the kits section in your config.yml file\n";
                        fileContents += yaml.dump(config.getValues(false));

                        uploadPaste(user.getSource(), kitName, delay, fileContents);
                        event.getWhoClicked().sendMessage(tl("updateKit", kitName));
                        event.getWhoClicked().closeInventory();
                    }

                } else if(event.getSlot() == 50) {
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(tl("cancelKitEdit"));
                }

            }
        }*/
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryInteractEvent e) {
        e.getWhoClicked().sendMessage(tl("cancelKitEdit"));
    }

    private void uploadPaste(final CommandSource sender, final String kitName, final long delay, final String contents) {
        executorService.submit(() -> {
            try {
                final HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_UPLOAD_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "EssentialsX plugin");
                try (final OutputStream os = connection.getOutputStream()) {
                    os.write(contents.getBytes(Charsets.UTF_8));
                }
                // Error
                if (connection.getResponseCode() >= 400) {
                    sender.sendMessage(tl("createKitFailed", kitName));
                    final String message = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
                    ess.getLogger().severe("Error creating kit: " + message);
                    return;
                }

                // Read URL
                final JsonObject object = GSON.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8), JsonObject.class);
                final String pasteUrl = PASTE_URL + object.get("key").getAsString();
                connection.disconnect();

                final String separator = tl("createKitSeparator");
                String delayFormat = "0";
                if (delay > 0) {
                    delayFormat = DateUtil.formatDateDiff(System.currentTimeMillis() + (delay * 1000));
                }
                sender.sendMessage(separator);
                sender.sendMessage(tl("createKitSuccess", kitName, delayFormat, pasteUrl));
                sender.sendMessage(separator);
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().info(sender.getSender().getName() + " created a kit: " + pasteUrl);
                }
            } catch (final Exception e) {
                sender.sendMessage(tl("createKitFailed", kitName));
                e.printStackTrace();
            }
        });
    }
}
