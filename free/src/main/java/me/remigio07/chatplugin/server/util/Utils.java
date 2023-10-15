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

package me.remigio07.chatplugin.server.util;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.sponge.SpongeReflection;

public class Utils extends me.remigio07.chatplugin.api.server.util.Utils {
	
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
			
			viewer.sendPacket(SpongeReflection.getInstance("SPacketOpenWindow", new Class[] { int.class, String.class, SpongeReflection.getLoadedClass("ITextComponent"), int.class }, SpongeReflection.getFieldValue("Container", container, "field_75152_c"), "minecraft:container", SpongeReflection.invokeMethod("ITextComponent$Serializer", "func_150699_a", null, "{\"text\":\"" + title + "\"}"), rows * 9));
			SpongeReflection.invokeMethod("EntityPlayerMP", "func_71120_a", viewer.toAdapter().spongeValue(), container);
		}
	}
	
}
