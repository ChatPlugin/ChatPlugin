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

package me.remigio07.chatplugin.api.server.util.adapter.scoreboard;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) team adapter.
 */
public class TeamAdapter {
	
	private Object team;
	
	/**
	 * Constructs a team adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.scoreboard.Team} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.scoreboard.Team} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param team Team object
	 */
	public TeamAdapter(Object team) {
		this.team = team;
	}
	
	/**
	 * Gets the team adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted team
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.scoreboard.Team bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.scoreboard.Team) team;
		else throw new UnsupportedOperationException("Unable to adapt team to a Bukkit's Team on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the team adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted team
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.scoreboard.Team spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.scoreboard.Team) team;
		else throw new UnsupportedOperationException("Unable to adapt team to a Sponge's Team on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this team's name.
	 * 
	 * @return Team's name
	 */
	@NotNull
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : spongeValue().getName();
	}
	
	/**
	 * Gets this team's prefix.
	 * 
	 * @return Team's prefix
	 */
	@NotNull
	public String getPrefix() {
		return Environment.isBukkit() ? bukkitValue().getPrefix() : spongeValue().getPrefix().toPlain();
	}
	
	/**
	 * Gets this team's suffix.
	 * 
	 * @return Team's suffix
	 */
	@NotNull
	public String getSuffix() {
		return Environment.isBukkit() ? bukkitValue().getSuffix() : spongeValue().getSuffix().toPlain();
	}
	
	/**
	 * Sets this team's prefix.
	 * 
	 * @param prefix Team's prefix
	 */
	public void setPrefix(@NotNull String prefix) {
		if (Environment.isBukkit())
			bukkitValue().setPrefix(prefix);
		else spongeValue().setPrefix(Utils.serializeSpongeText(prefix));
	}
	
	/**
	 * Sets this team's suffix.
	 * 
	 * @param suffix Team's suffix
	 */
	public void setSuffix(@NotNull String suffix) {
		if (Environment.isBukkit())
			bukkitValue().setSuffix(suffix);
		else spongeValue().setSuffix(Utils.serializeSpongeText(suffix));
	}
	
}
