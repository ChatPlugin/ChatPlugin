package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.motd.ServerMoTDManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.mixin.extension.ClientConnectionExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
	
	@Shadow
	private ClientConnection connection;
	
	@Inject(
			method = "onHandshake",
			at = @At("TAIL")
			)
	private void chatPlugin$onHandshake(
			HandshakeC2SPacket packet,
			
			CallbackInfo info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& ServerMoTDManager.getInstance().isEnabled()
				&& !ProxyManager.getInstance().isEnabled())
			((ClientConnectionExtension) connection).chatPlugin$setProtocolVersion(packet.getProtocolVersion());
	}
	
}
