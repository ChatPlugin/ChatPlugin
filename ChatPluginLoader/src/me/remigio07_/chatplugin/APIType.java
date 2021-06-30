package me.remigio07_.chatplugin;

import java.util.List;

import me.remigio07_.chatplugin.Main.Bungee.MainBungee;
import me.remigio07_.chatplugin.Main.Spigot.MainSpigot;
import me.remigio07_.chatplugin.Main.Velocity.MainVelocity;

public enum APIType {
	
	SPIGOT("Spigot", "me.remigio07_.chatplugin.ChatPlugin", new String[] { "org.bukkit.plugin.java.JavaPlugin", "java.util.logging.Logger" }),
	BUNGEECORD("BungeeCord", "me.remigio07_.chatplugin.bungee.ChatPluginBungee", new String[] { "net.md_5.bungee.api.plugin.Plugin", "java.util.logging.Logger" }),
	VELOCITY("Velocity", "me.remigio07_.chatplugin.velocity.ChatPluginVelocity", new String[] { "java.lang.Object", "com.velocitypowered.api.proxy.ProxyServer", "org.slf4j.Logger", "java.nio.file.Path" });

	private String name, mainClass;
	private String[] onEnableTypes;
	
	private APIType(String name, String mainClass, String[] onEnableTypes) {
		this.name = name;
		this.mainClass = mainClass;
		this.onEnableTypes = onEnableTypes;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMainClass() {
		return mainClass;
	}
	
	public Class<?>[] getOnEnableTypes() {
		Class<?>[] classes = new Class<?>[onEnableTypes.length];
		
		try {
			for (int i = 0; i < onEnableTypes.length; i++)
				classes[i] = Class.forName(onEnableTypes[i]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} return classes;
	}
	
	public List<String> getExcludedClasspaths() {
		switch (this) {
		case SPIGOT:
			return MainSpigot.getExcludedClasspaths();
		case BUNGEECORD:
			return MainBungee.getExcludedClasspaths();
		case VELOCITY:
			return MainVelocity.getExcludedClasspaths();
		} return null;
	}
	
	public boolean isProxy() {
		return this != SPIGOT;
	}
}