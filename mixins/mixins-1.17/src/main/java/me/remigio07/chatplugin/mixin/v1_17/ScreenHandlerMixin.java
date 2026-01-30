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

package me.remigio07.chatplugin.mixin.v1_17;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ScreenHandler.class)
abstract class ScreenHandlerMixin implements ContainerAccessor { // 1.17-1.18.2
	
	@Shadow
	private ItemStack cursorStack;
	
	@Unique
	private Map<Integer, ItemStack> chatPlugin$draggedSlots;
	
	@Redirect(
			method = "internalOnSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Set;size()I",
					ordinal = 1
					)
			)
	private int chatPlugin$internalOnSlotClick$0(
			Set<Slot> instance,
			
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player
			) {
		ChatPluginServerPlayer chatPlugin$serverPlayer;
		return ChatPlugin.getState() == ChatPluginState.LOADED
				&& player.currentScreenHandler instanceof GenericContainerScreenHandler
				&& (chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid())) != null
				&& GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer) != null
				? 0
				: instance.size();
	}
	
	@Inject(
			method = "internalOnSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;getCount()I",
					shift = Shift.AFTER,
					ordinal = 1
					)
			)
	private void chatPlugin$internalOnSlotClick$1(
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player,
			
			CallbackInfo info
			) {
		chatPlugin$draggedSlots = new HashMap<>();
	}
	
	@Redirect(
			method = "internalOnSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V"
					)
			)
	private void chatPlugin$internalOnSlotClick$2(
			Slot instance,
			ItemStack stack,
			
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player
			) {
		ChatPluginServerPlayer chatPlugin$serverPlayer;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& player.currentScreenHandler instanceof GenericContainerScreenHandler
				&& (chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid())) != null
				&& GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer) != null)
			chatPlugin$draggedSlots.put(instance.getIndex(), stack);
		else instance.setStack(stack);
	}
	
	@Redirect(
			method = "internalOnSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V"
					)
			)
	private void chatPlugin$internalOnSlotClick$3(
			ScreenHandler instance,
			ItemStack stack,
			
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player
			) {
		boolean chatPlugin$executeOriginal = true;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED && instance instanceof GenericContainerScreenHandler) {
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
							instance.setStackInSlot(draggedSlot.getKey(), draggedSlot.getValue());
						if (instance.getCursorStack() != null) {
							instance.setCursorStack(chatPlugin$dragEvent.getCursor().fabricValue());
							chatPlugin$needsUpdate = true;
						}
					} else instance.setCursorStack(chatPlugin$oldCursor);
					
					if (chatPlugin$needsUpdate && player instanceof ServerPlayerEntity)
						instance.syncState();
				}
			}
		} if (chatPlugin$executeOriginal)
			instance.setCursorStack(stack);
	}
	
	@SuppressWarnings("deprecation")
	@Inject(
			method = "internalOnSlotClick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/slot/Slot;markDirty()V",
					shift = Shift.AFTER
					)
			)
	private void chatPlugin$internalOnSlotClick$4(
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player,
			
			CallbackInfo info,
			@Local Slot slot
			) {
		ChatPluginServerPlayer chatPlugin$serverPlayer;
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& player.currentScreenHandler instanceof GenericContainerScreenHandler
				&& player instanceof ServerPlayerEntity
				&& slot.getMaxItemCount() != 99
				&& (chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid())) != null
				&& GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer) != null) {
			ScreenHandler chatPlugin$this = (ScreenHandler) (Object) this;
			
			chatPlugin$serverPlayer.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(chatPlugin$this.syncId, slot.getIndex(), slot.getStack()));
			// NOTE: missing crafting inventories support
		}
	}
	
	@Inject(
			method = "getCursorStack",
			at = @At("HEAD")
			)
	private void chatPlugin$getCursorStack( // we don't have the player parameter here but we apply it anyways as it should not be that invasive
			CallbackInfoReturnable<ItemStack> info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& ((Object) this) instanceof GenericContainerScreenHandler // in the future, create a custom ScreenHandler that extends GenericC. only used for GUIs for faster recognition
				&& cursorStack.isEmpty()
				) {
			((ScreenHandler) (Object) this).setCursorStack(ItemStack.EMPTY);
		}
	}
	
}
