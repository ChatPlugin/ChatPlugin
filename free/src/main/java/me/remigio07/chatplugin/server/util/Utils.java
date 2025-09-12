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

package me.remigio07.chatplugin.server.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;

import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.sponge.SpongeReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils extends me.remigio07.chatplugin.api.server.util.Utils {
	
	public static final String[] FREE_VERSION_ADS = new String[] {
			"You are running the free version of the plugin.",
			"Did you know the premium version includes a punishment system?",
			"Did you know the premium version includes Discord and Telegram integrations?",
			"Did you know the premium version includes an advanced chat logging system?",
			"The paid version supports multi-instance synchronization.",
			"The paid version supports exclusive anticheat integrations.",
			"The paid version supports proxy softwares for networks.",
	};
	private static final String[] ATTEMPTS = { "a", "b", "c", "d", "e", "f" };
	private static final String ADVANCEMENTS_EXPLANATION = "ChatPlugin uses custom advancements to display private messages. If you receive a private message while your Advancements tab is open, its advancement will appear here. You can safely ignore this, keep playing!";
	private static final boolean AT_LEAST_1_20_2 = VersionUtils.getVersion().isAtLeast(Version.V1_20_2);
	private static final boolean AT_LEAST_1_20_3 = VersionUtils.getVersion().isAtLeast(Version.V1_20_3);
	public static Map<UUID, String> inventoryTitles = new HashMap<>();
	
	static {
		if (!ChatPlugin.getInstance().isPremium()) // yeah, I've put it right here... remove it if you want, I guess 🙄
			TaskManager.scheduleAsync(() -> LogManager.log(FREE_VERSION_ADS[ThreadLocalRandom.current().nextInt(FREE_VERSION_ADS.length)] + " Buy premium to unlock new features and get rid of ads.", 0), 14400000L, 14400000L);
	}
	
	@SuppressWarnings("deprecation")
	public static void setTitle(ChatPluginServerPlayer viewer, String title, int rows) {
		if (Environment.isBukkit()) {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20)) {
				int windowID = (int) BukkitReflection.getFieldValue("Container", BukkitReflection.getFieldValue("EntityHuman", BukkitReflection.invokeMethod("CraftHumanEntity", "getHandle", ((ChatPluginBukkitPlayer) viewer).getCraftPlayer()), VersionUtils.getVersion().isAtLeast(Version.V1_20_2) ? "bS" : VersionUtils.getVersion().isAtLeast(Version.V1_20) ? "bR" : VersionUtils.getVersion().isAtLeast(Version.V1_19_4) ? "bP" : "activeContainer", "bV", "bW", "containerMenu", "bU"), "windowId", "containerId", "j");
				Object packet, component = BukkitReflection.getIChatBaseComponent("{\"text\":\"" + Jsoner.escape(title) + "\"}");
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_14))
					packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, String.class, BukkitReflection.getLoadedClass("IChatBaseComponent"), int.class }, windowID, "minecraft:container", component, rows * 9);
				else packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, BukkitReflection.getLoadedClass("Containers"), BukkitReflection.getLoadedClass("IChatBaseComponent") }, windowID, BukkitReflection.getFieldValue("Containers", null, "GENERIC_9X" + rows, ATTEMPTS[rows - 1]), component);
				
				viewer.sendPacket(packet);
				viewer.toAdapter().bukkitValue().updateInventory();
			} else BukkitReflection.invokeMethod("InventoryView", "setTitle", BukkitReflection.invokeMethod("HumanEntity", "getOpenInventory", viewer.toAdapter().bukkitValue()), title);
		} else {
			Object container = viewer.toAdapter().spongeValue().getOpenInventory().get();
			
			viewer.sendPacket(SpongeReflection.getInstance("SPacketOpenWindow", new Class[] { int.class, String.class, SpongeReflection.getLoadedClass("ITextComponent"), int.class }, SpongeReflection.getFieldValue("Container", container, "field_75152_c"), "minecraft:container", SpongeReflection.invokeMethod("ITextComponent$Serializer", "func_150699_a", null, "{\"text\":\"" + title + "\"}"), rows * 9));
			SpongeReflection.invokeMethod("EntityPlayerMP", "func_71120_a", viewer.toAdapter().spongeValue(), container);
		} inventoryTitles.put(viewer.getUUID(), title);
	}
	
	public static String getTitle(ChatPluginServerPlayer viewer) {
		return inventoryTitles.get(viewer.getUUID());
	}
	
	@SuppressWarnings("deprecation")
	public static void displayAdvancement(ChatPluginServerPlayer player, String text, ItemStackAdapter itemStack) { // :)
		if (Environment.isBukkit()) {
			Map<String, Object> criteria = Collections.singletonMap("impossible", AT_LEAST_1_20_2
					? BukkitReflection.getInstance("Criterion", new Class[] { BukkitReflection.getLoadedClass("CriterionTrigger"), BukkitReflection.getLoadedClass("CriterionInstance") }, BukkitReflection.getInstance("CriterionTriggerImpossible"), BukkitReflection.getInstance("CriterionTriggerImpossible$a"))
					: BukkitReflection.getInstance("Criterion", new Class[] { BukkitReflection.getLoadedClass("CriterionInstance") }, BukkitReflection.getInstance("CriterionTriggerImpossible$a")));
			Object key = BukkitReflection.getInstance("MinecraftKey", "chatplugin", "toast_notification");
			Object progress = BukkitReflection.getInstance("AdvancementProgress");
			Object requirements = AT_LEAST_1_20_2 ? AT_LEAST_1_20_3
					? BukkitReflection.getInstance("AdvancementRequirements", new Class[] { List.class }, Collections.singletonList(Collections.singletonList("impossible")))
					: BukkitReflection.getInstance("AdvancementRequirements", new Class[] { String[][].class }, (Object) new String[][] { new String[] { "impossible" } })
					: new String[][] { new String[] { "impossible" } };
			Object display = BukkitReflection.getInstance(
					"AdvancementDisplay",
					new Class[] { BukkitReflection.getLoadedClass("ItemStack"), BukkitReflection.getLoadedClass("IChatBaseComponent"), BukkitReflection.getLoadedClass("IChatBaseComponent"), AT_LEAST_1_20_3 ? Optional.class : BukkitReflection.getLoadedClass("MinecraftKey"), BukkitReflection.getLoadedClass("AdvancementFrameType"), boolean.class, boolean.class, boolean.class },
					BukkitReflection.invokeMethod("CraftItemStack", "asNMSCopy", null, itemStack.bukkitValue()), BukkitReflection.getIChatBaseComponent("{\"text\":\"" + Jsoner.escape(text) + "\"}"), BukkitReflection.getIChatBaseComponent("{\"text\":\"" + ADVANCEMENTS_EXPLANATION + "\"}"), AT_LEAST_1_20_3 ? Optional.empty() : null, BukkitReflection.getEnum("AdvancementFrameType", 2), true, false, true
					);
			Object rewards = BukkitReflection.getInstance(
					"AdvancementRewards",
					new Class[] { int.class, AT_LEAST_1_20_3 ? List.class : Array.newInstance(BukkitReflection.getLoadedClass("MinecraftKey"), 0).getClass(), AT_LEAST_1_20_3 ? List.class : Array.newInstance(BukkitReflection.getLoadedClass("MinecraftKey"), 0).getClass(), AT_LEAST_1_20_3 ? Optional.class : BukkitReflection.getLoadedClass("CustomFunction$a") },
					0, AT_LEAST_1_20_3 ? Collections.emptyList() : Array.newInstance(BukkitReflection.getLoadedClass("MinecraftKey"), 0), AT_LEAST_1_20_3 ? Collections.emptyList() : Array.newInstance(BukkitReflection.getLoadedClass("MinecraftKey"), 0), AT_LEAST_1_20_3 ? Optional.empty() : null
					);
			Object advancement = VersionUtils.getVersion().isAtLeast(Version.V1_20) ? AT_LEAST_1_20_2 ? BukkitReflection.getInstance(
					"Advancement",
					new Class[] { Optional.class, Optional.class, BukkitReflection.getLoadedClass("AdvancementRewards"), Map.class, BukkitReflection.getLoadedClass("AdvancementRequirements"), boolean.class, Optional.class },
					Optional.empty(), Optional.of(display), rewards, criteria, requirements, false, Optional.empty()
					) : BukkitReflection.getInstance(
							"Advancement",
							new Class[] { BukkitReflection.getLoadedClass("MinecraftKey"), BukkitReflection.getLoadedClass("Advancement"), BukkitReflection.getLoadedClass("AdvancementDisplay"), BukkitReflection.getLoadedClass("AdvancementRewards"), Map.class, String[][].class, boolean.class },
							key, null, display, rewards, criteria, requirements, false
							) : BukkitReflection.getInstance(
									"Advancement",
									new Class[] { BukkitReflection.getLoadedClass("MinecraftKey"), BukkitReflection.getLoadedClass("Advancement"), BukkitReflection.getLoadedClass("AdvancementDisplay"), BukkitReflection.getLoadedClass("AdvancementRewards"), Map.class, String[][].class },
									key, null, display, rewards, criteria, requirements
									);
			if (AT_LEAST_1_20_2)
				BukkitReflection.invokeMethod("AdvancementProgress", "update", progress, requirements);
			else BukkitReflection.invokeMethod("AdvancementProgress", "update", progress, criteria, requirements);
			
			BukkitReflection.invokeMethod("CriterionProgress", "grant", BukkitReflection.invokeMethod("AdvancementProgress", "getCriterionProgress", progress, "impossible"));
			player.sendPacket(BukkitReflection.getInstance("PacketPlayOutAdvancements", new Class[] { boolean.class, Collection.class, Set.class, Map.class }, false, Arrays.asList(AT_LEAST_1_20_2 ? BukkitReflection.getInstance("AdvancementHolder", new Class[] { BukkitReflection.getLoadedClass("MinecraftKey"), BukkitReflection.getLoadedClass("Advancement") }, key, advancement) : advancement), Collections.emptySet(), Collections.singletonMap(key, progress)));
			player.sendPacket(BukkitReflection.getInstance("PacketPlayOutAdvancements", new Class[] { boolean.class, Collection.class, Set.class, Map.class }, false, Collections.emptyList(), Collections.singleton(key), Collections.emptyMap()));
		} else SpongeAdvancement.displayAdvancement(player, text, itemStack);
	}
	
	public static void ensureSync(Runnable runnable) {
		if (Environment.isBukkit() ? Bukkit.isPrimaryThread() : Sponge.getServer().isMainThread())
			runnable.run();
		else TaskManager.runSync(runnable, 0L);
	}
	
	public static TextComponent deserializeLegacy(String text, boolean translate) {
		return LegacyComponentSerializer.legacySection().deserialize(translate ? ChatColor.translate(text) : text);
	}
	
	public static String serializeLegacy(Component text) {
		return LegacyComponentSerializer.legacySection().serialize(text);
	}
	
	private static class SpongeAdvancement {
		
		@SuppressWarnings("deprecation")
		public static void displayAdvancement(ChatPluginServerPlayer player, String text, ItemStackAdapter itemStack) {
			ensureSync(() -> {
				Advancement advancement = Advancement.builder().id("toast_notification").criterion(AdvancementCriterion.DUMMY).displayInfo(DisplayInfo.builder().icon(itemStack.spongeValue()).title(serializeSpongeText(text, false)).description(serializeSpongeText(ADVANCEMENTS_EXPLANATION, false)).type(AdvancementTypes.GOAL).announceToChat(false).hidden(true).build()).build();
				
				AdvancementTree.builder().id("toast_notification").rootAdvancement(advancement).build();
				Sponge.getRegistry().register(Advancement.class, advancement);
				player.toAdapter().spongeValue().getProgress(advancement).grant();
				TaskManager.runSync(() -> {
					if (player.isOnline())
						player.toAdapter().spongeValue().getProgress(advancement).revoke();
				}, 0L);
			});
		}
		
	}
	
}
