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

package me.remigio07.chatplugin.api.server.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.EnchantmentAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemFlagAdapter;

/**
 * Represents a {@link IconType#GENERATED} icon's layout inside of a {@link FillableGUI}.
 */
public class IconLayout {
	
	private String id, skullOwner, skullTextureURL;
	private MaterialAdapter material;
	private boolean keepOpen, glowing;
	private int amount;
	private short damage;
	private Color leatherArmorColor;
	private List<String> commands;
	private List<ItemFlagAdapter> itemFlags;
	private Map<Language, String> displayNames;
	private Map<Language, List<String>> lores;
	private Map<EnchantmentAdapter, Integer> enchantments;
	
	/**
	 * Reads an icon layout from the specified configuration and path.
	 * The icon's ID is the text after <code>path</code>'s last
	 * dot ('.') or <code>path</code> if it does not contain
	 * any and has to match {@link Icon#ICON_ID_PATTERN}.
	 * 
	 * @param configuration Configuration to read
	 * @param path Icon layout's path
	 * @throws IllegalArgumentException If icon's ID <code>!</code>{@link Icon#isValidIconID(String)}
	 * or material's ID found at <code>path + ".material"</code> is invalid
	 * @throws NumberFormatException If color found at <code>path + ".leather-color-armor"</code> is not
	 * <code>null</code> and is invalid (does not respect the format required by {@link Color#decode(String)})
	 */
	public IconLayout(Configuration configuration, String path) {
		Icon icon = GUIManager.getInstance().createIcon(configuration, path);
		id = icon.getID();
		material = icon.getMaterial();
		keepOpen = icon.isKeepOpen();
		glowing = icon.isGlowing();
		amount = icon.getAmount();
		damage = icon.getDamage();
		skullOwner = icon.getSkullOwner();
		skullTextureURL = icon.getSkullTextureURL();
		leatherArmorColor = icon.getLeatherArmorColor();
		commands = icon.getCommands();
		itemFlags = icon.getItemFlags();
		displayNames = icon.getDisplayNames();
		lores = icon.getLores();
		enchantments = icon.getEnchantments();
	}
	
	/**
	 * Constructs a new icon layout specifying onlt the essential
	 * options and assuming the others as their default values.
	 * Note that {@link #IconLayout(Configuration, String)} is capable of reading icon layouts from
	 * {@link Configuration}s. Use this constructor just to obtain custom icon layouts via code.
	 * 
	 * @param id Icon layout's ID
	 * @param material Icon layout's material
	 * @param keepOpen Whether the GUI will remain open on click
	 * @param glowing Whether the glowing effect should be applied
	 * @param amount Icon layout's items' amount [0 - 64]
	 * @param damage Icon layout's items' damage [0 - max durability]
	 */
	public IconLayout(
			String id,
			MaterialAdapter material,
			boolean keepOpen,
			boolean glowing,
			int amount,
			short damage
			) {
		this(
				id,
				material,
				keepOpen,
				glowing,
				amount,
				damage,
				null,
				null,
				null,
				new ArrayList<>(),
				new ArrayList<>(),
				new HashMap<>(),
				new HashMap<>(),
				new HashMap<>()
				);
	}
	
	/**
	 * Constructs a new icon layout specifying all the available options.
	 * Note that {@link #IconLayout(Configuration, String)} is capable of reading icon layouts from
	 * {@link Configuration}s. Use this constructor just to obtain custom icon layouts via code.
	 * 
	 * @param id Icon layout's ID
	 * @param material Icon layout's material
	 * @param keepOpen Whether the GUI will remain open on click
	 * @param glowing Whether the glowing effect should be applied
	 * @param amount Icon layout's items' amount [0 - 64]
	 * @param damage Icon layout's items' damage [0 - max durability]
	 * @param skullOwner Icon layout's skull's owner
	 * @param skullTextureURL Icon layout's skull's texture's URL
	 * @param leatherArmorColor Icon layout's leather armor's color
	 * @param commands Icon layout's commands executed on click
	 * @param itemFlags Icon layout's item flags
	 * @param displayNames Icon layout's display names
	 * @param lores Icon layout's lores
	 * @param enchantments Icon layout's enchantments
	 */
	public IconLayout(
			String id,
			MaterialAdapter material,
			boolean keepOpen,
			boolean glowing,
			int amount,
			short damage,
			@Nullable(why = "Skull's owner is removed when null") String skullOwner,
			@Nullable(why = "Skull's texture's URL is removed when null") String skullTextureURL,
			@Nullable(why = "Color is set to #A06540 when null") Color leatherArmorColor,
			@NotNull List<String> commands,
			@NotNull List<ItemFlagAdapter> itemFlags,
			@NotNull Map<Language, String> displayNames,
			@NotNull Map<Language, List<String>> lores,
			@NotNull Map<EnchantmentAdapter, Integer> enchantments
			) {
		this.id = id;
		this.material = material;
		this.keepOpen = keepOpen;
		this.glowing = glowing;
		this.amount = amount;
		this.damage = damage;
		this.skullOwner = skullOwner;
		this.skullTextureURL = skullTextureURL;
		this.leatherArmorColor = leatherArmorColor;
		this.commands = commands;
		this.itemFlags = itemFlags;
		this.displayNames = displayNames;
		this.lores = lores;
		this.enchantments = enchantments;
	}
	
