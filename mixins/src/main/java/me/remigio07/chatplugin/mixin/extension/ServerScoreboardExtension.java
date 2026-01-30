package me.remigio07.chatplugin.mixin.extension;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerScoreboardExtension {
	
	public void chatPlugin$setOwner(ServerPlayerEntity chatPlugin$owner);
	
}
