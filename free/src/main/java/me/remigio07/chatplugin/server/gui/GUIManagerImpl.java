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

package me.remigio07.chatplugin.server.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ValueContainer;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.FillableGUILayout;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUILayout;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.IconLayout;
import me.remigio07.chatplugin.api.server.gui.IconType;
import me.remigio07.chatplugin.api.server.gui.OpenActions;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickTypeAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.EnchantmentAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemFlagAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

public class GUIManagerImpl extends GUIManager {
	
	private long taskID = -1;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.CONFIG.get().getBoolean("guis.enabled") || !checkAvailability(true))
			return;
		if ((perPlayerGUIsUnloadTime = Utils.getTime(ConfigurationType.CONFIG.get().getString("guis.per-player-guis-unload-time"), false, false)) == -1) {
			LogManager.log("Invalid time specified at \"guis.per-player-guis-unload-time\" in config.yml; setting to default value of 2 minutes.", 2);
			
			perPlayerGUIsUnloadTime = 120000L;
		}
		
		InternalGUIs.createMain();
		InternalGUIs.createLanguages();
		InternalGUIs.createChatColor();
		InternalGUIs.createEmojisTone();
		InternalGUIs.preparePlayerInfo();
		InternalGUIs.preparePreferences();
		
		if (ChatPlugin.getInstance().isPremium()) {
			InternalGUIs.createBanlist();
			InternalGUIs.createWarnlist();
			InternalGUIs.createMutelist();
			InternalGUIs.createViolations();
			InternalGUIs.preparePlayerPunishments();
			InternalGUIs.preparePlayerViolations();
		} taskID = TaskManager.scheduleAsync(() -> {
			if (ChatPlugin.getInstance().isReloading())
				return;
			List<String> internalGUIs = Arrays.asList("main", "chat-color", "violations", "player-info", "preferences", "player-violations");
			
			for (GUI gui : guis)
				if (internalGUIs.contains(gui.getLayout().getID()))
					gui.refresh();
		}, 1000L, 1000L);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(taskID);
		InternalGUIs.clearLayouts();
		ServerPlayerManager.getInstance().getPlayers().values().stream().filter(player -> getOpenGUI(player) != null).forEach(ChatPluginServerPlayer::closeInventory);
		guis.stream().filter(PerPlayerGUI.class::isInstance).forEach(gui -> TaskManager.cancelSync(((PerPlayerGUI) gui).getUnloadTaskID()));
		guis.clear();
		
		perPlayerGUIsUnloadTime = 0L;
	}
	
	@Override
	public GUILayout createGUILayout(Configuration configuration) {
		boolean fillable = configuration.contains("settings.filling-function");
		GUILayout.Builder builder = fillable ? new FillableGUILayoutImpl.Builder(configuration) : new SinglePageGUILayoutImpl.Builder(configuration);
		
		for (String iconID : configuration.getKeys("icons")) {
			Icon icon = createIcon(configuration, "icons." + iconID);
			
			builder.setIcon(icon);
		} if (fillable) {
			((FillableGUILayout.Builder) builder).setSlots(configuration.getInt("settings.filling-function.start-slot"), configuration.getInt("settings.filling-function.end-slot"));
			((FillableGUILayout.Builder) builder).setEmptyListIcon(createIcon(configuration, "settings.filling-function.empty-list-icon"));
			
			for (String iconLayoutID : configuration.getKeys("settings.filling-function.icon-layouts"))
				((FillableGUILayout.Builder) builder).setIconLayout(new IconLayout(configuration, "settings.filling-function.icon-layouts." + iconLayoutID));
		} return fillable ? ((FillableGUILayout.Builder) builder).build() : ((SinglePageGUILayout.Builder) builder).build();
	}
	
	@Override
	public SinglePageGUILayout.Builder createSinglePageGUILayoutBuilder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		return new SinglePageGUILayoutImpl.Builder(id, rows, openActions, clickSound, titles);
	}
	
	@Override
	public FillableGUILayout.Builder createFillableGUILayoutBuilder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		return new FillableGUILayoutImpl.Builder(id, rows, openActions, clickSound, titles);
	}
	
	@Override
	public GUI createGUI(GUILayout layout) {
		return layout instanceof SinglePageGUILayout ? new SinglePageGUIImpl((SinglePageGUILayout) layout) : new FillableGUIImpl<>((FillableGUILayout) layout);
	}
	
	@Override
	public <T extends GUI & PerPlayerGUI> T createPerPlayerGUI(GUILayout layout, ChatPluginServerPlayer player) {
		if (!isValidPerPlayerGUIID(layout.getID()))
			throw new IllegalArgumentException("GUI ID \"" + layout.getID() + "\" is invalid as it does not respect the following pattern: \"" + PER_PLAYER_GUI_ID_PATTERN.pattern() + "\"");
		if (getGUI(layout.getID() + "-" + player.getName()) != null)
			throw new IllegalArgumentException("Specified ID (" + layout.getID() + "-" + player.getName() + ") is already in use");
		@SuppressWarnings("unchecked")
		T gui = (T) (layout instanceof SinglePageGUILayout ? new SinglePageGUIImpl.PerPlayer((SinglePageGUILayout) layout, player) : new FillableGUIImpl.PerPlayer<>((FillableGUILayout) layout, player));
		
		addGUI(gui);
		return gui;
	}
	
	@Override
	public Icon createIcon(Configuration configuration, String path) {
		String id = path.lastIndexOf('.') == -1 ? path : path.substring(path.lastIndexOf('.') + 1);
		Object amount = configuration.getMappings().get(path + ".amount", 1);
		Icon icon = new Icon(
				id,
				IconType.PAGE_SWITCHER_ICONS_IDS.contains(id) ? IconType.PAGE_SWITCHER : IconType.CUSTOM,
				new MaterialAdapter(configuration.getString(path + ".material", "material ID not found at \"" + path + ".material\" in " + configuration.getFile().getName())),
				new ValueContainer<>(amount instanceof String ? amount : ((Integer) amount).shortValue()),
				configuration.getShort(path + ".damage"),
				configuration.getBoolean(path + ".keep-open"),
				configuration.getBoolean(path + ".glowing"),
				Icon.calcIconPosition(configuration.getInt(path + ".x"), configuration.getInt(path + ".y")),
				configuration.getString(path + ".skull-owner", null),
				configuration.getString(path + ".skull-texture-url", null),
				configuration.contains(path + ".leather-armor-color") ? Color.decode(configuration.getString(path + ".leather-armor-color")) : null,
				configuration.getString(path + ".permission", null),
				new ArrayList<>(configuration.getList(path + ".commands", Collections.emptyList())),
				configuration.getStringList(path + ".item-flags").stream().map(ItemFlagAdapter::valueOf).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new)),
				LanguageManager.getInstance().getLanguages().stream().collect(HashMap::new, (map, language) -> map.put(language, configuration.getString(path + ".display-names." + language.getID(), null)), HashMap::putAll),
				LanguageManager.getInstance().getLanguages().stream().collect(HashMap::new, (map, language) -> map.put(language, configuration.getList(path + ".lores." + language.getID(), null)), HashMap::putAll),
				new HashMap<>(configuration.getKeys(path + ".enchantments").stream().filter(enchantment -> EnchantmentAdapter.valueOf(enchantment) != null).collect(Collectors.toMap(EnchantmentAdapter::valueOf, enchantment -> configuration.getInt(path + ".enchantments." + enchantment), (first, second) -> first)))
				);
		return icon;
	}
	
	@Override
	public GUI getOpenGUI(ChatPluginServerPlayer player) {
		Object spongeTitle = null, inventory = Environment.isBukkit()
				? BukkitReflection.invokeMethod("InventoryView", "getTopInventory", BukkitReflection.invokeMethod("HumanEntity", "getOpenInventory", player.toAdapter().bukkitValue()))
				: player.toAdapter().spongeValue().getOpenInventory().orElse(null);
		
		if (Environment.isSponge()) {
			if ((spongeTitle = ((Inventory) inventory).getInventoryProperty(InventoryTitle.class).orElse(null)) == null)
				return null;
			spongeTitle = ((InventoryTitle) spongeTitle).getValue().toPlain();
		} if (inventory != null)
			for (GUI gui : guis)
				for (InventoryAdapter page : gui instanceof SinglePageGUI ? ((SinglePageGUI) gui).getInventories().values() : ((FillableGUI<?>) gui).getInventories().values().stream().flatMap(List::stream).collect(Collectors.toList()))
					if (Environment.isBukkit() ? inventory.equals(page.bukkitValue()) : (inventory = page.spongeValue().getInventoryProperty(InventoryTitle.class).orElse(null)) == null ? false : spongeTitle.equals(((InventoryTitle) inventory).getValue().toPlain()))
						return gui;
		return null;
	}
	
	public static void executeCommands(ChatPluginServerPlayer player, List<String> commands, ClickTypeAdapter clickType) {
		for (String command : commands) {
			if (command.isEmpty())
				continue;
			ClickTypeAdapter ct = null;
			
			for (ClickTypeAdapter other : ClickTypeAdapter.values()) {
				if (command.startsWith(other.name() + ":")) {
					command = command.substring(command.indexOf(':') + 1).trim();
					ct = other;
					break;
				}
			} if (ct == null || ct == clickType) {
				if (command.startsWith("p:"))
					player.executeCommand(command.substring(2).trim());
				else ChatPlugin.getInstance().runConsoleCommand(command.replace("{0}", player.getName()), false);
			}
		}
	}
	
}
