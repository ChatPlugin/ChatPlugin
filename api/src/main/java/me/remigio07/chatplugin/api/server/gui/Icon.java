/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.server.gui;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.ValueContainer;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.EnchantmentAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemFlagAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;

/**
 * Represents an icon inside of a {@link GUI}.
 */
public class Icon {
	
	/**
	 * Pattern representing the allowed icon IDs.
	 * 
	 * <p><strong>Regex:</strong> <a href="https://regex101.com/r/9iSnkI/1"><code>^[a-zA-Z0-9-_]{2,36}$</code></a></p>
	 * 
	 * @see #isValidIconID(String)
	 */
	public static final Pattern ICON_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	private String id, skullOwner, skullTextureURL, permission;
	private IconType type;
	private MaterialAdapter material;
	private ValueContainer<Short> amount;
	private short damage;
	private boolean keepOpen, glowing;
	private int position;
	private Color leatherArmorColor;
	private List<String> commands;
	private List<ItemFlagAdapter> itemFlags;
	private Map<Language, String> displayNames;
	private Map<Language, List<String>> lores;
	private Map<EnchantmentAdapter, Integer> enchantments;
	
	/**
	 * Constructs a new icon specifying another icon's values.
	 * 
	 * <p><strong>Note:</strong> changes made to that icon's lists, maps and
	 * leather armor color will be reflected to this icon and vice versa.</p>
	 * 
	 * @param icon Icon to copy
	 */
	public Icon(Icon icon) {
		this(
				icon.getID(),
				icon.getType(),
				icon.getMaterial(),
				icon.getAmount(),
				icon.getDamage(),
				icon.isKeepOpen(),
				icon.isGlowing(),
				icon.getPosition(),
				icon.getSkullOwner(),
				icon.getSkullTextureURL(),
				icon.getLeatherArmorColor(),
				icon.getPermission(),
				icon.getCommands(),
				icon.getItemFlags(),
				icon.getDisplayNames(),
				icon.getLores(),
				icon.getEnchantments()
				);
	}
	
	/**
	 * Constructs a new icon specifying only the essential
	 * options and assuming the others as their default values.
	 * 
	 * <p><strong>Note:</strong> {@link GUIManager#createIcon(Configuration, String)} is capable of reading
	 * icons from {@link Configuration}s. Use this constructor just to obtain custom icons via code.</p>
	 * 
	 * @param id Icon's ID
	 * @param type Icon's type
	 * @param material Icon's material
	 * @param amount Icon's items' amount [1 - 64]
	 * @param damage Icon's items' damage [0 - max durability]
	 * @param keepOpen Whether the GUI will remain open on click
	 * @param glowing Whether the glowing effect should be applied
	 * @param position Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link #isValidIconID(String)}
	 */
	public Icon(
			String id,
			IconType type,
			MaterialAdapter material,
			ValueContainer<Short> amount,
			short damage,
			boolean keepOpen,
			boolean glowing,
			int position
			) {
		this(
				id,
				type,
				material,
				amount,
				damage,
				keepOpen,
				glowing,
				position,
				null,
				null,
				null,
				null,
				new CopyOnWriteArrayList<>(),
				new CopyOnWriteArrayList<>(),
				new ConcurrentHashMap<>(),
				new ConcurrentHashMap<>(),
				new ConcurrentHashMap<>()
				);
	}
	
