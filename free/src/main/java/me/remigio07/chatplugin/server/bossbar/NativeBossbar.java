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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.bossbar;

import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarColorAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.bossbar.BossbarStyleAdapter;

public class NativeBossbar extends PlayerBossbar {
	
	private BossbarAdapter bossbar = new BossbarAdapter(player.getName());
	
	public NativeBossbar(ChatPluginServerPlayer player) {
		super(player);
		
		bossbar.addPlayer(player);
	}
	
	@Override
	public String getTitle() {
		return bossbar.getTitle();
	}
	
	@Override
	public void setTitle(String title) {
		bossbar.setTitle(title);
	}
	
	@Override
	public float getProgress() {
		return bossbar.getProgress();
	}
	
	@Override
	public void setProgress(float progress) {
		bossbar.setProgress(progress);
	}
	
	@Override
	public boolean isHidden() {
		return !bossbar.isVisible();
	}
	
	@Override
	public void setHidden(boolean hidden) {
		bossbar.setVisible(!hidden);
	}
	
	@Override
	public void unregister() {
		bossbar.remove();
	}
	
	@Override
	public BossbarColorAdapter getColor() {
		return bossbar.getColor();
	}
	
	@Override
	public void setColor(BossbarColorAdapter color) {
		bossbar.setColor(color);
	}
	
	@Override
	public BossbarStyleAdapter getStyle() {
		return bossbar.getStyle();
	}
	
	@Override
	public void setStyle(BossbarStyleAdapter style) {
		bossbar.setStyle(style);
	}
	
}
