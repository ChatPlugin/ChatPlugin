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

package me.remigio07.chatplugin.api.common.util;

import java.util.StringJoiner;

/**
 * Holds information about a server under the proxy.
 */
public class ServerInformation {
	
	private String id;
	private int onlinePlayers, vanishedPlayers;
	private long lastEdit = System.currentTimeMillis();
	
	/**
	 * Constructs an instance for a server under the proxy.
	 * 
	 * @param id Server's ID
	 * @param onlinePlayers Online players' amount
	 * @param vanishedPlayers Vanished players' amount
	 */
	public ServerInformation(String id, int onlinePlayers, int vanishedPlayers) {
		this.id = id;
		this.onlinePlayers = onlinePlayers;
		this.vanishedPlayers = vanishedPlayers;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "ServerInformation{", "}")
				.add("id=\"" + id + "\"")
				.add("onlinePlayers=" + onlinePlayers)
				.add("vanishedPlayers=" + vanishedPlayers)
				.toString();
	}
	
	/**
	 * Gets this server's ID.
	 * 
	 * @return Server's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets the online players in this server.
	 * 
	 * @return Online players' amount
	 */
	public int getOnlinePlayers() {
		return onlinePlayers;
	}
	
	/**
	 * Gets the vanished players in this server.
	 * 
	 * @return Vanished players' amount
	 */
	public int getVanishedPlayers() {
		return vanishedPlayers;
	}
	
	/**
	 * Gets the last edit's time, in milliseconds
	 * 
	 * @return Last edit's time
	 */
	public long getLastEdit() {
		return lastEdit;
	}
	
	/**
	 * Sets the online players in this server.
	 * 
	 * @param onlinePlayers Online players' amount
	 * @return This instance
	 */
	public ServerInformation setOnlinePlayers(int onlinePlayers) {
		this.onlinePlayers = onlinePlayers;
		lastEdit = System.currentTimeMillis();
		return this;
	}
	
	/**
	 * Sets the vanished players in this server.
	 * 
	 * @param vanishedPlayers Vanished players' amount
	 * @return This instance
	 */
	public ServerInformation setVanishedPlayers(int vanishedPlayers) {
		this.vanishedPlayers = vanishedPlayers;
		lastEdit = System.currentTimeMillis();
		return this;
	}
	
}
