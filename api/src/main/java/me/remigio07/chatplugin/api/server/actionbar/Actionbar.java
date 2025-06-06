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

package me.remigio07.chatplugin.api.server.actionbar;

import java.util.Map;
import java.util.NoSuchElementException;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an actionbar handled by the {@link ActionbarManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Actionbars">ChatPlugin wiki/Modules/Actionbars</a>
 */
public class Actionbar {
	
	private String id;
	private Map<Language, String> texts;
	private boolean hidden;
	
	/**
	 * Constructs a new actionbar.
	 * 
	 * <p>You have two ways to send it to a player:
	 * 	<ul>
	 * 		<li>using {@link ActionbarManager#sendActionbar(Actionbar, ChatPluginServerPlayer)}</li>
	 * 		<li>by adding it to {@link ActionbarManager#getActionbars()}; in this case it will be removed on {@link ActionbarManager#unload()}</li>
	 * 	</ul>
	 * 
	 * @param id Actionbar's ID
	 * @param texts Actionbar's texts
	 * @param hidden Whether this actionbar is hidden
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link ActionbarManager#isValidActionbarID(String)}
	 * @throws NoSuchElementException If <code>texts.get({@link Language#getMainLanguage()}) == null</code>
	 */
	public Actionbar(String id, @NotNull Map<Language, String> texts, boolean hidden) {
		if (!ActionbarManager.getInstance().isValidActionbarID(id))
			throw new IllegalArgumentException("Actionbar ID \"" + id + "\" does not respect the following pattern: \"" + ActionbarManager.ACTIONBAR_ID_PATTERN.pattern() + "\"");
		if (texts.get(Language.getMainLanguage()) == null)
			throw new NoSuchElementException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.texts = texts;
		this.hidden = hidden;
	}
	
	/**
	 * Gets this actionbar's ID.
	 * 
	 * @return Actionbar's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this actionbar's texts.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.</p>
	 * 
	 * @return Actionbar's texts
	 */
	public Map<Language, String> getTexts() {
		return texts;
	}
	
	/**
	 * Gets this actionbar's text for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s text if no text is present for the specified language.
	 * Will return <code>null</code> if {@link #getTexts()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the text
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Actionbar's text
	 */
	@Nullable(why = "No text may be present for the specified language")
	public String getText(Language language, boolean avoidNull) {
		return texts.get(language) == null ? avoidNull ? texts.get(Language.getMainLanguage()) : null : texts.get(language);
	}
	
	/**
	 * Checks if this actionbar is hidden.
	 * 
	 * @return Whether this actionbar is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Sets whether this actionbar should be hidden.
	 * 
	 * @param hidden Whether this actionbar should be hidden
	 * @return This actionbar
	 */
	public Actionbar setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}
	
}
