package me.remigio07.chatplugin.mixin.v1_19;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.remigio07.chatplugin.mixin.extension.ClientSettingsC2SPacketExtension;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;

@Mixin(ClientSettingsC2SPacket.class)
public interface ClientSettingsC2SPacketAccessor extends ClientSettingsC2SPacketExtension { // 1.18-1.20.1
	
	@Accessor("language")
	@Override
	public String chatPlugin$getLanguage();
	
}
