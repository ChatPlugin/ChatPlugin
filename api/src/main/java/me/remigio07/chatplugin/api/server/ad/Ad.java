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

package me.remigio07.chatplugin.api.server.ad;

import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;

/**
 * Represents an ad handled by the {@link AdManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ads">ChatPlugin wiki/Modules/Ads</a>
 */
public class Ad {
	
	private String id;
	private Map<Language, String> texts, hovers, clickValues;
	private ClickActionAdapter clickAction;
	private List<Rank> disabledRanks;
	
	/**
	 * Constructs a new ad.
	 * 
	 * <p>You have two ways to send it to a player:
	 * 	<ul>
	 * 		<li>using {@link AdManager#sendAd(Ad, ChatPluginServerPlayer)}</li>
	 * 		<li>by adding it to {@link AdManager#getAds()}; in this case it will be removed on {@link AdManager#unload()}</li>
	 * 	</ul>
	 * 
	 * @param id Ad's ID
	 * @param texts Ad's texts
	 * @param hovers Ad's hovers
	 * @param clickAction Ad's click action
	 * @param clickValues Ad's click values
	 * @param disabledRanks Ranks that will not receive the ad
	 * @throws IllegalArgumentException If specified ID <code>!{@link AdManager#isValidAdID(String)} ||
	 * texts.get({@link Language#getMainLanguage()}) == null</code> (other two maps may contain <code>null</code>)
	 */
	public Ad(
			String id,
			@NotNull Map<Language, String> texts,
			@NotNull Map<Language, String> hovers,
			@Nullable(why = "Click action may not be specified") ClickActionAdapter clickAction,
			@NotNull Map<Language, String> clickValues,
			List<Rank> disabledRanks
			) {
		if (!AdManager.getInstance().isValidAdID(id))
			throw new IllegalArgumentException("Ad ID \"" + id + "\" does not respect the following pattern: \"" + AdManager.AD_ID_PATTERN.pattern() + "\"");
		if (texts.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.texts = texts;
		this.hovers = hovers;
		this.clickAction = clickAction;
		this.clickValues = clickValues;
		this.disabledRanks = disabledRanks;
	}
	
	/**
	 * Gets this ad's ID.
	 * 
	 * @return Ad's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this ad's texts.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.</p>
	 * 
	 * @return Ad's texts
	 */
	public Map<Language, String> getTexts() {
		return texts;
	}
	
	/**
	 * Gets this ad's text for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s text if no text is present for the specified language.
	 * Will return <code>null</code> if {@link #getTexts()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the text
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Ad's text
	 */
	@Nullable(why = "No text may be present for the specified language")
	public String getText(Language language, boolean avoidNull) {
		return texts.get(language) == null ? avoidNull ? texts.get(Language.getMainLanguage()) : null : texts.get(language);
	}
	
	/**
	 * Gets this ad's hovers.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Ad's hovers
	 */
	public Map<Language, String> getHovers() {
		return hovers;
	}
	
	/**
	 * Gets this ad's hover for the specified language.
	 * 
	 * <p>Will return <code>null</code> if {@link #getHovers()}<code>.get(language) == null</code>.</p>
	 * 
	 * @param language Language used to translate the hover
	 * @return Ad's hover
	 */
	@Nullable(why = "No hover may be present for the specified language")
	public String getHover(Language language) {
		return hovers.get(language);
	}
	
	/**
	 * Gets this ad's click action.
	 * 
	 * <p>Will return <code>null</code> if the click action has not been specified.</p>
	 * 
	 * @return Ad's click action
	 */
	@Nullable(why = "Click action may not have been specified")
	public ClickActionAdapter getClickAction() {
		return clickAction;
	}
	
	/**
	 * Sets this ad's click action.
	 * 
	 * <p>Specify <code>null</code> to not specify a click action.</p>
	 * 
	 * @param clickAction Ad's click action
	 * @return This ad
	 */
	public Ad setClickAction(@Nullable(why = "Click action may not be specified") ClickActionAdapter clickAction) {
		this.clickAction = clickAction;
		return this;
	}
	
	/**
	 * Gets this ad's click values.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Ad's click values
	 */
	public Map<Language, String> getClickValues() {
		return clickValues;
	}
	
	/**
	 * Gets this ad's click value for the specified language.
	 * 
	 * <p>Will return <code>null</code> if {@link #getClickValues()}<code>.get(language) == null</code>.</p>
	 * 
	 * @param language Language used to translate the click value
	 * @return Ad's click value
	 */
	@Nullable(why = "No click value may be present for the specified language")
	public String getClickValue(Language language) {
		return clickValues.get(language);
	}
	
	/**
	 * Gets the list of ranks that will not receive this ad.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Ad's disabled ranks
	 */
	public List<Rank> getDisabledRanks() {
		return disabledRanks;
	}
	
}
