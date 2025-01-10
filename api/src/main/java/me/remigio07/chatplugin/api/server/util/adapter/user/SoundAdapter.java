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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) sound adapter.
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
	 * Constructs a sound adapter that accepts a Bukkit's {@link org.bukkit.Sound} or Sponge's
	 * {@link org.spongepowered.api.effect.sound.SoundTypes} compatible sound ID as input.
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
	
	/**
	 * Gets the sound adapted for Bukkit environments.
	 * 
	 * <p>Will return <code>null</code> if {@link #getID()} is not recognized by Bukkit, but
	 * keep in mind that it may be recognized by mods or different Minecraft versions.</p>
	 * 
	 * <p>The <code>org.bukkit.Sound</code> enum became an interface in 1.21.3; cast
	 * the returned value according to the version you are compiling against.</p>
	 * 
	 * @return Bukkit-adapted sound
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	@Nullable(why = "Sound's ID may be invalid")
	public Object bukkitValue() {
		if (Environment.isBukkit())
			try {
				return Class.forName("org.bukkit.Sound").getMethod("valueOf", String.class).invoke(null, id);
			} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException e) {
				return null;
			}
		throw new UnsupportedOperationException("Unable to adapt sound to a Bukkit's Sound on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the sound adapted for Sponge environments.
	 * 
	 * <p>Will return <code>null</code> if {@link #getID()} is not recognized by Sponge, but
	 * keep in mind that it may be recognized by mods or different Minecraft versions.</p>
	 * 
	 * <p>Unlike Bukkit, Sponge does not let you play custom sound IDs unless you register them.</p>
	 * 
	 * @param warnIfInvalid Whether to send a warning message if the sound is invalid
	 * @return Sponge-adapted sound
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	@Nullable(why = "Sound's ID may be invalid")
	public SoundType spongeValue(boolean warnIfInvalid) {
		if (Environment.isSponge())
			try {
				return (SoundType) SoundTypes.class.getField(id).get(null);
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				if (warnIfInvalid)
					LogManager.log("Unknown sound ID: {0}.", 1, id);
				return null;
			}
		throw new UnsupportedOperationException("Unable to adapt sound to a Sponge's SoundType on a " + Environment.getCurrent().getName() + " environment");
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
