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

package me.remigio07.chatplugin.api.proxy.util;

import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Proxy utils class.
 */
public class Utils extends me.remigio07.chatplugin.api.common.util.Utils {
	
	/**
	 * Serializes the specified input to a BungeeCord-compatible text.
	 * 
	 * @param input Input text
	 * @return BungeeCord-compatible text
	 */
	@SuppressWarnings("deprecation")
	public static TextComponent serializeBungeeCordText(String input) {
		TextComponent bungeeCordComponent = new TextComponent("§r");
		
		if (!input.isEmpty())
			for (BaseComponent component : TextComponent.fromLegacyText(ChatColor.translate(input)))
				bungeeCordComponent.addExtra(component);
		return bungeeCordComponent;
	}
	
	/**
	 * Deserializes the specified input to a plain text.
	 * 
	 * @param input BungeeCord-compatible text
	 * @return Plain text
	 */
	public static String deserializeBungeeCordText(TextComponent input) {
		return TextComponent.toLegacyText(input);
	}
	
}
