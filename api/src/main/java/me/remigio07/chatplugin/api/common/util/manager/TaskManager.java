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

package me.remigio07.chatplugin.api.common.util.manager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;

/**
 * Manager that handles (a)synchronous tasks executed by ChatPlugin.
 */
public abstract class TaskManager implements ChatPluginManager {
	
	protected static TaskManager instance;
	protected boolean enabled;
	protected ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	protected ExecutorService executor = Executors.newCachedThreadPool();
	protected Map<Long, Object> syncTasks = new ConcurrentHashMap<>(); // Bukkit: -1, Sponge: UUID, Fabric: ScheduledFuture<?> (Void)
	protected Map<Long, ScheduledFuture<?>> asyncTasks = new ConcurrentHashMap<>();
	protected AtomicLong currentSyncID, currentAsyncID = new AtomicLong();
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the asynchronous tasks' scheduler.
	 * 
	 * @return Async tasks' scheduler
	 */
	public ExecutorService getScheduler() {
		return scheduler;
	}
	
	/**
	 * Gets the asynchronous tasks' executor.
	 * 
	 * @return Async tasks' executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
	
	/**
	 * Gets the synchronous tasks' set.
	 * 
	 * @return Sync tasks' set
	 */
	public Set<Long> getSyncTasks() {
		return syncTasks.keySet();
	}
	
	/**
	 * Gets the asynchronous tasks' map.
	 * 
	 * @return Async tasks' map
	 */
	public Map<Long, ScheduledFuture<?>> getAsyncTasks() {
		return asyncTasks;
	}
	
	/**
	 * Gets the current sync task's ID.
	 * 
	 * <p>Will return <code>null</code> if called when
	 * <code>!{@link Environment#isSponge()} &amp;&amp; !{@link Environment#isFabric()}</code>.</p>
	 * 
	 * @return Current sync task's ID
	 */
	@Nullable(why = "Null if !Environment#isSponge && !Environment.isFabric()")
	public AtomicLong getCurrentSyncID() {
		return currentSyncID;
	}
	
	/**
	 * Gets the current async task's ID.
	 * 
	 * @return Current async task's ID
	 */
	public AtomicLong getCurrentAsyncID() {
		return currentAsyncID;
	}
	
