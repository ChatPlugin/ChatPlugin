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

package me.remigio07.chatplugin.server.util.bstats;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.telegram.TelegramIntegrationManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.bstats.CommonMetrics;
import me.remigio07.chatplugin.common.util.bstats.charts.SimplePie;

public abstract class ServerMetrics extends CommonMetrics {
	
	@Override
	public ServerMetrics load() {
		boolean multiInstanceMode = ProxyManager.getInstance().isEnabled();
		int languages = LanguageManager.getInstance().getLanguages().size();
		int ranks = RankManager.getInstance().getRanks().size();
		
		super.load();
		addCustomChart(new SimplePie("pluginEdition", () -> ChatPlugin.getInstance().isPremium() ? "Premium" : "Free"));
		addCustomChart(new SimplePie("languages", () -> String.valueOf(languages > 4 ? languages > 9 ? languages > 14 ? "15+" : "10-14" : "5-9" : "1-4")));
		addCustomChart(new SimplePie("ranks", () -> String.valueOf(ranks > 9 ? ranks > 19 ? ranks > 29 ? ranks > 49 ? ranks > 69 ? "70-99" : "50-69" : "30-49" : "20-29" : "10-19" : "1-9")));
		addCustomChart(new SimplePie("actionbars", () -> range(ActionbarManager.class, ActionbarManager.getInstance().getActionbars().size())));
		addCustomChart(new SimplePie("ads", () -> range(AdManager.class, AdManager.getInstance().getAds().size())));
		addCustomChart(new SimplePie("f3ServerNames", () -> range(F3ServerNameManager.class, F3ServerNameManager.getInstance().getF3ServerNames().size())));
		addCustomChart(new SimplePie("tablists", () -> range(TablistManager.class, TablistManager.getInstance().getTablists().size())));
		
		if (ChatPlugin.getInstance().isPremium()) {
			if (!multiInstanceMode) {
				addCustomChart(new SimplePie("discordIntegration", () -> DiscordIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
				addCustomChart(new SimplePie("telegramIntegration", () -> TelegramIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
			} addCustomChart(new SimplePie("multiInstanceMode", () -> multiInstanceMode ? "Enabled" : "Disabled"));
			addCustomChart(new SimplePie("bossbars", () -> range(BossbarManager.class, BossbarManager.getInstance().getBossbars().size())));
			addCustomChart(new SimplePie("scoreboards", () -> range(ScoreboardManager.class, ScoreboardManager.getInstance().getScoreboards().size())));
			addCustomChart(new SimplePie("guis", () -> range(GUIManager.class, GUIManager.getInstance().getGUIs().size())));
		} if (!multiInstanceMode)
			addCustomChart(new SimplePie("ipLookupMethod", () -> IPLookupManager.getInstance().isEnabled() ? Utils.capitalizeEveryWord(IPLookupManager.getInstance().getMethod().name()) : "Disabled"));
		return this;
	}
	
}
