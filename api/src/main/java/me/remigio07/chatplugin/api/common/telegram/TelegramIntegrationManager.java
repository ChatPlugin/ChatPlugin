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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.telegram;

import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles the plugin's {@link TelegramBot}. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Telegram-integration">ChatPlugin wiki/Telegram integration</a>
 */
public abstract class TelegramIntegrationManager implements ChatPluginManager {
	
	/**
	 * Array containing all the libraries required for this module to work.
	 * 
	 * <p><strong>Content:</strong> [
	 * {@link Library#GUAVA}, {@link Library#ERROR_PRONE_ANNOTATIONS}, {@link Library#J2OBJC_ANNOTATIONS}, {@link Library#CHECKER_QUAL},
	 * {@link Library#JAVAX_ANNOTATION}, {@link Library#SLF4J_API}, {@link Library#JACKSON_ANNOTATIONS}, {@link Library#JACKSON_CORE},
	 * {@link Library#JACKSON_DATABIND}, {@link Library#TELEGRAM_BOTS}, {@link Library#TELEGRAM_BOTS_META}]</p>
	 */
	public static final Library[] LIBRARIES = new Library[] { Library.GUAVA, Library.ERROR_PRONE_ANNOTATIONS, Library.J2OBJC_ANNOTATIONS, Library.CHECKER_QUAL, Library.JAVAX_ANNOTATION, Library.SLF4J_API, Library.JACKSON_ANNOTATIONS, Library.JACKSON_CORE, Library.JACKSON_DATABIND, Library.TELEGRAM_BOTS, Library.TELEGRAM_BOTS_META };
	protected static TelegramIntegrationManager instance;
	protected boolean enabled;
	
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Reloads the bot.
	 * You can specify <code>null</code> as <code>whoReloaded</code> to not make
	 * appear in the logs the following message: <code>"User " + whoReloaded
	 * + " has reloaded the Telegram integration through the bot."</code>
	 * 
	 * @param whoReloaded Who reloaded the bot
	 * @return Time elapsed, in milliseconds
	 */
	public abstract int reload(@Nullable(why = "User will not show up in logs if null") String whoReloaded);
	
	/**
	 * Gets the Telegram Bots' version.
	 * 
	 * @return Telegram Bots' version
	 */
	public abstract String getTelegramBotsVersion();
	
}
