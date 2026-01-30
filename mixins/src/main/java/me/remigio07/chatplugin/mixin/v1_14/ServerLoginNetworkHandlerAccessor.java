package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.GameProfile;

import me.remigio07.chatplugin.mixin.extension.ServerLoginNetworkHandlerExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;

@Mixin(ServerLoginNetworkHandler.class)
public interface ServerLoginNetworkHandlerAccessor extends ServerLoginNetworkHandlerExtension {
	
	@Accessor("profile")
	@Override
	public GameProfile chatPlugin$getProfile();
	
	@Accessor("connection")
	@Override
	public ClientConnection chatPlugin$getConnection();
	
}
