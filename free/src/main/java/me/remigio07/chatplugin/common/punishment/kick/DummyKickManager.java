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

package me.remigio07.chatplugin.common.punishment.kick;

import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.language.Language;

public class DummyKickManager extends KickManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void kick(ChatPluginPlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, @NotNull String lobbyServer, KickType type, boolean silent) {
		
	}
	
	@Override
	public @Nullable(why = "Specified kick may not exist") Kick getKick(int id) {
		return null;
	}
	
	@Override
	public @NotNull String formatTypeMessage(KickType type, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatSilentMessage(boolean silent, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatKickMessage(KickType type, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatReason(@Nullable(why = "Reason may not have been specified") String reason, Language language) {
		return null;
	}
	
}
