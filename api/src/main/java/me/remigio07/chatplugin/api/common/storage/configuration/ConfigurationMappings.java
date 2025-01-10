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

package me.remigio07.chatplugin.api.common.storage.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;

/**
 * Represents mappings of keys and values in a {@link Configuration}.
 */
public class ConfigurationMappings {
	
	private Map<String, Object> mappings = new LinkedHashMap<>();
	
	private ConfigurationMappings() {
		
	}
	
	/**
	 * Creates new mappings for a configuration.
	 * 
	 * @deprecated Internal use only. Use {@link Configuration#Configuration(java.io.File)}
	 * to create custom configurations and {@link Configuration#load()} to load them.
	 * @param mappings Configuration's mappings
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public ConfigurationMappings(Map<String, Object> mappings) {
		for (Entry<String, Object> entry : mappings.entrySet()) {
			String key = entry.getKey() == null ? "null" : entry.getKey().toString();
			
			if (entry.getValue() instanceof Map)
				this.mappings.put(key, new ConfigurationMappings((Map<String, Object>) entry.getValue()));
			else this.mappings.put(key, entry.getValue());
		}
	}
	
	/**
	 * Gets this configuration's mappings.
	 * 
	 * @return Configuration's mappings
	 */
	public Map<String, Object> getMappings() {
		return mappings;
	}
	
	/**
	 * Gets the value at the specified path.
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param <T> Value's type
	 * @param path Path to check
	 * @param def Default value
	 * @return Value at path
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Default value may be null")
	public <T> T get(String path, @Nullable(why = "Default value may be null") T def) {
		ConfigurationMappings section = getSectionFor(path, false);
		
		if (section == null)
			return def;
		Object value;
		
		if (section == this)
			value = mappings.get(path);
		else value = section.get(getChild(path), def);
		if (value == null && def instanceof ConfigurationMappings)
			mappings.put(path, def);
		return value == null ? def : (T) value;
	}
	
	/**
	 * Gets a path's child path.
	 * 
	 * <p>Will return given path if it has no children.</p>
	 * 
	 * @param path Path to check
	 * @return Child path
	 */
	@NotNull
	public String getChild(@NotNull String path) {
		int index = path.indexOf('.');
		return index == -1 ? path : path.substring(index + 1);
	}
	
	/**
	 * Gets the section at the specified path.
	 * 
	 * <p>Will return <code>null</code> if the config does not contain the specified section.</p>
	 * 
	 * @param path Path to check
	 * @return Section at specified path
	 * @throws ClassCastException If specified path does not contain a section
	 */
	@Nullable(why = "Config may not contain specified section")
	public ConfigurationMappings getSection(String path) {
		return (ConfigurationMappings) get(path, null);
	}
	
	private ConfigurationMappings getSectionFor(String path, boolean put) {
		int index = path.indexOf('.');
		
		if (index == -1)
			return this;
		String root = path.substring(0, index);
		Object section = mappings.get(root);
		
		if (section == null) {
			section = new ConfigurationMappings();
			
			if (put)
				mappings.put(root, section);
		} return (ConfigurationMappings) section;
	}
	
	/**
	 * Gets the keys at the root path.
	 * 
	 * @return Keys at root path
	 */
	@NotNull
	public List<String> getKeys() {
		return new ArrayList<>(new LinkedHashSet<>(mappings.keySet()));
	}
	
	/**
	 * Gets the keys at the specified path.
	 * 
	 * @param path Path to check
	 * @return Keys at specified path
	 */
	@NotNull
	public List<String> getKeys(String path) {
		ConfigurationMappings section = getSection(path);
		return new ArrayList<>(section == null ? Collections.emptyList() : section.getKeys());
	}
	
	/**
	 * Checks if a path is contained in this config.
	 * 
	 * @param path Path to check
	 * @return Whether the key is contained in this config
	 */
	public boolean contains(String path) {
		return get(path, null) != null;
	}
	
	/**
	 * Adds a default value at the specified path.
	 * 
	 * @param path Default value's path
	 * @param value Default value to add
	 */
	public void addDefault(String path, @NotNull Object value) {
		if (!contains(path))
			set(path, value);
	}
	
