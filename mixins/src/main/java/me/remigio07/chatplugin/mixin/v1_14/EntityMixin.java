package me.remigio07.chatplugin.mixin.v1_14;

import org.spongepowered.asm.mixin.Mixin;

import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {
	
	@Override
	public Vec3d chatPlugin$getLocation() {
		return ((Entity) (Object) this).getPos();
	}
	
}
