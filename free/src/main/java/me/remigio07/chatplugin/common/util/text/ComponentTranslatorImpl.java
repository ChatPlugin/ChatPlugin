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

package me.remigio07.chatplugin.common.util.text;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.DateFormat;
import me.remigio07.chatplugin.api.server.util.Utils;

public class ComponentTranslatorImpl extends ComponentTranslator {
	
	@Override
	public String createJSON(Component component, Object... values) {
		if (component.getKeys().length != values.length && !containsArray(values))
			throw new IllegalArgumentException("The lengths of the arrays of the specified values (" + values.length + ") and " + component.name() + "'s keys (" + component.getKeys().length + ") do not match");
		StringBuilder json = new StringBuilder("{\"id\":\"" + component.getID().toLowerCase() + "\",");
		
		for (int i = 0; i < component.getKeys().length; i++) {
			json.append("\"" + component.getKeys()[i] + "\":");
			
			if (values[i].getClass().isArray())
				json.append(toJSON((Object[]) values[i]));
			else if (values[i] instanceof String || values[i] instanceof Enum)
				json.append("\"" + values[i] + "\",");
			else json.append(values[i] + ",");
		} json.delete(json.length() - 1, json.length());
		return json.toString() + "}";
	}
	
	private static boolean containsArray(Object... values) {
		for (Object value : values)
			if (value.getClass().isArray())
				return true;
		return false;
	}
	
	private static String toJSON(Object[] array) {
		if (array.length == 0)
			return "[]";
		StringBuilder sb = new StringBuilder("[");
		
		for (Object o : array)
			sb.append("\"" + o + "\", ");
		sb.setLength(sb.length() - 2);
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public Component getComponent(String json) {
		try {
			Jsoner.deserialize(json); // validation
			return Component.valueOf(json.substring(7, (json.contains(",") ? json.indexOf(',') : json.indexOf('}')) - 1).toUpperCase());
		} catch (JsonException | IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
			
		} return null;
	}
	
	@Override
	public String translate(Language language, String text) {
		Component component = getComponent(text);
		JsonObject json;
		Object defaultValue;
		
		try {
			component.toString(); // validation: throws NPE if null
			
			json = (JsonObject) Jsoner.deserialize(text);
			
			if (json.get("value") instanceof String && json.get("value").equals("null"))
				defaultValue = null;
			else defaultValue = json.get("value");
		} catch (JsonException | NullPointerException e) {
			return text;
		} try {
			switch (getComponent(text)) {
			case BAN_REASON:
				return BanManager.getInstance().formatReason((String) defaultValue, language);
			case WARNING_REASON:
				return WarningManager.getInstance().formatReason((String) defaultValue, language);
			case MUTE_REASON:
				return MuteManager.getInstance().formatReason((String) defaultValue, language);
			case KICK_REASON:
				return KickManager.getInstance().formatReason((String) defaultValue, language);
			case BAN_TYPE_MESSAGE:
				return BanManager.getInstance().formatTypeMessage(BanType.valueOf((String) defaultValue), language);
			case KICK_TYPE_MESSAGE:
				return KickManager.getInstance().formatTypeMessage(KickType.valueOf((String) defaultValue), language);
			case WARNING_MAX_AMOUNT:
				return String.valueOf(WarningManager.getInstance().getMaxAmount());
			case BAN_ACTIVE_MESSAGE:
				return BanManager.getInstance().formatActiveMessage((boolean) defaultValue, language);
			case WARNING_ACTIVE_MESSAGE:
				return WarningManager.getInstance().formatActiveMessage((boolean) defaultValue, language);
			case MUTE_ACTIVE_MESSAGE:
				return MuteManager.getInstance().formatActiveMessage((boolean) defaultValue, language);
			case BAN_GLOBAL_MESSAGE:
				return BanManager.getInstance().formatGlobalMessage((boolean) defaultValue, language);
			case WARNING_GLOBAL_MESSAGE:
				return WarningManager.getInstance().formatGlobalMessage((boolean) defaultValue, language);
			case MUTE_GLOBAL_MESSAGE:
				return MuteManager.getInstance().formatGlobalMessage((boolean) defaultValue, language);
			case BAN_SILENT_MESSAGE:
				return BanManager.getInstance().formatSilentMessage((boolean) defaultValue, language);
			case WARNING_SILENT_MESSAGE:
				return WarningManager.getInstance().formatSilentMessage((boolean) defaultValue, language);
			case KICK_SILENT_MESSAGE:
				return KickManager.getInstance().formatSilentMessage((boolean) defaultValue, language);
			case MUTE_SILENT_MESSAGE:
				return MuteManager.getInstance().formatSilentMessage((boolean) defaultValue, language);
			case IP_LOOKUP_ISP:
				return IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getISP();
			case IP_LOOKUP_CONTINENT:
				return IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getContinent();
			case IP_LOOKUP_COUNTRY:
				return IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getCountry();
			case IP_LOOKUP_CITY:
				return IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getCity();
			case IP_LOOKUP_SUBDIVISIONS:
				return IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).formatSubdivisions();
			case IP_LOOKUP_COUNTRY_CODE:
				return String.valueOf(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getCountryCode());
			case IP_LOOKUP_POSTAL_CODE:
				return String.valueOf(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getPostalCode());
			case IP_LOOKUP_LATITUDE:
				return String.valueOf(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getLatitude());
			case IP_LOOKUP_LONGITUDE:
				return String.valueOf(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getLongitude());
			case IP_LOOKUP_ACCURACY_RADIUS_KM:
				return String.valueOf(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getAccuracyRadius());
			case IP_LOOKUP_ACCURACY_RADIUS_MI:
				return String.valueOf(Utils.kilometersToMiles(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getAccuracyRadius()));
			case IP_LOOKUP_ACCURACY_RADIUS_NM:
				return String.valueOf(Utils.kilometersToNauticalMiles(IPLookupManager.getInstance().getFromCache(Utils.getInetAddress((String) defaultValue)).getAccuracyRadius()));
			case TIME_FORMAT:
				return Utils.formatTime(((Number) defaultValue).longValue(), language, (boolean) json.get("everInsteadOfNever"), (boolean) json.get("useZeroSecondsInstead"));
			case DATE_FORMAT:
				return ((Number) defaultValue).longValue() == -1L ? Utils.NOT_APPLICABLE : Utils.formatDate(((Number) defaultValue).longValue(), language, DateFormat.valueOf((String) json.get("format")));
			case MEMORY_FORMAT:
				return MemoryUtils.formatMemory(((Number) defaultValue).longValue(), MemoryUtils.valueOf((String) json.get("scale")));
			case MEMORY_DEFAULT_FORMAT:
				return MemoryUtils.formatMemory(((Number) defaultValue).longValue());
			case TRANSLATED_MESSAGE:
			case TRANSLATED_MESSAGE_ARGS:
				return language.getMessage((String) defaultValue, json.containsKey("args") ? (Object[]) json.get("args") : new Object[0]);
			}
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
		} return text;
	}
	
	public static void setInstance(ComponentTranslator instance) {
		ComponentTranslator.instance = instance;
	}
	
}
