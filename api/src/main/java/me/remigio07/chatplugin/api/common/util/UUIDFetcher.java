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

package me.remigio07.chatplugin.api.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Util class used to calculate and fetch players' UUIDs and names.
 * 
 * <p>Its methods are abstract as some of them use libraries' classes.</p>
 */
public abstract class UUIDFetcher {
	
	protected static UUIDFetcher instance;
	
	/**
	 * Reads the content at the specified URL.
	 * 
	 * <p>It has a connection timeout of 5 seconds.</p>
	 * 
	 * @param url URL to read
	 * @return URL's response code and content
	 * @throws URISyntaxException If the URL is invalid
	 * @throws IOException If a connection error occurrs
	 */
	public abstract Entry<Integer, String> readURL(String url) throws URISyntaxException, IOException;
	
	/**
	 * Calls {@link #getOnlineUUID(String)} or {@link #getOfflineUUID(String)}
	 * depending on {@link ChatPlugin#isOnlineMode()}.
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * @param name Player's name
	 * @return Player's UUID
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	public abstract CompletableFuture<UUID> getUUID(String name);
	
	/**
	 * Fetches the specified player's online UUID v4 from Mojang's servers.
	 * 
	 * <p>Will return {@link Utils#NIL_UUID} if the specified
	 * name does not belong to a premium account.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * @param name Player's name
	 * @return Player's online UUID
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	public abstract CompletableFuture<UUID> getOnlineUUID(String name);
	
	/**
	 * Calculates the specified player's offline UUID
	 * v3 using {@link UUID#nameUUIDFromBytes(byte[])}.
	 * 
	 * @param name Player's name
	 * @return Player's offline UUID
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	public abstract UUID getOfflineUUID(String name);
	
	/**
	 * Fetches the specified player's name from Mojang's servers.
	 * 
	 * <p>Will return <code>null</code> if the specified
	 * UUID does not belong to a premium account.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * @param uuid Player's online UUID
	 * @return Player's name
	 */
	@Nullable(why = "The specified UUID may not belong to any premium account")
	public abstract CompletableFuture<String> getOnlineName(UUID uuid);
	
	/**
	 * Gets a premium player's skin's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if the specified
	 * name does not belong to a premium account.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * <p>The URL will point to <code>textures.minecraft.net</code> or to
	 * <code>api.mineatar.io</code> if a rate limit is encountered.</p>
	 * 
	 * @param name Player's name
	 * @return Player's skin's texture's URL
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	@Nullable(why = "The specified name may not belong to any premium account")
	public abstract CompletableFuture<String> getSkinTextureURL(String name);
	
	/**
	 * Gets a premium player's skin's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if the specified
	 * UUID does not belong to a premium account.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * <p>The URL will point to <code>textures.minecraft.net</code> or to
	 * <code>api.mineatar.io</code> if a rate limit is encountered.</p>
	 * 
	 * @param uuid Player's UUID
	 * @return Player's skin's texture's URL
	 */
	@Nullable(why = "The specified UUID may not belong to any premium account")
	public abstract CompletableFuture<String> getSkinTextureURL(UUID uuid);
	
	/**
	 * Gets a premium player's cape's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if the specified name does
	 * not belong to a premium account or they do not have a cape.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * <p>The URL will point to <code>textures.minecraft.net</code> or
	 * to <code>api.capes.dev</code> if a rate limit is encountered.</p>
	 * 
	 * @param name Player's name
	 * @return Player's cape's texture's URL
	 * @throws IllegalArgumentException If specified name !{@link Utils#isValidUsername(String)}
	 */
	@Nullable(why = "The specified name may not belong to any premium account or may not have a cape")
	public abstract CompletableFuture<String> getCapeTextureURL(String name);
	
	/**
	 * Gets a premium player's cape's texture's URL.
	 * 
	 * <p>Will return <code>null</code> if the specified UUID does
	 * not belong to a premium account or they do not have a cape.</p>
	 * 
	 * <p>The future throws {@link IOException} if a connection or JSON error occurs.</p>
	 * 
	 * <p>The URL will point to <code>textures.minecraft.net</code> or
	 * to <code>api.capes.dev</code> if a rate limit is encountered.</p>
	 * 
	 * @param uuid Player's UUID
	 * @return Player's cape's texture's URL
	 */
	@Nullable(why = "The specified UUID may not belong to any premium account or may not have a cape")
	public abstract CompletableFuture<String> getCapeTextureURL(UUID uuid);
	
	/**
	 * Applies dashes to the given UUID. For example,
	 * "e1b47c83541c4a2a91d0b382279e9017" becomes
	 * "e1b47c83-541c-4a2a-91d0-b382279e9017".
	 * 
	 * @param uuid UUID to dash
	 * @return UUID with dashes
	 * @throws NumberFormatException If specified UUID is invalid
	 * @throws IndexOutOfBoundsException If specified UUID is invalid
	 */
	public abstract UUID dash(String uuid);
	
	/**
	 * Gets the fetcher's current instance.
	 * 
	 * @return Current instance
	 */
	public static UUIDFetcher getInstance() {
		return instance;
	}
	
}

