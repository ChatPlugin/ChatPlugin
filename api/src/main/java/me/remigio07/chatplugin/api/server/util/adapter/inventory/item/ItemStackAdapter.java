/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

package me.remigio07.chatplugin.api.server.util.adapter.inventory.item;

import java.awt.Color;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.property.item.UseLimitProperty;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) item stack adapter.
 */
public class ItemStackAdapter implements Cloneable {
	
	private Object itemStack;
	private MaterialAdapter type;
	private List<ItemFlagAdapter> itemFlags = new CopyOnWriteArrayList<>();
	private Map<EnchantmentAdapter, Integer> enchantments = new ConcurrentHashMap<>();
	private String skullOwner;
	private boolean itemMeta = true;
	private static Map<String, Object[]> skullsCache = new ConcurrentHashMap<>();
	private static List<String> nonPremiumUsernames = new CopyOnWriteArrayList<>();
	
	/**
	 * Constructs an item stack with the given material,
	 * default amount of 1 and default damage of 0.
	 * 
	 * @param type Item stack's type
	 */
	public ItemStackAdapter(MaterialAdapter type) {
		this(type, (short) 1);
	}
	
	/**
	 * Constructs an item stack with the given
	 * material and amount and default damage of 0.
	 * 
	 * @param type Item stack's type
	 * @param amount Amount of items
	 */
	public ItemStackAdapter(MaterialAdapter type, short amount) {
		this(type, amount, (short) 0);
	}
	
	/**
	 * Constructs an item stack with the
	 * given material, amount and damage.
	 * 
	 * @param type Item stack's type
	 * @param amount Amount of items [0 - 64]
	 * @param damage Item's damage [0 - {@link #getMaxDurability()}]
	 */
	@SuppressWarnings("deprecation")
	public ItemStackAdapter(MaterialAdapter type, short amount, short damage) {
		this.type = type;
		amount = amount < 0 ? 0 : amount > 64 ? 64 : amount;
		damage = damage < 0 ? 0 : damage;
		
		if (Environment.isSponge()) {
			itemStack = ItemStack.builder().itemType(type.spongeValue()).quantity(amount).build();
			
			setDamage(damage);
		} else itemStack = new org.bukkit.inventory.ItemStack(type.bukkitValue(), amount, damage);
		
		if (Environment.isBukkit() && !Bukkit.getItemFactory().isApplicable(bukkitValue().getItemMeta(), (org.bukkit.inventory.ItemStack) itemStack))
			itemMeta = false;
	}
	
	/**
	 * Constructs an item stack adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.inventory.ItemStack} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.item.inventory.ItemStack} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param itemStack Item stack object
	 */
	@SuppressWarnings("deprecation")
	public ItemStackAdapter(Object itemStack) {
		this.itemStack = itemStack;
		
		if (Environment.isBukkit()) {
			type = new MaterialAdapter(((org.bukkit.inventory.ItemStack) itemStack).getType().name());
			
			if (((org.bukkit.inventory.ItemStack) itemStack).hasItemMeta()) {
				ItemMeta meta = ((org.bukkit.inventory.ItemStack) itemStack).getItemMeta();
				
				meta.getEnchants().forEach((enchantment, level) -> enchantments.put(EnchantmentAdapter.value(enchantment.getName()), level));
				meta.getItemFlags().stream().map(itemFlag -> ItemFlagAdapter.valueOf(itemFlag.name())).filter(Objects::nonNull).distinct().forEach(itemFlag -> itemFlags.add(itemFlag));
				
				if (isPlayerHead())
					skullOwner = ((SkullMeta) meta).getOwner();
			} else itemMeta = false;
		} else {
			type = new MaterialAdapter(((ItemStack) itemStack).getType().getId().substring(10));
			
			for (ItemFlagAdapter itemFlag : ItemFlagAdapter.values())
				if (((ItemStack) itemStack).getOrElse(itemFlag.spongeValue(), false))
					itemFlags.add(itemFlag);
			if (!((ItemStack) itemStack).getOrCreate(EnchantmentData.class).get().enchantments().isEmpty())
				((ItemStack) itemStack).get(EnchantmentData.class).get().enchantments().get().forEach(enchantment -> enchantments.put(EnchantmentAdapter.value(enchantment.getType().getId().substring(10)), enchantment.getLevel()));
			if (isPlayerHead())
				spongeValue().get(RepresentedPlayerData.class).ifPresent(player -> player.owner().get().getName().ifPresent(name -> skullOwner = name));
		}
	}
	
