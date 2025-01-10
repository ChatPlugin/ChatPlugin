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

package me.remigio07.chatplugin.common.punishment.ban;

import java.net.InetAddress;
import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.language.Language;

public class DummyBanManager extends BanManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void ban(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void banIP(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void banIP(InetAddress ipAddress, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void unban(OfflinePlayer player, @Nullable(why = "Null to disactive a global ban") String server, @NotNull String whoUnbanned) {
		
	}
	
	@Override
	public void unbanIP(InetAddress ipAddress, @Nullable(why = "Null to disactive a global ban") String server, @NotNull String whoUnbanned) {
		
	}
	
	@Override
	public void unban(int id, @NotNull String whoUnbanned) {
		
	}
	
	@Override
	public @NotNull List<Ban> getActiveBans(OfflinePlayer player) {
		return null;
	}
	
	@Override
	public @NotNull List<Ban> getActiveBans(InetAddress ipAddress) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Player may not be banned in the specified server") Ban getActiveBan(OfflinePlayer player, @Nullable(why = "Null to check global bans") String server) {
		return null;
	}
	
	@Override
	public @Nullable(why = "IP address may not be banned in the specified server") Ban getActiveBan(InetAddress ipAddress, @Nullable(why = "Null to check global bans") String server) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified ban may not be active") Ban getActiveBan(int id) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Specified ban may not exist") Ban getBan(int id) {
		return null;
	}
	
	@Override
	public @NotNull String formatTypeMessage(BanType type, Language language) {
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
	public @NotNull String formatKickMessage(KickType type, Language language) {
		return null;
	}
	
	@Override
	public @NotNull String formatReason(@Nullable(why = "Reason may not have been specified") String reason, Language language) {
		return null;
	}
	
}
