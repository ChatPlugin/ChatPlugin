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

package me.remigio07.chatplugin.api.common.event.discord;

import me.remigio07.chatplugin.api.common.discord.ActivityTypeAdapter;
import me.remigio07.chatplugin.api.common.discord.DiscordBot;
import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;

/**
 * Represents the event called just before the {@link DiscordBot}'s status is changed.
 * 
 * @see DiscordBot#setStatus(ActivityTypeAdapter, String)
 */
public class StatusUpdateEvent implements ChatPluginEvent, CancellableEvent {
	
	private boolean cancelled;
	private ActivityTypeAdapter activityType;
	private String value;
	
	/**
	 * Constructs a new status update event.
	 * 
	 * @param activityType Status' activity's type
	 * @param value Status' value
	 */
	public StatusUpdateEvent(ActivityTypeAdapter activityType, String value) {
		this.activityType = activityType;
		this.value = value;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * Gets the new status' activity's type
	 * 
	 * @return Status' activity's type
	 */
	public ActivityTypeAdapter getActivityType() {
		return activityType;
	}
	
	/**
	 * Gets the new status' value.
	 * 
	 * @return Status' value
	 */
	public String getValue() {
		return value;
	}
	
}