	@Override
	public ItemStackAdapter clone() {
		ItemStackAdapter itemStack = new ItemStackAdapter(type.clone(), getAmount(), getDamage());
		
		if (hasDisplayName())
			itemStack.setDisplayName(getDisplayName());
		if (hasLore())
			itemStack.setLore(new ArrayList<>(getLore()));
		if (isEnchanted())
			enchantments.forEach(itemStack::enchant);
		if (isPlayerHead()) {
			if (getSkullOwner() != null)
				itemStack.setSkullOwner(getSkullOwner(), getSkullTextureURL() != null);
			else if (getSkullTextureURL() != null)
				itemStack.setSkullTextureURL(getSkullTextureURL());
		} else if (isLeatherArmor())
			itemStack.setLeatherArmorColor(getLeatherArmorColor());
		itemStack.addItemFlags(itemFlags.toArray(new ItemFlagAdapter[0]));
		return itemStack;
	}
	
	/**
	 * Gets the item stack adapted for Bukkit environments.
	 * 
	 * <p><strong>Note:</strong> do not modify the returned item stack's
	 * (meta)data manually. Only use the provided wrapper methods.</p>
	 * 
	 * @return Bukkit-adapted item stack
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.inventory.ItemStack bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.inventory.ItemStack) itemStack;
		throw new UnsupportedOperationException("Unable to adapt item stack to a Bukkit's ItemStack on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item stack adapted for Sponge environments.
	 * 
	 * <p><strong>Note:</strong> do not modify the returned item stack's
	 * (meta)data manually. Only use the provided wrapper methods.</p>
	 * 
	 * @return Sponge-adapted item stack
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.item.inventory.ItemStack spongeValue() {
		if (Environment.isSponge())
			return (ItemStack) itemStack;
		throw new UnsupportedOperationException("Unable to adapt item stack to a Sponge's ItemStack on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this item stack's type.
	 * 
	 * @return Item stack's type
	 */
	public MaterialAdapter getType() {
		return type;
	}
	
	/**
	 * Checks if this item stack has item meta.
	 * 
	 * <p>It only applies to Bukkit environments.</p>
	 * 
	 * @return Whether this item stack has item meta
	 */
	public boolean hasItemMeta() {
		return itemMeta;
	}
	
	/**
	 * Gets the amount of items in this item stack.
	 * 
	 * @return Amount of items
	 */
	public short getAmount() {
		return (short) (Environment.isBukkit() ? bukkitValue().getAmount() : spongeValue().getQuantity());
	}
	
	/**
	 * Sets the amount of items in this item stack.
	 * 
	 * @param amount Amount of items
	 * @return This item stack
	 */
	public ItemStackAdapter setAmount(short amount) {
		if (Environment.isBukkit())
			bukkitValue().setAmount(amount);
		else spongeValue().setQuantity(amount);
		return this;
	}
	
	/**
	 * Gets this item stack's max durability.
	 * 
	 * @return Item's max durability
	 */
	public short getMaxDurability() {
		return Environment.isBukkit() ? type.bukkitValue().getMaxDurability() : spongeValue().getProperty(UseLimitProperty.class).isPresent() ? spongeValue().getProperty(UseLimitProperty.class).get().getValue().shortValue() : 0;
	}
	
