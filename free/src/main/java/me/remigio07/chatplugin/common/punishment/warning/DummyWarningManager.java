/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.common.punishment.warning;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.language.Language;

public class DummyWarningManager extends WarningManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void warn(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void unwarn(int id, @NotNull String whoUnwarned) {
		
	}
	
	@Override
	public void removeLastWarning(OfflinePlayer player, @Nullable(why = "Null to disactive a global warning") String server, @NotNull String whoUnwarned) {
		
	}
	
	@Override
	public void clearWarnings(OfflinePlayer player, @Nullable(why = "Null to disactive global warnings") String server, @NotNull String whoUnwarned) {
		
	}
	
	@Override
	public @NotNull List<Warning> getActiveWarnings(OfflinePlayer player) {
		return null;
	}
	
	@Override
	public @NotNull List<Warning> getActiveWarnings(OfflinePlayer player,
			@Nullable(why = "Null to check global warnings") String server) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified warning may not be active") Warning getActiveWarning(int id) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified warning may not exist") Warning getWarning(int id) {
		return null;
	}
	
	@Override
	public @NotNull List<String> getPunishCommands(int warnings, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatActiveMessage(boolean active, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatGlobalMessage(boolean global, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatSilentMessage(boolean silent, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatReason(@Nullable(why = "Reason may not have been specified") String reason, Language language) {
		return null;
	}
	
}
