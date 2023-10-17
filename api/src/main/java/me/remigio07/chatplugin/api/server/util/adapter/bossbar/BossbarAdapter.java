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

package me.remigio07.chatplugin.api.server.util.adapter.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) bossbar adapter.
 */
public class BossbarAdapter {
	
	private Object bossbar;
	private String id;
	
	/**
	 * Constructs a bossbar adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.boss.BossBar} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.boss.ServerBossBar} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param bossbar Bossbar object
	 */
	public BossbarAdapter(Object bossbar) {
		this.bossbar = bossbar;
	}
	
	/**
	 * Constructs a bossbar adapter with given ID, color and style.
	 * 
	 * @param id Bossbar's ID
	 */
	public BossbarAdapter(String id) {
		this.id = id;
		bossbar = Environment.isBukkit() ? BukkitBossbar.get(id) : SpongeBossbar.get();
	}
	
	/**
	 * Gets the bossbar adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted bossbar
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public BossBar bukkitValue() {
		if (Environment.isBukkit())
			return (BossBar) bossbar;
		else throw new UnsupportedOperationException("Unable to adapt bossbar to a Bukkit's ServerBossBar on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted bossbar
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public ServerBossBar spongeValue() {
		if (Environment.isSponge())
			return (ServerBossBar) bossbar;
		else throw new UnsupportedOperationException("Unable to adapt bossbar to a Sponge's ServerBossBar on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this bossbar's ID.
	 * 
	 * <p>Will return <code>null</code> if this adapter was
	 * initialized using {@link #BossbarAdapter(Object)}.</p>
	 * 
	 * @return Bossbar's ID
	 */
	@Nullable(why = "Will return null if the first constructor was used")
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this bossbar's title.
	 * 
	 * @return Bossbar's title
	 */
	@NotNull
	public String getTitle() {
		return Environment.isBukkit() ? bukkitValue().getTitle() : spongeValue().getName().toPlain();
	}
	
	/**
	 * Sets this bossbar's title.
	 * 
	 * @param title Bossbar's title
	 */
	public void setTitle(@NotNull String title) {
		if (Environment.isBukkit())
			bukkitValue().setTitle(title);
		else spongeValue().setName(Utils.serializeSpongeText(title, false));
	}
	
	/**
	 * Gets this bossbar's progress.
	 * 
	 * @return Bossbar's progress (0.0 - 1.0)
	 */
	public float getProgress() {
		return (float) (Environment.isBukkit() ? bukkitValue().getProgress() : spongeValue().getPercent());
	}
	
	/**
	 * Sets this bossbar's progress.
	 * 
	 * @param progress Bossbar's progress (0.0 - 1.0)
	 */
	public void setProgress(float progress) {
		progress = progress < 0 ? 0 : progress > 1 ? 1 : progress;
		
		if (Environment.isBukkit())
			bukkitValue().setProgress(progress);
		else spongeValue().setPercent(progress);
	}
	
	/**
	 * Checks if this bossbar is visible.
	 * 
	 * @return Whether this bossbar is visible
	 */
	public boolean isVisible() {
		return Environment.isBukkit() ? bukkitValue().isVisible() : spongeValue().isVisible();
	}
	
	/**
	 * Sets whether this bossbar should be visible.
	 * 
	 * @param visible Whether the bossbar should be visible
	 */
	public void setVisible(boolean visible) {
		if (Environment.isBukkit())
			bukkitValue().setVisible(visible);
		else spongeValue().setVisible(visible);
	}
	
	/**
	 * Gets this bossbar's color.
	 * 
	 * @return Bossbar's color
	 */
	@NotNull
	public BossbarColorAdapter getColor() {
		return BossbarColorAdapter.valueOf(Environment.isBukkit() ? bukkitValue().getColor().name() : spongeValue().getColor().getId());
	}
	
	/**
	 * Sets this bossbar's color.
	 * 
	 * @param color Bossbar's color
	 */
	public void setColor(@NotNull BossbarColorAdapter color) {
		if (Environment.isBukkit())
			bukkitValue().setColor(color.bukkitValue());
		else spongeValue().setColor(color.spongeValue());
	}
	
	/**
	 * Gets this bossbar's style.
	 * 
	 * @return Bossbar's style
	 */
	@NotNull
	public BossbarStyleAdapter getStyle() {
		return BossbarStyleAdapter.valueOf(Environment.isBukkit() ? bukkitValue().getStyle().name() : spongeValue().getOverlay().getId());
	}
	
	/**
	 * Sets this bossbar's style.
	 * 
	 * @param style Bossbar's style
	 */
	public void setStyle(@NotNull BossbarStyleAdapter style) {
		if (Environment.isBukkit())
			bukkitValue().setStyle(style.bukkitValue());
		else spongeValue().setOverlay(style.spongeValue());
	}
	
	/**
	 * Adds a player to the bossbar's players list.
	 * 
	 * @param player Player to add
	 */
	public void addPlayer(ChatPluginServerPlayer player) {
		if (Environment.isBukkit())
			bukkitValue().addPlayer(player.toAdapter().bukkitValue());
		else spongeValue().addPlayer(player.toAdapter().spongeValue());
	}
	
	/**
	 * Remove a player from the bossbar's players list.
	 * 
	 * @param player Player to remove
	 */
	public void removePlayer(ChatPluginServerPlayer player) {
		if (Environment.isBukkit())
			bukkitValue().removePlayer(player.toAdapter().bukkitValue());
		else spongeValue().removePlayer(player.toAdapter().spongeValue());
	}
	
	/**
	 * Removes this bossbar from the server's loaded bossbars.
	 */
	public void remove() {
		if (Environment.isBukkit()) {
			bukkitValue().getPlayers().forEach(player -> bukkitValue().removePlayer(player));
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13))
				BukkitBossbar.remove(id);
		} else spongeValue().getPlayers().forEach(player -> spongeValue().removePlayer(player));
	}
	
	private static class BukkitBossbar {
		
		public static BossBar get(String id) {
			return VersionUtils.getVersion().isAtLeast(Version.V1_13)
					? Bukkit.createBossBar(new NamespacedKey(BukkitBootstrapper.getInstance(), id), "", BarColor.PINK, BarStyle.SOLID)
					: Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
		}
		
		public static void remove(String id) {
			NamespacedKey key = new NamespacedKey(BukkitBootstrapper.getInstance(), id);
			
			if (Bukkit.getBossBar(key) != null)
				Bukkit.removeBossBar(key);
		}
		
	}
	
	private static class SpongeBossbar {
		
		public static ServerBossBar get() {
			return ServerBossBar.builder()
					.name(Text.EMPTY)
					.color(BossBarColors.PINK)
					.overlay(BossBarOverlays.PROGRESS)
					.build();
		}
		
	}
	
}
