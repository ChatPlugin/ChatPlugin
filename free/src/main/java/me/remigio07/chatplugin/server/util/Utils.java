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

package me.remigio07.chatplugin.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.sponge.SpongeReflection;

public class Utils extends me.remigio07.chatplugin.api.server.util.Utils {
	
	private static Class<?>[] classes;
	private static Version version = VersionUtils.getVersion();
	
	static {
		if (Environment.isBukkit()) {
			List<Class<?>> classes = new ArrayList<>();
			
			classes.add(BukkitReflection.getLoadedClass("IChatBaseComponent"));
			
			if (version.isAtLeast(Version.V1_19))
				classes.add(boolean.class);
			else if (version.isAtLeast(Version.V1_12)) {
				classes.add(BukkitReflection.getLoadedClass("ChatMessageType"));
				
				if (version.isAtLeast(Version.V1_16))
					classes.add(UUID.class);
			} else classes.add(byte.class);
			Utils.classes = classes.toArray(new Class[0]);
		}
	}
	
	public static boolean reportPerm(ChatPluginServerPlayer player, String permission) {
		if (permission.isEmpty() || player.hasPermission(permission))
			return true;
		player.sendTranslatedMessage("misc.no-permission");
		return false;
	}
	
	private static String[] ATTEMPTS = new String[] { "a", "b", "c", "d", "e", "f" };
	
	@SuppressWarnings("deprecation")
	public static void setTitle(ChatPluginServerPlayer viewer, String title, int rows) {
		if (Environment.isBukkit()) {
			int windowID = (int) BukkitReflection.getFieldValue("Container", BukkitReflection.getFieldValue("EntityHuman", BukkitReflection.invokeMethod("CraftHumanEntity", "getHandle", ((ChatPluginBukkitPlayer) viewer).getCraftPlayer()), VersionUtils.getVersion().isAtLeast(Version.V1_20) ? "bR" : VersionUtils.getVersion().isAtLeast(Version.V1_19_4) ? "bP" : "activeContainer", "bV", "bW", "containerMenu", "bU"), "windowId", "containerId", "j");
			Object packet, component = BukkitReflection.invokeMethod("ChatSerializer", "a", null, "{\"text\":\"" + title + "\"}");
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_13))
				packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, String.class, BukkitReflection.getLoadedClass("IChatBaseComponent"), int.class }, windowID, "minecraft:container", component, rows * 9);
			else packet = BukkitReflection.getInstance("PacketPlayOutOpenWindow", new Class[] { int.class, BukkitReflection.getLoadedClass("Containers"), BukkitReflection.getLoadedClass("IChatBaseComponent") }, windowID, VersionUtils.getVersion().isAtLeast(Version.V1_14) ? BukkitReflection.getFieldValue("Containers", null, "GENERIC_9X" + rows, ATTEMPTS[rows - 1]) : ("minecraft:generic_9x" + rows), component);
			
			viewer.sendPacket(packet);
			viewer.toAdapter().bukkitValue().updateInventory();
		} else {
			Object container = viewer.toAdapter().spongeValue().getOpenInventory().get();
			
			viewer.sendPacket(SpongeReflection.getInstance("SPacketOpenWindow", new Class[] { int.class, String.class, SpongeReflection.getLoadedClass("ITextComponent"), int.class }, SpongeReflection.getField("Container", container, "field_75152_c"), "minecraft:container", SpongeReflection.invokeMethod("ITextComponent$Serializer", "func_150699_a", null, "{\"text\":\"" + title + "\"}"), rows * 9));
			SpongeReflection.invokeMethod("EntityPlayerMP", "func_71120_a", viewer.toAdapter().spongeValue(), container);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void sendBukkitMessage(ChatPluginServerPlayer player, String json) {
		Object text = BukkitReflection.invokeMethod("ChatSerializer", "a", null, json);
		
		if (version.isAtLeast(Version.V1_19)) {
			player.sendPacket(BukkitReflection.getInstance("ClientboundSystemChatPacket", classes, text, false));
			return;
		} List<Object> args = new ArrayList<>();
		
		args.add(text);
		args.add(version.isAtLeast(Version.V1_12) ? BukkitReflection.getEnum("ChatMessageType", 0) : (byte) 0);
		
		if (version.isAtLeast(Version.V1_16))
			args.add(UUID.randomUUID());
		player.sendPacket(BukkitReflection.getInstance("PacketPlayOutChat", classes, args.toArray(new Object[0])));
	}
	
	public static String getBukkitNBT(ItemStackAdapter itemStack) {
		return BukkitReflection.invokeMethod("ItemStack", "getTag", BukkitReflection.invokeMethod("CraftItemStack", "asNMSCopy", itemStack)).toString();
	}
	
}
