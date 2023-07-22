/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.server.gui;

import java.util.HashMap;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.text.ChatColor;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Represents the actions performed on {@link GUI}s' openings.
 */
public class OpenActions {
	
	private Map<Language, String> messages;
	private SoundAdapter sound;
	
	/**
	 * Constructs a new instance of this class.
	 * 
	 * @param messages Open actions' messages
	 * @param sound Open actions' sound
	 * @throws IllegalArgumentException If <code>messages.get({@link Language#getMainLanguage()}) == null</code>
	 */
	public OpenActions(Map<Language, String> messages, SoundAdapter sound) {
		if (messages.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified open actions' map does not contain a translation for the main language");
		this.messages = messages;
		this.sound = sound;
	}
	
	/**
	 * Constructs a new instance of this class passing values found at
	 * 
	 * 	<ul>
	 * 		<li>"settings.open-actions.send-messages" - for {@link #getMessages()}</li>
	 * 		<li>"settings.open-actions.play-sound" - for {@link #getSound()}</li>
	 * 	</ul>
	 * 
	 * to {@link #OpenActions(Map, SoundAdapter)}.
	 * 
	 * @param configuration Configuration to read
	 * @throws IllegalArgumentException If translation for {@link Language#getMainLanguage()} is not present
	 * @see SoundAdapter#SoundAdapter(Configuration, String)
	 */
	public OpenActions(Configuration configuration) {
		this(
				LanguageManager.getInstance().getLanguages().stream().collect(HashMap::new, (map, language) -> map.put(language, configuration.getString("settings.open-actions.send-messages." + language.getID(), null)), HashMap::putAll),
				new SoundAdapter(configuration, "settings.open-actions.play-sound")
				);
	}
	
	/**
	 * Gets these open actions' messages.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return Open actions' messages
	 */
	public Map<Language, String> getMessages() {
		return messages;
	}
	
	/**
	 * Gets these open actions' message for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s message if no message is present for the specified language.
	 * Will return <code>null</code> if {@link #getMessages()}<code>.get(language) == null && !avoidNull</code>.
	 * 
	 * @param language Language used to translate the message
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Open actions' message
	 */
	@Nullable(why = "No message may be present for the specified language")
	public String getMessage(Language language, boolean avoidNull) {
		return messages.get(language) == null ? avoidNull ? messages.get(Language.getMainLanguage()) : null : messages.get(language);
	}
	
	/**
	 * Gets these open actions' sound.
	 * 
	 * @return Open actions' sound
	 */
	public SoundAdapter getSound() {
		return sound;
	}
	
	/**
	 * Performs these open actions.
	 * 
	 * @param player Target player
	 */
	public void perform(ChatPluginServerPlayer player) {
		player.playSound(sound);
		player.sendMessage(ChatColor.translate(getMessage(player.getLanguage(), true).replace("{pfx}", player.getLanguage().getMessage("misc.prefix"))));
	}
	
}
