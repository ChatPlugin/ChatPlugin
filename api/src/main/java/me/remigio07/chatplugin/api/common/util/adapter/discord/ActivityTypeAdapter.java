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

package me.remigio07.chatplugin.api.common.util.adapter.discord;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;

/**
 * Represents an activity's type.
 * 
 * <p>This is an adapter of <code>net.dv8tion.jda.api.entities.Activity.ActivityType</code>.</p>
 */
public class ActivityTypeAdapter extends PseudoEnum<ActivityTypeAdapter> {
	
	/**
	 * Used to indicate that the activity should display as "Competing in...".
	 */
	public static final ActivityTypeAdapter COMPETING = new ActivityTypeAdapter("COMPETING");
	
	/**
	 * Used to indicate that the activity should display as a custom status.
	 */
	public static final ActivityTypeAdapter CUSTOM_STATUS = new ActivityTypeAdapter("CUSTOM_STATUS");
	
	/**
	 * Used to indicate that the activity should display as "Listening...".
	 */
	public static final ActivityTypeAdapter LISTENING = new ActivityTypeAdapter("LISTENING");
	
	/**
	 * Used to indicate that the activity should display as "Playing...".
	 */
	public static final ActivityTypeAdapter PLAYING = new ActivityTypeAdapter("PLAYING");
	
	/**
	 * Used to indicate that the activity should display as "Streaming...".
	 */
	public static final ActivityTypeAdapter STREAMING = new ActivityTypeAdapter("STREAMING");
	
	/**
	 * Used to indicate that the activity should display as "Watching...".
	 */
	public static final ActivityTypeAdapter WATCHING = new ActivityTypeAdapter("WATCHING");
	private static final ActivityTypeAdapter[] VALUES = new ActivityTypeAdapter[] { COMPETING, CUSTOM_STATUS, LISTENING, PLAYING, STREAMING, WATCHING };
	private static int ordinal = 0;
	
	private ActivityTypeAdapter(String name) {
		super(name, ordinal++);
	}
	
	/**
	 * Equivalent of <code>valueOf(String)</code>.
	 * 
	 * @param name Constant's name
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 * @throws IllegalArgumentException If {@link #values()}
	 * does not contain a constant with the specified name
	 */
	public static ActivityTypeAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static ActivityTypeAdapter[] values() {
		return VALUES;
	}
	
}
