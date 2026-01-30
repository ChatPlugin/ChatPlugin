package me.remigio07.chatplugin.mixin.extension;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public interface ServerPlayerEntityExtension extends EntityExtension {
	
	public PlayerInventory chatPlugin$getInventory();
	
	public String chatPlugin$getClientLanguage();
	
	public void chatPlugin$setClientLanguage(String chatPlugin$clientLanguage);
	
	public void chatPlugin$setPlayerListName(Text chatPlugin$playerListName);
	
	public default void chatPlugin$setPlayerListOrder(int chatPlugin$playerListOrder) {
		throw new UnsupportedOperationException("Version 1.21.2 or newer is required");
	}
	
}
