/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.common.util.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a method contained in a class inside
 * of the {@link me.remigio07.chatplugin.api.common} (or implementation's
 * <code>me.remigio07.chatplugin.common</code>) package is available on
 * proxy implementations only: BungeeCord and Velocity (plus their forks).
 */
@Target({ METHOD, TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface ProxyImplementationOnly {
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "This plugin message may be received by the proxy only"</p>
	 */
	public static final String NO_PLUGIN_MESSAGE = "This plugin message may be received by the proxy only";
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "Settings not present on the servers' implementations"</p>
	 */
	public static final String SETTINGS_NOT_PRESENT = "Settings not present on the servers' implementations";
	
	/**
	 * Short explanation of why the method is
	 * only available on proxy implementations.
	 * 
	 * @return Reason why
	 */
	public String why();
	
}
