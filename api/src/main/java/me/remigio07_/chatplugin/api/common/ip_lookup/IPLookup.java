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

package me.remigio07_.chatplugin.api.common.ip_lookup;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.remigio07_.chatplugin.api.common.util.Utils;

/**
 * Represents an IP lookup handled by the {@link IPLookupManager}. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/IP-lookup">ChatPlugin wiki/IP lookup</a>
 */
public abstract class IPLookup {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with an IP lookup's information. See wiki for more info:
	 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/IP-lookup#placeholders">ChatPlugin wiki/IP lookup/Placeholders</a>
	 * 
	 * <p><strong>Content:</strong> ["ip_address", "isp", "continent", "country", "subdivisions", "city", "postal_code", "latitude", "longitude", "accuracy_radius_km", "accuracy_radius_mi", "accuracy_radius_nm"]</p>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "ip_address", "isp", "continent", "country", "subdivisions", "city", "postal_code", "latitude", "longitude", "accuracy_radius_km", "accuracy_radius_mi", "accuracy_radius_nm" };
	protected IPLookupMethod method = IPLookupMethod.REMOTE;
	protected InetAddress ipAddress = Utils.getInetAddress("127.0.0.1");
	protected String json = "{}";
	protected String isp = "unknown ISP";
	protected String continent = "unknown continent";
	protected String country = "unknown country";
	protected String city = "unknown city";
	protected String countryCode = "unknown country code";
	protected String postalCode = "unknown postal code";
	protected List<String> subdivisions = new ArrayList<>();
	protected boolean insideEU, valid = true;
	protected double latitude = -1, longitude = -1;
	protected long accuracyRadius = -1;
	
	/**
	 * Gets the method used to obtain this lookup.
	 * 
	 * @return Method used
	 */
	public IPLookupMethod getMethod() {
		return method;
	}
	
	/**
	 * Sets the method used to obtain this lookup.
	 * 
	 * @param method Method used
	 */
	public void setMethod(IPLookupMethod method) {
		this.method = method;
	}
	
	/**
	 * Checks if this lookup is valid.
	 * 
	 * @return Whether this lookup is valid
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Chooses if this lookup is valid.
	 * 
	 * @param valid Whether this lookup is valid
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
		
		if (!valid) {
			isp = "unknown ISP";
			continent = "unknown continent";
			country = "unknown country";
			city = "unknown city";
			countryCode = "unknown country code";
			postalCode = "unknown postal code";
			subdivisions = Collections.emptyList();
			insideEU = false;
			latitude = longitude = accuracyRadius = -1;
		}
	}
	
	/**
	 * Gets this lookup's IP address.
	 * 
	 * @return Lookup's IP address
	 */
	public InetAddress getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * Gets this lookup's Internet Service Provider.
	 * 
	 * @return Lookup's ISP
	 */
	public String getISP() {
		return isp;
	}
	
	/**
	 * Sets this lookup's Internet Service Provider.
	 * 
	 * @param isp Lookup's ISP
	 */
	public void setISP(String isp) {
		this.isp = isp;
	}
	
	/**
	 * Gets this lookup's position's continent.
	 * 
	 * @return Lookup's continent
	 */
	public String getContinent() {
		return continent;
	}
	
	/**
	 * Sets this lookup's position's continent.
	 * 
	 * @param continent Lookup's continent
	 */
	public void setContinent(String continent) {
		this.continent = continent;
	}
	
	/**
	 * Gets this lookup's position's country.
	 * 
	 * @return Lookup's country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets this lookup's position's country.
	 * 
	 * @param country Lookup's country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Gets this lookup's position's city.
	 * 
	 * @return Lookup's city
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Sets this lookup's position's city.
	 * 
	 * @param city Lookup's city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Gets this lookup's position's subdivisions. A subdivision is a more detailed
	 * geographical division of a territory. For example, Italian regions and provinces.
	 * 
	 * @return Lookup's subdivisions
	 */
	public List<String> getSubdivisions() {
		return subdivisions;
	}
	
	/**
	 * Sets this lookup's position's subdivisions. A subdivision is a more detailed
	 * geographical division of a territory. For example, Italian regions and provinces.
	 * 
	 * @param subdivisions Lookup's subdivisions
	 */
	public void setSubdivisions(List<String> subdivisions) {
		this.subdivisions = subdivisions;
	}
	
	/**
	 * Formats this lookup's position's subdivisions as a single string.
	 * 
	 * <p><strong>Example:</strong> Provincia di Parma, Emilia-Romagna</p>
	 * 
	 * @return Subdivisions merged into one string
	 */
	public String formatSubdivisions() {
		String str = String.join(", ", subdivisions.toArray(new String[0]));
		return str.isEmpty() ? "unknown location" : str;
	}
	
	/**
	 * Checks if this lookup's position is inside the European Union.
	 * This does not always work (returns <code>false</code>). MaxMind's issues, sorry.
	 * 
	 * @return Whether the IP is inside EU
	 */
	public boolean isInsideEU() {
		return insideEU;
	}
	
	/**
	 * Sets if this lookup's position is inside the European Union.
	 * 
	 * @param insideEU Whether the IP is inside EU
	 */
	public void setInsideEU(boolean insideEU) {
		this.insideEU = insideEU;
	}
	
	/**
	 * Gets this lookup's position's <a href="https://en.wikipedia.org/wiki/ISO_3166-2#Current_codes">ISO 3166-2</a> country code.
	 * 
	 * @return Lookup's country code
	 */
	public String getCountryCode() {
		return countryCode;
	}
	
	/**
	 * Sets this lookup's <a href="https://en.wikipedia.org/wiki/ISO_3166-2#Current_codes">ISO 3166-2</a> country code.
	 * 
	 * @param countryCode Lookup's country code
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	/**
	 * Gets this lookup's position's postal code.
	 * Note that not all countries use postal codes.
	 * 
	 * @return Lookup's postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}
	
	/**
	 * Sets this lookup's position's postal code.
	 * 
	 * @param postalCode Lookup's postal code
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	/**
	 * Gets this lookup's position's latitude.
	 * 
	 * @return Lookup's latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Sets this lookup's position's latitude.
	 * 
	 * @param latitude Lookup's latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets this lookup's position's longitude.
	 * 
	 * @return Lookup's longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Sets this lookup's position's longitude.
	 * 
	 * @param longitude Lookup's longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Gets this lookup's position's accuracy radius, in kilometers.
	 * You can use {@link Utils#kilometersToMiles(long)} and
	 * {@link Utils#kilometersToNauticalMiles(long)} to convert values.
	 * 
	 * @return Lookup's accuracy radius
	 */
	public long getAccuracyRadius() {
		return accuracyRadius;
	}
	
	/**
	 * Sets this lookup's position's accuracy radius, in kilometers.
	 * 
	 * @param accuracyRadius Lookup's accuracy radius
	 */
	public void setAccuracyRadius(long accuracyRadius) {
		this.accuracyRadius = accuracyRadius;
	}
	
	/**
	 * Gets the JSON String that represents this IP lookup.
	 * 
	 * @return JSON representation
	 */
	public String toJSON() {
		return json;
	}
	
	/**
	 * Sets the JSON string that represents this IP lookup and updates it with new values.
	 * 
	 * @param json JSON formatted string
	 * @return Resulting IP lookup
	 * @throws Exception If something goes wrong
	 */
	public abstract IPLookup setJSON(String json) throws Exception;
	
	/**
	 * Translates an input string with this IP lookup's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input containing placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input);
	
	/**
	 * Translates an input string list with this IP lookup's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input string list
	 * @return Formatted string list
	 */
	public abstract List<String> formatPlaceholders(List<String> input);
	
}
