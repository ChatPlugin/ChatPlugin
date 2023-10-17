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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.punishment;

import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Abstract manager that handles {@link Punishment}s and interacts with the storage.
 * 
 * @see BanManager
 * @see WarningManager
 * @see KickManager
 * @see MuteManager
 */
public abstract class PunishmentManager implements ChatPluginManager {
	
	protected static StorageConnector storage;
	protected static ComponentTranslator componentTranslator;
	protected boolean enabled;
	protected int nextID, storageCount, staffStorageCount, anticheatStorageCount;
	
	@Override
	public void load() throws ChatPluginManagerException {
		storage = StorageConnector.getInstance();
		componentTranslator = ComponentTranslator.getInstance();
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the next punishment's ID.
	 * 
	 * @return Next punishment's ID
	 */
	public int getNextID() {
		return nextID;
	}
	
	/**
	 * Gets the amount of punishments stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * @return Amount of punishments
	 */
	public long getStorageCount() {
		return storageCount;
	}
	
	/**
	 * Sets the amount of punishments stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * <p>This method also updates {@link #getNextID()}'s value.</p>
	 * 
	 * @param storageCount Amount of punishments
	 */
	public void setStorageCount(int storageCount) {
		this.storageCount = storageCount;
		nextID = storageCount + 1;
	}
	
	/**
	 * Gets the amount of punishments performed by Staff members stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_staff_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * @return Amount of punishments
	 */
	public int getStaffStorageCount() {
		return staffStorageCount;
	}
	
	/**
	 * Sets the amount of punishments performed by Staff members stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_staff_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * @param staffStorageCount Amount of punishments
	 */
	public void setStaffStorageCount(int staffStorageCount) {
		this.staffStorageCount = staffStorageCount;
	}
	
	/**
	 * Gets the amount of punishments performed by the anticheat stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_anticheat_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * @return Amount of punishments
	 */
	public long getAnticheatStorageCount() {
		return anticheatStorageCount;
	}
	
	/**
	 * Sets the amount of punishments performed by the anticheat stored in the storage.
	 * 
	 * <p>This value is only used to translate "{total_anticheat_punishments}",
	 * where "punishments" is one of the following:
	 * "bans", "warnings", "kicks", "mutes".</p>
	 * 
	 * @param anticheatStorageCount Amount of punishments
	 */
	public void setAnticheatStorageCount(int anticheatStorageCount) {
		this.anticheatStorageCount = anticheatStorageCount;
	}
	
	/**
	 * Formats a punishment's reason checking if it was not specified (<code>== null</code>):
	 * in that case, it will be replaced with the default one set in the specified language's messages file.
	 * 
	 * @param reason Reason to format
	 * @param language Language used to translate the reason
	 * @return Formatted reason
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatReason(@Nullable(why = "Reason may not have been specified") String reason, Language language);
	
}
