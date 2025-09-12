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

package me.remigio07.chatplugin.common.punishment.ban.banwave;

import java.net.InetAddress;
import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveEntry;
import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class DummyBanwaveManager extends BanwaveManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void run() {
		
	}
	
	@Override
	public void addEntry(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void addIPEntry(OfflinePlayer player, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void addIPEntry(InetAddress ipAddress, @NotNull String staffMember, @Nullable(why = "Reason may not be specified") String reason, @NotNull String server, long duration, boolean global, boolean silent) {
		
	}
	
	@Override
	public void removeEntry(OfflinePlayer player, @Nullable(why = "Null to disactive a global entry") String server, @NotNull String whoRemoved) {
		
	}
	
	@Override
	public void removeIPEntry(InetAddress ipAddress, @Nullable(why = "Null to disactive a global entry") String server, @NotNull String whoRemoved) {
		
	}
	
	@Override
	public @NotNull List<BanwaveEntry> getEntries(OfflinePlayer player) {
		return null;
	}
	
	@Override
	public @NotNull List<BanwaveEntry> getEntries(InetAddress ipAddress) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Player may not be banned in the specified server") BanwaveEntry getEntry(OfflinePlayer player, @NotNull String server) {
		return null;
	}
	
	@Override
	public @Nullable(why = "IP address may not be banned in the specified server") BanwaveEntry getEntry(InetAddress ipAddress, @NotNull String server) {
		return null;
	}
	
	@Override
	public boolean isBanwaveReason(@Nullable(why = "Reason may not be specified") String reason) {
		return false;
	}
	
}
