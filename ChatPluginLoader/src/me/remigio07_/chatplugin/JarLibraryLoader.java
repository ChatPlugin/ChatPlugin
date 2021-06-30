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

public class JarLibraryLoader extends URLClassLoader {

	private static JarLibraryLoader instance;
	private static Object plugin, velocityServer, logger;
	
	static {
		registerAsParallelCapable();
	}
	
	public JarLibraryLoader() {
		super(new URL[0], Main.class.getClassLoader());
	}
	
	public void add(URL path) {
		addURL(path);
	}
	
	public static void init(Object... args) {
		try (JarLibraryLoader loader = new JarLibraryLoader()) {
			APIType apiType = Main.getAPIType();
			instance = loader;
			plugin = args[0];
			
			if (apiType == APIType.VELOCITY) {
				velocityServer = args[1];
				logger = args[2];
			} else logger = args[1];
			if (instance.getURLs().length == 0) {
				URL jar = extract();
				
				instance.addURL(jar);
				
				for (String clazz : getClasses(new JarFile(new File(jar.toURI())), apiType.getExcludedClasspaths()))
					loader.loadClass(clazz);
			} Class.forName(apiType.getMainClass(), true, instance).getMethod("onEnable", apiType.getOnEnableTypes()).invoke(null, args);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void disable() {
		try {
			if (Main.isSpigot())
				Class.forName(APIType.SPIGOT.getMainClass(), false, instance).getMethod("onDisable").invoke(null);
			else if (Main.isBungee())
				Class.forName(APIType.BUNGEECORD.getMainClass(), false, instance).getMethod("onDisable").invoke(null);
			else Class.forName(APIType.VELOCITY.getMainClass(), false, instance).getMethod("onDisable").invoke(null);
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
	
	public static Object getVelocityServer() {
		return velocityServer;
	}
	
	public static Object getLogger() {
		return logger;
	}
}
