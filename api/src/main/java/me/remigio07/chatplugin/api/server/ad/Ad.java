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
 */
public class Ad {
	
	private String id;
	private Map<Language, String> texts, hovers, clickValues;
	private ClickActionAdapter clickAction;
	private List<Rank> disabledRanks;
	
	/**
	 * Constructs a new ad. You have two ways to send it to a player:
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
			throw new IllegalArgumentException("Ad ID \"" + id + "\" is invalid as it does not respect the following pattern: \"" + AdManager.AD_ID_PATTERN.pattern() + "\"");
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
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return Ad's texts
	 */
	public Map<Language, String> getTexts() {
		return texts;
	}
	
	/**
	 * Gets this ad's text for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s text if no text is present for the specified language.
	 * Will return <code>null</code> if {@link #getTexts()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
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
	 * You may modify the returned map.
	 * 
	 * @return Ad's hovers
	 */
	public Map<Language, String> getHovers() {
		return hovers;
	}
	
	/**
	 * Gets this ad's hover for the specified language.
	 * Will return <code>null</code> if {@link #getHovers()}<code>.get(language) == null</code>.
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
	 * Will return <code>null</code> if the click action has not been specified.
	 * 
	 * @return Ad's click action
	 */
	@Nullable(why = "Click action may not have been specified")
	public ClickActionAdapter getClickAction() {
		return clickAction;
	}
	
	/**
	 * Sets this ad's click action.
	 * Specify <code>null</code> to not specify a click action.
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
	 * You may modify the returned map.
	 * 
	 * @return Ad's click values
	 */
	public Map<Language, String> getClickValues() {
		return clickValues;
	}
	
	/**
	 * Gets this ad's click value for the specified language.
	 * Will return <code>null</code> if {@link #getClickValues()}<code>.get(language) == null</code>.
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
	 * You may modify the returned list.
	 * 
	 * @return Ad's disabled ranks
	 */
	public List<Rank> getDisabledRanks() {
		return disabledRanks;
	}
	
	/**
	 * Gets the JSON String that represents this ad.
	 * May be used with Vanilla's <code>/tellraw</code>.
	 * 
	 * @param language Language used to translate the JSON
	 * @return JSON representation
	 */
	public String toJSON(Language language) {
		return
				"[\"\",{\"text\":\""
				+ (AdManager.getInstance().hasPrefix() ? AdManager.getInstance().getPrefix() : "")
				+ getText(language, true)
				+ "\""
				+ getHover(language) == null ? "" : (",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + getHover(language) + "\"}}")
				+ clickAction == null || getClickValue(language) == null ? "" : (",\"clickEvent\":{\"action\":\"" + clickAction.getID() + "\",\"value\":\"" + getClickValue(language) + "\"}")
				+ "}]";
	}
	
}
