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

package me.remigio07.chatplugin.api.server.f3servername;

import java.util.Map;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an F3 server name handled by the {@link F3ServerNameManager}.
 */
public class F3ServerName {
	
	private String id;
	private Map<Language, String> texts;
	
	/**
	 * Creates a new F3 server name. You have two ways to send it to a player:
	 * 	<ul>
	 * 		<li>using {@link F3ServerNameManager#sendF3ServerName(F3ServerName, ChatPluginServerPlayer)}</li>
	 * 		<li>by adding it to {@link F3ServerNameManager#getF3ServerNames()}; in this case it will be removed on {@link F3ServerNameManager#unload()}</li>
	 * 	</ul>
	 * 
	 * @param id F3 server name's ID
	 * @param texts F3 server name's texts
	 * @throws IllegalArgumentException If specified ID <code>!{@link F3ServerNameManager#isValidF3ServerNameID(String)}
	 * || texts.get({@link Language#getMainLanguage()}) == null</code>
	 */
	public F3ServerName(String id, @NotNull Map<Language, String> texts) {
		if (!F3ServerNameManager.getInstance().isValidF3ServerNameID(id))
			throw new IllegalArgumentException("F3 server name ID \"" + id + "\" is invalid as it does not respect the following pattern: \"" + F3ServerNameManager.F3_SERVER_NAME_ID_PATTERN.pattern() + "\"");
		if (texts.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.texts = texts;
	}
	
	/**
	 * Gets this F3 server name's ID.
	 * 
	 * @return F3 server name's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this F3 server name's texts.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return F3 server name's texts
	 */
	public Map<Language, String> getTexts() {
		return texts;
	}
	
	/**
	 * Gets this F3 server name's text for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s text if no text is present for the specified language.
	 * Will return <code>null</code> if {@link #getTexts()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
	 * 
	 * @param language Language used to translate the text
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return F3 server name's text
	 */
	@Nullable(why = "No text may be present for the specified language")
	public String getText(Language language, boolean avoidNull) {
		return texts.get(language) == null ? avoidNull ? texts.get(Language.getMainLanguage()) : null : texts.get(language);
	}
	
}
