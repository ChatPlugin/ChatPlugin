package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(Entity.class)
public interface EntityAccessor extends EntityExtension {
	
	@Accessor("world")
	@Override
	public World chatPlugin$getWorld();
	
}
