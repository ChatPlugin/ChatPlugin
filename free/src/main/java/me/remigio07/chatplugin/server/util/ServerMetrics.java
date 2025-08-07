package me.remigio07.chatplugin.server.util;

import java.util.stream.Stream;

import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.telegram.TelegramIntegrationManager;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.CommonMetrics;

public class ServerMetrics extends CommonMetrics {
	
	public static void load(Object instance) {
		Stream.Builder<CustomChart> charts = Stream.builder();
		boolean multiInstanceMode = ProxyManager.getInstance().isEnabled();
		int languages = LanguageManager.getInstance().getLanguages().size();
		int ranks = RankManager.getInstance().getRanks().size();
		
		charts
				.add(new SimplePie("languages", () -> String.valueOf(languages > 4 ? languages > 9 ? languages > 14 ? "15+" : "10-14" : "5-9" : "1-4")))
				.add(new SimplePie("ranks", () -> String.valueOf(ranks > 9 ? ranks > 19 ? ranks > 29 ? ranks > 49 ? ranks > 69 ? "70-99" : "50-69" : "30-49" : "20-29" : "10-19" : "1-9")))
				.add(new SimplePie("actionbars", () -> range(ActionbarManager.class, ActionbarManager.getInstance().getActionbars().size())))
				.add(new SimplePie("ads", () -> range(AdManager.class, AdManager.getInstance().getAds().size())))
				.add(new SimplePie("f3ServerNames", () -> range(F3ServerNameManager.class, F3ServerNameManager.getInstance().getF3ServerNames().size())))
				.add(new SimplePie("tablists", () -> range(TablistManager.class, TablistManager.getInstance().getTablists().size())));
		
		if (ChatPlugin.getInstance().isPremium()) {
			if (!multiInstanceMode)
				charts
						.add(new SimplePie("discordIntegration", () -> DiscordIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"))
						.add(new SimplePie("telegramIntegration", () -> TelegramIntegrationManager.getInstance().isEnabled() ? "Enabled" : "Disabled"));
			charts
					.add(new SimplePie("multiInstanceMode", () -> multiInstanceMode ? "Enabled" : "Disabled"))
					.add(new SimplePie("bossbars", () -> range(BossbarManager.class, BossbarManager.getInstance().getBossbars().size())))
					.add(new SimplePie("scoreboards", () -> range(ScoreboardManager.class, ScoreboardManager.getInstance().getScoreboards().size())))
					.add(new SimplePie("guis", () -> range(GUIManager.class, GUIManager.getInstance().getGUIs().size())));
		} if (!multiInstanceMode)
			charts.add(new SimplePie("ipLookupMethod", () -> IPLookupManager.getInstance().isEnabled() ? Utils.capitalizeEveryWord(IPLookupManager.getInstance().getMethod().name()) : "Disabled"));
		load(instance, charts);
	}
	
}
