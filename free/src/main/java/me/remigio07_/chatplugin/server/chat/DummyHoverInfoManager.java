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

package me.remigio07_.chatplugin.server.chat;

import me.remigio07_.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.rank.Rank;

public class DummyHoverInfoManager extends HoverInfoManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public @Nullable(why = "No hover info may be present for the specified language") TextAdapter getRankHoverInfo(Rank rank, Language language, boolean avoidNull) {
		return null;
	}
	
	@Override
	public @Nullable(why = "No hover info may be present for the specified language") TextAdapter getPlayerHoverInfo(ChatPluginServerPlayer player, Language language, boolean avoidNull) {
		return null;
	}
	
	@Override
	public @Nullable(why = "No hover info may be present for the specified language") TextAdapter getURLsHoverInfo(String input, Language language, boolean avoidNull) {
		return null;
	}
	
	@Override
	public TextAdapter getMessageHoverInfo(String message, ChatPluginServerPlayer player, Language language) {
		return null;
	}
	
}
