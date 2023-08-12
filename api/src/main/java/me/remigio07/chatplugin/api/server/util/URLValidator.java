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

package me.remigio07.chatplugin.api.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.ChatManager;

/**
 * Util class used to validate URLs. Every method except
 * {@link #stripProtocol(String)} works only when {@link ChatManager#isEnabled()}.
 */
public class URLValidator {
	
	private static Pattern domainNamePattern = Pattern.compile("^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$");
	private static List<String> protocols = Arrays.asList("http://", "https://");
	
	/**
	 * Gets the list of the URLs contained in the input String.
	 * URLs' domain names are checked using {@link #getDomainName(String)}.
	 * You may modify the returned list.
	 * 
	 * @param input Input containing URLs
	 * @return URLs contained in given input
	 */
	public static List<String> getURLs(String input) {
		if (!input.contains("."))
			return Collections.emptyList();
		List<String> urls = new ArrayList<>();
		
		for (String word : input.split(" ")) {
			if (!word.contains("."))
				continue;
			if (getDomainName(word) != null)
				urls.add(word);
		} return urls;
	}
	
	/**
	 * Strips the protocol from the specified URL.
	 * Will return <code>url</code> if the specified URL is invalid
	 * or if it does not start with a protocol ("http://", "https://").
	 * 
	 * @param url URL to strip
	 * @return URL without its protocol
	 */
	@NotNull
	public static String stripProtocol(String url) {
		for (String protocol : protocols)
			if (url.toLowerCase().startsWith(protocol))
				url = url.substring(url.indexOf(':') + 3);
		return url;
	}
	
	/**
	 * Gets the specified URL's domain name.
	 * Will return <code>null</code> if the URL is invalid or if <code>!</code>{@link ChatManager#isEnabled()} or if
	 * {@link ChatManager#getRecognizedTLDs()} does not contain the URL's <a href="https://en.wikipedia.org/wiki/Top-level_domain">TLD</a>.
	 * 
	 * @param url Target url
	 * @return Specified URL's domain name
	 */
	@Nullable(why = "Null if the URL is invalid or if !ChatManager#isEnabled or if ChatManager#getRecognizedTLDs does not contain the URL's TLD")
	public static String getDomainName(String url) {
		if (!ChatManager.getInstance().isEnabled())
			return null;
		url = stripProtocol(url.toLowerCase());
		
		if (url.contains("/"))
			url = url.substring(0, url.indexOf('/'));
		return domainNamePattern.matcher(url).find() ? (ChatManager.getInstance().getRecognizedTLDs().contains(url.substring(url.lastIndexOf('.') + 1).toLowerCase()) ? url : null) : null;
	}
	
}
