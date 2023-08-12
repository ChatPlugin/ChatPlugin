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

package me.remigio07.chatplugin.api.common.ip_lookup;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles {@link IPLookup} requests to the local database.
 * Only used when {@link IPLookupManager#getMethod()}<code> == </code>{@link IPLookupMethod#LOCAL}.
 */
public abstract class LocalIPLookupManager implements ChatPluginManager {
	
	protected static LocalIPLookupManager instance;
	protected boolean enabled;
	protected File cityDatabase, asnDatabase, lockFile;
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the MaxMind's city local database file.
	 * 
	 * @return City database file
	 */
	public File getCityDatabaseFile() {
		return cityDatabase;
	}
	
	/**
	 * Gets the MaxMind's ASN local database file.
	 * 
	 * @return ASN database file
	 */
	public File getASNDatabaseFile() {
		return asnDatabase;
	}
	
	/**
	 * Gets the .lock file associated with this server's databases.
	 * 
	 * @return Databases' .lock file
	 */
	public File getLockFile() {
		return lockFile;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static LocalIPLookupManager getInstance() {
		return instance;
	}
	
	/**
	 * Updates an existing lookup with the local database's information.
	 * 
	 * @param lookup Lookup to update
	 */
	public abstract void update(IPLookup lookup);
	
	/**
	 * Downloads an up to date copy of the database files from MaxMind's website.
	 * 
	 * @param type The database to download
	 * @throws IOException If something goes wrong
	 */
	public abstract void downloadDatabase(DatabaseType type) throws IOException;
	
	/**
	 * Formats the MaxMind website's database download URL with your license key.
	 * 
	 * @param type The database to download
	 * @return direct URL to the database file
	 */
	public abstract URL formatURL(DatabaseType type);
	
	/**
	 * Gets the MaxMind's city local database reader. This is an instance of <code>DatabaseReader</code>,
	 * but the method returns just an <code>Object</code> because you cannot directly access the libraries'
	 * classes. You can safely cast it to that class, but make sure the current class loader has loaded it.
	 * 
	 * @return The city database reader
	 */
	public abstract Object getCityDatabaseReader();
	
	/**
	 * Gets the MaxMind's ASN local database reader. This is an instance of <code>DatabaseReader</code>,
	 * but the method returns just an <code>Object</code> because you cannot directly access the libraries'
	 * classes. You can safely cast it to that class, but make sure the current class loader has loaded it.
	 * 
	 * @return The ASN database reader
	 */
	public abstract Object getASNDatabaseReader();
	
	/**
	 * Refreshes the specified database type's file. The following operations (in this order) are performed:
	 * 	<ol>
	 * 		<li>check if the file exists already</li>
	 * 		<li>if it does, delete it and create a new one</li>
	 * 		<li>download the .tar.gz database and extract it</li>
	 * 		<li>transfer its content to the file's location</li>
	 * 	</ol>
	 * 
	 * @param type Database's type
	 * @throws IOException If something goes wrong
	 */
	public abstract void refreshDatabaseFile(DatabaseType type) throws IOException;
	
	/**
	 * Represents the type of a local database. There are just two types:
	 * 	<ul>
	 * 		<li>{@link #ASN} - database containing data about an IP's ISP</li>
	 * 		<li>{@link #CITY} - database containing data about an IP's position</li>
	 * 	</ul>
	 */
	public enum DatabaseType {
		
		/**
		 * Database containing info about an IP's ISP.
		 */
		ASN("ASN", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-ASN&license_key={0}&suffix=tar.gz"),
		
		/**
		 * Database containing info about an IP's position.
		 */
		CITY("city", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key={0}&suffix=tar.gz");
		
		private String name, url;
		
		private DatabaseType(String name, String url) {
			this.name = name;
			this.url = url;
		}
		
		/**
		 * Gets this database type's name. Possible values:
		 * 	<ul>
		 * 		<li>"ASN" - database containing data about an IP's ISP</li>
		 * 		<li>"city" - database containing data about an IP's position</li>
		 * 	</ul>
		 * 
		 * @return Database type's name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the download URL of this database type. You have to replace "{0}" with your
		 * license key in the returned string to obtain the permission to download it.
		 * 
		 * @return Direct download URL
		 */
		public String getURL() {
			return url;
		}
		
	}
	
}
