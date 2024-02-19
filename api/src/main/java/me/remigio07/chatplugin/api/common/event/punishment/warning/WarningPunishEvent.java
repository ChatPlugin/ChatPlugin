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

package me.remigio07.chatplugin.api.common.event.punishment.warning;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents the event called after a player
 * gets punished because of a warning.
 * 
 * @see WarningManager#warn(OfflinePlayer, String, String, String, long, boolean, boolean)
 * @see WarningManager#isAmountPunished(int)
 * @see WarningManager#getPunishCommands(int, Language)
 */
@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
public class WarningPunishEvent extends WarningEvent {
	
	/**
	 * Constructs a new warning punish event.
	 * 
	 * @param warning Warning involved
	 */
	public WarningPunishEvent(Warning warning) {
		super(warning);
	}
	
}