	/**
	 * Gets this item stack's current durability.
	 * 
	 * @return Item's durability
	 */
	@SuppressWarnings("deprecation")
	public short getDurability() {
		return (short) (Environment.isBukkit() ? getMaxDurability() - bukkitValue().getDurability() : Integer.valueOf(spongeValue().get(Keys.ITEM_DURABILITY).orElse(0)).shortValue());
	}
	
	/**
	 * Sets this item stack's current durability.
	 * 
	 * @param durability Item's durability
	 * @return This item stack
	 */
	public ItemStackAdapter setDurability(short durability) {
		if (Environment.isBukkit())
			setDamage((short) (getMaxDurability() - durability));
		else if (spongeValue().supports(DurabilityData.class))
			spongeValue().offer(Keys.ITEM_DURABILITY, (int) durability);
		return this;
	}
	
	/**
	 * Gets this item stack's current damage.
	 * 
	 * @return Item's damage
	 */
	@SuppressWarnings("deprecation")
	public short getDamage() {
		return (short) (Environment.isBukkit() ? bukkitValue().getDurability() : spongeValue().supports(DurabilityData.class) ? getMaxDurability() - getDurability() : SpongeItemStack.getDamage(spongeValue()));
	}
	
	/**
	 * Sets this item stack's current damage.
	 * 
	 * @param damage Item's damage
	 * @return This item stack
	 */
	@SuppressWarnings("deprecation")
	public ItemStackAdapter setDamage(short damage) {
		if (Environment.isBukkit())
			bukkitValue().setDurability(damage);
		else if (spongeValue().supports(DurabilityData.class))
			setDurability((short) (getMaxDurability() - damage));
		else itemStack = SpongeItemStack.setDamage(spongeValue(), damage);
		return this;
	}
	
	/**
	 * Gets the enchantments applied to this item stack.
	 * 
	 * @return Item's enchantments
	 */
	public Map<EnchantmentAdapter, Integer> getEnchantments() {
		return enchantments;
	}
	
	/**
	 * Adds an enchantment to this item stack.
	 * 
	 * @param enchantment Enchantment to add
	 * @param level Enchantment's level
	 * @return This item stack
	 */
	public ItemStackAdapter enchant(EnchantmentAdapter enchantment, int level) {
		if (Environment.isBukkit())
			bukkitValue().addUnsafeEnchantment(enchantment.bukkitValue(), level);
		else SpongeItemStack.enchant(spongeValue(), enchantment.spongeValue(), level);
		enchantments.put(enchantment, level);
		return this;
	}
	
	/**
	 * Adds enchantments to this item stack.
	 * 
	 * @param enchantments Enchantments to add
	 * @return This item stack
	 */
	public ItemStackAdapter enchant(Map<EnchantmentAdapter, Integer> enchantments) {
		enchantments.forEach(this::enchant);
		return this;
	}
	
	/**
	 * Removes an enchantment from this item stack.
	 * 
	 * @param enchantment Enchantment to remove
	 * @return This item stack
	 */
	public ItemStackAdapter disenchant(EnchantmentAdapter enchantment) {
		if (Environment.isBukkit())
			bukkitValue().removeEnchantment(enchantment.bukkitValue());
		else SpongeItemStack.disenchant(spongeValue(), enchantment.spongeValue());
		enchantments.remove(enchantment);
		return this;
	}
	
	/**
	 * Removes all enchantments from this item stack.
	 * 
	 * @return This item stack
	 */
	public ItemStackAdapter disenchant() {
		enchantments.keySet().forEach(this::disenchant);
		return this;
	}
	
	/**
	 * Checks if this item stack has any enchantments.
	 * 
	 * @return Whether this item is enchanted
	 */
	public boolean isEnchanted() {
		return !enchantments.isEmpty();
	}
	
	/**
	 * Applies the glowing effect to this item.
	 * 
	 * @return This item stack
	 * @see #isGlowing()
	 */
	public ItemStackAdapter setGlowing() {
		return setGlowing(true);
	}
	
