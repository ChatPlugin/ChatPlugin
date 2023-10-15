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

package me.remigio07.chatplugin.server.player;

import java.util.ArrayList;
import java.util.Collections;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.Utils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public abstract class BaseChatPluginServerPlayer extends ChatPluginServerPlayer {
	
	protected Audience audience;
	protected boolean playerStored = false;
	
	@SuppressWarnings("deprecation")
	public BaseChatPluginServerPlayer(PlayerAdapter player) {
		super(player);
		
		if (ProxyManager.getInstance().isEnabled() && ServerPlayerManager.getInstance().getPlayerVersion(player.getUUID()) == null)
			throw new IllegalStateException("Server has not received a PlayerJoin plugin message from the proxy");
		if (PrivateMessagesManager.getInstance().isEnabled() && PrivateMessagesManager.getInstance().isSocialspyOnJoinEnabled() && player.hasPermission("chatplugin.commands.socialspy"))
			socialspyEnabled = true;
		ignoredPlayers = PlayerIgnoreManager.getInstance().isEnabled() ? new ArrayList<>(PlayerIgnoreManager.getInstance().getIgnoredPlayers(this)) : Collections.emptyList();
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
	
	public void setLastCorrespondent(ChatPluginServerPlayer lastCorrespondent) {
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
	
}
