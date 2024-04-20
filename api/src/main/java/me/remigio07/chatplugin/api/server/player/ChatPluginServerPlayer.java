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

package me.remigio07.chatplugin.api.server.player;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.packet.type.MessagePacketType;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;

/**
 * Represents a {@link ChatPluginPlayer} loaded on a server (Bukkit/Sponge) environment.
 * 
 * @see ServerPlayerManager
 */
public abstract class ChatPluginServerPlayer extends OfflinePlayer implements ChatPluginPlayer {
	
	protected Version version;
	protected boolean bedrockPlayer, socialspyEnabled, rangedChatSpyEnabled;
	protected Rank rank;
	protected Language language;
	protected Scoreboard scoreboard;
	protected PlayerBossbar bossbar;
	protected IPLookup ipLookup;
	protected ObjectiveAdapter objective;
	protected Object playerConnection;
	protected int ping, id, messagesSent;
	protected short bans, anticheatBans, warnings, anticheatWarnings, kicks, anticheatKicks, mutes, anticheatMutes;
	protected long loginTime;
	protected ChatPluginServerPlayer lastCorrespondent;
	protected List<OfflinePlayer> ignoredPlayers;
	
	protected ChatPluginServerPlayer(PlayerAdapter player) {
		super(player);
	}
	
	@Override
	public Version getVersion() {
		return version;
	}
	
	@Override
	public boolean isBedrockPlayer() {
		return bedrockPlayer;
	}
	
	/**
	 * Checks if this player has socialspy enabled.
	 * 
	 * @return Whether socialspy is enabled
	 */
	public boolean hasSocialspyEnabled() {
		return socialspyEnabled;
	}
	
	/**
	 * Checks if this player has the ranged chat spy enabled.
	 * 
	 * @return Whether the ranged chat spy is enabled
	 */
	public boolean hasRangedChatSpyEnabled() {
		return rangedChatSpyEnabled;
	}
	
	/**
	 * Sets whether this player should have socialspy enabled.
	 * 
	 * @param socialspyEnabled Whether socialspy is enabled
	 */
	public void setSocialspyEnabled(boolean socialspyEnabled) {
		this.socialspyEnabled = socialspyEnabled;
	}
	
	/**
	 * Sets whether this player should have the ranged chat spy enabled.
	 * 
	 * @param rangedChatSpyEnabled Whether the ranged chat spy is enabled
	 */
	public void setRangedChatSpyEnabled(boolean rangedChatSpyEnabled) {
		this.rangedChatSpyEnabled = rangedChatSpyEnabled;
	}
	
	/**
	 * Gets this player's rank.
	 * 
	 * @return Player's rank
	 */
	public Rank getRank() {
		return rank;
	}
	
	/**
	 * Gets this player's language.
	 * 
	 * @return Player's language
	 */
	public Language getLanguage() {
		return language;
	}
	
	/**
	 * Gets this player's active scoreboard.
	 * 
	 * <p>Will return <code>null</code> if this player does not have an active scoreboard.</p>
	 * 
	 * @return Player's scoreboard
	 */
	@Nullable(why = "Player may not have an active scoreboard")
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Gets this player's active bossbar.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link BossbarManager#isEnabled()}.</p>
	 * 
	 * @return Player's bossbar
	 */
	@Nullable(why = "BossbarManager may be disabled")
	public PlayerBossbar getBossbar() {
		return bossbar;
	}
	
