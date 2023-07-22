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

package me.remigio07_.chatplugin.server.scoreboard;

import java.util.List;

import me.remigio07_.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07_.chatplugin.api.server.scoreboard.Scoreboard.Builder;
import me.remigio07_.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;

public class DummyScoreboardManager extends ScoreboardManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public Builder createScoreboardBuilder(String id, boolean displayOnlyOneNumberEnabled, int displayOnlyOneNumberValue, List<PlaceholderType> placeholderTypes) {
		return null;
	}
	
	@Override
	public Scoreboard createScoreboard(Configuration configuration) {
		return null;
	}
	
}
