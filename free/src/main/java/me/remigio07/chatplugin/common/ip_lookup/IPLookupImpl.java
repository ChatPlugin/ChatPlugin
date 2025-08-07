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

package me.remigio07.chatplugin.common.ip_lookup;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupMethod;
import me.remigio07.chatplugin.api.common.ip_lookup.LocalIPLookupManager;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.DateFormat;
import me.remigio07.chatplugin.api.server.util.Utils;

public class IPLookupImpl extends IPLookup {
	
	public IPLookupImpl(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public IPLookupImpl(InetAddress ipAddress, boolean setValues) {
		this(ipAddress);
		IPLookupManager manager = IPLookupManager.getInstance();
		
		TaskManager.runAsync(() -> {
			if (!PlayerManager.getInstance().getPlayersIPs().contains(ipAddress))
				manager.removeFromCache(ipAddress);
		}, manager.getCacheTime());
		
		manager.putInCache(ipAddress, this);
		
		if (setValues) {
			if (manager.getMethod() == IPLookupMethod.REMOTE)
				try {
					setJSON(manager.readURL(manager.getURL().replace("{0}", ipAddress.getHostAddress()))); 
				} catch (Exception e) {
					valid = false;
				}
			else LocalIPLookupManager.getInstance().update(this);
		}
	}
	
	@Override
	public String toString() {
		return "IPLookupImpl{ipAddress=" + ipAddress.getHostAddress() + "}";
	}
	
	@Override
	public IPLookup setJSON(String jsonString) throws Exception {
		json = jsonString;
		JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
		JsonObject location = (JsonObject) json.get("location");
		JsonObject country = (JsonObject) json.get("country");
		
		if (location != null) {
			if (location.containsKey("latitude"))
				latitude = ((Number) location.get("latitude")).doubleValue();
			if (location.containsKey("longitude"))
				longitude = ((Number) location.get("longitude")).doubleValue();
			if (location.containsKey("accuracy_radius"))
				accuracyRadius = ((Number) location.get("accuracy_radius")).longValue();
			if (location.containsKey("time_zone"))
				timeZone = (String) location.get("time_zone");
		} if (country != null) {
			if (country.containsKey("names"))
				this.country = (String) ((JsonObject) country.get("names")).get("en");
			if (country.containsKey("is_in_european_union") && (boolean) country.get("is_in_european_union"))
				insideEU = true;
			if (country.containsKey("iso_code"))
				countryCode = (String) country.get("iso_code");
		} if (json.containsKey("continent") && ((JsonObject) json.get("continent")).containsKey("names"))
			continent = (String) ((JsonObject) ((JsonObject) json.get("continent")).get("names")).get("en");
		if (json.containsKey("traits") && ((JsonObject) json.get("traits")).containsKey("autonomous_system_organization"))
			isp = (String) ((JsonObject) json.get("traits")).get("autonomous_system_organization");
		if (json.containsKey("city") && ((JsonObject) json.get("city")).containsKey("names"))
			city = (String) ((JsonObject) ((JsonObject) json.get("city")).get("names")).get("en");
		if (json.containsKey("postal") && ((JsonObject) json.get("postal")).containsKey("code"))
			postalCode = (String) ((JsonObject) json.get("postal")).get("code");
		if (json.containsKey("subdivisions"))
			Collections.reverse(subdivisions = ((JsonArray) json.get("subdivisions")).stream().map(subdivision -> (String) ((JsonObject) ((JsonObject) subdivision).get("names")).get("en")).collect(Collectors.toCollection(ArrayList::new)));
		return this;
	}
	
	@Override
	public String formatSubdivisions() {
		String str = String.join(", ", subdivisions);
		return str.isEmpty() ? "unknown location" : str;
	}
	
	@Override
	public String formatPlaceholders(String input) {
		return input == null ? null : input
				.replace("{ip_address}", ipAddress.getHostAddress())
				.replace("{isp}", isp)
				.replace("{continent}", continent)
				.replace("{country}", country)
				.replace("{subdivisions}", formatSubdivisions())
				.replace("{city}", city)
				.replace("{country_code}", countryCode)
				.replace("{inside_eu}", String.valueOf(insideEU))
				.replace("{time_zone}", timeZone)
				.replace("{postal_code}", postalCode)
				.replace("{latitude}", String.valueOf(Utils.truncate(latitude, 3)))
				.replace("{longitude}", String.valueOf(Utils.truncate(longitude, 3)))
				.replace("{accuracy_radius_km}", String.valueOf(accuracyRadius))
				.replace("{accuracy_radius_mi}", String.valueOf(Utils.kilometersToMiles(accuracyRadius)))
				.replace("{accuracy_radius_nm}", String.valueOf(Utils.kilometersToNauticalMiles(accuracyRadius)));
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		long now = System.currentTimeMillis();
		long date = tz.getID().equals(timeZone) ? tz.getOffset(now) - PlayerManager.getInstance().getDisplayedTimeZone().getOffset(now) + now : -1;
		return formatPlaceholders(input)
				.replace("{relative_date_full}", date == -1 ? "unknown relative date" : Utils.formatDate(date, language, DateFormat.FULL))
				.replace("{relative_date_day}", date == -1 ? "unknown relative date" : Utils.formatDate(date, language, DateFormat.DAY))
				.replace("{relative_date_hour}", date == -1 ? "unknown relative date" : Utils.formatDate(date, language, DateFormat.HOUR));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input) {
		return input.stream().map(this::formatPlaceholders).collect(Collectors.toList());
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
