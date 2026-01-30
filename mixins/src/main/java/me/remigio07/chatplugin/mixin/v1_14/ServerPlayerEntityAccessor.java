package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.remigio07.chatplugin.mixin.extension.ServerPlayerEntityExtension;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor extends ServerPlayerEntityExtension {
	
	@Accessor("clientLanguage")
	@Override
	public String chatPlugin$getClientLanguage();
	
	@Accessor("clientLanguage")
	@Override
	public void chatPlugin$setClientLanguage(String clientLanguage);
	
}
