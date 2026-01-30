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

package me.remigio07.chatplugin.api.server.util.adapter.scoreboard;

import org.bukkit.scoreboard.Objective;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.scoreboard.ScoreboardObjective;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) objective adapter.
 */
public class ObjectiveAdapter {

	private Object objective;
	
	/**
	 * Constructs an objective adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.scoreboard.Objective} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.scoreboard.objective.Objective} for Sponge environments</li>
	 * 		<li>{@link net.minecraft.scoreboard.ScoreboardObjective} for Fabric environments</li>
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
	public Objective bukkitValue() {
		if (Environment.isBukkit())
			return (Objective) objective;
		throw new UnsupportedOperationException("Unable to adapt objective to a Bukkit's Objective on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt objective to a Sponge's Objective on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the objective adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted objective
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public ScoreboardObjective fabricValue() {
		if (Environment.isFabric())
			return (ScoreboardObjective) objective;
		throw new UnsupportedOperationException("Unable to adapt objective to a Fabric's ScoreboardObjective on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this objective's display name.
	 * 
	 * @return Objective's display name
	 */
	@NotNull
	public String getDisplayName() {
		return Environment.isBukkit() ? bukkitValue().getDisplayName() : Environment.isSponge() ? Utils.toLegacyText(spongeValue().getDisplayName()) : Utils.toLegacyText(fabricValue().getDisplayName());
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
		else if (Environment.isSponge())
			spongeValue().setDisplayName(Utils.toSpongeComponent(displayName));
		else fabricValue().setDisplayName(Utils.toFabricComponent(displayName));
		return this;
	}
	
}
