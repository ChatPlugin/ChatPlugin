/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.sponge.SpongeReflection;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils extends me.remigio07.chatplugin.api.server.util.Utils {
	
	public static boolean reportPerm(ChatPluginServerPlayer player, String permission) {
		if (permission.isEmpty() || player.hasPermission(permission))
			return true;
		player.sendTranslatedMessage("misc.no-permission");
		return false;
	}
	
	private static boolean isAtLeastV1_20_5 = VersionUtils.getVersion().isAtLeast(Version.V1_20_5); 
	private static String[] ATTEMPTS = new String[] { "a", "b", "c", "d", "e", "f" };
	
	@SuppressWarnings("deprecation")
	public static void setTitle(ChatPluginServerPlayer viewer, String title, int rows) {
		if (Environment.isBukkit()) {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20)) {
				int windowID = (int) BukkitReflection.getFieldValue("Container", BukkitReflection.getFieldValue("EntityHuman", BukkitReflection.invokeMethod("CraftHumanEntity", "getHandle", ((ChatPluginBukkitPlayer) viewer).getCraftPlayer()), VersionUtils.getVersion().isAtLeast(Version.V1_20_2) ? "bS" : VersionUtils.getVersion().isAtLeast(Version.V1_20) ? "bR" : VersionUtils.getVersion().isAtLeast(Version.V1_19_4) ? "bP" : "activeContainer", "bV", "bW", "containerMenu", "bU"), "windowId", "containerId", "j");
				Object packet, component = BukkitReflection.invokeMethod("ChatSerializer", "a", null, "{\"text\":\"" + title + "\"}");
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_14))
					packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, String.class, BukkitReflection.getLoadedClass("IChatBaseComponent"), int.class }, windowID, "minecraft:container", component, rows * 9);
				else packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, BukkitReflection.getLoadedClass("Containers"), BukkitReflection.getLoadedClass("IChatBaseComponent") }, windowID, BukkitReflection.getFieldValue("Containers", null, "GENERIC_9X" + rows, ATTEMPTS[rows - 1]), component);
				
				viewer.sendPacket(packet);
				viewer.toAdapter().bukkitValue().updateInventory();
			} else viewer.toAdapter().bukkitValue().getOpenInventory().setTitle(title);
		} else {
			Object container = viewer.toAdapter().spongeValue().getOpenInventory().get();
			
			viewer.sendPacket(SpongeReflection.getInstance("SPacketOpenWindow", new Class[] { int.class, String.class, SpongeReflection.getLoadedClass("ITextComponent"), int.class }, SpongeReflection.getFieldValue("Container", container, "field_75152_c"), "minecraft:container", SpongeReflection.invokeMethod("ITextComponent$Serializer", "func_150699_a", null, "{\"text\":\"" + title + "\"}"), rows * 9));
			SpongeReflection.invokeMethod("EntityPlayerMP", "func_71120_a", viewer.toAdapter().spongeValue(), container);
		}
	}
	
	public static void displayAdvancement(ChatPluginServerPlayer player, String text, MaterialAdapter material, boolean glowing) {
		TaskManager.runSync(() -> BukkitAdvancement.displayAdvancement(player, text, material, glowing), 0L);
	}
	
	public static TextComponent deserializeLegacy(String text, boolean translate) {
		return LegacyComponentSerializer.legacySection().deserialize(translate ? ChatColor.translate(text) : text);
	}
	
	public static String serializeLegacy(TextComponent text) {
		return LegacyComponentSerializer.legacySection().serialize(text);
	}
	
	private static class BukkitAdvancement {
		
		@SuppressWarnings("deprecation")
		public static void displayAdvancement(ChatPluginServerPlayer player, String text, MaterialAdapter material, boolean glowing) {
			org.bukkit.UnsafeValues unsafe = Bukkit.getUnsafe();
			NamespacedKey key = new NamespacedKey(BukkitBootstrapper.getInstance(), UUID.randomUUID().toString());
			Advancement adv = unsafe.loadAdvancement(key, "{\"display\":{\"icon\":{\"" + (isAtLeastV1_20_5 ? "id" : "item") + "\":\"" + material.bukkitValue().getKey().getKey() + "\""
					+ (glowing ? isAtLeastV1_20_5 ? ",\"components\":{\"enchantments\":{\"levels\":{\"unbreaking\":1}}}" : ",\"nbt\":\"{Enchantments:[{id:unbreaking,lvl:1}]}\"" : "")
					+ "},\"title\":\"" + text + "\",\"frame\":\"goal\",\"description\":\"ChatPlugin's private message\",\"announce_to_chat\":false,\"show_toast\":true,\"hidden\":true},"
					+ "\"criteria\":{\"impossible\":{\"trigger\":\"" + (isAtLeastV1_20_5 ? "impossible" : "minecraft:impossible") + "\"}}}"
					);
			
			player.toAdapter().bukkitValue().getAdvancementProgress(adv).awardCriteria("impossible");
			unsafe.removeAdvancement(key);
		}
		
	}
	
}
