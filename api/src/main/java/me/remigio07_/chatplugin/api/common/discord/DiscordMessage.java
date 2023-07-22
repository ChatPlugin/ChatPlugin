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

package me.remigio07_.chatplugin.api.common.discord;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;

/**
 * Represents a message type sent by the {@link DiscordBot}.
 * 
 * @see DiscordMessages
 */
public interface DiscordMessage {
	
	/**
	 * Checks if this message is enabled and should be sent.
	 * Will return true if nothing is specified in {@link ConfigurationType#DISCORD_INTEGRATION}
	 * at path <code>"{@link #getPath()} + enabled"<code>; example: "messages.ban.info.enabled".
	 * 
	 * @return Whether this message is enabled
	 */
	public default boolean isEnabled() {
		return ConfigurationType.DISCORD_INTEGRATION.get().getBoolean(getPath() + "enabled", true);
	}
	
	/**
	 * Gets this message's path in {@link ConfigurationType#DISCORD_INTEGRATION}.
	 * Includes a dot at the end; example: "messages.ban.info.".
	 * 
	 * @return Message's path
	 * @throws UnsupportedOperationException If this message has no path
	 */
	public String getPath();
	
	/**
	 * Gets this message's <code>net.dv8tion.jda.api.entities.MessageEmbed</code> value. This method
	 * returns an {@link Object} because libraries' classes cannot be accessed directly from the API.
	 * 
	 * @param args Message's specific arguments
	 * @return Resulting embed message
	 * @throws UnsupportedOperationException If this message has no path
	 */
	public Object getEmbed(Object... args);
	
}