	/**
	 * Gets an IP lookup for this player's IP address.
	 * 
	 * <p>Will return {@link IPLookupManager#getDisabledFeatureConstructor()}
	 * if <code>!</code>{@link IPLookupManager#isEnabled()} or <code>!generateIfNull</code>
	 * and there are no cached IP lookups for this player or an error occurrs.</p>
	 * 
	 * <p><strong>Note:</strong> this method might take some
	 * time to be executed: async calls are recommended.</p>
	 * 
	 * @param generateIfNull Whether to generate the lookup if it is not cached
	 * @return Lookup for this player
	 */
	@NotNull
	public IPLookup getIPLookup(boolean generateIfNull) {
		if (ipLookup == null) {
			if (IPLookupManager.getInstance().isEnabled() && generateIfNull) {
				try {
					return ipLookup = IPLookupManager.getInstance().getIPLookup(getIPAddress()).get(5L, TimeUnit.SECONDS);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					LogManager.log("{0} occurred while waiting for {1}'s IP ({2}) lookup: {3}", 2, e.getClass().getSimpleName(), name, getIPAddress().getHostName(), e.getMessage());
				}
			} return IPLookupManager.getInstance().getDisabledFeatureConstructor();
		} else return ipLookup;
	}
	
	/**
	 * Gets this player's objective.
	 * 
	 * @return Player's objective
	 */
	@NotNull
	public ObjectiveAdapter getObjective() {
		return objective;
	}
	
	/**
	 * Gets this player's connection's instance.
	 * 
	 * @return Player's connection
	 */
	@NotNull
	public Object getPlayerConnection() {
		return playerConnection;
	}
	
