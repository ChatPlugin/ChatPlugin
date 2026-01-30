package me.remigio07.chatplugin.mixin.latest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.motd.MoTD;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.motd.ServerMoTDManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.mixin.extension.ClientConnectionExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerQueryNetworkHandler;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin { // 1.21.9+
	
	@Shadow
	private ClientConnection connection;
	
	@Unique
	private static Map<URL, ServerMetadata.Favicon> chatPlugin$favicons = new ConcurrentHashMap<>();
	
	@Redirect(
			method = "onRequest",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/network/ServerQueryNetworkHandler;metadata:Lnet/minecraft/server/ServerMetadata;"
					)
			)
	public ServerMetadata chatPlugin$onRequest(
			ServerQueryNetworkHandler instance
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& ServerMoTDManager.getInstance().isEnabled()
				&& !ProxyManager.getInstance().isEnabled()) {
			Version chatPlugin$version = Version.getVersion(((ClientConnectionExtension) connection).chatPlugin$getProtocolVersion(), false);
			InetAddress chatPlugin$ipAddress = connection.getAddress() instanceof InetSocketAddress ? ((InetSocketAddress) connection.getAddress()).getAddress() : InetAddress.getLoopbackAddress();
			MoTD chatPlugin$motd = ServerMoTDManager.getInstance().getMoTD(chatPlugin$ipAddress, chatPlugin$version);
			URL chatPlugin$customIconURL = chatPlugin$motd.getCustomIconURL();
			ServerMetadata chatPlugin$originalMetadata = FabricBootstrapper.getInstance().getServer().getServerMetadata();
			ServerMetadata.Favicon chatPlugin$favicon = null;
			
			if (chatPlugin$motd.isCustomIconDisplayed()) {
				if (!chatPlugin$favicons.containsKey(chatPlugin$customIconURL)) {
					try {
						HttpsURLConnection chatPlugin$connection = (HttpsURLConnection) chatPlugin$customIconURL.openConnection();
						
						chatPlugin$connection.setRequestProperty("User-Agent", Utils.USER_AGENT);
						chatPlugin$connection.setConnectTimeout(5000);
						
						try (
								InputStream chatPlugin$inputStream = chatPlugin$connection.getInputStream();
								ByteArrayOutputStream chatPlugin$output = new ByteArrayOutputStream();
								) {
							ImageIO.write(ImageIO.read(chatPlugin$inputStream), "PNG", chatPlugin$output);
							chatPlugin$favicons.put(chatPlugin$customIconURL, new ServerMetadata.Favicon(chatPlugin$output.toByteArray()));
						}
					} catch (IOException chatPlugin$ioe) {
						LogManager.log("IOException occurred while setting MoTD's favicon: {0}", 2, chatPlugin$ioe.getLocalizedMessage());
					}
				} chatPlugin$favicon = chatPlugin$favicons.get(chatPlugin$customIconURL);
			} return new ServerMetadata(
					Utils.toFabricComponent(ChatColor.translate(chatPlugin$motd.getDescription())),
					Optional.of(new ServerMetadata.Players(chatPlugin$motd.getMaxPlayers().value().intValue(), chatPlugin$motd.getOnlinePlayers().value().intValue(), chatPlugin$motd.isHoverDisplayed() ? Stream.of(chatPlugin$motd.getHover().split("\n")).map(ChatColor::translate).map(PlayerConfigEntry::fromNickname).collect(Collectors.toList()) : chatPlugin$originalMetadata.players().get().sample())),
					Optional.of(new ServerMetadata.Version(chatPlugin$motd.isVersionNameDisplayed() ? chatPlugin$motd.getVersionName() : chatPlugin$originalMetadata.version().get().gameVersion(), chatPlugin$version.getProtocol())),
					chatPlugin$favicon == null ? chatPlugin$originalMetadata.favicon() : Optional.of(chatPlugin$favicon),
					FabricBootstrapper.getInstance().getServer().shouldEnforceSecureProfile()
					);
		} return FabricBootstrapper.getInstance().getServer().getServerMetadata();
	}
	
}
