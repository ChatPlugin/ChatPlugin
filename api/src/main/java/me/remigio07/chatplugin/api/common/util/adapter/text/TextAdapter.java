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

package me.remigio07.chatplugin.api.common.util.adapter.text;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) text adapter with hover and click event support.
 */
public class TextAdapter {
	
	/**
	 * Adapter containing an empty string. Do not call {@link #append(TextAdapter)}, {@link #setText(String)},
	 * {@link #onHover(String)} or {@link #onClick(ClickActionAdapter, String)} on this instance.
	 * 
	 * <p><strong>Content:</strong> ""</p>
	 */
	public static final TextAdapter EMPTY_TEXT = new TextAdapter("");
	
	/**
	 * Adapter containing a new line. Do not call {@link #append(TextAdapter)}, {@link #setText(String)},
	 * {@link #onHover(String)} or {@link #onClick(ClickActionAdapter, String)} on this instance.
	 * 
	 * <p><strong>Content:</strong> "\n"</p>
	 */
	public static final TextAdapter NEW_LINE = new TextAdapter("\n");
	private Object text;
	
	/**
	 * Constructs a text adapter with the specified input string.
	 * 
	 * @param text Input string
	 */
	@SuppressWarnings("deprecation")
	public TextAdapter(@NotNull String text) {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			this.text = new BukkitTextComponent(text);
			break;
		case BUNGEECORD:
			TextComponent textComponent = new TextComponent("\u00A7r");
			
			if (!text.isEmpty())
				for (BaseComponent component : TextComponent.fromLegacyText(text))
					textComponent.addExtra(component);
			this.text = textComponent;
			break;
		case SPONGE:
			this.text = TextSerializers.LEGACY_FORMATTING_CODE.deserialize(text);
			break;
		case VELOCITY:
			this.text = LegacyComponentSerializer.legacySection().deserialize(text);
			break;
		}
	}
	
	/**
	 * Constructs a copy of the specified text adapter.
	 * 
	 * @param text Input text
	 */
	public TextAdapter(@NotNull TextAdapter text) {
		this(Environment.isBukkit() ? text.bukkitValue() : Environment.isSponge() ? text.spongeValue() : Environment.isBungeeCord() ? text.bungeeCordValue() : text.velocityValue());
	}
	
	/**
	 * Constructs a text adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link me.remigio07.chatplugin.api.common.util.adapter.text.TextAdapter.BukkitTextComponent} for Bukkit environments</li>
	 * 		<li>{@link net.md_5.bungee.api.chat.TextComponent} for BungeeCord environments</li>
	 * 		<li>{@link org.spongepowered.api.text.Text} for Sponge environments</li>
	 * 		<li>{@link net.kyori.adventure.text.Component} for Velocity environments</li>
	 * 	</ul>
	 * 
	 * @param text Input text object
	 */
	public TextAdapter(Object text) {
		this.text = text;
	}
	
	/**
	 * Gets the text adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted text
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public BukkitTextComponent bukkitValue() {
		if (Environment.isBukkit())
			return (BukkitTextComponent) text;
		else throw new UnsupportedOperationException("Unable to adapt text to a Bukkit's BukkitTextComponent on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the text adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted text
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.text.Text spongeValue() {
		if (Environment.isSponge())
			return (Text) text;
		else throw new UnsupportedOperationException("Unable to adapt text to a Sponge's Text on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the text adapted for BungeeCord environments.
	 * 
	 * @return BungeeCord-adapted text
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBungeeCord()}
	 */
	public net.md_5.bungee.api.chat.TextComponent bungeeCordValue() {
		if (Environment.isBungeeCord())
			return (TextComponent) text;
		else throw new UnsupportedOperationException("Unable to adapt text to a BungeeCord's TextComponent on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the text adapted for Velocity environments.
	 * 
	 * @return Velocity-adapted text
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isVelocity()}
	 */
	public net.kyori.adventure.text.Component velocityValue() {
		if (Environment.isVelocity())
			return (Component) text;
		else throw new UnsupportedOperationException("Unable to adapt text to a Velocity's Component on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Converts this text to a plain string without hover and/or click events.
	 * 
	 * @return Plain text
	 */
	@SuppressWarnings("deprecation")
	public String toPlain() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return bukkitValue().toPlain();
		case BUNGEECORD:
			return TextComponent.toLegacyText(bungeeCordValue());
		case SPONGE:
			return TextSerializers.LEGACY_FORMATTING_CODE.serialize(spongeValue());
		case VELOCITY:
			return LegacyComponentSerializer.legacySection().serialize(velocityValue());
		} return null;
	}
	
	/**
	 * Append the specified text to this adapter.
	 * 
	 * @param text Text to append
	 * @return This adapter
	 */
	public TextAdapter append(TextAdapter text) {
		if (this != EMPTY_TEXT && this != NEW_LINE)
			switch (Environment.getCurrent()) {
			case BUKKIT:
				bukkitValue().append(text.bukkitValue());
				break;
			case BUNGEECORD:
				this.text = BungeeCordText.join(bungeeCordValue(), text.bungeeCordValue());
				break;
			case SPONGE:
				this.text = new TextAdapter(Text.join(spongeValue(), text.spongeValue())).spongeValue(); // XXX maybe it's not necessary to call a new constructor - check the docs
				break;
			case VELOCITY:
				this.text = velocityValue().append(text.velocityValue()); // XXX
				break;
			}
		return this;
	}
	
	/**
	 * Changes this adapter's displayed text.
	 * 
	 * @param text Text to display
	 * @return This adapter
	 */
	public TextAdapter setText(String text) {
		if (this != EMPTY_TEXT && this != NEW_LINE)
			switch (Environment.getCurrent()) {
			case BUKKIT:
				bukkitValue().setText(text);
				break;
			case BUNGEECORD:
				this.text = BungeeCordText.setText(bungeeCordValue(), text);
				break;
			case SPONGE:
				this.text = SpongeText.setText(spongeValue(), text);
				break;
			case VELOCITY:
				this.text = VelocityText.setText(velocityValue(), text);
				break;
			}
		return this;
	}
	
	/**
	 * Applies a hover event to this adapter.
	 * 
	 * @param hover Text to show
	 * @return This adapter
	 */
	@SuppressWarnings("deprecation")
	public TextAdapter onHover(String hover) {
		if (this != EMPTY_TEXT && this != NEW_LINE)
			switch (Environment.getCurrent()) {
			case BUKKIT:
				bukkitValue().onHover(hover);
				break;
			case BUNGEECORD:
				bungeeCordValue().setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { new TextAdapter(hover).bungeeCordValue() }));
				break;
			case SPONGE:
				text = SpongeText.setHoverEvent(spongeValue(), hover);
				break;
			case VELOCITY:
				text = VelocityText.setHoverEvent(velocityValue(), hover);
	//			velocityValue().hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(new TextWrapper(hover).velocityValue()));
				break;
			}
		return this;
	}
	
	/**
	 * Applies a click event to this adapter.
	 * 
	 * @param action Action to perform
	 * @param value Event's value
	 * @return This adapter
	 */
	public TextAdapter onClick(ClickActionAdapter action, String value) {
		if (this != EMPTY_TEXT && this != NEW_LINE)
			switch (Environment.getCurrent()) {
			case BUKKIT:
				bukkitValue().onClick(action, value);
				break;
			case BUNGEECORD:
				bungeeCordValue().setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(action.bungeeCordValue(), value));
				break;
			case SPONGE:
				text = SpongeText.setClickEvent(spongeValue(), action, value);
				break;
			case VELOCITY:
				velocityValue().clickEvent((net.kyori.adventure.text.event.ClickEvent) action.velocityValue(value));
				break;
			}
		return this;
	}
	
	/**
	 * Represents a text component for Bukkit environments.
	 */
	public static class BukkitTextComponent {
		
		private String text, hover, clickValue;
		private ClickActionAdapter clickAction;
		private List<BukkitTextComponent> extras = new ArrayList<>();
		
		/**
		 * Constructs a text component with the specified input string.
		 * 
		 * @param text Input string
		 */
		public BukkitTextComponent(String text) {
			this.text = text;
		}
		
		/**
		 * Converts this component to a plain string without hover and/or click events.
		 * 
		 * @return Plain text
		 */
		public String toPlain() {
			StringBuilder sb = new StringBuilder(text);
			
			for (BukkitTextComponent extra : extras)
				sb.append(extra.text);
			return ChatColor.translate(sb.toString());
		}
		
		/**
		 * Changes this component's displayed text.
		 * 
		 * @param text Text to display
		 * @return This component
		 */
		public BukkitTextComponent setText(String text) {
			this.text = text;
			return this;
		}
		
		/**
		 * Applies a hover event to this component.
		 * 
		 * @param hover Text to show
		 * @return This component
		 */
		public BukkitTextComponent onHover(String hover) {
			this.hover = hover;
			return this;
		}
		
		/**
		 * Applies a click event to this component.
		 * 
		 * @param action Action to perform
		 * @param value Event's value
		 * @return This component
		 */
		public BukkitTextComponent onClick(ClickActionAdapter action, String value) {
			clickAction = action;
			clickValue = value;
			return this;
		}
		
		/**
		 * Append the specified text to this component.
		 * 
		 * @param text Text to append
		 * @return This componnet
		 */
		public BukkitTextComponent append(BukkitTextComponent text) {
			extras.add(text);
			return this;
		}
		
		/**
		 * Gets the JSON String that represents this text.
		 * May be used with Vanilla's <code>/tellraw</code>.
		 * 
		 * @return JSON representation
		 */
		public String toJSON() {
			StringBuilder sb = new StringBuilder("[\"\"");
			
			extras.add(0, this);
			
			for (BukkitTextComponent extra : extras) {
				sb.append(
						",{\"text\":\""
						+ extra.text
						+ "\""
						+ (extra.hover == null ? "" : (",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + extra.hover + "\"}}"))
						+ (extra.clickAction == null ? "" : (",\"clickEvent\":{\"action\":\"" + extra.clickAction.getID() + "\",\"value\":\"" + extra.clickValue + "\"}"))
						+ "}"
						);
			} extras.remove(0);
			sb.append(']');
			return ChatColor.translate(sb.toString());
		}
		
	}
	
	private static class BungeeCordText {
		
		public static TextComponent join(TextComponent text1, TextComponent text2) {
			text1.addExtra(text2);
			return text1;
		}
		
		public static TextComponent setText(TextComponent text, String value) {
			TextComponent newText = new TextAdapter(value).bungeeCordValue();
			
			newText.setHoverEvent(text.getHoverEvent());
			newText.setClickEvent(text.getClickEvent());
			return newText;
		}
		
	}
	
	private static class SpongeText {
		
		public static Text setText(Text text, String value) {
			return new TextAdapter(value).spongeValue().toBuilder()
					.onHover(text.getHoverAction().orElse(null))
					.onClick(text.getClickAction().orElse(null))
					.build();
		}
		
		public static Text setHoverEvent(Text text, String value) {
			return text.toBuilder()
					.onHover(TextActions.showText(new TextAdapter(value).spongeValue()))
					.build();
		}
		
		public static Text setClickEvent(Text text, ClickActionAdapter action, String value) {
			return text.toBuilder()
					.onClick((org.spongepowered.api.text.action.ClickAction<?>) action.spongeValue(value))
					.build();
		}
		
	}
	
	private static class VelocityText {
		
		public static Component setText(Component component, String value) {
			return new TextAdapter(value).velocityValue()
					.hoverEvent(component.hoverEvent())
					.clickEvent(component.clickEvent());
		}
		
		public static Component setHoverEvent(Component component, String value) {
			return component.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(new TextAdapter(value).velocityValue()));
		}
		
	}
	
}