	/**
	 * Applies/removes the glowing effect to/from this item.
	 * 
	 * @param glowing Whether this item is glowing
	 * @return This item stack
	 * @see #isGlowing()
	 */
	public ItemStackAdapter setGlowing(boolean glowing) {
		if (glowing) {
			enchant(EnchantmentAdapter.UNBREAKING, 1);
			addItemFlags(ItemFlagAdapter.HIDE_ENCHANTMENTS);
		} else {
			disenchant();
			removeItemFlags(ItemFlagAdapter.HIDE_ENCHANTMENTS);
		} return this;
	}
	
	/**
	 * Checks if this item has the glowing effect.
	 * 
	 * <p>An item is glowing when it {@link #isEnchanted()}
	 * and {@link #getItemFlags()} contains
	 * {@link ItemFlagAdapter#HIDE_ENCHANTMENTS}).</p>
	 * 
	 * @return Whether this item is glowing
	 */
	public boolean isGlowing() {
		return isEnchanted() && itemFlags.contains(ItemFlagAdapter.HIDE_ENCHANTMENTS);
	}
	
	/**
	 * Gets this item's display name.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #hasDisplayName()}.</p>
	 * 
	 * @return Item's display name
	 */
	@Nullable(why = "Null when item does not have a display name")
	public String getDisplayName() {
		return Environment.isBukkit() ? itemMeta ? bukkitValue().getItemMeta().getDisplayName() : null : spongeValue().getOrCreate(DisplayNameData.class).get().displayName().get().toPlain();
	}
	
	/**
	 * Checks if this item has a display name.
	 * 
	 * @return Whether this item has a display name
	 */
	public boolean hasDisplayName() {
		return Environment.isBukkit() ? itemMeta ? bukkitValue().getItemMeta().hasDisplayName() : false : spongeValue().getValue(Keys.DISPLAY_NAME).isPresent();
	}
	
	/**
	 * Sets this item's display name.
	 * 
	 * <p>You can specify <code>null</code> to remove the display name.</p>
	 * 
	 * @param displayName Display name to set
	 * @return This item stack
	 */
	public ItemStackAdapter setDisplayName(@Nullable(why = "Display name is removed when null") String displayName) {
		if (Environment.isSponge()) {
			if (displayName == null)
				spongeValue().remove(Keys.DISPLAY_NAME);
			else spongeValue().offer(Keys.DISPLAY_NAME, Utils.serializeSpongeText(displayName, false));
		} else if (itemMeta) {
			ItemMeta meta = bukkitValue().getItemMeta();
			
			meta.setDisplayName(displayName);
			bukkitValue().setItemMeta(meta);
		} return this;
	}
	
	/**
	 * Gets this item's lore.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #hasLore()}.</p>
	 * 
	 * @return Item's lore
	 */
	@Nullable(why = "Null when item does not have a lore")
	public List<String> getLore() {
		return Environment.isBukkit() ? itemMeta ? bukkitValue().getItemMeta().getLore() : null : SpongeItemStack.toStringList(spongeValue().getOrCreate(LoreData.class).get().lore().get());
	}
	
	/**
	 * Checks if this item has a lore.
	 * 
	 * @return Whether this item has a lore
	 */
	public boolean hasLore() {
		return Environment.isBukkit() ? itemMeta ? bukkitValue().getItemMeta().hasLore() : false : spongeValue().getValue(Keys.ITEM_LORE).isPresent();
	}
	
