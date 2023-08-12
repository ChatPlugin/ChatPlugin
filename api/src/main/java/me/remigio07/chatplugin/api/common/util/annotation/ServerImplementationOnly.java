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
 * server implementations only: Bukkit and Sponge (plus their forks).
 */
@Target({ METHOD, TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface ServerImplementationOnly {
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "You cannot interact with languages on the proxy"</p>
	 */
	public static final String NO_LANGUAGES = "You cannot interact with languages on the proxy";
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "This plugin message may be sent by servers only"</p>
	 */
	public static final String NO_PLUGIN_MESSAGE = "This plugin message may be sent by servers only";
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "Settings not present on the proxy and different on every server"</p>
	 */
	public static final String SETTINGS_NOT_PRESENT = "Settings not present on the proxy and different on every server";
	
	/**
	 * String useful to avoid text repetition in each annotation.
	 * 
	 * <p><strong>Value:</strong> "Minecraft Vanilla game features and mechanics are not handled by the proxy"</p>
	 */
	public static final String GAME_FEATURE = "Minecraft Vanilla game features and mechanics are not handled by the proxy";
	
	/**
	 * Short explanation of why the method is
	 * only available on server implementations.
	 * 
	 * @return Reason why
	 */
	public String why();
	
}
