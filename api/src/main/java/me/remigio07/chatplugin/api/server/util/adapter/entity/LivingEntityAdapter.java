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

import org.bukkit.attribute.Attribute;
import org.spongepowered.api.data.key.Keys;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) living entity adapter.
 */
public class LivingEntityAdapter {
	
	private Object livingEntity;
	
	/**
	 * Constructs a living entity adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.entity.LivingEntity} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.entity.living.Living} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param livingEntity Living entity object
	 */
	public LivingEntityAdapter(Object livingEntity) {
		this.livingEntity = livingEntity;
	}
	
	/**
	 * Gets the living entity adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted living entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.entity.LivingEntity bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.entity.LivingEntity) livingEntity;
		else throw new UnsupportedOperationException("Unable to adapt living entity to a Bukkit's LivingEntity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the living entity adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted living entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.entity.living.Living spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.entity.living.Living) livingEntity;
		else throw new UnsupportedOperationException("Unable to adapt living entity to a Sponge's Living on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this living entity's name.
	 * Will return the entity type's name
	 * if a display name is not provided.
	 * 
	 * @return Living entity's name
	 */
	@NotNull
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : Utils.deserializeSpongeText(spongeValue().getOrElse(Keys.DISPLAY_NAME, Utils.serializeSpongeText(spongeValue().getType().getName())));
	}
	
	/**
	 * Gets this living entity's health.
	 * 
	 * @return Living entity's health
	 */
	public double getHealth() {
		return Environment.isBukkit() ? bukkitValue().getHealth() : spongeValue().health().get();
	}
	
	/**
	 * Gets this living entity's max health.
	 * 
	 * @return Living entity's max health
	 */
	public double getMaxHealth() {
		return Environment.isBukkit() ? bukkitValue().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() : spongeValue().maxHealth().get();
	}
	
}
