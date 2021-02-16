package io.github.bilektugrul.simpleservertools.stuff;

import me.despical.commonsbox.ReflectionUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;


// Made by Despical
public class ActionBar {

    private static final boolean SIXTEEN;
    private static final boolean SPIGOT;
    private static final MethodHandle CHAT_COMPONENT_TEXT;
    private static final MethodHandle PACKET_PLAY_OUT_CHAT;
    private static final Object CHAT_MESSAGE_TYPE;

    static {
        boolean exists = false;
        if (Material.getMaterial("KNOWLEDGE_BOOK") != null) {
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                exists = true;
            } catch (ClassNotFoundException ignored) {
            }
        }
        SPIGOT = exists;
    }

    static {
        boolean sixteen;
        try {
            Class.forName("org.bukkit.entity.Zoglin");
            sixteen = true;
        } catch (ClassNotFoundException ignored) {
            sixteen = false;
        }

        SIXTEEN = sixteen;
    }

    static {
        MethodHandle packet = null;
        MethodHandle chatComp = null;
        Object chatMsgType = null;

        if (!SPIGOT) {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> packetPlayOutChatClass = ReflectionUtils.getNMSClass("PacketPlayOutChat");
            Class<?> iChatBaseComponentClass = ReflectionUtils.getNMSClass("IChatBaseComponent");

            try {
                Class<?> chatMessageTypeClass = Class.forName(ReflectionUtils.NMS + "ChatMessageType");

                MethodType type;
                if (SIXTEEN) type = MethodType.methodType(void.class, iChatBaseComponentClass, chatMessageTypeClass, UUID.class);
                else type = MethodType.methodType(void.class, iChatBaseComponentClass, chatMessageTypeClass);

                for (Object obj : chatMessageTypeClass.getEnumConstants()) {
                    String name = obj.toString();
                    if (name.equals("GAME_INFO") || name.equalsIgnoreCase("ACTION_BAR")) {
                        chatMsgType = obj;
                        break;
                    }
                }

                Class<?> chatComponentTextClass = ReflectionUtils.getNMSClass("ChatComponentText");
                chatComp = lookup.findConstructor(chatComponentTextClass, MethodType.methodType(void.class, String.class));

                packet = lookup.findConstructor(packetPlayOutChatClass, type);
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException ignored) {
                try {
                    chatMsgType = (byte) 2;

                    Class<?> chatComponentTextClass = ReflectionUtils.getNMSClass("ChatComponentText");
                    chatComp = lookup.findConstructor(chatComponentTextClass, MethodType.methodType(void.class, String.class));

                    packet = lookup.findConstructor(packetPlayOutChatClass, MethodType.methodType(void.class, iChatBaseComponentClass, byte.class));
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        CHAT_MESSAGE_TYPE = chatMsgType;
        CHAT_COMPONENT_TEXT = chatComp;
        PACKET_PLAY_OUT_CHAT = packet;
    }

    public static void sendActionBar(Player player, String message) {
        Objects.requireNonNull(player, "Cannot send action bar to null player");
        if (SPIGOT) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            return;
        }

        try {
            Object component = CHAT_COMPONENT_TEXT.invoke(message);
            Object packet;
            if (SIXTEEN) packet = PACKET_PLAY_OUT_CHAT.invoke(component, CHAT_MESSAGE_TYPE, player.getUniqueId());
            else packet = PACKET_PLAY_OUT_CHAT.invoke(component, CHAT_MESSAGE_TYPE);

            ReflectionUtils.sendPacket(player, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void sendPlayersActionBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) sendActionBar(player, message);
    }

    public static void clearActionBar(Player player) {
        sendActionBar(player, " ");
    }

    public static void clearPlayersActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) clearActionBar(player);
    }

    public static void sendActionBarWhile(JavaPlugin plugin, Player player, String message, Callable<Boolean> callable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!callable.call()) {
                        cancel();
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                sendActionBar(player, message);
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }

    public static void sendActionBarWhile(JavaPlugin plugin, Player player, Callable<String> message, Callable<Boolean> callable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!callable.call()) {
                        cancel();
                        return;
                    }
                    sendActionBar(player, message.call());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // Re-sends the messages every 2 seconds so it doesn't go away from the player's screen.
        }.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }

    public static void sendActionBar(JavaPlugin plugin, Player player, String message, long duration) {
        if (duration < 1) return;

        new BukkitRunnable() {
            long repeater = duration;

            @Override
            public void run() {
                sendActionBar(player, message);
                repeater -= 40L;
                if (repeater - 40L < -20L) cancel();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }

}