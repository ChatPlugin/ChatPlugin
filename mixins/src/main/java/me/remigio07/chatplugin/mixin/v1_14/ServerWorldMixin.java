package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;

import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldExtension {
	
	@Override
	public String chatPlugin$getName() {
		return ((ServerWorld) (Object) this).getLevelProperties().getLevelName();
	}
	
}
