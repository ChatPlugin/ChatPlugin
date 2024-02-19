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

package me.remigio07.chatplugin.api.server.util.adapter.inventory.item;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
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
	private List<ItemFlagAdapter> itemFlags = new ArrayList<>();
	private Map<EnchantmentAdapter, Integer> enchantments = new HashMap<>();
	private String skullOwner;
	private boolean itemMeta = true;
	private static Map<String, Object[]> skullsCache = new HashMap<>();
	private static List<String> nonPremiumUsernames = new ArrayList<>();
	
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
	 * Constructs an item stack with the given
	 * {@link org.bukkit.inventory.ItemStack}.
	 * 
	 * @param itemStack Bukkit's item stack
	 */
	@SuppressWarnings("deprecation")
	public ItemStackAdapter(org.bukkit.inventory.ItemStack itemStack) {
		this.itemStack = itemStack;
		type = new MaterialAdapter(itemStack.getType().name());
		
		if (itemStack.hasItemMeta()) {
			itemStack.getItemMeta().getItemFlags().forEach(itemFlag -> itemFlags.add(ItemFlagAdapter.valueOf(itemFlag.name())));
			itemStack.getItemMeta().getEnchants().forEach((enchantment, level) -> enchantments.put(EnchantmentAdapter.valueOf(enchantment.getName()), level));
		} else itemMeta = false;
	}
	
	/**
	 * Constructs an item stack with the given
	 * {@link org.spongepowered.api.item.inventory.ItemStack}.
	 * 
	 * @param itemStack Sponge's item stack
	 */
	public ItemStackAdapter(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		this.itemStack = itemStack;
		type = new MaterialAdapter(itemStack.getType().getId());
		
		for (ItemFlagAdapter itemFlag : ItemFlagAdapter.values())
			if (itemStack.getOrElse(itemFlag.spongeValue(), false))
				itemFlags.add(itemFlag);
		if (!itemStack.getOrCreate(EnchantmentData.class).get().enchantments().isEmpty())
			itemStack.get(EnchantmentData.class).get().enchantments().get().forEach(enchantment -> enchantments.put(EnchantmentAdapter.valueOf(enchantment.getType().getId()), enchantment.getLevel()));
	}
	
	@Override
	public ItemStackAdapter clone() {
		ItemStackAdapter itemStack = new ItemStackAdapter(type.clone(), getAmount(), getDamage());
		
		if (hasDisplayName())
			itemStack.setDisplayName(getDisplayName());
		if (hasLore())
			itemStack.setLore(new ArrayList<>(getLore()));
		if (isEnchanted())
			enchantments.forEach((enchantment, level) -> itemStack.enchant(enchantment, level));
		if (isPlayerHead()) {
			if (getSkullOwner() != null)
				itemStack.setSkullOwner(getSkullOwner());
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
		else throw new UnsupportedOperationException("Unable to adapt item stack to a Bukkit's ItemStack on a " + Environment.getCurrent().getName() + " environment");
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
		else throw new UnsupportedOperationException("Unable to adapt item stack to a Sponge's ItemStack on a " + Environment.getCurrent().getName() + " environment");
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
	 * Sets this item stack's type.
	 * 
	 * @param type Item stack's type
	 * @return This item stack
	 */
	public ItemStackAdapter setType(MaterialAdapter type) {
		if (!type.equals(this.type)) {
			this.type = type;
			
			if (Environment.isBukkit())
				bukkitValue().setType(type.bukkitValue());
			else itemStack = clone();
		} return this;
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
	 * Sets this item stack's current durability
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
	 * Adds an enchantment to this item.
	 * 
	 * @param enchantment Enchantment to add
	 * @param level Enchantment's level
	 * @return This item stack
	 */
	public ItemStackAdapter enchant(EnchantmentAdapter enchantment, int level) {
		if (Environment.isBukkit())
			bukkitValue().addUnsafeEnchantment(enchantment.bukkitValue(), level);
		else SpongeItemStack.enchant(spongeValue(), enchantment.spongeValue(), level);
		return this;
	}
	
	/**
	 * Removes an enchantment from this item.
	 * 
	 * @param enchantment Enchantment to remove
	 * @return This item stack
	 */
	public ItemStackAdapter disenchant(EnchantmentAdapter enchantment) {
		if (Environment.isBukkit())
			bukkitValue().removeEnchantment(enchantment.bukkitValue());
		else SpongeItemStack.disenchant(spongeValue(), enchantment.spongeValue());
		return this;
	}
	
	/**
	 * Checks if this item has any enchantments.
	 * 
	 * @return Whether this item is enchanted
	 */
	public boolean isEnchanted() {
		return !enchantments.isEmpty();
	}
	
	/**
	 * Applies the glowing effect to this item (when {@link #isEnchanted()} and
	 * {@link #getItemFlags()} contains {@link ItemFlagAdapter#HIDE_ENCHANTMENTS}).
	 * 
	 * @return This item stack
	 */
	public ItemStackAdapter setGlowing() {
		enchant(EnchantmentAdapter.UNBREAKING, 1);
		addItemFlags(ItemFlagAdapter.HIDE_ENCHANTMENTS);
		return this;
	}
	
	/**
	 * Checks if this item has the glowing effect (when {@link #isEnchanted()} and
	 * {@link #getItemFlags()} contains {@link ItemFlagAdapter#HIDE_ENCHANTMENTS}).
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
	 * <p>Will do nothing if the specified player name does not belong to a premium
	 * player. You can specify <code>null</code> to remove the skull's owner.</p>
	 * 
	 * @param skullOwner Skull's owner
	 * @return This item stack
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	@SuppressWarnings("deprecation")
	public CompletableFuture<ItemStackAdapter> setSkullOwner(@Nullable(why = "Skull's owner is removed when null") String skullOwner) {
		CompletableFuture<ItemStackAdapter> future = new CompletableFuture<>();
		
		if (isPlayerHead()) {
			if (skullOwner == null) {
				if (Environment.isBukkit()) {
					ItemMeta meta = bukkitValue().getItemMeta();
					
					((SkullMeta) meta).setOwner(null);
					bukkitValue().setItemMeta(meta);
				} else spongeValue().remove(Keys.REPRESENTED_PLAYER);
			} else if (!Utils.isValidUsername(skullOwner))
				throw new IllegalArgumentException("Username \"" + skullOwner + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
			else if (!skullOwner.equals(this.skullOwner) && !nonPremiumUsernames.contains(skullOwner)) {
				Object[] cache = skullsCache.get(skullOwner);
				
				if (cache == null) {
					TaskManager.runAsync(() -> {
						try {
							UUID uuid = UUIDFetcher.getInstance().getOnlineUUID(skullOwner);
							
							if (uuid == null) {
								nonPremiumUsernames.add(skullOwner);
								future.complete(this);
								return;
							} skullsCache.put(skullOwner, new Object[] { UUIDFetcher.getInstance().getSkinTextureURL(uuid), uuid });
							setSkullTextureURL((String) skullsCache.get(skullOwner)[0], uuid);
						} catch (IOException e) {
							e.printStackTrace();
							LogManager.log("IOException occurred while setting a skull's owner for an item stack: {0}", 2, e.getMessage());
						} future.complete(this);
					}, 0L);
					return future;
				} setSkullTextureURL((String) cache[0], (UUID) cache[1]);
			}
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
			} catch (IndexOutOfBoundsException e) {
				
			}
		return null;
	}
	
	/**
	 * Sets this skull's texture found at given URL.
	 * 
	 * <p>You can specify <code>null</code> to remove the skull's texture's URL.</p>
	 * 
	 * @param skullTextureURL Texture's URL
	 * @return This item stack
	 */
	public ItemStackAdapter setSkullTextureURL(@Nullable(why = "Texture's URL is removed when null") String skullTextureURL) {
		return setSkullTextureURL(skullTextureURL, UUID.randomUUID());
	}
	
	private ItemStackAdapter setSkullTextureURL(String skullTextureURL, UUID uuid) {
		if (!isPlayerHead())
			return this;
		if (Environment.isBukkit()) {
			ItemMeta meta = bukkitValue().getItemMeta();
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_2)) {
				GameProfile profile = new GameProfile(uuid, null);
				Class<?> headMeta = meta.getClass();
				
				if (skullTextureURL != null) {
					profile.getProperties().put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
					
					try {
						Field field = headMeta.getDeclaredField("profile");
						
						field.setAccessible(true);
						field.set(meta, profile);
						bukkitValue().setItemMeta(meta);
					} catch (IllegalAccessException | NoSuchFieldException e) {
						
					}
				} else profile.getProperties().removeAll("textures");
			} else {
				if (skullTextureURL != null) {
					PlayerProfile profile = Bukkit.createPlayerProfile(uuid, "chatplugin");
					PlayerTextures textures = profile.getTextures();
					
					try {
						textures.setSkin(new URL(skullTextureURL));
						profile.setTextures(textures);
						((SkullMeta) meta).setOwnerProfile(profile);
						bukkitValue().setItemMeta(meta);
					} catch (Exception e) {
						LogManager.log("{0} occurred while setting a skull's texture's URL for an item stack: {1}", 2, e.getClass().getSimpleName(), e.getMessage());
					}
				} else {
					((SkullMeta) meta).setOwnerProfile(null);
					bukkitValue().setItemMeta(meta);
				}
			}
		} else {
			if (skullTextureURL == null)
				spongeValue().get(Keys.REPRESENTED_PLAYER).ifPresent(profile -> profile.getPropertyMap().removeAll("textures"));
			else spongeValue().offer(Keys.REPRESENTED_PLAYER, Sponge.getServer().getGameProfileManager().createProfile(uuid, null).addProperty("textures", ProfileProperty.of("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1))));
		} return this;
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
			return list.stream().map(text -> Utils.deserializeSpongeText(text)).collect(Collectors.toList());
		}
		
	}
	
}
