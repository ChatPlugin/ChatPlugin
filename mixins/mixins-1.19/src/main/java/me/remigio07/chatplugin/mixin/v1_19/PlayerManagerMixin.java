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

package me.remigio07.chatplugin.mixin.v1_19;

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
import me.remigio07.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
	
	@Unique
	private static Class<?> chatPlugin$FabricEventManager;
	
	@Unique
	private static Method chatPlugin$onJoin, chatPlugin$onLeave;
	
	@Redirect(
			method = "onPlayerConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"
					)
			)
	private void chatPlugin$onPlayerConnect$0( // cancel join message
			PlayerManager instance,
			Text message,
			RegistryKey<MessageType> typeKey,
			
			ClientConnection connection,
			ServerPlayerEntity player
			) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED
				|| !JoinMessageManager.getInstance().isEnabled()
				|| !(player.getUuid().version() != 0 || player.getName().getString().startsWith(ServerPlayerManager.getInstance().getFloodgateUsernamePrefix()))
				|| !ServerPlayerManager.getInstance().isWorldEnabled(((ServerWorldExtension) player.world).chatPlugin$getName())
				)
			instance.broadcast(message, typeKey);
	}
	
	@Inject(
			method = "onPlayerConnect",
			at = @At("RETURN")
			)
	private void chatPlugin$onPlayerConnect$1(
			ClientConnection connection,
			ServerPlayerEntity player,
			
			CallbackInfo info
			) {
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
		try {
			if (chatPlugin$onLeave == null)
				chatPlugin$onLeave = chatPlugin$FabricEventManager.getMethod("onLeave", ServerPlayerEntity.class);
			chatPlugin$onLeave.invoke(EventManager.getInstance(), player);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}
