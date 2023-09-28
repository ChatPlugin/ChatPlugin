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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.server.sponge;

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
			
			// ServerStatusResponse$Version
			clazz = getClass(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "network.ServerStatusResponse$Version" : "network.ServerStatusResponse$MinecraftProtocolVersionIdentifier");
			classes.put("ServerStatusResponse$Version", clazz);
			
			// ServerStatusResponse
			clazz = getClass("network.ServerStatusResponse");
			classes.put("ServerStatusResponse", clazz);
			putMethod(clazz, "func_151321_a", Arrays.asList(classes.get("ServerStatusResponse$Version")));
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
			return getField(loadedClass, attempts).get(instance);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static Field getField(String loadedClass, String... attempts) {
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
	
	public static Class<?> getClass(String path) throws ClassNotFoundException {
		return Class.forName("net.minecraft." + path);
	}
	
}
