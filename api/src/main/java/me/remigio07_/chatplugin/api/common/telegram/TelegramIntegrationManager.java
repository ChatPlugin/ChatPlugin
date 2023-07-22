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

package me.remigio07_.chatplugin.api.common.telegram;

import me.remigio07_.chatplugin.api.common.util.Library;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;

public abstract class TelegramIntegrationManager implements ChatPluginManager {
	
	public static final Library[] LIBRARIES = new Library[] { Library.GUAVA, Library.ERROR_PRONE_ANNOTATIONS, Library.J2OBJC_ANNOTATIONS, Library.CHECKER_QUAL, Library.JAVAX_ANNOTATION, Library.SLF4J_API, Library.JACKSON_ANNOTATIONS, Library.JACKSON_CORE, Library.JACKSON_DATABIND, Library.TELEGRAM_BOTS, Library.TELEGRAM_BOTS_META };
	protected static TelegramIntegrationManager instance;
	protected boolean enabled;
	
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public abstract int reload(String whoReloaded);
	
	public abstract String getTelegramBotsVersion();
	
}
