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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util.text;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.DateFormat;
import me.remigio07.chatplugin.api.server.util.Utils;

/**
 * Util class used to send translatable {@link Component}s between the proxy and the servers.
 * 
 * <p>Its methods are abstract as some of them use libraries' classes.</p>
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
	 * 
	 * <p>The <code>values</code> array may contain <code>null</code> elements.</p>
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
	 * 
	 * <p>Will return <code>null</code> if the format is invalid
	 * or the string does not corresponds to a component.</p>
	 * 
	 * @param json Component's JSON representation
	 * @return Component specified in given string
	 */
	@Nullable(why = "String may not correspond to any component or be invalid")
	public abstract Component getComponent(String json);
	
	/**
	 * Translates the given component's JSON representation.
	 * 
	 * <p>Will return <code>text</code> back if it is invalid.</p>
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
		
		/**
		 * Ban's reason component.
		 * 
		 * @see BanManager#formatReason(String, Language)
		 */
		BAN_REASON(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Ban's type component.
		 * 
		 * @see BanManager#formatTypeMessage(BanType, Language)
		 */
		BAN_TYPE_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Ban's active message component.
		 * 
		 * @see BanManager#formatActiveMessage(boolean, Language)
		 */
		BAN_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Ban's global message component.
		 * 
		 * @see BanManager#formatGlobalMessage(boolean, Language)
		 */
		BAN_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Ban's silent message component.
		 * 
		 * @see BanManager#formatSilentMessage(boolean, Language)
		 */
		BAN_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Warning's reason component.
		 * 
		 * @see WarningManager#formatReason(String, Language)
		 */
		WARNING_REASON(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Warning's max amount component.
		 * 
		 * @see WarningManager#getMaxAmount()
		 */
		WARNING_MAX_AMOUNT(),
		
		/**
		 * Warning's active message component.
		 * 
		 * @see WarningManager#formatActiveMessage(boolean, Language)
		 */
		WARNING_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Warning's global message component.
		 * 
		 * @see WarningManager#formatGlobalMessage(boolean, Language)
		 */
		WARNING_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Warning's silent message component.
		 * 
		 * @see WarningManager#formatSilentMessage(boolean, Language)
		 */
		WARNING_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Kick's reason component.
		 * 
		 * @see KickManager#formatReason(String, Language)
		 */
		KICK_REASON(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Kick's type message component.
		 * 
		 * @see KickManager#formatKickMessage(KickType, Language)
		 */
		KICK_TYPE_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Kick's silent message component.
		 * 
		 * @see KickManager#formatSilentMessage(boolean, Language)
		 */
		KICK_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Mute's reason component.
		 * 
		 * @see MuteManager#formatReason(String, Language)
		 */
		MUTE_REASON(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Mute's active message component.
		 * 
		 * @see MuteManager#formatActiveMessage(boolean, Language)
		 */
		MUTE_ACTIVE_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Mute's global message component.
		 * 
		 * @see MuteManager#formatGlobalMessage(boolean, Language)
		 */
		MUTE_GLOBAL_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Mute's silent message component.
		 * 
		 * @see MuteManager#formatSilentMessage(boolean, Language)
		 */
		MUTE_SILENT_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's ISP component.
		 * 
		 * @see IPLookup#getISP()
		 */
		IP_LOOKUP_ISP(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's continent component.
		 * 
		 * @see IPLookup#getContinent()
		 */
		IP_LOOKUP_CONTINENT(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's country component.
		 * 
		 * @see IPLookup#getCountry()
		 */
		IP_LOOKUP_COUNTRY(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's city component.
		 * 
		 * @see IPLookup#getCity()
		 */
		IP_LOOKUP_CITY(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's subdivisions component.
		 * 
		 * @see IPLookup#getSubdivisions()
		 */
		IP_LOOKUP_SUBDIVISIONS(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's country code component.
		 * 
		 * @see IPLookup#getCountryCode()
		 */
		IP_LOOKUP_COUNTRY_CODE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's postal code component.
		 * 
		 * @see IPLookup#getPostalCode()
		 */
		IP_LOOKUP_POSTAL_CODE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's latitude component.
		 * 
		 * @see IPLookup#getLatitude()
		 */
		IP_LOOKUP_LATITUDE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's longitude component.
		 * 
		 * @see IPLookup#getLongitude()
		 */
		IP_LOOKUP_LONGITUDE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's accuracy radius (in km) component.
		 * 
		 * @see IPLookup#getAccuracyRadius()
		 */
		IP_LOOKUP_ACCURACY_RADIUS_KM(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's accuracy radius (in mi) component.
		 * 
		 * @see IPLookup#getAccuracyRadius()
		 * @see Utils#kilometersToMiles(long)
		 */
		IP_LOOKUP_ACCURACY_RADIUS_MI(DEFAULT_COMPONENT_KEY),
		
		/**
		 * IP lookup's accuracy radius (in nm) component.
		 * 
		 * @see IPLookup#getAccuracyRadius()
		 * @see Utils#kilometersToNauticalMiles(long)
		 */
		IP_LOOKUP_ACCURACY_RADIUS_NM(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Time format's component.
		 * 
		 * <p><strong>Keys:</strong> ["value", "everInsteadOfNever", "useZeroSecondsInstead"]</p>
		 * 
		 * @see Utils#formatTime(long, Language, boolean, boolean)
		 */
		TIME_FORMAT(DEFAULT_COMPONENT_KEY, "everInsteadOfNever", "useZeroSecondsInstead"),
		
		/**
		 * Date format's component.
		 * 
		 * <p><strong>Keys:</strong> ["value", "format"]</p>
		 * 
		 * @see Utils#formatDate(long, Language, DateFormat)
		 */
		DATE_FORMAT(DEFAULT_COMPONENT_KEY, "format"),
		
		/**
		 * Memory format's component.
		 * 
		 * <p><strong>Keys:</strong> ["value", "scale"]</p>
		 * 
		 * @see MemoryUtils#formatMemory(long, MemoryUtils)
		 */
		MEMORY_FORMAT(DEFAULT_COMPONENT_KEY, "scale"),
		
		/**
		 * Memory default format's component.
		 * 
		 * @see MemoryUtils#formatMemory(long) 
		 */
		MEMORY_DEFAULT_FORMAT(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Translated message's (with no arguments) component.
		 * 
		 * @see Language#getMessage(String, Object...)
		 */
		TRANSLATED_MESSAGE(DEFAULT_COMPONENT_KEY),
		
		/**
		 * Translated message's (with arguments) component.
		 * 
		 * <p><strong>Keys:</strong> ["value", "args"]</p>
		 * 
		 * @see Language#getMessage(String, Object...)
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
