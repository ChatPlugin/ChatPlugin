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

package me.remigio07.chatplugin.api.server.player;

import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.event.chat.channel.ChatChannelJoinEvent;
import me.remigio07.chatplugin.api.server.event.chat.channel.ChatChannelLeaveEvent;
import me.remigio07.chatplugin.api.server.event.chat.channel.ChatChannelSwitchEvent;
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
	protected boolean bedrockPlayer, socialspyEnabled, chatChannelSpyEnabled, actionbarEnabled = true;
	protected Rank rank;
	protected Language language;
	protected Scoreboard scoreboard;
	protected PlayerBossbar bossbar;
	protected IPLookup ipLookup;
	protected ObjectiveAdapter objective;
	protected Object playerConnection;
	protected int ping, id, messagesSent, antispamInfractions;
	protected short bans, anticheatBans, warnings, anticheatWarnings, kicks, anticheatKicks, mutes, anticheatMutes;
	protected long loginTime;
	protected OfflinePlayer lastCorrespondent;
	protected ChatColor chatColor, emojisTone;
	protected ChatChannel<? extends ChatChannelData> writingChannel;
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
	 * Sets whether this player should have socialspy enabled.
	 * 
	 * @param socialspyEnabled Whether socialspy is enabled
	 */
	public void setSocialspyEnabled(boolean socialspyEnabled) {
		this.socialspyEnabled = socialspyEnabled;
	}
	
	/**
	 * Checks if this player has the chat channel spy enabled.
	 * 
	 * @return Whether the chat channel spy is enabled
	 */
	public boolean hasChatChannelSpyEnabled() {
		return chatChannelSpyEnabled;
	}
	
	/**
	 * Sets whether this player should have the chat channel spy enabled.
	 * 
	 * @param chatChannelSpyEnabled Whether the chat channel spy is enabled
	 */
	public void setChatChannelSpyEnabled(boolean chatChannelSpyEnabled) {
		this.chatChannelSpyEnabled = chatChannelSpyEnabled;
	}
	
	/**
	 * Checks if actionbars should be displayed to this player.
	 * 
	 * @return Whether their actionbar is enabled
	 */
	public boolean hasActionbarEnabled() {
		return actionbarEnabled;
	}
	
	/**
	 * Sets whether actionbars should be displayed to this player.
	 * 
	 * @param actionbarEnabled Whether their actionbar is enabled
	 */
	public void setActionbarEnabled(boolean actionbarEnabled) {
		this.actionbarEnabled = actionbarEnabled;
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
	 * <p>Will return <code>null</code> if this player does not have an active bossbar.</p>
	 * 
	 * @return Player's bossbar
	 */
	@Nullable(why = "Player may not have an active bossbar")
	public PlayerBossbar getBossbar() {
		return bossbar;
	}
	
	/**
	 * Gets an IP lookup for this player's IP address.
	 * 
	 * <p>Will return {@link IPLookupManager#getDisabledFeatureConstructor()} if
	 * <code>!</code>{@link IPLookupManager#isEnabled()} or if <code>!generateIfNull</code>
	 * and there are no cached IP lookups for this player or if an error occurrs.</p>
	 * 
	 * <p>The future is not instantly completed if it is necessary to call
	 * {@link IPLookupManager#getIPLookup(InetAddress)}. It will take a
	 * maximum of 5 seconds and will never be completed exceptionally.</p>
	 * 
	 * @param generateIfNull Whether to generate the lookup if it is not cached
	 * @return Lookup for this player
	 */
	public CompletableFuture<IPLookup> getIPLookup(boolean generateIfNull) { // at the end, try to rename it
		if (ipLookup == null) {
			if (IPLookupManager.getInstance().isEnabled() && generateIfNull) {
				CompletableFuture<IPLookup> future = new CompletableFuture<>();
				
				TaskManager.runAsync(() -> {
					try {
						future.complete(ipLookup = IPLookupManager.getInstance().getIPLookup(getIPAddress()).get(5L, TimeUnit.SECONDS));
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						LogManager.log("{0} occurred while waiting for the IP ({1}) lookup of {2}: {3}", 2, e.getClass().getSimpleName(), getIPAddress().getHostAddress(), name, e.getLocalizedMessage());
						future.complete(IPLookupManager.getInstance().getDisabledFeatureConstructor());
					}
				}, 0L);
				return future;
			} return CompletableFuture.completedFuture(IPLookupManager.getInstance().getDisabledFeatureConstructor());
		} return CompletableFuture.completedFuture(ipLookup);
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
	 * Gets this player's {@link PlayersDataType#ANTISPAM_INFRACTIONS} from {@link DataContainer#PLAYERS}.
	 * 
	 * @return Player's antispam infractions in the storage
	 */
	public int getAntispamInfractions() {
		return antispamInfractions;
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
	 * Gets this player's last correspondent.
	 * 
	 * <p>Will return <code>null</code> if this player does not have a correspondent.
	 * Returned value is the last player that has sent a private message to this
	 * player if {@link PrivateMessagesManager#isReplyToLastSender()}, otherwise
	 * it is the player to whom this player has last sent a private message.</p>
	 * 
	 * <p><strong>Note:</strong> the returned player may be vanished or
	 * in another server under the proxy and <code>null</code> does not
	 * represent the console - it is not possible to reply to it directly.</p>
	 * 
	 * @return Player's last correspondent
	 */
	@Nullable(why = "There may not be a correspondent")
	public OfflinePlayer getLastCorrespondent() {
		return lastCorrespondent;
	}
	
	/**
	 * Gets this player's chat's default color.
	 * 
	 * <p>Will return {@link ChatColor#RESET} if
	 * no chat color is set for this player.</p>
	 * 
	 * @return Player's chat's color
	 */
	@NotNull
	public ChatColor getChatColor() {
		return chatColor;
	}
	
	/**
	 * Gets this player's emojis' tone.
	 * 
	 * <p>Will return {@link ChatColor#RESET} if
	 * no emojis' tone is set for this player.</p>
	 * 
	 * <p>If that is the case, you may want to use the
	 * {@linkplain InstantEmojisManager#getDefaultTone() default tone} instead.</p>
	 * 
	 * @return Player's emojis' tone
	 */
	@NotNull
	public ChatColor getEmojisTone() {
		return emojisTone;
	}
	
	/**
	 * Gets the channel this player is writing in.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link ChatChannelsManager#isEnabled()}.</p>
	 * 
	 * @return Player's writing channel
	 */
	@Nullable(why = "Null if !ChatChannelsManager#isEnabled()")
	public ChatChannel<? extends ChatChannelData> getWritingChannel() {
		return writingChannel;
	}
	
	/**
	 * Gets the channels this player is listening to.
	 * 
	 * @return Player's channels
	 */
	public List<ChatChannel<? extends ChatChannelData>> getChannels() {
		return ChatChannelsManager.getInstance().getChannels().stream().filter(channel -> channel.getListeners().contains(this)).collect(Collectors.toList());
	}
	
	/**
	 * Makes this player join the specified channel.
	 * 
	 * <p>Will do nothing and return <code>false</code>
	 * if they are already listening to it.</p>
	 * 
	 * @param channel Channel to join
	 * @return Whether the event has completed
	 * @see ChatChannelJoinEvent
	 */
	public boolean joinChannel(ChatChannel<? extends ChatChannelData> channel) {
		if (!ChatChannelsManager.getInstance().isEnabled() || channel.getListeners().contains(this))
			return false;
		ChatChannelJoinEvent event = new ChatChannelJoinEvent(this, channel);
		
		event.call();
		
		if (event.isCancelled())
			return false;
		channel.getListeners().add(this);
		return true;
	}
	
	/**
	 * Makes this player leave the specified channel.
	 * 
	 * <p>Will do nothing and return <code>false</code>
	 * if they are not listening to it.</p>
	 * 
	 * @param channel Channel to leave
	 * @return Whether the event has completed
	 * @throws IllegalArgumentException If {@link #getWritingChannel()}<code>.equals(channel)</code>
	 * @see ChatChannelLeaveEvent
	 */
	public boolean leaveChannel(ChatChannel<? extends ChatChannelData> channel) {
		if (getWritingChannel().equals(channel) && isLoaded())
			throw new IllegalArgumentException("Unable to make player leave their current writing channel");
		if (!ChatChannelsManager.getInstance().isEnabled() || !channel.getListeners().contains(this))
			return false;
		ChatChannelLeaveEvent event = new ChatChannelLeaveEvent(this, channel);
		
		event.call();
		
		if (event.isCancelled() && isLoaded()) // FIXME in new version
			return false;
		channel.getListeners().remove(this);
		return true;
	}
	
	/**
	 * Makes this player switch channels for writing.
	 * 
	 * <p>Will call {@link #joinChannel(ChatChannel)} if they
	 * are not listening to it and return <code>false</code>
	 * before executing {@link ChatChannelSwitchEvent} if
	 * that method returns <code>false</code>.</p>
	 * 
	 * @param channel Channel to switch to
	 * @return Whether the event has completed
	 * @see ChatChannelSwitchEvent
	 */
	public boolean switchChannel(ChatChannel<? extends ChatChannelData> channel) {
		if (!ChatChannelsManager.getInstance().isEnabled() || !getChannels().contains(channel) && !joinChannel(channel))
			return false;
		ChatChannelSwitchEvent event = new ChatChannelSwitchEvent(this, channel);
		
		event.call();
		
		if (event.isCancelled())
			return false;
		writingChannel = channel;
		return true;
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
	 * Sends a translated message (with numeric placeholders) to this player.
	 * 
	 * @param path Message's path
	 * @param args Optional arguments ({@link Utils#replaceNumericPlaceholders(String, Object...)})
	 */
	public void sendTranslatedMessage(String path, Object... args) {
		sendMessage(language.getMessage(path, args));
	}
	
	/**
	 * Sends a translated message (with custom placeholders) to this player.
	 * 
	 * @param path Message's path
	 * @param placeholders Message's placeholders
	 * @param args Optional arguments ({@link Utils#replaceCustomPlaceholders(String, String[], Object...)})
	 */
	public void sendTranslatedMessage(String path, String[] placeholders, Object... args) {
		sendMessage(Utils.replaceCustomPlaceholders(language.getMessage(path), placeholders, args));
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
	 * @param command Command to execute without '/'
	 */
	public abstract void executeCommand(String command);
	
	/**
	 * Teleports this player to another player.
	 * 
	 * @param player Destination player
	 */
	public abstract void teleport(ChatPluginServerPlayer player);
	
	/**
	 * Gets this player's display name.
	 * 
	 * <p>It can be changed by other plugins
	 * and may include formatting codes.</p>
	 * 
	 * @return Player's display name
	 */
	public abstract String getDisplayName();
	
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
	 * Gets this player's distance from the specified location.
	 * 
	 * @param x X coord
	 * @param y Y coord
	 * @param z Z coord
	 * @return Player's distance
	 */
	public abstract double getDistance(double x, double y, double z);
	
	/**
	 * Gets this player's locale set in their game's settings.
	 * 
	 * @return Player's locale
	 */
	public abstract Locale getLocale();
	
	/**
	 * Sets this player's chat's default color.
	 * 
	 * <p>Specify {@link ChatColor#RESET} to reset it and make it appear as
	 * their rank's {@link Rank#getChatColor()} when they type in chat.</p>
	 * 
	 * @param chatColor Player's chat's color
	 * @throws IllegalArgumentException If {@link ChatColor#isFormatCode()}
	 */
	public abstract void setChatColor(@NotNull ChatColor chatColor);
	
	/**
	 * Sets this player's emojis' tone.
	 * 
	 * <p>Specify {@link ChatColor#RESET} to reset it and make it appear as
	 * the {@linkplain InstantEmojisManager#getDefaultTone() default tone}.</p>
	 * 
	 * @param emojisTone Player's emojis' tone
	 * @throws IllegalArgumentException If {@link ChatColor#isFormatCode()}
	 */
	public abstract void setEmojisTone(@NotNull ChatColor emojisTone);
	
}
