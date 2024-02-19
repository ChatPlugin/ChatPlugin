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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

public class PlayerPingManagerImpl extends PlayerPingManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.player-ping.enabled"))
			return;
		soundEnabled = ConfigurationType.CHAT.get().getBoolean("chat.player-ping.sound.enabled");
		color = ConfigurationType.CHAT.get().getString("chat.player-ping.color");
		sound = new SoundAdapter(
				ConfigurationType.CHAT.get().getString("chat.player-ping.sound.id"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.volume"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.pitch")
				);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = soundEnabled = false;
		color = null;
		sound = null;
	}
	
	@Override
	public String performPing(ChatPluginServerPlayer player, String message) {
		if (enabled && player.hasPermission("chatplugin.player-ping")) {
			for (ChatPluginServerPlayer pinged : getPingedPlayers(player, message)) {
				if (!pinged.isVanished()) {
					message = message.replace(pinged.getName(), ChatColor.translate(color) + "@" + pinged.getName() + "\u00A7r");
					
					if (!PlayerIgnoreManager.getInstance().isEnabled() || !pinged.getIgnoredPlayers().contains(player)) {
						pinged.sendTranslatedMessage("chat.pinged", player.getName());
						playPingSound(pinged);
					}
				}
			}
		} return message;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ChatPluginServerPlayer> getPingedPlayers(ChatPluginServerPlayer player, String message) {
		return Arrays.asList(message.split(" "))
				.stream()
				.map(str -> ServerPlayerManager.getInstance().getPlayer(str, false, false))
				.distinct()
				.filter(other -> other != null && other != player)
				.collect(Collectors.toList());
	}
	
	@Override
	public void playPingSound(ChatPluginServerPlayer player) {
		if (enabled && soundEnabled)
			player.playSound(sound);
	}
	
}
