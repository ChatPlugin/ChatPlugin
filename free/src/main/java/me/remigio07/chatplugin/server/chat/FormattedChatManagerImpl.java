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

package me.remigio07.chatplugin.server.chat;

import java.util.List;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;

public class FormattedChatManagerImpl extends FormattedChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.formatted-chat.enabled"))
			return;
		sendAnyway = ConfigurationType.CHAT.get().getBoolean("chat.formatted-chat.send-anyway");
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = sendAnyway = false;
	}
	
	@Override
	public boolean containsFormattedText(String input, List<String> urls, boolean considerURLs) {
		if (considerURLs) {
			for (String arg : input.split(" "))
				if (!urls.contains(arg) && !ChatColor.stripColor(ChatColor.translate(arg)).equals(arg))
					return true;
			return false;
		} return !ChatColor.stripColor(ChatColor.translate(input)).equals(input);
	}
	
	@Override
	public String translate(String input, List<String> urls, boolean considerURLs) {
		if (considerURLs) {
			String[] args = input.split(" ");
			input = "";
			
			for (int i = 0; i < args.length; i++)
				input += (urls.contains(args[i]) ? args[i] : ChatColor.translate(args[i])) + " ";
			return input.trim();
		} return ChatColor.translate(input);
	}
	
}
