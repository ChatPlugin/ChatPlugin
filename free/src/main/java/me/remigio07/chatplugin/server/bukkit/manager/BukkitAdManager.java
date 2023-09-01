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

package me.remigio07.chatplugin.server.bukkit.manager;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.event.ad.AdSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.ad.BaseAdManager;
import me.remigio07.chatplugin.server.util.Utils;

public class BukkitAdManager extends BaseAdManager {
	
	@Override
	public void sendAd(Ad ad, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		AdSendEvent event = new AdSendEvent(ad, player);
		Language language = player.getLanguage();
		
		event.call();
		
		if (event.isCancelled())
			return;
		if (VersionUtils.isSpigot()) {
			TextAdapter text = new TextAdapter(ChatColor.translate(checkPrefixes(ad.getText(language, true))));
			
			if (ad.getHover(language) != null)
				text.onHover(ChatColor.translate(ad.getHover(language)));
			if (ad.getClickAction() != null && ad.getClickValue(language) != null)
				text.onClick(ad.getClickAction(), ad.getClickValue(language));
			player.sendMessage(text);
		} else Utils.sendBukkitMessage(player, ad.toJSON(language));
	}
	
}
