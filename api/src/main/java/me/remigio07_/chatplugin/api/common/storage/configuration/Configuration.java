/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.common.storage.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents a YAML configuration handled by the {@link ConfigurationManager}.
 */
public class Configuration {
	
	protected ConfigurationType type;
	protected Yaml yaml;
	protected ConfigurationMappings mappings;
	protected File file;
	
	/**
	 * Constructs an internal configuration of ChatPlugin.
	 * 
	 * @deprecated Internal use only. Use {@link #Configuration(File)} instead.
	 * @param type Configuration's type
	 */
	@Deprecated
	public Configuration(ConfigurationType type) {
		this.type = type;
		file = type.getFile();
	}
	
	/**
	 * Constructs a new configuration of type {@link ConfigurationType#CUSTOM}.
	 * If <code>file</code> does not exist, it will be created.
	 * 
	 * @param file Configuration's file
	 * @throws IOException If {@link File#createNewFile()} fails
	 * @throws IllegalArgumentException If file's name does not end with ".yml" or ".yaml" (ignoring case)
	 */
	public Configuration(File file) throws IOException {
		if (!file.exists())
			file.createNewFile();
		if (file.getName().toLowerCase().endsWith(".yml") || file.getName().toLowerCase().endsWith(".yaml")) {
			type = ConfigurationType.CUSTOM;
			this.file = file;
		} else throw new IllegalArgumentException("File name \"" + file.getName() + "\" is invalid as it does not end with \".yml\" or \".yaml\" (ignoring case)");
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
	 * Gets this configuration's file.
	 * 
	 * @return Configuration's file
	 */
	@NotNull
	public File getFile() {
		return file;
	}
	
	/**
	 * Creates {@link #getFile()} if it does not exist.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void createFile() throws IOException {
		if (!file.exists()) {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}
	
	/**
	 * Loads {@link #getMappings()} from {@link #getFile()}.
	 * 
	 * @throws IOException If something goes wrong
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void load() throws IOException {
		DumperOptions options = new DumperOptions();
		Map<String, Object> map = null;
		
		options.setWidth(250);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		
		yaml = new Yaml(new Representer() {
			
			{
				representers.put(ConfigurationMappings.class, new Represent() {
					
					@Override
					public Node representData(Object data) {
						return represent(((ConfigurationMappings) data).getMappings());
					}
					
				});
			}
		}, options);
		
		if (!file.exists())
			map = yaml.loadAs("", LinkedHashMap.class);
		else try (InputStream input = new FileInputStream(file)) {
			map = yaml.loadAs(input, LinkedHashMap.class);
		} mappings = new ConfigurationMappings(map == null ? new LinkedHashMap<>() : map);
	}
	
	/**
	 * Saves {@link #getMappings()} into {@link #getFile()}.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void save() throws IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			yaml.dump(mappings.getMappings(), writer);
		}
	}
	
	// Forward the following methods to this config's ConfigurationMappings object.
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getSection(String)}.
	 * Will return <code>null</code> if the config does not contain the specified section.
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
	 * If you need to remove the mapping at the specified path,
	 * specify a <code>null</code> as <code>value</code>.
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
	 * Shortcut for {@link ConfigurationMappings#getChar(String)}.
	 * 
	 * @param path Path to check
	 * @return Char value
	 */
	public char getChar(String path) {
		return mappings.getChar(path, '\u0000');
	}
	
	/**
	 * Shortcut for {@link ConfigurationMappings#getChar(String, char)}.
	 * 
	 * @param path Path to check
	 * @param def Default value
	 * @return Char value
	 */
	public char getChar(String path, char def) {
		return mappings.getChar(path, def);
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
	 * You can specify <code>null</code> as <code>def</code>.
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
	 * You can specify <code>null</code> as <code>def</code>.
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
	 * Shortcut for {@link ConfigurationMappings#getCharacterList(String)}.
	 * 
	 * @param path Path to check
	 * @return Char list value
	 */
	@NotNull
	public List<Character> getCharacterList(String path) {
		return mappings.getCharacterList(path);
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
	 * Shortcut for {@link ConfigurationMappings#translateString(String, String, boolean)}.
	 * You can specify <code>null</code> as <code>def</code>.
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
	 * Shortcut for {@link ConfigurationMappings#translateStringList(String, List, boolean)}.
	 * You can specify <code>null</code> as <code>def</code>.
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
