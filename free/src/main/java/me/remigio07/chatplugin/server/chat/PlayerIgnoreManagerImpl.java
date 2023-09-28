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

package me.remigio07.chatplugin.server.chat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.event.chat.IgnoreEvent;
import me.remigio07.chatplugin.api.server.event.chat.UnignoreEvent;

public class PlayerIgnoreManagerImpl extends PlayerIgnoreManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.player-ignore.enabled"))
			return;
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
	}
	
	@Override
	public boolean ignore(OfflinePlayer player, OfflinePlayer ignoredPlayer) {
		if (enabled) {
			if (player.equals(ignoredPlayer))
				throw new IllegalArgumentException("The player and the ignored player correspond");
			try {
				String data = StorageConnector.getInstance().getPlayerData(PlayersDataType.IGNORED_PLAYERS, player);
				String ignoredHexID = Integer.toHexString(StorageConnector.getInstance().getPlayerData(PlayersDataType.ID, ignoredPlayer));
				List<String> ids;
				
				if (data != null) {
					ids = Utils.getListFromString(data);
					
					if (ids.contains(ignoredHexID))
						return false;
					if (ids.size() == 25)
						throw new IndexOutOfBoundsException(player.getName() + " is already ignoring 25 players");
				} else ids = new ArrayList<>();
				IgnoreEvent event = new IgnoreEvent(player, ignoredPlayer);
				
				event.call();
				
				if (event.isCancelled())
					return false;
				ids.add(ignoredHexID);
				StorageConnector.getInstance().setPlayerData(PlayersDataType.IGNORED_PLAYERS, player, Utils.getStringFromList(ids, false, false));
				
				if (player.isLoaded())
					player.toServerPlayer().getIgnoredPlayers().add(ignoredPlayer);
				return true;
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while trying to add {1} to {2}'s ignored players' list: {3}", 2, e.getClass().getSimpleName(), ignoredPlayer.getName(), player.getName(), e.getMessage());
			}
		} return false;
	}
	
	@Override
	public boolean unignore(OfflinePlayer player, OfflinePlayer ignoredPlayer) {
		if (enabled) {
			if (player.equals(ignoredPlayer))
				throw new IllegalArgumentException("The player and the ignored player correspond");
			try {
				String data = StorageConnector.getInstance().getPlayerData(PlayersDataType.IGNORED_PLAYERS, player);
				String ignoredHexID = Integer.toHexString(StorageConnector.getInstance().getPlayerData(PlayersDataType.ID, ignoredPlayer));
				
				if (data != null) {
					List<String> ids = Utils.getListFromString(data);
					
					if (ids.contains(ignoredHexID)) {
						UnignoreEvent event = new UnignoreEvent(player, ignoredPlayer);
						
						event.call();
						
						if (event.isCancelled())
							return false;
						ids.remove(ignoredHexID);
						StorageConnector.getInstance().setPlayerData(PlayersDataType.IGNORED_PLAYERS, player, Utils.getStringFromList(ids, false, false));
						
						if (player.isLoaded())
							player.toServerPlayer().getIgnoredPlayers().remove(ignoredPlayer);
						return true;
					}
				}
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while trying to remove {1} from {2}'s ignored players' list: {3}", 2, e.getClass().getSimpleName(), ignoredPlayer.getName(), player.getName(), e.getMessage());
			}
		} return false;
	}
	
	@Override
	public List<OfflinePlayer> getIgnoredPlayers(OfflinePlayer player) {
		if (enabled)
			if (player.isLoaded())
				return player.toServerPlayer().getIgnoredPlayers();
			else try {
				String data = StorageConnector.getInstance().getPlayerData(PlayersDataType.IGNORED_PLAYERS, player);
				
				if (data == null)
					return Collections.emptyList();
				List<OfflinePlayer> ignoredPlayers = new ArrayList<>();
				
				for (String id : Utils.getListFromString(data)) {
					OfflinePlayer ignored = StorageConnector.getInstance().getPlayer(Integer.parseInt(id, 16));
					
					if (ignored != null)
						ignoredPlayers.add(ignored);
				} return ignoredPlayers;
			} catch (SQLException e) {
				LogManager.log("{0} occurred while trying to get {1}'s ignored players' list: {2}", 2, e.getClass().getSimpleName(), player.getName(), e.getMessage());
			}
		return Collections.emptyList();
	}
	
}
