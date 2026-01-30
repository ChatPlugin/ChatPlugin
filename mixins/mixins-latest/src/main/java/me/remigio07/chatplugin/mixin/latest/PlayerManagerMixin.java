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

package me.remigio07.chatplugin.mixin.latest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin { // 1.20.2+
	
	@Unique
	private static Class<?> chatPlugin$FabricEventManager;
	
	@Unique
	private static Method chatPlugin$onJoin, chatPlugin$onLeave;
	
	@Redirect(
			method = "onPlayerConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
					)
			)
	private void chatPlugin$onPlayerConnect$0( // cancel join message
			PlayerManager instance,
			Text message,
			boolean overlay,
			
			ClientConnection connection,
			ServerPlayerEntity player,
			ConnectedClientData clientData
			) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED
				|| !JoinMessageManager.getInstance().isEnabled()
				|| !(player.getUuid().version() != 0 || player.getName().getString().startsWith(ServerPlayerManager.getInstance().getFloodgateUsernamePrefix()))
				|| !ServerPlayerManager.getInstance().isWorldEnabled(((ServerWorldExtension) ((EntityExtension) player).chatPlugin$getWorld()).chatPlugin$getName())
				)
			instance.broadcast(message, overlay);
	}
	
	@Inject(
			method = "onPlayerConnect",
			at = @At("RETURN")
			)
	private void chatPlugin$onPlayerConnect$1(
			ClientConnection connection,
			ServerPlayerEntity player,
			ConnectedClientData clientData,
			
			CallbackInfo info
			) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_21_5)) // create a proper mixin for this in the future
			try {
				if (chatPlugin$FabricEventManager == null) {
					chatPlugin$FabricEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricEventManager", false, JARLibraryLoader.getInstance());
					chatPlugin$onJoin = chatPlugin$FabricEventManager.getMethod("onJoin", ServerPlayerEntity.class);
				} chatPlugin$onJoin.invoke(EventManager.getInstance(), player);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	@Inject(
			method = "remove",
			at = @At("HEAD")
			)
	private void chatPlugin$remove(
			ServerPlayerEntity player,
			
			CallbackInfo info
			) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_21_5))
			try {
				if (chatPlugin$onLeave == null)
					chatPlugin$onLeave = chatPlugin$FabricEventManager.getMethod("onLeave", ServerPlayerEntity.class);
				chatPlugin$onLeave.invoke(EventManager.getInstance(), player);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
}
