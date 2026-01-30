package me.remigio07.chatplugin.mixin.extension;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface EntityExtension {
	
	public World chatPlugin$getWorld();
	
	public Vec3d chatPlugin$getLocation();
	
}
