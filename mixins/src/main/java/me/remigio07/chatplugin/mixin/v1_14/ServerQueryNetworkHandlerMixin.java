package me.remigio07.chatplugin.mixin.v1_14;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
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

import com.mojang.authlib.GameProfile;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.motd.MoTD;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.motd.ServerMoTDManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.mixin.extension.ClientConnectionExtension;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerQueryNetworkHandler;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin { // 1.14-1.19.3
	
	@Shadow
	private ClientConnection connection;
	
	@Unique
	private static Map<URL, String> chatPlugin$favicons = new ConcurrentHashMap<>();
	
	@Redirect(
			method = "onRequest",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/MinecraftServer;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"
					)
			)
	public ServerMetadata chatPlugin$onRequest(
			MinecraftServer instance
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED
				&& ServerMoTDManager.getInstance().isEnabled()
				&& !ProxyManager.getInstance().isEnabled()) {
			Version chatPlugin$version = Version.getVersion(((ClientConnectionExtension) connection).chatPlugin$getProtocolVersion(), false);
			InetAddress chatPlugin$ipAddress = connection.getAddress() instanceof InetSocketAddress ? ((InetSocketAddress) connection.getAddress()).getAddress() : InetAddress.getLoopbackAddress();
			MoTD chatPlugin$motd = ServerMoTDManager.getInstance().getMoTD(chatPlugin$ipAddress, chatPlugin$version);
			URL chatPlugin$customIconURL = chatPlugin$motd.getCustomIconURL();
			ServerMetadata chatPlugin$originalMetadata = instance.getServerMetadata();
			String chatPlugin$favicon = null;
			
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
							chatPlugin$favicons.put(chatPlugin$customIconURL, "data:image/png;base64," + new String(Base64.getEncoder().encode(chatPlugin$output.toByteArray()), StandardCharsets.UTF_8));
						}
					} catch (IOException chatPlugin$ioe) {
						LogManager.log("IOException occurred while setting MoTD's favicon: {0}", 2, chatPlugin$ioe.getLocalizedMessage());
					}
				} chatPlugin$favicon = chatPlugin$favicons.get(chatPlugin$customIconURL);
			} ServerMetadata chatPlugin$metadata = new ServerMetadata();
			ServerMetadata.Players chatPlugin$players = new ServerMetadata.Players(chatPlugin$motd.getMaxPlayers().value().intValue(), chatPlugin$motd.getOnlinePlayers().value().intValue());
			
			chatPlugin$metadata.setDescription(Utils.toFabricComponent(ChatColor.translate(chatPlugin$motd.getDescription())));
			chatPlugin$players.setSample(chatPlugin$motd.isHoverDisplayed() ? Stream.of(chatPlugin$motd.getHover().split("\n")).map(ChatColor::translate).map(name -> new GameProfile(Utils.NIL_UUID, name)).collect(Collectors.toList()).toArray(new GameProfile[0]) : chatPlugin$originalMetadata.getPlayers().getSample());
			chatPlugin$metadata.setPlayers(chatPlugin$players);
			chatPlugin$metadata.setVersion(new ServerMetadata.Version(chatPlugin$motd.isVersionNameDisplayed() ? chatPlugin$motd.getVersionName() : chatPlugin$originalMetadata.getVersion().getGameVersion(), chatPlugin$version.getProtocol()));
			chatPlugin$metadata.setFavicon(chatPlugin$favicon == null ? chatPlugin$originalMetadata.getFavicon() : chatPlugin$favicon);
			return chatPlugin$metadata;
		} return instance.getServerMetadata();
	}
	
}
