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

package me.remigio07.chatplugin.api.server.bossbar;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarColorAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarStyleAdapter;

/**
 * Represents a {@link ChatPluginServerPlayer}'s displayed bossbar.
 */
public abstract class PlayerBossbar {
	
	protected ChatPluginServerPlayer player;
	
	protected PlayerBossbar(ChatPluginServerPlayer player) {
		this.player = player;
	}
	
	/**
	 * Gets the player this bossbar is being displayed to.
	 * 
	 * @return Bossbar's player
	 */
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets this bossbar's color.
	 * 
	 * @return Bossbar's color
	 */
	public BossbarColorAdapter getColor() {
		return BossbarColorAdapter.PINK;
	}
	
	/**
	 * Sets this bossbar's color.
	 * 
	 * @param color Color to set
	 * @throws UnsupportedOperationException If this method is called on a reflection bossbar
	 */
	public void setColor(BossbarColorAdapter color) {
		throw new UnsupportedOperationException("Unable to set the bossbar's color as it requires Minecraft 1.9+");
	}
	
	/**
	 * Gets this bossbar's style.
	 * 
	 * @return Bossbar's style
	 */
	public BossbarStyleAdapter getStyle() {
		return BossbarStyleAdapter.SOLID;
	}
	
	/**
	 * Sets this bossbar's style.
	 * 
	 * @param style Style to set
	 * @throws UnsupportedOperationException If this method is called on a reflection bossbar
	 */
	public void setStyle(BossbarStyleAdapter style) {
		throw new UnsupportedOperationException("Unable to set the bossbar's style as it requires Minecraft 1.9+");
	}
	
	/**
	 * Gets this bossbar's title.
	 * 
	 * @return Bossbar's title
	 */
	public abstract String getTitle();
	
	/**
	 * Sets this bossbar's title.
	 * 
	 * @param title Title to set
	 */
	public abstract void setTitle(String title);
	
	/**
	 * Gets this bossbar's progress.
	 * 
	 * @return Bossbar's progress (0.0 - 1.0)
	 */
	public abstract float getProgress();
	
	/**
	 * Sets this bossbar's progress.
	 * 
	 * @param progress Bossbar's progress (0.0 - 1.0)
	 */
	public abstract void setProgress(float progress);
	
	/**
	 * Checks if this bossbar is hidden.
	 * 
	 * @return Whether this bossbar is hidden
	 */
	public abstract boolean isHidden();
	
	/**
	 * Sets if this bossbar should be hidden.
	 * 
	 * @param hidden Whether this bossbar should be hidden
	 */
	public abstract void setHidden(boolean hidden);
	
	/**
	 * Unregisters this bossbar.
	 */
	public abstract void unregister();
	
}
