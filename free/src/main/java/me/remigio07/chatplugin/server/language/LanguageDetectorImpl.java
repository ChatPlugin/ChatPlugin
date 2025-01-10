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

package me.remigio07.chatplugin.server.language;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetectionMethod;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

public class LanguageDetectorImpl extends LanguageDetector {
	
	@Override
	public void load() {
		if (!ConfigurationType.CONFIG.get().getBoolean("languages.detector.enabled"))
			return;
		delay = ConfigurationType.CONFIG.get().getLong("languages.detector.delay-ms");
		
		try {
			method = LanguageDetectionMethod.valueOf(ConfigurationType.CONFIG.get().getString("languages.detector.method"));
			
			if (method == LanguageDetectionMethod.GEOLOCALIZATION) {
				if (!IPLookupManager.getInstance().isEnabled()) {
					LogManager.log("Language detector method GEOLOCALIZATION specified at \"languages.detector.method\" in config.yml requires the IP lookup module to be enabled. You can enable it at \"ip-lookup.enabled\" in config.yml. The module will be disabled; all new players' languages will be set to the default language, specified at \"languages.main-language-id\" in config.yml.", 2);
					unload();
					return;
				} if (!IPLookupManager.getInstance().isLoadOnJoin()) {
					LogManager.log("Language detector method GEOLOCALIZATION specified at \"languages.detector.method\" in config.yml requires the IP lookup module's option specified at \"ip-lookup.load-on-join\" in config.yml to be enabled. The module will be disabled; all new players' languages will be set to the default language, specified at \"languages.main-language-id\" in config.yml.", 2);
					unload();
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			LogManager.log(
					"Invalid language detector method (\"{0}\") set at \"languages.detector.method\" in config.yml: only CLIENT_LOCALE and GEOLOCALIZATION are allowed; setting to default value of CLIENT_LOCALE.",
					2,
					ConfigurationType.CONFIG.get().getString("languages.detector.method")
					);
			
			method = LanguageDetectionMethod.CLIENT_LOCALE;
		} enabled = true;
	}
	
	@Override
	public Language detectUsingClientLocale(ChatPluginServerPlayer player) {
		return detect(player.getLocale().getCountry());
	}
	
	@Override
	public Language detectUsingGeolocalization(IPLookup ipLookup) {
		return detect(ipLookup.getCountryCode());
	}
	
	private Language detect(String countryCode) {
		for (Language language : LanguageManager.getInstance().getLanguages())
			for (String languageCountryCode : language.getCountryCodes())
				if (languageCountryCode.equalsIgnoreCase(countryCode))
					return language;
		return Language.getMainLanguage();
	}
	
}
