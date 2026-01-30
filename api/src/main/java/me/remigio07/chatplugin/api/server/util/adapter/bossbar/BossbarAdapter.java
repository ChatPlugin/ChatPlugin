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
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.util.Identifier;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) bossbar adapter.
 */
public class BossbarAdapter {
	
	private Object bossbar;
	private String id;
	
	/**
	 * Constructs a bossbar adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.boss.BossBar} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.boss.ServerBossBar} for Sponge environments</li>
	 * 		<li>{@link net.minecraft.entity.boss.CommandBossBar} for Fabric environments</li>
	 * 	</ul>
	 * 
	 * @param bossbar Bossbar object
	 */
	public BossbarAdapter(Object bossbar) {
		this.bossbar = bossbar;
	}
	
	/**
	 * Constructs a bossbar adapter with given ID.
	 * 
	 * @param id Bossbar's ID
	 */
	public BossbarAdapter(String id) {
		this.id = id;
		bossbar = Environment.isBukkit() ? BukkitBossbar.get(id)
				: Environment.isSponge() ? SpongeBossbar.get()
				: FabricBootstrapper.getInstance().getServer().getBossBarManager().add(Identifier.tryParse("chatplugin:" + id.toLowerCase()), Utils.toFabricComponent(""));
	}
	
	@Override
	public String toString() {
		return "BossbarAdapter{id=" + (id == null ? id : "\"" + id + "\"") + "}";
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
		throw new UnsupportedOperationException("Unable to adapt bossbar to a Bukkit's ServerBossBar on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt bossbar to a Sponge's ServerBossBar on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted bossbar
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public CommandBossBar fabricValue() {
		if (Environment.isFabric())
			return (CommandBossBar) bossbar;
		throw new UnsupportedOperationException("Unable to adapt bossbar to a Fabric's CommandBossBar on a " + Environment.getCurrent().getName() + " environment");
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
		return Environment.isBukkit() ? bukkitValue().getTitle() : Environment.isSponge() ? Utils.toLegacyText(spongeValue().getName()) : Utils.toLegacyText(fabricValue().getName());
	}
	
	/**
	 * Sets this bossbar's title.
	 * 
	 * @param title Bossbar's title
	 */
	public void setTitle(@NotNull String title) {
		if (Environment.isBukkit())
			bukkitValue().setTitle(title);
		else if (Environment.isSponge())
			spongeValue().setName(Utils.toSpongeComponent(title));
		else fabricValue().setName(Utils.toFabricComponent(title));
	}
	
	/**
	 * Gets this bossbar's progress.
	 * 
	 * @return Bossbar's progress (0.0 - 1.0)
	 */
	public float getProgress() {
		return Environment.isBukkit() ? (float) bukkitValue().getProgress() : Environment.isSponge() ? spongeValue().getPercent() : fabricValue().getPercent();
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
		else if (Environment.isSponge())
			spongeValue().setPercent(progress);
		else fabricValue().setPercent(progress);
	}
	
	/**
	 * Checks if this bossbar is visible.
	 * 
	 * @return Whether this bossbar is visible
	 */
	public boolean isVisible() {
		return Environment.isBukkit() ? bukkitValue().isVisible() : Environment.isSponge() ? spongeValue().isVisible() : fabricValue().isVisible();
	}
	
	/**
	 * Sets whether this bossbar should be visible.
	 * 
	 * @param visible Whether the bossbar should be visible
	 */
	public void setVisible(boolean visible) {
		if (Environment.isBukkit())
			bukkitValue().setVisible(visible);
		else if (Environment.isSponge())
			spongeValue().setVisible(visible);
		else fabricValue().setVisible(visible);
	}
	
	/**
	 * Gets this bossbar's color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Bossbar's color
	 */
	@NotNull
	public BossbarColorAdapter getColor() {
		return BossbarColorAdapter.value(Environment.isBukkit() ? bukkitValue().getColor().name() : Environment.isSponge() ? spongeValue().getColor().getId() : fabricValue().getColor().name());
	}
	
	/**
	 * Sets this bossbar's color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @param color Bossbar's color
	 */
	public void setColor(@NotNull BossbarColorAdapter color) {
		if (Environment.isBukkit())
			bukkitValue().setColor(color.bukkitValue());
		else if (Environment.isSponge())
			spongeValue().setColor(color.spongeValue());
		else fabricValue().setColor(color.fabricValue());
	}
	
	/**
	 * Gets this bossbar's style.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Bossbar's style
	 */
	@NotNull
	public BossbarStyleAdapter getStyle() {
		return BossbarStyleAdapter.value(Environment.isBukkit() ? bukkitValue().getStyle().name() : Environment.isSponge() ? spongeValue().getOverlay().getId() : fabricValue().getStyle().name());
	}
	
	/**
	 * Sets this bossbar's style.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @param style Bossbar's style
	 */
	public void setStyle(@NotNull BossbarStyleAdapter style) {
		if (Environment.isBukkit())
			bukkitValue().setStyle(style.bukkitValue());
		else if (Environment.isSponge())
			spongeValue().setOverlay(style.spongeValue());
		else fabricValue().setStyle(style.fabricValue());
	}
	
	/**
	 * Adds a player to the bossbar's players list.
	 * 
	 * @param player Player to add
	 */
	public void addPlayer(ChatPluginServerPlayer player) {
		if (Environment.isBukkit())
			bukkitValue().addPlayer(player.toAdapter().bukkitValue());
		else if (Environment.isSponge())
			spongeValue().addPlayer(player.toAdapter().spongeValue());
		else fabricValue().addPlayer(player.toAdapter().fabricValue());
	}
	
	/**
	 * Remove a player from the bossbar's players list.
	 * 
	 * @param player Player to remove
	 */
	public void removePlayer(ChatPluginServerPlayer player) {
		if (Environment.isBukkit())
			bukkitValue().removePlayer(player.toAdapter().bukkitValue());
		else if (Environment.isSponge())
			spongeValue().removePlayer(player.toAdapter().spongeValue());
		else fabricValue().removePlayer(player.toAdapter().fabricValue());
	}
	
	/**
	 * Removes this bossbar from the server's loaded bossbars.
	 */
	public void remove() {
		if (Environment.isBukkit()) {
			bukkitValue().getPlayers().forEach(player -> bukkitValue().removePlayer(player));
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13_2))
				BukkitBossbar.remove(id);
		} else if (Environment.isFabric()) {
			fabricValue().getPlayers().forEach(player -> fabricValue().removePlayer(player)); // is this required?
			FabricBootstrapper.getInstance().getServer().getBossBarManager().remove(fabricValue());
		} else spongeValue().getPlayers().forEach(player -> spongeValue().removePlayer(player));
	}
	
	private static class BukkitBossbar {
		
		public static BossBar get(String id) {
			return VersionUtils.getVersion().isAtLeast(Version.V1_13_2)
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
