/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.server.chat.channel;

/**
 * Represents the type of a {@link ChatChannel}.
 */
public enum ChatChannelType {
	
	/**
	 * Local (ranged) channel.
	 */
	LOCAL,
	
	/**
	 * World-based channel.
	 */
	WORLD,
	
	/**
	 * Global (server-wide) channel.
	 */
	GLOBAL,
	
	/**
	 * Network (proxy-wide) channel.
	 */
	NETWORK,
	
	/**
	 * Discord-linked channel.
	 */
	DISCORD,
	
	/**
	 * Telegram-linked channel.
	 */
	TELEGRAM;
	
	/**
	 * Checks if this is a proxy-wide channel.
	 * 
	 * <p>The following will return <code>true</code>:
	 * {@link #NETWORK}, {@link #DISCORD} and {@link #TELEGRAM}</p>
	 * 
	 * @return Whether this is a proxy-wide channel.
	 */
	public boolean isProxyWide() {
		return ordinal() > 2;
	}
	
}
