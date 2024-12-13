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

import me.remigio07.chatplugin.api.ChatPlugin;

/**
 * Represents the plugin's state.
 * 
 * @see ChatPlugin#getState()
 */
public enum ChatPluginState {
	
	/**
	 * Initial state, when the server has not started loading it yet.
	 * 
	 * <p><strong>Warning:</strong> no operations involving
	 * ChatPlugin should be executed at this moment.</p>
	 */
	NOT_STARTED_YET,
	
	/**
	 * Starting state, when the plugin is either:
	 * 	<ul>
	 * 		<li>being started during a server startup or <code>/bukkit:reload</code></li>
	 * 		<li>being restarted after an attempt of recovery caused by a failed {@link ChatPlugin#reload()}</li>
	 * 	</ul>
	 * 
	 * <p><strong>Warning:</strong> operations involving
	 * ChatPlugin may be executed at this moment, however there
	 * are no guarantees on the behaviour of its modules.</p>
	 */
	STARTING,
	
	/**
	 * Reloading state, when a {@link ChatPlugin#reload()} is being performed.
	 * 
	 * <p><strong>Warning:</strong> operations involving
	 * ChatPlugin may be executed at this moment, however there
	 * are no guarantees on the behaviour of its modules.</p>
	 */
	RELOADING,
	
	/**
	 * Ordinary execution state, when the plugin is
	 * correctly working with (hopefully) no issues.
	 * 
	 * <p>Generally, operations involving ChatPlugin
	 * should be executed in this period of time.</p>
	 */
	LOADED,
	
	/**
	 * Unloading state, when the plugin is either:
	 * 	<ul>
	 * 		<li>being disabled during a server shutdown or <code>/bukkit:reload</code></li>
	 * 		<li>being unloaded after a failed {@link ChatPlugin#reload()}</li>
	 * 	</ul>
	 * 
	 * <p><strong>Warning:</strong> operations involving
	 * ChatPlugin may be executed at this moment, however there
	 * are no guarantees on the behaviour of its modules.</p>
	 */
	UNLOADING,
	
	/**
	 * Unloaded state, when {@link ChatPlugin#unload()} has been
	 * completed <em>without issues</em> and the plugin has been disabled.
	 * 
	 * <p><strong>Warning:</strong> no operations involving
	 * ChatPlugin should be executed at this moment.</p>
	 */
	UNLOADED,
	
	/**
	 * Recovery mode state, when the plugin fails
	 * {@link ChatPlugin#reload()} or {@link ChatPlugin#unload()}.
	 * 
	 * <p><strong>Warning:</strong> no operations involving
	 * ChatPlugin should be executed at this moment.</p>
	 */
	RECOVERY;
	
}
