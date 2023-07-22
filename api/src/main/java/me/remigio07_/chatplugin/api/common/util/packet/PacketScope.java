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

package me.remigio07_.chatplugin.api.common.util.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used in {@link Packets} to indicate a packet's scope.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface PacketScope {
	
	/**
	 * Gets this packet's scope.
	 * 
	 * @return Packet's scope
	 */
	Scope value();
	
	/**
	 * Represents a packet's scope.
	 */
	public static enum Scope {
		
		/**
		 * Represents a packet sent to the proxy by a server.
		 */
		SERVER_TO_PROXY,
		
		/**
		 * Represents a packet sent to a server by the proxy.
		 */
		PROXY_TO_SERVER,
		
		/**
		 * Represents a packet that is either:
		 * 	<ul>
		 * 		<li>sent from a server to the proxy and then redirected to the target server</li>
		 * 		<li>sent from the proxy directly to the target server (no redirection)</li>
		 * 	</ul>
		 * 
		 * If a packet's method is denoted by this scope, you may specify "ALL" as the "server" variable
		 * (the first in most packets) and send it to the proxy to redirect it to every server under the network.
		 */
		SERVER_TO_SERVER;
		
	}
	
}
