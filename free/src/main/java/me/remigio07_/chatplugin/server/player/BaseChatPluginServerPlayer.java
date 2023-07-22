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

package me.remigio07_.chatplugin.server.player;

import me.remigio07_.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07_.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07_.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;

public abstract class BaseChatPluginServerPlayer extends ChatPluginServerPlayer {
	
	protected boolean playerStored = false;
	
	@SuppressWarnings("deprecation")
	public BaseChatPluginServerPlayer(PlayerAdapter player) {
		super(player);
		
		if (ProxyManager.getInstance().isEnabled() && ServerPlayerManager.getInstance().getPlayerVersion(player.getUUID()) == null)
			throw new IllegalStateException("Server has not received a PlayerJoin plugin message from the proxy");
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
	
	public boolean isPlayerStored() {
		return playerStored;
	}
	
	public void sendLanguageDetectedMessage(Language language) {
		sendMessage(new TextAdapter(language.getMessage("languages.detected.text", language.getDisplayName()))
				.onHover(language.getMessage("languages.detected.hover", language.getDisplayName()))
				.onClick(ClickActionAdapter.SEND_MESSAGE, "/chatplugin language " + language.getID())
				);
	}
	
}
