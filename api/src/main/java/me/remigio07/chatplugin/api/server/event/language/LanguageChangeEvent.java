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

package me.remigio07.chatplugin.api.server.event.language;

import me.remigio07.chatplugin.api.common.event.player.OfflinePlayerEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;

/**
 * Represents an event called after a player's language is changed.
 * 
 * @see LanguageManager#setLanguage(OfflinePlayer, Language)
 */
public class LanguageChangeEvent implements OfflinePlayerEvent {
	
	private OfflinePlayer player;
	private Language language;
	
	/**
	 * Constructs a new language change event.
	 * 
	 * @param player Player involved
	 * @param language Language involved
	 */
	public LanguageChangeEvent(OfflinePlayer player, Language language) {
		this.player = player;
		this.language = language;
	}
	
	@Override
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the language involved with this event.
	 * 
	 * @return Language involved
	 */
	public Language getLanguage() {
		return language;
	}
	
}
