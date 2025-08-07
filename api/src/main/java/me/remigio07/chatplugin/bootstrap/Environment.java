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

package me.remigio07.chatplugin.bootstrap;

/**
 * Represents the server/proxy environments supported by ChatPlugin.
 */
public enum Environment {
	
	/**
	 * Bukkit environment.
	 */
	BUKKIT("Bukkit", new String[] { "java.util.logging.Logger", "java.io.File" }),
	
	/**
	 * Sponge environment.
	 */
	SPONGE("Sponge", new String[] { "org.slf4j.Logger", "java.nio.file.Path" }),
	
	/**
	 * BungeeCord environment.
	 */
	BUNGEECORD("BungeeCord", BUKKIT.loadMethodArgsTypes),
	
	/**
	 * Velocity environment.
	 */
	VELOCITY("Velocity", new String[] { "com.velocitypowered.api.proxy.ProxyServer", "org.slf4j.Logger", "java.nio.file.Path" });
	
	static Environment currentEnvironment;
	String[] loadMethodArgsTypes;
	private String name;
	
	private Environment(String name, String[] loadMethodArgsTypes) {
		this.name = name;
		this.loadMethodArgsTypes = loadMethodArgsTypes;
	}
	
	/**
	 * Gets this environment's name.
	 * 
	 * @return Environment's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the current environment.
	 * 
	 * @return Current environment
	 */
	public static Environment getCurrent() {
		return currentEnvironment;
	}
	
	/**
	 * Checks if the current environment is Bukkit.
	 * 
	 * @return Whether Bukkit is running
	 */
	public static boolean isBukkit() {
		return currentEnvironment == BUKKIT;
	}
	
	/**
	 * Checks if the current environment is Sponge.
	 * 
	 * @return Whether Sponge is running
	 */
	public static boolean isSponge() {
		return currentEnvironment == SPONGE;
	}
	
	/**
	 * Checks if the current environment is BungeeCord.
	 * 
	 * @return Whether BungeeCord is running
	 */
	public static boolean isBungeeCord() {
		return currentEnvironment == BUNGEECORD;
	}
	
	/**
	 * Checks if the current environment is Velocity.
	 * 
	 * @return Whether Velocity is running
	 */
	public static boolean isVelocity() {
		return currentEnvironment == VELOCITY;
	}
	
	/**
	 * Checks if the current environment is a proxy (BungeeCord or Velocity).
	 * 
	 * @return Whether the current environment is a proxy
	 */
	public static boolean isProxy() {
		return isBungeeCord() || isVelocity();
	}
	
}