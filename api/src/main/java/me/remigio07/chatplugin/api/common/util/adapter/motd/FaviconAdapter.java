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

package me.remigio07.chatplugin.api.common.util.adapter.motd;

import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) favicon adapter.
 */
public class FaviconAdapter {
	
	private Object favicon;
	
	/**
	 * Constructs a favicon adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.spongepowered.api.network.status.Favicon} for Sponge environments</li>
	 * 		<li>{@link net.md_5.bungee.api.Favicon} for BungeeCord environments</li>
	 * 		<li>{@link com.velocitypowered.api.util.Favicon} for Velocity environments</li>
	 * 	</ul>
	 * 
	 * @param favicon Favicon object
	 */
	public FaviconAdapter(Object favicon) {
		this.favicon = favicon;
	}
	
	/**
	 * Gets the favicon adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted favicon
	 * @throws UnsupportedOperationException If !{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.network.status.Favicon spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.network.status.Favicon) favicon;
		throw new UnsupportedOperationException("Unable to adapt favicon to a Sponge's Favicon on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the favicon adapted for BungeeCord environments.
	 * 
	 * @return BungeeCord-adapted favicon
	 * @throws UnsupportedOperationException If !{@link Environment#isBungeeCord()}
	 */
	public net.md_5.bungee.api.Favicon bungeeCordValue() {
		if (Environment.isBungeeCord())
			return (net.md_5.bungee.api.Favicon) favicon;
		throw new UnsupportedOperationException("Unable to adapt favicon to a BungeeCord's Favicon on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the favicon adapted for Velocity environments.
	 * 
	 * @return Velocity-adapted favicon
	 * @throws UnsupportedOperationException If !{@link Environment#isVelocity()}
	 */
	public com.velocitypowered.api.util.Favicon velocityValue() {
		if (Environment.isVelocity())
			return (com.velocitypowered.api.util.Favicon) favicon;
		throw new UnsupportedOperationException("Unable to adapt favicon to a Velocity's Favicon on a " + Environment.getCurrent().getName() + " environment");
	}
	
}
