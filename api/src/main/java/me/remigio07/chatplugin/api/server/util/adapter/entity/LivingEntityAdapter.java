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

package me.remigio07.chatplugin.api.server.util.adapter.entity;

import java.util.StringJoiner;

import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) living entity adapter.
 */
public class LivingEntityAdapter extends EntityAdapter {
	
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
		super(livingEntity);
	}
	
	@Override
	public org.bukkit.entity.LivingEntity bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.entity.LivingEntity) entity;
		throw new UnsupportedOperationException("Unable to adapt living entity to a Bukkit's LivingEntity on a " + Environment.getCurrent().getName() + " environment");
	}
	
	@Override
	public org.spongepowered.api.entity.living.Living spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.entity.living.Living) entity;
		throw new UnsupportedOperationException("Unable to adapt living entity to a Sponge's Living on a " + Environment.getCurrent().getName() + " environment");
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
	@SuppressWarnings("deprecation")
	public double getMaxHealth() {
		return Environment.isBukkit() ? bukkitValue().getMaxHealth() : spongeValue().maxHealth().get();
	}
	
}
