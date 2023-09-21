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

package me.remigio07.chatplugin.api.server.language;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;

/**
 * Represents a language detector method. There are just two methods:
 * 	<ul>
 * 		<li>{@link #CLIENT_LOCALE} - performed by checking the client's language settings</li>
 * 		<li>{@link #GEOLOCALIZATION} - performed through {@link IPLookup}s; check {@link Language#getCountryCodes()}</li>
 * 	</ul>
 * 
 * @see LanguageDetector
 * @see LanguageManager
 */
public enum LanguageDetectorMethod {
	
	/**
	 * Detection performed by checking the client's language settings.
	 */
	CLIENT_LOCALE,
	
	/**
	 * Detection performed through {@link IPLookup}s.
	 * 
	 * @see Language#getCountryCodes()
	 */
	GEOLOCALIZATION;
	
}
