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

package me.remigio07.chatplugin.common.util.manager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;

public class TaskManagerImpl extends TaskManager { // in the future, we should create a TaskManager for every environment
	
	@Override
	public void load() {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (Environment.isSponge() || Environment.isFabric())
			currentSyncID = new AtomicLong();
		enabled = true;
		loadTime = System.currentTimeMillis() - ms; // kinda pointless but let's leave it for consistency
	}
	
	@Override
	public void unload() {
		enabled = false;
		
		if (Environment.isBukkit())
			BukkitScheduler.cancelTasks();
		else if (Environment.isSponge())
			Sponge.getScheduler().getScheduledTasks(SpongeBootstrapper.getInstance()).forEach(Task::cancel);
		else if (Environment.isFabric())
			syncTasks.values().forEach(task -> ((ScheduledFuture<?>) task).cancel(false));
		asyncTasks.keySet().forEach(future -> asyncTasks.remove(future).cancel(false));
		syncTasks.clear();
	}
	
	private static class BukkitScheduler {
		
		public static void cancelTasks() {
			Bukkit.getScheduler().cancelTasks(BukkitBootstrapper.getInstance());
		}
		
	}
	
}
