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

package me.remigio07.chatplugin.server.chat.antispam;

import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;

public class AntispamResultImpl extends AntispamResult {
	
	public AntispamResultImpl() {
		// everything null
	}
	
	public AntispamResultImpl(DenyChatReason<AntispamManager> reason, String disallowedText, String highlightedMessage) {
		this.reason = reason;
		this.disallowedText = disallowedText;
		this.highlightedMessage = highlightedMessage;
	}
	
}