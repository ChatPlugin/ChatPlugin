package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;

@Mixin(ResourcePackStatusC2SPacket.class)
public interface ResourcePackStatusC2SPacketAccessor {
	
	@Accessor("status")
	public ResourcePackStatusC2SPacket.Status chatPlugin$getStatus();
	
}
