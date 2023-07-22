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

package me.remigio07_.chatplugin.api.common.motd;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import me.remigio07_.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07_.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.common.util.packet.Packets;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Represents a MoTD handled by the {@link MoTDManager}.
 */
public class MoTD {
	
	private String description, hover, versionName;
	private URL customIconURL;
	private boolean hoverDisplayed, versionNameDisplayed, customIconDisplayed;
	private int onlinePlayers, maxPlayers;
	
	/**
	 * Constructs a new MoTD.
	 * 
	 * @param description MoTD's description
	 * @param hover MoTD's hover
	 * @param versionName MoTD's version name
	 * @param customIconURL MoTD's custom icon's URL
	 * @param hoverDisplayed Whether the hover should be visible
	 * @param versionNameDisplayed Whether the version name should be visible
	 * @param customIconDisplayed Whether the custom icon should be visible
	 * @param onlinePlayers MoTD's online players
	 * @param maxPlayers MoTD's max players
	 */
	public MoTD(String description, String hover, String versionName, URL customIconURL, boolean hoverDisplayed, boolean versionNameDisplayed, boolean customIconDisplayed, int onlinePlayers, int maxPlayers) {
		this.description = description;
		this.hover = hover;
		this.versionName = versionName;
		this.customIconURL = customIconURL;
		this.hoverDisplayed = hoverDisplayed;
		this.versionNameDisplayed = versionNameDisplayed;
		this.customIconDisplayed = customIconDisplayed;
		this.onlinePlayers = onlinePlayers;
		this.maxPlayers = maxPlayers;
	}
	
	/**
	 * Gets this MoTD's description.
	 * 
	 * @return MoTD's description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets this MoTD's hover.
	 * 
	 * @return MoTD's hover
	 */
	public String getHover() {
		return hover;
	}
	
	/**
	 * Gets this MoTD's version name.
	 * 
	 * @return MoTD's version name
	 */
	public String getVersionName() {
		return versionName;
	}
	
	/**
	 * Gets this MoTD's custom icon's URL.
	 * 
	 * @return MoTD's custom icon's URL
	 */
	public URL getCustomIconURL() {
		return customIconURL;
	}
	
	/**
	 * Checks if {@link #getHover()} should be displayed.
	 * 
	 * @return Whether the hover is visible
	 */
	public boolean isHoverDisplayed() {
		return hoverDisplayed;
	}
	
	/**
	 * Checks if {@link #getVersionName()} should be displayed.
	 * 
	 * @return Whether the version name is visible
	 */
	public boolean isVersionNameDisplayed() {
		return versionNameDisplayed;
	}
	
	/**
	 * Checks if the favicon found at {@link #getCustomIconURL()} should be displayed.
	 * 
	 * @return Whether the custom icon is visible
	 */
	public boolean isCustomIconDisplayed() {
		return customIconDisplayed;
	}
	
	/**
	 * Gets this MoTD's online players' amount.
	 * 
	 * @return MoTD's online players
	 */
	public int getOnlinePlayers() {
		return onlinePlayers;
	}
	
	/**
	 * Gets this MoTD's max players' amount.
	 * 
	 * @return MoTD's max players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	/**
	 * Sets this MoTD's description.
	 * 
	 * @param description MoTD's description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Sets this MoTD's hover.
	 * 
	 * @param hover MoTD's hover
	 */
	public void setHover(String hover) {
		this.hover = hover;
	}
	
	/**
	 * Sets this MoTD's version name.
	 * 
	 * @param versionName MoTD's version name
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	/**
	 * Sets this MoTD's custom icon's URL.
	 * 
	 * @param customIconURL MoTD's custom icon's URL
	 */
	public void setCustomIconURL(URL customIconURL) {
		this.customIconURL = customIconURL;
	}
	
	/**
	 * Sets if {@link #getHover()} should be displayed.
	 * 
	 * @param hoverDisplayed Whether the hover is visible
	 */
	public void setHoverDisplayed(boolean hoverDisplayed) {
		this.hoverDisplayed = hoverDisplayed;
	}
	
	/**
	 * Sets if {@link #getVersionName()} should be displayed.
	 * 
	 * @param versionNameDisplayed Whether the version name is visible
	 */
	public void setVersionNameDisplayed(boolean versionNameDisplayed) {
		this.versionNameDisplayed = versionNameDisplayed;
	}
	
	/**
	 * Sets if the favicon found at {@link #getCustomIconURL()} should be displayed.
	 * 
	 * @param customIconDisplayed Whether the custom icon is visible
	 */
	public void setCustomIconDisplayed(boolean customIconDisplayed) {
		this.customIconDisplayed = customIconDisplayed;
	}
	
	/**
	 * Sets this MoTD's online players' amount.
	 * 
	 * @param onlinePlayers MoTD's online players
	 */
	public void setOnlinePlayers(int onlinePlayers) {
		this.onlinePlayers = onlinePlayers;
	}
	
	/**
	 * Sets this MoTD's max players' amount.
	 * 
	 * @param maxPlayers MoTD's max players
	 */
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	/**
	 * Initializes a <code>MoTDResponse</code>
	 * packet for the specified IP address.
	 * 
	 * @param ipAddress Packet's IP address
	 * @return <code>MoTDResponse</code> packet
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_PLUGIN_MESSAGE)
	@SuppressWarnings("deprecation")
	public PacketSerializer toPacket(InetAddress ipAddress) {
		return Packets.Sync.motdResponse(
				ProxyManager.getInstance().getServerID(),
				ipAddress,
				description,
				hover,
				versionName,
				customIconURL,
				hoverDisplayed,
				versionNameDisplayed,
				customIconDisplayed,
				onlinePlayers,
				maxPlayers
				);
	}
	
	/**
	 * Reads a MoTD from a <code>MoTDResponse</code> packet.
	 * Will return <code>null</code> if {@link #getCustomIconURL()} is
	 * invalid or the packet's subchannel is not <code>MoTDResponse</code>.
	 * 
	 * @param packet Packet to read
	 * @return New MoTD
	 */
	public static MoTD fromPacket(byte[] packet) {
		PacketDeserializer deserializer = new PacketDeserializer(packet);
		
		if (deserializer.readUTF().equals("MoTDResponse")) {
			deserializer.readUTF();
			deserializer.readUTF();
			
			try {
				return new MoTD(
						deserializer.readUTF(),
						deserializer.readUTF(),
						deserializer.readUTF(),
						new URL(deserializer.readUTF()),
						deserializer.readBoolean(),
						deserializer.readBoolean(),
						deserializer.readBoolean(),
						deserializer.readInt(),
						deserializer.readInt()
						);
			} catch (MalformedURLException e) {
				
			}
		} return null;
	}
	
}
