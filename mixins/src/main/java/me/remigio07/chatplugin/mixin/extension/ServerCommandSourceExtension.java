package me.remigio07.chatplugin.mixin.extension;

import net.minecraft.text.Text;

public interface ServerCommandSourceExtension {
	
	public void chatPlugin$sendMessage(Text message);
	
}
