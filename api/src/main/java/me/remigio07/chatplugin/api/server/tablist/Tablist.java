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

package me.remigio07.chatplugin.api.server.tablist;

import java.util.HashMap;
import java.util.Map;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.event.tablist.TablistSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a tablist handled by the {@link TablistManager}.
 */
public class Tablist {
	
	/**
	 * Tablist used to reset a player's header and footer.
	 * Some of its instance methods will return <code>null</code>.
	 * {@link TablistSendEvent} is not called when sending this tablist.
	 */
	public static final Tablist NULL_TABLIST = new Tablist();
	private String id;
	private Map<Language, String> headers;
	private Map<Language, String> footers;
	
	private Tablist() {
		headers = footers = new HashMap<>();
	}
	
	/**
	 * Constructs a new tablist. You have two ways to send it to a player:
	 * 	<ul>
	 * 		<li>using {@link TablistManager#sendTablist(Tablist, ChatPluginServerPlayer)}</li>
	 * 		<li>by adding it to {@link TablistManager#getTablists()}; in this case it will be removed on {@link TablistManager#unload()}</li>
	 * 	</ul>
	 * 
	 * @param id Tablist's ID
	 * @param headers Tablist's headers
	 * @param footers Tablist's footers
	 * @throws IllegalArgumentException If specified ID <code>!{@link TablistManager#isValidTablistID(String)} ||
	 * headers.get({@link Language#getMainLanguage()}) == null || footers.get({@link Language#getMainLanguage()}) == null</code>
	 */
	public Tablist(String id, @NotNull Map<Language, String> headers, @NotNull Map<Language, String> footers) {
		if (!TablistManager.getInstance().isValidTablistID(id))
			throw new IllegalArgumentException("Tablist ID \"" + id + "\" is invalid as it does not respect the following pattern: \"" + TablistManager.TABLIST_ID_PATTERN.pattern() + "\"");
		if (headers.get(Language.getMainLanguage()) == null || footers.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.headers = headers;
		this.footers = footers;
	}
	
	/**
	 * Gets this tablist's ID.
	 * 
	 * @return Tablist's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this tablist's headers.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return Tablist's headers
	 */
	public Map<Language, String> getHeaders() {
		return headers;
	}
	
	/**
	 * Gets this tablist's header for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s header if no header is present for the specified language.
	 * Will return <code>null</code> if {@link #getHeaders()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
	 * 
	 * @param language Language used to translate the text
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Tablist's header
	 */
	@Nullable(why = "No header may be present for the specified language")
	public String getHeader(Language language, boolean avoidNull) {
		return headers.get(language) == null ? avoidNull ? headers.get(Language.getMainLanguage()) : null : headers.get(language);
	}
	
	/**
	 * Gets this tablist's footers.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return Tablist's footers
	 */
	public Map<Language, String> getFooters() {
		return footers;
	}
	
	/**
	 * Gets this tablist's footer for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s footer if no footer is present for the specified language.
	 * Will return <code>null</code> if {@link #getFooters()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
	 * 
	 * @param language Language used to translate the text
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Tablist's footer
	 */
	@Nullable(why = "No footer may be present for the specified language")
	public String getFooter(Language language, boolean avoidNull) {
		return footers.get(language) == null ? avoidNull ? footers.get(Language.getMainLanguage()) : null : footers.get(language);
	}
	
}