	/**
	 * Sets this item's lore.
	 * 
	 * <p>You can specify <code>null</code> to remove the lore.</p>
	 * 
	 * @param lore Lore to set
	 * @return This item stack
	 */
	public ItemStackAdapter setLore(@Nullable(why = "Lore is removed when null") List<String> lore) {
		if (Environment.isSponge()) {
			if (lore == null)
				spongeValue().remove(Keys.ITEM_LORE);
			else spongeValue().offer(Keys.ITEM_LORE, SpongeItemStack.toTextList(lore));
		} else if (itemMeta) {
			ItemMeta meta = bukkitValue().getItemMeta();
			
			meta.setLore(lore == null ? null : lore.stream().map(line -> line.replace("\n", " ").replace("\r\n", " ").replace("\r", " ")).collect(Collectors.toList()));
			bukkitValue().setItemMeta(meta);
		} return this;
	}
	
	/**
	 * Gets this item's item flags.
	 * 
	 * @return Item's item flags
	 */
	@NotNull
	public List<ItemFlagAdapter> getItemFlags() {
		return itemFlags;
	}
	
	/**
	 * Checks if this item has the specified item flag.
	 * 
	 * @param itemFlag Item flag to check
	 * @return Whether this item has given item flag
	 */
	public boolean hasItemFlag(ItemFlagAdapter itemFlag) {
		return itemFlags.contains(itemFlag);
	}
	
	/**
	 * Adds item flags to this item.
	 * 
	 * @param itemFlags Item flags to add
	 * @return This item stack
	 */
	public ItemStackAdapter addItemFlags(ItemFlagAdapter... itemFlags) {
		for (ItemFlagAdapter itemFlag : itemFlags) {
			if (!this.itemFlags.contains(itemFlag)) {
				if (Environment.isSponge()) {
					spongeValue().offer(itemFlag.spongeValue(), true);
				} else if (itemMeta) {
					ItemMeta meta = bukkitValue().getItemMeta();
					
					if (itemFlag == ItemFlagAdapter.HIDE_ATTRIBUTES && VersionUtils.getVersion().isAtLeast(Version.V1_20_5))
						meta.setAttributeModifiers(bukkitValue().getType().getDefaultAttributeModifiers(EquipmentSlot.HAND));
					meta.addItemFlags(itemFlag.bukkitValue());
					bukkitValue().setItemMeta(meta);
				} this.itemFlags.add(itemFlag);
			}
		} return this;
	}
	
	/**
	 * Removes item flags from this item.
	 * 
	 * @param itemFlags Item flags to remove
	 * @return This item stack
	 */
	public ItemStackAdapter removeItemFlags(ItemFlagAdapter... itemFlags) {
		for (ItemFlagAdapter itemFlag : itemFlags) {
			if (this.itemFlags.contains(itemFlag)) {
				if (Environment.isSponge()) {
					spongeValue().remove(itemFlag.spongeValue());
				} else if (itemMeta) {
					ItemMeta meta = bukkitValue().getItemMeta();
					
					if (itemFlag == ItemFlagAdapter.HIDE_ATTRIBUTES && VersionUtils.getVersion().isAtLeast(Version.V1_20_5))
						meta.setAttributeModifiers(null);
					meta.removeItemFlags(itemFlag.bukkitValue());
					bukkitValue().setItemMeta(meta);
				} this.itemFlags.remove(itemFlag);
			}
		} return this;
	}
	
	/**
	 * Checks if this item is a player head.
	 * 
	 * @return Whether this is a player head
	 */
	public boolean isPlayerHead() {
		String material = type.getID();
		return material.equals("PLAYER_HEAD") || (material.equals(Environment.isBukkit() ? "SKULL_ITEM" : "SKULL") && getDamage() == 3);
	}
	
	/**
	 * Gets this skull's owner.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isPlayerHead()}
	 * or if it does not have an owner specified.</p>
	 * 
	 * @return Skull's owner
	 */
	@Nullable(why = "Item may not be a skull or may not have an owner")
	public String getSkullOwner() {
		return skullOwner;
	}
	
