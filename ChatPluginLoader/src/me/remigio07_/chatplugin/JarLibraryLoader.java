package me.remigio07_.chatplugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;

import me.remigio07_.chatplugin.Main.Bungee.MainBungee;
import me.remigio07_.chatplugin.Main.Spigot.MainSpigot;
import net.md_5.bungee.api.plugin.Plugin;

public class JarLibraryLoader extends URLClassLoader {

	private static JarLibraryLoader instance;
	private static Object plugin;
	private static boolean spigot;
	
	static {
		registerAsParallelCapable();
	}
	
	public JarLibraryLoader() {
		super(new URL[0] , Main.class.getClassLoader());
	}
	
	public void add(URL path) {
		addURL(path);
	}
	
	public static void init(Object plugin) {
		try (JarLibraryLoader loader = new JarLibraryLoader()) {
			JarLibraryLoader.plugin = plugin;
			instance = loader;
			spigot = plugin.getClass().getSimpleName().equals("MainSpigot");
			
			if (instance.getURLs().length == 0) {
				URL jar = extract();
				
				instance.addURL(jar);
				
				for (String clazz : getClasses(new JarFile(new File(jar.toURI())), spigot ? MainSpigot.getExcludedClasspaths() : MainBungee.getExcludedClasspaths()))
					loader.loadClass(clazz);
			} if (spigot)
				Class.forName("me.remigio07_.chatplugin.ChatPlugin", true, instance).getMethod("onEnable", JavaPlugin.class).invoke(null, plugin);
			else Class.forName("me.remigio07_.chatplugin.bungee.ChatPluginBungee", true, instance).getMethod("onEnable", Plugin.class).invoke(null, plugin);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void disable() {
		try {
			if (spigot)
				Class.forName("me.remigio07_.chatplugin.ChatPlugin", false, instance).getMethod("onDisable").invoke(null);
			else Class.forName("me.remigio07_.chatplugin.bungee.ChatPluginBungee", false, instance).getMethod("onDisable").invoke(null);
			plugin = null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static URL extract() {
		try (InputStream input = Main.class.getClassLoader().getResource("ChatPlugin.jar").openStream()) {
			Path path = Files.createTempFile("ChatPlugin", ".jar.tmp");
			
			path.toFile().deleteOnExit();
			Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
			return path.toUri().toURL();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void load(File target, String clazz) {
		try {
			instance.add(target.toURI().toURL());
			
			for (String clazz2 : getClasses(new JarFile(target), null)) {
				try {
					Class.forName(clazz2, false, instance);
				} catch (ClassNotFoundException | NoClassDefFoundError e) {
					// no problem: they will be loaded if needed
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getClasses(JarFile jar, List<String> excludedClasspaths) {
		List<String> classes = new ArrayList<>();
		Enumeration<JarEntry> entries = jar.entries();
		String entry;
		
		while (entries.hasMoreElements() && (entry = entries.nextElement().getName()) != null)
			if (entry.endsWith(".class") && !entry.startsWith("META-INF") && !entry.equals("module-info.class") && (excludedClasspaths == null || isIncluded(entry, excludedClasspaths)))
				classes.add(entry.substring(0, entry.lastIndexOf('.')).replace('/', '.'));
		return classes;
	}
	
	public static boolean isIncluded(String entryName, List<String> excludedClasspaths) {
		for (String excludedClasspath : excludedClasspaths)
			if (entryName.replace('/', '.').startsWith(excludedClasspath))
				return false;
		return true;
	}
	
	public static JarLibraryLoader getInstance() {
		return instance;
	}
	
	public static Object getPlugin() {
		return plugin;
	}
	
	public static boolean isSpigot() {
		return spigot;
	}
}
