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

package me.remigio07.chatplugin.api.server.join_quit;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bukkit.ServerLinks;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.join_quit.ServerLinkManager.ServerLink.Type;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.server.ServerLinks.Known;

/**
 * Manager that handles {@link ServerLink}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#server-links">ChatPlugin wiki/Modules/Join-quit/Server links</a>
 */
@GameFeature(
		name = "server link",
		minimumBukkitVersion = Version.V1_21,
		minimumSpongeVersion = Version.UNSUPPORTED,
		minimumFabricVersion = Version.V1_21
		)
public abstract class ServerLinkManager implements ChatPluginManager {
	
	protected static ServerLinkManager instance;
	protected boolean enabled;
	protected Set<PlaceholderType> placeholderTypes = Collections.emptySet();
	protected List<ServerLink> serverLinks = new ArrayList<>();
	protected long loadTime;
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.server-links.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the set of placeholder types used to
	 * translate {@link ServerLink#getDisplayNames()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.server-links.settings.placeholder-types" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Placeholders used to translate display names
	 */
	public Set<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of loaded server links.
	 * 
	 * <p>You may modify the returned list, but there should be up
	 * to 1 server link per non-{@link Type#CUSTOM CUSTOM} type.</p>
	 * 
	 * @return Loaded server links' list
	 */
	public List<ServerLink> getServerLinks() {
		return serverLinks;
	}
	
	/**
	 * Gets a server link from {@link #getServerLinks()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the server link is not loaded.</p>
	 * 
	 * @param id Server link's ID, case insensitive
	 * @return Loaded server link
	 */
	@Nullable(why = "Specified server link may not be loaded")
	public ServerLink getServerLink(String id) {
		return serverLinks.stream().filter(serverLink -> serverLink.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ServerLinkManager getInstance() {
		return instance;
	}
	
	/**
	 * Represents a server link handled by the {@link ServerLinkManager}.
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#server-links">ChatPlugin wiki/Modules/Join-quit/Server links</a>
	 */
	public static class ServerLink {
		
		private String id;
		private Type type;
		private URI uri;
		private Map<Language, String> displayNames;
		
		/**
		 * Constructs a new server link of the specified type.
		 * 
		 * @param id Server link's ID
		 * @param type Server link's type
		 * @param uri Server link's URI
		 * @throws IllegalArgumentException If <code>type == {@link Type#CUSTOM}</code>
		 * @see #ServerLink(String, URI, Map) Constructor for <code>CUSTOM</code> server links
		 */
		public ServerLink(String id, @NotNull Type type, URI uri) {
			if (type == Type.CUSTOM)
				throw new IllegalArgumentException("CUSTOM cannot be used as type with this constructor");
			this.id = id;
			this.type = type;
			this.uri = uri;
		}
		
		/**
		 * Constructs a new server link of type {@link Type#CUSTOM}.
		 * 
		 * @param id Server link's ID
		 * @param uri Server link's URI
		 * @param displayNames Server link's display names
		 * @throws NoSuchElementException If <code>displayNames.get({@link Language#getMainLanguage()}) == null</code>
		 * @see #ServerLink(String, Type, URI) Constructor for non-<code>CUSTOM</code> server links
		 */
		public ServerLink(String id, URI uri, @NotNull Map<Language, String> displayNames) {
			if (displayNames.get(Language.getMainLanguage()) == null)
				throw new NoSuchElementException("Specified map does not contain a translation for the main language");
			this.id = id;
			type = Type.CUSTOM;
			this.uri = uri;
			this.displayNames = displayNames;
		}
		
		/**
		 * Gets this server link's ID.
		 * 
		 * @return Server link's ID
		 */
		public String getID() {
			return id;
		}
		
		/**
		 * Gets this server link's type.
		 * 
		 * @return Server link's type
		 */
		@NotNull
		public Type getType() {
			return type;
		}
		
		/**
		 * Gets this server link's URI.
		 * 
		 * @return Server link's URI
		 */
		public URI getURI() {
			return uri;
		}
		
		/**
		 * Gets this server link's display names.
		 * 
		 * <p>Will return <code>null</code> if {@link #getType()}<code> != </code>{@link Type#CUSTOM}.</p>
		 * 
		 * @return Server link's display names
		 */
		@Nullable(why = "Null if getType() != CUSTOM")
		public Map<Language, String> getDisplayNames() {
			return displayNames;
		}
		
		/**
		 * Represents a {@link ServerLink}'s type.
		 * 
		 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#types">ChatPlugin wiki/Modules/Join-quit/Server links/Types</a>
		 */
		public static class Type extends PseudoEnum<Type> {
			
			/**
			 * Bug report link, also shown on crash screens.
			 * 
			 * <p><strong>Translation:</strong> "Report Server Bug"</p>
			 */
			public static final Type REPORT_BUG = new Type("REPORT_BUG");
			
			/**
			 * Community guidelines link.
			 * 
			 * <p><strong>Translation:</strong> "Community Guidelines"</p>
			 */
			public static final Type COMMUNITY_GUIDELINES = new Type("COMMUNITY_GUIDELINES");
			
			/**
			 * Support link.
			 * 
			 * <p><strong>Translation:</strong> "Support"</p>
			 */
			public static final Type SUPPORT = new Type("SUPPORT");
			
			/**
			 * Status link.
			 * 
			 * <p><strong>Translation:</strong> "Status"</p>
			 */
			public static final Type STATUS = new Type("STATUS");
			
			/**
			 * Feedback link.
			 * 
			 * <p><strong>Translation:</strong> "Feedback"</p>
			 */
			public static final Type FEEDBACK = new Type("FEEDBACK");
			
			/**
			 * Community link.
			 * 
			 * <p><strong>Translation:</strong> "Community"</p>
			 */
			public static final Type COMMUNITY = new Type("COMMUNITY");
			
			/**
			 * Website link.
			 * 
			 * <p><strong>Translation:</strong> "Website"</p>
			 */
			public static final Type WEBSITE = new Type("WEBSITE");
			
			/**
			 * Forums link.
			 * 
			 * <p><strong>Translation:</strong> "Forums"</p>
			 */
			public static final Type FORUMS = new Type("FORUMS");
			
			/**
			 * News link.
			 * 
			 * <p><strong>Translation:</strong> "News"</p>
			 */
			public static final Type NEWS = new Type("NEWS");
			
			/**
			 * Announcements link.
			 * 
			 * <p><strong>Translation:</strong> "Announcements"</p>
			 */
			public static final Type ANNOUNCEMENTS = new Type("ANNOUNCEMENTS");
			
			/**
			 * Custom link.
			 * 
			 * <p>This type of link does not provide a fixed translation but lets you
			 * specify {@linkplain ServerLink#getDisplayNames() custom display names}.
			 * An unlimited amount of custom server links can be specified.</p>
			 */
			public static final Type CUSTOM = new Type("CUSTOM");
			private static final Type[] VALUES = { REPORT_BUG, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK, COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS, CUSTOM };
			private static int ordinal = 0;
			
			private Type(String name) {
				super(name, ordinal++);
			}
			
			/**
			 * Gets the server link type adapted for Bukkit environments.
			 * 
			 * <p>Will return <code>null</code> if <code>this == </code>{@link #CUSTOM}.</p>
			 * 
			 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_21 1.21}</p>
			 * 
			 * @return Bukkit-adapted server link type
			 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
			 */
			@Nullable(why = "Null if this == CUSTOM")
			public ServerLinks.Type bukkitValue() {
				if (Environment.isBukkit())
					return this == CUSTOM ? null : ServerLinks.Type.valueOf(name);
				throw new UnsupportedOperationException("Unable to adapt server link type to a Bukkit's ServerLinks.Type on a " + Environment.getCurrent().getName() + " environment");
			}
			
			/**
			 * Gets the server link type adapted for Fabric environments.
			 * 
			 * <p>Will return <code>null</code> if <code>this == </code>{@link #CUSTOM}.</p>
			 * 
			 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_21 1.21}</p>
			 * 
			 * @return Fabric-adapted server link type
			 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
			 */
			@Nullable(why = "Null if this == CUSTOM")
			public Known fabricValue() {
				if (Environment.isFabric())
					return this == CUSTOM ? null : this == REPORT_BUG ? Known.BUG_REPORT : Known.valueOf(name);
				throw new UnsupportedOperationException("Unable to adapt server link type to a Fabric's ServerLinks.Known on a " + Environment.getCurrent().getName() + " environment");
			}
			
			/**
			 * Equivalent of <code>valueOf(String)</code>.
			 * 
			 * @param name Constant's name
			 * @return Pseudo-enum's constant
			 * @throws NullPointerException If <code>name == null</code>
			 * @throws IllegalArgumentException If {@link #values()}
			 * does not contain a constant with the specified name
			 */
			public static Type valueOf(String name) {
				return valueOf(name, VALUES);
			}
			
			/**
			 * Equivalent of <code>values()</code>.
			 * 
			 * @return Pseudo-enum's constants
			 */
			public static Type[] values() {
				return VALUES;
			}
			
			/**
			 * Equivalent of {@link #valueOf(String)}, but:
			 * 	<ul>
			 * 		<li>case insensitive</li>
			 * 		<li>returns <code>null</code> instead of throwing {@link IllegalArgumentException}</li>
			 * 		<li>also recognizes Bukkit- and Fabric-compatible IDs</li>
			 * 	</ul>
			 * 
			 * <p>Will return <code>null</code> if the specified name is invalid.</p>
			 * 
			 * @param name Constant's name, case insensitive
			 * @return Pseudo-enum's constant
			 * @throws NullPointerException If <code>name == null</code>
			 */
			@Nullable(why = "Specified name may be invalid")
			public static Type value(String name) {
				if (name.equalsIgnoreCase("BUG_REPORT"))
					return REPORT_BUG;
				try {
					return valueOf(name.toUpperCase());
				} catch (IllegalArgumentException iae) {
					return null;
				}
			}
			
		}
		
	}
	
}
