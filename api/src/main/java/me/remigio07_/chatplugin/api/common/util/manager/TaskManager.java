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

package me.remigio07_.chatplugin.api.common.util.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import me.remigio07_.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07_.chatplugin.bootstrap.Environment;
import me.remigio07_.chatplugin.bootstrap.SpongeBootstrapper;

/**
 * Manager that handles (a)synchronous tasks executed by ChatPlugin.
 */
public abstract class TaskManager implements ChatPluginManager {
	
	protected static TaskManager instance;
	protected boolean enabled;
	protected Map<Long, UUID> syncTasks = new HashMap<>();
	protected Map<Long, TimerTask> asyncTasks = new HashMap<>();
	protected long currentSpongeID, currentAsyncID, loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the synchronous tasks map.
	 * 
	 * @return Sync tasks
	 */
	public Map<Long, UUID> getSyncTasks() {
		return syncTasks;
	}
	
	/**
	 * Gets the asynchronous tasks map.
	 * 
	 * @return Async tasks
	 */
	public Map<Long, TimerTask> getAsyncTasks() {
		return asyncTasks;
	}
	
	/**
	 * Gets the next Sponge sync task's ID and increments it by 1.
	 * Only applicable when {@link Environment#isSponge()}.
	 * 
	 * @return Next Sponge sync task's ID
	 */
	public long getNextSpongeID() {
		return currentSpongeID++;
	}
	
	/**
	 * Gets the next async task's ID and increments it by 1.
	 * 
	 * @return Next async task's ID
	 */
	public long getNextAsyncID() {
		return currentAsyncID++;
	}
	
	/**
	 * Runs a synchronous task on the main thread. Might impact performance
	 * with heavy tasks: use {@link #runAsync(Runnable, long)} if possible.
	 * Note that synchronous tasks are available on ChatPlugin's Bukkit
	 * and Sponge implementations only.
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @return Task's ID
	 * @throws UnsupportedOperationException If {@link Environment#isProxy()}
	 */
	public static long runSync(Runnable runnable, long delay) {
		if (Environment.isProxy())
			throw new UnsupportedOperationException("Synchronous tasks are not available on a " + Environment.getCurrent().getName() + " environment. Bukkit and Sponge only");
		long taskID = Environment.isBukkit() ? (long) BukkitScheduler.runSync(runnable, delay) : instance.currentSpongeID++;
		
		instance.syncTasks.put(taskID, Environment.isBukkit() ? UUID.randomUUID() : Sponge.getScheduler().createTaskBuilder().execute(runnable).delayTicks((delay < 0 ? 0 : delay) / 50).submit(SpongeBootstrapper.getInstance()).getUniqueId());
		runAsync(() -> instance.syncTasks.remove(taskID), delay);
		return taskID;
	}
	
	/**
	 * Runs an asynchronous task on a new thread.
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @return Task's ID
	 */
	public static long runAsync(Runnable runnable, long delay) {
		long taskID = instance.getNextAsyncID();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				if (runnable != null)
					runnable.run();
				instance.asyncTasks.remove(taskID);
			}
			
		};
		new Timer().schedule(task, delay < 0 ? 0 : delay);
		instance.asyncTasks.put(taskID, task);
		return taskID;
	}
	
	/**
	 * Schedules a synchronous task on the main thread. Might impact performance
	 * with heavy tasks: use {@link #scheduleAsync(Runnable, long, long)} if possible.
	 * Note that synchronous tasks are available on ChatPlugin's Bukkit
	 * and Sponge implementations only.
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @param period Period to wait between executions, in milliseconds
	 * @return Task's ID
	 * @throws UnsupportedOperationException If {@link Environment#isProxy()}
	 */
	public static long scheduleSync(Runnable runnable, long delay, long period) {
		if (Environment.isProxy())
			throw new UnsupportedOperationException("Synchronous tasks are not available on a " + Environment.getCurrent().getName() + " environment. Bukkit and Sponge only");
		long taskID = Environment.isBukkit() ? (long) BukkitScheduler.scheduleSync(runnable, delay, period) : instance.currentSpongeID++;
		
		instance.syncTasks.put(taskID, Environment.isBukkit() ? UUID.randomUUID() : Sponge.getScheduler().createTaskBuilder().execute(runnable).delayTicks((delay < 0 ? 0 : delay) / 50).intervalTicks(period / 50).submit(SpongeBootstrapper.getInstance()).getUniqueId());
		return taskID;
	}
	
	/**
	 * Schedules an asynchronous task on a new thread.
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @param period Period to wait between executions, in milliseconds
	 * @return Task's ID
	 */
	public static long scheduleAsync(Runnable runnable, long delay, long period) {
		long taskID = instance.getNextAsyncID();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				if (runnable != null)
					runnable.run();
			}
			
		};
		new Timer().schedule(task, delay < 0 ? 0 : delay, period);
		instance.asyncTasks.put(taskID, task);
		return taskID;
	}
	
	/**
	 * Cancels a synchronous task. If the specified ID does not belong to any
	 * existing task, nothing will happen. If the task has not run yet, calling
	 * this method will prevent it from running and if it is a repeating task
	 * (obtained with {@link #scheduleSync(Runnable, long, long)})
	 * and it has run already, calling this method will prevent it from running again.
	 * Also, if the task is running in this moment, it will run to completion,
	 * but will not be executed again.
	 * 
	 * @param id Task's ID
	 * @see #getSyncTasks()
	 */
	public static void cancelSync(long id) {
		if (instance.syncTasks.containsKey(id)) {
			if (Environment.isBukkit())
				Bukkit.getScheduler().cancelTask((int) id);
			else Sponge.getScheduler().getTaskById(instance.syncTasks.get(id)).get().cancel();
			instance.syncTasks.remove(id);
		}
	}
	
	/**
	 * Cancels an asynchronous task. If the specified ID does not belong to any
	 * existing task, nothing will happen. If the task has not run yet, calling
	 * this method will prevent it from running and if it is a repeating task
	 * (obtained with {@link #scheduleAsync(Runnable, long, long)})
	 * and it has run already, calling this method will prevent it from running again.
	 * Also, if the task is running in this moment, it will run to completion,
	 * but will not be executed again.
	 * 
	 * @param id Task's ID
	 * @see #getAsyncTasks()
	 */
	public static void cancelAsync(long id) {
		if (instance.asyncTasks.containsKey(id)) {
			instance.asyncTasks.get(id).cancel();
			instance.asyncTasks.remove(id);
		}
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static TaskManager getInstance() {
		return instance;
	}
	
	private static class BukkitScheduler {
		
		public static int runSync(Runnable runnable, long delay) {
			return Bukkit.getScheduler().runTaskLater(BukkitBootstrapper.getInstance(), runnable, (delay < 0 ? 0 : delay) / 50).getTaskId();
		}
		
		public static int scheduleSync(Runnable runnable, long delay, long period) {
			return Bukkit.getScheduler().runTaskTimer(BukkitBootstrapper.getInstance(), runnable, (delay < 0 ? 0 : delay) / 50, period / 50).getTaskId();
		}
		
	}
	
}
