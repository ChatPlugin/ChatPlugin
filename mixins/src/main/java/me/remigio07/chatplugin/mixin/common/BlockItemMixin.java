package me.remigio07.chatplugin.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.block.BlockAdapter;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	
	@Shadow
	private Block block;
	
	@Inject(
			method = "place",
			at = @At("HEAD")
			)
	private void chatPlugin$place(
			ItemPlacementContext context,
			
			CallbackInfoReturnable<ActionResult> info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("block-place-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(context.getPlayer().getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, new BlockAdapter(new BlockAdapter.FabricBlock(block, context.getBlockPos())));
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
				}
			}
		}
	}
	
}
