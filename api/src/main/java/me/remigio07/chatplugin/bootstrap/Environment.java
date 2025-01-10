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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the four server/proxy environments supported by ChatPlugin:
 * Bukkit, Sponge, BungeeCord and Velocity.
 */
public enum Environment {
	
	/**
	 * Bukkit environment.
	 * 
	 * <p><code>enable(...)</code> method's arguments' types:
	 * {@link java.util.logging.Logger}, {@link java.io.File}</p>
	 */
	BUKKIT("Bukkit", new String[] { "java.util.logging.Logger", "java.io.File" }),
	
	/**
	 * Sponge environment.
	 * 
	 * <p><code>enable(...)</code> method's arguments' types:
	 * {@link org.slf4j.Logger}, {@link java.nio.file.Path}</p>
	 */
	SPONGE("Sponge", new String[] { "org.slf4j.Logger", "java.nio.file.Path" }),
	
	/**
	 * BungeeCord environment.
	 * 
	 * <p><code>enable(...)</code> method's arguments' types:
	 * {@link java.util.logging.Logger}, {@link java.io.File}</p>
	 */
	BUNGEECORD("BungeeCord", BUKKIT.getEnableMethodArgsTypes()),
	
	/**
	 * Velocity environment.
	 * 
	 * <p><code>enable(...)</code> method's arguments' types:
	 * {@link com.velocitypowered.api.proxy.ProxyServer}, {@link org.slf4j.Logger}, {@link java.nio.file.Path}</p>
	 */
	VELOCITY("Velocity", new String[] { "com.velocitypowered.api.proxy.ProxyServer", "org.slf4j.Logger", "java.nio.file.Path" });
	
	static Environment currentEnvironment;
	private String name;
	private String[] enableMethodArgsTypes;
	
	private Environment(String name, String[] enableMethodArgsTypes) {
		this.name = name;
		this.enableMethodArgsTypes = enableMethodArgsTypes;
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
	 * Gets the arguments' types used by the
	 * implementation's <code>enable(...)</code> method.
	 * 
	 * @return Enable method's arguments' types
	 */
	public String[] getEnableMethodArgsTypes() {
		return enableMethodArgsTypes;
	}
	
	/**
	 * Gets the JAR in JAR's classpaths that should not be
	 * loaded when running ChatPlugin on this environment.
	 * 
	 * @return Excluded classpaths
	 */
	public List<String> getExcludedClasspaths() { // I love streams
		return Stream.of(
				Arrays.asList(isProxy() ? "me.remigio07.chatplugin.server" : "me.remigio07.chatplugin.proxy", "me.remigio07.chatplugin.common.discord"),
				Arrays.asList(Environment.values())
				.stream()
				.filter(environment -> this != environment)
				.map(environment -> "me.remigio07.chatplugin." + (isProxy() ? "proxy" : "server") + "." + environment.name().toLowerCase())
				.collect(Collectors.toList())
				).flatMap(List::stream)
				.collect(Collectors.toList());
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
	 * Sets the current environment.
	 * 
	 * @deprecated Internal use only.
	 * @param environment Current environment
	 */
	@Deprecated
	public static void setDCurrent(Environment environment) {
		currentEnvironment = environment;
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