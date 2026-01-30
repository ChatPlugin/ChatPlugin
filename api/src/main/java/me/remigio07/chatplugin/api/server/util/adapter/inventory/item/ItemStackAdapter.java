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

package me.remigio07.chatplugin.api.server.util.adapter.inventory.item;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

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
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) item stack adapter.
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
		
		if (Environment.isBukkit()) {
			itemStack = new org.bukkit.inventory.ItemStack(type.bukkitValue(), amount, damage);
			
			if (!Bukkit.getItemFactory().isApplicable(bukkitValue().getItemMeta(), (org.bukkit.inventory.ItemStack) itemStack))
				itemMeta = false;
			return;
		} itemStack = Environment.isSponge()
				? org.spongepowered.api.item.inventory.ItemStack.builder().itemType(type.spongeValue()).quantity(amount).build()
				: FabricItemStack.newInstance(type, amount);
		
		setDamage(damage);
	}
	
	/**
	 * Constructs an item stack adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.inventory.ItemStack} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.item.inventory.ItemStack} for Sponge environments</li>
	 * 		<li>{@link ItemStack} for Fabric environments</li>
	 * 	</ul>
	 * 
	 * @param itemStack Item stack object
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public ItemStackAdapter(Object itemStack) {
		this.itemStack = itemStack;
		
		if (Environment.isBukkit()) {
			type = new MaterialAdapter(((org.bukkit.inventory.ItemStack) itemStack).getType().name());
			
			if (((org.bukkit.inventory.ItemStack) itemStack).hasItemMeta()) {
				ItemMeta meta = ((org.bukkit.inventory.ItemStack) itemStack).getItemMeta();
				
				meta.getEnchants().forEach((enchantment, level) -> enchantments.put(EnchantmentAdapter.value(enchantment.getName()), level));
				meta.getItemFlags().stream().map(itemFlag -> ItemFlagAdapter.value(itemFlag.name())).filter(Objects::nonNull).distinct().forEach(itemFlags::add);
				
				if (isPlayerHead())
					skullOwner = ((SkullMeta) meta).getOwner();
			} else itemMeta = false;
		} else if (Environment.isSponge()) {
			type = new MaterialAdapter(((org.spongepowered.api.item.inventory.ItemStack) itemStack).getType().getId().substring(10));
			
			for (ItemFlagAdapter itemFlag : ItemFlagAdapter.values())
				if (((org.spongepowered.api.item.inventory.ItemStack) itemStack).getOrElse(itemFlag.spongeValue(), false))
					itemFlags.add(itemFlag);
			if (!((org.spongepowered.api.item.inventory.ItemStack) itemStack).getOrCreate(EnchantmentData.class).get().enchantments().isEmpty())
				((org.spongepowered.api.item.inventory.ItemStack) itemStack).get(EnchantmentData.class).get().enchantments().get().forEach(enchantment -> enchantments.put(EnchantmentAdapter.value(enchantment.getType().getId().substring(10)), enchantment.getLevel()));
			if (isPlayerHead())
				spongeValue().get(RepresentedPlayerData.class).ifPresent(player -> player.owner().get().getName().ifPresent(name -> skullOwner = name));
		} else try {
			ItemStack fabricValue = (ItemStack) itemStack;
			type = new MaterialAdapter(VersionUtils.getVersion().isOlderThan(Version.V1_19_3)
					? Registry.class.getMethod("method_10221", Object.class).invoke(Registry.class.getField("field_11142").get(null), fabricValue.getItem()).toString() // Registry.ITEM.getId(fabricValue.getItem()).toString()
					: Registries.ITEM.getId(fabricValue.getItem()).toString());
			
			if (fabricValue.hasEnchantments()) {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_20_5)) {
					ItemEnchantmentsComponent enchantments = fabricValue.getEnchantments();
					
					enchantments.getEnchantments().forEach(enchantment -> this.enchantments.put(EnchantmentAdapter.value(enchantment.getIdAsString()), enchantments.getLevel(enchantment)));
				} else if (VersionUtils.getVersion().isOlderThan(Version.V1_19_3)) {
					Method getId = Registry.class.getMethod("method_10221", Object.class);
					Object ENCHANTMENT = Registry.class.getField("field_11160").get(null);
					
					for (Entry<Enchantment, Integer> enchantment : ((Map<Enchantment, Integer>) EnchantmentHelper.class.getMethod("method_8222", ItemStack.class).invoke(null, fabricValue)).entrySet()) // EnchantmentHelper.get(fabricValue).entrySet()
						enchantments.put(EnchantmentAdapter.value(getId.invoke(ENCHANTMENT, enchantment.getKey()).toString()), enchantment.getValue());
				} else {
					Registry<Enchantment> ENCHANTMENT = (Registry<Enchantment>) Registries.class.getField("field_41176").get(null);
					
					((Map<Enchantment, Integer>) EnchantmentHelper.class.getMethod("method_8222", ItemStack.class).invoke(null, fabricValue)).forEach((enchantment, level) -> enchantments.put(EnchantmentAdapter.value(ENCHANTMENT.getId(enchantment).toString()), level));
				}
			} if (VersionUtils.getVersion().isOlderThan(Version.V1_21_5)) {
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
					NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7969").invoke(fabricValue); // fabricValue().getTag()
					
					if (nbt != null) {
						if ((byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(nbt, "HideFlags") == 99) { // nbt.getType("HideFlags") == NbtElement.NUMBER_TYPE
							int HideFlags = (int) NbtCompound.class.getMethod("method_10550", String.class).invoke(nbt, "HideFlags"); // nbt.getInt("HideFlags)
							
							for (ItemFlagAdapter itemFlag : ItemFlagAdapter.values())
								if ((HideFlags & itemFlag.bitModifierValue()) != 0)
									itemFlags.add(itemFlag);
						}
					}
				} else {
					if (!FabricItemStack.New.showInTooltip(fabricValue.getEnchantments(), "field_49390"))
						itemFlags.add(ItemFlagAdapter.HIDE_ENCHANTMENTS);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.ATTRIBUTE_MODIFIERS), "comp_2394"))
						itemFlags.add(ItemFlagAdapter.HIDE_ATTRIBUTES);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.UNBREAKABLE), "comp_2417"))
						itemFlags.add(ItemFlagAdapter.HIDE_UNBREAKABLE);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.CAN_BREAK), "field_49253"))
						itemFlags.add(ItemFlagAdapter.HIDE_CAN_DESTROY);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.CAN_PLACE_ON), "field_49253"))
						itemFlags.add(ItemFlagAdapter.HIDE_CAN_BE_PLACED_ON);
					if (FabricItemStack.New.get(fabricValue, ItemFlagAdapter.HIDE_MISCELLANEOUS.fabricValue()) == Unit.INSTANCE || (VersionUtils.getVersion().isAtLeast(Version.V1_21) && !FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.JUKEBOX_PLAYABLE), "comp_2834")))
						itemFlags.add(ItemFlagAdapter.HIDE_MISCELLANEOUS);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.DYED_COLOR), "comp_2385"))
						itemFlags.add(ItemFlagAdapter.HIDE_DYE);
					if (!FabricItemStack.New.showInTooltip(FabricItemStack.New.get(fabricValue, DataComponentTypes.TRIM), "field_49279"))
						itemFlags.add(ItemFlagAdapter.HIDE_ARMOR_TRIM);
				}
			} else {
				TooltipDisplayComponent tooltipDisplay = fabricValue.get(DataComponentTypes.TOOLTIP_DISPLAY);
				
				if (tooltipDisplay != null)
					for (ItemFlagAdapter itemFlag : ItemFlagAdapter.values())
						if (itemFlag == ItemFlagAdapter.HIDE_MISCELLANEOUS
								? FabricItemStack.New.MISCELLANEOUS_COMPONENTS.stream().anyMatch(tooltipDisplay::shouldDisplay)
								: tooltipDisplay.shouldDisplay(itemFlag.fabricValue()))
							itemFlags.add(itemFlag);
			} if (isPlayerHead()) {
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
					NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7969").invoke(fabricValue); // fabricValue().getTag()
					
					if (nbt != null) {
						byte type = (byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(nbt, "SkullOwner"); // nbt.getType("SkullOwner")
						
						if (type == 10) { // COMPOUND_TYPE
							NbtCompound SkullOwner = (NbtCompound) NbtCompound.class.getMethod("method_10562", String.class).invoke(nbt, "SkullOwner"); // nbt.getCompound("SkullOwner")
							
							if ((byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(SkullOwner, "Name") == 8) // SkullOwner.getType("Name") == NbtElement.STRING_TYPE
								skullOwner = (String) NbtCompound.class.getMethod("method_10558", String.class).invoke(SkullOwner, "Name"); // SkullOwner.getString("Name")
						} else if (type == 8) // STRING_TYPE
							skullOwner = (String) NbtCompound.class.getMethod("method_10558", String.class).invoke(nbt, "SkullOwner"); // nbt.getString("SkullOwner")
					}
				} else FabricItemStack.New.get(fabricValue, DataComponentTypes.PROFILE).getName().ifPresent(owner -> skullOwner = owner);
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
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
		itemStack.addItemFlags(itemFlags.toArray(new ItemFlagAdapter[itemFlags.size()]));
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
			return (org.spongepowered.api.item.inventory.ItemStack) itemStack;
		throw new UnsupportedOperationException("Unable to adapt item stack to a Sponge's ItemStack on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item stack adapted for Fabric environments.
	 * 
	 * <p><strong>Note:</strong> do not modify the returned item stack's
	 * (meta)data manually. Only use the provided wrapper methods.</p>
	 * 
	 * @return Fabric-adapted item stack
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public ItemStack fabricValue() {
		if (Environment.isFabric())
			return (ItemStack) itemStack;
		throw new UnsupportedOperationException("Unable to adapt item stack to a Fabric's ItemStack on a " + Environment.getCurrent().getName() + " environment");
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
		return (short) (Environment.isBukkit() ? bukkitValue().getAmount() : Environment.isSponge() ? spongeValue().getQuantity() : fabricValue().getCount());
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
		else if (Environment.isSponge())
			spongeValue().setQuantity(amount);
		else fabricValue().setCount(amount);
		return this;
	}
	
	/**
	 * Gets this item stack's max durability.
	 * 
	 * @return Item's max durability
	 */
	public short getMaxDurability() {
		return Environment.isBukkit() ? type.bukkitValue().getMaxDurability() : Environment.isSponge() ? spongeValue().getProperty(UseLimitProperty.class).isPresent() ? spongeValue().getProperty(UseLimitProperty.class).get().getValue().shortValue() : 0 : (short) fabricValue().getMaxDamage();
	}
	
	/**
	 * Gets this item stack's current durability.
	 * 
	 * @return Item's durability
	 */
	public short getDurability() {
		return Environment.isSponge() ? spongeValue().get(Keys.ITEM_DURABILITY).orElse(0).shortValue() : (short) (getMaxDurability() - getDurability());
	}
	
	/**
	 * Sets this item stack's current durability.
	 * 
	 * @param durability Item's durability
	 * @return This item stack
	 */
	public ItemStackAdapter setDurability(short durability) {
		if (!Environment.isSponge())
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
		return (short) (Environment.isBukkit() ? bukkitValue().getDurability() : Environment.isSponge() ? spongeValue().supports(DurabilityData.class) ? getMaxDurability() - getDurability() : SpongeItemStack.getDamage(spongeValue()) : fabricValue().getDamage());
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
		else if (Environment.isSponge())
			if (spongeValue().supports(DurabilityData.class))
				setDurability((short) (getMaxDurability() - damage));
			else itemStack = SpongeItemStack.setDamage(spongeValue(), damage);
		else fabricValue().setDamage(damage);
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
		if (Environment.isBukkit()) {
			if (hasItemMeta())
				bukkitValue().addUnsafeEnchantment(enchantment.bukkitValue(), level);
		} else if (Environment.isSponge())
			SpongeItemStack.enchant(spongeValue(), enchantment.spongeValue(), level);
		else if (VersionUtils.getVersion().isAtLeast(Version.V1_21))
			fabricValue().addEnchantment(FabricBootstrapper.getInstance().getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(enchantment.fabricValue()), level);
		else try {
			ItemStack.class.getMethod("method_7978", Enchantment.class, int.class).invoke(fabricValue(), enchantment.fabricValue(), level);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} enchantments.put(enchantment, level);
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
		else if (Environment.isSponge())
			SpongeItemStack.disenchant(spongeValue(), enchantment.spongeValue());
		else if (VersionUtils.getVersion().isAtLeast(Version.V1_20_5))
			fabricValue().getEnchantments().getEnchantments().removeIf(entry -> entry.value().equals(enchantment.fabricValue())); // TODO does this actually remove the enchantment/update the item? also check EnchantmentHelper
		else try {
			@SuppressWarnings("unchecked")
			Map<Enchantment, Integer> enchantments = (Map<Enchantment, Integer>) EnchantmentHelper.class.getMethod("method_8222", ItemStack.class).invoke(null, fabricValue());
			
			if (enchantments.remove(enchantment.fabricValue()) != null)
				EnchantmentHelper.class.getMethod("method_8214", Map.class, ItemStack.class).invoke(null, enchantments, fabricValue());
			else return this; // skip next remove
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} enchantments.remove(enchantment);
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
	@SuppressWarnings("deprecation")
	@Nullable(why = "Null when item does not have a display name")
	public String getDisplayName() {
		if (hasDisplayName()) {
			if (Environment.isBukkit())
				return bukkitValue().getItemMeta().getDisplayName();
			if (Environment.isSponge())
				return Utils.toLegacyText(spongeValue().get(DisplayNameData.class).get().displayName().get());
			if (Environment.isFabric()) {
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5))
					try {
						// Utils.toLegacyText(new JsonParser().parse(fabricValue().getOrCreateSubTag("display").getString("Name")
						return (String) Class.forName("me.remigio07.chatplugin.common.util.Utils", false, JARLibraryLoader.getInstance()).getMethod("toLegacyText", String.class).invoke(null, new JsonParser().parse((String) NbtCompound.class.getMethod("method_10558", String.class).invoke(ItemStack.class.getMethod("method_7911", String.class).invoke(fabricValue(), "display"), "Name")));
					} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | JsonSyntaxException e) {
						e.printStackTrace();
					}
				else return Utils.toLegacyText(fabricValue().getCustomName());
			}
		} return null;
	}
	
	/**
	 * Checks if this item has a display name.
	 * 
	 * @return Whether this item has a display name
	 */
	public boolean hasDisplayName() {
		try {
			return Environment.isBukkit()
					? itemMeta
					? bukkitValue().getItemMeta().hasDisplayName()
					: false
					: Environment.isSponge()
					? spongeValue().getValue(Keys.DISPLAY_NAME).isPresent()
					: VersionUtils.getVersion().isOlderThan(Version.V1_20_5)
					? (boolean) ItemStack.class.getMethod("method_7938").invoke(fabricValue())
					: fabricValue().getCustomName() != null;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
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
			else spongeValue().offer(Keys.DISPLAY_NAME, Utils.toSpongeComponent(displayName));
		} else if (Environment.isFabric())
			FabricItemStack.setDisplayName(fabricValue(), displayName);
		else if (itemMeta) {
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
		if (hasLore()) {
			if (Environment.isBukkit())
				return itemMeta ? bukkitValue().getItemMeta().getLore() : null;
			if (Environment.isSponge())
				return SpongeItemStack.toStringList(spongeValue().getOrCreate(LoreData.class).get().lore().get());
			if (Environment.isFabric())
				return FabricItemStack.getLore(fabricValue());
		} return null;
	}
	
	/**
	 * Checks if this item has a lore.
	 * 
	 * @return Whether this item has a lore
	 */
	public boolean hasLore() {
		if (Environment.isBukkit())
			return itemMeta && bukkitValue().getItemMeta().hasLore();
		if (Environment.isSponge())
			return spongeValue().getValue(Keys.ITEM_LORE).isPresent();
		try {
			return VersionUtils.getVersion().isOlderThan(Version.V1_20_5) // fabricValue().method_7911("display").method_10540("Lore") not sure about this! - fabricValue().getOrCreateSubTag("display").getType("Lore")
					? (byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(ItemStack.class.getMethod("method_7911", String.class).invoke(fabricValue(), "display"), "Lore") == 9
					: FabricItemStack.New.get(fabricValue(), DataComponentTypes.LORE) != null;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
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
		} else if (Environment.isFabric())
			FabricItemStack.setLore(fabricValue(), lore);
		else if (itemMeta) {
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
			if (!itemFlag.isSupported())
				itemFlag = ItemFlagAdapter.HIDE_ATTRIBUTES;
			if (!this.itemFlags.contains(itemFlag)) {
				if (Environment.isSponge()) {
					spongeValue().offer(itemFlag.spongeValue(), true);
				} else if (Environment.isFabric()) {
					if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
						try {
							NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7948").invoke(fabricValue()); // fabricValue().getOrCreateNbt()
							
							nbt.putInt("HideFlags", (int) NbtCompound.class.getMethod("method_10550", String.class).invoke(nbt, "HideFlags") | itemFlag.bitModifierValue()); // nbt.getInt("HideFlags")
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					} else FabricItemStack.New.addItemFlag(fabricValue(), itemFlag);
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
				} else if (Environment.isFabric()) {
					if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
						try {
							NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7969").invoke(fabricValue()); // fabricValue().getNbt()
							
							if (nbt != null && (byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(nbt, "HideFlags") == 99) // nbt.getType("HideFlags") == NbtElement.NUMBER_TYPE
								nbt.putInt("HideFlags", (int) NbtCompound.class.getMethod("method_10550", String.class).invoke(nbt, "HideFlags") & ~itemFlag.bitModifierValue()); // nbt.getInt("HideFlags")
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					} else FabricItemStack.New.removeItemFlag(fabricValue(), itemFlag);
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
		switch (type.getID()) {
		case "minecraft:player_head":
		case "player_head":
		case "PLAYER_HEAD":
			return true;
		case "minecraft:skull":
		case "skull":
		case "SKULL_ITEM":
		case "SKULL":
			return getDamage() == 3;
		default:
			return false;
		}
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
				try {
					Field field = meta.getClass().getDeclaredField("profile");
					
					field.setAccessible(true);
					
					GameProfile profile = (GameProfile) field.get(meta);
					
					if (profile != null) {
						Property property = Iterables.getFirst(((PropertyMap) GameProfile.class.getMethod("getProperties").invoke(profile)).get("textures"), null);
						
						if (property != null) // should never be null
							value = (String) Property.class.getMethod("getValue").invoke(property);
					}
				} catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				PlayerProfile profile = ((SkullMeta) meta).getOwnerProfile();
				
				if (profile != null) {
					URL skin = profile.getTextures().getSkin();
					
					if (skin != null)
						return skin.toExternalForm();
				} return null;
			}
		} else if (Environment.isSponge()) {
			Multimap<String, ProfileProperty> map = spongeValue().getOrCreate(RepresentedPlayerData.class).get().owner().get().getPropertyMap();
			
			if (map != null) {
				ProfileProperty property = Iterables.getFirst(map.get("textures"), null);
				
				if (property != null) // should never be null
					value = property.getValue();
			}
		} else try {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
				NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7969").invoke(fabricValue()); // fabricValue().getNbt()
				
				if (nbt != null && (byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(nbt, "SkullOwner") == 10) { // nbt.getType("SkullOwner") == NbtElement.COMPOUND_TYPE
					GameProfile profile = (GameProfile) NbtHelper.class.getMethod("method_10683", NbtCompound.class).invoke(null, NbtCompound.class.getMethod("method_10580", String.class).invoke(nbt, "SkullOwner")); // NbtHelper.toGameProfile(nbt)
					
					if (profile != null) {
						if (VersionUtils.getVersion().isAtLeast(Version.V1_20_2)) {
							Property property = Iterables.getFirst(((PropertyMap) (VersionUtils.getVersion().isAtLeast(Version.V1_21_9)
									? profile.properties()
									: GameProfile.class.getMethod("getProperties").invoke(profile)
									)).get("textures"), null);
							
							if (property != null)
								value = property.value();
						} else {
							Property property = Iterables.getFirst(((PropertyMap) GameProfile.class.getMethod("getProperties").invoke(profile)).get("textures"), null);
							
							if (property != null)
								value = (String) Property.class.getMethod("getValue").invoke(property);
						}
					}
				}
			} else {
				ProfileComponent profile = FabricItemStack.New.get(fabricValue(), DataComponentTypes.PROFILE);
				
				if (profile != null) { // GameProfile became a record in 1.21.9 (AuthLib v7)
					Property property = Iterables.getFirst((VersionUtils.getVersion().isOlderThan(Version.V1_21_9)
							? (PropertyMap) GameProfile.class.getMethod("getProperties").invoke(ProfileComponent.class.getMethod("comp_2413").invoke(profile))
							: profile.getGameProfile().properties()
							).get("textures"), null);
					
					if (property != null)
						value = property.value();
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
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
				try {
					GameProfile profile = uuid == null ? null : new GameProfile(uuid, name);
					
					if (skullTextureURL != null)
						((PropertyMap) GameProfile.class.getMethod("getProperties").invoke(profile)).put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
					Field field = meta.getClass().getDeclaredField("profile");
					
					field.setAccessible(true);
					field.set(meta, profile);
					bukkitValue().setItemMeta(meta);
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
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
		} else if (Environment.isSponge()) {
			if (uuid != null) {
				org.spongepowered.api.profile.GameProfile profile = Sponge.getServer().getGameProfileManager().createProfile(uuid, name);
				
				if (skullTextureURL != null)
					profile.addProperty("textures", ProfileProperty.of("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
				spongeValue().offer(Keys.REPRESENTED_PLAYER, profile);
			} else spongeValue().remove(Keys.REPRESENTED_PLAYER);
		} else FabricItemStack.setSkullProfile(fabricValue(), uuid, name, skullTextureURL);
		return this;
	}
	
	/**
	 * Checks if this item is a dyeable leather armor.
	 * 
	 * @return Whether this item is a leather armor
	 */
	public boolean isLeatherArmor() {
		return type.getID().toLowerCase().contains("leather_");
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
		if (isLeatherArmor()) {
			if (Environment.isBukkit())
				return new Color(((LeatherArmorMeta) bukkitValue().getItemMeta()).getColor().asRGB());
			if (Environment.isSponge())
				return spongeValue().getOrCreate(DyeableData.class).get().type().get().getColor().asJavaColor();
			if (Environment.isFabric())
				try {
					if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
						NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7941").invoke(fabricValue(), "display"); // fabricValue().getSubTag("display")
						return nbt == null || (byte) NbtCompound.class.getMethod("method_10540", String.class).invoke(nbt, "color") != 99 ? Color.decode("#A06540") : new Color((int) NbtCompound.class.getMethod("method_10550", String.class).invoke(nbt, "color"));
					} return new Color(FabricItemStack.New.get(fabricValue(), DataComponentTypes.DYED_COLOR).rgb());
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | JsonSyntaxException e) {
					e.printStackTrace();
				}
		} return null;
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
			} else if (Environment.isSponge())
				SpongeItemStack.setLeatherArmorColor(spongeValue(), leatherArmorColor);
			else if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
				try {
					((NbtCompound) ItemStack.class.getMethod("method_7911", String.class).invoke(fabricValue(), "display")).putInt("color", leatherArmorColor.getRGB());
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else fabricValue().set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(leatherArmorColor.getRGB()));
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
		addItemFlags(other.getItemFlags().toArray(new ItemFlagAdapter[other.getItemFlags().size()]));
		
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
		
		public static short getDamage(org.spongepowered.api.item.inventory.ItemStack itemStack) {
			return ((Integer) itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).get()).shortValue();
		}
		
		public static org.spongepowered.api.item.inventory.ItemStack setDamage(org.spongepowered.api.item.inventory.ItemStack itemStack, short damage) {
			return org.spongepowered.api.item.inventory.ItemStack.builder().fromContainer(itemStack.toContainer().set(DataQuery.of("UnsafeDamage"), Integer.valueOf(damage))).build();
		}
		
		public static void enchant(org.spongepowered.api.item.inventory.ItemStack item, EnchantmentType enchantment, int level) {
			EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
			
			data.addElement(org.spongepowered.api.item.enchantment.Enchantment.of(enchantment, level));
			item.offer(data);
		}
		
		public static void disenchant(org.spongepowered.api.item.inventory.ItemStack item, EnchantmentType enchantment) {
			EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
			List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = data.enchantments().get();
			
			for (int i = 0; i < enchantments.size(); i++)
				if (enchantments.get(i).getType().equals(enchantment))
					enchantments.remove(i);
			item.offer(data);
		}
		
		public static void setLeatherArmorColor(org.spongepowered.api.item.inventory.ItemStack item, Color color) {
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
			return list.stream().map(string -> Utils.toSpongeComponent(string.replace("\n", " ").replace("\r\n", " ").replace("\r", " "))).collect(Collectors.toList());
		}
		
		public static List<String> toStringList(List<Text> list) {
			return list.stream().map(Utils::toLegacyText).collect(Collectors.toList());
		}
		
	}
	
	private static class FabricItemStack {
		
		public static ItemStack newInstance(MaterialAdapter type, short amount) {
			return new ItemStack(type.fabricValue(), amount);
		}
		
		public static void setDisplayName(ItemStack itemStack, String displayName) {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5))
				try {
					Object text = displayName == null ? null : Utils.toFabricComponent(displayName);
					
					if (text != null)
						text = VersionUtils.getVersion().isOlderThan(Version.V1_16)
								? net.minecraft.text.Text.class.getMethod("method_10862", Style.class).invoke(text, Style.class.getConstructor().newInstance().withItalic(false))
								: MutableText.class.getMethod("method_27696", Style.class).invoke(((net.minecraft.text.Text) text).copy(), Style.EMPTY.withItalic(false));
					ItemStack.class.getMethod("method_7977", net.minecraft.text.Text.class).invoke(itemStack, text);
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
					e.printStackTrace();
				}
			else New.setDisplayName(itemStack, displayName);
		}
		
		public static List<String> getLore(ItemStack itemStack) {
			try {
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
					// fabricValue().method_7911("display").method_10554("Lore", 8)
					// fabricValue().getOrCreateSubTag("display").getList("Lore", NbtElement.STRING_TYPE)
					NbtList lore = (NbtList) NbtCompound.class.getMethod("method_10554", String.class, int.class).invoke(ItemStack.class.getMethod("method_7911", String.class).invoke(itemStack, "display"), "Lore", 8);
					List<String> list = new ArrayList<>(lore.size());
					Method toLegacyText = Class.forName("me.remigio07.chatplugin.common.util.Utils", false, JARLibraryLoader.getInstance()).getMethod("toLegacyText", String.class);
					
					for (int i = 0; i < lore.size(); i++)
						list.add((String) toLegacyText.invoke(null, lore.getString(i)));
					return list;
				} return FabricItemStack.New.get(itemStack, DataComponentTypes.LORE).lines().stream().map(Utils::toLegacyText).collect(Collectors.toList());
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static void setLore(ItemStack itemStack, List<String> lore) {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5))
				try {
					NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7911", String.class).invoke(itemStack, "display");
					
					if (lore != null) {
						NbtList list = new NbtList();
						
						for (String line : lore) {
							Object text = Utils.toFabricComponent(line.replace("\n", " ").replace("\r\n", " ").replace("\r", " "));
							text = VersionUtils.getVersion().isOlderThan(Version.V1_16)
									? (net.minecraft.text.Text) net.minecraft.text.Text.class.getMethod("method_10862", Style.class).invoke(text, Style.class.getConstructor().newInstance().withItalic(false))
									: MutableText.class.getMethod("method_27696", Style.class).invoke(((net.minecraft.text.Text) text).copy(), Style.EMPTY.withItalic(false));
							Constructor<NbtString> constructor = NbtString.class.getDeclaredConstructor(String.class);
							
							constructor.setAccessible(true);
							list.add(constructor.newInstance(((JsonElement) Class.forName("me.remigio07.chatplugin.server.fabric.ChatPluginFabric", false, JARLibraryLoader.getInstance()).getMethod("toJSON", net.minecraft.text.Text.class).invoke(null, text)).toString()));
						} nbt.put("Lore", list);
					} else NbtCompound.class.getMethod("method_10551", String.class).invoke(nbt, "Lore"); // nbt.remove("Lore")
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
					e.printStackTrace();
				}
			else itemStack.set(DataComponentTypes.LORE, lore == null ? null : new LoreComponent(lore.stream().map(str -> Utils.toFabricComponent(str.replace("\n", " ").replace("\r\n", " ").replace("\r", " ")).copy().fillStyle(Style.EMPTY.withItalic(false))).collect(Collectors.toList())));
		}
		
		public static void setSkullProfile(ItemStack itemStack, UUID uuid, String name, String skullTextureURL) {
			try {
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5)) {
					NbtCompound nbt = (NbtCompound) ItemStack.class.getMethod("method_7948").invoke(itemStack); // itemStack.getOrCreateNbt()
					
					if (uuid != null) {
						GameProfile profile = new GameProfile(uuid, name);
						
						if (skullTextureURL != null)
							((PropertyMap) GameProfile.class.getMethod("getProperties").invoke(profile)).put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
						nbt.put("SkullOwner", (NbtCompound) NbtHelper.class.getMethod("method_10684", NbtCompound.class, GameProfile.class).invoke(null, new NbtCompound(), profile)); // NbtHelper.fromGameProfile(...)
					} else NbtCompound.class.getMethod("method_10551", String.class).invoke(nbt, "SkullOwner"); // nbt.remove("SkullOwner")
				} else if (uuid != null)
					if (VersionUtils.getVersion().isOlderThan(Version.V1_21_9)) {
						PropertyMap map = PropertyMap.class.getConstructor().newInstance();
						
						if (skullTextureURL != null)
							map.put("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)));
						itemStack.set(DataComponentTypes.PROFILE, ProfileComponent.class.getConstructor(Optional.class, Optional.class, PropertyMap.class).newInstance(Optional.of(name), Optional.of(uuid), map));
					} else itemStack.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(new GameProfile(uuid, name, skullTextureURL == null ? PropertyMap.EMPTY : new PropertyMap(ImmutableListMultimap.of("textures", new Property("textures", new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + skullTextureURL + "\"}}}").getBytes()), StandardCharsets.ISO_8859_1)))))));
				else itemStack.remove(DataComponentTypes.PROFILE);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static class New { // 1.20.5+
			
			public static final Set<ComponentType<?>> MISCELLANEOUS_COMPONENTS;
			
			static {
				MISCELLANEOUS_COMPONENTS = VersionUtils.getVersion().isAtLeast(Version.V1_21_5)
						? Stream.of(ItemFlagAdapter.HIDE_MISCELLANEOUS.getComponents()).map(component -> Registries.DATA_COMPONENT_TYPE.get(Identifier.ofVanilla(component))).filter(Objects::nonNull).collect(Collectors.toSet())
						: null;
			}
			
			@SuppressWarnings("unchecked")
			public static <T> T get(ItemStack itemStack, ComponentType<? extends T> component) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
				return VersionUtils.getVersion().isAtLeast(Version.V1_21_5)
						? itemStack.get(component)
						: (T) ItemStack.class.getMethod("method_57824", ComponentType.class).invoke(itemStack, component);
			}
			
			public static void setDisplayName(ItemStack itemStack, String displayName) {
				itemStack.set(DataComponentTypes.CUSTOM_NAME, displayName == null ? null : Utils.toFabricComponent(displayName).copy().fillStyle(Style.EMPTY.withItalic(false)));
			}
			
			public static void addItemFlag(ItemStack itemStack, ItemFlagAdapter itemFlag) {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_21_5)) {
					TooltipDisplayComponent tooltip = itemStack.get(DataComponentTypes.TOOLTIP_DISPLAY);
					SequencedSet<ComponentType<?>> hiddenComponents = new LinkedHashSet<>();
					
					if (tooltip != null)
						hiddenComponents.addAll(tooltip.hiddenComponents());
					if (itemFlag == ItemFlagAdapter.HIDE_MISCELLANEOUS)
						hiddenComponents.addAll(MISCELLANEOUS_COMPONENTS);
					else hiddenComponents.add(itemFlag.fabricValue());
					itemStack.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, hiddenComponents));
				} else withShowInTooltip(itemStack, itemFlag, false);
			}
			
			public static void removeItemFlag(ItemStack itemStack, ItemFlagAdapter itemFlag) {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_21_5)) {
					TooltipDisplayComponent tooltip = itemStack.get(DataComponentTypes.TOOLTIP_DISPLAY);
					if (tooltip != null) {
						SequencedSet<ComponentType<?>> hiddenComponents = new LinkedHashSet<>(tooltip.hiddenComponents());
						
						if (itemFlag == ItemFlagAdapter.HIDE_MISCELLANEOUS)
							hiddenComponents.removeAll(MISCELLANEOUS_COMPONENTS);
						else hiddenComponents.remove(itemFlag.fabricValue());
						itemStack.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(false, hiddenComponents));
					}
				} else withShowInTooltip(itemStack, itemFlag, true);
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private static void withShowInTooltip(ItemStack itemStack, ItemFlagAdapter itemFlag, boolean showInTooltip) {
				ComponentChanges.Builder builder = ComponentChanges.builder();
				
				try {
					if (itemFlag == ItemFlagAdapter.HIDE_ENCHANTMENTS) {
						if (itemStack.hasEnchantments())
							builder.add(DataComponentTypes.ENCHANTMENTS, withShowInTooltip(itemStack.getEnchantments(), "method_58449", showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_ATTRIBUTES) {
						if (itemStack.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS))
							builder.add(DataComponentTypes.ATTRIBUTE_MODIFIERS, withShowInTooltip(get(itemStack, DataComponentTypes.ATTRIBUTE_MODIFIERS), "method_58423", showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_UNBREAKABLE) {
						if (itemStack.contains(DataComponentTypes.UNBREAKABLE))
							builder.add((ComponentType) DataComponentTypes.class.getField("field_49630").get(null), Class.forName("net.minecraft.class_9300").getConstructor(boolean.class).newInstance(showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_CAN_DESTROY) {
						if (itemStack.contains(DataComponentTypes.CAN_BREAK))
							builder.add(DataComponentTypes.CAN_BREAK, withShowInTooltip(get(itemStack, DataComponentTypes.CAN_BREAK), "method_58402", showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_CAN_BE_PLACED_ON) {
						if (itemStack.contains(DataComponentTypes.CAN_PLACE_ON))
							builder.add(DataComponentTypes.CAN_PLACE_ON, withShowInTooltip(get(itemStack, DataComponentTypes.CAN_PLACE_ON), "method_58402", showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_MISCELLANEOUS) {
						if (VersionUtils.getVersion().isAtLeast(Version.V1_21))
							builder.add(DataComponentTypes.JUKEBOX_PLAYABLE, withShowInTooltip(get(itemStack, DataComponentTypes.JUKEBOX_PLAYABLE), "method_60749", showInTooltip));
						if (showInTooltip)
							itemStack.remove((ComponentType<Unit>) DataComponentTypes.class.getField("field_49638").get(null));
						else builder.add((ComponentType<Unit>) DataComponentTypes.class.getField("field_49638").get(null), Unit.INSTANCE);
					} else if (itemFlag == ItemFlagAdapter.HIDE_DYE) {
						if (itemStack.contains(DataComponentTypes.DYED_COLOR))
							builder.add(DataComponentTypes.DYED_COLOR, withShowInTooltip(get(itemStack, DataComponentTypes.DYED_COLOR), "method_58422", showInTooltip));
					} else if (itemFlag == ItemFlagAdapter.HIDE_ARMOR_TRIM) {
						if (itemStack.contains(DataComponentTypes.TRIM))
							builder.add(DataComponentTypes.TRIM, withShowInTooltip(get(itemStack, DataComponentTypes.TRIM), "method_58421", showInTooltip));
					} itemStack.applyChanges(builder.build());
				} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			@SuppressWarnings("unchecked")
			private static <T> T withShowInTooltip(T instance, String method, boolean showInTooltip) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
				return (T) instance.getClass().getMethod(method, boolean.class).invoke(instance, showInTooltip);
			}
			
			private static boolean showInTooltip(Object instance, String field) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
				if (instance == null)
					return true;
				Field showInTooltip = instance.getClass().getDeclaredField(field);
				
				showInTooltip.setAccessible(true);
				return (boolean) showInTooltip.get(instance);
			}
			
		}
		
	}
	
}
