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

package me.remigio07_.chatplugin.server.sponge.manager;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;

import me.remigio07_.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07_.chatplugin.api.server.ad.Ad;
import me.remigio07_.chatplugin.api.server.event.ad.AdSendEvent;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.server.ad.BaseAdManager;

public class SpongeAdManager extends BaseAdManager {
	
	@Override
	public void sendAd(Ad ad, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		AdSendEvent event = new AdSendEvent(ad, player);
		Language language = player.getLanguage();
		
		event.call();
		
		if (event.isCancelled())
			return;
		Text.Builder builder = Text.builder(PlaceholderManager.getInstance().translatePlaceholders(checkPrefixes(ad.getText(language, true)), player, placeholderTypes));
		
		if (ad.getHover(language) != null)
			builder.onHover(TextActions.showText(new TextAdapter(PlaceholderManager.getInstance().translatePlaceholders(ad.getHover(language), player, placeholderTypes)).spongeValue()));
		if (ad.getClickAction() != null && ad.getClickValue(language) != null) {
			ClickAction<?> action = (ClickAction<?>) ad.getClickAction().spongeValue(ad.getClickValue(language));
			
			if (action != null)
				builder.onClick(action);
		} player.toAdapter().spongeValue().sendMessage(builder.build());
	}
	
}
