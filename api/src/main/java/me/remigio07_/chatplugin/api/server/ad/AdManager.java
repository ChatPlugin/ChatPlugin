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

package me.remigio07_.chatplugin.api.server.ad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Pattern;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.event.ad.AdSendEvent;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link Ad}s.
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Ads">ChatPlugin wiki/Ads</a>
 */
public abstract class AdManager extends TimerTask implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed ad IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidAdID(String)
	 */
	public static final Pattern AD_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static AdManager instance;
	protected boolean enabled, randomOrder, hasPrefix;
	protected String prefix;
	protected long sendingTimeout, timerTaskID = -1;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<Ad> ads = new ArrayList<>();
	protected int timerIndex = -1;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.enabled" in {@link ConfigurationType#ADS}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the ads should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.random-order" in {@link ConfigurationType#ADS}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Checks if {@link #getPrefix()} should be applied to ads.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.prefix.enabled" in {@link ConfigurationType#ADS}</p>
	 * 
	 * @return Whether to use prefixes
	 */
	public boolean hasPrefix() {
		return hasPrefix;
	}
	
	/**
	 * Gets the ads' prefix.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.prefix.format" in {@link ConfigurationType#ADS}</p>
	 * 
	 * @return Ads' prefix
	 * @see #hasPrefix()
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.sending-timeout-ms" in {@link ConfigurationType#ADS}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link Ad#getTexts()}, {@link Ad#getHovers()} and {@link Ad#getClickValues()}.
	 * 
	 * <p><strong>Found at:</strong> "ads.settings.placeholder-types" in {@link ConfigurationType#ADS}</p>
	 * 
	 * @return Placeholders used to translate texts, hovers and click values
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded ads.
	 * You may modify the returned list.
	 * 
	 * @return Loaded ads' list
	 */
	public List<Ad> getAds() {
		return ads;
	}
	
	/**
	 * Gets an ad from {@link #getAds()} by its ID.
	 * Will return <code>null</code> if the ad is not loaded.
	 * 
	 * @param id Ad's ID
	 * @return Loaded ad
	 */
	@Nullable(why = "Specified ad may not be loaded")
	public Ad getAd(String id) {
		return ads.stream().filter(ad -> ad.getID().equals(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the {@link #run()}'s timer's task's ID.
	 * You can interact with it using {@link TaskManager}'s methods.
	 * 
	 * @return Sending task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's index of {@link #getAds()}.
	 * 
	 * @return Timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Checks if the specified String is a valid ad ID.
	 * 
	 * @param adID Ad ID to check
	 * @return Whether the specified ad ID is valid
	 * @see #AD_ID_PATTERN
	 */
	public boolean isValidAdID(String adID) {
		return AD_ID_PATTERN.matcher(adID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static AdManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic ad sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sends an ad to a loaded player.
	 * It will not be sent if {@link Ad#getDisabledRanks()}<code>.contains({@link ChatPluginServerPlayer#getRank()})</code>.
	 * 
	 * @param ad Ad to send
	 * @param player Player to send the ad to
	 * @see AdSendEvent
	 */
	public abstract void sendAd(Ad ad, ChatPluginServerPlayer player);
	
}
