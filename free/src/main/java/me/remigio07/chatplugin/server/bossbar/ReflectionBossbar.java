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

package me.remigio07.chatplugin.server.bossbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.bossbar.PlayerBossbar;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

public class ReflectionBossbar extends PlayerBossbar {
	
//	private static Constructor<?> entityWither = Reflection.getLoadedClass("EntityWither").getConstructors()[0];
	private static Constructor<?>[] constructors = new Constructor<?>[3];
	private static final float MAX_HEALTH = 300F;
//	private static String setInvulMethodName;
	private Object wither;
	private int id;
	private boolean hidden;
	
	static {
		Constructor<?>[] witherConstructors = BukkitReflection.getLoadedClass("EntityWither").getConstructors();
		Constructor<?>[] spawnEntityConstructors = BukkitReflection.getLoadedClass("PacketPlayOutSpawnEntityLiving").getConstructors();
//		Constructor<?>[] entityDestroyConstructors = Reflection.getLoadedClass("PacketPlayOutEntityDestroy").getConstructors();
		constructors[0] = witherConstructors[0].getParameterCount() == 0 ? witherConstructors[1] : witherConstructors[0];
		constructors[1] = spawnEntityConstructors[0].getParameterCount() == 0 ? spawnEntityConstructors[1] : spawnEntityConstructors[0];
//		constructors[2] = entityDestroyConstructors[0].getParameterCount() == 0 ? entityDestroyConstructors[1] : entityDestroyConstructors[0];
		try {
			constructors[2] = BukkitReflection.getLoadedClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
//		switch (VersionUtils.getNMSVersion()) {
//		case "v1_8_R1":
//		case "v1_8_R2":
//		case "v1_8_R3":
//		case "v1_14_R1":
//			setInvulMethodName = "r";
//			break;
//		case "v1_9_R1":
//		case "v1_9_R2":
//			setInvulMethodName = "l";
//			break;
//		case "v1_10_R1":
//		case "v1_11_R1":
//		case "v1_12_R1":
//			setInvulMethodName = "g";
//			break;
//		case "v1_13_R1":
//			setInvulMethodName = "e";
//			break;
//		case "v1_13_R2":
//			setInvulMethodName = "d";
//			break;
//		case "v1_15_R1":
//		case "v1_18_R1":
//			setInvulMethodName = "s";
//			break;
//		case "v1_16_R1":
//		case "v1_16_R2":
//		case "v1_16_R3":
//		case "v1_17_R1":
//			setInvulMethodName = "setInvul";
//			break;
//		case "v1_18_R2":
//			setInvulMethodName = "setInvulnerableTicks";
//			break;
//		} try {
//			Reflection.putMethod(Reflection.getLoadedClass("EntityWither"), setInvulMethodName, Arrays.asList(Integer.TYPE));
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
	}
	
	public ReflectionBossbar(ChatPluginServerPlayer player) {
		super(player);
		
		Object world = BukkitReflection.invokeMethod("CraftWorld", "getHandle", BukkitReflection.getLoadedClass("CraftWorld").cast(player.toAdapter().bukkitValue().getWorld()));
		
		try {
			wither = VersionUtils.getVersion().isAtLeast(Version.V1_14) ? constructors[0].newInstance(BukkitReflection.getFieldValue("EntityTypes", null, "WITHER", "aZ"), world) : constructors[0].newInstance(world);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} id = (int) BukkitReflection.invokeMethod("Entity", "getId", wither);
		
		BukkitReflection.invokeMethod("Entity", "setInvisible", wither, true);
		BukkitReflection.invokeMethod("EntityWither", "setInvulnerableTicks", wither, 880);
		teleportWither();
		sendSpawnPacket();
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public String getTitle() {
		return (String) BukkitReflection.invokeMethod("Entity", "getCustomName", wither);
	}
	
	@Override
	public void setTitle(String title) {
		BukkitReflection.invokeMethod("Entity", "setCustomName", wither, title);
	}
	
	@Override
	public float getProgress() {
		return (float) BukkitReflection.invokeMethod("EntityLiving", "getHealth", wither) / MAX_HEALTH;
	}
	
	@Override
	public void setProgress(float progress) {
		progress = progress < 0 ? 0 : progress > 1 ? 1 : progress;
		
		BukkitReflection.invokeMethod("EntityLiving", "setHealth", wither, progress == 0 ? 0.001F : (progress * MAX_HEALTH));
	}
	
	@Override
	public boolean isHidden() {
		return hidden;
	}
	
	@Override
	public void setHidden(boolean hidden) {
		if (hidden)
			sendRemovePacket();
		else sendSpawnPacket();
		this.hidden = hidden;
	}
	
	@Override
	public void unregister() {
		sendRemovePacket();
	}
	
	@SuppressWarnings("deprecation")
	public void sendSpawnPacket() {
		if (!hidden)
			try {
				player.sendPacket(constructors[1].newInstance(wither));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	@SuppressWarnings("deprecation")
	public void sendRemovePacket() {
		try {
			player.sendPacket(constructors[2].newInstance(new int[] { id }));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void teleportWither() {
		if (hidden)
			return;
		Location loc = player.toAdapter().bukkitValue().getLocation();
		Location newLoc = loc.getDirection().multiply(BossbarManager.getInstance().getReflectionWitherTeleportationDistance()).add(loc.toVector()).toLocation(loc.getWorld());
		
		BukkitReflection.invokeMethod("Entity", "setLocation", wither, newLoc.getX(), newLoc.getY(), newLoc.getZ(), newLoc.getYaw(), newLoc.getPitch());
		sendSpawnPacket();
	}
	
}
