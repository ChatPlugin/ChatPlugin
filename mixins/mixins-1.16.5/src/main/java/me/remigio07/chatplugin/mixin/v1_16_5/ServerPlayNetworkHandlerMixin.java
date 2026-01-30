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

package me.remigio07.chatplugin.mixin.v1_16_5;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickActionAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickTypeAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.mixin.v1_14.ResourcePackStatusC2SPacketAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin { // 1.16.4-1.16.5
	
	@Unique
	private static Class<?> chatPlugin$FabricEventManager;
	
	@Unique
	private static Method chatPlugin$handleChatMessage;
	
	@Shadow
	private ServerPlayerEntity player;
	
	@Inject(
			method = "method_31286",
			at = @At(
					value = "NEW",
					target = "Lnet/minecraft/text/TranslatableText;",
					shift = Shift.BEFORE,
					ordinal = 2
					),
			cancellable = true
			)
	private void chatPlugin$method_31286(
			String text,
			
			CallbackInfo info
			) {
		try {
			if (chatPlugin$FabricEventManager == null) {
				chatPlugin$FabricEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricEventManager", false, JARLibraryLoader.getInstance());
				chatPlugin$handleChatMessage = chatPlugin$FabricEventManager.getMethod("handleChatMessage", ServerPlayerEntity.class, String.class);
			} if (!(boolean) chatPlugin$handleChatMessage.invoke(EventManager.getInstance(), player, text))
				info.cancel();
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Inject(
			method = "onResourcePackStatus",
			at = @At("TAIL")
			)
	private void chatPlugin$onResourcePackStatus(
			ResourcePackStatusC2SPacket packet,
			
			CallbackInfo info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("resource-pack-status-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((ServerPlayerEntity) (Object) this).getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, ((ResourcePackStatusC2SPacketAccessor) packet).chatPlugin$getStatus().name());
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
				}
			}
		}
	}
	
	@Redirect(
			method = "onDisconnected",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"
					)
			)
	private void chatPlugin$onDisconnected(
			PlayerManager instance,
			Text message,
			MessageType type,
			UUID senderUuid
			) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED
				|| !QuitMessageManager.getInstance().isEnabled()
				|| ServerPlayerManager.getInstance().getPlayer(player.getUuid()) == null)
			instance.broadcastChatMessage(message, type, senderUuid);
	}
	
	@Redirect(
			method = "onClickSlot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"
					)
			)
	private ItemStack chatPlugin$onClickSlot$0( // can't these params be removed?
			ScreenHandler instance,
			int slotIndex,
			int button,
			SlotActionType actionType,
			PlayerEntity player,
			
			ClickSlotC2SPacket packet
			) {
		return ItemStack.EMPTY;
	}
	
	@SuppressWarnings("deprecation")
	@Inject(
			method = "onClickSlot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
					),
			cancellable = true
			)
	private void chatPlugin$onClickSlot$1(
			ClickSlotC2SPacket packet,
			
			CallbackInfo info,
			@Local LocalRef<ItemStack> stack
			) {
		int chatPlugin$slotIndex = packet.getSlot();
		int chatPlugin$button = packet.getButton();
		SlotActionType chatPlugin$actionType = packet.getActionType();
		
		if (ChatPlugin.getState() == ChatPluginState.LOADED && player.currentScreenHandler instanceof GenericContainerScreenHandler) {
			ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid());
			
			if (chatPlugin$serverPlayer != null) {
				GUI chatPlugin$gui = GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer);
				
				if (chatPlugin$gui != null) {
					if (chatPlugin$slotIndex < -1 && chatPlugin$slotIndex != -999) {
						info.cancel();
						return;
					} ClickTypeAdapter chatPlugin$clickType = ClickTypeAdapter.UNKNOWN;
					ClickActionAdapter chatPlugin$clickAction = ClickActionAdapter.UNKNOWN;
					
					switch (chatPlugin$actionType) {
					case PICKUP:
						if (chatPlugin$button == 0)
							chatPlugin$clickType = ClickTypeAdapter.SHIFT_LEFT;
						else if (chatPlugin$button == 1)
							chatPlugin$clickType = ClickTypeAdapter.SHIFT_RIGHT;
						if (chatPlugin$clickType != ClickTypeAdapter.UNKNOWN) {
							chatPlugin$clickAction = ClickActionAdapter.NOTHING;
							
							if (chatPlugin$slotIndex == -999) {
								if (!player.inventory.getCursorStack().isEmpty())
									chatPlugin$clickAction = chatPlugin$button == 0 ? ClickActionAdapter.DROP_ALL_CURSOR : ClickActionAdapter.DROP_ONE_CURSOR;
							} else if (chatPlugin$slotIndex >= 0) {
								Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
								ItemStack chatPlugin$item = chatPlugin$slot.getStack();
								ItemStack chatPlugin$cursor = player.inventory.getCursorStack();
								
								if (chatPlugin$item.isEmpty()) {
									if (!chatPlugin$cursor.isEmpty())
										chatPlugin$clickAction = chatPlugin$button == 0 ? ClickActionAdapter.PLACE_ALL : ClickActionAdapter.PLACE_ONE;
								} else if (chatPlugin$slot.canInsert(chatPlugin$cursor)) {
									if (chatPlugin$cursor.isEmpty())
										chatPlugin$clickAction = chatPlugin$button == 0 ? ClickActionAdapter.PICKUP_ALL : ClickActionAdapter.PICKUP_HALF;
									else if (chatPlugin$slot.canInsert(chatPlugin$cursor)) {
										if (chatPlugin$canCombine(chatPlugin$item, chatPlugin$cursor)) {
											int chatPlugin$toPlace = Math.min(
													Math.min(
															chatPlugin$button == 0 ? chatPlugin$cursor.getCount() : 1,
															chatPlugin$item.getMaxCount() - chatPlugin$item.getCount()
															),
													chatPlugin$slot.inventory.getMaxCountPerStack() - chatPlugin$item.getCount()
													);
											
											if (chatPlugin$toPlace == 1)
												chatPlugin$clickAction = ClickActionAdapter.PLACE_ONE;
											else if (chatPlugin$toPlace == chatPlugin$cursor.getCount())
												chatPlugin$clickAction = ClickActionAdapter.PLACE_ALL;
											else if (chatPlugin$toPlace < 0)
												chatPlugin$clickAction = chatPlugin$toPlace != -1 ? ClickActionAdapter.PICKUP_SOME : ClickActionAdapter.PICKUP_ONE;
											else if (chatPlugin$toPlace != 0)
												chatPlugin$clickAction = ClickActionAdapter.PLACE_SOME;	
										} else if (chatPlugin$cursor.getCount() <= chatPlugin$slot.getMaxItemCount())
											chatPlugin$clickAction = ClickActionAdapter.SWAP_WITH_CURSOR;
									} else if (chatPlugin$canCombine(chatPlugin$cursor, chatPlugin$item)
											&& chatPlugin$item.getCount() >= 0
											&& chatPlugin$item.getCount() + chatPlugin$cursor.getCount() <= chatPlugin$cursor.getMaxCount())
										chatPlugin$clickAction = ClickActionAdapter.PICKUP_ALL;
								}
							}
						} break;
					case QUICK_MOVE:
						if (chatPlugin$button == 0)
							chatPlugin$clickType = ClickTypeAdapter.SHIFT_LEFT;
						else if (chatPlugin$button == 1)
							chatPlugin$clickType = ClickTypeAdapter.SHIFT_RIGHT;
						if (chatPlugin$clickType != ClickTypeAdapter.UNKNOWN) {
							if (chatPlugin$slotIndex >= 0) {
								Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
								
								if (chatPlugin$slot.canTakeItems(player) && chatPlugin$slot.hasStack()) {
									chatPlugin$clickAction = ClickActionAdapter.MOVE_TO_OTHER_INVENTORY;
									break;
								}
							} chatPlugin$clickAction = ClickActionAdapter.NOTHING;
						} break;
					case SWAP:
						if ((chatPlugin$button >= 0 && chatPlugin$button < 9) || chatPlugin$button == 40) {
							chatPlugin$clickType = chatPlugin$button == 40 ? ClickTypeAdapter.SWAP_OFFHAND : ClickTypeAdapter.NUMBER_KEY;
							Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
							
							if (chatPlugin$slot.canTakeItems(player)) {
								ItemStack chatPlugin$hotbar = player.inventory.getStack(chatPlugin$button);
								
								if (chatPlugin$slot.hasStack()) {
									chatPlugin$clickAction = chatPlugin$hotbar.isEmpty() || (chatPlugin$slot.inventory == player.inventory && chatPlugin$slot.canInsert(chatPlugin$hotbar)) ? ClickActionAdapter.HOTBAR_SWAP : ClickActionAdapter.HOTBAR_MOVE_AND_READD;
									break;
								} else if (!chatPlugin$hotbar.isEmpty() && chatPlugin$slot.canInsert(chatPlugin$hotbar)) {
									chatPlugin$clickAction = ClickActionAdapter.HOTBAR_SWAP;
									break;
								}
							} chatPlugin$clickAction = ClickActionAdapter.NOTHING;
						} break;
					case CLONE:
						if (chatPlugin$button == 2) {
							chatPlugin$clickType = ClickTypeAdapter.MIDDLE;
							
							if (chatPlugin$slotIndex >= 0) {
								Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
								
								if (chatPlugin$slot != null && chatPlugin$slot.hasStack() && player.abilities.creativeMode && player.inventory.getCursorStack().isEmpty()) {
									chatPlugin$clickAction = ClickActionAdapter.CLONE_STACK;
									break;
								}
							} chatPlugin$clickAction = ClickActionAdapter.NOTHING;
						} break;
					case THROW:
						if (chatPlugin$slotIndex >= 0) {
							if (chatPlugin$button == 0) {
								chatPlugin$clickType = ClickTypeAdapter.DROP;
								Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
								
								if (chatPlugin$slot.hasStack() && chatPlugin$slot.canTakeItems(player) && !chatPlugin$slot.getStack().isEmpty() && chatPlugin$slot.getStack().getItem() != Items.AIR) {
									chatPlugin$clickAction = ClickActionAdapter.DROP_ONE_SLOT;
									break;
								}
							} else if (chatPlugin$button == 1) {
								chatPlugin$clickType = ClickTypeAdapter.CONTROL_DROP;
								Slot chatPlugin$slot = player.currentScreenHandler.getSlot(chatPlugin$slotIndex);
								
								if (chatPlugin$slot.hasStack() && chatPlugin$slot.canTakeItems(player) && !chatPlugin$slot.getStack().isEmpty() && chatPlugin$slot.getStack().getItem() != Items.AIR) {
									chatPlugin$clickAction = ClickActionAdapter.DROP_ALL_SLOT;
									break;
								}
							}
						} else chatPlugin$clickType = chatPlugin$button == 1 ? ClickTypeAdapter.RIGHT : ClickTypeAdapter.LEFT;
						chatPlugin$clickAction = ClickActionAdapter.NOTHING;
						break;
					case PICKUP_ALL:
						chatPlugin$clickType = ClickTypeAdapter.DOUBLE_CLICK;
						ItemStack chatPlugin$cursor;
						
						if (chatPlugin$slotIndex >= 0
								&& !(chatPlugin$cursor = player.inventory.getCursorStack()).isEmpty()
								&& (player.currentScreenHandler.getStacks().stream().anyMatch(chatPlugin$stack -> chatPlugin$canCombine(chatPlugin$stack, chatPlugin$cursor))
										|| player.inventory.contains(chatPlugin$cursor))) {
							chatPlugin$clickAction = ClickActionAdapter.COLLECT_TO_CURSOR;
							break;
						} chatPlugin$clickAction = ClickActionAdapter.NOTHING;
						break;
					default: // includes QUICK_CRAFT (drag event), handled by ScreenHandlerMixin
						break;
					} if (chatPlugin$actionType != SlotActionType.QUICK_CRAFT) {
						ScreenHandler chatPlugin$oldScreenHandler = player.currentScreenHandler;
						boolean chatPlugin$cancelled = chatPlugin$gui instanceof SinglePageGUI
								? ((SinglePageGUI) chatPlugin$gui).handleClickEvent(chatPlugin$serverPlayer, new ClickEventAdapter(
										chatPlugin$clickType,
										chatPlugin$clickAction,
										player.inventory.getCursorStack().isEmpty() ? null : new ItemStackAdapter(player.inventory.getCursorStack()),
										chatPlugin$slotIndex == -999 ? -1 : chatPlugin$slotIndex,
										chatPlugin$button
										))
								: ((FillableGUI<?>) chatPlugin$gui).handleClickEvent(chatPlugin$serverPlayer, new ClickEventAdapter(
										chatPlugin$clickType,
										chatPlugin$clickAction,
										player.inventory.getCursorStack().isEmpty() ? null : new ItemStackAdapter(player.inventory.getCursorStack()),
										chatPlugin$slotIndex == -999 ? -1 : chatPlugin$slotIndex,
										chatPlugin$button
										), ((FillableGUI<?>) chatPlugin$gui).getViewers().get(chatPlugin$serverPlayer));
						
						if (player.currentScreenHandler != chatPlugin$oldScreenHandler) {
							info.cancel();
							return;
						} if (chatPlugin$cancelled) { // unfortunately it is not an enum
							if (chatPlugin$clickAction == ClickActionAdapter.PICKUP_ALL
									|| chatPlugin$clickAction == ClickActionAdapter.MOVE_TO_OTHER_INVENTORY
									|| chatPlugin$clickAction == ClickActionAdapter.HOTBAR_MOVE_AND_READD
									|| chatPlugin$clickAction == ClickActionAdapter.HOTBAR_SWAP
									|| chatPlugin$clickAction == ClickActionAdapter.COLLECT_TO_CURSOR
									|| chatPlugin$clickAction == ClickActionAdapter.UNKNOWN) {
								player.refreshScreenHandler(chatPlugin$oldScreenHandler);
							} else if (chatPlugin$clickAction == ClickActionAdapter.PICKUP_SOME
									|| chatPlugin$clickAction == ClickActionAdapter.PICKUP_HALF
									|| chatPlugin$clickAction == ClickActionAdapter.PICKUP_ONE
									|| chatPlugin$clickAction == ClickActionAdapter.PLACE_ALL
									|| chatPlugin$clickAction == ClickActionAdapter.PLACE_SOME
									|| chatPlugin$clickAction == ClickActionAdapter.PLACE_ONE
									|| chatPlugin$clickAction == ClickActionAdapter.SWAP_WITH_CURSOR) {
								chatPlugin$serverPlayer.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, chatPlugin$oldScreenHandler.getSlot(chatPlugin$slotIndex).getStack()));
								chatPlugin$serverPlayer.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(chatPlugin$oldScreenHandler.syncId, chatPlugin$slotIndex, chatPlugin$oldScreenHandler.getSlot(chatPlugin$slotIndex).getStack()));
							} else if (chatPlugin$clickAction == ClickActionAdapter.DROP_ALL_SLOT
									|| chatPlugin$clickAction == ClickActionAdapter.DROP_ONE_SLOT) {
								chatPlugin$serverPlayer.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(chatPlugin$oldScreenHandler.syncId, chatPlugin$slotIndex, chatPlugin$oldScreenHandler.getSlot(chatPlugin$slotIndex).getStack()));
							} else if (chatPlugin$clickAction == ClickActionAdapter.DROP_ALL_CURSOR
									|| chatPlugin$clickAction == ClickActionAdapter.DROP_ONE_CURSOR
									|| chatPlugin$clickAction == ClickActionAdapter.CLONE_STACK) {
								chatPlugin$serverPlayer.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, player.inventory.getCursorStack()));
							} return;
						} // NOTE: missing crafting inventories support
					}
				}
			}
		} stack.set(player.currentScreenHandler.onSlotClick(chatPlugin$slotIndex, chatPlugin$button, chatPlugin$actionType, player));
	}
	
	private static boolean chatPlugin$canCombine(ItemStack chatPlugin$stack, ItemStack chatPlugin$otherStack) {
		return chatPlugin$stack.isItemEqualIgnoreDamage(chatPlugin$otherStack) && ItemStack.areTagsEqual(chatPlugin$stack, chatPlugin$otherStack);
	}
	
}
