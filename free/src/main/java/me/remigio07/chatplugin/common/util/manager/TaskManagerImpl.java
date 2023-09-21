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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.common.util.manager;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;

public class TaskManagerImpl extends TaskManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		enabled = true;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		if (Environment.isBukkit())
			syncTasks.keySet().forEach(task -> Bukkit.getScheduler().cancelTask(task.intValue())); // should not exceed Integer.MAX_VALUE
		else if (Environment.isSponge())
			Sponge.getScheduler().getScheduledTasks(SpongeBootstrapper.getInstance()).forEach(Task::cancel);
		asyncTasks.values().forEach(TimerTask::cancel);
		syncTasks.clear();
		asyncTasks.clear();
	}
	
}
