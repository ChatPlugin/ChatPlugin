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

package me.remigio07.chatplugin.api.server.chat.channel.data;

abstract class SocialChatChannel extends ChatChannelData {
	
	protected String minecraftFormat;
	
	protected SocialChatChannel(String minecraftFormat) {
		this.minecraftFormat = minecraftFormat;
	}
	
	/**
	 * Gets the format used by external
	 * messages sent to the Minecraft chat.
	 * 
	 * @return Minecraft messages' format
	 */
	public String getMinecraftFormat() {
		return minecraftFormat;
	}
	
}
