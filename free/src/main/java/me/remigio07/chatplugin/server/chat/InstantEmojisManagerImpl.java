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

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;

public class InstantEmojisManagerImpl extends InstantEmojisManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.instant-emojis.enabled"))
			return;
		for (String id : ConfigurationType.CHAT.get().getKeys("chat.instant-emojis.values"))
			emojis.put(id, ConfigurationType.CHAT.get().getString("chat.instant-emojis.values." + id).toCharArray()[0]);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		emojis.clear();
	}
	
	@Override
	public String format(String input) {
		String string = input;
		
		for (String emoji : emojis.keySet())
			if (input.contains(":" + emoji + ":"))
				string = string.replace(":" + emoji + ":", String.valueOf(emojis.get(emoji).charValue()));
		return string;
	}
	
}
