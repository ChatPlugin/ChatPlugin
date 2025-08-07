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

package me.remigio07.chatplugin.api.server.f3servername;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.f3servername.F3ServerNameSendEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link F3ServerName}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/F3-server-names">ChatPlugin wiki/Modules/F3 server names</a>
 */
@GameFeature(
		name = "F3 server name",
		availableOnBukkit = true,
		availableOnSponge = false,
		spigotRequired = false,
		paperRequired = false,
		minimumBukkitVersion = Version.V1_7_2,
		minimumSpongeVersion = Version.UNSUPPORTED
		)
public abstract class F3ServerNameManager implements ChatPluginManager, Runnable {
	
	/**
	 * Pattern representing the allowed F3 server name IDs.
	 * 
	 * <p><strong>Regex:</strong> <a href="https://regex101.com/r/9iSnkI/1"><code>^[a-zA-Z0-9-_]{2,36}$</code></a></p>
	 * 
	 * @see #isValidF3ServerNameID(String)
	 */
	public static final Pattern F3_SERVER_NAME_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	
	/**
	 * Channel used to send F3 server names to loaded players.
	 * 
	 * <p><strong>Content:</strong> "minecraft:brand" on &gt; 1.12.2, "MC|Brand" otherwise</p>
	 */
	public static final String CHANNEL_ID = VersionUtils.getVersion().getProtocol() > 340 ? "minecraft:brand" : "MC|Brand";
	protected static F3ServerNameManager instance;
	protected boolean enabled, randomOrder;
	protected long sendingTimeout, timerTaskID = -1;
	protected Set<PlaceholderType> placeholderTypes = Collections.emptySet();
	protected List<F3ServerName> f3ServerNames = new CopyOnWriteArrayList<>();
	protected int timerIndex = -1;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "f3-server-names.settings.enabled" in {@link ConfigurationType#F3_SERVER_NAMES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the F3 server names should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "f3-server-names.settings.random-order" in {@link ConfigurationType#F3_SERVER_NAMES}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "f3-server-names.settings.sending-timeout-ms" in {@link ConfigurationType#F3_SERVER_NAMES}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Gets the set of placeholder types used
	 * to translate {@link F3ServerName#getTexts()}.
	 * 
	 * <p><strong>Found at:</strong> "f3-server-names.settings.placeholder-types" in {@link ConfigurationType#F3_SERVER_NAMES}</p>
	 * 
	 * @return Placeholders used to translate texts
	 */
	public Set<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded F3 server names.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Loaded F3 server names' list
	 */
	public List<F3ServerName> getF3ServerNames() {
		return f3ServerNames;
	}
	
	/**
	 * Gets an F3ServerName from {@link #getF3ServerNames()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the F3 server name is not loaded.</p>
	 * 
	 * @param id F3 server name's ID, case insensitive
	 * @return Loaded F3 server name
	 */
	@Nullable(why = "Specified F3 server name may not be loaded")
	public F3ServerName getF3ServerName(String id) {
		return f3ServerNames.stream().filter(f3ServerName -> f3ServerName.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the {@link #run()}'s timer's task's ID.
	 * 
	 * <p>You can interact with it using {@link TaskManager}'s methods.</p>
	 * 
	 * @return Sending task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's index of {@link #getF3ServerNames()}.
	 * 
	 * @return Timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Checks if the specified String is a valid F3 server name ID.
	 * 
	 * @param f3ServerNameID F3 server name ID to check
	 * @return Whether the specified F3 server name ID is valid
	 * @see #F3_SERVER_NAME_ID_PATTERN
	 */
	public boolean isValidF3ServerNameID(String f3ServerNameID) {
		return F3_SERVER_NAME_ID_PATTERN.matcher(f3ServerNameID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static F3ServerNameManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic F3 server name sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sends an F3 server name to a loaded player.
	 * 
	 * @param f3ServerName F3 server name to send
	 * @param player Player to send the F3 server name to
	 * @see F3ServerNameSendEvent
	 */
	public abstract void sendF3ServerName(F3ServerName f3ServerName, ChatPluginServerPlayer player);
	
}
