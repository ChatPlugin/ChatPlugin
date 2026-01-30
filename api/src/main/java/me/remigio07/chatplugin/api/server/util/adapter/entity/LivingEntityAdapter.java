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

package me.remigio07.chatplugin.api.server.util.adapter.entity;

import java.util.StringJoiner;
import java.util.UUID;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.entity.LivingEntity;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) living entity adapter.
 */
public class LivingEntityAdapter {
	
	private Object livingEntity;
	
	/**
	 * Constructs a living entity adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.entity.LivingEntity} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.entity.living.Living} for Sponge environments</li>
	 * 		<li>{@link net.minecraft.entity.LivingEntity} for Fabric environments</li>
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
		throw new UnsupportedOperationException("Unable to adapt living entity to a Bukkit's LivingEntity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the living entity adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted living entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public Living spongeValue() {
		if (Environment.isSponge())
			return (Living) livingEntity;
		throw new UnsupportedOperationException("Unable to adapt living entity to a Sponge's Living on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the living entity adapted for Fabric environments.
	 * 
	 * @return Sponge-adapted living entity
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public LivingEntity fabricValue() {
		if (Environment.isFabric())
			return (LivingEntity) livingEntity;
		throw new UnsupportedOperationException("Unable to adapt living entity to a Sponge's LivingEntity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "LivingEntityAdapter{", "}")
				.add("uuid=" + getUUID().toString())
				.add("name=\"" + getName() + "\"")
				.add("health=" + getHealth())
				.add("maxHealth=" + getMaxHealth())
				.toString();
	}
	
	/**
	 * Gets this living entity's UUID.
	 * 
	 * @return Living entity's UUID
	 */
	@NotNull
	public UUID getUUID() {
		return Environment.isBukkit() ? bukkitValue().getUniqueId() : Environment.isSponge() ? spongeValue().getUniqueId() : fabricValue().getUuid();
	}
	
	/**
	 * Gets this living entity's name.
	 * 
	 * <p>Will return the entity type's name
	 * if a display name is not provided.</p>
	 * 
	 * @return Living entity's name
	 */
	@NotNull
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : Environment.isSponge() ? Utils.toLegacyText(spongeValue().getOrElse(Keys.DISPLAY_NAME, Utils.toSpongeComponent(spongeValue().getType().getName()))) : Utils.toLegacyText(fabricValue().getDisplayName());
	}
	
	/**
	 * Gets this living entity's health.
	 * 
	 * @return Living entity's health
	 */
	public double getHealth() {
		return Environment.isBukkit() ? bukkitValue().getHealth() : Environment.isSponge() ? spongeValue().health().get() : fabricValue().getHealth();
	}
	
	/**
	 * Gets this living entity's max health.
	 * 
	 * @return Living entity's max health
	 */
	@SuppressWarnings("deprecation")
	public double getMaxHealth() {
		return Environment.isBukkit() ? bukkitValue().getMaxHealth() : Environment.isSponge() ? spongeValue().maxHealth().get() : fabricValue().getMaxHealth();
	}
	
}