	/**
	 * Gets this player's ping, in milliseconds.
	 * 
	 * @return Player's ping
	 */
	public int getPing() {
		return ping;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#ID} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's ID in the storage
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#BANS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's bans in the storage
	 */
	public int getBans() {
		return bans;
	}
	
	/**
	 * Gets this player's anticheat bans from {@link DataContainer#BANS}.
	 * 
	 * @return Player's anticheat bans in the storage
	 */
	public short getAnticheatBans() {
		return anticheatBans;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#WARNINGS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's warnings in the storage
	 */
	public int getWarnings() {
		return warnings;
	}
	
	/**
	 * Gets this player's anticheat warnings from {@link DataContainer#WARNINGS}.
	 * 
	 * @return Player's anticheat warnings in the storage
	 */
	public short getAnticheatWarnings() {
		return anticheatWarnings;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#KICKS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's kicks in the storage
	 */
	public int getKicks() {
		return kicks;
	}
	
	/**
	 * Gets this player's anticheat kicks from {@link DataContainer#KICKS}.
	 * 
	 * @return Player's anticheat kicks in the storage
	 */
	public short getAnticheatKicks() {
		return anticheatKicks;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#MUTES} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's mutes in the storage
	 */
	public int getMutes() {
		return mutes;
	}
	
	/**
	 * Gets this player's anticheat mutes from {@link DataContainer#MUTES}.
	 * 
	 * @return Player's anticheat mutes in the storage
	 */
	public short getAnticheatMutes() {
		return anticheatMutes;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#MESSAGES_SENT} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's messages sent in the storage
	 */
	public int getMessagesSent() {
		return messagesSent;
	}
	
	/**
	 * Gets this player's login time, in milliseconds.
	 * 
	 * @return Player's login time
	 */
	public long getLoginTime() {
		return loginTime;
	}
	
	/**
	 * Checks if this player is vanished.
	 * 
	 * @return Whether this player is vanished
	 */
	public boolean isVanished() {
		return VanishManager.getInstance().isEnabled() && VanishManager.getInstance().isVanished(this);
	}
	
	/**
	 * Gets the last player that has sent a private message to this player.
	 * 
	 * <p>Will return <code>null</code> if no private messages were sent to
	 * this player since they have joined or if the correspondent was unloaded.</p>
	 * 
	 * <p>Keep in mind that the returned player may be vanished and that <code>null</code>
	 * does not represent the console: it is not possible to reply to it directly.</p>
	 * 
	 * @return Player's last correspondent
	 */
	@Nullable(why = "There may not be a correspondent loaded")
	public ChatPluginServerPlayer getLastCorrespondent() {
		return lastCorrespondent;
	}
	
	/**
	 * Cached version of {@link PlayerIgnoreManager#getIgnoredPlayers(OfflinePlayer)}.
	 * 
	 * @return Ignored players' list
	 */
	public List<OfflinePlayer> getIgnoredPlayers() {
		return ignoredPlayers;
	}
	
	/**
	 * Sends a {@link MessagePacketType#NUMERIC_PLACEHOLDERS} message to this player.
	 * 
	 * @param path Message's path
	 * @param args Optional arguments (translated using {@link Utils#numericPlaceholders(String, Object...)})
	 */
	public void sendTranslatedMessage(String path, Object... args) {
		sendMessage(language.getMessage(path, args));
	}
	
	/**
	 * Sends a {@link MessagePacketType#CUSTOM_PLACEHOLDERS} message to this player.
	 * 
	 * <p>The <code>placeholders</code> and the <code>args</code> arrays cannot contain <code>null</code> elements.</p>
	 * 
	 * @param path Message's path
	 * @param placeholders Message's placeholders
	 * @param args Message's arguments
	 */
	public void sendTranslatedMessage(String path, String[] placeholders, Object... args) {
		String message = language.getMessage(path);
		
		for (int i = 0; i < placeholders.length; i++)
			message = message.replace("{" + placeholders[i] + "}", String.valueOf(args[i]));
		sendMessage(message);
	}
	
	/**
	 * Calls {@link #sendTitle(String, String, int, int, int)} specifying default
	 * values of 500, 3500 and 1000 milliseconds (or 10, 70 and 20 ticks).
	 * 
	 * @param title Title to send
	 * @param subtitle Subtitle to send
	 */
	public void sendTitle(@Nullable(why = "Title may not be specified") String title, @Nullable(why = "Subtitle may not be specified") String subtitle) {
		sendTitle(title, subtitle, 500, 3500, 1000);
	}
	
	/**
	 * Sends a title and a subtitle to this player.
	 * 
	 * @param title Title to send
	 * @param subtitle Subtitle to send
	 * @param fadeIn Fading in effect's duration, in milliseconds
	 * @param stay Stay effect's duration, in milliseconds
	 * @param fadeOut Fading out effect's duration, in milliseconds
	 */
	public abstract void sendTitle(@Nullable(why = "Title may not be specified") String title, @Nullable(why = "Subtitle may not be specified") String subtitle, int fadeIn, int stay, int fadeOut);
	
	/**
	 * Sends an actionbar to this player.
	 * 
	 * @param actionbar Actionbar to send
	 */
	public abstract void sendActionbar(@NotNull String actionbar);
	
	/**
	 * Sends a packet to this player.
	 * 
	 * @deprecated Internal use only.
	 * @param packet Packet to send
	 */
	@Deprecated
	public abstract void sendPacket(Object packet);
	
	/**
	 * Opens an inventory to this player.
	 * 
	 * @param inventory Inventory to open
	 */
	public abstract void openInventory(InventoryAdapter inventory);
	
	/**
	 * Closes the player's open inventory.
	 */
	public abstract void closeInventory();
	
	/**
	 * Plays a sound to this player.
	 * 
	 * @param sound Sound to play
	 */
	public abstract void playSound(SoundAdapter sound);
	
	/**
	 * Makes this player execute a command.
	 * 
	 * @param command Command to execute without "/"
	 */
	public abstract void executeCommand(String command);
	
	/**
	 * Teleports this player to another player.
	 * 
	 * @param player Destination player
	 */
	public abstract void teleport(ChatPluginServerPlayer player);
	
	/**
	 * Gets this player's world's name.
	 * 
	 * @return Player's world's name
	 */
	public abstract String getWorld();
	
	/**
	 * Gets this player's location's X coordinate.
	 * 
	 * @return Player's X coord
	 */
	public abstract double getX();
	
	/**
	 * Gets this player's location's Y coordinate.
	 * 
	 * @return Player's Y coord
	 */
	public abstract double getY();
	
	/**
	 * Gets this player's location's Z coordinate.
	 * 
	 * @return Player's Z coord
	 */
	public abstract double getZ();
	
	/**
	 * Gets this player's locale set in their game's settings.
	 * 
	 * @return Player's locale
	 */
	public abstract Locale getLocale();
	
}
