package me.remigio07.chatplugin.mixin;

import java.lang.reflect.InvocationTargetException;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class Utils { // hacky (temporary?) solution to register chat event on 1.19
	
	public static void registerAllowChatMessage() {
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, typeKey) -> {
			try {
				return (boolean) Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricChatManager", false, JARLibraryLoader.getInstance()).getMethod("allowChatMessage", SignedMessage.class, ServerPlayerEntity.class).invoke(ChatManager.getInstance(), message.raw(), sender);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return true;
			}
		});
	}
	
}
