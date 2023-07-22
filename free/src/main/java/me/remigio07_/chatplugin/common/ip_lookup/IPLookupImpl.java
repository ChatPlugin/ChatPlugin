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

package me.remigio07_.chatplugin.common.ip_lookup;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookupMethod;
import me.remigio07_.chatplugin.api.common.ip_lookup.LocalIPLookupManager;
import me.remigio07_.chatplugin.api.common.player.PlayerManager;
import me.remigio07_.chatplugin.api.common.util.Utils;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

public class IPLookupImpl extends IPLookup { //ID: 513719; key: 5CY0bXritQc1YdQj
	
	public IPLookupImpl() {
		// disabled feature constructor
	}
	
	public IPLookupImpl(InetAddress ipAddress, boolean setValues) {
		this.ipAddress = ipAddress;
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
	public IPLookup setJSON(String jsonString) throws JsonException {
		json = jsonString;
		JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
		JsonObject location = (JsonObject) json.get("location");
		JsonObject country = (JsonObject) json.get("country");
		
		if (location.containsKey("latitude"))
			latitude = ((Number) location.get("latitude")).doubleValue();
		if (location.containsKey("longitude"))
			longitude = ((Number) location.get("longitude")).doubleValue();
		if (location.containsKey("accuracy_radius"))
			accuracyRadius = ((Number) location.get("accuracy_radius")).longValue();
		if (country.containsKey("names"))
			this.country = (String) ((JsonObject) country.get("names")).get("en");
		if (json.containsKey("continent") && ((JsonObject) json.get("continent")).containsKey("names"))
			continent = (String) ((JsonObject) ((JsonObject) json.get("continent")).get("names")).get("en");
		if (json.containsKey("traits") && ((JsonObject) json.get("traits")).containsKey("autonomous_system_organization"))
			isp = (String) ((JsonObject) json.get("traits")).get("autonomous_system_organization");
		if (json.containsKey("city") && ((JsonObject) json.get("city")).containsKey("names"))
			city = (String) ((JsonObject) ((JsonObject) json.get("city")).get("names")).get("en");
		if (country.containsKey("is_in_european_union") && (boolean) country.get("is_in_european_union"))
			insideEU = true;
		if (country.containsKey("iso_code"))
			countryCode = (String) country.get("iso_code");
		if (json.containsKey("postal") && ((JsonObject) json.get("postal")).containsKey("code"))
			postalCode = (String) ((JsonObject) json.get("postal")).get("code");
		if (json.containsKey("subdivisions")) {
			JsonArray subdivisions = (JsonArray) json.get("subdivisions");
			
			for (int i = 0; i < subdivisions.size(); i++)
				this.subdivisions.add((String) ((JsonObject) ((JsonObject) subdivisions.get(i)).get("names")).get("en"));
			Collections.reverse(this.subdivisions);
		} return this;
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
					.replace("{inside_eu}", String.valueOf(insideEU))
					.replace("{postal_code}", String.valueOf(postalCode))
					.replace("{latitude}", String.valueOf(Utils.truncate(latitude, 3)))
					.replace("{longitude}", String.valueOf(Utils.truncate(longitude, 3)))
					.replace("{accuracy_radius_km}", String.valueOf(accuracyRadius))
					.replace("{accuracy_radius_mi}", String.valueOf(Utils.kilometersToMiles(accuracyRadius)))
					.replace("{accuracy_radius_nm}", String.valueOf(Utils.kilometersToNauticalMiles(accuracyRadius)));
		}
	
	@Override
	public List<String> formatPlaceholders(List<String> input) {
		return input.stream().map(str -> formatPlaceholders(str)).collect(Collectors.toList());
	}
	
}
