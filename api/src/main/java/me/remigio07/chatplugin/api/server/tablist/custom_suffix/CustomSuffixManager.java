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

package me.remigio07.chatplugin.api.server.tablist.custom_suffix;

import java.util.Collections;
import java.util.List;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ValueContainer;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles custom suffixes in the {@link Tablist}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Tablists#custom-suffix">ChatPlugin wiki/Modules/Tablists/Custom suffix</a>
 * @see RenderType
 */
public abstract class CustomSuffixManager implements ChatPluginManager, Runnable {
	
	protected static CustomSuffixManager instance;
	protected boolean enabled;
	protected long updateTimeout, timerTaskID = -1;
	protected ValueContainer<Integer> displayedValue;
	protected RenderType renderType;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.custom-suffix.enabled" in {@link ConfigurationType#TABLISTS}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the timeout between updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.custom-suffix.update-timeout-ms" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Time between updates
	 */
	public long getUpdateTimeout() {
		return updateTimeout;
	}
	
	/**
	 * Gets the displayed value of the custom suffix.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.custom-suffix.displayed-value" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Displayed value of the suffix
	 */
	public ValueContainer<Integer> getDisplayedValue() {
		return displayedValue;
	}
	
	/**
	 * Gets the render type of the custom suffix.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.custom-suffix.render-type" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Render type of the suffix
	 */
	public RenderType getRenderType() {
		return renderType;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link #getDisplayedValue()}.
	 * 
	 * <p><strong>Found at:</strong> "tablists.settings.custom-suffix.placeholder-types" in {@link ConfigurationType#TABLISTS}</p>
	 * 
	 * @return Placeholders used to translate displayed value
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's task's ID.
	 * 
	 * <p>You can interact with it using {@link TaskManager}'s methods.</p>
	 * 
	 * @return Update task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static CustomSuffixManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic custom suffixes updater, called
	 * once every {@link #getUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Updates custom suffixes to a player.
	 * 
	 * @param player Player to update the suffixes for
	 */
	public abstract void updateCustomSuffixes(ChatPluginServerPlayer player);
	
}
