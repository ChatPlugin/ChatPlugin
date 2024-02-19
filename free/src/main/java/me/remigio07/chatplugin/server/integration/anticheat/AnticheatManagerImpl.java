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

package me.remigio07.chatplugin.server.integration.anticheat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUIFiller;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.Violation;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;

public class AnticheatManagerImpl extends AnticheatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!isEnabled())
			return;
		reasonsStartWith = new ArrayList<>(ConfigurationType.CONFIG.get().getStringList("settings.anticheat-integration.reasons-start-with"));
		violationsExpirationTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("settings.anticheat-integration.violations-expiration-timeout"), false);
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
	public void addViolation(
			OfflinePlayer cheater,
			IntegrationType<AnticheatIntegration> anticheat,
			String cheatID,
			String component,
			@NotNull String server,
			int amount,
			int ping,
			double tps,
			Version version
			) {
		if (!isEnabled())
			return;
		List<Violation> list = violations.getOrDefault(cheater, new ArrayList<>());
		ViolationImpl target = null;
		
		for (Violation violation : list)
			if (violation.getAnticheat() == anticheat && violation.getCheatID().equals(cheatID)) {
				target = (ViolationImpl) violation;
				break;
			}
		if (target == null)
			list.add(target = new ViolationImpl(cheater, anticheat, cheatID));
		target.updateData(component, server, amount, ping, tps, version);
		violations.put(cheater, list);
		
		final Violation finalViolation = target;
		
		if (GUIManager.getInstance().isEnabled()) {
			FillableGUI<OfflinePlayer> violationsGUI = (FillableGUI<OfflinePlayer>) GUIManager.getInstance().getGUI("violations");
			
			if (violationsGUI != null && violationsGUI.getFiller(cheater) == null) {
				violationsGUI.addFiller(new GUIFiller<OfflinePlayer>() {
					
					@Override
					public FillableGUI<OfflinePlayer> getGUI() {
						return violationsGUI;
					}
					
					@Override
					public OfflinePlayer getFiller() {
						return cheater;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						List<Violation> violationsList = getViolations(cheater);
						
						if (!violationsList.isEmpty()) {
							Violation lastViolation = violationsList.get(0);
							
							for (Violation violation : violationsList)
								if (violation.getLastTime() > lastViolation.getLastTime())
									lastViolation = violation;
							return lastViolation.formatPlaceholders(input, language);
						} return input;
					}
					
					@Override
					public List<String> formatPlaceholders(List<String> input, Language language) {
						int index = input.indexOf("{violations}");
						List<String> list = input;
						
						if (index != -1) {
							String format = ConfigurationType.VIOLATIONS_GUI.get().translateString("settings.violations-list-format", Utils.STRING_NOT_FOUND, false);
							list = new ArrayList<>(input);
							
							list.remove(index);
							list.addAll(index, getViolations(cheater).stream().map(violation -> violation.formatPlaceholders(format, language)).collect(Collectors.toList()));
						} return list.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
					}
					
				}, false);
			} FillableGUI<Violation> gui = (FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + cheater.getName());
			
			if (gui != null && gui.getFiller(target) == null)
				gui.addFiller(new GUIFiller<Violation>() {
					
					@Override
					public FillableGUI<Violation> getGUI() {
						return gui;
					}
					
					@Override
					public Violation getFiller() {
						return finalViolation;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						return finalViolation.formatPlaceholders(input, language);
					}
					
					@Override
					public Icon getIcon(Icon icon) {
						String path = anticheat.name().toLowerCase() + "." + cheatID.toLowerCase() + ".";
						
						try {
							return icon
									.setMaterial(new MaterialAdapter(ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "material")))
									.setDamage(ConfigurationType.VIOLATIONS_ICONS.get().getShort(path + "damage"))
									.setSkullOwner(ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "skull-owner"));
						} catch (IllegalArgumentException e) {
							LogManager.log("Invalid material ID found at {0} in violations-icons.yml: {1}.", 2, path + "material", ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "material", Utils.STRING_NOT_FOUND));
							return icon.setMaterial(new MaterialAdapter("BARRIER"));
						}
					}
					
				}, false);
		} TaskManager.runAsync(() -> {
			for (Violation violation : new ArrayList<>(getViolations(cheater))) {
				if (violation.getCheatID().equals(cheatID) && violation.getLastTime() == finalViolation.getLastTime()) {
					removeViolation(cheater, anticheat, cheatID);
					break;
				}
			}
		}, violationsExpirationTimeout);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeViolation(OfflinePlayer cheater, IntegrationType<AnticheatIntegration> anticheat, String cheatID) {
		if (!isEnabled() || !violations.containsKey(cheater))
			return;
		for (Violation violation : getViolations(cheater)) {
			if (violation.getAnticheat() == anticheat && violation.getCheatID().equals(cheatID)) {
				violations.put(cheater, Utils.removeAndGet(violations.get(cheater), Arrays.asList(violation)));
				
				if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("player-violations-" + cheater.getName()) != null)
					((FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + cheater.getName())).removeFiller(violation, true);
				break;
			}
		} if (getViolations(cheater).isEmpty()) {
			violations.remove(cheater);
			
			if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("violations") != null)
				((FillableGUI<OfflinePlayer>) GUIManager.getInstance().getGUI("violations")).removeFiller(cheater, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void clearViolations(OfflinePlayer cheater) {
		if (!isEnabled())
			return;
		violations.remove(cheater);
		
		if (GUIManager.getInstance().isEnabled()) {
			if (GUIManager.getInstance().getGUI("violations") != null)
				((FillableGUI<OfflinePlayer>) GUIManager.getInstance().getGUI("violations")).removeFiller(cheater, false);
			if (GUIManager.getInstance().getGUI("player-violations-" + cheater.getName()) != null)
				((FillableGUI<Violation>) GUIManager.getInstance().getGUI("player-violations-" + cheater.getName())).clearFillers(false);
		}
	}
	
	@Override
	public boolean isAnticheatReason(String reason) {
		if (reason != null) {
			reason = ChatColor.stripColor(reason).toLowerCase();
			
			for (String reasonStartsWith : reasonsStartWith)
				if (reason.startsWith(reasonStartsWith.toLowerCase()))
					return true;
		} return false;
	}
	
}
