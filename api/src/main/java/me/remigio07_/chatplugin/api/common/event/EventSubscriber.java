/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.common.event;

import java.util.function.Consumer;

/**
 * Represents an event subscriber containing an event's
 * class and a consumer to execute when the event is fired.
 * 
 * @param <E> Event's class
 * @see EventManager
 */
public class EventSubscriber<E extends ChatPluginEvent> {
	
	private Class<E> event;
	private Consumer<E> consumer;
	
	/**
	 * Constructs a new event subscriber.
	 * 
	 * @deprecated Internal use only. Use {@link EventManager#subscribe(Class, Consumer)} to obtain instances of this class.
	 * @param event Event's class
	 * @param consumer Consumer to execute
	 */
	@Deprecated
	public EventSubscriber(Class<E> event, Consumer<E> consumer) {
		this.event = event;
		this.consumer = consumer;
	}
	
	/**
	 * Gets this subscriber's event's class.
	 * 
	 * @return Subscriber's event's class
	 */
	public Class<E> getEvent() {
		return event;
	}
	
	/**
	 * Gets this subscriber's consumer.
	 * 
	 * @return Subscriber's consumer
	 */
	public Consumer<E> getConsumer() {
		return consumer;
	}
	
	/**
	 * Unsubscribe this event subscriber using
	 * {@link EventManager#unsubscribe(EventSubscriber)}.
	 */
	public void unsubscribe() {
		EventManager.getInstance().unsubscribe(this);
	}
	
}
