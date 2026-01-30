/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.common.util.manager;

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents an exception thrown by a {@link ChatPluginManager}, which could
 * potentially prevent the plugin from starting or reloading correctly.
 */
public class ChatPluginManagerException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
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
		super((exception.getCause() == null ? exception : exception.getCause()).getClass().getSimpleName() + " occurred while (un)loading " + Utils.getOriginalClass(manager).getSimpleName() + ": " + exception.getLocalizedMessage());
		this.manager = manager;
	}
	
	/**
	 * Constructs an exception with the given error message for the specified manager.
	 * 
	 * @param manager The manager's instance
	 * @param message The error message, starting in lowercase
	 * @param args Optional arguments ({@link Utils#replaceNumericPlaceholders(String, Object...)})
	 */
	public ChatPluginManagerException(ChatPluginManager manager, String message, Object... args) {
		super("Error occurred while (un)loading " + Utils.getOriginalClass(manager).getSimpleName() + ": " + Utils.replaceNumericPlaceholders(message, args));
		this.manager = manager;
	}
	
	/**
	 * Constructs an exception with the given exception for the specified source.
	 * 
	 * @deprecated Internal use only. You should declare which manager is throwing this exception.
	 * @param source Exception's source
	 * @param exception Exception caught by the source
	 */
	@Deprecated
	public ChatPluginManagerException(String source, Exception exception) {
		super((exception.getCause() == null ? exception : exception.getCause()).getClass().getSimpleName() + " occurred while (un)loading <" + source + ">: " + exception.getLocalizedMessage());
		this.source = source;
	}
	
	/**
	 * Constructs an exception with the given error message for the specified source.
	 * 
	 * @deprecated Internal use only. You should declare which manager is throwing this exception.
	 * @param source The exception's source
	 * @param message The error message
	 * @param args Optional arguments ({@link Utils#replaceNumericPlaceholders(String, Object...)})
	 */
	@Deprecated
	public ChatPluginManagerException(String source, String message, Object... args) {
		super("Error occurred while (un)loading <" + source + ">: " + Utils.replaceNumericPlaceholders(message, args));
		this.source = source;
	}
	
	/**
	 * Gets the manager associated with this exception.
	 * 
	 * <p>Will return <code>null</code> if this exception has
	 * been initialized using the deprecated constructors.</p>
	 * 
	 * @return Exception's manager
	 */
	@Nullable(why = "Exception may have been initialized using the deprecated constructors")
	public ChatPluginManager getManager() {
		return manager;
	}
	
	/**
	 * Gets this exception's source's representation.
	 * 
	 * <p>Will return the manager's name if this exception has
	 * not been initialized using the deprecated constructors.</p>
	 * 
	 * @return Exception's source
	 */
	public String getSource() {
		return source == null ? Utils.getOriginalClass(manager).getSimpleName() : source;
	}
	
}
