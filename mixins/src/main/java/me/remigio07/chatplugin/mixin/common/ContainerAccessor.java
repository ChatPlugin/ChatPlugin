package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.container.Container;

@Mixin(Container.class)
public interface ContainerAccessor {
	
	@Accessor("quickCraftStage")
	public int chatPlugin$getQuickCraftStage();
	
}
