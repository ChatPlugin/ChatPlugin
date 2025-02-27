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

package me.remigio07.chatplugin.api.server.scoreboard.event;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Annotation used to provide information about the arguments passed to
 * {@link EventScoreboard#prepareEvent(ChatPluginServerPlayer, Object...)}.
 */
@Target(FIELD)
public @interface EventArguments {
	
	/**
	 * Gets the arguments' types.
	 * 
	 * @return Arguments' types
	 */
	Class<?>[] types();
	
	/**
	 * Gets the arguments' descriptions.
	 * 
	 * @return Arguments' descriptions
	 */
	String[] descriptions();
	
}