	/**
	 * Constructs a new icon specifying all the available options.
	 * 
	 * <p><strong>Note:</strong> {@link GUIManager#createIcon(Configuration, String)} is capable of reading
	 * icons from {@link Configuration}s. Use this constructor just to obtain custom icons via code.</p>
	 * 
	 * <p>It is recommended to use thread-safe collections.</p>
	 * 
	 * @param id Icon's ID
	 * @param type Icon's type
	 * @param material Icon's material
	 * @param amount Icon's items' amount [1 - 64]
	 * @param damage Icon's items' damage [0 - max durability]
	 * @param keepOpen Whether the GUI will remain open on click
	 * @param glowing Whether the glowing effect should be applied
	 * @param position Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 * @param skullOwner Icon's skull's owner
	 * @param skullTextureURL Icon's skull's texture's URL
	 * @param leatherArmorColor Icon's leather armor's color
	 * @param permission Icon's permission
	 * @param commands Icon's commands executed on click
	 * @param itemFlags Icon's item flags
	 * @param displayNames Icon's display names
	 * @param lores Icon's lores
	 * @param enchantments Icon's enchantments
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link #isValidIconID(String)}
	 */
	public Icon(
			String id,
			IconType type,
			MaterialAdapter material,
			ValueContainer<Short> amount,
			short damage,
			boolean keepOpen,
			boolean glowing,
			int position,
			@Nullable(why = "Skull's owner is removed when null") String skullOwner,
			@Nullable(why = "Skull's texture's URL is removed when null") String skullTextureURL,
			@Nullable(why = "Color is set to #A06540 when null") Color leatherArmorColor,
			@Nullable(why = "No permission check is performed when null") String permission,
			@NotNull List<String> commands,
			@NotNull List<ItemFlagAdapter> itemFlags,
			@NotNull Map<Language, String> displayNames,
			@NotNull Map<Language, List<String>> lores,
			@NotNull Map<EnchantmentAdapter, Integer> enchantments
			) {
		if (!isValidIconID(id))
			throw new IllegalArgumentException("Icon ID \"" + id + "\" does not respect the following pattern: \"" + ICON_ID_PATTERN.pattern() + "\"");
		this.id = id;
		this.type = type;
		this.material = material;
		this.amount = amount;
		this.damage = damage;
		this.keepOpen = keepOpen;
		this.glowing = glowing;
		this.position = position;
		this.skullOwner = skullOwner;
		this.skullTextureURL = skullTextureURL;
		this.leatherArmorColor = leatherArmorColor;
		this.permission = permission;
		this.commands = commands;
		this.itemFlags = itemFlags;
		this.displayNames = displayNames;
		this.lores = lores;
		this.enchantments = enchantments;
	}
	
	/**
	 * Gets this icon's ID.
	 * 
	 * @return Icon's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this icon's type.
	 * 
	 * @return Icon's type
	 */
	public IconType getType() {
		return type;
	}
	
	/**
	 * Gets this icon's material.
	 * 
	 * @return Icon's material
	 */
	public MaterialAdapter getMaterial() {
		return material;
	}
	
	/**
	 * Sets this icon's material.
	 * 
	 * @param material Icon's material
	 * @return This icon
	 */
	public Icon setMaterial(@NotNull MaterialAdapter material) {
		this.material = material;
		return this;
	}
	
	/**
	 * Gets this icon's items' amount.
	 * 
	 * @return Icon's items' amount [1 - 64]
	 */
	public ValueContainer<Short> getAmount() {
		return amount;
	}
	
	/**
	 * Sets this icon's items' amount.
	 * 
	 * @param amount Icon's items' amount [1 - 64]
	 * @return This icon
	 */
	public Icon setAmount(ValueContainer<Short> amount) {
		this.amount = amount.placeholder() == null ? amount.value() < 1 ? new ValueContainer<>((short) 1) : amount.value() > 64 ? new ValueContainer<>((short) 64) : amount : amount;
		return this;
	}
	
	/**
	 * Gets this icon's items' damage.
	 * 
	 * @return Icon's items' damage [0 - max durability]
	 */
	public short getDamage() {
		return damage;
	}
	
	/**
	 * Sets this icon's items' damage.
	 * 
	 * @param damage Icon's items' damage [0 - max durability]
	 * @return This icon
	 */
	public Icon setDamage(short damage) {
		this.damage = damage < 0 ? 0 : damage;
		return this;
	}
	
	/**
	 * Checks if the GUI should be kept open when this icon is clicked.
	 * 
	 * @return Whether the GUI will remain open on click
	 */
	public boolean isKeepOpen() {
		return keepOpen;
	}
	
