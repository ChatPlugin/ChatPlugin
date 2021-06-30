package me.remigio07_.chatplugin;

import java.lang.reflect.InvocationTargetException;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public class VelocityEventsAdapter {

	public static void init(ProxyServer proxy, Object plugin) {
		proxy.getEventManager().register(plugin, new VelocityEventsAdapter());
	}
	
	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		JarLibraryLoader.disable();
	}
	
	@Subscribe
	public void onMessage(PluginMessageEvent event) {
		adapt(event);
	}
	
	@Subscribe
	public void onServerPreConnect(ServerPreConnectEvent event) {
		adapt(event);
	}
	
	@Subscribe
	public void onServerConnected(ServerConnectedEvent event) {
		adapt(event);
	}
	
	@Subscribe
	public void onDisconnect(DisconnectEvent event) {
		adapt(event);
	}
	
	@Subscribe
	public void onLogin(PostLoginEvent event) {
		adapt(event);
	}
	
	@Subscribe
	public void onPlayerChat(PlayerChatEvent event) {
		adapt(event);
	}
	
	private static void adapt(Object event) {
		try {
			Class.forName("me.remigio07_.chatplugin.velocity.VelocityEvents", false, JarLibraryLoader.getInstance()).getMethod("on" + event.getClass().getSimpleName().substring(0, event.getClass().getSimpleName().indexOf("Event")), event.getClass()).invoke(null, event);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
