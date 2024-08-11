/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.server.actionbar;

import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.actionbar.ActionbarSendEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link Actionbar}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Actionbars">ChatPlugin wiki/Modules/Actionbars</a>
 */
@GameFeature(
		name = "actionbar",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		minimumBukkitVersion = Version.V1_8,
		minimumSpongeVersion = Version.V1_11
		)
public abstract class ActionbarManager extends TimerTask implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed actionbar IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidActionbarID(String)
	 */
	public static final Pattern ACTIONBAR_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static ActionbarManager instance;
	protected boolean enabled, randomOrder, hasPrefix;
	protected String prefix;
	protected long sendingTimeout, timerTaskID = -1;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<Actionbar> actionbars = new CopyOnWriteArrayList<>();
	protected int timerIndex = -1;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.enabled" in {@link ConfigurationType#ACTIONBARS}</p>
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the actionbars should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.random-order" in {@link ConfigurationType#ACTIONBARS}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Checks if {@link #getPrefix()} should be applied to actionbars.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.prefix.enabled" in {@link ConfigurationType#ACTIONBARS}</p>
	 * 
	 * @return Whether to use prefixes
	 */
	public boolean hasPrefix() {
		return hasPrefix;
	}
	
	/**
	 * Gets the actionbars' prefix.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.prefix.format" in {@link ConfigurationType#ACTIONBARS}</p>
	 * 
	 * @return Actionbars' prefix
	 * @see #hasPrefix()
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.sending-timeout-ms" in {@link ConfigurationType#ACTIONBARS}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link Actionbar#getTexts()}.
	 * 
	 * <p><strong>Found at:</strong> "actionbars.settings.placeholder-types" in {@link ConfigurationType#ACTIONBARS}</p>
	 * 
	 * @return Placeholders used to translate texts
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded actionbars.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Loaded actionbars' list
	 */
	public List<Actionbar> getActionbars() {
		return actionbars;
	}
	
	/**
	 * Gets an actionbar from {@link #getActionbars()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the actionbar is not loaded.</p>
	 * 
	 * @param id Actionbar's ID, case insensitive
	 * @return Loaded actionbar
	 */
	@Nullable(why = "Specified actionbar may not be loaded")
	public Actionbar getActionbar(String id) {
		return actionbars.stream().filter(actionbar -> actionbar.getID().equalsIgnoreCase(id)).findAny().orElse(null);
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
	 * Gets the {@link #run()}'s timer's index of {@link #getActionbars()}.
	 * 
	 * @return Timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Checks if the specified String is a valid actionbar ID.
	 * 
	 * @param actionbarID Actionbar ID to check
	 * @return Whether the specified actionbar ID is valid
	 * @see #ACTIONBAR_ID_PATTERN
	 */
	public boolean isValidActionbarID(String actionbarID) {
		return ACTIONBAR_ID_PATTERN.matcher(actionbarID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ActionbarManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic actionbar sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sends an actionbar to a loaded player.
	 * 
	 * <p>It will not be sent if {@link Actionbar#isHidden()}.</p>
	 * 
	 * @param actionbar Actionbar to send
	 * @param player Player to send the actionbar to
	 * @see ActionbarSendEvent
	 */
	public abstract void sendActionbar(Actionbar actionbar, ChatPluginServerPlayer player);
	
}
