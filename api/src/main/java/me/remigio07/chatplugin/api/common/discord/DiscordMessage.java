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

package me.remigio07.chatplugin.api.common.discord;

import me.remigio07.chatplugin.api.common.discord.DiscordMessages.MessageArguments;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

/**
 * Represents a message type sent by the {@link DiscordBot}.
 * 
 * @see DiscordMessages
 */
public interface DiscordMessage {
	
	/**
	 * Checks if this message is enabled and should be sent.
	 * 
	 * <p>Will return <code>true</code> if nothing is specified in {@link ConfigurationType#DISCORD_INTEGRATION}
	 * at path <code>{@link #getPath()} + "enabled"</code>; example: "messages.ban.banned.enabled".</p>
	 * 
	 * @return Whether this message is enabled
	 */
	public default boolean isEnabled() {
		return ConfigurationType.DISCORD_INTEGRATION.get().getBoolean(getPath() + "enabled", true);
	}
	
	/**
	 * Gets this message's path in {@link ConfigurationType#DISCORD_INTEGRATION}.
	 * 
	 * <p>It includes a dot at the end; example: "messages.ban.banned.".</p>
	 * 
	 * @return Message's path
	 * @throws UnsupportedOperationException If this message has no path
	 */
	public String getPath();
	
//	/**
//	 * Gets this message's plain value. TODO
//	 * 
//	 * @param args Message's specific arguments
//	 * @return Resulting plain message
//	 * @throws UnsupportedOperationException If this message has no path
//	 * @see MessageArguments
//	 */
//	public String getPlain(Object... args);
	
	/**
	 * Gets this message's <code>net.dv8tion.jda.api.entities.MessageEmbed</code> value.
	 * 
	 * <p>This method returns an {@link Object} because libraries'
	 * classes cannot be accessed directly from the API.</p>
	 * 
	 * @param args Message's specific arguments
	 * @return Resulting embed message
	 * @throws UnsupportedOperationException If this message has no path
	 * @see MessageArguments
	 */
	public Object getEmbed(Object... args);
	
}
