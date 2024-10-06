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

package me.remigio07.chatplugin.api.server.gui;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.event.gui.EmptySlotClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.event.gui.IconClickEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Manager that handles {@link GUI}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/GUIs">ChatPlugin wiki/Modules/GUIs</a>
 */
@GameFeature(
		name = "GUI",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		minimumBukkitVersion = Version.V1_8,
		minimumSpongeVersion = Version.V1_12
		)
public abstract class GUIManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed GUI IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidGUIID(String)
	 */
	public static final Pattern GUI_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	
	/**
	 * Pattern representing the allowed per-player GUI IDs.
	 * 
	 * <p>Strings allowed by this one are shorter (2-19 characters) than {@link #GUI_ID_PATTERN}'s ones
	 * because they have to contain "-" plus a player's name, which might be 16 characters long.</p>
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,19}$"</p>
	 * 
	 * @see #isValidPerPlayerGUIID(String)
	 */
	public static final Pattern PER_PLAYER_GUI_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,19}$");
	protected static GUIManager instance;
	protected boolean enabled;
	protected List<GUI> guis = new CopyOnWriteArrayList<>();
	protected long perPlayerGUIsUnloadTime, loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "guis.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded GUIs' list.
	 * 
	 * <p>Do <strong>not</strong> modify the returned list.</p>
	 * 
	 * @return Loaded GUIs' list
	 */
	public List<GUI> getGUIs() {
		return guis;
	}
	
	/**
	 * Gets a {@link GUI} from {@link #getGUIs()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the GUI is not loaded.</p>
	 * 
	 * @param id GUI's ID, case insensitive
	 * @return Loaded GUI
	 */
	@Nullable(why = "Specified GUI may not be loaded")
	public GUI getGUI(String id) {
		return guis.stream().filter(gui -> gui.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Adds a GUI to {@link #getGUIs()}.
	 * 
	 * <p><strong>Note:</strong> the GUI will be removed on {@link #unload()}.</p>
	 * 
	 * @param gui GUI to add
	 * @throws IllegalArgumentException If {@link GUI#getID()} is already in use, case insensitive
	 */
	public void addGUI(GUI gui) {
		if (guis.stream().anyMatch(other -> other.getID().equalsIgnoreCase(gui.getID())))
			throw new IllegalArgumentException("Specified ID (" + gui.getID() + ") is already in use");
		else guis.add(gui);
	}
	
	/**
	 * Gets the per-player GUIs' unload time, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "guis.per-player-guis-unload-time" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Per-player GUIs' unload time
	 */
	public long getPerPlayerGUIsUnloadTime() {
		return perPlayerGUIsUnloadTime;
	}
	
	/**
	 * Reads a single page GUI from the specified GUI layout.
	 * 
	 * <p>Add it to {@link #getGUIs()} using {@link #addGUI(GUI)}.</p>
	 * 
	 * @param layout Layout to read
	 * @return New single page GUI
	 */
	public SinglePageGUI createSinglePageGUI(SinglePageGUILayout layout) {
		return (SinglePageGUI) createGUI(layout);
	}
	
	/**
	 * Reads a fillable GUI from the specified GUI layout.
	 * 
	 * <p>Add it to {@link #getGUIs()} using {@link #addGUI(GUI)}.</p>
	 * 
	 * @param layout Layout to read
	 * @return New fillable GUI
	 */
	public FillableGUI<?> createFillableGUI(FillableGUILayout layout) {
		return (FillableGUI<?>) createGUI(layout);
	}
	
	/**
	 * Checks if the specified String is a valid GUI ID.
	 * 
	 * @param guiID GUI ID to check
	 * @return Whether the specified GUI ID is valid
	 * @see #GUI_ID_PATTERN
	 */
	public boolean isValidGUIID(String guiID) {
		return GUI_ID_PATTERN.matcher(guiID).matches();
	}
	
	/**
	 * Checks if the specified String is a valid per-player GUI ID.
	 * 
	 * @param perPlayerGUIID Per-player GUI ID to check
	 * @return Whether the specified per-player GUI ID is valid
	 * @see #PER_PLAYER_GUI_ID_PATTERN
	 */
	public boolean isValidPerPlayerGUIID(String perPlayerGUIID) {
		return PER_PLAYER_GUI_ID_PATTERN.matcher(perPlayerGUIID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static GUIManager getInstance() {
		return instance;
	}
	
	/**
	 * Reads a GUI layout from the specified configuration.
	 * 
	 * <p>The GUI layout's ID is obtained by removing {@link Configuration#getFile()}'s
	 * extension and has to match {@link #GUI_ID_PATTERN}.
	 * The returned GUI layout is a {@link FillableGUILayout} if <code>configuration</code> contains
	 * mappings for "settings.filling-function" or {@link SinglePageGUILayout} otherwise.</p>
	 * 
	 * @param configuration Configuration to read
	 * @return New GUI layout
	 * @throws IllegalArgumentException If GUI layout's ID <code>!</code>{@link #isValidGUIID(String)}
	 * or translations for {@link Language#getMainLanguage()} are not present
	 * or an icon's ID {@link Icon#isValidIconID(String)} or an icon's material's ID is invalid
	 * or an icon's {@link Icon#getPosition()} is outside of valid range
	 * @throws IndexOutOfBoundsException If less than 1 or more than 6 rows are specified
	 * @throws NumberFormatException If an icon's {@link Icon#getLeatherArmorColor()} is not
	 * <code>null</code> and is invalid (does not respect the format required by {@link Color#decode(String)})
	 */
	public abstract GUILayout createGUILayout(Configuration configuration);
	
	/**
	 * Creates a new single page GUI layout builder.
	 * 
	 * @param id GUI layout's ID
	 * @param rows GUI layout's rows [1 - 6]
	 * @param openActions GUI layout's open actions
	 * @param clickSound GUI layout's click sound
	 * @param titles GUI layout's titles
	 * @return New GUI layout builder
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link #isValidGUIID(String)}
	 * @throws IndexOutOfBoundsException If less than 1 or more than 6 rows are specified
	 */
	public abstract SinglePageGUILayout.Builder createSinglePageGUILayoutBuilder(
			String id,
			int rows,
			OpenActions openActions,
			SoundAdapter clickSound,
			Map<Language, String> titles
			);
	
	/**
	 * Creates a new fillable GUI layout builder.
	 * 
	 * @param id GUI layout's ID
	 * @param rows GUI layout's rows [1 - 6]
	 * @param openActions GUI layout's open actions
	 * @param clickSound GUI layout's click sound
	 * @param titles GUI layout's titles
	 * @return New GUI layout builder
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link #isValidGUIID(String)}
	 * @throws IndexOutOfBoundsException If less than 1 or more than 6 rows are specified
	 */
	public abstract FillableGUILayout.Builder createFillableGUILayoutBuilder(
			String id,
			int rows,
			OpenActions openActions,
			SoundAdapter clickSound,
			Map<Language, String> titles
			);
	
	/**
	 * Reads a GUI from the specified GUI layout.
	 * <p>Add it to {@link #getGUIs()} using {@link #addGUI(GUI)}.</p>
	 * 
	 * @param layout GUI layout to read
	 * @return New GUI
	 */
	public abstract GUI createGUI(GUILayout layout);
	
	/**
	 * Reads a per-player GUI from the specified GUI layout
	 * for a player and adds it to {@link #getGUIs()}.
	 * 
	 * <p>The returned GUI's ID is set to {@link GUILayout#getID() layout.getID()}
	 * <code> + "-" + </code>{@link ChatPluginServerPlayer#getName() player.getName()}.
	 * A task that unloads the returned GUI after {@link #getPerPlayerGUIsUnloadTime()}
	 * of inactivity ({@link GUIOpenEvent}, {@link EmptySlotClickEvent}, {@link IconClickEvent}) is automatically started.
	 * The GUI gets removed when <code>player</code> is unloaded from {@link PlayerManager#getPlayers()}.</p>
	 * 
	 * @param <T> GUI's type
	 * @param layout GUI layout to read
	 * @param player GUI's player
	 * @return New GUI
	 * @throws IllegalArgumentException If {@link GUILayout#getID() layout.getID()}
	 * <code>!</code>{@link #isValidPerPlayerGUIID(String)} or {@link GUI#getID()}
	 * is already in use by another GUI in {@link #getGUIs()}
	 */
	public abstract <T extends GUI & PerPlayerGUI> T createPerPlayerGUI(GUILayout layout, ChatPluginServerPlayer player);
	
	/**
	 * Reads an icon from the specified configuration and path.
	 * 
	 * <p>The icon's ID is the text after <code>path</code>'s last
	 * dot ('.') or <code>path</code> if it does not contain
	 * any and has to match {@link Icon#ICON_ID_PATTERN}.
	 * The icon's type is {@link IconType#PAGE_SWITCHER} if its ID
	 * is contained in {@link IconType#PAGE_SWITCHER_ICONS_IDS}
	 * or {@link IconType#CUSTOM} otherwise.</p>
	 * 
	 * @param configuration Configuration to read
	 * @param path Icon's path
	 * @return New icon
	 * @throws IllegalArgumentException If icon's ID <code>!</code>{@link Icon#isValidIconID(String)}
	 * or material's ID found at <code>path + ".material"</code> is invalid
	 * @throws NumberFormatException If color found at <code>path + ".leather-color-armor"</code> is not
	 * <code>null</code> and is invalid (does not respect the format required by {@link Color#decode(String)})
	 */
	public abstract Icon createIcon(Configuration configuration, String path);
	
	/**
	 * Gets the GUI the specified player is currently viewing.
	 * 
	 * <p>Will return <code>null</code> if they do not have a GUI open.</p>
	 * 
	 * @param player Target player
	 * @return Player's open GUI
	 */
	@Nullable(why = "Player may not have a GUI open")
	public abstract GUI getOpenGUI(ChatPluginServerPlayer player);
	
}