	/**
	 * Sets this skull's owner.
	 * 
	 * <p>Will do nothing if the specified player name does not belong to a premium player.
	 * Specify <code>null</code> as <code>skullOwner</code> to remove the skull's owner.
	 * Specify <code>true</code> as <code>snapshot</code> to save a snapshot of the textures.</p>
	 * 
	 * <p>The latter forces clients to render the head using the original skin on Mojang servers.
	 * Useful to prevent some launchers like TLauncher from loading skins from their servers.</p>
	 * 
	 * @param skullOwner Skull's owner
	 * @param snapshot Whether to save a snapshot of the textures
	 * @return This item stack
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public CompletableFuture<ItemStackAdapter> setSkullOwner(@Nullable(why = "Skull's owner is removed when null") String skullOwner, boolean snapshot) {
		CompletableFuture<ItemStackAdapter> future = new CompletableFuture<>();
		
		if (isPlayerHead()) {
			if (skullOwner == null)
				setSkullProfile(null, null, null);
			else if (!PlayerManager.getInstance().isValidUsername(skullOwner))
				throw new IllegalArgumentException("Username \"" + skullOwner + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
			else if (!skullOwner.equalsIgnoreCase(this.skullOwner) && !nonPremiumUsernames.contains(skullOwner.toLowerCase())) {
				Object[] cache = skullsCache.get(skullOwner.toLowerCase());
				
				if (cache == null) {
					TaskManager.runAsync(() -> {
						try {
							UUID uuid = UUIDFetcher.getInstance().getOnlineUUID(skullOwner).get();
							String onlineName = UUIDFetcher.getInstance().getName(uuid).get();
							
							skullsCache.put(skullOwner.toLowerCase(), new Object[] { uuid, onlineName, UUIDFetcher.getInstance().getSkinTextureURL(uuid).get() });
							setSkullProfile(uuid, this.skullOwner = onlineName, snapshot ? (String) skullsCache.get(skullOwner.toLowerCase())[2] : null);
						} catch (InterruptedException | ExecutionException e) {
							if (e.getCause() instanceof NoSuchElementException)
								nonPremiumUsernames.add(skullOwner.toLowerCase());
							else LogManager.log("{0} occurred while setting a skull's owner for an item stack: {1}", 2, e.getClass().getSimpleName(), e.getLocalizedMessage());
						} future.complete(this);
					}, 0L);
					return future;
				} setSkullProfile((UUID) cache[0], (String) cache[1], snapshot ? (String) cache[2] : null);
			} this.skullOwner = skullOwner;
		} future.complete(this);
		return future;
	}
	
	/**
	 * Gets this skull's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isPlayerHead()}
	 * or if the texture's URL is <code>null</code>.</p>
	 * 
	 * @return Skull's texture's URL
	 */
	@Nullable(why = "Item may not be a skull or its texture URL may be null")
	public String getSkullTextureURL() {
		if (!isPlayerHead())
			return null;
		String value = null;
		
		if (Environment.isBukkit()) {
			ItemMeta meta = bukkitValue().getItemMeta();
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_2)) {
				Class<?> headMeta = meta.getClass();
				
				try {
					Field field = headMeta.getDeclaredField("profile");
					
					field.setAccessible(true);
					value = Iterables.getFirst(((GameProfile) field.get(meta)).getProperties().get("textures"), null).getValue();
				} catch (Exception e) {
					
				}
			} else {
				PlayerProfile profile = ((SkullMeta) meta).getOwnerProfile();
				
				if (profile != null) {
					URL skin = profile.getTextures().getSkin();
					
					if (skin != null)
						return skin.toExternalForm();
				} return null;
			}
		} else {
			Multimap<String, ProfileProperty> map = spongeValue().getOrCreate(RepresentedPlayerData.class).get().owner().get().getPropertyMap();
			
			if (map != null) {
				ProfileProperty property = Iterables.getFirst(map.get("textures"), null);
				
				if (property != null)
					value = property.getValue();
			}
		} if (value != null)
			try {
				value = new String(Base64.getDecoder().decode(value), StandardCharsets.ISO_8859_1);
				int index = value.indexOf("\"http") + 1;
				return value.substring(index, value.indexOf('"', index));
			} catch (IndexOutOfBoundsException ioobe) {
				ioobe.printStackTrace(); // should never happen
			}
		return null;
	}
	
	/**
	 * Sets this skull's texture found at given URL.
	 * 
	 * <p>Specify <code>null</code> to remove the skull's texture's URL. The URL must point to the Minecraft texture server; example:
	 * <code>https://textures.minecraft.net/texture/b3fbd454b599df593f57101bfca34e67d292a8861213d2202bb575da7fd091ac</code></p>
	 * 
	 * @param skullTextureURL Texture's URL
	 * @return This item stack
	 */
	public ItemStackAdapter setSkullTextureURL(@Nullable(why = "Texture's URL is removed when null") String skullTextureURL) {
		return isPlayerHead() ? setSkullProfile(UUID.randomUUID(), "chatplugin", skullTextureURL) : this;
	}
	
	private ItemStackAdapter setSkullProfile(UUID uuid, String name, String skullTextureURL) {
		if (Environment.isBukkit()) {
			ItemMeta meta = bukkitValue().getItemMeta();
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_2)) {
				GameProfile profile = uuid == null ? null : new GameProfile(uuid, name);
				
				if (skullTextureURL != null)
					profile.getProperties().put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
				try {
					Field field = meta.getClass().getDeclaredField("profile");
					
					field.setAccessible(true);
					field.set(meta, profile);
					bukkitValue().setItemMeta(meta);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					LogManager.log("{0} occurred while setting a skull's profile for an item stack: {1}", 2, e.getClass().getSimpleName(), e.getLocalizedMessage());
				}
			} else {
				PlayerProfile profile = uuid == null ? null : Bukkit.createPlayerProfile(uuid, name);
				
				try {
					if (skullTextureURL != null) {
						PlayerTextures textures = profile.getTextures();
						
						textures.setSkin(new URI(skullTextureURL).toURL());
						profile.setTextures(textures);
					} ((SkullMeta) meta).setOwnerProfile(profile);
					bukkitValue().setItemMeta(meta);
				} catch (URISyntaxException | IllegalArgumentException | MalformedURLException e) {
					LogManager.log("{0} occurred while setting a skull's profile for an item stack: {1}", 2, e.getClass().getSimpleName(), e.getLocalizedMessage());
				}
			}
		} else if (uuid != null) {
			org.spongepowered.api.profile.GameProfile profile = Sponge.getServer().getGameProfileManager().createProfile(uuid, name);
			
			if (skullTextureURL != null)
				profile.addProperty("textures", ProfileProperty.of("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
			spongeValue().offer(Keys.REPRESENTED_PLAYER, profile);
		} else spongeValue().remove(Keys.REPRESENTED_PLAYER);
		return this;
	}
	
	/**
	 * Checks if this item is a dyeable leather armor.
	 * 
	 * @return Whether this item is a leather armor
	 */
	public boolean isLeatherArmor() {
		return type.getID().contains("LEATHER_");
	}
	
	/**
	 * Gets this leather armor's color.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isLeatherArmor()}.</p>
	 * 
	 * @return Leather armor's color
	 */
	@Nullable(why = "Item may not be a leather armor")
	public Color getLeatherArmorColor() {
		return isLeatherArmor() ? Environment.isBukkit() ? new Color(((LeatherArmorMeta) bukkitValue().getItemMeta()).getColor().asRGB()) : spongeValue().getOrCreate(DyeableData.class).get().type().get().getColor().asJavaColor() : null;
	}
	
	/**
	 * Sets this leather armor's color.
	 * 
	 * <p>Will do nothing if <code>!</code>{@link #isLeatherArmor()}.</p>
	 * 
	 * <p>If you need to reset the color to the default value, specify
	 * <code>null</code> and the following color will be applied: #A06540.</p>
	 * 
	 * @param leatherArmorColor Leather armor's color
	 * @return This item stack
	 */
	public ItemStackAdapter setLeatherArmorColor(@Nullable(why = "Color is set to #A06540 when null") Color leatherArmorColor) {
		if (isLeatherArmor()) {
			leatherArmorColor = leatherArmorColor == null ? Color.decode("#A06540") : leatherArmorColor;
			
			if (Environment.isBukkit()) {
				ItemMeta meta = bukkitValue().getItemMeta();
				
				((LeatherArmorMeta) meta).setColor(org.bukkit.Color.fromRGB(leatherArmorColor.getRed(), leatherArmorColor.getGreen(), leatherArmorColor.getBlue()));
				bukkitValue().setItemMeta(meta);
			} else SpongeItemStack.setLeatherArmorColor(spongeValue(), leatherArmorColor);
		} return this;
	}
	
	/**
	 * Imports all supported data from another item stack.
	 * 
	 * @param other Other item stack
	 * @return This item stack
	 */
	public ItemStackAdapter importData(ItemStackAdapter other) {
		setAmount(other.getAmount());
		setDamage(other.getDamage());
		disenchant();
		enchant(other.getEnchantments());
		setDisplayName(other.getDisplayName());
		setLore(other.getLore());
		removeItemFlags(ItemFlagAdapter.values());
		addItemFlags(other.getItemFlags().toArray(new ItemFlagAdapter[0]));
		
		if (other.getSkullOwner() != null) {
			if (!getSkullOwner().equals(other.getSkullOwner()))
				setSkullOwner(other.getSkullOwner(), other.getSkullTextureURL() != null);
		} else if (other.getSkullTextureURL() != null) {
			if (!getSkullTextureURL().equals(other.getSkullTextureURL()))
				setSkullTextureURL(other.getSkullTextureURL());
		} setLeatherArmorColor(other.getLeatherArmorColor());
		return this;
	}
	
	private static class SpongeItemStack {
		
		public static short getDamage(ItemStack itemStack) {
			return ((Integer) itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).get()).shortValue();
		}
		
		public static ItemStack setDamage(ItemStack itemStack, short damage) {
			return ItemStack.builder().fromContainer(itemStack.toContainer().set(DataQuery.of("UnsafeDamage"), Integer.valueOf(damage))).build();
		}
		
		public static void enchant(ItemStack item, EnchantmentType enchantment, int level) {
			EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
			
			data.addElement(Enchantment.of(enchantment, level));
			item.offer(data);
		}
		
		public static void disenchant(ItemStack item, EnchantmentType enchantment) {
			EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
			List<Enchantment> enchantments = data.enchantments().get();
			
			for (int i = 0; i < enchantments.size(); i++)
				if (enchantments.get(i).getType().equals(enchantment))
					enchantments.remove(i);
			item.offer(data);
		}
		
		public static void setLeatherArmorColor(ItemStack item, Color color) {
			DyeableData data = item.getOrCreate(DyeableData.class).get();
			String hex = "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
			
			data.type().set(new DyeColor() {
				
				@Override
				public Translation getTranslation() {
					return new FixedTranslation(hex);
				}
				
				@Override
				public String getName() {
					return hex;
				}
				
				@Override
				public String getId() {
					return hex;
				}
				
				@Override
				public org.spongepowered.api.util.Color getColor() {
					return org.spongepowered.api.util.Color.of(color);
				}
				
			});
			item.offer(data);
		}
		
		public static List<Text> toTextList(List<String> list) {
			return list.stream().map(string -> Utils.serializeSpongeText(string.replace("\n", " ").replace("\r\n", " ").replace("\r", " "), false)).collect(Collectors.toList());
		}
		
		public static List<String> toStringList(List<Text> list) {
			return list.stream().map(Utils::deserializeSpongeText).collect(Collectors.toList());
		}
		
	}
	
}
