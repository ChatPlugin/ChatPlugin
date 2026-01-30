package me.remigio07.chatplugin.mixin.extension;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.ClientConnection;

public interface ServerLoginNetworkHandlerExtension {
	
	public GameProfile chatPlugin$getProfile();
	
	public ClientConnection chatPlugin$getConnection();
	
}
