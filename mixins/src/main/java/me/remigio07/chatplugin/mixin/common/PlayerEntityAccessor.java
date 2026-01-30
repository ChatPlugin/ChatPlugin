package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.remigio07.chatplugin.mixin.extension.ServerPlayerEntityExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor extends ServerPlayerEntityExtension {
	
	@Accessor("inventory")
	@Override
	public PlayerInventory chatPlugin$getInventory();
	
}
