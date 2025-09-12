/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.server.bossbar;

import java.util.Map;
import java.util.NoSuchElementException;

import me.remigio07.chatplugin.api.common.util.ValueContainer;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarColorAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarStyleAdapter;

/**
 * Represents a bossbar handled by the {@link BossbarManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Bossbars">ChatPlugin wiki/Modules/Bossbars</a>
 */
public class Bossbar {
	
	private String id;
	private Map<Language, String> titles;
	private ValueContainer<Double> value, maxValue;
	private BossbarColorAdapter color;
	private BossbarStyleAdapter style;
	private boolean hidden;
	
	/**
	 * Constructs a new bossbar.
	 * 
	 * <p>You have two ways to send it to a player:
	 * 	<ul>
	 * 		<li>using {@link BossbarManager#sendBossbar(Bossbar, ChatPluginServerPlayer)}</li>
	 * 		<li>by adding it to {@link BossbarManager#getBossbars()}; in this case it will be removed on {@link BossbarManager#unload()}</li>
	 * 	</ul>
	 * 
	 * @param id Bossbar's ID
	 * @param titles Bossbar's titles
	 * @param value Bossbar's value
	 * @param maxValue Bossbar's max value
	 * @param color Bossbar's color
	 * @param style Bossbar's style
	 * @param hidden Whether this bossbar is hidden
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link BossbarManager#isValidBossbarID(String)}
	 * @throws NoSuchElementException If <code>titles.get({@link Language#getMainLanguage()}) == null</code>
	 */
	public Bossbar(
			String id,
			@NotNull Map<Language, String> titles,
			@NotNull ValueContainer<Double> value,
			@NotNull ValueContainer<Double> maxValue,
			BossbarColorAdapter color,
			BossbarStyleAdapter style,
			boolean hidden
			) {
		if (!BossbarManager.getInstance().isValidBossbarID(id))
			throw new IllegalArgumentException("Bossbar ID \"" + id + "\" does not respect the following pattern: \"" + BossbarManager.BOSSBAR_ID_PATTERN.pattern() + "\"");
		if (titles.get(Language.getMainLanguage()) == null)
			throw new NoSuchElementException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.titles = titles;
		this.value = value;
		this.maxValue = maxValue;
		this.color = color;
		this.style = style;
		this.hidden = hidden;
	}
	
	@Override
	public String toString() {
		return "Bossbar{id=\"" + id + "\"}";
	}
	
	/**
	 * Gets this bossbar's ID.
	 * 
	 * @return Bossbar's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this bossbar's titles.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.</p>
	 * 
	 * @return Bossbar's titles
	 */
	public Map<Language, String> getTitles() {
		return titles;
	}
	
	/**
	 * Gets this bossbar's title for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s title if no title is present for the specified language.
	 * Will return <code>null</code> if {@link #getTitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the title
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Bossbar's title
	 */
	@Nullable(why = "No title may be present for the specified language")
	public String getTitle(Language language, boolean avoidNull) {
		return titles.get(language) == null ? avoidNull ? titles.get(Language.getMainLanguage()) : null : titles.get(language);
	}
	
	/**
	 * Gets this bossbar's value.
	 * 
	 * @return Bossbar's value
	 */
	public ValueContainer<Double> getValue() {
		return value;
	}
	
	/**
	 * Sets this bossbar's value.
	 * 
	 * @param value Bossbar's value
	 * @return This bossbar
	 */
	public Bossbar setValue(ValueContainer<Double> value) {
		this.value = value;
		return this;
	}
	
	/**
	 * Gets this bossbar's max value.
	 * 
	 * @return Bossbar's max value
	 */
	public ValueContainer<Double> getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Sets this bossbar's max value.
	 * 
	 * @param maxValue Bossbar's max value
	 * @return This bossbar
	 */
	public Bossbar setMaxValue(ValueContainer<Double> maxValue) {
		this.maxValue = maxValue;
		return this;
	}
	
	/**
	 * Gets this bossbar's color.
	 * 
	 * @return Bossbar's color
	 */
	public BossbarColorAdapter getColor() {
		return color;
	}
	
	/**
	 * Sets this bossbar's color.
	 * 
	 * @param color Bossbar's color
	 * @return This bossbar
	 */
	public Bossbar setColor(BossbarColorAdapter color) {
		this.color = color;
		return this;
	}
	
	/**
	 * Gets this bossbar's style.
	 * 
	 * @return Bossbar's style
	 */
	public BossbarStyleAdapter getStyle() {
		return style;
	}
	
	/**
	 * Sets this bossbar's style.
	 * 
	 * @param style Bossbar's style
	 * @return This bossbar
	 */
	public Bossbar setStyle(BossbarStyleAdapter style) {
		this.style = style;
		return this;
	}
	
	/**
	 * Checks if this bossbar is hidden.
	 * 
	 * @return Whether this bossbar is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Sets whether this bossbar should be hidden.
	 * 
	 * @param hidden Whether this bossbar should be hidden
	 * @return This bossbar
	 */
	public Bossbar setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}
	
}
