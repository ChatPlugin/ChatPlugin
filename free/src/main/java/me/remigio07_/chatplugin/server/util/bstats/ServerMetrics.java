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

package me.remigio07_.chatplugin.server.util.bstats;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07_.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.common.util.bstats.CommonMetrics;
import me.remigio07_.chatplugin.common.util.bstats.charts.SimplePie;

public abstract class ServerMetrics extends CommonMetrics {
	
	@Override
	public ServerMetrics load() {
		addCustomChart(new SimplePie("pluginEdition", () -> ChatPlugin.getInstance().isPremium() ? "Premium" : "Free"));
		addCustomChart(new SimplePie("databaseStorageMethod", () -> DatabaseManager.getInstance().getMethod().getName()));
		addCustomChart(new SimplePie("totalLanguages", () -> LanguageManager.getInstance().getLanguages().size() > 10 ? "10+" : String.valueOf(LanguageManager.getInstance().getLanguages().size())));
		
		addCustomChart(new SimplePie("ipLookup", () -> IPLookupManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
		
		if (IPLookupManager.getInstance().isEnabled() && !ProxyManager.getInstance().isEnabled())
			addCustomChart(new SimplePie("ipLookupMethod", () -> IPLookupManager.getInstance().getMethod().name().charAt(0) + IPLookupManager.getInstance().getMethod().name().toLowerCase().substring(1)));
		if (ChatPlugin.getInstance().isPremium()) {
			addCustomChart(new SimplePie("multiInstanceMode", () -> ProxyManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
			addCustomChart(new SimplePie("discordIntegration", () -> DiscordIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
//			addCustomChart(new SimplePie("telegramIntegration", () -> TelegramIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
		} return this;
	}
	
}
