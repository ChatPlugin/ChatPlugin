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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.event.ad.AdSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.ad.BaseAdManager;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BukkitAdManager extends BaseAdManager {
	
	@SuppressWarnings("deprecation")
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
			TextComponent text = new TextComponent("");
			
			for (BaseComponent component : TextComponent.fromLegacyText(ChatColor.translate(checkPrefixes(ad.getText(language, true)))))
				text.addExtra(component);
			if (ad.getHover(language) != null)
				text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.translate(ad.getHover(language)))));
			if (ad.getClickAction() != null && ad.getClickValue(language) != null)
				text.setClickEvent(new ClickEvent(ad.getClickAction().bukkitValue(), ad.getClickValue(language)));
			player.toAdapter().bukkitValue().spigot().sendMessage(text);
		} else {
			List<Object> args = new ArrayList<>();
			
			args.add(BukkitReflection.invokeMethod("ChatSerializer", "a", null, ad.toJSON(language)));
			args.add(VersionUtils.getVersion().getProtocol() > 316 ? BukkitReflection.getEnum("ChatMessageType", 0) : 0);
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_19)) {
				player.sendPacket(BukkitReflection.getInstance("ClientboundSystemChatPacket", args));
				return;
			} if (VersionUtils.getVersion().getProtocol() > 578)
				args.add(UUID.randomUUID());
			player.sendPacket(BukkitReflection.getInstance("PacketPlayOutChat", args.toArray(new Object[0])));
		}
	}
	
}