	/**
	 * Runs a synchronous task on the main thread.
	 * 
	 * <p>Might impact performance with heavy tasks: use
	 * {@link #runAsync(Runnable, long)} if possible.</p>
	 * 
	 * <p><strong>Note:</strong> synchronous tasks are available
	 * on ChatPlugin's Bukkit and Sponge implementations only.</p>
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @return Task's ID
	 * @throws UnsupportedOperationException If {@link Environment#isProxy()}
	 */
	public static long runSync(@NotNull Runnable runnable, long delay) {
		if (Environment.isProxy())
			throw new UnsupportedOperationException("Synchronous tasks are not available on a " + Environment.getCurrent().getName() + " environment");
		delay = delay < 0 ? 0 : delay;
		long taskID = Environment.isBukkit() ? (long) BukkitScheduler.runSync(runnable, delay) : instance.currentSyncID.getAndIncrement();
		
		instance.syncTasks.put(taskID, Environment.isBukkit()
				? -1
				: Environment.isSponge()
				? Sponge.getScheduler().createTaskBuilder().execute(runnable).delayTicks(delay / 50).submit(SpongeBootstrapper.getInstance()).getUniqueId()
				: instance.scheduler.schedule(() -> FabricBootstrapper.getInstance().getServer().execute(runnable), delay, TimeUnit.MILLISECONDS)
				);
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
	public static long runAsync(@NotNull Runnable runnable, long delay) {
		long taskID = instance.currentAsyncID.getAndIncrement();
		
		instance.asyncTasks.put(taskID, instance.scheduler.schedule(() -> instance.executor.execute(() -> {
			try {
				runnable.run();
			} catch (Throwable t) {
				LogManager.log("{0} occurred while executing an asynchronous task (delay: {1} ms): {2}", 2, t.getClass().getSimpleName(), delay, t.getLocalizedMessage());
				t.printStackTrace();
			} instance.asyncTasks.remove(taskID);
		}), delay < 0 ? 0 : delay, TimeUnit.MILLISECONDS));
		return taskID;
	}
	
	/**
	 * Schedules a synchronous repeating task (fixed delay) on the main thread.
	 * 
	 * <p>Might impact performance with heavy tasks: use
	 * {@link #scheduleAsync(Runnable, long, long)} if possible.</p>
	 * 
	 * <p><strong>Note:</strong> synchronous tasks are available
	 * on ChatPlugin's Bukkit and Sponge implementations only.</p>
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @param period Period to wait between executions, in milliseconds
	 * @return Task's ID
	 * @throws UnsupportedOperationException If {@link Environment#isProxy()}
	 */
	public static long scheduleSync(@NotNull Runnable runnable, long delay, long period) {
		if (Environment.isProxy())
			throw new UnsupportedOperationException("Synchronous tasks are not available on a " + Environment.getCurrent().getName() + " environment");
		delay = delay < 0 ? 0 : delay;
		period = period < 1 ? 1 : period;
		long taskID = Environment.isBukkit() ? (long) BukkitScheduler.scheduleSync(runnable, delay, period) : instance.currentSyncID.getAndIncrement();
		
		instance.syncTasks.put(taskID, Environment.isBukkit()
				? -1
				: Environment.isSponge()
				? Sponge.getScheduler().createTaskBuilder().execute(runnable).delayTicks(delay / 50).intervalTicks(period / 50).submit(SpongeBootstrapper.getInstance()).getUniqueId()
				: instance.scheduler.scheduleAtFixedRate(() -> FabricBootstrapper.getInstance().getServer().execute(runnable), delay, period, TimeUnit.MILLISECONDS));
		return taskID;
	}
	
	/**
	 * Schedules an asynchronous repeating task (fixed delay) on a new thread.
	 * 
	 * @param runnable Task to run
	 * @param delay Delay to wait for first execution, in milliseconds
	 * @param period Period to wait between executions, in milliseconds
	 * @return Task's ID
	 */
	public static long scheduleAsync(@NotNull Runnable runnable, long delay, long period) {
		long taskID = instance.currentAsyncID.getAndIncrement();
		
		instance.asyncTasks.put(taskID, instance.scheduler.scheduleWithFixedDelay(() -> instance.executor.execute(() -> {
			try {
				runnable.run();
			} catch (Throwable t) {
				LogManager.log("{0} occurred while executing an asynchronous repeating task (delay: {1} ms, period: {2} ms): {3}", 2, t.getClass().getSimpleName(), delay, period, t.getLocalizedMessage());
				t.printStackTrace();
			}
		}), delay < 0 ? 0 : delay, period < 1 ? 1 : period, TimeUnit.MILLISECONDS));
		return taskID;
	}
	
	/**
	 * Cancels a synchronous task.
	 * 
	 * <p>If the specified ID does not belong to any
	 * existing task, nothing will happen. If the task has not run yet, calling
	 * this method will prevent it from running and if it is a repeating task
	 * (obtained with {@link #scheduleSync(Runnable, long, long)})
	 * and it has run already, calling this method will prevent it from running again.
	 * Also, if the task is running in this moment, it will run to completion,
	 * but will not be executed again.</p>
	 * 
	 * @param id Task's ID
	 * @see #getSyncTasks()
	 */
	public static void cancelSync(long id) {
		if (instance.syncTasks.containsKey(id)) {
			if (Environment.isBukkit())
				Bukkit.getScheduler().cancelTask((int) id);
			else if (Environment.isSponge())
				Sponge.getScheduler().getTaskById((UUID) instance.syncTasks.get(id)).get().cancel();
			else ((ScheduledFuture<?>) instance.syncTasks.get(id)).cancel(false);
			instance.syncTasks.remove(id);
		}
	}
	
	/**
	 * Cancels an asynchronous task.
	 * 
	 * <p>If the specified ID does not belong to any
	 * existing task, nothing will happen. If the task has not run yet, calling
	 * this method will prevent it from running and if it is a repeating task
	 * (obtained with {@link #scheduleAsync(Runnable, long, long)})
	 * and it has run already, calling this method will prevent it from running again.
	 * Also, if the task is running in this moment, it will run to completion,
	 * but will not be executed again.</p>
	 * 
	 * @param id Task's ID
	 * @see #getAsyncTasks()
	 */
	public static void cancelAsync(long id) {
		if (instance.asyncTasks.containsKey(id))
			instance.asyncTasks.remove(id).cancel(false);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static TaskManager getInstance() {
		return instance;
	}
	
	protected static class BukkitScheduler {
		
		public static int runSync(Runnable runnable, long delay) {
			return Bukkit.getScheduler().runTaskLater(BukkitBootstrapper.getInstance(), runnable, (delay < 0 ? 0 : delay) / 50).getTaskId();
		}
		
		public static int scheduleSync(Runnable runnable, long delay, long period) {
			return Bukkit.getScheduler().runTaskTimer(BukkitBootstrapper.getInstance(), runnable, (delay < 0 ? 0 : delay) / 50, (period < 1 ? 1 : period) / 50).getTaskId();
		}
		
		public static void cancelTasks() {
			Bukkit.getScheduler().cancelTasks(BukkitBootstrapper.getInstance());
		}
		
	}
	
}
