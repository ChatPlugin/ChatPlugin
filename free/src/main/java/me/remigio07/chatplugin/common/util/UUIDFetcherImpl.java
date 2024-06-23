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

import javax.net.ssl.HttpsURLConnection;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.common.primitives.UnsignedLongs;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

public class UUIDFetcherImpl extends UUIDFetcher {
	
	@Override
	public Entry<Integer, String> readURL(String url) throws URISyntaxException, IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URI(url).toURL().openConnection();
		
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 +https://remigio07.me/chatplugin ChatPlugin/" + ChatPlugin.VERSION);
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
	public UUID getUUID(String name) throws IOException {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return ChatPlugin.getInstance().isOnlineMode() ? getOnlineUUID(name) : getOfflineUUID(name);
	}
	
	@Override
	public UUID getOnlineUUID(String name) throws IOException {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		try {
			Entry<Integer, String> response = readURL("https://api.mojang.com/users/profiles/minecraft/" + name);
			
			switch (response.getKey()) {
			case 200:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				
				if (json.containsKey("id"))
					return dash((String) json.get("id"));
				break;
			case 429:
				response = readURL("https://playerdb.co/api/player/minecraft/" + name);
				
				if (response.getKey() == 200) {
					JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("id"))
						return UUID.fromString((String) json2.get("id"));
				} break;
			default:
				break;
			} return Utils.NIL_UUID;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public UUID getOfflineUUID(String name) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public String getOnlineName(UUID uuid) throws IOException {
		try {
			Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
			
			switch (response.getKey()) {
			case 200:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				
				if (json.containsKey("name"))
					return (String) json.get("name");
				break;
			case 429:
				response = readURL("https://playerdb.co/api/player/minecraft/" + uuid.toString());
				
				if (response.getKey() == 200) {
					JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json2.containsKey("data") && (json2 = (JsonObject) json2.get("data")).containsKey("player") && (json2 = (JsonObject) json2.get("player")).containsKey("username"))
						return (String) json2.get("username");
				} break;
			default:
				break;
			} return null;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String getSkinTextureURL(String name) throws IOException {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return getSkinTextureURL(getOnlineUUID(name));
	}
	
	@Override
	public String getSkinTextureURL(UUID uuid) throws IOException {
		try {
			Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
			
			switch (response.getKey()) {
			case 200:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				JsonArray array;
				
				if (json.containsKey("properties") && (array = (JsonArray) json.get("properties")).size() > 0 && (json = (JsonObject) array.get(0)).containsKey("value")
						&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
						&& (json = (JsonObject) json.get("textures")).containsKey("SKIN") && (json = (JsonObject) json.get("SKIN")).containsKey("url"))
					return (String) json.get("url");
				break;
			case 429:
				return "https://api.mineatar.io/skin/" + uuid.toString();
			default:
				break;
			} return null;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public @Nullable(why = "The specified name may not belong to any premium account or may not have a cape") String getCapeTextureURL(String name) throws IOException {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		return getCapeTextureURL(getOnlineUUID(name));
	}
	
	@Override
	public @Nullable(why = "The specified UUID may not belong to any premium account or may not have a cape") String getCapeTextureURL(UUID uuid) throws IOException {
		try {
			Entry<Integer, String> response = readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
			
			switch (response.getKey()) {
			case 200:
				JsonObject json = (JsonObject) Jsoner.deserialize(response.getValue());
				JsonArray array;
				
				if (json.containsKey("properties") && (array = (JsonArray) json.get("properties")).size() > 0 && (json = (JsonObject) array.get(0)).containsKey("value")
						&& (json = (JsonObject) Jsoner.deserialize(new String(Base64.getDecoder().decode((String) json.get("value")), StandardCharsets.ISO_8859_1))).containsKey("textures")
						&& (json = (JsonObject) json.get("textures")).containsKey("CAPE") && (json = (JsonObject) json.get("CAPE")).containsKey("url"))
					return (String) json.get("url");
				break;
			case 429:
				response = readURL("https://api.capes.dev/load/" + uuid.toString() + "/minecraft");
				
				if (response.getKey() == 200 || response.getKey() == 304) {
					JsonObject json2 = (JsonObject) Jsoner.deserialize(response.getValue());
					
					if (json2.containsKey("imageUrl"))
						return (String) json2.get("imageUrl");
				} break;
			default:
				break;
			} return null;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public UUID dash(String uuid) {
		return new UUID(UnsignedLongs.parseUnsignedLong(uuid.substring(0, 16), 16), UnsignedLongs.parseUnsignedLong(uuid.substring(16), 16));
	}
	
	public static void setInstance(UUIDFetcher instance) {
		UUIDFetcher.instance = instance;
	}
	
}
