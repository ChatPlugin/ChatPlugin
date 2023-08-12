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

package me.remigio07.chatplugin.server.integration.anticheat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUIFiller;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.Violation;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.Utils;

public class AnticheatManagerImpl extends AnticheatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!isEnabled())
			return;
		reasonsStartWith = ConfigurationType.CONFIG.get().getStringList("settings.anticheat-integration.reasons-start-with");
		violationsExpirationTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("settings.violations-expiration-timeout"), false);
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		violations.clear();
		reasonsStartWith.clear();
		
		violationsExpirationTimeout = -1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addViolation(Violation violation) {
		if (!isEnabled())
			return;
		ChatPluginServerPlayer player = violation.getCheater();
		List<Violation> list = violations.getOrDefault(player, new ArrayList<>());
		
		for (Violation viol : new ArrayList<>(list))
			if (viol.getCheatID().equals(violation.getCheatID()))
				list.remove(viol);
		list.add(violation);
		violations.put(player, list);
		
		if (GUIManager.getInstance().isEnabled()) {
			FillableGUI<ChatPluginServerPlayer> violationsGUI = (FillableGUI<ChatPluginServerPlayer>) GUIManager.getInstance().getGUI("violations");
			
			if (violationsGUI != null && violationsGUI.getFiller(player) != null) {
				
				violationsGUI.addFiller(new GUIFiller<ChatPluginServerPlayer>() {
					
					@Override
					public FillableGUI<ChatPluginServerPlayer> getGUI() {
						return violationsGUI;
					}
					
					@Override
					public ChatPluginServerPlayer getFiller() {
						return player;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						return violation.formatPlaceholders(input, language);
					}
					
					@Override
					public List<String> formatPlaceholders(List<String> input, Language language) {
						int index = input.indexOf("{violations}");
						List<String> list = input;
						
						if (index != -1) {
							String format = ConfigurationType.VIOLATIONS_GUI.get().translateString("violations.violations-list-format");
							list = new ArrayList<>(input);
							
							list.remove(index);
							list.addAll(index, AnticheatManager.getInstance().getViolations(player).stream().map(violation -> violation.formatPlaceholders(format, language)).collect(Collectors.toList()));
						} return list.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
					}
					
				}, false);
			} if (GUIManager.getInstance().getGUI("player-violations-" + player.getName()) != null)
				((FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + player.getName())).addFiller(violation, (t, u) -> violation.formatPlaceholders(t, u), false);
		}
		
		TaskManager.runAsync(() -> {
			for (Violation vl : new ArrayList<>(getViolations(player))) {
				if (vl.getCheatID().equals(violation.getCheatID()) && vl.getLastTime() == violation.getLastTime()) {
					removeViolation(vl.getAnticheat(), player, vl.getCheatID());
					break;
				}
			}
		}, violationsExpirationTimeout);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeViolation(IntegrationType<AnticheatIntegration> anticheat, ChatPluginServerPlayer player, String cheatID) {
		if (!isEnabled() || !violations.containsKey(player))
			return;
		for (Violation violation : violations.get(player)) {
			if (violation.getAnticheat() == anticheat && violation.getCheatID().equals(cheatID)) {
				violations.put(player, Utils.removeAndGet(violations.get(player), Arrays.asList(violation)));
				
				if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("player-violations-" + player.getName()) != null)
					((FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + player.getName())).removeFiller(violation, true);
				break;
			}
		} if (violations.get(player).isEmpty()) {
			violations.remove(player);
			
			if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("violations") != null)
				((FillableGUI<ChatPluginServerPlayer>) GUIManager.getInstance().getGUI("violations")).removeFiller(player, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void clearViolations(ChatPluginServerPlayer player) {
		if (!isEnabled())
			return;
		violations.remove(player);
		
		if (GUIManager.getInstance().isEnabled()) {
			if (GUIManager.getInstance().getGUI("violations") != null)
				((FillableGUI<ChatPluginServerPlayer>) GUIManager.getInstance().getGUI("violations")).removeFiller(player, false);
			if (GUIManager.getInstance().getGUI("player-violations-" + player.getName()) != null)
				((FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + player.getName())).clearFillers(false);
		}
	}
	
	@Override
	public boolean isAnticheatReason(String reason) {
		reason = ChatColor.stripColor(reason).toLowerCase();
		
		for (String reasonStartsWith : reasonsStartWith)
			if (reason.startsWith(reasonStartsWith.toLowerCase()))
				return true;
		return false;
	}
	
}