	/**
	 * Sets if the GUI should be kept open when this icon is clicked.
	 * 
	 * @param keepOpen Whether the GUI will remain open on click
	 * @return This icon
	 */
	public Icon setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
		return this;
	}
	
	/**
	 * Checks if the glowing effect should be applied to this icon.
	 * 
	 * @return Whether the glowing effect should be applied
	 */
	public boolean isGlowing() {
		return glowing;
	}
	
	/**
	 * Sets if the glowing effect should be applied to this icon.
	 * 
	 * @param glowing Whether the glowing effect should be applied
	 * @return This icon
	 */
	public Icon setGlowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}
	
	/**
	 * Gets this icon's position in the GUI's inventory.
	 * 
	 * @return Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets this icon's position in the GUI's inventory.
	 * 
	 * @param position Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 * @return This icon
	 */
	public Icon setPosition(int position) {
		this.position = position < 0 ? 0 : position;
		return this;
	}
	
	/**
	 * Gets this icon's skull's owner.
	 * 
	 * <p>Will return <code>null</code> if the skull's owner has not been specified.</p>
	 * 
	 * @return Icon's skull's owner
	 */
	@Nullable(why = "Icon's skull's owner may not have been specified")
	public String getSkullOwner() {
		return skullOwner;
	}
	
	/**
	 * Sets this icon's skull's owner.
	 * 
	 * <p>You can specify <code>null</code> to remove the skull's owner.</p>
	 * 
	 * @param skullOwner Icon's skull's owner
	 * @return This icon
	 */
	public Icon setSkullOwner(@Nullable(why = "Skull's owner is removed when null") String skullOwner) {
		this.skullOwner = skullOwner;
		return this;
	}
	
	/**
	 * Gets this icon's skull's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if the skull's texture's URL has not been specified.</p>
	 * 
	 * @return Icon's skull's texture's URL
	 */
	@Nullable(why = "Icon's skull's texture's URL may not have been specified")
	public String getSkullTextureURL() {
		return skullTextureURL;
	}
	
	/**
	 * Sets this icon's skull's texture's URL.
	 * 
	 * <p>You can specify <code>null</code> to remove the skull's texture's URL.</p>
	 * 
	 * @param skullTextureURL Icon's skull's texture's URL
	 * @return This icon
	 */
	public Icon setSkullTextureURL(@Nullable(why = "Skull's texture's URL is removed when null") String skullTextureURL) {
		this.skullTextureURL = skullTextureURL;
		return this;
	}
	
	/**
	 * Gets this icon's leather armor's color.
	 * 
	 * <p>Will return <code>null</code> if the leather armor's color has not been specified.</p>
	 * 
	 * @return Icon's leather armor's color
	 */
	@Nullable(why = "Icon's leather armor's color may not have been specified")
	public Color getLeatherArmorColor() {
		return leatherArmorColor;
	}
	
	/**
	 * Sets this icon's leather armor's color.
	 * 
	 * <p>If you need to reset the color to the default value, specify
	 * <code>null</code> and the following hex code will be applied: "#A06540".</p>
	 * 
	 * @param leatherArmorColor Icon's leather armor's color
	 * @return This icon
	 */
	public Icon setLeatherArmorColor(@Nullable(why = "Color is set to #A06540 when null") Color leatherArmorColor) {
		this.leatherArmorColor = leatherArmorColor;
		return this;
	}
	
	/**
	 * Gets this icon's permission.
	 * 
	 * <p>Will return <code>null</code> if this icon does
	 * not require any permission to be clicked.</p>
	 * 
	 * @return Icon's permission
	 */
	@Nullable(why = "No permission check is performed when null")
	public String getPermission() {
		return permission;
	}
	
	/**
	 * Sets this icon's permission.
	 * 
	 * <p>Specify <code>null</code> if this icon should
	 * not require any permission to be clicked.</p>
	 * 
	 * @param permission Icon's permission
	 */
	public void setPermission(@Nullable(why = "No permission check is performed when null") String permission) {
		this.permission = permission;
	}
	
	/**
	 * Gets the commands that will be executed when this icon is clicked.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Icon's commands
	 */
	public List<String> getCommands() {
		return commands;
	}
	
	/**
	 * Sets the commands that will be executed when this icon is clicked.
	 * 
	 * @param commands Icon's commands
	 * @return This icon
	 */
	public Icon setCommands(@NotNull List<String> commands) {
		this.commands = commands;
		return this;
	}
	
	/**
	 * Gets this icon's item flags.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Icon's item flags
	 */
	public List<ItemFlagAdapter> getItemFlags() {
		return itemFlags;
	}
	
	/**
	 * Sets this icon's item flags.
	 * 
	 * @param itemFlags Icon's item flags
	 * @return This icon
	 */
	public Icon setItemFlags(@NotNull List<ItemFlagAdapter> itemFlags) {
		this.itemFlags = itemFlags;
		return this;
	}
	
	/**
	 * Gets this icon's display names.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Icon's display names
	 */
	public Map<Language, String> getDisplayNames() {
		return displayNames;
	}
	
	/**
	 * Sets this icon's display names.
	 * 
	 * @param displayNames Icon's display names
	 * @return This icon
	 */
	public Icon setDisplayNames(@NotNull Map<Language, String> displayNames) {
		this.displayNames = displayNames;
		return this;
	}
	
	/**
	 * Gets this icon's lores.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Icon's lores
	 */
	public Map<Language, List<String>> getLores() {
		return lores;
	}
	
	/**
	 * Sets this icon's lores.
	 * 
	 * @param lores Icon's lores
	 * @return This icon
	 */
	public Icon setLores(@NotNull Map<Language, List<String>> lores) {
		this.lores = lores;
		return this;
	}
	
	/**
	 * Gets this icon's enchantments.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Icon's enchantments
	 */
	public Map<EnchantmentAdapter, Integer> getEnchantments() {
		return enchantments;
	}
	
	/**
	 * Sets this icon's enchantments.
	 * 
	 * @param enchantments Icon's enchantments
	 * @return This icon
	 */
	public Icon setEnchantments(@NotNull Map<EnchantmentAdapter, Integer> enchantments) {
		this.enchantments = enchantments;
		return this;
	}
	
	/**
	 * Generates an item stack for the specified language using this icon's values and
	 * translating placeholders using {@link #formatPlaceholders(String, GUI, Language, boolean)} and
	 * {@link #formatPlaceholders(List, GUI, Language, boolean)} translating colors when necessary.
	 * 
	 * <p>This method takes no time to execute but it does not consider {@link #getSkullOwner()}.
	 * To change a player head icon skull's owner use {@link #updateSkullOwner(ItemStackAdapter, GUI, Language)}.</p>
	 * 
	 * <p><strong>Note:</strong> {@link #getSkullTextureURL()} is considered because it takes no time to set.</p>
	 * 
	 * @param gui GUI containing this icon
	 * @param language Language used to translate the placeholders
	 * @return New item stack
	 */
	public ItemStackAdapter toItemStackAdapter(GUI gui, Language language) {
		short amount;
		
		if (this.amount.value() == null)
			try {
				amount = Short.valueOf(formatPlaceholders(this.amount.placeholder(), gui, language, false));
			} catch (NumberFormatException e) {
				amount = 1;
			}
		else amount = this.amount.value();
		
		ItemStackAdapter itemStack = new ItemStackAdapter(material, amount, damage)
				.setDisplayName(displayNames.get(language) == null ? null : formatPlaceholders(displayNames.get(language), gui, language, true))
				.setLore(lores.get(language) == null ? null : formatPlaceholders(lores.get(language), gui, language, true))
				.setLeatherArmorColor(leatherArmorColor)
				.setSkullTextureURL(skullTextureURL == null ? null : formatPlaceholders(skullTextureURL, gui, language, false))
				.addItemFlags(itemFlags.toArray(new ItemFlagAdapter[0]));
		
		if (glowing)
			itemStack.setGlowing();
		enchantments.forEach(itemStack::enchant);
		return itemStack;
	}
	
	/**
	 * Sets the skull's owner for the specified item stack if it is a player head.
	 * 
	 * <p>When the future is completed, you need to manually set the icon to the inventory
	 * using {@link InventoryAdapter#setItem(ItemStackAdapter, int)} to update it.</p>
	 * 
	 * <p>The future will be completed instantly in the following cases:
	 * 	<ul>
	 * 		<li><code>!</code>{@link ItemStackAdapter#isPlayerHead()}</li>
	 * 		<li>{@link #getSkullOwner()}<code> == null</code> (when skull owner is removed)</li>
	 * 		<li>{@link #getSkullOwner()}'s skin is cached (caching is automatic)</li>
	 * 	</ul>
	 * 
	 * Otherwise it will take some time to fetch the skin.
	 * 
	 * @param itemStack Original item stack
	 * @param gui GUI containing this icon
	 * @param language Language used to translate the placeholders
	 * @return New item stack
	 * @see InventoryAdapter#setItem(ItemStackAdapter, int)
	 */
	public CompletableFuture<ItemStackAdapter> updateSkullOwner(ItemStackAdapter itemStack, GUI gui, Language language) {
		CompletableFuture<ItemStackAdapter> future = new CompletableFuture<>();
		
		try {
			itemStack.setSkullOwner(skullOwner == null ? null : formatPlaceholders(skullOwner, gui, language, false), true).thenAccept(future::complete);
		} catch (IllegalArgumentException e) {
			future.complete(itemStack);
		} return future;
	}
	
	/**
	 * Translates an input string containing placeholders for the specified
	 * GUI and language using {@link GUI#getStringPlaceholdersTranslator()}.
	 * 
	 * @param input Input containing placeholders
	 * @param gui GUI containing this icon
	 * @param language Language used to translate the placeholders
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public String formatPlaceholders(String input, GUI gui, Language language, boolean translateColors) {
		return translateColors ? ChatColor.translate(gui.getStringPlaceholdersTranslator() == null ? input : gui.getStringPlaceholdersTranslator().apply(input, language, this))
				: gui.getStringPlaceholdersTranslator() == null ? input : gui.getStringPlaceholdersTranslator().apply(input, language, this);
	}
	
	/**
	 * Translates an input string list containing placeholders for the specified
	 * GUI and language using {@link GUI#getStringListPlaceholdersTranslator()}.
	 * 
	 * @param input Input containing placeholders
	 * @param gui GUI containing this icon
	 * @param language Language used to translate the placeholders
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public List<String> formatPlaceholders(List<String> input, GUI gui, Language language, boolean translateColors) {
		return gui.getStringListPlaceholdersTranslator() == null ? input.stream().map(str -> formatPlaceholders(str, gui, language, translateColors)).collect(Collectors.toList())
				: translateColors ? ChatColor.translate(gui.getStringListPlaceholdersTranslator().apply(input, language, this)) : gui.getStringListPlaceholdersTranslator().apply(input, language, this);
	}
	
	/**
	 * Checks if the specified String is a valid icon ID.
	 * 
	 * @param iconID Icon ID to check
	 * @return Whether the specified icon ID is valid
	 * @see #ICON_ID_PATTERN
	 */
	public static boolean isValidIconID(String iconID) {
		return ICON_ID_PATTERN.matcher(iconID).matches();
	}
	
	/**
	 * Calculates an icon's position using its X and Y coordinates.
	 * 
	 * @param x Icon's X coordinate [1 - 9]
	 * @param y Icon's Y coordinate [1 - ({@link InventoryAdapter#getSize()} / 9)]
	 * @return Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 * @see #calcIconCoords(int)
	 */
	public static int calcIconPosition(int x, int y) {
		return ((y < 1 ? 1 : y > 6 ? 6 : y) - 1) * 9 + ((x < 1 ? 1 : x > 9 ? 9 : x) - 1);
	}
	
	/**
	 * Calculates an icon's X and Y coordinates using its position.
	 * 
	 * @param position Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
	 * @return Icon's X and Y coordinates
	 * @see #calcIconPosition(int, int)
	 */
	public static int[] calcIconCoords(int position) {
		position = position < 0 ? 0 : position > 53 ? 53 : position;
		int y = position / 9 + 1;
		return new int[] { position - 9 * (y - 1) + 1, y };
	}
	
}
