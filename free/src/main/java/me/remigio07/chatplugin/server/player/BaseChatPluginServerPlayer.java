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

package me.remigio07.chatplugin.server.player;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public abstract class BaseChatPluginServerPlayer extends ChatPluginServerPlayer {
	
	protected Audience audience;
	protected boolean playerStored = false;
	
	public BaseChatPluginServerPlayer(PlayerAdapter player) {
		super(player);
		
		if ((version = ServerPlayerManager.getPlayerVersion(player.getUUID())) == null && ProxyManager.getInstance().isEnabled())
			throw new IllegalStateException("Server has not received a PlayerJoin plugin message from the proxy");
		if (PrivateMessagesManager.getInstance().isEnabled() && PrivateMessagesManager.getInstance().isSocialspyOnJoinEnabled() && player.hasPermission("chatplugin.commands.socialspy"))
			socialspyEnabled = true;
		if (RangedChatManager.getInstance().isEnabled() && RangedChatManager.getInstance().isSpyOnJoinEnabled() && player.hasPermission("chatplugin.commands.rangedchatspy"))
			rangedChatSpyEnabled = true;
		bedrockPlayer = ServerPlayerManager.isBedrockPlayer(uuid);
		loginTime = ServerPlayerManager.getPlayerLoginTime(uuid);
		ignoredPlayers = PlayerIgnoreManager.getInstance().isEnabled() ? new ArrayList<>(PlayerIgnoreManager.getInstance().getIgnoredPlayers(this)) : Collections.emptyList();
		
		try {
			Integer color = StorageManager.getInstance().getConnector().getPlayerData(PlayersDataType.CHAT_COLOR, this);
			chatColor = color == null ? ChatColor.RESET : ChatColor.of(new Color(color, true));
		} catch (SQLException e) {
			try {
				if (e.getMessage().contains("Column \"CHAT_COLOR\" not found") || e.getMessage().contains("(no such column: chat_color)")) // compatibility with older ChatPlugin versions
					DatabaseConnector.getInstance().executeUpdate("ALTER TABLE " + DataContainer.PLAYERS.getDatabaseTableID() + " ADD `chat_color` INTEGER");
				else LogManager.log("Unable to get chat's color from database for player {0}: {1}", 2, name, e.getMessage());
			} catch (SQLException e2) {
				LogManager.log("Unable to alter database table {0} after version update: {1}", 2, DataContainer.PLAYERS.getDatabaseTableID(), e2.getMessage());
			} chatColor = ChatColor.RESET;
		} try {
			Integer tone = StorageManager.getInstance().getConnector().getPlayerData(PlayersDataType.EMOJIS_TONE, this);
			emojisTone = tone == null ? ChatColor.RESET : ChatColor.of(new Color(tone, true));
		} catch (SQLException e) {
			try {
				if (e.getMessage().contains("Column \"EMOJIS_TONE\" not found") || e.getMessage().contains("(no such column: emojis_tone)")) // compatibility with older ChatPlugin versions
					DatabaseConnector.getInstance().executeUpdate("ALTER TABLE " + DataContainer.PLAYERS.getDatabaseTableID() + " ADD `emojis_tone` INTEGER");
				else LogManager.log("Unable to get emojis' tone from database for player {0}: {1}", 2, name, e.getMessage());
			} catch (SQLException e2) {
				LogManager.log("Unable to alter database table {0} after version update: {1}", 2, DataContainer.PLAYERS.getDatabaseTableID(), e2.getMessage());
			} emojisTone = ChatColor.RESET;
		}
	}
	
	@Override
	public void setChatColor(@NotNull ChatColor chatColor) {
		if (chatColor.isFormatCode())
			throw new IllegalArgumentException("Unable to set chat's color to a format code");
		try {
			StorageManager.getInstance().getConnector().setPlayerData(PlayersDataType.CHAT_COLOR, this, (this.chatColor = chatColor) == ChatColor.RESET ? null : chatColor.getColor().getRGB() & 0xFFFFFF);
		} catch (SQLException | IOException e) {
			LogManager.log("Unable to set chat's color to storage for player {0}: {1}", 2, name, e.getMessage());
		}
	}
	
	@Override
	public void setEmojisTone(@NotNull ChatColor emojisTone) {
		if (emojisTone.isFormatCode())
			throw new IllegalArgumentException("Unable to set emojis' tone to a format code");
		try {
			StorageManager.getInstance().getConnector().setPlayerData(PlayersDataType.EMOJIS_TONE, this, (this.emojisTone = emojisTone) == ChatColor.RESET ? null : emojisTone.getColor().getRGB() & 0xFFFFFF);
		} catch (SQLException | IOException e) {
			LogManager.log("Unable to set emojis' tone to storage for player {0}: {1}", 2, name, e.getMessage());
		}
	}
	
	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
	
	public void setBossbar(PlayerBossbar bossbar) {
		this.bossbar = bossbar;
	}
	
	public void setObjective(ObjectiveAdapter objective) {
		this.objective = objective;
	}
	
	public void setPing(int ping) {
		this.ping = ping;
	}
	
	public void setBans(short bans) {
		this.bans = bans;
	}
	
	public void setAnticheatBans(short anticheatBans) {
		this.anticheatBans = anticheatBans;
	}
	
	public void setWarnings(short warnings) {
		this.warnings = warnings;
	}
	
	public void setAnticheatWarnings(short anticheatWarnings) {
		this.anticheatWarnings = anticheatWarnings;
	}
	
	public void setKicks(short kicks) {
		this.kicks = kicks;
	}
	
	public void setAnticheatKicks(short anticheatKicks) {
		this.anticheatKicks = anticheatKicks;
	}
	
	public void setMutes(short mutes) {
		this.mutes = mutes;
	}
	
	public void setAnticheatMutes(short anticheatMutes) {
		this.anticheatMutes = anticheatMutes;
	}
	
	public void setMessagesSent(int messagesSent) {
		this.messagesSent = messagesSent;
	}
	
	public void setLastCorrespondent(OfflinePlayer lastCorrespondent) {
		this.lastCorrespondent = lastCorrespondent;
	}
	
	public boolean isPlayerStored() {
		return playerStored;
	}
	
	public void sendLanguageDetectedMessage(Language language) {
		sendMessage(Utils.deserializeLegacy(language.getMessage("languages.detected.text", language.getDisplayName()), false)
				.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(language.getMessage("languages.detected.hover", language.getDisplayName()), false)))
				.clickEvent(ClickEvent.runCommand("/chatplugin language " + language.getID()))
				);
	}
	
	public abstract double getDistance(double x, double y, double z);
	
	public abstract void sendMessage(Component... components);
	
}
