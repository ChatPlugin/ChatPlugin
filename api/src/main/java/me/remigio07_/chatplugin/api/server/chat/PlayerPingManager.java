/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.server.chat;

import java.util.List;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Manager that handles player pings in the chat. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Chat/player-pings">ChatPlugin wiki/Chat/Player pings</a>
 * 
 * @see me.remigio07_.chatplugin.api.server.chat Chat-related managers
 */
public abstract class PlayerPingManager implements ChatPluginManager {
	
	protected static PlayerPingManager instance;
	protected boolean enabled, soundEnabled;
	protected String color;
	protected SoundAdapter sound;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if players should hear a sound when pinged.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.sound.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether players should hear a sound when pinged
	 */
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	/**
	 * Gets the color pings in chat will have.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.color" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Pings' color
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * Gets the sound that pings will produce.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.sound" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Pings' sound
	 */
	public SoundAdapter getSound() {
		return sound;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PlayerPingManager getInstance() {
		return instance;
	}
	
	/**
	 * Pings every loaded player contained in <code>message</code> and
	 * returns the message with their names colored using {@link #getColor()}.
	 * Will do nothing if the player does not have the permission "chatplugin.player-ping".
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @return Message adjusted with color
	 */
	public abstract String performPing(ChatPluginServerPlayer player, String message);
	
	/**
	 * Gets the list of every loaded player contained in <code>message</code>.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @return Pinged players' list
	 */
	public abstract List<ChatPluginServerPlayer> getPingedPlayers(ChatPluginServerPlayer player, String message);
	
	/**
	 * Plays {@link #getSound()} to the specified certain player. This
	 * will have no effect if <code>!</code>{@link #isSoundEnabled()}.
	 * 
	 * @param player Player to play the sound to
	 */
	public abstract void playPingSound(ChatPluginServerPlayer player);
	
}
