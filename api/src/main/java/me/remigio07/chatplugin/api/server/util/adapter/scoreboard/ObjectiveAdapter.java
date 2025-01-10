/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

package me.remigio07.chatplugin.api.server.util.adapter.scoreboard;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) objective adapter.
 */
public class ObjectiveAdapter {

	private Object objective;
	
	/**
	 * Constructs an objective adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.scoreboard.Objective} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.scoreboard.objective.Objective} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param objective Objective object
	 */
	public ObjectiveAdapter(Object objective) {
		this.objective = objective;
	}
	
	/**
	 * Gets the objective adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted objective
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.scoreboard.Objective bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.scoreboard.Objective) objective;
		else throw new UnsupportedOperationException("Unable to adapt objective to a Bukkit's Objective on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the objective adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted objective
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.scoreboard.objective.Objective spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.scoreboard.objective.Objective) objective;
		else throw new UnsupportedOperationException("Unable to adapt objective to a Sponge's Objective on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this objective's display name.
	 * 
	 * @return Objective's display name
	 */
	@NotNull
	public String getDisplayName() {
		return Environment.isBukkit() ? bukkitValue().getDisplayName() : Utils.deserializeSpongeText(spongeValue().getDisplayName());
	}
	
	/**
	 * Sets this objective's display name.
	 * 
	 * @param displayName Objective's display name
	 * @return This objective
	 */
	public ObjectiveAdapter setDisplayName(@NotNull String displayName) {
		if (Environment.isBukkit())
			bukkitValue().setDisplayName(displayName);
		else spongeValue().setDisplayName(Utils.serializeSpongeText(displayName, false));
		return this;
	}
	
}
