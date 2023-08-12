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

package me.remigio07.chatplugin.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.common.primitives.UnsignedLongs;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;

public class UUIDFetcherImpl extends UUIDFetcher {
	
	@Override
	public String readURL(String url) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		
		connection.setConnectTimeout(10000);
		
		if (connection.getResponseCode() != 200)
			return "";
		StringBuilder output = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		
		while ((line = in.readLine()) != null)
			output.append(line);
		in.close();
		connection.disconnect();
		return output.toString();
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
			JsonObject json = ((JsonObject) Jsoner.deserialize(readURL("https://api.mojang.com/users/profiles/minecraft/" + name)));
			
			if (json.isEmpty())
				return Utils.NIL_UUID;
			return dash((String) json.get("id"));
		} catch (JsonException e) {
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
			JsonObject json = ((JsonObject) Jsoner.deserialize(readURL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString())));
			
			if (json.isEmpty() || !json.containsKey("name"))
				return null;
			return (String) json.get("name");
		} catch (JsonException e) {
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
