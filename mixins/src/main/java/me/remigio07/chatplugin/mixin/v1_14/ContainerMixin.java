/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
 * 	Copyright 2025  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://remigio07.me/chatplugin>
 */

package me.remigio07.chatplugin.mixin.v1_14;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.mixin.common.ContainerAccessor;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(Container.class)
abstract class ContainerMixin implements ContainerAccessor { // 1.14-1.15.2
	
	@Unique
	private Map<Integer, ItemStack> chatPlugin$draggedSlots;
	
	@Inject(
			method = "onSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;getCount()I",
					shift = Shift.AFTER,
					ordinal = 1
					)
			)
	private void chatPlugin$onSlotClick$0(
			int slotId,
			int clickData,
			SlotActionType actionType,
			PlayerEntity player,
			
			CallbackInfoReturnable<ItemStack> info
			) {
		chatPlugin$draggedSlots = new HashMap<>();
	}
	
	@Redirect(
			method = "onSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/container/Slot;setStack(Lnet/minecraft/item/ItemStack;)V"
					)
			)
	private void chatPlugin$onSlotClick$1(
			Slot instance,
			ItemStack stack,
			
			int slotId,
			int clickData,
			SlotActionType actionType,
			PlayerEntity player
			) {
		ChatPluginServerPlayer chatPlugin$serverPlayer;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& player.container instanceof GenericContainer
				&& (chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid())) != null
				&& GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer) != null)
			chatPlugin$draggedSlots.put(instance.id, stack);
		else instance.setStack(stack);
	}
	
	@Redirect(
			method = "onSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerInventory;setCursorStack(Lnet/minecraft/item/ItemStack;)V"
					)
			)
	private void chatPlugin$onSlotClick$2(
			PlayerInventory instance,
			ItemStack stack,
			
			int slotId,
			int clickData,
			SlotActionType actionType,
			PlayerEntity player
			) {
		boolean chatPlugin$executeOriginal = true;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ((Object) this) instanceof GenericContainer) {
			ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid());
			
			if (chatPlugin$serverPlayer != null) {
				GUI chatPlugin$gui = GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer);
				
				if (chatPlugin$gui != null) {
					chatPlugin$executeOriginal = false;
					Map<Integer, ItemStack> chatPlugin$eventMap = new HashMap<>();
					
					for (Entry<Integer, ItemStack> chatPlugin$draggedSlot : chatPlugin$draggedSlots.entrySet())
						chatPlugin$eventMap.put(chatPlugin$draggedSlot.getKey(), chatPlugin$draggedSlot.getValue());
					ItemStack chatPlugin$oldCursor = instance.getCursorStack();
					
					instance.setCursorStack(stack);
					
					DragEventAdapter chatPlugin$dragEvent = new DragEventAdapter(
							chatPlugin$eventMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ItemStackAdapter(entry.getValue()))),
							new ItemStackAdapter(chatPlugin$oldCursor),
							stack.getItem() == Items.AIR ? null : new ItemStackAdapter(stack),
							chatPlugin$getQuickCraftStage() == 1
							);
					boolean chatPlugin$cancelled = chatPlugin$gui instanceof SinglePageGUI
							? ((SinglePageGUI) chatPlugin$gui).handleDragEvent(chatPlugin$serverPlayer, chatPlugin$dragEvent)
							: ((FillableGUI<?>) chatPlugin$gui).handleDragEvent(chatPlugin$serverPlayer, chatPlugin$dragEvent, ((FillableGUI<?>) chatPlugin$gui).getViewers().get(chatPlugin$serverPlayer));
					boolean chatPlugin$needsUpdate = chatPlugin$cancelled;
					
					if (!chatPlugin$cancelled) {
						for (Entry<Integer, ItemStack> draggedSlot : chatPlugin$draggedSlots.entrySet())
							((Container) (Object) this).setStackInSlot(draggedSlot.getKey(), draggedSlot.getValue());
						if (instance.getCursorStack() != null) {
							instance.setCursorStack(chatPlugin$dragEvent.getCursor().fabricValue());
							chatPlugin$needsUpdate = true;
						}
					} else instance.setCursorStack(chatPlugin$oldCursor);
					
					if (chatPlugin$needsUpdate && player instanceof ServerPlayerEntity)
						((ServerPlayerEntity) player).openContainer((Container) (Object) this);
				}
			}
		} if (chatPlugin$executeOriginal)
			instance.setCursorStack(stack);
	}
	
	@SuppressWarnings("deprecation")
	@Inject(
			method = "onSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/container/Slot;markDirty()V",
					shift = Shift.AFTER
					)
			)
	private void chatPlugin$onSlotClick$3(
			int slotId,
			int clickData,
			SlotActionType actionType,
			PlayerEntity player,
			
			CallbackInfoReturnable<ItemStack> info,
			@Local Slot slot
			) {
		ChatPluginServerPlayer chatPlugin$serverPlayer;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& player.container instanceof GenericContainer
				&& player instanceof ServerPlayerEntity
				&& slot.getMaxStackAmount() != 99
				&& (chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid())) != null
				&& GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer) != null) {
			chatPlugin$serverPlayer.sendPacket(new ContainerSlotUpdateS2CPacket(((Container) (Object) this).syncId, slot.id, slot.getStack()));
			// NOTE: missing crafting inventories support
		}
	}
	
}
