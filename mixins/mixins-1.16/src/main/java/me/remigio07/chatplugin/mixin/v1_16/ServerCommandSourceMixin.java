package me.remigio07.chatplugin.mixin.v1_16;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.mixin.extension.ServerCommandSourceExtension;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements ServerCommandSourceExtension {
	
	@Shadow
	private CommandOutput output;
	
	@Override
	public void chatPlugin$sendMessage(Text message) {
		output.sendSystemMessage(message, Utils.NIL_UUID);
	}
	
}
