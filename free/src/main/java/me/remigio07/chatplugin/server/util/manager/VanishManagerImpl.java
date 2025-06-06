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

package me.remigio07.chatplugin.server.util.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.potion.PotionEffectType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.common.entity.player.tab.TabListEntryBuilder;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.utils.WorldUtils;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.event.vanish.VanishDisableEvent;
import me.remigio07.chatplugin.api.server.event.vanish.VanishEnableEvent;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.util.Utils;

public class VanishManagerImpl extends VanishManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.CONFIG.get().getBoolean("vanish.enabled"))
			return;
		invisibility = ConfigurationType.CONFIG.get().getBoolean("vanish.invisibility");
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = invisibility = false;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void hide(ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		VanishEnableEvent event = new VanishEnableEvent(player);
		
		event.call();
		
		if (event.isCancelled())
			return;
		vanished.put(player.getWorld(), Utils.addAndGet(getVanishedList(player.getWorld()), Arrays.asList(player)));
		Utils.ensureSync(() -> {
			Object adaptedPlayer = null;
			
			if (Environment.isSponge()) {
				adaptedPlayer = player.toAdapter().spongeValue();
				
				try { // Sponge v4.2
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBLE").get(null), true);
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_IGNORES_COLLISION").get(null), true);
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_PREVENTS_TARGETING").get(null), true);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					((Player) adaptedPlayer).offer(Keys.VANISH, true);
					((Player) adaptedPlayer).offer(Keys.VANISH_IGNORES_COLLISION, true);
					((Player) adaptedPlayer).offer(Keys.VANISH_PREVENTS_TARGETING, true);
				}
			} else adaptedPlayer = player.toAdapter().bukkitValue();
			
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				if (other.equals(player))
					continue;
				if (!other.hasPermission(VANISH_PERMISSION))
					if (Environment.isBukkit()) {
						org.bukkit.entity.Player otherBukkitPlayer = other.toAdapter().bukkitValue();
						
						if (otherBukkitPlayer.canSee((org.bukkit.entity.Player) adaptedPlayer))
							otherBukkitPlayer.hidePlayer((org.bukkit.entity.Player) adaptedPlayer); // backward-compatible
					} else other.toAdapter().spongeValue().getTabList().removeEntry(player.getUUID());
			} if (invisibility)
				if (Environment.isBukkit())
					((org.bukkit.entity.Player) adaptedPlayer).addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.INVISIBILITY, VersionUtils.getVersion().isAtLeast(Version.V1_20) ? -1 : Integer.MAX_VALUE, 0, true, false));
				else ((Player) adaptedPlayer).getOrCreate(PotionEffectData.class).get().addElement(PotionEffect.of(PotionEffectTypes.INVISIBILITY, 0, Integer.MAX_VALUE));
		});
		
		List<String> removedCosmetics = new ArrayList<>();
		
		if (IntegrationType.GADGETSMENU.isEnabled() && WorldUtils.isWorldEnabled(player.toAdapter().bukkitValue().getWorld())) {
			com.yapzhenyie.GadgetsMenu.player.PlayerManager manager = GadgetsMenuAPI.getPlayerManager(player.toAdapter().bukkitValue());
			
			if (manager != null) {
				removedCosmetics = IntegrationType.GADGETSMENU.get().removeActiveCosmetics(player);
			} else player.sendTranslatedMessage("vanish.gadgetsmenu-reload");
		} if (IntegrationType.PLAYERPARTICLES.isEnabled() && !IntegrationType.PLAYERPARTICLES.get().removeActiveCosmetics(player).isEmpty() && !removedCosmetics.contains("particles")) 
			removedCosmetics.add("particles");
		if (!removedCosmetics.isEmpty())
			player.sendTranslatedMessage("vanish.cosmetics-reset", String.join(", ", removedCosmetics));
		if (QuitMessageManager.getInstance().isEnabled())
			((QuitPacketImpl) QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID())).setVanished(true);
	}
	
	@Override
	public void show(ChatPluginServerPlayer player) {
		show(player, true);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void show(ChatPluginServerPlayer player, boolean fixQuitPacket) {
		if (!enabled)
			return;
		VanishDisableEvent event = new VanishDisableEvent(player);
		
		event.call();
		
		if (event.isCancelled())
			return;
		vanished.put(player.getWorld(), Utils.removeAndGet(getVanishedList(player.getWorld()), Arrays.asList(player)));
		Utils.ensureSync(() -> {
			Object adaptedPlayer = null;
			
			if (Environment.isSponge()) {
				adaptedPlayer = player.toAdapter().spongeValue();
				
				try { // Sponge v4.2
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBLE").get(null), false);
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_IGNORES_COLLISION").get(null), false);
					((Player) adaptedPlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_PREVENTS_TARGETING").get(null), false);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					((Player) adaptedPlayer).offer(Keys.VANISH, false);
					((Player) adaptedPlayer).offer(Keys.VANISH_IGNORES_COLLISION, false);
					((Player) adaptedPlayer).offer(Keys.VANISH_PREVENTS_TARGETING, false);
				}
			} else adaptedPlayer = player.toAdapter().bukkitValue();
			
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				if (other.equals(player))
					continue;
				if (Environment.isSponge()) {
					TabList tablist = other.toAdapter().spongeValue().getTabList();
					
					if (!tablist.getEntry(player.getUUID()).isPresent())
						tablist.addEntry(new TabListEntryBuilder()
								.list(tablist)
								.gameMode(((Player) adaptedPlayer).gameMode().get())
								.profile(((Player) adaptedPlayer).getProfile())
								.build()
								);
				} else other.toAdapter().bukkitValue().showPlayer((org.bukkit.entity.Player) adaptedPlayer); // backward-compatible
			} if (invisibility)
				if (Environment.isBukkit())
					((org.bukkit.entity.Player) adaptedPlayer).removePotionEffect(PotionEffectType.INVISIBILITY);
				else ((Player) adaptedPlayer).getOrCreate(PotionEffectData.class).get().remove(PotionEffect.of(org.spongepowered.api.effect.potion.PotionEffectTypes.INVISIBILITY, 0, Integer.MAX_VALUE));
		});
		
		if (QuitMessageManager.getInstance().getQuitPackets().containsKey(player.getUUID()) && fixQuitPacket)
			((QuitPacketImpl) QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID())).setVanished(false);
	}
	
	@SuppressWarnings("deprecation")
	public void update(ChatPluginServerPlayer player, boolean justJoined) {
		if (!enabled)
			return;
		Object adaptedPlayer = Environment.isBukkit() ? player.toAdapter().bukkitValue() : player.toAdapter().spongeValue();
		
		Utils.ensureSync(() -> {
			if (!justJoined) {
				for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
					if (other.equals(player))
						continue;
					if (Environment.isBukkit()) {
						org.bukkit.entity.Player bukkitPlayer = other.toAdapter().bukkitValue();
						
						if (!((org.bukkit.entity.Player) adaptedPlayer).canSee(bukkitPlayer))
							((org.bukkit.entity.Player) adaptedPlayer).showPlayer(bukkitPlayer);
					} else {
						TabList tablist = other.toAdapter().spongeValue().getTabList();
						
						if (!tablist.getEntry(player.getUUID()).isPresent())
							tablist.addEntry(new TabListEntryBuilder()
									.list(tablist)
									.gameMode(((Player) adaptedPlayer).gameMode().get())
									.profile(((Player) adaptedPlayer).getProfile())
									.build()
									);
					}
				}
			} if (!player.hasPermission(VANISH_PERMISSION))
				for (ChatPluginServerPlayer vanishedPlayer : getVanishedList())
					if (Environment.isBukkit())
						((org.bukkit.entity.Player) adaptedPlayer).hidePlayer(vanishedPlayer.toAdapter().bukkitValue());
					else ((Player) adaptedPlayer).getTabList().removeEntry(vanishedPlayer.getUUID());
		});
	}
	
	@Override
	public List<ChatPluginServerPlayer> getVanishedList() {
		return vanished.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}
	
	@Override
	public List<String> getVanishedNames() {
		return getVanishedList().stream().map(ChatPluginServerPlayer::getName).collect(Collectors.toList());
	}
	
	@Override
	public boolean isVanished(ChatPluginServerPlayer player) {
		return enabled && vanished.getOrDefault(player.getWorld(), Collections.emptyList()).contains(player);
	}
	
	@Override
	public int getOnlineWorld(String world) {
		return Utils.getOnlineWorld(world) - getVanishedList(world).size();
	}
	
	@Override
	public int getOnlineServer() {
		return PlayerManager.getInstance().getPlayers().size() - getVanishedAmount();
	}
	
}
