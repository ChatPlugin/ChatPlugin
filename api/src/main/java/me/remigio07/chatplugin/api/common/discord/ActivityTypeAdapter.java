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

package me.remigio07.chatplugin.api.common.discord;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents an activity's type. This is an adapter of <code>net.dv8tion.jda.api.entities.Activity.ActivityType</code>.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class ActivityTypeAdapter {
	
	/**
	 * Used to indicate that the activity should display as "Competing in...".
	 */
	public static final ActivityTypeAdapter COMPETING = new ActivityTypeAdapter("COMPETING");
	
	/**
	 * Used to indicate that the activity should display as a custom status.
	 * 
	 * <p><strong>Note:</strong> this activity type is read only for bots.</p>
	 */
	public static final ActivityTypeAdapter CUSTOM_STATUS = new ActivityTypeAdapter("CUSTOM_STATUS");
	
	/**
	 * Used to indicate that the activity should display as a normal message.
	 */
	public static final ActivityTypeAdapter DEFAULT = new ActivityTypeAdapter("DEFAULT");
	
	/**
	 * Used to indicate that the activity should display as "Listening...".
	 */
	public static final ActivityTypeAdapter LISTENING = new ActivityTypeAdapter("LISTENING");
	
	/**
	 * Used to indicate that the activity should display as "Streaming...".
	 */
	public static final ActivityTypeAdapter STREAMING = new ActivityTypeAdapter("STREAMING");
	
	/**
	 * Used to indicate that the activity should display as "Watching...".
	 * 
	 * <p><strong>Note:</strong> this activity type is not confirmed for the official API yet.</p>
	 */
	public static final ActivityTypeAdapter WATCHING = new ActivityTypeAdapter("WATCHING");
	private static final ActivityTypeAdapter[] VALUES = new ActivityTypeAdapter[] { COMPETING, CUSTOM_STATUS, DEFAULT, LISTENING, STREAMING, WATCHING };
	private String name;
	
	private ActivityTypeAdapter(String name) {
		this.name = name;
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * @return Constant's ordinal
	 */
	public int ordinal() {
		for (int i = 0; i < VALUES.length; i++)
			if (this == VALUES[i])
				return i;
		return -1;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static ActivityTypeAdapter valueOf(String name) {
		for (ActivityTypeAdapter activityType : VALUES)
			if (activityType.name().equals(name))
				return activityType;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static ActivityTypeAdapter[] values() {
		return VALUES;
	}
	
}