	/**
	 * Gets this icon layout's ID.
	 * 
	 * @return Icon layout's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this icon layout's material.
	 * 
	 * @return Icon layout's material
	 */
	public MaterialAdapter getMaterial() {
		return material;
	}
	
	/**
	 * Sets this icon layout's material.
	 * 
	 * @param material Icon layout's material
	 */
	public void setMaterial(MaterialAdapter material) {
		this.material = material;
	}
	
	/**
	 * Checks if the GUI should be kept open when this icon layout is clicked.
	 * 
	 * @return Whether the GUI will remain open on click
	 */
	public boolean isKeepOpen() {
		return keepOpen;
	}
	
	/**
	 * Sets if the GUI should be kept open when this icon layout is clicked.
	 * 
	 * @param keepOpen Whether the GUI will remain open on click
	 */
	public void setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
	}
	
	/**
	 * Checks if the glowing effect should be applied to this icon layout.
	 * 
	 * @return Whether the glowing effect should be applied
	 */
	public boolean isGlowing() {
		return glowing;
	}
	
	/**
	 * Sets if the glowing effect should be applied to this icon layout.
	 * 
	 * @param glowing Whether the glowing effect should be applied
	 */
	public void setGlowing(boolean glowing) {
		this.glowing = glowing;
	}
	
	/**
	 * Gets this icon layout's items' amount.
	 * 
	 * @return Icon layout's items' amount [0 - 64]
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Sets this icon layout's items' amount.
	 * 
	 * @param amount Icon layout's items' amount [0 - 64]
	 */
	public void setAmount(int amount) {
		this.amount = amount < 0 ? 0 : amount > 64 ? 64 : amount;
	}
	
	/**
	 * Gets this icon layout's items' damage.
	 * 
	 * @return Icon layout's items' damage [0 - max durability]
	 */
	public short getDamage() {
		return damage;
	}
	
	/**
	 * Sets this icon layout's items' damage.
	 * 
	 * @param damage Icon layout's items' damage [0 - max durability]
	 */
	public void setDamage(short damage) {
		this.damage = damage < 0 ? 0 : damage;
	}
	
	/**
	 * Gets this icon layout's skull's owner.
	 * Will return <code>null</code> if the skull's owner has not been specified.
	 * 
	 * @return Icon layout's skull's owner
	 */
	@Nullable(why = "Icon's skull's owner may not have been specified")
	public String getSkullOwner() {
		return skullOwner;
	}
	
	/**
	 * Sets this icon layout's skull's owner.
	 * You can specify <code>null</code> to remove the skull's owner.
	 * 
	 * @param skullOwner Icon layout's skull's owner
	 */
	public void setSkullOwner(@Nullable(why = "Skull's owner is removed when null") String skullOwner) {
		this.skullOwner = skullOwner;
	}
	
	/**
	 * Gets this icon layout's skull's texture's URL.
	 * Will return <code>null</code> if the skull's texture's URL has not been specified.
	 * 
	 * @return Icon layout's skull's texture's URL
	 */
	@Nullable(why = "Icon's skull's texture's URL may not have been specified")
	public String getSkullTextureURL() {
		return skullTextureURL;
	}
	
	/**
	 * Sets this icon layout's skull's texture's URL.
	 * You can specify <code>null</code> to remove the skull's texture's URL.
	 * 
	 * @param skullTextureURL Icon layout's skull's texture's URL
	 */
	public void setSkullTextureURL(@Nullable(why = "Skull's owner is removed when null") String skullTextureURL) {
		this.skullTextureURL = skullTextureURL;
	}
	
	/**
	 * Gets this icon layout's leather armor's color.
	 * Will return <code>null</code> if the leather armor's color has not been specified.
	 * 
	 * @return Icon layout's leather armor's color
	 */
	@Nullable(why = "Icon's leather armor's color may not have been specified")
	public Color getLeatherArmorColor() {
		return leatherArmorColor;
	}
	
	/**
	 * Sets this icon layout's leather armor's color.
	 * If you need to reset the color to the default value, specify
	 * <code>null</code> and the following hex code will be applied: "#A06540".
	 * 
	 * @param leatherArmorColor Icon layout's leather armor's color
	 */
	public void setLeatherArmorColor(@Nullable(why = "Color is set to #A06540 when null") Color leatherArmorColor) {
		this.leatherArmorColor = leatherArmorColor;
	}
	
	/**
	 * Gets the commands that will be executed when this icon layout is clicked.
	 * You may modify the returned list.
	 * 
	 * @return Icon layout's commands executed on click
	 */
	public List<String> getCommands() {
		return commands;
	}
	
	/**
	 * Gets this icon layout's item flags.
	 * You may modify the returned list.
	 * 
	 * @return Icon layout's item flags
	 */
	public List<ItemFlagAdapter> getItemFlags() {
		return itemFlags;
	}
	
	/**
	 * Gets this icon layout's display names.
	 * You may modify the returned map.
	 * 
	 * @return Icon layout's display names
	 */
	public Map<Language, String> getDisplayNames() {
		return displayNames;
	}
	
	/**
	 * Gets this icon layout's lores.
	 * You may modify the returned map.
	 * 
	 * @return Icon layout's lores
	 */
	public Map<Language, List<String>> getLores() {
		return lores;
	}
	
	/**
	 * Gets this icon layout's enchantments.
	 * You may modify the returned map.
	 * 
	 * @return Icon layout's enchantments
	 */
	public Map<EnchantmentAdapter, Integer> getEnchantments() {
		return enchantments;
	}
	
}