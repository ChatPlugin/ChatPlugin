package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import me.remigio07.chatplugin.mixin.extension.ClientConnectionExtension;
import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ClientConnectionExtension {
	
	@Unique
	private int chatPlugin$protocolVersion;
	
	@Override
	public int chatPlugin$getProtocolVersion() {
		return chatPlugin$protocolVersion;
	}
	
	@Override
	public void chatPlugin$setProtocolVersion(int chatPlugin$protocolVersion) {
		this.chatPlugin$protocolVersion = chatPlugin$protocolVersion;
	}
	
}
