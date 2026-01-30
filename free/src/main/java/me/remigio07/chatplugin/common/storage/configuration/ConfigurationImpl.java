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

package me.remigio07.chatplugin.common.storage.configuration;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationMappings;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

public class ConfigurationImpl extends Configuration {
	
	private Yaml yaml;
	
	public ConfigurationImpl(ConfigurationType type) {
		this.type = type;
		path = type.toPath();
	}
	
	public ConfigurationImpl(Path path) {
		String str = path.toString().toLowerCase();
		
		if (str.endsWith(".yml") || str.endsWith(".yaml")) {
			type = ConfigurationType.CUSTOM;
			this.path = path;
		} else throw new IllegalArgumentException("Path " + (path.getNameCount() == 0 ? "<empty path>" : "\"" + path.getFileName().toString() + "\"") + " does not end with \".yml\" or \".yaml\" (ignoring case)");
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
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
		
		if (Files.exists(path))
			try {
				map = yaml.loadAs(escape(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)), LinkedHashMap.class);
			} catch (YAMLException yamle) {
				yamle.printStackTrace();
				throw new IOException("failed to load " + (path.getNameCount() == 0 ? "<empty path>" : "\"" + path.getFileName().toString() + "\"") + ": " + yamle.getLocalizedMessage(), yamle);
			}
		else map = yaml.loadAs("", LinkedHashMap.class);
		mappings = createMappings(map == null ? new LinkedHashMap<>() : map);
	}
	
	private String escape(String data) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < data.length(); i++) {
			char ch = data.charAt(i);
			
			sb.append(Character.isHighSurrogate(ch) || Character.isLowSurrogate(ch)
					? String.format("\\u%04x", (int) ch)
					: ch
					);
		} return sb.toString();
	}
	
	@Override
	public void save() throws IOException {
		if (yaml == null)
			throw new IllegalStateException("Configuration#load() must be called at least once before saving");
		try (Writer writer = Files.newBufferedWriter(path)) {
			yaml.dump(mappings.getMappings(), writer);
		}
	}
	
	@Override
	public String toString() {
		return "ConfigurationImpl{path=" + path.toString() + "}";
	}
	
	public Yaml getYAML() {
		return yaml;
	}
	
}
