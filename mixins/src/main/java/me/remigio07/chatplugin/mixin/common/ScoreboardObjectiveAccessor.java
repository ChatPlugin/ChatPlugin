package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

@Mixin(ScoreboardObjective.class)
public interface ScoreboardObjectiveAccessor {
	
	@Accessor("scoreboard")
	public Scoreboard chatPlugin$getScoreboard();
	
}
