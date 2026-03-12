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

package me.remigio07.chatplugin.api.server.rank;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents the mode used to detect players' ranks.
 * 
 * <p>This controls when {@link ChatPluginServerPlayer#getRank()}
 * is calculated and possibly updated.</p>
 */
public enum RankDetectionMode {
	
	/**
	 * Rank detection occurs on player join.
	 * 
	 * <p>This is the default value.</p>
	 */
	JOIN,
	
	/**
	 * The permission plugin's events are used to detect rank changes.
	 * 
	 * <p>Requires {@linkplain IntegrationType#LUCKPERMS LuckPerms}.
	 * More integrations will be supported in the future.</p>
	 */
	EVENT, // use @RequiredIntegration when multiple integrations will be supported
	
	/**
	 * An asynchronous repeating task detects ranks changes periodically.
	 * 
	 * <p>The task runs every 5 seconds, but the actual check
	 * is performed based on the online players' amount:
	 * 	<ul>
	 * 		<li>0–49: every 5 seconds</li>
	 * 		<li>50–99: every 10 seconds</li>
	 * 		<li>100–149: every 15 seconds</li>
	 * 		<li>...and so on</li>
	 * 	</ul>
	 */
	PERIODIC;
	
}
