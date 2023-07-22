/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.common.util.adapter.motd;

import me.remigio07_.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) favicon adapter.
 */
public class FaviconAdapter {
	
	private Object favicon;
	
	/**
	 * Constructs a favicon adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.spongepowered.common.network.status.SpongeFavicon} for Sponge environments</li>
	 * 		<li>{@link net.md_5.bungee.api.Favicon} for BungeeCord environments</li>
	 * 		<li>{@link com.velocitypowered.api.util.Favicon} for Velocity environments</li>
	 * 	</ul>
	 * 
	 * @param player Player object
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
	public org.spongepowered.common.network.status.SpongeFavicon spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.common.network.status.SpongeFavicon) favicon;
		else throw new UnsupportedOperationException("Unable to adapt favicon to a Sponge's SpongeFavicon on a " + Environment.getCurrent().getName() + " environment");
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
		else throw new UnsupportedOperationException("Unable to adapt favicon to a BungeeCord's Favicon on a " + Environment.getCurrent().getName() + " environment");
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
		else throw new UnsupportedOperationException("Unable to adapt favicon to a Velocity's Favicon on a " + Environment.getCurrent().getName() + " environment");
	}
	
}
