/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	Unlike the public version found at <https://github.com/ChatPlugin/ChatPlugin>,
 * 	this version of ChatPlugin is private and can only be obtainted by paying
 * 	Remigio07, its developer, through one of the authorized marketplaces.
 * 	
 * 	Nobody except him should be in possession of this code.  Please report on
 * 	the Discord server (<https://discord.gg/CPtysXTfQg>) the website or person
 * 	that has provided it to you, then delete this code.
 */

package me.remigio07.chatplugin.server.command.admin;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class SocialspyCommand extends PlayerCommand {
	
	public SocialspyCommand() {
		super("/socialspy");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("socialspy", "chatspy", "ss");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (PrivateMessagesManager.getInstance().isEnabled()) {
			if (player.hasSocialspyEnabled()) {
				player.setSocialspyEnabled(false);
				player.sendTranslatedMessage("commands.socialspy.disabled");
			} else {
				player.setSocialspyEnabled(true);
				player.sendTranslatedMessage("commands.socialspy.enabled");
			}
		} else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
}