	/**
	 * Sets a value at the specified path.
	 * 
	 * <p>If you need to remove the mapping at the specified path,
	 * specify a <code>value</code> of <code>null</code>.</p>
	 * 
	 * @param path Value's path
	 * @param value Value to set
	 */
	@SuppressWarnings("unchecked")
	public void set(String path, @Nullable(why = "Mapping is removed when value is null") Object value) {
		if (value instanceof Map)
			value = new ConfigurationMappings((Map<String, Object>) value);
		ConfigurationMappings section = getSectionFor(path, true);
		
		if (section != null) {
			if (section == this) {
				if (value == null)
					mappings.remove(path);
				else mappings.put(path, value);
			} else section.set(getChild(path), value);
		}
	}
	
	// Getters
	
	/**
	 * Gets a boolean value.
	 * 
	 * <p>Will return <code>false</code> if a <code>boolean</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Boolean value
	 */
	public boolean getBoolean(String path) {
		return getBoolean(path, false);
	}
	
	/**
	 * Gets a boolean value.
	 * 
	 * <p>Will return <code>def</code> if a <code>boolean</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Boolean value
	 */
	public boolean getBoolean(String path, boolean def) {
		Object value = get(path, def);
		return value instanceof Boolean ? (boolean) value : def;
	}
	
	/**
	 * Gets a byte value.
	 * 
	 * <p>Will return <code>0x0</code> if a <code>byte</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Byte value
	 */
	public byte getByte(String path) {
		return getByte(path, (byte) 0);
	}
	
