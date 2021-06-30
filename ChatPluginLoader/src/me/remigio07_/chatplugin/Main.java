package me.remigio07_.chatplugin;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

public class Main {

	private static APIType apiType;
	
	public static class Spigot {
		
		public static class MainSpigot extends org.bukkit.plugin.java.JavaPlugin {
			
			private static List<String> excludedClasspaths = Arrays.asList("me.remigio07_.chatplugin.bungee", "me.remigio07_.chatplugin.velocity", "me.remigio07_.chatplugin.discord", "me.remigio07_.chatplugin.misc.integrations");
			
			@Override
			public void onEnable() {
				apiType = APIType.SPIGOT;
				
				JarLibraryLoader.init(this, getLogger());
			}
			
			@Override
			public void onDisable() {
				JarLibraryLoader.disable();
			}
			
			public static List<String> getExcludedClasspaths() {
				return excludedClasspaths;
			}
		}
	}
	
	public static class Bungee {
		
		public static class MainBungee extends net.md_5.bungee.api.plugin.Plugin {
			
			private static List<String> excludedClasspaths = Arrays.asList("me.remigio07_.chatplugin.ChatPlugin", "me.remigio07_.chatplugin.ChatPluginPlayer", "me.remigio07_.chatplugin.misc.SpigotEvents", "me.remigio07_.chatplugin.utils.ProxyUtils", "me.remigio07_.chatplugin.velocity", "me.remigio07_.chatplugin.guis", "me.remigio07_.chatplugin.commands", "me.remigio07_.chatplugin.ads", "me.remigio07_.chatplugin.discord", "me.remigio07_.chatplugin.misc.integrations");
			
			@Override
			public void onEnable() {
				apiType = APIType.BUNGEECORD;
				
				JarLibraryLoader.init(this, getLogger());
			}
			
			@Override
			public void onDisable() {
				JarLibraryLoader.disable();
			}
			
			public static List<String> getExcludedClasspaths() {
				return excludedClasspaths;
			}
		}
	}
	
	public static class Velocity {
		
		@Plugin(
				id = "chatplugin",
				name = "ChatPlugin",
				version = "∞",
				url = "https://megaproserver.com/chatplugin",
				description = "A lightweight yet complete plugin which handles just too many features! Check the wiki for info: https://github.com/Remigio07/ChatPlugin",
				authors = "Remigio07_"
				)
		public static class MainVelocity {

			private static List<String> excludedClasspaths = Arrays.asList("me.remigio07_.chatplugin.ChatPlugin", "me.remigio07_.chatplugin.ChatPluginPlayer", "me.remigio07_.chatplugin.misc.SpigotEvents", "me.remigio07_.chatplugin.utils.ProxyUtils", "me.remigio07_.chatplugin.velocity.VelocityEvents", "me.remigio07_.chatplugin.bungee", "me.remigio07_.chatplugin.guis", "me.remigio07_.chatplugin.commands", "me.remigio07_.chatplugin.ads", "me.remigio07_.chatplugin.discord", "me.remigio07_.chatplugin.misc.integrations");
			private ProxyServer proxy;
			private Logger logger;
			private Path dataFolder;
			
			@Inject
			public MainVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
				apiType = APIType.VELOCITY;
				this.proxy = proxy;
				this.logger = logger;
				this.dataFolder = dataFolder;
			}
			
			@Subscribe
			public void onProxyInitialize(ProxyInitializeEvent event) {
				JarLibraryLoader.init(this, proxy, logger, dataFolder);
				VelocityEventsAdapter.init(proxy, this);
			}
			
			public static List<String> getExcludedClasspaths() {
				return excludedClasspaths;
			}
		}
	}
	
	public static APIType getAPIType() {
		return apiType;
	}
	
	public static boolean isSpigot() {
		return apiType == APIType.SPIGOT;
	}
	
	public static boolean isBungee() {
		return apiType == APIType.BUNGEECORD;
	}
	
	public static boolean isVelocity() {
		return apiType == APIType.VELOCITY;
	}
	
	public static boolean isProxy() {
		return apiType.isProxy();
	}
}
