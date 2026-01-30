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

package me.remigio07.chatplugin.api.server.tablist;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.tablist.TablistSendEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link Tablist}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Tablists">ChatPlugin wiki/Modules/Tablists</a>
 * @see CustomSuffixManager
 */
public abstract class TablistManager implements ChatPluginManager, Runnable {
	
	/**
	 * Pattern representing the allowed tablist IDs.
	 * 
	 * <p><strong>Regex:</strong> <a href="https://regex101.com/r/9iSnkI/1"><code>^[a-zA-Z0-9-_]{2,36}$</code></a></p>
	 * 
	 * @see #isValidTablistID(String)
	 */
	public static final Pattern TABLIST_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static TablistManager instance;
	protected boolean enabled, randomOrder, playerNamesTeamsMode;
	protected long sendingTimeout, playerNamesUpdateTimeout, timerTaskID = -1, playerNamesTimerTaskID = -1;
	protected String playerNamesPrefix, playerNamesSuffix;
	protected Set<PlaceholderType> placeholderTypes = Collections.emptySet();
	protected List<Tablist> tablists = new CopyOnWriteArrayList<>();
	protected Runnable playerNamesUpdater;
	protected int timerIndex = -1;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.enabled" in {@link ConfigurationType#TABLISTS}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the tablists should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.random-order" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Checks if teams' prefixes, suffixes and name
	 * colors should be used to display player names.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.player-names.teams-mode" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Whether the teams mode is enabled for player names
	 */
	public boolean isPlayerNamesTeamsMode() {
		return playerNamesTeamsMode;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.sending-timeout-ms" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Gets the timeout between player names' updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.player-names.update-timeout-ms" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Time between player names' updates
	 */
	public long getPlayerNamesUpdateTimeout() {
		return playerNamesUpdateTimeout;
	}
	
	/**
	 * Gets the format of the prefix prepended to players' names in the tablist.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.player-names.prefix" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Player names' prefix
	 */
	public String getPlayerNamesPrefix() {
		return playerNamesPrefix;
	}
	
	/**
	 * Gets the format of the suffix appended to players' names in the tablist.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.player-names.suffix" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Player names' suffix
	 */
	public String getPlayerNamesSuffix() {
		return playerNamesSuffix;
	}
	
	/**
	 * Gets the set of placeholder types used to translate
	 * {@link Tablist#getHeaders()} and {@link Tablist#getFooters()}.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.placeholder-types" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Placeholders used to translate texts
	 */
	public Set<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded tablists.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Loaded tablists' list
	 */
	public List<Tablist> getTablists() {
		return tablists;
	}
	
	/**
	 * Gets a tablist from {@link #getTablists()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the tablist is not loaded.</p>
	 * 
	 * @param id Tablist's ID, case insensitive
	 * @return Loaded tablist
	 */
	@Nullable(why = "Specified tablist may not be loaded")
	public Tablist getTablist(String id) {
		return tablists.stream().filter(tablist -> tablist.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the automatic player names' updater, called
	 * once every {@link #getPlayerNamesUpdateTimeout()} ms.
	 * 
	 * @return Player names' updater
	 */
	public Runnable getPlayerNamesUpdater() {
		return playerNamesUpdater;
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
	 * Gets the  {@link #getPlayerNamesUpdater()}'s timer's task's ID.
	 * 
	 * <p>You can interact with it using {@link TaskManager}'s methods.</p>
	 * 
	 * @return Player names' update task's ID
	 */
	public long getPlayerNamesTimerTaskID() {
		return playerNamesTimerTaskID;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's index of {@link #getTablists()}.
	 * 
	 * @return Timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Checks if the specified String is a valid tablist ID.
	 * 
	 * @param tablistID Tablist ID to check
	 * @return Whether the specified tablist ID is valid
	 * @see #TABLIST_ID_PATTERN
	 */
	public boolean isValidTablistID(String tablistID) {
		return TABLIST_ID_PATTERN.matcher(tablistID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static TablistManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic tablist sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sends a tablist to a loaded player.
	 * 
	 * @param tablist Tablist to send
	 * @param player Player to send the tablist to
	 * @see TablistSendEvent
	 */
	public abstract void sendTablist(Tablist tablist, ChatPluginServerPlayer player);
	
}
