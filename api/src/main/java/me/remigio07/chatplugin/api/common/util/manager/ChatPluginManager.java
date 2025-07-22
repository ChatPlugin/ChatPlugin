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

package me.remigio07.chatplugin.api.common.util.manager;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Interface that represents one of the managers used by ChatPlugin.
 */
public interface ChatPluginManager {
	
	/**
	 * Loads (or reloads) this manager.
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public void load() throws ChatPluginManagerException;
	
	/**
	 * Unloads this manager.
	 * 
	 * <p>Will do nothing if this method is not overridden.</p>
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public default void unload() throws ChatPluginManagerException {
		
	}
	
	/**
	 * Reloads this manager.
	 * 
	 * <p>Will call {@link #unload()} and then
	 * {@link #load()} if not overridden.</p>
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public default void reload() throws ChatPluginManagerException {
		unload();
		load();
	}
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * @return Whether this manager is enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Checks if this manager will be reloaded on a plugin reload.
	 * 
	 * <p>Will return <code>true</code> if this method is not overridden.</p>
	 * 
	 * @return Whether this manager is reloadable
	 */
	public default boolean isReloadable() {
		return true;
	}
	
	/**
	 * Checks if this manager specifies a {@link GameFeature} annotation.
	 * 
	 * <p>In that case, if <code>{@link Environment#isProxy()} == false</code>
	 * ({@link GameFeature}s are not used on proxies), the feature's
	 * availability on the current environment is checked through three steps:
	 * 	<ol>
	 * 		<li>environment compatibility - whether the feature is available on Bukkit/Sponge</li>
	 * 		<li>Spigot requirement - whether the feature requires Spigot or a fork; Bukkit only</li>
	 * 		<li>Paper requirement - whether the feature requires Paper or a fork; Bukkit only</li>
	 * 		<li>minimum version - the minimum Vanilla version required to run the feature</li>
	 * 	</ol>
	 * 
	 * @param warnIfUnavailable Whether to send a message if the feature is unavailable
	 * @return Whether this manager's features may run on {@link Environment#getCurrent()}
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.GAME_FEATURE)
	public default boolean checkAvailability(boolean warnIfUnavailable) {
		GameFeature gameFeature;
		Class<?> clazz = getClass();
		
		do {
			if ((gameFeature = clazz.getAnnotation(GameFeature.class)) != null)
				break;
		} while ((clazz = clazz.getSuperclass()) != null);
		
		if (gameFeature == null)
			gameFeature = checkInterfaces(getClass());
		if (Environment.isProxy() || gameFeature == null)
			return true;
		String str = null;
		
		if (Environment.isBukkit()) {
			if (!gameFeature.availableOnBukkit())
				str = "Bukkit cannot";
			else if (gameFeature.spigotRequired() && !VersionUtils.isSpigot())
				str = "Spigot is required to";
			else if (gameFeature.paperRequired() && !VersionUtils.isPaper())
				str = "Paper is required to";
			else if (VersionUtils.getVersion().ordinal() < gameFeature.minimumBukkitVersion().ordinal())
				str = "At least Minecraft " + gameFeature.minimumBukkitVersion().toString() + " is required to";
		} else {
			if (!gameFeature.availableOnSponge())
				str = "Sponge cannot";
			else if (VersionUtils.getVersion().ordinal() < gameFeature.minimumSpongeVersion().ordinal())
				str = "At least Minecraft " + gameFeature.minimumSpongeVersion().toString() + " is required to";
		} if (str != null && warnIfUnavailable)
			LogManager.log(str + " run the " + gameFeature.name() + " module; disabling feature...", 1);
		return str == null;
	}
	
	static GameFeature checkInterfaces(Class<?> clazz) {
		for (Class<?> clazz2 : clazz.getInterfaces()) {
			GameFeature feature = clazz2.getAnnotation(GameFeature.class);
			
			if (feature != null)
				return feature;
			else if (clazz2.isInterface() && (feature = checkInterfaces(clazz2)) != null)
				return feature;
		} return null;
	}
	
}
