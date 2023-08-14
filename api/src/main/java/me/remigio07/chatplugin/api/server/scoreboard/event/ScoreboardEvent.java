/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.scoreboard.event;

import me.remigio07.chatplugin.api.common.integration.RequiredIntegration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.integration.combatlog.CombatLogIntegration;
import me.remigio07.chatplugin.api.server.integration.region.RegionIntegration;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardType;
import me.remigio07.chatplugin.api.server.util.adapter.block.BlockAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.entity.LivingEntityAdapter;

/**
 * Represents an event that triggers a {@link Scoreboard} of type {@link ScoreboardType#EVENT}.
 */
public enum ScoreboardEvent {
	
	/**
	 * Event triggered when a player joins.
	 */
	JOIN,
	
	/**
	 * Event triggered when a player joins for the first time.
	 */
	FIRST_JOIN,
	
	/**
	 * Event triggered when a player attacks an entity.
	 */
	@EventArguments(types = { LivingEntityAdapter.class, double.class }, descriptions = { "Related entity", "Damage dealt" })
	ENTITY_ATTACK("entity_name", "entity_health", "entity_max_health", "damage"),
	
	/**
	 * Event triggered when a player tags another player.
	 */
	@RequiredIntegration(type = CombatLogIntegration.class)
	@EventArguments(types = { LivingEntityAdapter.class, double.class }, descriptions = { "Related opponent", "Damage dealt" } )
	COMBAT_TAG("opponent_name", "opponent_health", "opponent_max_health", "damage"),
	
	/**
	 * Event triggered when a player places a block.
	 */
	@EventArguments(types = BlockAdapter.class, descriptions = "Related block")
	BLOCK_PLACE("block_place_event_placed_block_type", "block_place_event_placed_block_x", "block_place_event_placed_block_y", "block_place_event_placed_block_z"),
	
	/**
	 * Event triggered when a player breaks a block.
	 */
	@EventArguments(types = BlockAdapter.class, descriptions = "Related block")
	BLOCK_BREAK("block_break_event_broken_block_type", "block_break_event_broken_block_x", "block_break_event_broken_block_y", "block_break_event_broken_block_z"),
	
	/**
	 * Event triggered when a player changes world.
	 */
	@EventArguments(types = String.class, descriptions = "Related old world's name")
	CHANGED_WORLD("changed_world_event_old_world"),
	
	/**
	 * Event triggered when a player dies.
	 */
	@EventArguments(types = { int.class, int.class, int.class, int.class, int.class }, descriptions = { "Death's location's X coord", "Death's location's Y coord", "Death's location's Z coord", "Dropped xp levels amount", "Dropped items amount" })
	DEATH("death_event_x", "death_event_y", "death_event_z", "death_event_dropped_xp", "death_event_dropped_items"),
	
	/**
	 * Event triggered when a player respawns.
	 */
	RESPAWN,
	
	/**
	 * Event triggered when a player enters a bed.
	 */
	@EventArguments(types = { int.class, int.class, int.class }, descriptions = { "Bed's location's X coord", "Bed's location's Y coord", "Bed's location's Z coord" })
	BED_ENTER("bed_enter_event_bed_x", "bed_enter_event_bed_y", "bed_enter_event_bed_z"),
	
	/**
	 * Event triggered when a player leaves a bed.
	 */
	@EventArguments(types = { int.class, int.class, int.class }, descriptions = { "Bed's location's X coord", "Bed's location's Y coord", "Bed's location's Z coord" })
	BED_LEAVE("bed_leave_event_bed_x", "bed_leave_event_bed_y", "bed_leave_event_bed_z"),
	
	/**
	 * Event triggered when a player sends a message.
	 */
	CHAT,
	
	/**
	 * Event triggered when a player catches something while fishing.
	 */
	@EventArguments(types = { String.class, String.class, int.class }, descriptions = { "Caught entity's type", "Caught entity's name", "Dropped xp levels amount" })
	FISH("fish_event_caught_type", "fish_event_caught_name", "fish_event_dropped_xp"),
	
	/**
	 * Event triggered when a player changes their level.
	 */
	@EventArguments(types = int.class, descriptions = "Old XP levels amount")
	LEVEL_CHANGE("level_change_event_old_xp"),
	
	/**
	 * Event triggered when a player changes their language.
	 */
	@EventArguments(types = String.class, descriptions = "Old locale's language")
	LOCALE_CHANGE("locale_change_event_old_locale_language"),
	
	/**
	 * Event triggered when a player changes their resource pack's status.
	 */
	@EventArguments(types = String.class, descriptions = "Client's response's status")
	RESOURCE_PACK_STATUS("resource_pack_status_event_status_short", "resource_pack_status_event_status_long"),
	
	/**
	 * Event triggered when a player enters a region.
	 */
	@RequiredIntegration(type = RegionIntegration.class)
	@EventArguments(types = String.class, descriptions = "New region's ID")
	REGION_ENTER(
			"region_enter_event_region_description_0", "region_enter_event_region_description_1", "region_enter_event_region_description_2", "region_enter_event_region_description_3",
			"region_enter_event_region_description_4", "region_enter_event_region_description_5", "region_enter_event_region_description_6", "region_enter_event_region_description_7",
			"region_enter_event_region_description_8", "region_enter_event_region_description_9", "region_enter_event_region_description_10", "region_enter_event_region_description_11",
			"region_enter_event_region_description_12", "region_enter_event_region_description_13", "region_enter_event_region_description_14", "region_enter_event_region_description_15"
			),
	
	/**
	 * Event triggered when a player leaves a region.
	 */
	@RequiredIntegration(type = RegionIntegration.class)
	@EventArguments(types = String.class, descriptions = "Old region's ID")
	REGION_LEAVE(
			"region_leave_event_old_region_name", "region_leave_event_old_region_name_capitalized", "region_leave_event_old_region_owner",
			"region_leave_event_old_region_min_point_x", "region_leave_event_old_region_min_point_y", "region_leave_event_old_region_min_point_z",
			"region_leave_event_old_region_max_point_x", "region_leave_event_old_region_max_point_y", "region_leave_event_old_region_max_point_z"
			);
	
	private String[] placeholders = new String[0];
	
	private ScoreboardEvent(String... placeholders) {
		this.placeholders = placeholders;
	}
	
	/**
	 * Gets the array containing all available placeholders
	 * that can be translated with this event's information.
	 * 
	 * @return Event's placeholders
	 */
	public String[] getPlaceholders() {
		return placeholders;
	}
	
	/**
	 * Gets this event's ID. It is obtained by lowering
	 * {@link #name()}'s case and replacing '_' with '-'.
	 * 
	 * <p><strong>Example:</strong> {@link #BED_ENTER} -&gt; "bed-enter"</p>
	 * 
	 * @return Event's ID
	 */
	public String getID() {
		return name().toLowerCase().replace('_', '-');
	}
	
	/**
	 * Gets a scoreboard's event by its ID.
	 * Will return <code>null</code> if specified
	 * ID does not belong to any event.
	 * 
	 * @param id Event's ID ({@link #getID()})
	 * @return Scoreboard's event
	 */
	@Nullable(why = "Null if the ID is invalid")
	public static ScoreboardEvent get(String id) {
		for (ScoreboardEvent event : values())
			if (id.equalsIgnoreCase(event.getID()))
				return event;
		return null;
	}
	
}
