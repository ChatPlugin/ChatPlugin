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
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;

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
		vanished.put(player.getWorld(), Utils.addAndGet(getVanishedList(player.getWorld()), Arrays.asList(player)));
		
		if (Environment.isSponge()) {
			Player spongePlayer = player.toAdapter().spongeValue();
			
			try { // Sponge v4.2
				spongePlayer.offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBLE").get(null), true);
				spongePlayer.offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_IGNORES_COLLISION").get(null), true);
				spongePlayer.offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_PREVENTS_TARGETING").get(null), true);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				spongePlayer.offer(Keys.VANISH, true);
				spongePlayer.offer(Keys.VANISH_IGNORES_COLLISION, true);
				spongePlayer.offer(Keys.VANISH_PREVENTS_TARGETING, true);
			}
		} for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
			if (!other.hasPermission("chatplugin.commands.vanish"))
				if (Environment.isBukkit()) {
					if (other.toAdapter().bukkitValue().canSee(player.toAdapter().bukkitValue()))
						other.toAdapter().bukkitValue().hidePlayer(player.toAdapter().bukkitValue());
				} else if (other.toAdapter().spongeValue().getTabList().getEntry(player.getUUID()).isPresent())
					other.toAdapter().spongeValue().getTabList().removeEntry(player.getUUID());
		} if (invisibility)
			if (Environment.isBukkit())
				player.toAdapter().bukkitValue().addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
			else player.toAdapter().spongeValue().getOrCreate(PotionEffectData.class).get().addElement(PotionEffect.of(PotionEffectTypes.INVISIBILITY, 0, Integer.MAX_VALUE));
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
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void show(ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		vanished.put(player.getWorld(), Utils.removeAndGet(getVanishedList(player.getWorld()), Arrays.asList(player)));
		
		Object spongePlayer = null;
		
		if (Environment.isSponge()) {
			spongePlayer = player.toAdapter().spongeValue();
			
			try { // Sponge v4.2
				((Player) spongePlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBLE").get(null), false);
				((Player) spongePlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_IGNORES_COLLISION").get(null), false);
				((Player) spongePlayer).offer((Key<Value<Boolean>>) Keys.class.getField("INVISIBILITY_PREVENTS_TARGETING").get(null), false);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				((Player) spongePlayer).offer(Keys.VANISH, false);
				((Player) spongePlayer).offer(Keys.VANISH_IGNORES_COLLISION, false);
				((Player) spongePlayer).offer(Keys.VANISH_PREVENTS_TARGETING, false);
			}
		} for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values())
			if (Environment.isSponge()) {
				TabList tablist = other.toAdapter().spongeValue().getTabList();
				
				if (!tablist.getEntry(player.getUUID()).isPresent())
					tablist.addEntry(new TabListEntryBuilder()
							.list(tablist)
							.gameMode(((Player) spongePlayer).gameMode().get())
							.profile(((Player) spongePlayer).getProfile())
							.build()
							);
			} else other.toAdapter().bukkitValue().showPlayer(player.toAdapter().bukkitValue());
		if (invisibility)
			if (Environment.isBukkit())
				player.toAdapter().bukkitValue().removePotionEffect(PotionEffectType.INVISIBILITY);
			else player.toAdapter().spongeValue().getOrCreate(PotionEffectData.class).get().remove(PotionEffect.of(org.spongepowered.api.effect.potion.PotionEffectTypes.INVISIBILITY, 0, Integer.MAX_VALUE));
		if (ProxyManager.getInstance().isEnabled() && QuitMessageManager.getInstance().getQuitPackets().containsKey(player.getUUID()))
			((QuitPacketImpl) QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID())).setVanished(false);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void update(ChatPluginServerPlayer player, boolean justJoined) {
		if (!enabled)
			return;
		if (!justJoined) {
			Object spongePlayer = null;
			
			if (Environment.isSponge())
				spongePlayer = player.toAdapter().spongeValue();
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				if (Environment.isBukkit()) {
					if (!player.toAdapter().bukkitValue().canSee(other.toAdapter().bukkitValue()))
						player.toAdapter().bukkitValue().showPlayer(other.toAdapter().bukkitValue());
				} else {
					TabList tablist = other.toAdapter().spongeValue().getTabList();
					
					if (!tablist.getEntry(player.getUUID()).isPresent())
						tablist.addEntry(new TabListEntryBuilder()
								.list(tablist)
								.gameMode(((Player) spongePlayer).gameMode().get())
								.profile(((Player) spongePlayer).getProfile())
								.build()
								);
				}
			}
		} if (player.hasPermission("chatplugin.commands.vanish"))
			return;
		for (ChatPluginServerPlayer vanishedPlayer : getVanishedList())
			if (Environment.isBukkit())
				player.toAdapter().bukkitValue().hidePlayer(vanishedPlayer.toAdapter().bukkitValue());
			else player.toAdapter().spongeValue().getTabList().removeEntry(vanishedPlayer.getUUID());
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
		return vanished.getOrDefault(player.getWorld(), Collections.emptyList()).contains(player);
	}
	
	@Override
	public int getOnlineWorld(String world) {
		return Utils.getOnlineWorld(world) - getVanishedList(world).size();
	}
	
	@Override
	public int getOnlineServer() {
		return PlayerManager.getInstance().getTotalPlayers() - getVanishedAmount();
	}
	
}
