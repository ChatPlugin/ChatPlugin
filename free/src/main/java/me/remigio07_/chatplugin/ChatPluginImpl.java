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

package me.remigio07_.chatplugin;

import java.io.File;
import java.nio.file.Path;

import me.remigio07_.chatplugin.bootstrap.Environment;
import me.remigio07_.chatplugin.server.bukkit.ChatPluginBukkit;
import me.remigio07_.chatplugin.server.sponge.ChatPluginSponge;

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
