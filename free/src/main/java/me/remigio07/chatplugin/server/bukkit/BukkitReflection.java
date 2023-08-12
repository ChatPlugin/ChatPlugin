/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.primitives.Primitives;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class BukkitReflection {
	
	private static String cbPath, nmsPath;
	private static Map<String, Class<?>> classes = new HashMap<>();
	private static Map<Class<?>, Map<String, Method>> methods = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public static void initReflection() throws ChatPluginManagerException {
		boolean atLeast1_17 = VersionUtils.getVersion().isAtLeast(Version.V1_17);
//		boolean atLeast1_18 = VersionUtils.getVersion().isAtLeast(Version.V1_18);
		Class<?> clazz;
		cbPath = "org.bukkit.craftbukkit." + VersionUtils.getNMSVersion() + ".";
		nmsPath = "net.minecraft.server." + (atLeast1_17 ? "" : VersionUtils.getNMSVersion() + ".");
		
		try {
			// CraftPlayer
			clazz = getCBClass("entity.CraftPlayer");
			classes.put("CraftPlayer", clazz);
			putMethod(clazz, "getHandle", "");
			
			// CraftWorld - < 1.9
			clazz = getCBClass("CraftWorld");
			classes.put("CraftWorld", clazz);
			putMethod(clazz, "getHandle", "");
			
			// MinecraftServer
			clazz = getNMSClass("MinecraftServer");
			classes.put("MinecraftServer", clazz);
			putMethod(clazz, "getServer", "");
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_19)) {
				// ClientboundSystemChatPacket - > 1.18.2
				clazz = getNMNClass("protocol.game.ClientboundSystemChatPacket");
				classes.put("ClientboundSystemChatPacket", clazz);
			} else {
				// ChatComponentText - < 1.19
				clazz = atLeast1_17 ? getNMNClass("chat.ChatComponentText") : getNMSClass("ChatComponentText");
				classes.put("ChatComponentText", clazz);
				
				// PacketPlayOutChat
				clazz = atLeast1_17 ? getNMNClass("protocol.game.PacketPlayOutChat") : getNMSClass("PacketPlayOutChat");
				classes.put("PacketPlayOutChat", clazz);
			}
			
			// IChatBaseComponent
			clazz = atLeast1_17 ? getNMNClass("chat.IChatBaseComponent") : getNMSClass("IChatBaseComponent");
			classes.put("IChatBaseComponent", clazz);
			if (VersionUtils.getVersion().getProtocol() < 341)
				putMethod(clazz, "getText", "");
			else putMethod(clazz, "getString", "");
			
				// ChatSerializer
				clazz = atLeast1_17 ? getNMNClass("chat.IChatBaseComponent$ChatSerializer") : getNMSClass("IChatBaseComponent$ChatSerializer");
				classes.put("ChatSerializer", clazz);
				putMethod(clazz, "a", Arrays.asList(String.class));
			
			// Packet
			clazz = atLeast1_17 ? getNMNClass("protocol.Packet") : getNMSClass("Packet");
			classes.put("Packet", clazz);
			
			// EntityPlayer
			clazz = getNMSClass((atLeast1_17 ? "level." : "") + "EntityPlayer");
			classes.put("EntityPlayer", clazz);
				
				// PlayerConnection
				clazz = getNMSClass((atLeast1_17 ? "network." : "") + "PlayerConnection");
				classes.put("PlayerConnection", clazz);
				putMethod(clazz, "sendPacket", Arrays.asList(classes.get("Packet")), "a");
			
			// PacketPlayOutOpenWindow
			clazz = atLeast1_17 ? Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow") : getNMSClass("PacketPlayOutOpenWindow");
			classes.put("PacketPlayOutOpenWindow", clazz);
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_14)) {
				// Containers
				clazz = atLeast1_17 ? Class.forName("net.minecraft.world.inventory.Containers") : getNMSClass("Containers");
				classes.put("Containers", clazz);
			}
			
			// Container
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.inventory.Container") : getNMSClass("Container");
			classes.put("Container", clazz);
			
			// CraftHumanEntity
			clazz = getCBClass("entity.CraftHumanEntity");
			classes.put("CraftHumanEntity", clazz);
			putMethod(clazz, "getHandle", "");
			
			// EntityHuman
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.player.EntityHuman") : getNMSClass("EntityHuman");
			classes.put("EntityHuman", clazz);
			
			if (VersionUtils.getVersion().getProtocol() > 316) {
				// ChatMessageType
				clazz = atLeast1_17 ? getNMNClass("chat.ChatMessageType") : getNMSClass("ChatMessageType");
				classes.put("ChatMessageType", clazz);
			} if (VersionUtils.getVersion().isOlderThan(Version.V1_9)) {
				// PacketPlayOutTitle
				clazz = getNMSClass("PacketPlayOutTitle");
				classes.put("PacketPlayOutTitle", clazz);
				
					// EnumTitleAction
					clazz = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
					classes.put("EnumTitleAction", clazz);
			} else return;
			
			// PacketPlayOutPlayerListHeaderFooter
			clazz = atLeast1_17 ? getNMNClass("protocol.game.PacketPlayOutPlayerListHeaderFooter") : getNMSClass("PacketPlayOutPlayerListHeaderFooter");
			classes.put("PacketPlayOutPlayerListHeaderFooter", clazz);
			
			// PacketPlayOutSpawnEntityLiving
			clazz = atLeast1_17 ? getNMNClass("protocol.game.PacketPlayOutSpawnEntityLiving") : getNMSClass("PacketPlayOutSpawnEntityLiving");
			classes.put("PacketPlayOutSpawnEntityLiving", clazz);
			
			// PacketPlayOutEntityDestroy
			clazz = atLeast1_17 ? getNMNClass("protocol.game.PacketPlayOutEntityDestroy") : getNMSClass("PacketPlayOutEntityDestroy");
			classes.put("PacketPlayOutEntityDestroy", clazz);
			
			// Entity
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.Entity") : getNMSClass("Entity");
			classes.put("Entity", clazz);
			putMethod(clazz, "getId", "ae");
			putMethod(clazz, "getWorld", "cA");
			putMethod(clazz, "getCustomName", "Z");
			putMethod(clazz, "setCustomName", Arrays.asList(VersionUtils.getVersion().getProtocol() > 340 ? classes.get("IChatBaseComponent") : String.class), "a");
			putMethod(clazz, "setInvisible", Arrays.asList(Boolean.TYPE), "j");
			putMethod(clazz, "setLocation", Arrays.asList(Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE), "a");
			putMethod(clazz, "getDataWatcher", "ai");
			
			// EntityLiving
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.EntityLiving") : getNMSClass("EntityLiving");
			classes.put("EntityLiving", clazz);
			putMethod(clazz, "getHealth", "ea", "dZ");
			putMethod(clazz, "setHealth", Arrays.asList(Float.TYPE), "c");
			
			// EntityWither
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.boss.wither.EntityWither") : getNMSClass("EntityWither");
			classes.put("EntityWither", clazz);
			putMethod(clazz, "setInvulnerableTicks", Arrays.asList(Integer.TYPE), "setInvulnerableTicks", "setInvul", "r", "l", "g", "e", "d", "s");
			
			/*
			 * 1.8		r						ck, cm, cm
			 * 1.9		l						da, db
			 * 1.10		g						df
			 * 1.11		g						di
			 * 1.12		g						dn
			 * 1.13		e, d					dB, dA
			 * 1.14		r						dW
			 * 1.15		s						J_
			 * 1.16		setInvul				T_, S_
			 * 1.17		setInvul				isPowered
			 * 1.18		s, setInvulnerableTicks	a, isPowered
			 */
			
			// EntityTypes
			clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.EntityTypes") : getNMSClass("EntityTypes");
			classes.put("EntityTypes", clazz);
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new ChatPluginManagerException("reflection utils", e);
		}
	}
	
	public static void putMethod(Class<?> clazz, String method, String... otherAttempts) throws NoSuchMethodException {
		putMethod(clazz, method, Collections.emptyList(), otherAttempts);
	}
	
	public static void putMethod(Class<?> clazz, String method, List<Class<?>> parameters, String... otherAttempts) throws NoSuchMethodException {
		Map<String, Method> map = methods.getOrDefault(clazz, new HashMap<>());
		List<String> temp = new ArrayList<>(Arrays.asList(otherAttempts));
		
		temp.add(0, method);
		
		otherAttempts = temp.toArray(new String[0]);
		
		for (String otherAttempt : otherAttempts) {
			try {
				map.put(method, clazz.getMethod(otherAttempt, parameters.toArray(new Class<?>[0])));
				break;
			} catch (NoSuchMethodException e) {
				
			}
		} methods.put(clazz, map);
	}
	
	public static Object invokeMethod(String loadedClass, String method, Object instance, Object... args) {
		try {
			return methods.get(getLoadedClass(loadedClass)).get(method).invoke(instance, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Class<?> getLoadedClass(String loadedClass) {
		return classes.get(loadedClass);
	}
	
	public static Object getInstance(String loadedClass, Object... args) {
		try {
			return getLoadedClass(loadedClass).getConstructor(objectsToTypes(args)).newInstance(args);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Object getInstance(String loadedClass, Class<?>[] types, Object... args) {
		try {
			return getLoadedClass(loadedClass).getConstructor(types).newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Class<?>[] objectsToTypes(Object... args) throws ClassNotFoundException {
		Class<?>[] array = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			if (Primitives.isWrapperType(args[i].getClass())) {
				array[i] = Primitives.unwrap(args[i].getClass());
				continue;
			} array[i] = args[i].getClass();
		} return array;
	}
	
	public static Object getField(String loadedClass, Object instance, String... attempts) {
		try {
			return getHiddenField(loadedClass, attempts).get(instance);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Field getHiddenField(String loadedClass, String... attempts) {
		for (String attempt : attempts) {
			try {
				return getLoadedClass(loadedClass).getField(attempt);
			} catch (NoSuchFieldException e) {
				
			}
		} return null;
	}
	
	public static Field getDeclaredField(String loadedClass, String... attempts) {
		for (String attempt : attempts) {
			try {
				return getLoadedClass(loadedClass).getDeclaredField(attempt);
			} catch (NoSuchFieldException e) {
				
			}
		} return null;
	}
	
	public static Object getEnum(String loadedClass, String... attempts) {
		if (!getLoadedClass(loadedClass).isEnum())
			return null;
		Object[] enumConstants = getLoadedClass(loadedClass).getEnumConstants();
		
		for (String attempt : attempts)
			for (int i = 0; i < enumConstants.length; i++)
				if (attempt.equals(((Enum<?>) enumConstants[i]).name()))
					return enumConstants[i];
		return null;
	}
	
	public static Object getEnum(String loadedClass, int ordinal) {
		if (!getLoadedClass(loadedClass).isEnum())
			return null;
		return getLoadedClass(loadedClass).getEnumConstants()[ordinal];
	}
	
	public static Class<?> getCBClass(String path) throws ClassNotFoundException {
		return Class.forName(cbPath + path);
	}
	
	public static Class<?> getNMSClass(String path) throws ClassNotFoundException {
		return Class.forName(nmsPath + path);
	}
	
	public static Class<?> getNMNClass(String path) throws ClassNotFoundException {
		return Class.forName("net.minecraft.network." + path);
	}
	
	public static Locale getLocale(ChatPluginBukkitPlayer player) {
		String str = VersionUtils.getVersion().getProtocol() >= 341 ? player.toAdapter().bukkitValue().getLocale() : (String) getField("EntityPlayer", invokeMethod("CraftPlayer", "getHandle", player.getCraftPlayer()), "locale", "");
		return new Locale(str.substring(0, str.indexOf('_')), str.substring(str.indexOf('_') + 1));
	}
	
}
