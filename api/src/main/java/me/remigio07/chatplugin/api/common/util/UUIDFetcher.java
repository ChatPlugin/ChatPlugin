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

package me.remigio07.chatplugin.api.common.util;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.PlayerManager;

/**
 * Util class used to calculate and fetch
 * Java and Bedrock players' UUIDs and names.
 */
public abstract class UUIDFetcher {
	
	/**
	 * Pattern representing the allowed
	 * <a href="https://learn.microsoft.com/en-us/gaming/gdk/_content/gc/live/features/identity/user-profile/gamertags/live-classic-gamertags-overview">classic gamertags</a>.
	 * 
	 * <p><strong>Note:</strong> Microsoft's classic gamertags' documentation is not exhaustive at all:
	 * 	<ul>
	 * 		<li>commas (',') should be allowed in gamertags, however, out of ~40M accounts tracked by GeyserMC, none of these contains any; this pattern does not allow commas</li>
	 * 		<li>it is unclear whether space-only and number-only gamertags are allowed; this pattern does not perform that kind of validation</li>
	 * 		<li>it is unclear where and how many consecutive spaces are allowed; this pattern does not perform that kind of validation</li>
	 * 	</ul>
	 * 
	 * <p><strong>Regex:</strong> <a href="https://regex101.com/r/fR6ysX/1"><code>^[a-zA-Z0-9 ]{1,15}$</code></a></p>
	 * 
	 * @see #isValidClassicGamertag(String)
	 */
	public static final Pattern CLASSIC_GAMERTAG_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]{1,15}$");
	protected static UUIDFetcher instance;
	
	/**
	 * Gets a Java or Bedrock player's name based on the input:
	 * 	<ul>
	 * 		<li>{@link UUID}v0 for a Bedrock player</li>
	 * 		<li>{@link UUID}v4 for a Java player</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the UUID does not belong to a paid Java or Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * @param uuid Java or Bedrock player's UUID
	 * @return Java or Bedrock player's name
	 * @throws IllegalArgumentException If {@link UUID#version()}<code> != 0 &amp;&amp; {@link UUID#version()} != 4</code>
	 */
	public abstract CompletableFuture<String> getName(UUID uuid);
	
	/**
	 * Gets a Bedrock player's name.
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the XUID does not belong to a paid Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * @param xuid Bedrock player's XUID
	 * @return Bedrock player's name
	 */
	public abstract CompletableFuture<String> getName(long xuid);
	
	/**
	 * Gets a Java (online or offline) or Bedrock player's UUID based on the input:
	 * 	<ul>
	 * 		<li>a name starting with {@link PlayerManager#getFloodgateUsernamePrefix()} for a Bedrock player</li>
	 * 		<li>any other name while {@link ChatPlugin#isOnlineMode()} for a Java online player</li>
	 * 		<li>any other name while <code>!</code>{@link ChatPlugin#isOnlineMode()} for a Java offline player</li>
	 * 	</ul>
	 * 
	 * <p>The future is instantly completed and will not throw any exception if specified name
	 * is not a Bedrock username and <code>!</code>{@link ChatPlugin#isOnlineMode()}.</p>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the name does not belong to a paid Java (if {@link ChatPlugin#isOnlineMode()}) or Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * @param name Java (online or offline) or Bedrock player's name
	 * @return Java (online or offline) or Bedrock player's UUID
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public abstract CompletableFuture<UUID> getUUID(String name);
	
	/**
	 * Gets a Java or Bedrock player's UUID based on the input:
	 * 	<ul>
	 * 		<li>a name starting with {@link PlayerManager#getFloodgateUsernamePrefix()} for a Bedrock player</li>
	 * 		<li>any other name for a Java player</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the name does not belong to a paid Java or Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * @param name Java or Bedrock player's name
	 * @return Java or Bedrock player's UUID
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public abstract CompletableFuture<UUID> getOnlineUUID(String name);
	
	/**
	 * Gets a Bedrock player's UUID.
	 * 
	 * <p>This simply converts the specified XUID to its
	 * corresponding UUIDv0; no validation is performed.</p>
	 * 
	 * @param xuid Bedrock player's XUID
	 * @return Bedrock player's UUID
	 */
	public abstract UUID getOnlineUUID(long xuid);
	
	/**
	 * Calculates the specified Java player's offline
	 * UUIDv3 using {@link UUID#nameUUIDFromBytes(byte[])}.
	 * 
	 * @param name Java player's offline name
	 * @return Java player's offline UUIDv3
	 * @throws IllegalArgumentException If specified name is a Bedrock
	 * username or <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public abstract UUID getOfflineUUID(String name);
	
	/**
	 * Gets a Bedrock player's XUID by:
	 * 	<ul>
	 * 		<li>their in-game username (a name starting with {@link PlayerManager#getFloodgateUsernamePrefix()} matching {@link PlayerManager#getUsernamePattern()})</li>
	 * 		<li>their gamertag (any other name)</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the name does not belong to a paid Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * @param name Bedrock player's name or gamertag
	 * @return Bedrock player's XUID
	 * @throws IllegalArgumentException If <code>!{@link PlayerManager#isValidUsername(String)} &amp;&amp; !{@link #isValidClassicGamertag(String)}</code>
	 */
	public abstract CompletableFuture<Long> getXUID(String name);
	
	/**
	 * Gets a Bedrock player's XUID.
	 * 
	 * <p>This simply converts the specified UUIDv0 to its corresponding
	 * XUID; only a check on {@link UUID#version()} is performed.</p>
	 * 
	 * @param uuid Bedrock player's UUID
	 * @return Bedrock player's XUID
	 * @throws IllegalArgumentException If {@link UUID#version()}<code> != 0</code>
	 */
	public abstract long getXUID(UUID uuid);
	
	/**
	 * Gets a Java or Bedrock player's skin's texture's URL based on the input:
	 * 	<ul>
	 * 		<li>a name starting with {@link PlayerManager#getFloodgateUsernamePrefix()} for a Bedrock player</li>
	 * 		<li>any other name for a Java player</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the name does not belong to a paid Java or Bedrock account <em>with a skin</em></li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * <p>All paid Java accounts have a default skin, but Bedrock ones do not.
	 * Skin texture URLs always point to <code>textures.minecraft.net</code>.</p>
	 * 
	 * @param name Java or Bedrock player's name
	 * @return Java or Bedrock player's skin's texture's URL
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public abstract CompletableFuture<String> getSkinTextureURL(String name);
	
	/**
	 * Gets a Java or Bedrock player's skin's texture's URL based on the input:
	 * 	<ul>
	 * 		<li>{@link UUID}v0 for a Bedrock player</li>
	 * 		<li>{@link UUID}v4 for a Java player</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the UUID does not belong to a paid Java or Bedrock account <em>with a skin</em></li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * <p>All paid Java accounts have a default skin, but Bedrock ones do not.
	 * Skin texture URLs always point to <code>textures.minecraft.net</code>.</p>
	 * 
	 * @param uuid Java or Bedrock player's UUID
	 * @return Java or Bedrock player's skin's texture's URL
	 * @throws IllegalArgumentException If {@link UUID#version()}<code> != 0 &amp;&amp; {@link UUID#version()} != 4</code>
	 */
	public abstract CompletableFuture<String> getSkinTextureURL(UUID uuid);
	
	/**
	 * Gets a Bedrock player's skin's texture's URL.
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link NoSuchElementException} if the XUID does not belong to a paid Bedrock account <em>with a skin</em></li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs</li>
	 * 	</ul>
	 * 
	 * <p>All paid Java accounts have a default skin, but Bedrock ones do not.
	 * Skin texture URLs always point to <code>textures.minecraft.net</code>.</p>
	 * 
	 * @param xuid Bedrock player's XUID
	 * @return Bedrock player's skin's texture's URL
	 */
	public abstract CompletableFuture<String> getSkinTextureURL(long xuid);
	
	/**
	 * Gets the fetcher's current instance.
	 * 
	 * @return Current instance
	 */
	public static UUIDFetcher getInstance() {
		return instance;
	}
	
	/**
	 * Applies dashes to the given UUID.
	 * 
	 * <p><strong>Example:</strong> "e1b47c83541c4a2a91d0b382279e9017"
	 * ➝ "e1b47c83-541c-4a2a-91d0-b382279e9017"</p>
	 * 
	 * @param nonDashedUUID UUID to dash
	 * @return Dashed UUID
	 * @throws IllegalArgumentException If specified UUID is invalid
	 */
	public static UUID dash(String nonDashedUUID) {
		if (nonDashedUUID.length() == 32)
			return new UUID(Long.parseUnsignedLong(nonDashedUUID.substring(0, 16), 16), Long.parseUnsignedLong(nonDashedUUID.substring(16), 16));
		throw new IllegalArgumentException("Specified non-dashed UUID is not 32 characters long");
	}
	
	/**
	 * Checks if the specified String is a valid classic gamertag.
	 * 
	 * @param classicGamertag Classic gamertag to check
	 * @return Whether the specified classic gamertag is valid
	 * @see #CLASSIC_GAMERTAG_PATTERN
	 */
	public static boolean isValidClassicGamertag(String classicGamertag) {
		return CLASSIC_GAMERTAG_PATTERN.matcher(classicGamertag).matches();
	}
	
}

