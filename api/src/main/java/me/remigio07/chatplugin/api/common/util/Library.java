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

package me.remigio07.chatplugin.api.common.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
	ACTIVATION_FRAMEWORK("Activation Framework", "javax.activation.DataSource", new Relocation("com.sun.activation", "javax.activation"), "https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar", "activation-framework.jar", "BE7C430DF50B330CFFC4848A3ABEDBFB"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure API</a> library.
	 */
	ADVENTURE_API("Adventure API", "net.kyori.adventure.Adventure", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-api/4.23.0/adventure-api-4.23.0.jar", "adventure-api.jar", "E026DF57FFF736460C3F71A369E09C81"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Key</a> library.
	 */
	ADVENTURE_KEY("Adventure Key", "net.kyori.adventure.key.Key", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-key/4.23.0/adventure-key-4.23.0.jar", "adventure-key.jar", "6BE0440604D0CFCDAE3258C9F6476A36"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure NBT</a> library.
	 */
	ADVENTURE_NBT("Adventure NBT", "net.kyori.adventure.nbt.BinaryTagLike", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-nbt/4.23.0/adventure-nbt-4.23.0.jar", "adventure-nbt.jar", "48A8F6594EBBFDE14D29421E01D9C869"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform API</a> library.
	 */
	ADVENTURE_PLATFORM_API("Adventure Platform", "net.kyori.adventure.platform.AudienceProvider", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-api/4.4.0/adventure-platform-api-4.4.0.jar", "adventure-platform-api.jar", "44DB3CACEEFBD9DAA37625AFB2EA8A5D"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform Bukkit</a> library.
	 */
	ADVENTURE_PLATFORM_BUKKIT("Adventure Platform Bukkit", "net.kyori.adventure.platform.bukkit.BukkitAudiences", Relocation.KYORI_RELOCATION, "https://remigio07.me/drive/libs/adventure-platform-bukkit-4.4.1-SNAPSHOT.jar", "adventure-platform-bukkit.jar", "B05180C99683FFBD928D6B5BC70862E9"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform Facet</a> library.
	 */
	ADVENTURE_PLATFORM_FACET("Adventure Platform Facet", "net.kyori.adventure.platform.facet.Facet", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-facet/4.4.0/adventure-platform-facet-4.4.0.jar", "adventure-platform-facet.jar", "F1C2157458CE8A9EA2AEF4D23B986F11"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Platform SpongeAPI</a> library.
	 */
	ADVENTURE_PLATFORM_SPONGEAPI("Adventure Platform SpongeAPI", "net.kyori.adventure.platform.spongeapi.SpongeAudiences", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-platform-spongeapi/4.4.0/adventure-platform-spongeapi-4.4.0.jar", "adventure-platform-spongeapi.jar", "90D826BE8E3D7B3FD1043A267C19AE5A"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Gson</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_GSON("Adventure Text Serializer Gson", "net.kyori.adventure.text.serializer.gson.GsonComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-gson/4.23.0/adventure-text-serializer-gson-4.23.0.jar", "adventure-text-serializer-gson.jar", "EDE70DD1983DC7E4A4F07205C51346D0"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Gson Legacy Impl</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL("Adventure Text Serializer Gson Legacy Impl", "net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-gson-legacy-impl/4.23.0/adventure-text-serializer-gson-legacy-impl-4.23.0.jar", "adventure-text-serializer-gson-legacy-impl.jar", "71A67BD4D03E45F3F03E59BCAEAB37A6"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer JSON</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_JSON("Adventure Text Serializer JSON", "net.kyori.adventure.text.serializer.json.JSONComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-json/4.23.0/adventure-text-serializer-json-4.23.0.jar", "adventure-text-serializer-json.jar", "1653A0EEE94CF73BB4F459AC0D628652"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer JSON Legacy Impl</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_JSON_LEGACY_IMPL("Adventure Text Serializer JSON Legacy Impl", "net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-json-legacy-impl/4.23.0/adventure-text-serializer-json-legacy-impl-4.23.0.jar", "adventure-text-serializer-json-legacy-impl.jar", "BC2A23AD4612DDD7EADF5E6F7CCFE7FC"),
	
	/**
	 * Represents the <a href="https://docs.advntr.dev">Adventure Text Serializer Legacy</a> library.
	 */
	ADVENTURE_TEXT_SERIALIZER_LEGACY("Adventure Text Serializer Legacy", "net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/adventure-text-serializer-legacy/4.23.0/adventure-text-serializer-legacy-4.23.0.jar", "adventure-text-serializer-legacy.jar", "8C95E49B044CAB677C0D794EA1BDC5E2"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-codec/">Apache Commons Codec</a> library.
	 */
	APACHE_COMMONS_CODEC("Apache Commons Codec", "org.apache.commons.codec.Charsets", new Relocation("org.apache.commons.codec"), "https://repo1.maven.org/maven2/commons-codec/commons-codec/1.15/commons-codec-1.15.jar", "apache-commons-codec.jar", "303BAF002CE6D382198090AEDD9D79A2"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a> library.
	 */
	APACHE_COMMONS_COLLECTIONS("Apache Commons Collections", "org.apache.commons.collections4.Trie", new Relocation("org.apache.commons.collections4"), "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar", "apache-commons-collections.jar", "4A37023740719B391F10030362C86BE6"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-logging/">Apache Commons Logging</a> library.
	 */
	APACHE_COMMONS_LOGGING("Apache Commons Logging", "org.apache.commons.logging.Log", new Relocation("org.apache.commons.logging"), "https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar", "apache-commons-logging.jar", "040B4B4D8EAC886F6B4A2A3BD2F31B00"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-math/">Apache Commons Math</a> library.
	 */
	APACHE_COMMONS_MATH("Apache Commons Math", "org.apache.commons.math3.Field", new Relocation("assets.org.apache.commons.math3", "org.apache.commons.math3"), "https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar", "apache-commons-math.jar", "5B730D97E4E6368069DE1983937C508E"),
	
	/**
	 * Represents the <a href="https://hc.apache.org/httpcomponents-client/">Apache HttpClient</a> library.
	 */
	APACHE_HTTPCLIENT("Apache HttpClient", "org.apache.http.auth.Credentials", new Relocation("mozilla", "org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar", "apache-httpclient.jar", "2CB357C4B763F47E58AF6CAD47DF6BA3"),
	
	/**
	 * Represents the <a href="https://hc.apache.org/httpcomponents-core">Apache HttpCore</a> library.
	 */
	APACHE_HTTPCORE("Apache HttpCore", "org.apache.http.HttpHost", new Relocation("org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar", "apache-httpcore.jar", "28D2CD9BF8789FD2EC774FB88436EBD1"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM</a> library.
	 */
	ASM("ASM", "org.objectweb.asm.ModuleVisitor", "https://repo1.maven.org/maven2/org/ow2/asm/asm/7.2/asm-7.2.jar", "asm.jar", "26CF10DFD4729FD22FCAE0694E041167"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM Commons</a> library.
	 */
	ASM_COMMONS("ASM Commons", "org.objectweb.asm.commons.ClassRemapper", "https://repo1.maven.org/maven2/org/ow2/asm/asm-commons/7.2/asm-commons-7.2.jar", "asm-commons.jar", "321121317A6C6221CC26E8F9EE97022F"),
	
	/**
	 * Represents the <a href="https://checkerframework.org/">Checker Qual</a> library.
	 */
	CHECKER_QUAL("Checker Qual", "org.checkerframework.dataflow.qual.Deterministic", new Relocation("org.checkerframework"), "https://repo1.maven.org/maven2/org/checkerframework/checker-qual/3.21.0/checker-qual-3.21.0.jar", "checker-qual.jar", "275841B506F4B14EAF4E7892329DD26F"),
	
	/**
	 * Represents the <a href="https://errorprone.info/index">Error Prone Annotations</a> library.
	 */
	ERROR_PRONE_ANNOTATIONS("Error Prone Annotations", "com.google.errorprone.annotations.Var", new Relocation("com.google.errorprone.annotations"), "https://repo1.maven.org/maven2/com/google/errorprone/error_prone_annotations/2.10.0/error_prone_annotations-2.10.0.jar", "error-prone-annotations.jar", "DF93BD9F00B1D2B1E2CA6317FD8F6DF3"),
	
	/**
	 * Represents the <a href="https://github.com/KyoriPowered/examination">Examination API</a> library.
	 */
	EXAMINATION_API("Examination API", "net.kyori.examination.Examinable", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar", "examination-api.jar", "B1887361D811C89CCCA4DBF61B88DEF4"),
	
	/**
	 * Represents the <a href="https://github.com/KyoriPowered/examination">Examination String</a> library.
	 */
	EXAMINATION_STRING("Examination String", "net.kyori.examination.string.Strings", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/examination-string/1.3.0/examination-string-1.3.0.jar", "examination-string.jar", "9E4752EA3F53AE45E736C9D8F016F23D"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/com.google.guava/failureaccess">Failure Access</a> library.
	 */
	FAILURE_ACCESS("Failure Access", "com.google.common.util.concurrent.internal.InternalFutureFailureAccess", new Relocation("com.google.common.util.concurrent.internal"), "https://repo1.maven.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar", "failure-access.jar", "091883993EF5BFA91DA01DCC8FC52236"),
	
	/**
	 * Represents the <a href="https://findbugs.sourceforge.net/">FindBugs JSR305</a> library.
	 */
	FINDBUGS_JSR305("FindBugs JSR305", "javax.annotation.Nullable", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar", "findbugs-jsr305.jar", "DD83ACCB899363C32B07D7A1B2E4CE40"),
	
	/**
	 * Represents the <a href="https://trove4j.sourceforge.net/html/overview.html">GNU Trove</a> library.
	 */
	GNU_TROVE("GNU Trove", "gnu.trove.Version", new Relocation("gnu.trove"), "https://repo1.maven.org/maven2/net/sf/trove4j/core/3.1.0/core-3.1.0.jar", "gnu-trove.jar", "DB61170EB992D933F4119172326DF9B6"),
	
	/**
	 * Represents the <a href="https://github.com/google/gson">Gson</a> library.
	 */
	GSON("Gson", "com.google.gson.Gson", new Relocation("com.google.json"), "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar", "gson.jar", "DF6097815738CB31FC56391553210843"),
	
	/**
	 * Represents the <a href="https://guava.dev/">Guava</a> library.
	 */
	GUAVA("Guava", "com.google.common.annotations.GwtCompatible", new Relocation("com.google"), "https://repo1.maven.org/maven2/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar", "guava.jar", "BB811CA86CBA6506CCA5D415CD5559A7"),
	
	/**
	 * Represents the <a href="https://h2database.com/html/main.html">H2 Database Engine</a> library.
	 */
	H2_DATABASE_ENGINE("H2 Database Engine", "org.h2.Driver", "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar", "h2-driver.jar", "769D5A85D19CCC2B06620F8C81D6D8F8"),
	
	/**
	 * Represents the <a href="http://www.trustice.com/java/tar/">Ice TAR</a> library.
	 */
	ICE_TAR("Ice TAR", "com.ice.tar.TarProgressDisplay", new Relocation("com.ice.tar"), "https://repo1.maven.org/maven2/javatar/javatar/2.5/javatar-2.5.jar", "ice-tar.jar", "3E0E11B872EEE1822ED3C0690380D22A"),
	
	/**
	 * Represents the <a href="https://developers.google.com/j2objc">J2ObjC Annotations</a> library.
	 */
	J2OBJC_ANNOTATIONS("J2ObjC Annotations", "com.google.j2objc.annotations.Weak", new Relocation("com.google.j2objc.annotations"), "https://repo1.maven.org/maven2/com/google/j2objc/j2objc-annotations/1.3/j2objc-annotations-1.3.jar", "j2objc-annotations.jar", "5FA4EC4EC0C5AA70AF8A7D4922DF1931"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-annotations">Jackson Annotations</a> library.
	 */
	JACKSON_ANNOTATIONS("Jackson Annotations", "com.fasterxml.jackson.annotation.JacksonAnnotation", new Relocation("com.fasterxml.jackson.annotation"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.16.0/jackson-annotations-2.16.0.jar", "jackson-annotations.jar", "9CAEC6E7CDD5CC18E18F99359B9E8D95"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-core">Jackson Core</a> library.
	 */
	JACKSON_CORE("Jackson Core", "com.fasterxml.jackson.core.Versioned", new Relocation("com.fasterxml.jackson.core"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.16.0/jackson-core-2.16.0.jar", "jackson-core.jar", "E2510FA60B7D363566A9016A8C40710F"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-databind">Jackson Databind</a> library.
	 */
	JACKSON_DATABIND("Jackson Databind", "com.fasterxml.jackson.databind.annotation.NoClass", new Relocation("com.fasterxml.jackson.databind"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.16.0/jackson-databind-2.16.0.jar", "jackson-databind.jar", "F9AE3E3BD773BE8B474CB1B98DCF7EE6"),
	
	/**
	 * Represents the <a href="https://github.com/Remigio07/JAR-Relocator">JAR Relocator</a> library.
	 */
	JAR_RELOCATOR("JAR Relocator", "me.remigio07.jarrelocator.JarRelocator", "https://github.com/Remigio07/JAR-Relocator/releases/download/v1.6/JAR-Relocator-1.6.jar", "jar-relocator.jar", "673F248643DD9B3FBEDBC1FE01FD6F04"),
	
	/**
	 * Represents the <a href="https://github.com/java-native-access/jna">Java Native Access</a> library.
	 */
	JAVA_NATIVE_ACCESS("Java Native Access", "com.sun.jna.Version", new Relocation("com.sun.jna"), "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar", "java-native-access.jar", "2B884B01AD2F62D4F5DD11387A8BF799"),
	
	/**
	 * Represents the <a href="https://github.com/pengrad/java-telegram-bot-api/">Java Telegram Bot API</a> library.
	 */
	JAVA_TELEGRAM_BOT_API("Java Telegram Bot API", "com.pengrad.telegrambot.TelegramBot", new Relocation("com.pengrad.telegrambot"), "https://repo1.maven.org/maven2/com/github/pengrad/java-telegram-bot-api/7.9.1/java-telegram-bot-api-7.9.1.jar", "java-telegram-bot-api.jar", "A986C13BB1095CA698F46AB1B60583C7"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api">Javax Annotation</a> library.
	 */
	JAVAX_ANNOTATION("Javax Annotation", "javax.annotation.Priority", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar", "javax-annotation.jar", "2AB1973EEFFFAA2AEEC47D50B9E40B9D"),
	
	/**
	 * Represents the <a href="https://github.com/discord-jda/JDA">JDA</a> library.
	 */
	JDA("JDA", "net.dv8tion.jda.api.JDA", new Relocation("net.dv8tion.jda"), "https://repo1.maven.org/maven2/net/dv8tion/JDA/5.1.0/JDA-5.1.0.jar", "jda.jar", "E211D9DA4E6A68233AA97679693B8F79"),
	
	/**
	 * Represents the <a href="https://github.com/JetBrains/java-annotations">Jetbrains Annotations</a> library.
	 */
	JETBRAINS_ANNOTATIONS("Jetbrains Annotations", "org.jetbrains.annotations.Nullable", new Relocation("org.intellij.lang.annotations", "org.jetbrains.annotations"), "https://repo1.maven.org/maven2/org/jetbrains/annotations/24.1.0/annotations-24.1.0.jar", "jetbrains-annotations.jar", "38673ABE87D12508CD05498EFF0B9731"),
	
	/**
	 * Represents the <a href="https://cliftonlabs.github.io/json-simple/">JSON.simple</a> library.
	 */
	JSON_SIMPLE("JSON.simple", "com.github.cliftonlabs.json_simple.Jsonable", new Relocation("com.github.cliftonlabs.json_simple"), "https://repo1.maven.org/maven2/com/github/cliftonlabs/json-simple/4.0.0/json-simple-4.0.0.jar", "json-simple.jar", "8859BDA915254B3FA55ABBC17C21E67C"),
	
	/**
	 * Represents the <a href="https://kotlinlang.org/api/latest/jvm/stdlib/">Kotlin Stdlib</a> library.
	 */
	KOTLIN_STDLIB("Kotlin Stdlib", "kotlin.Deprecated", new Relocation(), "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.8.21/kotlin-stdlib-1.8.21.jar", "kotlin-stdlib.jar", "E4424CF44B4F8F7CD1517EAFDDA2F6A7"),
	
	/**
	 * Represents the <a href="https://square.github.io/okhttp">Logging Interceptor</a> library.
	 */
	LOGGING_INTERCEPTOR("Logging Interceptor", "okhttp.logging.HttpLoggingInterceptor", new Relocation("okhttp.logging"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/logging-interceptor/4.12.0/logging-interceptor-4.12.0.jar", "logging-interceptor.jar", "48029BA5A920BBB533503169CBA8E498"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/MaxMind-DB-Reader-java">MaxMind DB Reader</a> library.
	 */
	MAXMIND_DB_READER("MaxMind DB Reader", "com.maxmind.db.NodeCache", new Relocation("com.maxmind.db"), "https://repo1.maven.org/maven2/com/maxmind/db/maxmind-db/2.1.0/maxmind-db-2.1.0.jar", "maxmind-db-reader.jar", "E365D939445EF5AB91669A1C175D4E66"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/GeoIP2-java">MaxMind GeoIP2</a> library.
	 */
	MAXMIND_GEOIP2("MaxMind GeoIP2", "com.maxmind.geoip2.GeoIp2Provider", new Relocation("com.maxmind.geoip2"), "https://repo1.maven.org/maven2/com/maxmind/geoip2/geoip2/2.17.0/geoip2-2.17.0.jar", "maxmind-geoip2.jar", "4FD0A1FFF425A2C387D58D1DBB3E5FDD"),
	
	/**
	 * Represents the <a href="https://dev.mysql.com/doc/connector-j/en/">MySQL Connector/J</a> library.
	 */
	MYSQL_CONNECTOR_J("MySQL Connector/J", "com.mysql.jdbc.Driver", new Relocation("com.mysql"), "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar", "mysql-connector-j.jar", "DB2BDCFD7C2184780B5CDA29D8AF6997"),
	
	/**
	 * Represents the <a href="https://github.com/TakahikoKawasaki/nv-websocket-client">NV WebSocket Client</a> library.
	 */
	NV_WEBSOCKET_CLIENT("NV WebSocket Client", "com.neovisionaries.ws.client.Connectable", new Relocation("com.neovisionaries.ws.client"), "https://repo1.maven.org/maven2/com/neovisionaries/nv-websocket-client/2.14/nv-websocket-client-2.14.jar", "nv-websocket-client.jar", "27CF8D475BF9DCF035E0966810BDDED2"),
	
	/**
	 * Represents the <a href="https://square.github.io/okhttp/">OkHttp</a> library.
	 */
	OKHTTP("OkHttp", "okhttp3.Call", new Relocation("okhttp3"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.jar", "okhttp.jar", "6ACBA053AF88FED87E710C6C29911D7C"),
	
	/**
	 * Represents the <a href="https://square.github.io/okio/">Okio</a> library.
	 */
	OKIO("Okio", "okio.Source", new Relocation("okio"), "https://repo1.maven.org/maven2/com/squareup/okio/okio-jvm/3.6.0/okio-jvm-3.6.0.jar", "okio.jar", "26370180FF99A7E8A12DCAAC2A70CC6E"),
	
	/**
	 * Represents the <a href="https://github.com/KyoriPowered/option">Option</a> library.
	 */
	OPTION("Option", "net.kyori.option.Option", Relocation.KYORI_RELOCATION, "https://repo1.maven.org/maven2/net/kyori/option/1.1.0/option-1.1.0.jar", "option.jar", "39E0C47988FAA590EF8A26182853647C"),
	
	/**
	 * Represents the <a href="https://www.slf4j.org/">SLF4J API</a> library.
	 */
	SLF4J_API("SLF4J API", "org.slf4j.IMarkerFactory", new Relocation("org.slf4j"), "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar", "slf4j-api.jar", "872DA51F5DE7F3923DA4DE871D57FD85"),
	
	/**
	 * Represents the <a href="https://www.slf4j.org/">SLF4J Simple Provider</a> library.
	 */
	SLF4J_SIMPLE_PROVIDER("SLF4J Simple Provider", "org.slf4j.impl.SimpleLogger", new Relocation("org.slf4j.impl"), "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar", "slf4j-simple-provider.jar", "FD4A5FA54910B5EE561F639C903CF34A"),
	
	/**
	 * Represents the <a href="https://bitbucket.org/snakeyaml/snakeyaml">SnakeYAML</a> library.
	 */
	SNAKEYAML("SnakeYAML", "org.snakeyaml.yaml.Yaml", "https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.5/snakeyaml-2.5.jar", "snakeyaml.jar", "8D3B7581DB5C7620DB55183F33A4F2AD"), 
	
	/**
	 * Represents the <a href="https://github.com/xerial/sqlite-jdbc">SQLite JDBC</a> library.
	 */
	SQLITE_JDBC("SQLite JDBC", "org.sqlite.JDBC", "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.47.0.0/sqlite-jdbc-3.47.0.0.jar", "sqlite-driver.jar", "07B66539A5703FA7DEF2D8BBCBC51070");
	
	private String name, clazz, url, fileName, md5Hash;
	private Relocation relocation;
	
	static {
		Relocation.KYORI_RELOCATION.add(OPTION, JETBRAINS_ANNOTATIONS);
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
	
	private Library(String name, String clazz, String url, String fileName, String md5Hash) {
		this(name, clazz, null, url, fileName, md5Hash);
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
	 * <p>Will return <code>null</code> if this
	 * library does not need to be relocated.</p>
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
			return new URI(url).toURL();
		} catch (URISyntaxException | MalformedURLException e) {
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
		private static final Relocation KYORI_RELOCATION = new Relocation("net.kyori.adventure", "net.kyori.examination", "net.kyori.option");
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