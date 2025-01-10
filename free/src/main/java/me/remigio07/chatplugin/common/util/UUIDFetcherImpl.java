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

package me.remigio07.chatplugin.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.Base64;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.common.primitives.UnsignedLongs;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;

public class UUIDFetcherImpl extends UUIDFetcher {
	
	@Override
	public Entry<Integer, String> readURL(String url) throws URISyntaxException, IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URI(url).toURL().openConnection();
		
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", Utils.USER_AGENT);
		connection.setConnectTimeout(5000);
		
		int responseCode = connection.getResponseCode();
		
		if (responseCode != 200 && responseCode != 304)
			return new SimpleEntry<>(responseCode, null);
		StringBuilder output = new StringBuilder();
		String line;
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
			while ((line = in.readLine()) != null)
				output.append(line);
		} finally {
			connection.disconnect();
		} return new SimpleEntry<>(responseCode, output.toString());
	}
	
	@Override
	public CompletableFuture<UUID> getUUID(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return ChatPlugin.getInstance().isOnlineMode() ? getOnlineUUID(name) : CompletableFuture.completedFuture(getOfflineUUID(name));
	}
	
	@Override
	public CompletableFuture<UUID> getOnlineUUID(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		CompletableFuture<UUID> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://api.mojang.com/users/profiles/minecraft/" + name);
				
				switch (response.getKey()) {
				case 200:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.containsKey("id")) {
						future.complete(dash((String) json.get("id")));
						return;
					} break;
				case 429:
					response = readURL("https://playerdb.co/api/player/minecraft/" + name);
					
					if (response.getKey() == 200) {
						JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
						
						if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("id")) {
							future.complete(UUID.fromString((String) json2.get("id")));
							return;
						}
					} break;
				default:
					break;
				} future.complete(Utils.NIL_UUID);
			} catch (Exception e) {
				future.completeExceptionally(new IOException(e));
			}
		}, 0L);
		return future;
	}
	
	@Override
	public UUID getOfflineUUID(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public CompletableFuture<String> getOnlineName(UUID uuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
				
				switch (response.getKey()) {
				case 200:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.containsKey("name")) {
						future.complete((String) json.get("name"));
						return;
					} break;
				case 429:
					response = readURL("https://playerdb.co/api/player/minecraft/" + uuid.toString());
					
					if (response.getKey() == 200) {
						JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
						
						if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("username")) {
							future.complete((String) json2.get("username"));
							return;
						}
					} break;
				default:
					break;
				} future.complete(null);
			} catch (Exception e) {
				future.completeExceptionally(new IOException(e));
			}
		}, 0L);
		return future;
	}
	
	@Override
	public CompletableFuture<String> getSkinTextureURL(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				future.complete(getSkinTextureURL(getOnlineUUID(name).get()).get());
			} catch (InterruptedException | ExecutionException e) {
				future.completeExceptionally(e);
			}
		}, 0L);
		return future;
	}
	
	@Override
	public CompletableFuture<String> getSkinTextureURL(UUID uuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
				
				switch (response.getKey()) {
				case 200:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					JsonArray array;
					
					if (json.containsKey("properties") && (array = (JsonArray) json.get("properties")).size() > 0 && (json = (JsonObject) array.get(0)).containsKey("value")
							&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
							&& (json = (JsonObject) json.get("textures")).containsKey("SKIN") && (json = (JsonObject) json.get("SKIN")).containsKey("url")) {
						future.complete((String) json.get("url"));
						return;
					} break;
				case 429:
					future.complete("https://api.mineatar.io/skin/" + uuid.toString());
					return;
				default:
					break;
				} future.complete(null);
			} catch (Exception e) {
				future.completeExceptionally(new IOException(e));
			}
		}, 0L);
		return future;
	}
	
	@Override
	public @Nullable(why = "The specified name may not belong to any premium account or may not have a cape") CompletableFuture<String> getCapeTextureURL(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				future.complete(getCapeTextureURL(getOnlineUUID(name).get()).get());
			} catch (InterruptedException | ExecutionException e) {
				future.completeExceptionally(e);
			}
		}, 0L);
		return future;
	}
	
	@Override
	public @Nullable(why = "The specified UUID may not belong to any premium account or may not have a cape") CompletableFuture<String> getCapeTextureURL(UUID uuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
				
				switch (response.getKey()) {
				case 200:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					JsonArray array;
					
					if (json.containsKey("properties") && (array = (JsonArray) json.get("properties")).size() > 0 && (json = (JsonObject) array.get(0)).containsKey("value")
							&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
							&& (json = (JsonObject) json.get("textures")).containsKey("CAPE") && (json = (JsonObject) json.get("CAPE")).containsKey("url")) {
						future.complete((String) json.get("url"));
						return;
					} break;
				case 429:
					response = readURL("https://api.capes.dev/load/" + uuid.toString() + "/minecraft");
					
					if (response.getKey() == 200 || response.getKey() == 304) {
						JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
						
						if (json2.containsKey("imageUrl")) {
							future.complete((String) json2.get("imageUrl"));
							return;
						}
					} break;
				default:
					break;
				} future.complete(null);
			} catch (Exception e) {
				future.completeExceptionally(new IOException(e));
			}
		}, 0L);
		return future;
	}
	
	@Override
	public UUID dash(String uuid) {
		return new UUID(UnsignedLongs.parseUnsignedLong(uuid.substring(0, 16), 16), UnsignedLongs.parseUnsignedLong(uuid.substring(16), 16));
	}
	
	public static void setInstance(UUIDFetcher instance) {
		UUIDFetcher.instance = instance;
	}
	
}
