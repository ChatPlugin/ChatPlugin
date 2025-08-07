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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents a YAML configuration handled by the {@link ConfigurationManager}.
 */
public class Configuration {
	
	protected ConfigurationType type;
	protected Yaml yaml;
	protected ConfigurationMappings mappings;
	protected Path path;
	
	/**
	 * Constructs an internal configuration of ChatPlugin.
	 * 
	 * @deprecated Internal use only. Use {@link #Configuration(Path)} instead.
	 * @param type Configuration's type
	 */
	@Deprecated
	public Configuration(ConfigurationType type) {
		this.type = type;
		path = type.toPath();
	}
	
	/**
	 * Constructs a new {@link ConfigurationType#CUSTOM} configuration.
	 * 
	 * <p>You may want to {@link #load()} this configuration
	 * after calling {@link #createFile(FileAttribute...)}.</p>
	 * 
	 * @param path Configuration's path
	 * @throws IllegalArgumentException If the path does
	 * not end with ".yml" or ".yaml" (ignoring case)
	 */
	public Configuration(Path path) {
		String str = path.toString().toLowerCase();
		
		if (str.endsWith(".yml") || str.endsWith(".yaml")) {
			type = ConfigurationType.CUSTOM;
			this.path = path;
		} else throw new IllegalArgumentException("Path " + (path.getNameCount() == 0 ? "<empty path>" : "\"" + path.getFileName().toString() + "\"") + " does not end with \".yml\" or \".yaml\" (ignoring case)");
	}
	}
	
	/**
	 * Gets this configuration's type.
	 * 
	 * @return Configuration's type
	 */
	@NotNull
	public ConfigurationType getType() {
		return type;
	}
	
	/**
	 * Gets this configuration's {@link org.yaml.snakeyaml.Yaml} object.
	 * 
	 * @return Configuration's YAML object
	 */
	@NotNull
	public Yaml getYAML() {
		return yaml;
	}
	
	/**
	 * Gets this configuration's mappings,
	 * which contains the keys and their values.
	 * 
	 * @return Configuration's mappings
	 */
	@NotNull
	public ConfigurationMappings getMappings() {
		return mappings;
	}
	
	/**
	 * Gets this configuration's path.
	 * 
	 * @return Configuration's path
	 */
	@NotNull
	public Path getPath() {
		return path;
	}
	
	/**
	 * Creates this configuration's file if it does not exist.
	 * 
	 * @param attributes File's optional attributes
	 * @throws IOException If something goes wrong
	 */
	public void createFile(FileAttribute<?>... attributes) throws IOException {
		if (!Files.exists(path)) {
			if (path.getParent() != null)
				Files.createDirectories(path.getParent());
			Files.createFile(path, attributes);
		}
	}
	
	/**
	 * Loads mappings from this configuration's file.
	 * 
	 * @throws IOException If something goes wrong
	 * @see #createFile(FileAttribute...)
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void load() throws IOException {
		DumperOptions options = new DumperOptions();
		Map<String, Object> map = null;
		Representer representer;
		
		options.setWidth(250);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		try {
			representer = new Representer(options) {{
				representers.put(ConfigurationMappings.class, new Represent() {
					
					@Override
					public Node representData(Object data) {
						return represent(((ConfigurationMappings) data).getMappings());
					}
					
				});
			}};
		} catch (NoSuchMethodError nsme) { // compatible with older SnakeYAML versions
			representer = new Representer() {{
				representers.put(ConfigurationMappings.class, new Represent() {
					
					@Override
					public Node representData(Object data) {
						return represent(((ConfigurationMappings) data).getMappings());
					}
					
				});
			}};
		} yaml = new Yaml(representer, options);
		
		if (!Files.exists(path))
			map = yaml.loadAs("", LinkedHashMap.class);
		else try (InputStream input = Files.newInputStream(path)) {
			map = yaml.loadAs(input, LinkedHashMap.class);
		} catch (YAMLException yamle) {
			throw new IOException("failed to load " + (path.getNameCount() == 0 ? "<empty path>" : "\"" + path.getFileName().toString() + "\"") + ": " + yamle.getLocalizedMessage(), yamle);
		} mappings = new ConfigurationMappings(map == null ? new LinkedHashMap<>() : map);
	}
	
	/**
	 * Saves mappings into this configuration's file.
	 * 
	 * @throws IOException If something goes wrong
	 * @throws IllegalStateException If {@link #load()} has not been called yet
	 */
	public void save() throws IOException {
		if (yaml == null)
			throw new IllegalStateException("Configuration#load() must be called at least once before saving");
		try (Writer writer = Files.newBufferedWriter(path)) {
			yaml.dump(mappings.getMappings(), writer);
		}
	}
	
	// Forward the following methods to this config's ConfigurationMappings object.
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getSection(String)}.
	 * 
	 * <p>Will return <code>null</code> if the config does not contain the specified section.</p>
	 * 
	 * @param path Section's path
	 * @return Section at specified path
	 */
	@Nullable(why = "Config may not contain specified section")
	public ConfigurationMappings getSection(String path) {
		return mappings.getSection(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getKeys()}.
	 * 
	 * @return Keys at root path
	 */
	@NotNull
	public List<String> getKeys() {
		return mappings.getKeys();
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getKeys(String)}.
	 * 
	 * @param path Section's path
	 * @return Keys at specified path
	 */
	@NotNull
	public List<String> getKeys(String path) {
		return mappings.getKeys(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#contains(String)}.
	 * 
	 * @param path Path to check
	 * @return Whether the key is contained in this config
	 */
	public boolean contains(String path) {
		return mappings.contains(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#addDefault(String, Object)}.
	 * 
	 * @param path Default value's path
	 * @param value Default value to add
	 */
	public void addDefault(String path, @NotNull Object value) {
		mappings.addDefault(path, value);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#set(String, Object)}.
	 * 
	 * <p>If you need to remove the mapping at the specified path,
	 * specify a <code>null</code> as <code>value</code>.</p>
	 * 
	 * @param path Value's path
	 * @param value Value to set
	 */
	public void set(String path, @Nullable(why = "Mapping is removed when value is null") Object value) {
		mappings.set(path, value);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getBoolean(String)}.
	 * 
	 * @param path Path to check
	 * @return Boolean value
	 */
	public boolean getBoolean(String path) {
		return mappings.getBoolean(path, false);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getBoolean(String, boolean)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Boolean value
	 */
	public boolean getBoolean(String path, boolean def) {
		return mappings.getBoolean(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getByte(String)}.
	 * 
	 * @param path Path to check
	 * @return Byte value
	 */
	public byte getByte(String path) {
		return mappings.getByte(path, (byte) 0);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getByte(String, byte)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Byte value
	 */
	public byte getByte(String path, byte def) {
		return mappings.getByte(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getShort(String)}.
	 * 
	 * @param path Path to check
	 * @return Short value
	 */
	public short getShort(String path) {
		return mappings.getShort(path, (short) 0);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getShort(String, short)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Short value
	 */
	public short getShort(String path, short def) {
		return mappings.getShort(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getInt(String)}.
	 * 
	 * @param path Path to check
	 * @return Int value
	 */
	public int getInt(String path) {
		return mappings.getInt(path, 0);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getInt(String, int)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Int value
	 */
	public int getInt(String path, int def) {
		return mappings.getInt(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getLong(String)}.
	 * 
	 * @param path Path to check
	 * @return Long value
	 */
	public long getLong(String path) {
		return mappings.getLong(path, 0L);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getLong(String, long)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Long value
	 */
	public long getLong(String path, long def) {
		return mappings.getLong(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getFloat(String)}.
	 * 
	 * @param path Path to check
	 * @return Float value
	 */
	public float getFloat(String path) {
		return mappings.getFloat(path, 0F);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getFloat(String, float)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Float value
	 */
	public float getFloat(String path, float def) {
		return mappings.getFloat(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getDouble(String)}.
	 * 
	 * @param path Path to check
	 * @return Double value
	 */
	public double getDouble(String path) {
		return mappings.getDouble(path, 0D);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getDouble(String, double)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Double value
	 */
	public double getDouble(String path, double def) {
		return mappings.getDouble(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getString(String)}.
	 * 
	 * @param path Path to check
	 * @return String value
	 */
	@NotNull
	public String getString(String path) {
		return mappings.getString(path, "");
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getString(String, String)}.
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return String value
	 */
	@Nullable(why = "Default value may be null")
	public String getString(String path, @Nullable(why = "Default value may be null") String def) {
		return mappings.getString(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getList(String)}.
	 * 
	 * @param path Path to check
	 * @return List value
	 */
	@NotNull
	public List<?> getList(String path) {
		return mappings.getList(path, new ArrayList<>());
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getList(String, List)}.
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param <T> List's type
	 * @param path Path to check
	 * @param def Default value
	 * @return List value
	 */
	@Nullable(why = "Default value may be null")
	public <T> List<T> getList(String path, @Nullable(why = "Default value may be null") List<T> def) {
		return mappings.getList(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getBooleanList(String)}.
	 * 
	 * @param path Path to check
	 * @return Boolean list value
	 */
	@NotNull
	public List<Boolean> getBooleanList(String path) {
		return mappings.getBooleanList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getByteList(String)}.
	 * 
	 * @param path Path to check
	 * @return Byte list value
	 */
	@NotNull
	public List<Byte> getByteList(String path) {
		return mappings.getByteList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getShortList(String)}.
	 * 
	 * @param path Path to check
	 * @return Short list value
	 */
	@NotNull
	public List<Short> getShortList(String path) {
		return mappings.getShortList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getIntegerList(String)}.
	 * 
	 * @param path Path to check
	 * @return Integer list value
	 */
	@NotNull
	public List<Integer> getIntegerList(String path) {
		return mappings.getIntegerList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getLongList(String)}.
	 * 
	 * @param path Path to check
	 * @return Long list value
	 */
	@NotNull
	public List<Long> getLongList(String path) {
		return mappings.getLongList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getFloatList(String)}.
	 * 
	 * @param path Path to check
	 * @return Float list value
	 */
	@NotNull
	public List<Float> getFloatList(String path) {
		return mappings.getFloatList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getDoubleList(String)}.
	 * 
	 * @param path Path to check
	 * @return Double list value
	 */
	@NotNull
	public List<Double> getDoubleList(String path) {
		return mappings.getDoubleList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getStringList(String)}.
	 * 
	 * @param path Path to check
	 * @return String list value
	 */
	@NotNull
	public List<String> getStringList(String path) {
		return mappings.getStringList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateString(String)}.
	 * 
	 * @param path Path to check
	 * @return Translated String
	 */
	@NotNull
	public String translateString(String path) {
		return mappings.translateString(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateString(String, String)}.
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Translated String
	 */
	@Nullable(why = "Default value may be null")
	public String translateString(String path, @Nullable(why = "Default value may be null") String def) {
		return mappings.translateString(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateString(String, String, boolean)}.
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
		return mappings.translateString(path, def, retainNewLines);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateStringList(String)}.
	 * 
	 * @param path Path to check
	 * @return Translated String list
	 */
	@NotNull
	public List<String> translateStringList(String path) {
		return mappings.translateStringList(path);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateStringList(String, List)}.
	 * 
	 * <p>You can specify <code>null</code> as <code>def</code>.</p>
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Translated String list
	 */
	@Nullable(why = "Default value may be null")
	public List<String> translateStringList(String path, @Nullable(why = "Default value may be null") List<String> def) {
		return mappings.translateStringList(path, def);
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#translateStringList(String, List, boolean)}.
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
		return mappings.translateStringList(path, def, retainNewLines);
	}
	
}
