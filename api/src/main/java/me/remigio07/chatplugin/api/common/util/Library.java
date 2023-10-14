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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.ip_lookup.LocalIPLookupManager;
import me.remigio07.chatplugin.api.common.telegram.TelegramIntegrationManager;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents the libraries used by ChatPlugin.
 */
public enum Library {
	
	/**
	 * Represents the <a href="https://www.oracle.com/java/technologies/downloads.html">Activation Framework</a> library.
	 */
	ACTIVATION_FRAMEWORK("Activation Framework", "javax.activation.DataSource", new Relocation("com.sun.activation", "javax.activation"), "https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar", "activation-framework.jar", "F04F1CAFC1834351CC2AE0929D8BE5DD"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure API</a> library.
	 */
	ADVENTURE_API("Adventure API", "net.kyori.adventure.Adventure", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-api/4.14.0/adventure-api-4.14.0.jar", "adventure-api.jar", "A29EBB67130B00DEC479A94B51A71613"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Key</a> library.
	 */
	ADVENTURE_KEY("Adventure Key", "net.kyori.adventure.key.Key", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-key/4.14.0/adventure-key-4.14.0.jar", "adventure-key.jar", "A16BC7B53FEE1525437E44C481243E5D"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure NBT</a> library.
	 */
	ADVENTURE_NBT("Adventure NBT", "net.kyori.adventure.nbt.BinaryTagLike", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-nbt/4.14.0/adventure-nbt-4.14.0.jar", "adventure-nbt.jar", "33B0B5195DDFD75F9B44F67582FE84AE"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform API</a> library.
	 */
	ADVENTURE_PLATFORM_API("Adventure Platform", "net.kyori.adventure.platform.AudienceProvider", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-api/4.3.0/adventure-platform-api-4.3.0.jar", "adventure-platform-api.jar", "C11A0F828B2B60DCF4B463105ED3A4CE"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform Bukkit</a> library.
	 */
	ADVENTURE_PLATFORM_BUKKIT("Adventure Platform Bukkit", "net.kyori.adventure.platform.bukkit.BukkitAudiences", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-bukkit/4.3.0/adventure-platform-bukkit-4.3.0.jar", "adventure-platform-bukkit.jar", "8D6F58BA8FF620B83F2318D9BAA05B89"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform BungeeCord</a> library.
	 */
	ADVENTURE_PLATFORM_BUNGEECORD("Adventure Platform Bukkit", "net.kyori.adventure.platform.bungeecord.BungeeAudiences", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-bungeecord/4.3.0/adventure-platform-bungeecord-4.3.0.jar", "adventure-platform-bungeecord.jar", "17BF46408F7B916D47EF8F8AB1BF7E3B"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform Facet</a> library.
	 */
	ADVENTURE_PLATFORM_FACET("Adventure Platform Facet", "net.kyori.adventure.platform.facet.Facet", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-facet/4.3.0/adventure-platform-facet-4.3.0.jar", "adventure-platform-facet.jar", "3311ACE4E2206681765930AE3EE65918"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform SpongeAPI</a> library.
	 */
	ADVENTURE_PLATFORM_SPONGEAPI("Adventure Platform SpongeAPI", "net.kyori.adventure.platform.spongeapi.SpongeAudiences", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-spongeapi/4.3.0/adventure-platform-spongeapi-4.3.0.jar", "adventure-platform-spongeapi.jar", "5904A314C7BF7C978C9C32AE6096CC5D"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform Viaversion</a> library.
	 */
	ADVENTURE_PLATFORM_VIAVERSION("Adventure Platform Viaversion", "net.kyori.adventure.platform.viaversion.ViaFacet", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-viaversion/4.3.0/adventure-platform-viaversion-4.3.0.jar", "adventure-platform-viaversion.jar", "E9169676C6D9E79D61673722F08F799D"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Gson</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_GSON("Adventure Text Serializer Gson", "net.kyori.adventure.text.serializer.gson.GsonComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-gson/4.14.0/adventure-text-serializer-gson-4.14.0.jar", "adventure-text-serializer-gson.jar", "FBB7C33E665153D8A6F334ED651EDE08"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Gson Legacy Impl</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL("Adventure Text Serializer Gson Legacy Impl", "net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-gson-legacy-impl/4.14.0/adventure-text-serializer-gson-legacy-impl-4.14.0.jar", "adventure-text-serializer-gson-legacy-impl.jar", "4C7611A843432BB6520C41E6901AF83B"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer JSON</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_JSON("Adventure Text Serializer JSON", "net.kyori.adventure.text.serializer.json.JSONComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-json/4.14.0/adventure-text-serializer-json-4.14.0.jar", "adventure-text-serializer-json.jar", "A4CE26422BC22FF99748D7EFF5E61F6F"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer JSON Legacy Impl</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_JSON_LEGACY_IMPL("Adventure Text Serializer JSON Legacy Impl", "net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-json-legacy-impl/4.14.0/adventure-text-serializer-json-legacy-impl-4.14.0.jar", "adventure-text-serializer-json-legacy-impl.jar", "A9662F4362141338A625E368F790D643"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Legacy</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_LEGACY("Adventure Text Serializer Legacy", "net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-legacy/4.14.0/adventure-text-serializer-legacy-4.14.0.jar", "adventure-text-serializer-legacy.jar", "66B89517C0E97EBE9E5F4DB34EDCF231"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-codec/">Apache Commons Codec</a> library.
	 */
	APACHE_COMMONS_CODEC("Apache Commons Codec", "org.apache.commons.codec.Charsets", new Relocation("org.apache.commons.codec"), "https://repo1.maven.org/maven2/commons-codec/commons-codec/1.11/commons-codec-1.11.jar", "apache-commons-codec.jar", "076F25B3301E2A0F5FDBCE3BB3299054"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a> library.
	 */
	APACHE_COMMONS_COLLECTIONS("Apache Commons Collections", "org.apache.commons.collections4.Trie", new Relocation("org.apache.commons.collections4"), "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar", "apache-commons-collections.jar", "281E927BCA0A19A77242E6AB89076299"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-logging/">Apache Commons Logging</a> library.
	 */
	APACHE_COMMONS_LOGGING("Apache Commons Logging", "org.apache.commons.logging.Log", new Relocation("org.apache.commons.logging"), "https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar", "apache-commons-logging.jar", "6681045001B49A4DCFD283BB5D6A688F"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-math/">Apache Commons Math</a> library.
	 */
	APACHE_COMMONS_MATH("Apache Commons Math", "org.apache.commons.math3.Field", new Relocation("assets.org.apache.commons.math3", "org.apache.commons.math3"), "https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar", "apache-commons-math.jar", "4D1088F67703126F8733ED3279D5BB60"),
	
	/**
	 * Represents the <a href="https://hc.apache.org/httpcomponents-client/">Apache HttpClient</a> library.
	 */
	APACHE_HTTPCLIENT("Apache HttpClient", "org.apache.http.auth.Credentials", new Relocation("mozilla", "org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar", "apache-httpclient.jar", "4A5792DCE0D210AADE1170F5851C200B"),
	
	/**
	 * Represents the <a href="https://hc.apache.org/httpcomponents-core">Apache HttpCore</a> library.
	 */
	APACHE_HTTPCORE("Apache HttpCore", "org.apache.http.HttpHost", new Relocation("org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.13/httpcore-4.4.13.jar", "apache-httpcore.jar", "DE8BF04616F09DB7492D1E330B933CAC"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM</a> library.
	 */
	ASM("ASM", "org.objectweb.asm.ModuleVisitor", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm/7.2/asm-7.2.jar", "asm.jar", "26CF10DFD4729FD22FCAE0694E041167"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM Commons</a> library.
	 */
	ASM_COMMONS("ASM Commons", "org.objectweb.asm.commons.ClassRemapper", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm-commons/7.2/asm-commons-7.2.jar", "asm-commons.jar", "321121317A6C6221CC26E8F9EE97022F"),
	
	/**
	 * Represents the <a href="https://checkerframework.org/">Checker Qual</a> library.
	 */
	CHECKER_QUAL("Checker Qual", "org.checkerframework.dataflow.qual.Deterministic", new Relocation("org.checkerframework"), "https://repo1.maven.org/maven2/org/checkerframework/checker-qual/3.21.0/checker-qual-3.21.0.jar", "checker-qual.jar", "1EF067AE106177424B9FA4A0F07AE64B"),
	
	/**
	 * Represents the <a href="https://errorprone.info/index">Error Prone Annotations</a> library.
	 */
	ERROR_PRONE_ANNOTATIONS("Error Prone Annotations", "com.google.errorprone.annotations.Var", new Relocation("com.google.errorprone.annotations"), "https://repo1.maven.org/maven2/com/google/errorprone/error_prone_annotations/2.10.0/error_prone_annotations-2.10.0.jar", "error-prone-annotations.jar", "D382E1162430B837087236595B450873"),
	
	/**
	 * Represents the <a href="https://github.com/KyoriPowered/examination">Examination API</a> library.
	 */
	EXAMINATION_API("Examination API", "net.kyori.examination.Examinable", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar", "examination-api.jar", "F0AEDC8CF6C5D311BCDE6CFABA30B441"),
	
	/**
	 * Represents the <a href="https://github.com/KyoriPowered/examination">Examination String</a> library.
	 */
	EXAMINATION_STRING("Examination String", "net.kyori.examination.string.Strings", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/examination-string/1.3.0/examination-string-1.3.0.jar", "examination-string.jar", "68F131CC233CE87AF90E54851D22088E"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/com.google.guava/failureaccess">Failure Access</a> library.
	 */
	FAILURE_ACCESS("Failure Access", "com.google.common.util.concurrent.internal.InternalFutureFailureAccess", new Relocation("com.google.common.util.concurrent.internal"), "https://repo1.maven.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar", "failure-access.jar", "BC9445D5E747D30578857C758F28D2E2"),
	
	/**
	 * Represents the <a href="https://findbugs.sourceforge.net/">FindBugs JSR305</a> library.
	 */
	FINDBUGS_JSR305("FindBugs JSR305", "javax.annotation.Nullable", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar", "findbugs-jsr305.jar", "EAC20332706D0BAC468A9597BDADAB3A"),
	
	/**
	 * Represents the <a href="https://trove4j.sourceforge.net/html/overview.html">GNU Trove</a> library.
	 */
	GNU_TROVE("GNU Trove", "gnu.trove.Version", new Relocation("gnu.trove"), "https://repo1.maven.org/maven2/net/sf/trove4j/trove4j/3.0.3/trove4j-3.0.3.jar", "gnu-trove.jar", "15073F06E175640D3A9391D0CCEB6737"),
	
	/**
	 * Represents the <a href="https://github.com/google/gson">Gson</a> library.
	 */
	GSON("Gson", "com.google.gson.Gson", new Relocation("com.google.gson"), "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar", "gson.jar", "FC7D70560DE39F8B3A72DAEA28B64C50"),
	
	/**
	 * Represents the <a href="https://guava.dev/">Guava</a> library.
	 */
	GUAVA("Guava", "com.google.common.annotations.GwtCompatible", new Relocation("com.google"), "https://repo1.maven.org/maven2/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar", "guava.jar", "192143F0E691B6635BA8AF82B0F71ECD"),
	
	/**
	 * Represents the <a href="https://h2database.com/html/main.html">H2 Driver</a> library.
	 */
	H2_DRIVER("H2 Driver", "org.h2.Driver", null, "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar", "h2-driver.jar", "769D5A85D19CCC2B06620F8C81D6D8F8"),
	
	/**
	 * Represents the <a href="http://www.trustice.com/java/tar/">Ice TAR</a> library.
	 */
	ICE_TAR("Ice TAR", "com.ice.tar.TarProgressDisplay", new Relocation("com.ice.tar"), "https://repo1.maven.org/maven2/javatar/javatar/2.5/javatar-2.5.jar", "ice-tar.jar", "FB77F4154220C38FAF918000388EA042"),
	
	/**
	 * Represents the <a href="https://developers.google.com/j2objc">J2ObjC Annotations</a> library.
	 */
	J2OBJC_ANNOTATIONS("J2ObjC Annotations", "com.google.j2objc.annotations.Weak", new Relocation("com.google.j2objc.annotations"), "https://repo1.maven.org/maven2/com/google/j2objc/j2objc-annotations/1.3/j2objc-annotations-1.3.jar", "j2objc-annotations.jar", "BD2F3A3AC32A0C386E47B04E5831596C"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-annotations">Jackson Annotations</a> library.
	 */
	JACKSON_ANNOTATIONS("Jackson Annotations", "com.fasterxml.jackson.annotation.JacksonAnnotation", new Relocation("com.fasterxml.jackson.annotation"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.13.1/jackson-annotations-2.13.1.jar", "jackson-annotations.jar", "0EF6C147617BA0A3A4CC7EE196E783D1"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-core">Jackson Core</a> library.
	 */
	JACKSON_CORE("Jackson Core", "com.fasterxml.jackson.core.Versioned", new Relocation("com.fasterxml.jackson.core"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.13.1/jackson-core-2.13.1.jar", "jackson-core.jar", "475A23A31EE33D62A428A6AB5BF0DD75"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-databind">Jackson Databind</a> library.
	 */
	JACKSON_DATABIND("Jackson Databind", "com.fasterxml.jackson.databind.annotation.NoClass", new Relocation("com.fasterxml.jackson.databind"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.13.1/jackson-databind-2.13.1.jar", "jackson-databind.jar", "FF5F5AB1827AF68A9F6246AB08C8329A"),
	
	/**
	 * Represents the <a href="https://github.com/Remigio07/JAR-Relocator">JAR Relocator</a> library.
	 */
	JAR_RELOCATOR("JAR Relocator", "me.remigio07.jarrelocator.JarRelocator", null, "https://github.com/Remigio07/JAR-Relocator/releases/download/v1.6/JAR-Relocator-1.6.jar", "jar-relocator.jar", "04FCF414AE833346BF75BB79DAE641AB"),
	
	/**
	 * Represents the <a href="https://github.com/java-native-access/jna">Java Native Access</a> library.
	 */
	JAVA_NATIVE_ACCESS("Java Native Access", "com.sun.jna.Version", new Relocation("com.sun.jna"), "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar", "java-native-access.jar", "AC05C9F713EF2231D537F763CA078472"),
	
	/**
	 * Represents the <a href="https://github.com/pengrad/java-telegram-bot-api/">Java Telegram Bot API</a> library.
	 */
	JAVA_TELEGRAM_BOT_API("Java Telegram Bot API", "com.pengrad.telegrambot.TelegramBot", new Relocation("com.pengrad.telegrambot"), "https://repo1.maven.org/maven2/com/github/pengrad/java-telegram-bot-api/6.9.1/java-telegram-bot-api-6.9.1.jar", "java-telegram-bot-api.jar", "5C6EF18304FE2E6F6057440BDF926CE2"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api">Javax Annotation</a> library.
	 */
	JAVAX_ANNOTATION("Javax Annotation", "javax.annotation.Priority", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar", "javax-annotation.jar", "2F0194DAEA5C9CE359B36CE5D2C3E1DC"),
	
	/**
	 * Represents the <a href="https://github.com/discord-jda/JDA">JDA</a> library.
	 */
	JDA("JDA", "net.dv8tion.jda.api.JDA", new Relocation("com.iwebpp.crypto", "net.dv8tion.jda"), "https://jitpack.io/com/github/DV8FromTheWorld/JDA/4.4.0/JDA-4.4.0.jar", "jda.jar", "68A23F13524D98F3399209587440C7EF"),
	
	/**
	 * Represents the <a href="https://github.com/JetBrains/java-annotations">Jetbrains Annotations</a> library.
	 */
	JETBRAINS_ANNOTATIONS("Jetbrains Annotations", "org.jetbrains.annotations.Nullable", new Relocation("org.intellij.lang.annotations", "org.jetbrains.annotations"), "https://repo1.maven.org/maven2/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar", "jetbrains-annotations.jar", "7FF3B93D54998C854E77789295B55A24"),
	
	/**
	 * Represents the <a href="https://cliftonlabs.github.io/json-simple/">JSON.simple</a> library.
	 */
	JSON_SIMPLE("JSON.simple", "com.github.cliftonlabs.json_simple.Jsonable", new Relocation("com.github.cliftonlabs.json_simple"), "https://repo1.maven.org/maven2/com/github/cliftonlabs/json-simple/4.0.0/json-simple-4.0.0.jar", "json-simple.jar", "F803A9C4BA867669DCCF04F946BBE13E"),
	
	/**
	 * Represents the <a href="https://kotlinlang.org/api/latest/jvm/stdlib/">Kotlin Stdlib</a> library.
	 */
	KOTLIN_STDLIB("Kotlin Stdlib", "kotlin.Deprecated", new Relocation(), "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "kotlin-stdlib.jar", "6642A23CF11C7E33D9CE3090532093A5"),
	
	/**
	 * Represents the <a href="https://square.github.io/okhttp">Logging Interceptor</a> library.
	 */
	LOGGING_INTERCEPTOR("Logging Interceptor", "okhttp.logging.HttpLoggingInterceptor", new Relocation("okhttp.logging"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/logging-interceptor/4.10.0/logging-interceptor-4.10.0.jar", "logging-interceptor.jar", "F89006BA6DCC778B797A4114AA85F0B9"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/MaxMind-DB-Reader-java">MaxMind DB Reader</a> library.
	 */
	MAXMIND_DB_READER("MaxMind DB Reader", "com.maxmind.db.NodeCache", new Relocation("com.maxmind.db"), "https://repo1.maven.org/maven2/com/maxmind/db/maxmind-db/2.1.0/maxmind-db-2.1.0.jar", "maxmind-db-reader.jar", "71AC891D890FB2B5C76FBCFB31A666C5"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/GeoIP2-java">MaxMind GeoIP2</a> library.
	 */
	MAXMIND_GEOIP2("MaxMind GeoIP2", "com.maxmind.geoip2.GeoIp2Provider", new Relocation("com.maxmind.geoip2"), "https://repo1.maven.org/maven2/com/maxmind/geoip2/geoip2/2.16.1/geoip2-2.16.1.jar", "maxmind-geoip2.jar", "9BB5259ABA174818784908580801AC78"),
	
	/**
	 * Represents the <a href="https://dev.mysql.com/doc/connector-j/8.1/en/">MySQL Connector</a> library.
	 */
	MYSQL_CONNECTOR("MySQL Connector", "com.mysql.jdbc.Driver", new Relocation("com.mysql"), "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar", "mysql-connector.jar", "70AF333E1BDDE1D63F6FDDB5FDDA7E67"),
	
	/**
	 * Represents the <a href="https://github.com/TakahikoKawasaki/nv-websocket-client">NV WebSocket Client</a> library.
	 */
	NV_WEBSOCKET_CLIENT("NV WebSocket Client", "com.neovisionaries.ws.client.Connectable", new Relocation("com.neovisionaries.ws.client"), "https://repo1.maven.org/maven2/com/neovisionaries/nv-websocket-client/2.14/nv-websocket-client-2.14.jar", "nv-websocket-client.jar", "37845DC0EBFD3063B2BC5BAC680E02F6"),
	
	/**
	 * Represents the <a href="https://square.github.io/okhttp/">OkHttp</a> library.
	 */
	OKHTTP("OkHttp", "okhttp3.Call", new Relocation("okhttp3"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.10.0/okhttp-4.10.0.jar", "okhttp.jar", "2B39181401822E547EECCA6F89AA54F7"),
	
	/**
	 * Represents the <a href="https://square.github.io/okio/">Okio</a> library.
	 */
	OKIO("Okio", "okio.Source", new Relocation("okio"), "https://repo1.maven.org/maven2/com/squareup/okio/okio/2.10.0/okio-2.10.0.jar", "okio.jar", "CE91E248C5B81EA28FBFE00B61877BE1"),
	
	/**
	 * Represents the <a href="https://www.slf4j.org/">SLF4J API</a> library.
	 */
	SLF4J_API("SLF4J API", "org.slf4j.IMarkerFactory", new Relocation("org.slf4j"), "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar", "slf4j-api.jar", "439C0A219254F07CDA4E43AE54900E39"),
	
	/**
	 * Represents the <a href="https://www.slf4j.org/">SLF4J Simple Provider</a> library.
	 */
	SLF4J_SIMPLE_PROVIDER("SLF4J Simple Provider", "org.slf4j.impl.SimpleLogger", new Relocation("org.slf4j.impl"), "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.32/slf4j-simple-1.7.32.jar", "slf4j-simple-provider.jar", "56596094EDF1C6E6E091CFED359AA276"),
	
	/**
	 * Represents the <a href="https://github.com/xerial/sqlite-jdbc">SQLite Driver</a> library.
	 */
	SQLITE_DRIVER("SQLite Driver", "org.sqlite.JDBC", null, "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.43.0.0/sqlite-jdbc-3.43.0.0.jar", "sqlite-driver.jar", "CE4481C5A3E9D4C97613FD263B651883");
	
	private String name, clazz, url, fileName, md5Hash;
	private Relocation relocation;
	
	static {
		Relocation.KYORI_RELOCATION.add(GSON, JETBRAINS_ANNOTATIONS);
		JACKSON_DATABIND.getRelocation().add(JACKSON_CORE, JACKSON_ANNOTATIONS);
		APACHE_HTTPCLIENT.getRelocation().add(APACHE_COMMONS_CODEC, APACHE_COMMONS_LOGGING, APACHE_HTTPCORE);
		GUAVA.getRelocation().add(FINDBUGS_JSR305, ERROR_PRONE_ANNOTATIONS, FAILURE_ACCESS, J2OBJC_ANNOTATIONS, CHECKER_QUAL);
		JDA.getRelocation().add(DiscordIntegrationManager.LIBRARIES);
		KOTLIN_STDLIB.getRelocation().add(JETBRAINS_ANNOTATIONS);
		MAXMIND_GEOIP2.getRelocation().add(LocalIPLookupManager.LIBRARIES);
		SLF4J_SIMPLE_PROVIDER.getRelocation().add(SLF4J_API);
		OKHTTP.getRelocation().add(JETBRAINS_ANNOTATIONS);
		OKIO.getRelocation().add(KOTLIN_STDLIB);
		OKHTTP.getRelocation().add(OKIO);
		LOGGING_INTERCEPTOR.getRelocation().add(OKHTTP);
		JAVA_TELEGRAM_BOT_API.getRelocation().add(TelegramIntegrationManager.LIBRARIES);
	}
	
	private Library(String name, String clazz, Relocation relocation, String url, String fileName, String md5Hash) {
		this.name = name;
		this.clazz = (relocation == null ? "" : Relocation.PREFIX) + clazz;
		this.relocation = relocation;
		this.url = url;
		this.fileName = fileName;
		this.md5Hash = md5Hash;
	}
	
	/**
	 * Gets this library's name.
	 * 
	 * @return Library's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets one of this library's classes.
	 * 
	 * @return A random class
	 */
	public String getClazz() {
		return clazz;
	}
	
	/**
	 * Gets this library's JAR file's name.
	 * 
	 * @return Library's file name
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Gets this library's MD5 hash.
	 * 
	 * @return Library's MD5 hash
	 */
	public String getMD5Hash() {
		return md5Hash;
	}
	
	/**
	 * Gets this library's relocation data.
	 * 
	 * @return Library's relocation data
	 */
	@Nullable(why = "Not all libraries have to be relocated")
	public Relocation getRelocation() {
		return relocation;
	}
	
	/**
	 * Gets this library's download URL.
	 * 
	 * @return Library's URL
	 */
	public URL getURL() {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Relocation util used to relocate libraries.
	 */
	public static class Relocation {
		
		/**
		 * Prefix used for relocation.
		 * 
		 * <p><strong>Value:</strong> "me.remigio07.chatplugin.common.lib."</p>
		 */
		public static final String PREFIX = "me.remigio07.chatplugin.common.lib.";
		private static final Relocation KYORI_RELOCATION = new Relocation("net.kyori.adventure", "net.kyori.examination");
		private List<String> oldPackages;
		
		/**
		 * Constructs a new relocation.
		 * 
		 * @param oldPackages Old packages array
		 */
		public Relocation(String... oldPackages) {
			this.oldPackages = new ArrayList<>(Arrays.asList(oldPackages));
		}
		
		/**
		 * Gets the list of old packages.
		 * 
		 * @return Old packages list
		 */
		public List<String> getOldPackages() {
			return oldPackages;
		}
		
		private Relocation add(Library... libraries) {
			for (Library library : libraries)
				if (library.getRelocation() != null && !oldPackages.containsAll(library.getRelocation().getOldPackages()))
					oldPackages.addAll(library.getRelocation().getOldPackages());
			return this;
		}
		
	}
	
}