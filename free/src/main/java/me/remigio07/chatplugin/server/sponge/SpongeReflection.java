/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
 * 	Copyright 2025  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://remigio07.me/chatplugin>
 */

package me.remigio07.chatplugin.server.sponge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Primitives;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class SpongeReflection {
	
	private static Map<String, Class<?>> classes = new HashMap<>();
	private static Map<Class<?>, Map<String, Method>> methods = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public static void initReflection() throws ChatPluginManagerException {
		Class<?> clazz;
		
		try {
			// Packet
			clazz = getClass("network.Packet");
			classes.put("Packet", clazz);
			
			// NetHandlerPlayServer
			clazz = getClass("network.NetHandlerPlayServer");
			classes.put("NetHandlerPlayServer", clazz);
			putMethod(clazz, "func_147359_a", Arrays.asList(classes.get("Packet")));
			
			// ServerStatusResponse$Version
			clazz = getClass(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "network.ServerStatusResponse$Version" : "network.ServerStatusResponse$MinecraftProtocolVersionIdentifier");
			classes.put("ServerStatusResponse$Version", clazz);
			
			// ServerStatusResponse
			clazz = getClass("network.ServerStatusResponse");
			classes.put("ServerStatusResponse", clazz);
			putMethod(clazz, "func_151321_a", Arrays.asList(classes.get("ServerStatusResponse$Version")));
			
			// Action
			clazz = getClass("network.play.server." + (VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "SPacketPlayerListItem" : "S38PacketPlayerListItem") + "$Action");
			classes.put("Action", clazz);
			
			// SpongeTabListEntry
			clazz = Class.forName("org.spongepowered.common.entity.player.tab.SpongeTabListEntry");
			classes.put("SpongeTabListEntry", clazz);
			putMethod(clazz, "sendUpdate", Arrays.asList(classes.get("Action")));
			
			// in the end, we didn't even need it, but let's leave it for the future, just in case :)
			// due to Sponge's code (see SpongeTabListEntry#updateWithoutSend()) sometimes entries are not automatically updated
			// SpongeReflection.invokeMethod("SpongeTabListEntry", "sendUpdate", entry, SpongeReflection.getEnum("Action", "UPDATE_DISPLAY_NAME"));
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_12)) {
				// SPacketOpenWindow
				clazz = getClass("network.play.server.SPacketOpenWindow");
				classes.put("SPacketOpenWindow", clazz);
				
				// Container
				clazz = getClass("inventory.Container");
				classes.put("Container", clazz);
				
				clazz = getClass("entity.player.EntityPlayerMP");
				classes.put("EntityPlayerMP", clazz);
				putMethod(clazz, "func_71120_a", Arrays.asList(classes.get("Container")));
				
				// ITextComponent
				clazz = getClass("util.text.ITextComponent");
				classes.put("ITextComponent", clazz);
				
				// ITextComponent$Serializer
				clazz = getClass("util.text.ITextComponent$Serializer");
				classes.put("ITextComponent$Serializer", clazz);
				putMethod(clazz, "func_150699_a", Arrays.asList(String.class));
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			throw new ChatPluginManagerException("reflection utils", e);
		}
	}
	
	public static void putMethod(Class<?> clazz, String method, String... otherAttempts) throws NoSuchMethodException {
		putMethod(clazz, method, Collections.emptyList(), otherAttempts);
	}
	
	public static void putMethod(Class<?> clazz, String method, List<Class<?>> parameters, String... otherAttempts) throws NoSuchMethodException {
		Map<String, Method> map = methods.getOrDefault(clazz, new HashMap<>());
		List<String> attempts = new ArrayList<>(Arrays.asList(otherAttempts == null ? new String[0] : otherAttempts));
		
		attempts.add(0, method);
		
		for (String attempt : attempts) {
			try {
				Method declaredMethod = clazz.getDeclaredMethod(attempt, parameters.toArray(new Class<?>[0]));
				
				declaredMethod.setAccessible(true);
				map.put(method, declaredMethod);
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
		return getInstance(loadedClass, objectsToTypes(args), args);
	}
	
	public static Object getInstance(String loadedClass, Class<?>[] types, Object... args) {
		try {
			Constructor<?> constructor = getLoadedClass(loadedClass).getDeclaredConstructor(types);
			@SuppressWarnings("deprecation")
			boolean accessible = constructor.isAccessible();
			
			if (!accessible)
				constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Class<?>[] objectsToTypes(Object... args) {
		Class<?>[] array = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			if (Primitives.isWrapperType(args[i].getClass())) {
				array[i] = Primitives.unwrap(args[i].getClass());
				continue;
			} array[i] = args[i].getClass();
		} return array;
	}
	
	public static Object getFieldValue(String loadedClass, Object instance, String... attempts) {
		try {
			return getField(loadedClass, attempts).get(instance);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Field getField(String loadedClass, String... attempts) {
		for (String attempt : attempts) {
			try {
				Field field = getLoadedClass(loadedClass).getDeclaredField(attempt);
				@SuppressWarnings("deprecation")
				boolean accessible = field.isAccessible();
				
				if (!accessible)
					field.setAccessible(true);
				return field;
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
	
	public static Class<?> getClass(String path) throws ClassNotFoundException {
		return Class.forName("net.minecraft." + path);
	}
	
}
