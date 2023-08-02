/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.server.join_quit;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.server.join_quit.AccountCheckManager;

public class DummyAccountCheckManager extends AccountCheckManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void check(OfflinePlayer player) {
		
	}
	
	@Override
	public CompletableFuture<List<OfflinePlayer>> getAccounts(OfflinePlayer player, long maxTimePlayed, boolean useIPLookup) {
		return null;
	}
	
}
