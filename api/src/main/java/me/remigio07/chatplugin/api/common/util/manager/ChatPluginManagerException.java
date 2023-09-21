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

package me.remigio07.chatplugin.api.common.util.manager;

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents an exception thrown by a {@link ChatPluginManager}, which could
 * potentially prevent the plugin from starting or reloading correctly.
 */
public class ChatPluginManagerException extends Exception {
	
	private static final long serialVersionUID = 2161238166368480180L;
	
	/**
	 * Exception's manager.
	 */
	private ChatPluginManager manager;
	
	/**
	 * Exception's source.
	 */
	private String source;
	
	/**
	 * Constructs an exception with the given exception for the specified manager.
	 * 
	 * @param manager The manager's instance
	 * @param exception Exception caught by the manager
	 */
	public ChatPluginManagerException(ChatPluginManager manager, Exception exception) {
		super(exception.getClass().getSimpleName() + " occurred while (un)loading " + manager.getClass().getSimpleName() + ": " + exception.getMessage());
		this.manager = manager;
	}
	
	/**
	 * Constructs an exception with the given error message for the specified manager.
	 * 
	 * @param manager The manager's instance
	 * @param message The error message, starting in lowercase
	 * @param args Optional arguments ({@link Utils#numericPlaceholders(String, Object...)}
	 */
	public ChatPluginManagerException(ChatPluginManager manager, String message, Object... args) {
		super("Error occurred while (un)loading " + manager.getClass().getSimpleName() + ": " + Utils.numericPlaceholders(message, args));
		this.manager = manager;
	}
	
	/**
	 * Constructs an exception with the given exception for the specified source.
	 * 
	 * @deprecated Internal use only. You should declare what manager is throwing this exception.
	 * @param source Exception's source
	 * @param exception Exception caught by the source
	 */
	@Deprecated
	public ChatPluginManagerException(String source, Exception exception) {
		super(exception.getClass().getSimpleName() + " occurred while (un)loading <" + source + ">: " + exception.getMessage());
		this.source = source;
	}
	
	/**
	 * Constructs an exception with the given error message for the specified source.
	 * 
	 * @deprecated Internal use only. You should declare what manager is throwing this exception.
	 * @param source The exception's source
	 * @param message The error message
	 * @param args Optional arguments ({@link Utils#numericPlaceholders(String, Object...)}
	 */
	@Deprecated
	public ChatPluginManagerException(String source, String message, Object... args) {
		super("Error occurred while (un)loading <" + source + ">: " + Utils.numericPlaceholders(message, args));
		this.source = source;
	}
	
	/**
	 * Gets the manager associated with this exception.
	 * Will return <code>null</code> if this exception has
	 * been initialized using the deprecated constructors.
	 * 
	 * @return Exception's manager
	 */
	@Nullable(why = "Exception may have been initialized using third constructor")
	public ChatPluginManager getManager() {
		return manager;
	}
	
	/**
	 * Gets this exception's source's representation.
	 * Will return the manager's name if this exception has
	 * not been initialized using the deprecated constructors.
	 * 
	 * @return Exception's source
	 */
	public String getSource() {
		return source == null ? manager.getClass().getSimpleName() : source;
	}
	
}
