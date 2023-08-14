/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
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

package me.remigio07.chatplugin.api.server.bossbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.bossbar.BossbarSendEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link Bossbar}s. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Bossbars">ChatPlugin wiki/Bossbars</a>
 */
@GameFeature(
		name = "bossbar",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		minimumBukkitVersion = Version.V1_0,
		minimumSpongeVersion = Version.V1_9
		)
public abstract class BossbarManager extends TimerTask implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed bossbar IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidBossbarID(String)
	 */
	public static final Pattern BOSSBAR_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	
	/**
	 * Value representing the max title length.
	 * 
	 * <p><strong>Value:</strong> 256 on &gt; 1.12.2, 64 otherwise</p>
	 */
	public static final int MAX_TITLE_LENGTH = VersionUtils.getVersion().getProtocol() > 340 ? 256 : 64;
	
	/**
	 * String containing the "title too long" text indicator.
	 * 
	 * <p><strong>Content:</strong> "&sect;cTitle exceeds &sect;f+" + {@link #MAX_TITLE_LENGTH} + " &sect;cchars"</p>
	 */
	public static final String TITLE_TOO_LONG = "\u00A7cTitle exceeds \u00A7f64 \u00A7cchars";
	protected static BossbarManager instance;
	protected boolean enabled, randomOrder, loadingBossbarEnabled;
	protected double reflectionWitherTeleportationDistance;
	protected long reflectionWitherTeleportationTimeout, sendingTimeout, loadingBossbarSendingTimeout, timerTaskID = -1, reflectionTimerTaskID = -1, lastRunTime = -1;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<Bossbar> bossbars = new ArrayList<>();
	protected Map<ChatPluginServerPlayer, Long> loadingBossbarsTasks = new HashMap<>();
	protected ReflectionBossbarTimer reflectionBossbarTimer;
	protected int timerIndex = -1;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.enabled" in {@link ConfigurationType#BOSSBARS}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the bossbars should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.random-order" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Checks if an animation should be shown while loading the bossbar when a player joins.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.loading-bossbar.enabled" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Whether the loading bossbar is enabled
	 */
	public boolean isLoadingBossbarEnabled() {
		return loadingBossbarEnabled;
	}
	
	/**
	 * Gets the distance from the player to which the Wither will be teleported to when using reflection.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.reflection-wither-teleportation.distance" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Reflection Wither's teleportation distance
	 */
	public double getReflectionWitherTeleportationDistance() {
		return reflectionWitherTeleportationDistance;
	}
	
	/**
	 * Gets the timeout between Wither teleportations when using reflection, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.reflection-wither-teleportation.timeout-ms" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Time between reflection Wither's teleportations
	 */
	public long getReflectionWitherTeleportationTimeout() {
		return reflectionWitherTeleportationTimeout;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.sending-timeout-ms" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Gets the timeout between sendings while loading the bossbar, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.loading-bossbar.sending-timeout-ms" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Time between sendings while loading the bossbar
	 */
	public long getLoadingBossbarSendingTimeout() {
		return loadingBossbarSendingTimeout;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link Bossbar#getTitles()}.
	 * 
	 * <p><strong>Found at:</strong> "bossbars.settings.placeholder-types" in {@link ConfigurationType#BOSSBARS}</p>
	 * 
	 * @return Placeholders used to translate titles
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded bossbars.
	 * You may modify the returned list.
	 * 
	 * @return Loaded bossbars' list
	 */
	public List<Bossbar> getBossbars() {
		return bossbars;
	}
	
	/**
	 * Gets the loading bossbars' tasks' map.
	 * Do not modify the returned map.
	 * 
	 * @return Loading bossbars' tasks' map
	 */
	public Map<ChatPluginServerPlayer, Long> getLoadingBossbarsTasks() {
		return loadingBossbarsTasks;
	}
	
	/**
	 * Gets a bossbar from {@link #getBossbars()} by its ID.
	 * Will return <code>null</code> if the bossbar is not loaded.
	 * 
	 * @param id Bossbar's ID
	 * @return Loaded bossbar
	 */
	@Nullable(why = "Specified bossbar may not be loaded")
	public Bossbar getBossbar(String id) {
		return bossbars.stream().filter(bossbar -> bossbar.getID().equals(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the current reflection bossbar timer's instance.
	 * Will return <code>null</code> if reflection is not being used.
	 * 
	 * @return Current reflection bossbar timer's instance
	 */
	@Nullable(why = "Reflection may not be being used")
	public ReflectionBossbarTimer getReflectionBossbarTimer() {
		return reflectionBossbarTimer;
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
	 * Gets the {@link ReflectionBossbarTimer#run()}'s timer's task's ID.
	 * You can interact with it using {@link TaskManager}'s methods.
	 * 
	 * @return Reflection timer's index
	 */
	public long getReflectionTimerTaskID() {
		return reflectionTimerTaskID;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's index of {@link #getBossbars()}.
	 * 
	 * @return Timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Gets the last time {@link #run()}'s timer has been run, in milliseconds.
	 * 
	 * @return Timer's last run time
	 */
	public long getLastRunTime() {
		return lastRunTime;
	}
	
	/**
	 * Checks if the specified String is a valid bossbar ID.
	 * 
	 * @param bossbarID Bossbar ID to check
	 * @return Whether the specified bossbar ID is valid
	 * @see #BOSSBAR_ID_PATTERN
	 */
	public boolean isValidBossbarID(String bossbarID) {
		return BOSSBAR_ID_PATTERN.matcher(bossbarID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static BossbarManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic bossbar sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Sends a bossbar to a loaded player.
	 * It will not be sent if {@link Bossbar#isHidden()}.
	 * 
	 * @param bossbar Bossbar to send
	 * @param player Player to send the bossbar to
	 * @see BossbarSendEvent
	 */
	public abstract void sendBossbar(Bossbar bossbar, ChatPluginServerPlayer player);
	
	/**
	 * Starts displaying the loading bossbar to the specified player.
	 * 
	 * @param player Player to display the bossbar to
	 */
	public abstract void startLoading(ChatPluginServerPlayer player);
	
	/**
	 * Timer used to teleport the reflection bossbars' Withers.
	 */
	public abstract class ReflectionBossbarTimer extends TimerTask {
		
		/**
		 * Automatic reflection bossbar sender, called once every {@link BossbarManager#getReflectionWitherTeleportationTimeout()} ms.
		 */
		@Override
		public abstract void run();
		
	}
	
}
