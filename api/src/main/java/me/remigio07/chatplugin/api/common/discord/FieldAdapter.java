/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.common.discord;

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a field.
 * 
 * <p>This is an adapter of <code>net.dv8tion.jda.api.entities.MessageEmbed.Field</code>.</p>
 * 
 * @see FieldAdapter.Parser
 */
public class FieldAdapter {
	
	private String title, text;
	private boolean inline;
	
	/**
	 * Creates a field wrapper.
	 * 
	 * @param title Field's title
	 * @param text Field's text
	 * @param inline Whether this field is inline
	 */
	public FieldAdapter(@NotNull String title, @NotNull String text, boolean inline) {
		this.title = title;
		this.text = text;
		this.inline = inline;
	}
	
	/**
	 * Gets this field's title.
	 * 
	 * <p>Will appear in bold above {@link #getText()}.</p>
	 * 
	 * @return Field's title
	 */
	@NotNull
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets this field's title.
	 * 
	 * <p>Will appear in bold above {@link #getText()}.</p>
	 * 
	 * @param title Field's title
	 */
	public void setTitle(@NotNull String title) {
		this.title = title;
	}
	
	/**
	 * Gets this field's text.
	 * 
	 * @return Field's text
	 */
	@NotNull
	public String getText() {
		return text;
	}
	
	/**
	 * Sets this field's text.
	 * 
	 * @param text Field's text
	 */
	public void setText(@NotNull String text) {
		this.text = text;
	}
	
	/**
	 * Checks if this field is inline.
	 * 
	 * <p>You can set up to 3 fields per line.</p>
	 * 
	 * @return Whether this field is inline
	 */
	public boolean isInline() {
		return inline;
	}
	
	/**
	 * Sets whether this field is inline.
	 * 
	 * <p>You can set up to 3 fields per line.</p>
	 * 
	 * @param inline Whether this field is inline
	 */
	public void setInline(boolean inline) {
		this.inline = inline;
	}
	
	/**
	 * Parser used to read {@link FieldAdapter}s from YAML configurations and JSON strings.
	 * 
	 * <p>It can also be used to obtain instances of
	 * <code>net.dv8tion.jda.api.entities.MessageEmbed.Field</code>.</p>
	 */
	public abstract static class Parser {
		
		protected static Parser instance;
		
		/**
		 * This method returns a list containing converted values of the specified adapters
		 * just by iterating over them and calling {@link #jdaValue(FieldAdapter)} every time.
		 * 
		 * @param fields Fields to convert
		 * @return Equivalent JDA's fields
		 */
		public List<Object> jdaValue(List<FieldAdapter> fields) {
			return fields.stream().map(field -> jdaValue(field)).collect(Collectors.toList());
		}
		
		/**
		 * Gets this parser's instance.
		 * 
		 * @return Parser's instance
		 */
		public static Parser getInstance() {
			return instance;
		}
		
		/**
		 * Parses the list of fields contained in the specified YAML configuration at a certain path.
		 * 
		 * <p>Example of valid configuration section:
		 * 
		 * <pre>
		 *messages:
		 *  main:
		 *    info:
		 *      title:
		 *        text: 'Embed''s title''s text'
		 *        url: 'https://example.com'
		 *      description: 'Short description under the title'
		 *      fields:
		 *        '0': # field's ID
		 *          title: 'Random title'
		 *          text: 'Field''s content'
		 *          inline: true
		 *        '1':
		 *          title: 'Second field'
		 *          text: 'Both fields are aligned'
		 *          inline: true
		 * </pre>
		 * 
		 * In this case, <code>path</code> is "messages.main.info".
		 * 
		 * @param yaml Configuration to parse
		 * @param path Path to the fields section
		 * @return Fields contained in the section
		 */
		public abstract List<FieldAdapter> fromYAML(Configuration yaml, String path);
		
		/**
		 * Parses the list of fields contained in the specified JSON string.
		 * 
		 * <p>Example of valid string:
		 * 
		 * <pre>
		 *{
		 *  "title": {
		 *    "text": "Embed's title's text",
		 *    "url": "https://example.com"
		 *  },
		 *  "description": "Short description under the title",
		 *  "fields": {
		 *    "0": {
		 *      "title": "Random title",
		 *      "text": "Field's content",
		 *      "inline": true
		 *    },
		 *    "1": {
		 *      "title": "Second field",
		 *      "text": "Both fields are aligned",
		 *      "inline": true
		 *    }
		 *  }
		 *}
		 * </pre>
		 * 
		 * @param json String to parse
		 * @return Fields contained in the string
		 * @throws Exception If the string's format is invalid
		 */
		public abstract List<FieldAdapter> fromJSON(String json) throws Exception;
		
		/**
		 * Gets the equivalent JDA's <code>net.dv8tion.jda.api.entities.MessageEmbed.Field</code>.
		 * 
		 * <p>This method returns an {@link Object} because libraries' classes cannot be
		 * accessed directly from the API; just ignore that and consider it a field.</p>
		 * 
		 * @param field Field to convert
		 * @return Equivalent JDA's field
		 * @see #jdaValue(List)
		 */
		public abstract Object jdaValue(FieldAdapter field);
		
	}
	
}
