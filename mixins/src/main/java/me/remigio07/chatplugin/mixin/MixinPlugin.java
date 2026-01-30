package me.remigio07.chatplugin.mixin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import io.netty.buffer.Unpooled;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class MixinPlugin implements IMixinConfigPlugin {
	
	private static List<String> appliedMixins = new ArrayList<>();
	
	static {
		try {
			Version version = Version.parse(FabricLoader.getInstance().getRawGameVersion());
			
			if (version.compareTo(Version.parse("1.15.2")) > 0) { // 1.16+
				apply("latest.ServerWorldMixin");
				apply((version.compareTo(Version.parse("1.19")) < 0
						? "v1_16" // 1.16-1.18.2
						: version.compareTo(Version.parse("1.19.1")) < 0
						? "v1_19" // 1.19
						: version.compareTo(Version.parse("1.20.2")) < 0
						? "v1_19_1" // 1.19.1-1.20.1
						: "latest") // 1.20.2+
						+ ".PlayerManagerMixin"
						);
				apply((version.compareTo(Version.parse("1.19")) < 0
						? "v1_16" // 1.16-1.18.2
						: "latest") // 1.19+
						+ ".ServerCommandSourceMixin");
				apply((version.compareTo(Version.parse("1.20.2")) < 0
						? "v1_16" // 1.16-1.20.1
						: version.compareTo(Version.parse("1.21.2")) < 0
						? "v1_20_2" // 1.20.2-1.21.1
						: "latest") // 1.21.2+
						+ ".ServerPlayerEntityMixin"
						);
				apply((version.compareTo(Version.parse("1.17")) < 0
						? "v1_16" // 1.16-1.16.5
						: version.compareTo(Version.parse("1.19")) < 0
						? "v1_17" // 1.17-1.18.2
						: version.compareTo(Version.parse("1.19.4")) < 0
						? "1.19" // 1.19-1.19.3
						: version.compareTo(Version.parse("1.20.2")) < 0
						? "v1_19_4" // 1.19.4-1.20.1
						: "latest") // 1.20.2+
						+ ".ScreenHandlerMixin"
						);
				apply((version.compareTo(Version.parse("1.16.4")) < 0
						? "v1_16" // 1.16-1.16.3
						: version.compareTo(Version.parse("1.17")) < 0
						? "v1_16_5" // 1.16.4-1.16.5
						: version.compareTo(Version.parse("1.19")) < 0
						? "v1_17" // 1.17-1.18.2
						: version.compareTo(Version.parse("1.19.1")) < 0
						? "v1_19" // 1.19
						: version.compareTo(Version.parse("1.20.2")) < 0
						? "v1_19_1" // 1.19.1-1.20.1
						: version.compareTo(Version.parse("1.21.5")) < 0
						? "v1_20_2" // 1.20.2-1.21.4
						: "latest") // 1.21.5+
						+ ".ServerPlayNetworkHandlerMixin"
						);
				
				if (version.compareTo(Version.parse("1.20.2")) < 0)
					apply((version.compareTo(Version.parse("1.18")) < 0
							? "v1_16" // 1.16-1.17.1
							: "v1_19") // 1.18-1.20.1
							+ ".ClientSettingsC2SPacketAccessor"
							);
				if (version.compareTo(Version.parse("1.21.1")) < 0)
					apply("v1_16.LivingEntityMixin"); // 1.16-1.21
			} else apply( // 1.14-1.15.2
					"v1_14.ContainerMixin",
					"v1_14.LivingEntityMixin",
					"v1_14.PlayerManagerMixin",
					"v1_14.ServerCommandSourceMixin",
					"v1_14.ServerPlayerEntityAccessor",
					"v1_14.ServerPlayerEntityMixin",
					"v1_14.ServerPlayNetworkHandlerMixin",
					"v1_14.ServerWorldMixin"
					);
			
			apply((version.compareTo(Version.parse("1.19.4")) < 0
					? "v1_14" // 1.14-1.19.3
					: version.compareTo(Version.parse("1.21.9")) < 0
					? "v1_19_4" // 1.19.4-1.21.8
					: "latest") // 1.21.9+
					+ ".ServerQueryNetworkHandlerMixin");
			apply((version.compareTo(Version.parse("1.19.4")) < 0
					? "v1_14" // 1.14-1.19.3
					: version.compareTo(Version.parse("1.20.3")) < 0
					? "v1_19_4" // 1.19.4-1.20.2
					: "latest") // 1.20.3+
					+ ".ServerScoreboardMixin");
			if (version.compareTo(Version.parse("1.20.1")) > 0)
				apply("latest.ServerCommonNetworkHandlerMixin");
			apply(str(version, "1.19", "FishingBobberEntityMixin"));
			apply(str(version, "1.20.2", "ServerHandshakeNetworkHandlerMixin"));
			apply(str(version, "1.20.2", "ServerLoginNetworkHandlerAccessor"));
			apply(str(version, "1.21.9", "EntityMixin"));
		} catch (VersionParsingException vpe) {
			vpe.printStackTrace();
		}
	}
	
	private static void apply(String... mixins) {
		for (String mixin : mixins)
			appliedMixins.add("me.remigio07.chatplugin.mixin." + mixin);
	}
	
	private static String str(Version version, String o, String mixin) throws VersionParsingException {
		return (version.compareTo(Version.parse(o)) < 0 ? "v1_14" : "latest") + "." + mixin;
	}
	
	@Override
	public void onLoad(String mixinPackage) {
		
	}
	
	@Override
	public String getRefMapperConfig() {
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return appliedMixins.contains(mixinClassName);
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		
	}
	
	@Override
	public List<String> getMixins() {
		return null;
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		
	}
	
	// TODO following methods should not be here
	
	public static Object toLegacyText(Text fabricComponent) {
		try {
			return Class.forName("me.remigio07.chatplugin.server.fabric.ChatPluginFabric", false, JARLibraryLoader.getInstance()).getMethod("toLegacyText", Text.class).invoke(null, fabricComponent);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			throw new IllegalStateException("Unable to call MixinPlugin#toLegacyText(Text) as the plugin has not finished loading yet");
		}
	}
	
	public static void registerGlobalReceiver(Identifier id) {
		ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, responseSender) -> ProxyManager.getInstance().receivePluginMessage(buf.array()));
	}
	
	public static Object getBrandPacket(String brand) {
		return new CustomPayloadS2CPacket(Identifier.tryParse("minecraft:brand"), new PacketByteBuf(Unpooled.buffer()).writeString(brand));
	}
	
	public static void registerQueryStart() {
		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			try {
				Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricPremiumEventManager", false, JARLibraryLoader.getInstance()).getMethod("onLoginStart", ServerLoginNetworkHandler.class, MinecraftServer.class, PacketSender.class, LoginSynchronizer.class).invoke(EventManager.getInstance(), handler, server, sender, synchronizer);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}
	
}
