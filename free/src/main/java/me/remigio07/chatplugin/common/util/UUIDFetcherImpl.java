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
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;

public class UUIDFetcherImpl extends UUIDFetcher {
	
	@Override
	public CompletableFuture<String> getName(UUID uuid) {
		if (uuid.version() == 4) {
			CompletableFuture<String> future = new CompletableFuture<>();
			
			TaskManager.runAsync(() -> {
				try {
					Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
					
					switch (response.getKey()) {
					case 200:
						JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
						
						if (json.containsKey("name")) {
							future.complete((String) json.get("name"));
							break;
						} throw new IOException("Invalid response returned by sessionserver.mojang.com: " + json.toJson());
					case 400:
						throw new NoSuchElementException("Specified UUID (" + uuid.toString() + ") does not belong to a paid Java account");
					case 429:
						switch ((response = readURL("https://playerdb.co/api/player/minecraft/" + uuid.toString())).getKey()) {
						case 200:
							JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
							
							if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("username")) {
								future.complete((String) json2.get("username"));
								return;
							} throw new IOException("Invalid response returned by playerdb.co: " + json2.toJson());
						case 400:
							throw new NoSuchElementException("Specified UUID (" + uuid.toString() + ") does not belong to a paid Java account");
						default:
							throw new IOException("Invalid HTTP code returned by playerdb.co: " + response.getKey());
						}
					default:
						throw new IOException("Invalid HTTP code returned by sessionserver.mojang.com: " + response.getKey());
					}
				} catch (JsonException jsone) {
					future.completeExceptionally(new IOException(jsone));
				} catch (Exception e) {
					future.completeExceptionally(e);
				}
			}, 0L);
			return future;
		} else if (uuid.version() == 0)
			return getName(uuid.getLeastSignificantBits());
		throw new IllegalArgumentException("Specified UUIDv" + uuid.version() + " is not a valid Bedrock (v0) or Java online (v4) UUID");
	}
	
	@Override
	public CompletableFuture<String> getName(long xuid) {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://mcprofile.io/api/v1/bedrock/xuid/" + xuid);
				
				switch (response.getKey()) {
				case 200:
				case 304:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.containsKey("gamertag")) {
						future.complete(PlayerManager.getInstance().getFloodgateUsernamePrefix() + ((String) json.get("gamertag")).replace(' ', '_'));
						break;
					} throw new IOException("Invalid response returned by mcprofile.io: " + json.toJson());
				case 404:
					throw new NoSuchElementException("Specified XUID (" + xuid + ") does not belong to a paid Bedrock account");
				default:
					throw new IOException("Invalid HTTP code returned by mcprofile.io: " + response.getKey());
				}
			} catch (JsonException jsone) {
				future.completeExceptionally(new IOException(jsone));
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		}, 0L);
		return future;
	}
	
	@Override
	public CompletableFuture<UUID> getUUID(String name) {
		if (!PlayerManager.getInstance().isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
		CompletableFuture<UUID> future = new CompletableFuture<>();
		
		if (!PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() && name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix()))
			TaskManager.runAsync(() -> completeBedrockUUID(future, name), 0L);
		else if (ChatPlugin.getInstance().isOnlineMode())
			TaskManager.runAsync(() -> completeJavaUUID(future, name), 0L);
		else future.complete(getOfflineUUID0(name));
		return future;
	}
	
	@Override
	public CompletableFuture<UUID> getOnlineUUID(String name) {
		if (!PlayerManager.getInstance().isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
		CompletableFuture<UUID> future = new CompletableFuture<>();
		
		TaskManager.runAsync(PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() || !name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix()) ? () -> completeJavaUUID(future, name) : () -> completeBedrockUUID(future, name), 0);
		return future;
	}
	
	private void completeBedrockUUID(CompletableFuture<UUID> future, String name) {
		try {
			Entry<Integer, String> response = readURL("https://mcprofile.io/api/v1/bedrock/gamertag/" + name.substring(1).replace('_', ' '));
			
			switch (response.getKey()) {
			case 200:
			case 304:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				
				if (json.containsKey("floodgateuid")) {
					future.complete(UUID.fromString((String) json.get("floodgateuid")));
					break;
				} throw new IOException("Invalid response returned by mcprofile.io: " + json.toJson());
			case 404:
				throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Bedrock account");
			default:
				throw new IOException("Invalid HTTP code returned by mcprofile.io: " + response.getKey());
			}
		} catch (JsonException jsone) {
			future.completeExceptionally(new IOException(jsone));
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}
	
	private void completeJavaUUID(CompletableFuture<UUID> future, String name) {
		try {
			Entry<Integer, String> response = readURL("https://api.mojang.com/users/profiles/minecraft/" + name);
			
			switch (response.getKey()) {
			case 200:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				
				if (json.containsKey("id")) {
					future.complete(dash((String) json.get("id")));
					break;
				} throw new IOException("Invalid response returned by api.mojang.com: " + json.toJson());
			case 404:
				throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Java account");
			case 429:
				switch ((response = readURL("https://playerdb.co/api/player/minecraft/" + name)).getKey()) {
				case 200:
					JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("id")) {
						future.complete(UUID.fromString((String) json2.get("id")));
						return;
					} throw new IOException("Invalid response returned by playerdb.co: " + json2.toJson());
				case 400:
					throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Java account");
				default:
					throw new IOException("Invalid HTTP code returned by playerdb.co: " + response.getKey());
				}
			default:
				throw new IOException("Invalid HTTP code returned by api.mojang.com: " + response.getKey());
			}
		} catch (JsonException jsone) {
			future.completeExceptionally(new IOException(jsone));
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}
	
	@Override
	public UUID getOnlineUUID(long xuid) {
		return new UUID(0L, xuid);
	}
	
	@Override
	public UUID getOfflineUUID(String name) {
		if (!PlayerManager.getInstance().isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
		if (PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() || !name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix()))
			return getOfflineUUID0(name);
		throw new IllegalArgumentException("Username \"" + name + "\" cannot be used with UUIDFetcher#getOfflineUUID(String) as it is a Bedrock username");
	}
	
	private UUID getOfflineUUID0(String name) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public CompletableFuture<Long> getXUID(String name) {
		if (!PlayerManager.getInstance().isValidUsername(name) && !isValidClassicGamertag(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect any of the following patterns: [\"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\", \"" + CLASSIC_GAMERTAG_PATTERN.pattern() + "\"]");
		CompletableFuture<Long> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://mcprofile.io/api/v1/bedrock/gamertag/" + (PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() || !name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix()) ? name : name.substring(1).replace('_', ' ')));
				
				switch (response.getKey()) {
				case 200:
				case 304:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.containsKey("xuid")) {
						future.complete(Long.valueOf((String) json.get("xuid")));
						break;
					} throw new IOException("Invalid response returned by mcprofile.io: " + json.toJson());
				case 404:
					throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Bedrock account");
				default:
					throw new IOException("Invalid HTTP code returned by mcprofile.io: " + response.getKey());
				}
			} catch (JsonException jsone) {
				future.completeExceptionally(new IOException(jsone));
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		}, 0L);
		return future;	
	}
	
	@Override
	public long getXUID(UUID uuid) {
		if (uuid.version() == 0)
			return uuid.getLeastSignificantBits();
		throw new IllegalArgumentException("Specified UUIDv" + uuid.version() + " cannot be converted to a XUID as it is not a UUIDv0");
	}
	
	@Override
	public CompletableFuture<String> getSkinTextureURL(String name) {
		if (!PlayerManager.getInstance().isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
		if (PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() || !name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix())) {
			CompletableFuture<String> future = new CompletableFuture<>();
			
			TaskManager.runAsync(() -> {
				try {
					Entry<Integer, String> response = readURL("https://api.mojang.com/users/profiles/minecraft/" + name);
					
					switch (response.getKey()) {
					case 200:
						JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
						
						if (json.containsKey("id")) {
							switch ((response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + json.get("id"))).getKey()) { // 400 is returned if the account does not exist, but here we have just ensured it as api.mojang.com returned 200; no need to check its case
							case 200:
								json = (JsonObject) Jsoner.deserialize(response.getValue());
								JsonArray array;
								
								if (json.containsKey("properties") && !(array = (JsonArray) json.get("properties")).isEmpty() && (json = (JsonObject) array.get(0)).containsKey("value")
										&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
										&& (json = (JsonObject) json.get("textures")).containsKey("SKIN") && (json = (JsonObject) json.get("SKIN")).containsKey("url")) {
									future.complete((String) json.get("url"));
									return;
								} throw new IOException("Invalid response returned by sessionserver.mojang.com: " + json.toJson());
							case 429:
								break; // go to fetch below
							default:
								throw new IOException("Invalid HTTP code returned by sessionserver.mojang.com: " + response.getKey());
							}
						} else throw new IOException("Invalid response returned by api.mojang.com: " + json.toJson());
						break;
					case 404:
						throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Java account");
					case 429:
						switch ((response = readURL("https://playerdb.co/api/player/minecraft/" + name)).getKey()) {
						case 200:
							break; // go to fetch below
						case 400:
							throw new NoSuchElementException("Specified name (" + name + ") does not belong to a paid Java account");
						default:
							throw new IOException("Invalid HTTP code returned by playerdb.co: " + response.getKey());
						} break;
					default:
						throw new IOException("Invalid HTTP code returned by api.mojang.com: " + response.getKey());
					} JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.containsKey("data") && (json = (JsonObject) json.get("data")).containsKey("player") && (json = (JsonObject) json.get("player")).containsKey("skin_texture"))
						future.complete((String) json.get("skin_texture"));
					else throw new IOException("Invalid response returned by playerdb.co: " + json.toJson());
				} catch (JsonException jsone) {
					future.completeExceptionally(new IOException(jsone));
				} catch (Exception e) {
					future.completeExceptionally(e);
				}
			}, 0L);
			return future;
		} return getBedrockSkin("gamertag/" + name.substring(1).replace('_', ' '), "gamertag");
	}
	
	@Override
	public CompletableFuture<String> getSkinTextureURL(UUID uuid) {
		if (uuid.version() == 4) {
			CompletableFuture<String> future = new CompletableFuture<>();
			
			TaskManager.runAsync(() -> {
				try {
					Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
					
					switch (response.getKey()) {
					case 200:
						JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
						JsonArray array;
						
						if (json.containsKey("properties") && !(array = (JsonArray) json.get("properties")).isEmpty() && (json = (JsonObject) array.get(0)).containsKey("value")
								&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
								&& (json = (JsonObject) json.get("textures")).containsKey("SKIN") && (json = (JsonObject) json.get("SKIN")).containsKey("url")) {
							future.complete((String) json.get("url"));
							return;
						} throw new IOException("Invalid response returned by sessionserver.mojang.com: " + json.toJson());
					case 400:
						throw new NoSuchElementException("Specified UUID (" + uuid.toString() + ") does not belong to a paid Java account");
					case 429:
						switch ((response = readURL("https://playerdb.co/api/player/minecraft/" + uuid.toString())).getKey()) {
						case 200:
							JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
							
							if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("skin_texture")) {
								future.complete((String) json2.get("skin_texture"));
								return;
							} throw new IOException("Invalid response returned by playerdb.co: " + json2.toJson());
						case 400:
							throw new NoSuchElementException("Specified UUID (" + uuid.toString() + ") does not belong to a paid Java account");
						default:
							throw new IOException("Invalid HTTP code returned by playerdb.co: " + response.getKey());
						}
					default:
						throw new IOException("Invalid HTTP code returned by sessionserver.mojang.com: " + response.getKey());
					}
				} catch (JsonException jsone) {
					future.completeExceptionally(new IOException(jsone));
				} catch (Exception e) {
					future.completeExceptionally(e);
				}
			}, 0L);
			return future;
		} else if (uuid.version() == 0)
			return getBedrockSkin("fuid/" + uuid.toString(), "UUID");
		throw new IllegalArgumentException("Specified UUIDv" + uuid.version() + " is not a valid Bedrock (v0) or Java online (v4) UUID");
	}
	
	@Override
	public CompletableFuture<String> getSkinTextureURL(long xuid) {
		return getBedrockSkin("xuid/" + xuid, "XUID");
	}
	
	private CompletableFuture<String> getBedrockSkin(String endpointSuffix, String arg) {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		TaskManager.runAsync(() -> {
			try {
				Entry<Integer, String> response = readURL("https://mcprofile.io/api/v1/bedrock/" + endpointSuffix);
				
				switch (response.getKey()) {
				case 200:
				case 304:
					JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json.get("skin") != null) {
						future.complete((String) json.get("skin"));
						return;
					} break;
				case 404:
					break;
				default:
					throw new IOException("Invalid HTTP code returned by mcprofile.io: " + response.getKey());
				} throw new NoSuchElementException("Specified " + arg + " (" + endpointSuffix.substring(endpointSuffix.indexOf('/') + 1) + ") does not belong to a paid Bedrock account with a skin");
			} catch (JsonException jsone) {
				future.completeExceptionally(new IOException(jsone));
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		}, 0L);
		return future;
	}
	
	private Entry<Integer, String> readURL(String url) throws URISyntaxException, IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URI(url).toURL().openConnection();
		
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", Utils.USER_AGENT);
		connection.setConnectTimeout(10000);
		
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
	
	public static void setInstance(UUIDFetcher instance) {
		UUIDFetcher.instance = instance;
	}
	
}
