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

package me.remigio07.chatplugin.server.language;

import java.io.File;
import java.util.List;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.language.Language;

public class LanguageImpl extends Language {
	
	public LanguageImpl(String id, String displayName, List<String> countryCodes) {
		super(id, displayName, countryCodes);
		configuration = id.equals("english") ? ConfigurationType.MESSAGES.get() : new Configuration(ChatPlugin.getInstance().getDataFolder().resolve("custom-messages" + File.separator + "messages-" + id + ".yml"));
	}
	
}
