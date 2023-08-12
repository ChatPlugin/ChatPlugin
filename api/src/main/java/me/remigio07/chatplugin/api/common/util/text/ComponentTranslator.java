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

package me.remigio07.chatplugin.api.common.util.text;

import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.DateFormat;

/**
 * Util class used to send translatable {@link Component}s between the proxy and the servers.
 * Its methods are abstract as some of them use libraries' classes.
 */
public abstract class ComponentTranslator {
	
	/**
	 * Default key used in most components' JSON representations.
	 * 
	 * <p><strong>Content:</strong> "value"</p>
	 */
	public static final String DEFAULT_COMPONENT_KEY = "value";
	protected static ComponentTranslator instance;
	
	/**
	 * Creates a component's JSON representation with given values.
	 * The <code>values</code> array may contain <code>null</code> elements.
	 * 
	 * @param component Component to create
	 * @param values Component's values
	 * @return Component's JSON representation
	 * @throws IllegalArgumentException If {@link Component#getKeys()}<code>.length != values.length</code>
	 */
	@NotNull
	public abstract String createJSON(Component component, Object... values);
	
	/**
	 * Gets the component specified in the given JSON string
	 * obtained using {@link #createJSON(Component, Object...)}.
	 * Will return <code>null</code> if the format is invalid
	 * or the string does not corresponds to a component.
	 * 
	 * @param json Component's JSON representation
	 * @return Component specified in given string
	 */
	@Nullable(why = "String may not correspond to any component or be invalid")
	public abstract Component getComponent(String json);
	
	/**
	 * Translates the given component's JSON representation.
	 * Will return <code>text</code> back if it is invalid.
	 * 
	 * @param language Language used to translate the component
	 * @param text Text or component to translate
	 * @return Translated component or given text
	 */
	@NotNull
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	public abstract String translate(Language language, String text);
	
	/**
	 * Gets the translator's current instance.
	 * 
	 * @return Current instance
	 */
	public static ComponentTranslator getInstance() {
		return instance;
	}
	
	/**
	 * Represents the components used in the plugin's messages.
	 */
	public enum Component {
		
		BAN_REASON(DEFAULT_COMPONENT_KEY),
		BAN_TYPE(DEFAULT_COMPONENT_KEY),
		BAN_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		BAN_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		BAN_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		WARNING_REASON(DEFAULT_COMPONENT_KEY),
		WARNING_MAX_AMOUNT(),
		WARNING_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		WARNING_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		WARNING_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		KICK_REASON(DEFAULT_COMPONENT_KEY),
		KICK_TYPE(DEFAULT_COMPONENT_KEY),
		KICK_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		MUTE_REASON(DEFAULT_COMPONENT_KEY),
		MUTE_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		MUTE_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		MUTE_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		IP_LOOKUP_ISP(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_CONTINENT(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_COUNTRY(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_CITY(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_SUBDIVISIONS(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_COUNTRY_CODE(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_POSTAL_CODE(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_LATITUDE(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_LONGITUDE(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_ACCURACY_RADIUS_KM(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_ACCURACY_RADIUS_MI(DEFAULT_COMPONENT_KEY),
		IP_LOOKUP_ACCURACY_RADIUS_NM(DEFAULT_COMPONENT_KEY),
		
		/**
		 * <p><strong>Keys:</strong> ["value", "everInsteadOfNever", "useZeroSecondsInstead"]
		 * <br><strong>Reference:</strong> {@link Utils#formatTime(long, Language, boolean, boolean)}</p>
		 */
		TIME_FORMAT(DEFAULT_COMPONENT_KEY, "everInsteadOfNever", "useZeroSecondsInstead"),
		
		/**
		 * <p><strong>Keys:</strong> ["value", "format"]
		 * <br><strong>Reference:</strong> {@link Utils#formatDate(long, Language, DateFormat)}</p>
		 */
		DATE_FORMAT(DEFAULT_COMPONENT_KEY, "format"),
		
		/**
		 * <p><strong>Keys:</strong> ["value", "scale"]
		 * <br><strong>Reference:</strong> {@link MemoryUtils#formatMemory(long, MemoryUtils)}</p>
		 */
		MEMORY_FORMAT(DEFAULT_COMPONENT_KEY, "scale"),
		MEMORY_DEFAULT_FORMAT(DEFAULT_COMPONENT_KEY),
		
		TRANSLATED_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * <p><strong>Keys:</strong> ["value", "args"]
		 * <br><strong>Reference:</strong> {@link Language#getMessage(String, Object...)}</p>
		 */
		TRANSLATED_MESSAGE_ARGS(DEFAULT_COMPONENT_KEY, "args");
		
		private String[] keys;
		
		private Component(String... keys) {
			this.keys = keys;
		}
		
		/**
		 * Gets this component's keys used in the JSON representation.
		 * 
		 * @return Component's keys
		 */
		public String[] getKeys() {
			return keys;
		}
		
		/**
		 * Gets this component's ID.
		 * 
		 * @return Component's ID
		 */
		public String getID() {
			return name().toLowerCase();
		}
		
	}
	
}
