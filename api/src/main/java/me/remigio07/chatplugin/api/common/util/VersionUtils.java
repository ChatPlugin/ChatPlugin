/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.common.util;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import com.velocitypowered.api.network.ProtocolVersion;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.proxy.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;

/**
 * Util class used to grab information
 * about the current environment's version.
 */
public class VersionUtils {
	
	private static Version version;
	private static String implementationVersion, implementationName, nmsVersion = "N/A";
	
	/**
	 * Initializes this class.
	 * 
	 * @deprecated Internal use only.
	 * @throws ChatPluginManagerException If something goes wrong.
	 */
	public static void initVersionUtils() throws ChatPluginManagerException {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			version = Version.getVersion(Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf('-')));
			nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			implementationVersion = Bukkit.getVersion();
			implementationName = isPaper() ? "Paper" : isSpigot() ? "Spigot" : "Bukkit";
			break;
		case SPONGE:
			version = Version.getVersion(Sponge.getPlatform().getMinecraftVersion().getName());
			implementationVersion = Sponge.getPluginManager().getPlugin("spongeapi").get().getVersion().get();
			implementationName = "Sponge";
			break;
		case BUNGEECORD:
			String str = ((String) Utils.invokeBungeeCordMethod("getVersion", null)).substring(15).replace(':', '-');
			version = Version.getVersion(str.substring(str.indexOf('-') + 1, str.indexOf('-', str.indexOf('.'))));
			implementationVersion = (String) Utils.invokeBungeeCordMethod("getVersion", null);
			implementationName = isFlameCord() ? "FlameCord" : isWaterfall() ? "Waterfall" : "BungeeCord";
			break;
		case VELOCITY:
			version = Version.getVersion(ProtocolVersion.MAXIMUM_VERSION.getName());
			implementationVersion = VelocityBootstrapper.getInstance().getProxy().getVersion().getVersion();
			implementationName = "Velocity";
			break;
		} if (version == Version.UNSUPPORTED || version.isOlderThan(Version.V1_8))
			throw new ChatPluginManagerException("version utils", "This version is not supported. Is ChatPlugin up to date? Compatible versions: 1.8-{0}. Note: snapshots, pre-releases and release candidates are not supported", Version.values()[Version.values().length - 1].getName());
	}
	
	/**
	 * Gets the current environment's version.
	 * 
	 * @return Environment's version
	 */
	public static Version getVersion() {
		return version;
	}
	
	/**
	 * Gets the current environment's NMS version.
	 * 
	 * <p>It only applies to Bukkit environments.</p>
	 * 
	 * @return Environment's NMS version
	 */
	public static String getNMSVersion() {
		return nmsVersion;
	}
	
	/**
	 * Gets the current environment's implementation's version.
	 * 
	 * @return Environment's implementation's version
	 */
	public static String getImplementationVersion() {
		return implementationVersion;
	}
	
	/**
	 * Gets the current environment's implementation's name.
	 * 
	 * @return Environment's implementation's name
	 */
	public static String getImplementationName() {
		return implementationName;
	}
	
	/**
	 * Checks if this is a Spigot environment.
	 * 
	 * @return Whether Spigot is running
	 */
	public static boolean isSpigot() {
		try {
			Class.forName("org.bukkit.entity.Player$Spigot");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Checks if this is a Paper environment.
	 * 
	 * @return Whether Paper is running
	 */
	public static boolean isPaper() {
		try {
			Class.forName((version.isAtLeast(Version.V1_9) ? "com.destroystokyo.paper" : "org.github.paperspigot") + ".ServerSchedulerReportingWrapper");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Checks if this is a Waterfall environment.
	 * 
	 * @return Whether Waterfall is running
	 */
	public static boolean isWaterfall() {
		try {
			Class.forName("io.github.waterfallmc.waterfall.conf.WaterfallConfiguration");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Checks if this is a FlameCord environment.
	 * 
	 * @return Whether FlameCord is running
	 */
	public static boolean isFlameCord() {
		try {
			Class.forName("dev._2lstudios.flamecord.configuration.FlameCordConfiguration");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Represents a Minecraft version.
	 */
	public enum Version {
		
		/**
		 * Represents an unsupported version.
		 */
		UNSUPPORTED(-1, -1),
		
		// pre-Netty rewrite
		
		/**
		 * Version 1.0.
		 * 
		 * <p><strong>Protocol version number:</strong> 22
		 * <br><strong>Release date:</strong> November 18, 2011
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_0(22, 1321570800000L, "1.0.0/1"),
		
		/**
		 * Version 1.0.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 22
		 * <br><strong>Release date:</strong> November 24, 2011
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_0_1(22, 1322089200000L, "1.0.0/1"),
		
		/**
		 * Version 1.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 23
		 * <br><strong>Release date:</strong> January 12, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_1(23, 1326322800000L),
		
		/**
		 * Version 1.2.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 28
		 * <br><strong>Release date:</strong> March 1, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_2_1(28, 1330556400000L, "1.2.1-1.2.3"),
		
		/**
		 * Version 1.2.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 28
		 * <br><strong>Release date:</strong> March 1, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_2_2(28, 1330556400000L, "1.2.1-1.2.3"),
		
		/**
		 * Version 1.2.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 28
		 * <br><strong>Release date:</strong> March 2, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_2_3(28, 1330642800000L, "1.2.1-1.2.3"),
		
		/**
		 * Version 1.2.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 29
		 * <br><strong>Release date:</strong> March 22, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_2_4(29, 1332370800000L, "1.2.4/5"),
		
		/**
		 * Version 1.2.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 29
		 * <br><strong>Release date:</strong> April 4, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_2_5(29, 1333490400000L, "1.2.4/5"),
		
		/**
		 * Version 1.3.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 39
		 * <br><strong>Release date:</strong> August 1, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_3_1(39, 1343772000000L, "1.3.1/2"),
		
		/**
		 * Version 1.3.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 39
		 * <br><strong>Release date:</strong> August 16, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_3_2(39, 1345068000000L, "1.3.1/2"),
		
		/**
		 * Version 1.4.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> October 25, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_4_2(47, 1351116000000L, "1.4.0-1.4.2"),
		
		/**
		 * Version 1.4.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 49
		 * <br><strong>Release date:</strong> November 14, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_4_4(49, 1352847600000L, "1.4.4/5"),
		
		/**
		 * Version 1.4.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 49
		 * <br><strong>Release date:</strong> November 19, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_4_5(49, 1353279600000L, "1.4.4/5"),
		
		/**
		 * Version 1.4.6.
		 * 
		 * <p><strong>Protocol version number:</strong> 51
		 * <br><strong>Release date:</strong> December 20, 2012
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_4_6(51, 1355958000000L, "1.4.6/7"),
		
		/**
		 * Version 1.4.7.
		 * 
		 * <p><strong>Protocol version number:</strong> 51
		 * <br><strong>Release date:</strong> January 9, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_4_7(51, 1357686000000L, "1.4.6/7"),
		
		/**
		 * Version 1.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 60
		 * <br><strong>Release date:</strong> March 13, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_5(60, 1363129200000L, "1.5.0/1"),
		
		/**
		 * Version 1.5.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 60
		 * <br><strong>Release date:</strong> March 21, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_5_1(60, 1363820400000L, "1.5.0/1"),
		
		/**
		 * Version 1.5.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 61
		 * <br><strong>Release date:</strong> May 2, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_5_2(61, 1367445600000L),
		
		/**
		 * Version 1.6.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 73
		 * <br><strong>Release date:</strong> July 1, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_6_1(73, 1372629600000L),
		
		/**
		 * Version 1.6.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 74
		 * <br><strong>Release date:</strong> July 8, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_6_2(74, 1373234400000L),
		
		/**
		 * Version 1.6.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 78
		 * <br><strong>Release date:</strong> September 19, 2013
		 * <br><strong>Note:</strong> very old - pre-Netty rewrite</p>
		 */
		V1_6_4(78, 1379541600000L),
		
		// post Netty rewrite
		
		/**
		 * Version 1.7.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 4
		 * <br><strong>Release date:</strong> October 25, 2013
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_2(4, 1382652000000L, "1.7.2-1.7.5"),
		
		/**
		 * Version 1.7.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 4
		 * <br><strong>Release date:</strong> December 10, 2013
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_4(4, 1386630000000L, "1.7.2-1.7.5"),
		
		/**
		 * Version 1.7.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 4
		 * <br><strong>Release date:</strong> February 26, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_5(4, 1393369200000L, "1.7.2-1.7.5"),
		
		/**
		 * Version 1.7.6.
		 * 
		 * <p><strong>Protocol version number:</strong> 5
		 * <br><strong>Release date:</strong> April 9, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_6(5, 1396994400000L, "1.7.6-1.7.10"),
		
		/**
		 * Version 1.7.7.
		 * 
		 * <p><strong>Protocol version number:</strong> 5
		 * <br><strong>Release date:</strong> April 9, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_7(5, 1396994400000L, "1.7.6-1.7.10"),
		
		/**
		 * Version 1.7.8.
		 * 
		 * <p><strong>Protocol version number:</strong> 5
		 * <br><strong>Release date:</strong> April 11, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_8(5, 1397167200000L, "1.7.6-1.7.10"),
		
		/**
		 * Version 1.7.9.
		 * 
		 * <p><strong>Protocol version number:</strong> 5
		 * <br><strong>Release date:</strong> April 14, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_9(5, 1397426400000L, "1.7.6-1.7.10"),
		
		/**
		 * Version 1.7.10.
		 * 
		 * <p><strong>Protocol version number:</strong> 5
		 * <br><strong>Release date:</strong> June 26, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_7_10(5, 1403733600000L, "1.7.6-1.7.10"),
		
		/**
		 * Version 1.8.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> September 2, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8(47, 1409608800000L, "1.8.x"),
		
		/**
		 * Version 1.8.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> November 24, 2014
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_1(47, 1416783600000L, "1.8.x"),
		
		/**
		 * Version 1.8.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> February 19, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_2(47, 1424300400000L, "1.8.x"),
		
		/**
		 * Version 1.8.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> February 20, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_3(47, 1424386800000L, "1.8.x"),
		
		/**
		 * Version 1.8.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> April 17, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_4(47, 1429221600000L, "1.8.x"),
		
		/**
		 * Version 1.8.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> May 22, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_5(47, 1432245600000L, "1.8.x"),
		
		/**
		 * Version 1.8.6.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> May 25, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_6(47, 1432504800000L, "1.8.x"),
		
		/**
		 * Version 1.8.7.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> June 5, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_7(47, 1433455200000L, "1.8.x"),
		
		/**
		 * Version 1.8.8.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> July 28, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_8(47, 1438034400000L, "1.8.x"),
		
		/**
		 * Version 1.8.9.
		 * 
		 * <p><strong>Protocol version number:</strong> 47
		 * <br><strong>Release date:</strong> December 9, 2015
		 * <br><strong>Note:</strong> old - pre-1.9</p>
		 */
		V1_8_9(47, 1449615600000L, "1.8.x"),
		
		/**
		 * Version 1.9.
		 * 
		 * <p><strong>Protocol version number:</strong> 107
		 * <br><strong>Release date:</strong> February 29, 2016</p>
		 */
		V1_9(107, 1456700400000L),
		
		/**
		 * Version 1.9.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 108
		 * <br><strong>Release date:</strong> March 30, 2016</p>
		 */
		V1_9_1(108, 1459288800000L),
		
		/**
		 * Version 1.9.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 109
		 * <br><strong>Release date:</strong> March 30, 2016</p>
		 */
		V1_9_2(109, 1459288800000L),
		
		/**
		 * Version 1.9.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 110
		 * <br><strong>Release date:</strong> May 10, 2016</p>
		 */
		V1_9_3(110, 1462831200000L, "1.9.3/4"),
		
		/**
		 * Version 1.9.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 110
		 * <br><strong>Release date:</strong> May 10, 2016</p>
		 */
		V1_9_4(110, 1462831200000L, "1.9.3/4"),
		
		/**
		 * Version 1.10.
		 * 
		 * <p><strong>Protocol version number:</strong> 210
		 * <br><strong>Release date:</strong> June 8, 2016</p>
		 */
		V1_10(210, 1465336800000L, "1.10.x"),
		
		/**
		 * Version 1.10.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 210
		 * <br><strong>Release date:</strong> June 22, 2016</p>
		 */
		V1_10_1(210, 1466546400000L, "1.10.x"),
		
		/**
		 * Version 1.10.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 210
		 * <br><strong>Release date:</strong> June 23, 2016</p>
		 */
		V1_10_2(210, 1466632800000L, "1.10.x"),
		
		/**
		 * Version 1.11.
		 * 
		 * <p><strong>Protocol version number:</strong> 315
		 * <br><strong>Release date:</strong> November 14, 2016</p>
		 */
		V1_11(315, 1479078000000L),
		
		/**
		 * Version 1.11.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 316
		 * <br><strong>Release date:</strong> December 20, 2016</p>
		 */
		V1_11_1(316, 1482188400000L, "1.11.1/2"),
		
		/**
		 * Version 1.11.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 316
		 * <br><strong>Release date:</strong> December 21, 2016</p>
		 */
		V1_11_2(316, 1482274800000L, "1.11.1/2"),
		
		/**
		 * Version 1.12.
		 * 
		 * <p><strong>Protocol version number:</strong> 335
		 * <br><strong>Release date:</strong> June 7, 2017</p>
		 */
		V1_12(335, 1496786400000L),
		
		/**
		 * Version 1.12.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 338
		 * <br><strong>Release date:</strong> August 3, 2017</p>
		 */
		V1_12_1(338, 1501711200000L),
		
		/**
		 * Version 1.12.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 340
		 * <br><strong>Release date:</strong> September 18, 2017</p>
		 */
		V1_12_2(340, 1505685600000L),
		
		/**
		 * Version 1.13.
		 * 
		 * <p><strong>Protocol version number:</strong> 393
		 * <br><strong>Release date:</strong> July 18, 2018</p>
		 */
		V1_13(393, 1531864800000L),
		
		/**
		 * Version 1.13.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 401
		 * <br><strong>Release date:</strong> August 22, 2018</p>
		 */
		V1_13_1(401, 1534888800000L),
		
		/**
		 * Version 1.13.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 404
		 * <br><strong>Release date:</strong> October 22, 2018</p>
		 */
		V1_13_2(404, 1540159200000L),
		
		/**
		 * Version 1.14.
		 * 
		 * <p><strong>Protocol version number:</strong> 477
		 * <br><strong>Release date:</strong> April 23, 2019</p>
		 */
		V1_14(477, 1555970400000L),
		
		/**
		 * Version 1.14.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 480
		 * <br><strong>Release date:</strong> May 13, 2019</p>
		 */
		V1_14_1(480, 1557698400000L),
		
		/**
		 * Version 1.14.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 485
		 * <br><strong>Release date:</strong> May 27, 2019</p>
		 */
		V1_14_2(485, 1558908000000L),
		
		/**
		 * Version 1.14.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 490
		 * <br><strong>Release date:</strong> June 24, 2019</p>
		 */
		V1_14_3(490, 1561327200000L),
		
		/**
		 * Version 1.14.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 498
		 * <br><strong>Release date:</strong> July 19, 2019</p>
		 */
		V1_14_4(498, 1563487200000L),
		
		/**
		 * Version 1.15.
		 * 
		 * <p><strong>Protocol version number:</strong> 573
		 * <br><strong>Release date:</strong> December 10, 2019</p>
		 */
		V1_15(573, 1575932400000L),
		
		/**
		 * Version 1.15.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 575
		 * <br><strong>Release date:</strong> December 17, 2019</p>
		 */
		V1_15_1(575, 1576537200000L),
		
		/**
		 * Version 1.15.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 578
		 * <br><strong>Release date:</strong> January 21, 2020</p>
		 */
		V1_15_2(578, 1579561200000L),
		
		/**
		 * Version 1.16.
		 * 
		 * <p><strong>Protocol version number:</strong> 735
		 * <br><strong>Release date:</strong> June 23, 2020</p>
		 */
		V1_16(735, 1592863200000L),
		
		/**
		 * Version 1.16.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 736
		 * <br><strong>Release date:</strong> June 24, 2020</p>
		 */
		V1_16_1(736, 1592949600000L),
		
		/**
		 * Version 1.16.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 751
		 * <br><strong>Release date:</strong> August 11, 2020</p>
		 */
		V1_16_2(751, 1597096800000L),
		
		/**
		 * Version 1.16.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 753
		 * <br><strong>Release date:</strong> September 10, 2020</p>
		 */
		V1_16_3(753, 1599688800000L),
		
		/**
		 * Version 1.16.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 754
		 * <br><strong>Release date:</strong> November 2, 2020</p>
		 */
		V1_16_4(754, 1604271600000L, "1.16.4/5"),
		
		/**
		 * Version 1.16.5.
		 * 
		 * <p><strong>Protocol version number:</strong> 754
		 * <br><strong>Release date:</strong> January 15, 2021</p>
		 */
		V1_16_5(754, 1610665200000L, "1.16.4/5"),
		
		/**
		 * Version 1.17.
		 * 
		 * <p><strong>Protocol version number:</strong> 755
		 * <br><strong>Release date:</strong> June 8, 2021</p>
		 */
		V1_17(755, 1623103200000L),
		
		/**
		 * Version 1.17.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 756
		 * <br><strong>Release date:</strong> July 6, 2021</p>
		 */
		V1_17_1(756, 1625522400000L),
		
		/**
		 * Version 1.18.
		 * 
		 * <p><strong>Protocol version number:</strong> 757
		 * <br><strong>Release date:</strong> November 30, 2021</p>
		 */
		V1_18(757, 1638226800000L, "1.18.0/1"),
		
		/**
		 * Version 1.18.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 757
		 * <br><strong>Release date:</strong> December 10, 2021</p>
		 */
		V1_18_1(757, 1639090800000L, "1.18.0/1"),
		
		/**
		 * Version 1.18.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 758
		 * <br><strong>Release date:</strong> February 28, 2022</p>
		 */
		V1_18_2(758, 1646002800000L),
		
		/**
		 * Version 1.19.
		 * 
		 * <p><strong>Protocol version number:</strong> 759
		 * <br><strong>Release date:</strong> June 7, 2022</p>
		 */
		V1_19(759, 1654552800000L),
		
		/**
		 * Version 1.19.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 760
		 * <br><strong>Release date:</strong> July 27, 2022</p>
		 */
		V1_19_1(760, 1658872800000L, "1.19.1/2"),
		
		/**
		 * Version 1.19.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 760
		 * <br><strong>Release date:</strong> August 5, 2022</p>
		 */
		V1_19_2(760, 1659650400000L, "1.19.1/2"),
		
		/**
		 * Version 1.19.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 761
		 * <br><strong>Release date:</strong> December 7, 2022</p>
		 */
		V1_19_3(761, 1670367600000L),
		
		/**
		 * Version 1.19.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 762
		 * <br><strong>Release date:</strong> March 14, 2023</p>
		 */
		V1_19_4(762, 1678748400000L),
		
		/**
		 * Version 1.20.
		 * 
		 * <p><strong>Protocol version number:</strong> 763
		 * <br><strong>Release date:</strong> June 7, 2023</p>
		 */
		V1_20(763, 1686088800000L, "1.20.0/1"),
		
		/**
		 * Version 1.20.1.
		 * 
		 * <p><strong>Protocol version number:</strong> 763
		 * <br><strong>Release date:</strong> June 12, 2023</p>
		 */
		V1_20_1(763, 1686520800000L, "1.20.0/1"),
		
		/**
		 * Version 1.20.2.
		 * 
		 * <p><strong>Protocol version number:</strong> 764
		 * <br><strong>Release date:</strong> September 21, 2023</p>
		 */
		V1_20_2(764, 1695247200000L),
		
		/**
		 * Version 1.20.3.
		 * 
		 * <p><strong>Protocol version number:</strong> 765
		 * <br><strong>Release date:</strong> December 5, 2023</p>
		 */
		V1_20_3(765, 1701730800000L, "1.20.3/4"),
		
		/**
		 * Version 1.20.4.
		 * 
		 * <p><strong>Protocol version number:</strong> 765
		 * <br><strong>Release date:</strong> December 7, 2023</p>
		 */
		V1_20_4(765, 1701903600000L, "1.20.3/4");
		
		private int protocol;
		private long releaseDate;
		private String name;
		
		private Version(int protocol, long releaseDate) {
			this(protocol, releaseDate, null);
		}
		
		private Version(int protocol, long releaseDate, String name) {
			this.protocol = protocol;
			this.releaseDate = releaseDate;
			this.name = name;
		}
		
		/**
		 * Gets this version's protocol number.
		 * 
		 * <p>Will return -1 if <code>this == </code>{@link #UNSUPPORTED}.</p>
		 * 
		 * @return Version's protocol
		 */
		public int getProtocol() {
			return protocol;
		}
		
		/**
		 * Gets this version's release date, in milliseconds.
		 * 
		 * <p>Will return -1 if <code>this == </code>{@link #UNSUPPORTED}.</p>
		 * 
		 * @return Version's release date
		 */
		public long getReleaseDate() {
			return releaseDate;
		}
		
		/**
		 * Gets this version's name.
		 * 
		 * @return Version's name
		 */
		public String getName() {
			return name == null ? (name = name().substring(1).replace('_', '.')) : name;
		}
		
		/**
		 * Checks if this version is a pre-Netty rewrite version.
		 * 
		 * <p>Will return <code>false</code> if <code>this == </code>{@link #UNSUPPORTED}.</p>
		 * 
		 * @return Whether this is a pre-Netty rewrite version
		 */
		public boolean isPreNettyRewrite() {
			return ordinal() != 0 && ordinal() < 22;
		}
		
		/**
		 * Checks if this version is older than another one.
		 * 
		 * @param version Version to check
		 * @return Whether this version is older then another one
		 */
		public boolean isOlderThan(Version version) {
			return ordinal() < version.ordinal();
		}
		
		/**
		 * Checks if this version is at least as recent as another one.
		 * 
		 * @param version Version to check
		 * @return Whether this version is at least as recent as another one
		 */
		public boolean isAtLeast(Version version) {
			return !isOlderThan(version);
		}
		
		@Override
		public String toString() {
			return this == UNSUPPORTED ? name() : name().substring(1).replace("_", ".");
		}
		
		/**
		 * Formats this version according to the setting
		 * at <code>settings.truncate-version-string</code>
		 * in {@link ConfigurationType#CONFIG}.
		 * 
		 * @return Formatted version
		 */
		@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
		public String format() {
			if (this == UNSUPPORTED)
				return name();
			String format;
			
			if (getName() == null)
				format = toString();
			else format = getName();
			int dot = format.indexOf('.');
			
			return ConfigurationType.CONFIG.get().getBoolean("settings.truncate-version-string") && dot != -1 && format.indexOf('.', dot + 1) != -1 ? format.substring(0, format.indexOf('.', dot + 1)) : format;
		}
		
		/**
		 * Gets a version from the specified input.
		 * 
		 * <p>Will return {@link #UNSUPPORTED} if an invalid version is given.</p>
		 * 
		 * @param input Version to check
		 * @return Corresponding version
		 */
		@NotNull
		public static Version getVersion(String input) {
			Version version = Version.UNSUPPORTED;
			
			try {
				if (input.contains("/"))
					input = input.substring(0, input.indexOf('/'));
				if (input.endsWith(".0"))
					input = input.substring(0, input.lastIndexOf('.'));
			} catch (IndexOutOfBoundsException e) {
				return version;
			} for (Version value : values())
				if (input.equals(value.name().substring(1).replace('_', '.'))) {
					version = value;
					break;
				}
			return version;
		}
		
		/**
		 * Gets a version from its protocol number.
		 * 
		 * <p>Will return {@link #UNSUPPORTED} if an invalid version is given.</p>
		 * 
		 * @param protocol Version's protocol number
		 * @param preNettyRewrite Whether it is a pre-Netty rewrite version
		 * @return Corresponding version
		 */
		@NotNull
		public static Version getVersion(int protocol, boolean preNettyRewrite) {
			for (Version version : values())
				if (preNettyRewrite == version.isPreNettyRewrite() && version.getProtocol() == protocol)
					return version;
			return UNSUPPORTED;
		}
		
	}
	
}