	/**
	 * Gets a byte value.
	 * 
	 * <p>Will return <code>def</code> if a <code>byte</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Byte value
	 */
	public byte getByte(String path, byte def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).byteValue() : def;
	}
	
	/**
	 * Gets a short value.
	 * 
	 * <p>Will return <code>0</code> if a <code>short</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Short value
	 */
	public short getShort(String path) {
		return getShort(path, (short) 0);
	}
	
	/**
	 * Gets a short value.
	 * 
	 * <p>Will return <code>def</code> if a <code>short</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Short value
	 */
	public short getShort(String path, short def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).shortValue() : def;
	}
	
	/**
	 * Gets an int value.
	 * 
	 * <p>Will return <code>0</code> if an <code>int</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Int value
	 */
	public int getInt(String path) {
		return getInt(path, 0);
	}
	
	/**
	 * Gets an int value.
	 * 
	 * <p>Will return <code>def</code> if an <code>int</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Int value
	 */
	public int getInt(String path, int def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).intValue() : def;
	}
	
	/**
	 * Gets a long value.
	 * 
	 * <p>Will return <code>0L</code> if a <code>long</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Long value
	 */
	public long getLong(String path) {
		return getLong(path, 0L);
	}
	
	/**
	 * Gets a long value.
	 * 
	 * <p>Will return <code>def</code> if a <code>long</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Long value
	 */
	public long getLong(String path, long def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).longValue() : def;
	}
	
	/**
	 * Gets a float value.
	 * 
	 * <p>Will return <code>0F</code> if a <code>float</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Float value
	 */
	public float getFloat(String path) {
		return getFloat(path, 0F);
	}
	
	/**
	 * Gets a float value.
	 * 
	 * <p>Will return <code>def</code> if a <code>float</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Float value
	 */
	public float getFloat(String path, float def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).floatValue() : def;
	}
	
	/**
	 * Gets a double value.
	 * 
	 * <p>Will return <code>0D</code> if a <code>double</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Double value
	 */
	public double getDouble(String path) {
		return getDouble(path, 0D);
	}
	
	/**
	 * Gets a double value.
	 * 
	 * <p>Will return <code>def</code> if a <code>double</code> is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Double value
	 */
	public double getDouble(String path, double def) {
		Object value = get(path, def);
		return value instanceof Number ? ((Number) value).doubleValue() : def;
	}
	
	/**
	 * Gets a String value.
	 * 
	 * <p>Will return "" if a {@link String} is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return String value
	 */
	@NotNull
	public String getString(String path) {
		return getString(path, "");
	}
	
	/**
	 * Gets a String value.
	 * 
	 * <p>Will return <code>def</code> if a {@link String} is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return String value
	 */
	@Nullable(why = "Default value may be null")
	public String getString(String path, @Nullable(why = "Default value may be null") String def) {
		Object value = get(path, def);
		return value instanceof String ? (String) value : def;
	}
	
	/**
	 * Gets a list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a {@link List} is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return List value
	 */
	@NotNull
	public List<?> getList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a list value.
	 * 
	 * <p>Will return <code>def</code> if a {@link List}<code>&lt;T&gt;</code> is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param <T> List's type
	 * @param path Path to check
	 * @param def Default value
	 * @return List value
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Default value may be null")
	public <T> List<T> getList(String path, List<T> def) {
		Object value = get(path, def);
		
		try {
			return (List<T>) value;
		} catch (ClassCastException e) {
			return def;
		}
	}
	
	/**
	 * Gets a Boolean list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Boolean}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Boolean list value
	 */
	@NotNull
	public List<Boolean> getBooleanList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a Byte list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Byte}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Byte list value
	 */
	@NotNull
	public List<Byte> getByteList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a Short list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Short}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Short list value
	 */
	@NotNull
	public List<Short> getShortList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets an Integer list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Integer}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Integer list value
	 */
	@NotNull
	public List<Integer> getIntegerList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a Long list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Long}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Long list value
	 */
	@NotNull
	public List<Long> getLongList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a Float list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Float}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Float list value
	 */
	@NotNull
	public List<Float> getFloatList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a Double list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link Double}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Double list value
	 */
	@NotNull
	public List<Double> getDoubleList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	/**
	 * Gets a String list value.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link String}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return String list value
	 */
	@NotNull
	public List<String> getStringList(String path) {
		return getList(path, new ArrayList<>());
	}
	
	// Translators
	
	/**
	 * Calls {@link #translateString(String, String)}
	 * specifying <code>path</code> and "" as arguments.
	 * 
	 * <p>Will return "" if a {@link String} is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Translated String
	 */
	@NotNull
	public String translateString(String path) {
		return translateString(path, "");
	}
	
	/**
	 * Calls {@link #translateString(String, String, boolean)} specifying
	 * <code>path</code>, <code>def</code> and <code>true</code> as arguments.
	 * 
	 * <p>Will return <code>def</code> if a {@link String}
	 * is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Translated String
	 */
	@Nullable(why = "Default value may be null")
	public String translateString(String path, @Nullable(why = "Default value may be null") String def) {
		return translateString(path, def, true);
	}
	
	/**
	 * Calls {@link ChatColor#translate(String, boolean)} specifying
	 * {@link #getString(String, String)} and <code>retainNewLines</code> as arguments.
	 * 
	 * <p>Will return <code>def</code> if a {@link String}
	 * is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @param retainNewLines Whether to retain new lines ("\n")
	 * @return Translated String
	 */
	@Nullable(why = "Default value may be null")
	public String translateString(String path, @Nullable(why = "Default value may be null") String def, boolean retainNewLines) {
		return contains(path) || def != null ? ChatColor.translate(getString(path, def), retainNewLines) : null;
	}
	
	/**
	 * Calls {@link #translateStringList(String, List)} specifying
	 * <code>path</code>, <code>new </code>{@link ArrayList} and <code>true</code> as arguments.
	 * 
	 * <p>Will return a new {@link ArrayList} if a
	 * {@link List}<code>&lt;{@link String}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * @param path Path to check
	 * @return Translated String list
	 */
	@NotNull
	public List<String> translateStringList(String path) {
		return translateStringList(path, new ArrayList<>());
	}
	
	/**
	 * Calls {@link #translateStringList(String, List, boolean)} specifying
	 * <code>path</code>, <code>def</code> and <code>true</code> as arguments.
	 * 
	 * <p>Will return <code>def</code> if a
	 * {@link List}<code>&lt;{@link String}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Translated String list
	 */
	@Nullable(why = "Default value may be null")
	public List<String> translateStringList(String path, @Nullable(why = "Default value may be null") List<String> def) {
		return translateStringList(path, def, true);
	}
	
	/**
	 * Calls {@link ChatColor#translate(List, boolean)} specifying
	 * {@link #getString(String, String)} and <code>retainNewLines</code> as arguments.
	 * 
	 * <p>Will return <code>def</code> if a
	 * {@link List}<code>&lt;{@link String}&gt;</code>
	 * is not found at the specified path.</p>
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @param retainNewLines Whether to retain new lines ("\n")
	 * @return Translated String list
	 */
	@Nullable(why = "Default value may be null")
	public List<String> translateStringList(String path, @Nullable(why = "Default value may be null") List<String> def, boolean retainNewLines) {
		return contains(path) || def != null ? ChatColor.translate(getList(path, def), retainNewLines) : null;
	}
	
}
