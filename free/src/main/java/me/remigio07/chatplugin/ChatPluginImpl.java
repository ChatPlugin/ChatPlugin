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

package me.remigio07.chatplugin;

import java.io.File;
import java.nio.file.Path;

import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkit;
import me.remigio07.chatplugin.server.sponge.ChatPluginSponge;

public class ChatPluginImpl {
	
	public static void load(Object logger, Object dataFolder) {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			new ChatPluginBukkit().load((java.util.logging.Logger) logger, (File) dataFolder);
			break;
		case SPONGE:
			new ChatPluginSponge().load((org.slf4j.Logger) logger, (Path) dataFolder);
			break;
		default:
			throw new UnsupportedOperationException("Unable load the proxy plugin on the free version");
		}
	}
	
	public static void load(Object proxy, Object logger, Object dataFolder) {
		throw new UnsupportedOperationException("Unable load the proxy plugin on the free version");
	}
	
}
