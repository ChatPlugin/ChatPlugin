package me.remigio07.chatplugin.mixin.latest;

import org.spongepowered.asm.mixin.Mixin;

import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldExtension {
	
	@Override
	public String chatPlugin$getName() {
		return ((ServerWorldProperties) ((ServerWorld) (Object) this).getLevelProperties()).getLevelName();
	}
	
}
