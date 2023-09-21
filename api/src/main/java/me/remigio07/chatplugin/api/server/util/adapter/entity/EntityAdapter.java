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

package me.remigio07.chatplugin.api.server.util.adapter.entity;

import java.util.UUID;

import org.spongepowered.api.data.key.Keys;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) entity adapter.
 */
public class EntityAdapter {
	
	private Object entity;
	
	/**
	 * Constructs an entity adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.entity.Entity} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.entity.Entity} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param entity Entity object
	 */
	public EntityAdapter(Object entity) {
		this.entity = entity;
	}
	
	/**
	 * Gets the entity adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.entity.Entity bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.entity.Entity) entity;
		else throw new UnsupportedOperationException("Unable to adapt entity to a Bukkit's Entity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the entity adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.entity.Entity spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.entity.Entity) entity;
		else throw new UnsupportedOperationException("Unable to adapt entity to a Sponge's Entity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this entity's name.
	 * Will return the entity type's name
	 * if a display name is not provided.
	 * 
	 * @return Entity's name
	 */
	@NotNull
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : Utils.deserializeSpongeText(spongeValue().getOrElse(Keys.DISPLAY_NAME, Utils.serializeSpongeText(spongeValue().getType().getName())));
	}
	
	/**
	 * Gets this entity's UUID.
	 * 
	 * @return Entity's UUID
	 */
	@NotNull
	public UUID getUUID() {
		return Environment.isBukkit() ? bukkitValue().getUniqueId() : spongeValue().getUniqueId();
	}
	
}
