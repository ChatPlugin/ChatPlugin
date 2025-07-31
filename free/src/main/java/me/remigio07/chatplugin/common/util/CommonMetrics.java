package me.remigio07.chatplugin.common.util;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bstats.MetricsBase;
import org.bstats.charts.CustomChart;
import org.bstats.charts.SimplePie;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;

public class CommonMetrics {
	
	@SuppressWarnings("unchecked")
	protected static void load(Object instance, Stream.Builder<CustomChart> charts) {
		try {
			Field metricsBase = instance.getClass().getDeclaredField("metricsBase");
			Field customCharts = MetricsBase.class.getDeclaredField("customCharts");
			
			metricsBase.setAccessible(true);
			customCharts.setAccessible(true);
			((Set<CustomChart>) customCharts.get(metricsBase.get(instance))).addAll(charts
					.add(new SimplePie("databaseStorageMethod", () -> DatabaseManager.getInstance().getMethod().getName()))
					.add(new SimplePie("enabledIntegrations", () -> range(IntegrationManager.class, (int) IntegrationManager.getInstance().getIntegrations().values().stream().filter(ChatPluginIntegration::isEnabled).count())))
					.build()
					.collect(Collectors.toSet())
					);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected static String range(Class<? extends ChatPluginManager> manager, int amount) {
		return ChatPluginManagers.getInstance().getManager(manager).isEnabled() ? String.valueOf(amount > 4 ? amount > 9 ? amount > 14 ? "15+" : "10-14" : "5-9" : "0-4") : "Disabled";
	}
	
}
