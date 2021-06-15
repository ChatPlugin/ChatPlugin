package me.remigio07_.chatplugin;

import java.util.Arrays;
import java.util.List;

public class Main {

	public static class Spigot {
		
		public static class MainSpigot extends org.bukkit.plugin.java.JavaPlugin {
			
			private static List<String> excludedClasspaths = Arrays.asList("me.remigio07_.chatplugin.bungee", "me.remigio07_.chatplugin.discord", "me.remigio07_.chatplugin.misc.integrations");
			
			@Override
			public void onEnable() {
				JarLibraryLoader.init(this);
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
			
			private static List<String> excludedClasspaths = Arrays.asList("me.remigio07_.chatplugin.ChatPlugin", "me.remigio07_.chatplugin.ChatPluginPlayer", "me.remigio07_.chatplugin.misc.SpigotEvents", "me.remigio07_.chatplugin.utils.BungeeUtils", "me.remigio07_.chatplugin.guis", "me.remigio07_.chatplugin.commands", "me.remigio07_.chatplugin.ads", "me.remigio07_.chatplugin.discord", "me.remigio07_.chatplugin.misc.integrations");
			
			@Override
			public void onEnable() {
				JarLibraryLoader.init(this);
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
}
