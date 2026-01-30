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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.bukkit.NamespacedKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) sound adapter.
 * 
 * <p>It also contains additional information that are normally
 * not included with a sound type: a volume and a pitch.</p>
 */
public class SoundAdapter {
	
	/**
	 * Pattern representing the Vanilla-compliant sound IDs in 1.13+.
	 * 
	 * <p><strong>Regex:</strong> "[a-z0-9/._-]"</p>
	 * 
	 * @see #isVanillaCompliant()
	 */
	public static final Pattern NEW_VANILLA_COMPLIANT_IDS = Pattern.compile("[a-z0-9/._-]");
	private String id;
	private float volume, pitch;
	
	/**
	 * Constructs a sound adapter that accepts one of the following specified as <code>id</code>:
	 * 	<ul>
	 * 		<li>Vanilla-compliant IDs ("namespace:id_example") for all environments</li>
	 * 		<li>{@link org.bukkit.Sound}-compatible IDs for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.effect.sound.SoundTypes}-compatible IDs for Sponge environments </li>
	 * 		<li>{@link net.minecraft.sound.SoundEvents}-compatible IDs for Fabric environments </li>
	 * 	</ul>
	 * 
	 * @param id Sound's ID
	 * @param volume Sound's volume [0 - 1]
	 * @param pitch Sound's pitch [0 - 2]
	 */
	public SoundAdapter(String id, float volume, float pitch) {
		this.id = id;
		this.volume = volume < 0 ? 0 : volume > 1 ? 1 : volume;
		this.pitch = pitch < 0 ? 0 : pitch > 2 ? 2 : pitch;
	}
	
