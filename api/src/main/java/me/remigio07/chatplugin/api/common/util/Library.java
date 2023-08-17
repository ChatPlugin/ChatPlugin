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

package me.remigio07.chatplugin.api.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.telegram.TelegramIntegrationManager;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents the libraries used by ChatPlugin.
 */
public enum Library {
	
	/**
	 * Represents the <a href="https://www.oracle.com/java/technologies/downloads.html">Activation Framework</a> library.
	 */
	ACTIVATION_FRAMEWORK("Activation Framework", "javax.activation.DataSource", new Relocation("com.sun.activation", "javax.activation"), "https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar", "activation-framework.jar"),
	
	/**
	 * Represents the <a href="https://hc.apache.org/httpcomponents-client/">Apache HttpClient</a> library.
	 */
	APACHE_HTTPCLIENT("Apache HttpClient", "org.apache.http.auth.Credentials", new Relocation("mozilla", "org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar", "apache-httpclient.jar"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM</a> library.
	 */
	ASM("ASM", "org.objectweb.asm.ClassReader", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm/7.2/asm-7.2.jar", "asm.jar"),
	
	/**
	 * Represents the <a href="https://asm.ow2.io/">ASM Commons</a> library.
	 */
	ASM_COMMONS("ASM Commons", "org.objectweb.asm.commons.Method", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm-commons/7.2/asm-commons-7.2.jar", "asm-commons.jar"),
	
	/**
	 * Represents the <a href="https://checkerframework.org/">Checker Qual</a> library.
	 */
	CHECKER_QUAL("Checker Qual", "org.checkerframework.dataflow.qual.Deterministic", new Relocation("org.checkerframework"), "https://repo1.maven.org/maven2/org/checkerframework/checker-qual/3.21.0/checker-qual-3.21.0.jar", "checker-qual.jar"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-collections/">Commons Collections</a> library.
	 */
	COMMONS_COLLECTIONS("Commons Collections", "org.apache.commons.collections4.Trie", new Relocation("org.apache.commons.collections4"), "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar", "commons-collections.jar"),
	
	/**
	 * Represents the <a href="https://commons.apache.org/proper/commons-math/">Commons Math</a> library.
	 */
	COMMONS_MATH("Commons Math", "org.apache.commons.math3.Field", new Relocation("assets.org.apache.commons.math3", "org.apache.commons.math3"), "https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar", "commons-math.jar"),
	
	/**
	 * Represents the <a href="https://errorprone.info/index">Error Prone Annotations</a> library.
	 */
	ERROR_PRONE_ANNOTATIONS("Error Prone Annotations", "com.google.errorprone.annotations.Var", new Relocation("com.google.errorprone.annotations"), "https://repo1.maven.org/maven2/com/google/errorprone/error_prone_annotations/2.10.0/error_prone_annotations-2.10.0.jar", "error-prone-annotations.jar"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/com.google.guava/failureaccess">Failure Access</a> library.
	 */
	FAILURE_ACCESS("Failure Access", "com.google.common.util.concurrent.internal.InternalFutureFailureAccess", new Relocation("com.google.common.util.concurrent.internal"), "https://repo1.maven.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar", "failure-access.jar"),
	
	/**
	 * Represents the <a href="https://trove4j.sourceforge.net/html/overview.html">GNU Trove</a> library.
	 */
	GNU_TROVE("GNU Trove", "gnu.trove.Version", new Relocation("gnu.trove"), "https://repo1.maven.org/maven2/net/sf/trove4j/trove4j/3.0.3/trove4j-3.0.3.jar", "gnu-trove.jar"),
	
	/**
	 * Represents the <a href="https://guava.dev/">Guava</a> library.
	 */
	GUAVA("Guava", "com.google.common.annotations.GwtCompatible", new Relocation("com.google"), "https://repo1.maven.org/maven2/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar", "guava.jar"),
	
	/**
	 * Represents the <a href="https://h2database.com/html/main.html">H2 Driver</a> library.
	 */
	H2_DRIVER("H2 Driver", "org.h2.Driver", new Relocation("org.h2"), "https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar", "h2-driver.jar"),
	
	/**
	 * Represents the <a href="http://www.trustice.com/java/tar/">Ice TAR</a> library.
	 */
	ICE_TAR("Ice TAR", "com.ice.tar.TarProgressDisplay", new Relocation("com.ice.tar"), "https://repo1.maven.org/maven2/javatar/javatar/2.5/javatar-2.5.jar", "ice-tar.jar"),
	
	/**
	 * Represents the <a href="https://developers.google.com/j2objc">J2ObjC Annotations</a> library.
	 */
	J2OBJC_ANNOTATIONS("J2ObjC Annotations", "com.google.j2objc.annotations.Weak", new Relocation("com.google.j2objc.annotations"), "https://repo1.maven.org/maven2/com/google/j2objc/j2objc-annotations/1.3/j2objc-annotations-1.3.jar", "j2objc-annotations.jar"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-annotations">Jackson Annotations</a> library.
	 */
	JACKSON_ANNOTATIONS("Jackson Annotations", "com.fasterxml.jackson.annotation.JacksonAnnotation", new Relocation("com.fasterxml.jackson.annotation"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.13.1/jackson-annotations-2.13.1.jar", "jackson-annotations.jar"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-core">Jackson Core</a> library.
	 */
	JACKSON_CORE("Jackson Core", "com.fasterxml.jackson.core.Versioned", new Relocation("com.fasterxml.jackson.core"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.13.1/jackson-core-2.13.1.jar", "jackson-core.jar"),
	
	/**
	 * Represents the <a href="https://github.com/FasterXML/jackson-databind">Jackson Databind</a> library.
	 */
	JACKSON_DATABIND("Jackson Databind", "com.fasterxml.jackson.databind.annotation.NoClass", new Relocation("com.fasterxml.jackson.databind"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.13.1/jackson-databind-2.13.1.jar", "jackson-databind.jar"),
	
	/**
	 * Represents the <a href="https://github.com/Remigio07/JAR-Relocator">JAR Relocator</a> library.
	 */
	JAR_RELOCATOR("JAR Relocator", "me.remigio07.jarrelocator.JarRelocator", null, "https://github.com/Remigio07/JAR-Relocator/releases/download/v1.6/JAR-Relocator-1.6.jar", "jar-relocator.jar"),
	
	/**
	 * Represents the <a href="https://github.com/java-native-access/jna">Java Native Access</a> library.
	 */
	JAVA_NATIVE_ACCESS("Java Native Access", "com.sun.jna.Version", new Relocation("com.sun.jna"), "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar", "java-native-access.jar"),
	
	/**
	 * Represents the <a href="https://github.com/discord-jda/JDA">JDA</a> library.
	 */
	JDA("JDA", "net.dv8tion.jda.api.JDA", new Relocation("com.iwebpp.crypto", "net.dv8tion.jda"), "https://jitpack.io/com/github/DV8FromTheWorld/JDA/4.4.0/JDA-4.4.0.jar", "jda.jar"),
	
	/**
	 * Represents the <a href="https://github.com/JetBrains/java-annotations">Jetbrains Annotations</a> library.
	 */
	JETBRAINS_ANNOTATIONS("Jetbrains Annotations", "org.jetbrains.annotations.Nullable", new Relocation("org.intellij.lang.annotations", "org.jetbrains.annotations"), "https://repo1.maven.org/maven2/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar", "jetbrains-annotations.jar"),
	
	/**
	 * Represents the <a href="https://cliftonlabs.github.io/json-simple/">JSON.simple</a> library.
	 */
	JSON_SIMPLE("JSON.simple", "com.github.cliftonlabs.json_simple.Jsonable", new Relocation("com.github.cliftonlabs.json_simple"), "https://repo1.maven.org/maven2/com/github/cliftonlabs/json-simple/4.0.0/json-simple-4.0.0.jar", "json-simple.jar"),
	
	/**
	 * Represents the <a href="https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api">Javax Annotation</a> library.
	 */
	JAVAX_ANNOTATION("Javax Annotation", "javax.annotation.Nullable", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar", "javax-annotation.jar"),
	
	/**
	 * Represents the <a href="https://kotlinlang.org/api/latest/jvm/stdlib/">Kotlin Stdlib</a> library.
	 */
	KOTLIN_STDLIB("Kotlin Stdlib", "kotlin.Deprecated", new Relocation("kotlin"), "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "kotlin-stdlib.jar"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/MaxMind-DB-Reader-java">MaxMind DB Reader</a> library.
	 */
	MAXMIND_DB_READER("MaxMind DB Reader", "com.maxmind.db.NodeCache", new Relocation("com.maxmind.db"), "https://repo1.maven.org/maven2/com/maxmind/db/maxmind-db/2.1.0/maxmind-db-2.1.0.jar", "maxmind-db-reader.jar"),
	
	/**
	 * Represents the <a href="https://github.com/maxmind/GeoIP2-java">MaxMind GeoIP2</a> library.
	 */
	MAXMIND_GEOIP2("MaxMind GeoIP2", "com.maxmind.geoip2.GeoIp2Provider", new Relocation("com.maxmind.geoip2"), "https://repo1.maven.org/maven2/com/maxmind/geoip2/geoip2/2.16.1/geoip2-2.16.1.jar", "maxmind-geoip2.jar"),
	
	/**
	 * Represents the <a href="https://dev.mysql.com/doc/connector-j/8.1/en/">MySQL Connector</a> library.
	 */
	MYSQL_CONNECTOR("MySQL Connector", "com.mysql.jdbc.Driver", new Relocation("com.mysql"), "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar", "mysql-driver.jar"),
	
	/**
	 * Represents the <a href="https://github.com/TakahikoKawasaki/nv-websocket-client">NV WebSocket Client</a> library.
	 */
	NV_WEBSOCKET_CLIENT("NV WebSocket Client", "com.neovisionaries.ws.client.Connectable", new Relocation("com.neovisionaries.ws.client"), "https://repo1.maven.org/maven2/com/neovisionaries/nv-websocket-client/2.14/nv-websocket-client-2.14.jar", "nv-websocket-client.jar"),
	
	/**
	 * Represents the <a href="https://square.github.io/okhttp/">OkHttp</a> library.
	 */
	OKHTTP("OkHttp", "okhttp3.Call", new Relocation("okhttp3"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.9.3/okhttp-4.9.3.jar", "ok-http.jar"),
	
	/**
	 * Represents the <a href="https://square.github.io/okio/">Okio</a> library.
	 */
	OKIO("Okio", "okio.Source", new Relocation("okio"), "https://repo1.maven.org/maven2/com/squareup/okio/okio/2.10.0/okio-2.10.0.jar", "okio.jar"),
	
	/**
	 * Represents the <a href="https://www.slf4j.org/">SLF4J API</a> library.
	 */
	SLF4J_API("SLF4J API", "org.slf4j.IMarkerFactory", null, "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar", "slf4j-api.jar"),
	
	/**
	 * Represents the <a href="https://github.com/xerial/sqlite-jdbc">SQLite Driver</a> library.
	 */
	SQLITE_DRIVER("SQLite Driver", "org.sqlite.JDBC", null, "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar", "sqlite-driver.jar"),
	
	/**
	 * Represents the <a href="https://github.com/rubenlagus/TelegramBots">Telegram Bots</a> library.
	 */
	TELEGRAM_BOTS("Telegram Bots", "org.telegram.telegrambots.Constants", new Relocation("org.telegram.telegrambots"), "https://repo1.maven.org/maven2/org/telegram/telegrambots/5.6.0/telegrambots-5.6.0.jar", "telegram-bots.jar"),
	
	/**
	 * Represents the <a href="https://github.com/rubenlagus/TelegramBots">Telegram Bots Meta</a> library.
	 */
	TELEGRAM_BOTS_META("Telegram Bots Meta", "org.telegram.telegrambots.meta.ApiConstants", new Relocation("org.telegram.telegrambots.meta"), "https://repo1.maven.org/maven2/org/telegram/telegrambots-meta/5.6.0/telegrambots-meta-5.6.0.jar", "telegram-bots-meta.jar");
	
	/**
	 * Prefix used for relocation.
	 * 
	 * <p><strong>Value:</strong> "me.remigio07.chatplugin.common.lib."</p>
	 */
	public static final String PREFIX = "me.remigio07.chatplugin.common.lib.";
	private String name, clazz, url, fileName;
	private Relocation relocation;
	
	static {
		JACKSON_DATABIND.getRelocation().add(JACKSON_CORE, JACKSON_ANNOTATIONS);
		JDA.getRelocation().add(DiscordIntegrationManager.LIBRARIES);
		KOTLIN_STDLIB.getRelocation().add(JETBRAINS_ANNOTATIONS);
		MAXMIND_GEOIP2.getRelocation().add(IPLookupManager.LIBRARIES);
		OKHTTP.getRelocation().add(OKIO, KOTLIN_STDLIB);
		OKIO.getRelocation().add(KOTLIN_STDLIB);
		TELEGRAM_BOTS.getRelocation().add(TelegramIntegrationManager.LIBRARIES);
		TELEGRAM_BOTS_META.getRelocation().add(TelegramIntegrationManager.LIBRARIES);
	}
	
	private Library(String name, String clazz, Relocation relocation, String url, String fileName) {
		this.name = name;
		this.relocation = relocation;
		this.url = url;
		this.fileName = fileName;
		
		switch (name()) {
		case "ASM":
		case "ASM_COMMONS":
		case "JAR_RELOCATOR":
			this.clazz = clazz;
			break;
		default:
			this.clazz = PREFIX + clazz;
			break;
		}
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
		 * Adds libraries to this relocation.
		 * 
		 * @param libraries Libraries to add
		 * @return This relocation
		 */
		public Relocation add(Library... libraries) {
			for (Library library : libraries)
				if (library.getRelocation() != null && !oldPackages.containsAll(library.getRelocation().getOldPackages()))
					oldPackages.addAll(library.getRelocation().getOldPackages());
			return this;
		}
		
		/**
		 * Gets the list of old packages.
		 * 
		 * @return Old packages list
		 */
		public List<String> getOldPackages() {
			return oldPackages;
		}
		
	}
	
}