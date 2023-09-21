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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.player;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

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
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Represents a {@link ChatPluginPlayer} loaded on a server (Bukkit/Sponge) environment.
 * 
 * @see ServerPlayerManager
 */
public abstract class ChatPluginServerPlayer extends OfflinePlayer implements ChatPluginPlayer {
	
	protected Version version;
	protected boolean bedrockPlayer;
	protected Rank rank;
	protected Language language;
	protected Scoreboard scoreboard;
	protected PlayerBossbar bossbar;
	protected IPLookup ipLookup;
	protected ObjectiveAdapter objective;
	protected Object playerConnection;
	protected int ping, id, messagesSent;
	protected short bans, anticheatBans, warnings, anticheatWarnings, kicks, anticheatKicks, mutes, anticheatMutes;
	protected long loginTime = System.currentTimeMillis();
	
	/**
	 * Constructs a new server player with the given {@link PlayerAdapter} object.
	 * 
	 * @deprecated Internal use only. Use {@link ServerPlayerManager#loadPlayer(PlayerAdapter)} to load a player and {@link ServerPlayerManager#getPlayer(java.util.UUID)} to obtain their instance.
	 * @param player Player object
	 */
	@Deprecated
	public ChatPluginServerPlayer(PlayerAdapter player) {
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
	 * Will return <code>null</code> if this player does not have an active scoreboard.
	 * 
	 * @return Player's scoreboard
	 */
	@Nullable(why = "Player may not have an active scoreboard")
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Gets this player's active bossbar.
	 * Will return <code>null</code> if <code>!</code>{@link BossbarManager#isEnabled()}.
	 * 
	 * @return Player's bossbar
	 */
	@Nullable(why = "BossbarManager may be disabled")
	public PlayerBossbar getBossbar() {
		return bossbar;
	}
	
	/**
	 * Gets an IP lookup for this player's IP address. Will return {@link IPLookupManager#getDisabledFeatureConstructor()}
	 * if <code>!</code>{@link IPLookupManager#isEnabled()} or <code>!generateIfNull</code>
	 * and there are no cached IP lookups for this player or an error occurrs.
	 * Might take some time: async calls of this method are recommended.
	 * 
	 * @param generateIfNull Whether to generate the lookup if it is not cached
	 * @return Lookup for this player
	 */
	@NotNull
	public IPLookup getIPLookup(boolean generateIfNull) {
		if (ipLookup == null) {
			if (IPLookupManager.getInstance().isEnabled() && generateIfNull) {
				try {
					return ipLookup = IPLookupManager.getInstance().getIPLookup(getIPAddress()).get();
				} catch (InterruptedException | ExecutionException e) {
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
	 * @return Player's ID in the database
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#BANS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's bans in the database
	 */
	public int getBans() {
		return bans;
	}
	
	/**
	 * Gets this player's anticheat bans from {@link DataContainer#BANS}.
	 * 
	 * @return Player's anticheat bans in the database
	 */
	public short getAnticheatBans() {
		return anticheatBans;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#WARNINGS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's warnings in the database
	 */
	public int getWarnings() {
		return warnings;
	}
	
	/**
	 * Gets this player's anticheat warnings from {@link DataContainer#WARNINGS}.
	 * 
	 * @return Player's anticheat warnings in the database
	 */
	public short getAnticheatWarnings() {
		return anticheatWarnings;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#KICKS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's kicks in the database
	 */
	public int getKicks() {
		return kicks;
	}
	
	/**
	 * Gets this player's anticheat kicks from {@link DataContainer#KICKS}.
	 * 
	 * @return Player's anticheat kicks in the database
	 */
	public short getAnticheatKicks() {
		return anticheatKicks;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#MUTES} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's mutes in the database
	 */
	public int getMutes() {
		return mutes;
	}
	
	/**
	 * Gets this player's anticheat mutes from {@link DataContainer#MUTES}.
	 * 
	 * @return Player's anticheat mutes in the database
	 */
	public short getAnticheatMutes() {
		return anticheatMutes;
	}
	
	/**
	 * Gets this player's {@link PlayersDataType#MESSAGES_SENT} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's messages sent in the database
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
	 * values of <code>10</code>, <code>70</code> and <code>20</code> ticks.
	 * 
	 * @param title Title to send
	 * @param subtitle Subtitle to send
	 */
	public void sendTitle(@Nullable(why = "Title may not be specified") String title, @Nullable(why = "Subtitle may not be specified") String subtitle) {
		sendTitle(title, subtitle, 10, 70, 20);
	}
	
	/**
	 * Sends a title and a subtitle to this player.
	 * 
	 * @param title Title to send
	 * @param subtitle Subtitle to send
	 * @param fadeIn Fading in effect's duration, in ticks
	 * @param stay Stay effect's duration, in ticks
	 * @param fadeOut Fading out effect's duration, in ticks
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
	 * @param command Command to execute
	 */
	public abstract void executeCommand(String command);
	
	/**
	 * Gets this player's world's name.
	 * 
	 * @return Player's world's name
	 */
	public abstract String getWorld();
	
	/**
	 * Gets this player's locale set in their game's settings.
	 * 
	 * @return Player's locale
	 */
	public abstract Locale getLocale();
	
}
