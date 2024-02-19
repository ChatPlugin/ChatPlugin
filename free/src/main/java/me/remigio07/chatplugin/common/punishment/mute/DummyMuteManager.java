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

package me.remigio07.chatplugin.common.punishment.mute;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.language.Language;

public class DummyMuteManager extends MuteManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void mute(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void unmute(OfflinePlayer player, @Nullable(why = "Null to disactive a global mute") String server, @NotNull String whoUnmuted) {
		
	}
	
	@Override
	public void unmute(int id, @NotNull String whoUnmuted) {
		
	}
	
	@Override
	public @NotNull List<Mute> getActiveMutes(OfflinePlayer player) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Player may not be muted in the specified server") Mute getActiveMute(OfflinePlayer player, @Nullable(why = "Null to check global mutes") String server) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified mute may not be active") Mute getActiveMute(int id) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified mute may not exist") Mute getMute(int id) {
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
