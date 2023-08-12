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

package me.remigio07.chatplugin.common.util.manager;

import org.slf4j.Logger;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;

public class SLF4JLogManager extends LogManager {
	
	private Logger logger;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		logger = (Logger) ChatPlugin.getInstance().getLogger();
		
		setLoggerType(LoggerType.SLF4J);
		super.load();
		
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		super.unload();
		logger = null;
	}
	
	@Override
	public void logMessage(String message, LogLevel logLevel, Object... args) {
		message = Utils.numericPlaceholders(message, args);
		
		switch (logLevel) {
		case WARNING:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		default:
			if (logLevel.shouldBeSent()) {
				if (logLevel == LogLevel.DEBUG) {
					if (isDebug())
						logger.info(logLevel.getPrefix() + message);
				} else logger.info(message);
			} break;
		} writeToFile(logLevel.getPrefix() + message);
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
}
