/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

package me.remigio07.chatplugin.server.bukkit;

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

import org.bukkit.Location;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import com.google.common.primitives.Primitives;

import me.remigio07.chatplugin.api.common.util.Utils;
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
		boolean atLeast1_20_5 = VersionUtils.getVersion().isAtLeast(Version.V1_20_5);
		Class<?> clazz;
		cbPath = "org.bukkit.craftbukkit." + (VersionUtils.getNMSVersion().equals(Utils.NOT_APPLICABLE) ? "" : VersionUtils.getNMSVersion() + ".");
		nmsPath = "net.minecraft.server." + (atLeast1_17 ? "" : VersionUtils.getNMSVersion() + ".");
		
		try {
			// CraftPlayer
			clazz = getCBClass("entity.CraftPlayer");
			classes.put("CraftPlayer", clazz);
			putMethod(clazz, "getHandle");
			
			// CraftWorld - < 1.9
			clazz = getCBClass("CraftWorld");
			classes.put("CraftWorld", clazz);
			putMethod(clazz, "getHandle");
			
			// CraftChatMessage
			clazz = getCBClass("util.CraftChatMessage");
			classes.put("CraftChatMessage", clazz);
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_16_4))			
				putMethod(clazz, "fromJSON", Arrays.asList(String.class));
			
			// MinecraftServer
			clazz = getNMSClass("MinecraftServer");
			classes.put("MinecraftServer", clazz);
			putMethod(clazz, "getServer");
			
			// Packet
			clazz = atLeast1_17 ? getNMNClass("protocol.Packet") : getNMSClass("Packet");
			classes.put("Packet", clazz);
			
			// InventoryView - abstract class until 1.20.6, became interface in 1.21
			clazz = Class.forName("org.bukkit.inventory.InventoryView");
			classes.put("InventoryView", clazz);
			putMethod(clazz, "setTitle", Arrays.asList(String.class));
			putMethod(clazz, "getTopInventory");
			
			// HumanEntity
			clazz = Class.forName("org.bukkit.entity.HumanEntity");
			classes.put("HumanEntity", clazz);
			putMethod(clazz, "getOpenInventory");
			
			// Sound - enum until 1.21.2, became interface in 1.21.3
			clazz = Class.forName("org.bukkit.Sound");
			classes.put("Sound", clazz);
			
			clazz = Class.forName("org.bukkit.entity.Player");
			classes.put("Player", clazz);
			putMethod(clazz, "playSound", Arrays.asList(Location.class, classes.get("Sound"), float.class, float.class));
			
			if (VersionUtils.getNMSVersion().equals(Utils.NOT_APPLICABLE)) {
				// EntityPlayer
				clazz = getNMSClass("level.ServerPlayer");
				classes.put("EntityPlayer", clazz);
					
					// PlayerConnection
					clazz = getNMSClass("network.ServerPlayerConnection");
					classes.put("PlayerConnection", clazz);
					putMethod(clazz, "sendPacket", Arrays.asList(classes.get("Packet")), "send", "a");
			} else {
				// EntityPlayer
				clazz = getNMSClass((atLeast1_17 ? "level." : "") + "EntityPlayer");
				classes.put("EntityPlayer", clazz);
					
					// PlayerConnection
					clazz = getNMSClass(atLeast1_17 ? "network.ServerPlayerConnection" : "PlayerConnection");
					classes.put("PlayerConnection", clazz);
					putMethod(clazz, "sendPacket", Arrays.asList(classes.get("Packet")), "b", "a");
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20)) { // TODO remove duplicate IChatBaseComponent
					// IChatBaseComponent
					clazz = atLeast1_17 ? getNMNClass("chat.IChatBaseComponent") : getNMSClass("IChatBaseComponent");
					classes.put("IChatBaseComponent", clazz);
					
					if (VersionUtils.getVersion().getProtocol() < 341)
						putMethod(clazz, "getText");
					else putMethod(clazz, "getString");
					
					
					if (VersionUtils.getVersion().isOlderThan(Version.V1_16_4)) {
						// ChatSerializer
						clazz = atLeast1_17 ? getNMNClass("chat.IChatBaseComponent$ChatSerializer") : getNMSClass("IChatBaseComponent$ChatSerializer");
						classes.put("ChatSerializer", clazz);
						putMethod(clazz, "fromJson", Arrays.asList(String.class), "a");
					}
					
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
					putMethod(clazz, "getHandle");
					
					// EntityHuman
					clazz = atLeast1_17 ? Class.forName("net.minecraft.world.entity.player.EntityHuman") : getNMSClass("EntityHuman");
					classes.put("EntityHuman", clazz);
				}
			} if (VersionUtils.getVersion().isAtLeast(Version.V1_12)) {
				// IChatBaseComponent
				clazz = atLeast1_17 ? getNMNClass("chat." + (atLeast1_20_5 && VersionUtils.isPaper() ? "Component" : "IChatBaseComponent")) : getNMSClass("IChatBaseComponent");
				classes.put("IChatBaseComponent", clazz);
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_16_4)) { // TODO remove duplicate and remove useless version check
					// ChatSerializer
					clazz = atLeast1_17 ? getNMNClass(atLeast1_20_5 && VersionUtils.isPaper() ? "chat.Component$Serializer" : "chat.IChatBaseComponent$ChatSerializer") : getNMSClass("IChatBaseComponent$ChatSerializer");
					classes.put("ChatSerializer", clazz);
					putMethod(clazz, "fromJson", Arrays.asList(String.class), "a");
				}
				
				// ChatMessageType
				clazz = atLeast1_17 ? getNMNClass("chat." + (atLeast1_20_5 && VersionUtils.isPaper() ? "ChatType" : "ChatMessageType")) : getNMSClass("ChatMessageType");
				classes.put("ChatMessageType", clazz);
				
				// PacketPlayOutAdvancements
				clazz = atLeast1_17 ? getNMNClass("protocol.game." + (atLeast1_20_5 && VersionUtils.isPaper() ? "ClientboundUpdateAdvancementsPacket" : "PacketPlayOutAdvancements")) : getNMSClass("PacketPlayOutAdvancements");
				classes.put("PacketPlayOutAdvancements", clazz);
				
				// AdvancementDisplay
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements." + (atLeast1_20_5 && VersionUtils.isPaper() ? "DisplayInfo" : "AdvancementDisplay")) : getNMSClass("AdvancementDisplay");
				classes.put("AdvancementDisplay", clazz);
				
				// Advancement
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.Advancement") : getNMSClass("Advancement");
				classes.put("Advancement", clazz);
				
				// AdvancementFrameType
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements." + (atLeast1_20_5 && VersionUtils.isPaper() ? "AdvancementType" : "AdvancementFrameType")) : getNMSClass("AdvancementFrameType");
				classes.put("AdvancementFrameType", clazz);
				
				// AdvancementRewards
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.AdvancementRewards") : getNMSClass("AdvancementRewards");
				classes.put("AdvancementRewards", clazz);
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_3)) {
					// CustomFunction$a
					clazz = atLeast1_17 ? Class.forName("net.minecraft.commands.CustomFunction$a") : getNMSClass("CustomFunction$a");
					classes.put("CustomFunction$a", clazz);
				} if (VersionUtils.getVersion().isAtLeast(Version.V1_20_2)) {
					// AdvancementRequirements
					clazz = Class.forName("net.minecraft.advancements.AdvancementRequirements");
					classes.put("AdvancementRequirements", clazz);
					
					// AdvancementHolder
					clazz = Class.forName("net.minecraft.advancements.AdvancementHolder");
					classes.put("AdvancementHolder", clazz);
				}
				
				// AdvancementProgress
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.AdvancementProgress") : getNMSClass("AdvancementProgress");
				classes.put("AdvancementProgress", clazz);
				putMethod(clazz, "update", VersionUtils.getVersion().isAtLeast(Version.V1_20_2) ? Arrays.asList(classes.get("AdvancementRequirements")) : Arrays.asList(Map.class, String[][].class), "a");
				putMethod(clazz, "getCriterionProgress", Arrays.asList(String.class), "getCriterion", "c");
				
				// CriterionProgress
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.CriterionProgress") : getNMSClass("CriterionProgress");
				classes.put("CriterionProgress", clazz);
				putMethod(clazz, "grant", Collections.emptyList(), "b");
				
				// MinecraftKey
				clazz = atLeast1_17 ? Class.forName("net.minecraft.resources." + (atLeast1_20_5 && VersionUtils.isPaper() ? "ResourceLocation" : "MinecraftKey")) : getNMSClass("MinecraftKey");
				classes.put("MinecraftKey", clazz);
				
				// Criterion
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.Criterion") : getNMSClass("Criterion");
				classes.put("Criterion", clazz);
				
				// CriterionInstance
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements." + (atLeast1_20_5 && VersionUtils.isPaper() ? "CriterionTriggerInstance" : "CriterionInstance")) : getNMSClass("CriterionInstance");
				classes.put("CriterionInstance", clazz);
				
				// CriterionTriggerImpossible
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.critereon." + (atLeast1_20_5 && VersionUtils.isPaper() ? "ImpossibleTrigger" : "CriterionTriggerImpossible"))  : getNMSClass("CriterionTriggerImpossible");
				classes.put("CriterionTriggerImpossible", clazz);
				
				// CriterionTriggerImpossible$a
				clazz = atLeast1_17 ? Class.forName("net.minecraft.advancements.critereon." + (atLeast1_20_5 && VersionUtils.isPaper() ? "ImpossibleTrigger$TriggerInstance" : "CriterionTriggerImpossible$a")) : getNMSClass("CriterionTriggerImpossible$a");
				classes.put("CriterionTriggerImpossible$a", clazz);
				
				// ItemStack
				clazz = atLeast1_17 ? Class.forName("net.minecraft.world.item.ItemStack") : getNMSClass("ItemStack");
				classes.put("ItemStack", clazz);
				
				// CraftItemStack
				clazz = getCBClass("inventory.CraftItemStack");
				classes.put("CraftItemStack", clazz);
				putMethod(clazz, "asNMSCopy", Arrays.asList(ItemStack.class));
				
				if (VersionUtils.getVersion().isAtLeast(Version.V1_13)) {
					// CraftServer
					clazz = getCBClass("CraftServer");
					classes.put("CraftServer", clazz);
					putMethod(clazz, "syncCommands");
					
					if (VersionUtils.getVersion().isAtLeast(Version.V1_20_2)) {
						// AdvancementRequirements
						clazz = Class.forName("net.minecraft.advancements.AdvancementRequirements");
						classes.put("AdvancementRequirements", clazz);
						
						// CriterionTrigger
						clazz = Class.forName("net.minecraft.advancements.CriterionTrigger");
						classes.put("CriterionTrigger", clazz);
						
						if (VersionUtils.getVersion().isAtLeast(Version.V1_20_3)) {
							if (!VersionUtils.isPaper()) {
								// ScoreboardObjective
								clazz = Class.forName("net.minecraft.world.scores.ScoreboardObjective");
								classes.put("ScoreboardObjective", clazz);
								putMethod(clazz, "setNumberFormat", Arrays.asList(getNMNClass("chat.numbers.NumberFormat")), "b");
								
								// FixedFormat
								clazz = getNMNClass("chat.numbers.FixedFormat");
								classes.put("FixedFormat", clazz);
								
								// CraftObjective
								clazz = getCBClass("scoreboard.CraftObjective");
								classes.put("CraftObjective", clazz);
								putMethod(clazz, "getHandle");
							} if (atLeast1_20_5) {
								// ClientboundCustomPayloadPacket
								clazz = getNMNClass("protocol.common.ClientboundCustomPayloadPacket");
								classes.put("ClientboundCustomPayloadPacket", clazz);
								
								// CustomPacketPayload
								clazz = getNMNClass("protocol.common.custom.CustomPacketPayload");
								classes.put("CustomPacketPayload", clazz);
								
								// BrandPayload
								clazz = getNMNClass("protocol.common.custom.BrandPayload");
								classes.put("BrandPayload", clazz);
								
								// Attribute - enum until 1.21.2, became interface in 1.21.3
								clazz = Class.forName("org.bukkit.attribute.Attribute");
								classes.put("Attribute", clazz);
								
								// ItemMeta
								clazz = Class.forName("org.bukkit.inventory.meta.ItemMeta");
								classes.put("ItemMeta", clazz);
								putMethod(clazz, "addAttributeModifier", Arrays.asList(classes.get("Attribute"), AttributeModifier.class));
								putMethod(clazz, "removeAttributeModifier", Arrays.asList(classes.get("Attribute")));
							}
						}
					}
				}
			} if (VersionUtils.getVersion().isOlderThan(Version.V1_13_2)) {
				// PacketPlayOutPlayerListHeaderFooter
				clazz = getNMSClass("PacketPlayOutPlayerListHeaderFooter");
				classes.put("PacketPlayOutPlayerListHeaderFooter", clazz);
			} if (VersionUtils.getVersion().isAtLeast(Version.V1_9))
				return;
			
			// Scoreboard
			clazz = getNMSClass("Scoreboard");
			classes.put("Scoreboard", clazz);
			putMethod(clazz, "addPlayerToTeam", Arrays.asList(String.class, String.class));
			
			// CraftScoreboard
			clazz = getCBClass("scoreboard.CraftScoreboard");
			classes.put("CraftScoreboard", clazz);
			putMethod(clazz, "getHandle");
			
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
	
	@SuppressWarnings("all") // ...used to avoid "Unnecessary @SuppressWarnings("deprecation")" for the annotation below when using Java 8 on IDEs like Eclipse
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
			Field field = getField(loadedClass, attempts);
			return field == null ? null : field.get(instance);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} return null;
	}
	
	@SuppressWarnings("all") // ...used to avoid "Unnecessary @SuppressWarnings("deprecation")" for the annotation below when using Java 8 on IDEs like Eclipse
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
	
	public static Class<?> getCBClass(String path) throws ClassNotFoundException {
		return Class.forName(cbPath + path);
	}
	
	public static Class<?> getNMSClass(String path) throws ClassNotFoundException {
		return Class.forName(nmsPath + path);
	}
	
	public static Class<?> getNMNClass(String path) throws ClassNotFoundException {
		return Class.forName("net.minecraft.network." + path);
	}
	
	public static Object getIChatBaseComponent(String escapedJSON) {
		return VersionUtils.getVersion().isAtLeast(Version.V1_16_4) ? invokeMethod("CraftChatMessage", "fromJSON", null, escapedJSON) : invokeMethod("ChatSerializer", "fromJson", null, escapedJSON);
	}
	
}