	/**
	 * Constructs a sound adapter passing values found at
	 * 
	 * 	<ul>
	 * 		<li><code>path + ".id"</code> - for {@link #getID()};</li>
	 * 		<li><code>path + ".volume"</code> - for {@link #getVolume()};</li>
	 * 		<li><code>path + ".pitch"</code> - for {@link #getPitch()};</li>
	 * 	</ul>
	 * 
	 * to {@link #SoundAdapter(String, float, float)}.
	 * 
	 * @param configuration Configuration to read
	 * @param path Path to read
	 */
	public SoundAdapter(Configuration configuration, String path) {
		this(
				configuration.getString(path + ".id"),
				configuration.getFloat(path + ".volume", 1F),
				configuration.getFloat(path + ".pitch", 1F)
				);
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "SoundAdapter{", "}")
				.add("id=\"" + id + "\"")
				.add("volume=" + volume + "F")
				.add("pitch=" + pitch + "F")
				.toString();
	}
	
	/**
	 * Gets the sound adapted for Bukkit environments.
	 * 
	 * <p>Will return <code>null</code> if {@link #getID()} is not recognized by Bukkit.</p>
	 * 
	 * <p>The <code>org.bukkit.Sound</code> enum became an interface in 1.21.3; cast the
	 * returned value accordingly depending on the version you are compiling against.</p>
	 * 
	 * @return Bukkit-adapted sound
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	@Nullable(why = "Sound's ID may be invalid")
	public Object bukkitValue() {
		if (Environment.isBukkit()) {
			Object sound = null;
			
			try {
				if (id.equals(id.toUpperCase()))
					sound = Class.forName("org.bukkit.Sound").getMethod("valueOf", String.class).invoke(null, id);
				if (sound == null) {
					if (id.startsWith("minecraft:"))
						id = id.substring(10);
					if (VersionUtils.getVersion().isAtLeast(Version.V1_16_4))
						sound = org.bukkit.Registry.SOUNDS.get(NamespacedKey.minecraft(id));
					else if (VersionUtils.getVersion().isAtLeast(Version.V1_9)) {
						Class<?> CraftSound = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNMSVersion() + ".CraftSound");
						Field minecraftKey = CraftSound.getDeclaredField("minecraftKey");
						
						minecraftKey.setAccessible(true);
						
						for (Object craftSound : (Object[]) CraftSound.getMethod("values").invoke(null)) {
							if (minecraftKey.get(craftSound).equals(id)) {
								sound = Class.forName("org.bukkit.Sound").getMethod("valueOf", String.class).invoke(null, ((Enum<?>) craftSound).name());
								break;
							}
						}
					} else {
						Field sounds = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getNMSVersion() + ".CraftSound").getDeclaredField("sounds");
						
						sounds.setAccessible(true);
						
						int index = Utils.arrayIndexOf((String[]) sounds.get(null), id, false);
						
						if (index != -1)
							sound = ((Object[]) Class.forName("org.bukkit.Sound").getMethod("values").invoke(null))[index];
					}
				}
			} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException iae) {
				
			} return sound;
		} throw new UnsupportedOperationException("Unable to adapt sound to a Bukkit's Sound on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the sound adapted for Sponge environments.
	 * 
	 * <p>Will return <code>null</code> if {@link #getID()} is not recognized by Sponge.</p>
	 * 
	 * @param warnIfInvalid Whether to send a warning message if the sound is invalid
	 * @return Sponge-adapted sound
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	@Nullable(why = "Sound's ID may be invalid")
	public SoundType spongeValue(boolean warnIfInvalid) {
		if (Environment.isSponge()) {
			SoundType sound = null;
			
			try {
				if (id.equals(id.toUpperCase()))
					sound = (SoundType) SoundTypes.class.getField(id).get(null);
				if (sound == null)
					sound = Sponge.getRegistry().getType(SoundType.class, id).orElse(null);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				
			} if (warnIfInvalid && sound == null)
				LogManager.log("Unknown sound ID: {0}.", 1, id);
			return sound;
		} throw new UnsupportedOperationException("Unable to adapt sound to a Sponge's SoundType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the sound adapted for Fabric environments.
	 * 
	 * <p>Will return <code>null</code> if {@link #getID()} is not recognized by Fabric.</p>
	 * 
	 * @param warnIfInvalid Whether to send a warning message if the sound is invalid
	 * @return Fabric-adapted sound
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	@Nullable(why = "Sound's ID may be invalid")
	public SoundEvent fabricValue(boolean warnIfInvalid) {
		if (Environment.isFabric()) {
			Object sound = null;
			
			try {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_19_3)) {
					if (VersionUtils.getVersion().isAtLeast(Version.V1_21_2)) {
						if (id.equals(id.toUpperCase())) {
							String lowerCase = id.toLowerCase();
							
							if ((sound = Registries.SOUND_EVENT.get(Identifier.ofVanilla(id.toLowerCase().replace('_', '.')))) == null) {
								String[] parts = lowerCase.split("_");
								
								if (parts.length > 1)
									for (int i = 1; i < 1 << (parts.length - 1); i++)
										if ((sound = Registries.SOUND_EVENT.get(Identifier.ofVanilla(getCombination(parts, i)))) != null)
											break;
							}
						} if (sound == null)
							sound = Registries.SOUND_EVENT.get(id.contains(":") ? Identifier.of(id) : Identifier.ofVanilla(id));
					} else {
						Method get = Registry.class.getMethod("method_10223", Identifier.class);
						
						if (id.equals(id.toUpperCase())) {
							String lowerCase = id.toLowerCase();
							
							if ((sound = get.invoke(Registries.SOUND_EVENT, Identifier.tryParse("minecraft", id.toLowerCase().replace('_', '.')))) == null) {
								String[] parts = lowerCase.split("_");
								
								if (parts.length > 1)
									for (int i = 1; i < 1 << (parts.length - 1); i++)
										if ((sound = get.invoke(Registries.SOUND_EVENT, Identifier.tryParse("minecraft", getCombination(parts, i)))) != null)
											break;
							}
						} if (sound == null)
							sound = get.invoke(Registries.SOUND_EVENT, id.contains(":") ? Identifier.tryParse(id) : Identifier.tryParse("minecraft", id));
					}
				} else {
					Method get = Registry.class.getMethod("method_10223", Identifier.class);
					Object SOUND_EVENT = Registry.class.getField("field_11156").get(null);
					
					if (id.equals(id.toUpperCase())) {
						String lowerCase = id.toLowerCase();
						
						if ((sound = get.invoke(SOUND_EVENT, Identifier.tryParse("minecraft:" + id.toLowerCase().replace('_', '.')))) == null) {
							String[] parts = lowerCase.split("_");
							
							if (parts.length > 1)
								for (int i = 1; i < 1 << (parts.length - 1); i++)
									if ((sound = get.invoke(SOUND_EVENT, Identifier.tryParse("minecraft:" + getCombination(parts, i)))) != null)
										break;
						}
					} if (sound == null)
						sound = get.invoke(SOUND_EVENT, id.contains(":") ? Identifier.tryParse(id) : Identifier.tryParse("minecraft:" + id));
				}
			} catch (Exception e) {
				
			} if (warnIfInvalid && sound == null)
				LogManager.log("Unknown sound ID: {0}.", 1, id);
			return (SoundEvent) sound;
		} throw new UnsupportedOperationException("Unable to adapt sound to a Fabric's SoundEvent on a " + Environment.getCurrent().getName() + " environment");
	}
	
	private String getCombination(String[] parts, int index) {
		StringBuilder sb = new StringBuilder(parts[0]);
		
		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(((index >> i) & 1) == 0 ? '.' : '_');
			sb.append(parts[i + 1]);
		} return sb.toString();
	}
	
	/**
	 * Gets this sound's ID.
	 * 
	 * @return Sound's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Checks if this sound is Vanilla-compliant.
	 * 
	 * <p>In 1.13, an undocumented check of the ID passed to the Bukkit
	 * <code>playSound</code> method has been added to the Vanilla server.
	 * When called, it throws a <code>net.minecraft.util.ResourceLocationException</code>
	 * if {@link #getID()} does not respect {@link #NEW_VANILLA_COMPLIANT_IDS}.</p>
	 * 
	 * @return Whether this sound is Vanilla-compliant
	 */
	public boolean isVanillaCompliant() {
		return VersionUtils.getVersion().isOlderThan(Version.V1_13) || NEW_VANILLA_COMPLIANT_IDS.matcher(id).matches();
	}
	
	/**
	 * Gets this sound's volume.
	 * 
	 * @return Sound's volume [0 - 1]
	 */
	public float getVolume() {
		return volume;
	}
	
	/**
	 * Gets this sound's pitch.
	 * 
	 * @return Sound's pitch [0 - 2]
	 */
	public float getPitch() {
		return pitch;
	}
	
}
