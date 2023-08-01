package me.remigio07_.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;

import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.server.command.PlayerCommand;

public class LanguageCommand extends PlayerCommand {
	
	public LanguageCommand() {
		super("/language <language>");
		tabCompletionArgs.put(0, Arrays.asList("{languages}"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("language", "languages", "lang", "langs", "locale");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (args.length == 1)
			player.executeCommand("/chatplugin language " + args[0]);
		else sendUsage(player);
	}
	
}
