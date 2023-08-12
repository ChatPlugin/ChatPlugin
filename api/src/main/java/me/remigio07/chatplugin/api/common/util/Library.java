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
	
	ACTIVATION_FRAMEWORK("Activation Framework", "javax.activation.DataSource", new Relocation("com.sun.activation", "javax.activation"), "https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar", "activation-framework.jar"),
	APACHE_HTTPCLIENT("Apache HttpClient", "org.apache.http.auth.Credentials", new Relocation("mozilla", "org.apache.http"), "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar", "apache-httpclient.jar"),
	ASM("ASM", "org.objectweb.asm.ClassReader", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm/7.2/asm-7.2.jar", "asm.jar"),
	ASM_COMMONS("ASM Commons", "org.objectweb.asm.commons.Method", null, "https://repo1.maven.org/maven2/org/ow2/asm/asm-commons/7.2/asm-commons-7.2.jar", "asm-commons.jar"),
	CHECKER_QUAL("Checker Qual", "org.checkerframework.dataflow.qual.Deterministic", new Relocation("org.checkerframework"), "https://repo1.maven.org/maven2/org/checkerframework/checker-qual/3.21.0/checker-qual-3.21.0.jar", "checker-qual.jar"),
	COMMONS_COLLECTIONS("Commons Collections", "org.apache.commons.collections4.Trie", new Relocation("org.apache.commons.collections4"), "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar", "commons-collections.jar"),
	COMMONS_MATH("Commons Math", "org.apache.commons.math3.Field", new Relocation("assets.org.apache.commons.math3", "org.apache.commons.math3"), "https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar", "commons-math.jar"),
	ERROR_PRONE_ANNOTATIONS("Error Prone Annotations", "com.google.errorprone.annotations.Var", new Relocation("com.google.errorprone.annotations"), "https://repo1.maven.org/maven2/com/google/errorprone/error_prone_annotations/2.10.0/error_prone_annotations-2.10.0.jar", "error-prone-annotations.jar"),
	FAILURE_ACCESS("Failure Access", "com.google.common.util.concurrent.internal.InternalFutureFailureAccess", new Relocation("com.google.common.util.concurrent.internal"), "https://repo1.maven.org/maven2/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar", "failure-access.jar"),
	GNU_TROVE("GNU Trove", "gnu.trove.Version", new Relocation("gnu.trove"), "https://repo1.maven.org/maven2/net/sf/trove4j/trove4j/3.0.3/trove4j-3.0.3.jar", "gnu-trove.jar"),
	GUAVA("Guava", "com.google.common.annotations.GwtCompatible", new Relocation("com.google"), "https://repo1.maven.org/maven2/com/google/guava/guava/31.0.1-jre/guava-31.0.1-jre.jar", "guava.jar"),
	H2_DRIVER("H2", "org.h2.Driver", new Relocation("org.h2"), "https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar", "h2-driver.jar"),
	ICE_TAR("Ice TAR", "com.ice.tar.TarProgressDisplay", new Relocation("com.ice.tar"), "https://repo1.maven.org/maven2/javatar/javatar/2.5/javatar-2.5.jar", "ice-tar.jar"),
	J2OBJC_ANNOTATIONS("J2ObjC Annotations", "com.google.j2objc.annotations.Weak", new Relocation("com.google.j2objc.annotations"), "https://repo1.maven.org/maven2/com/google/j2objc/j2objc-annotations/1.3/j2objc-annotations-1.3.jar", "j2objc-annotations.jar"),
	JACKSON_ANNOTATIONS("Jackson Annotations", "com.fasterxml.jackson.annotation.JacksonAnnotation", new Relocation("com.fasterxml.jackson.annotation"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.13.1/jackson-annotations-2.13.1.jar", "jackson-annotations.jar"),
	JACKSON_CORE("Jackson Core", "com.fasterxml.jackson.core.Versioned", new Relocation("com.fasterxml.jackson.core"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.13.1/jackson-core-2.13.1.jar", "jackson-core.jar"),
	JACKSON_DATABIND("Jackson Databind", "com.fasterxml.jackson.databind.annotation.NoClass", new Relocation("com.fasterxml.jackson.databind"), "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.13.1/jackson-databind-2.13.1.jar", "jackson-databind.jar"),
	JAR_RELOCATOR("JAR Relocator", "me.remigio07.jarrelocator.JarRelocator", null, "https://github.com/Remigio07/JAR-Relocator/releases/download/v1.6/JAR-Relocator-1.6.jar", "jar-relocator.jar"),
	JAVA_NATIVE_ACCESS("Java Native Access", "com.sun.jna.Version", new Relocation("com.sun.jna"), "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.10.0/jna-5.10.0.jar", "java-native-access.jar"),
	JDA("JDA", "net.dv8tion.jda.api.JDA", new Relocation("com.iwebpp.crypto", "net.dv8tion.jda"), "https://jitpack.io/com/github/DV8FromTheWorld/JDA/4.4.0/JDA-4.4.0.jar", "jda.jar"),
	JETBRAINS_ANNOTATIONS("Jetbrains Annotations", "org.jetbrains.annotations.Nullable", new Relocation("org.intellij.lang.annotations", "org.jetbrains.annotations"), "https://repo1.maven.org/maven2/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar", "jetbrains-annotations.jar"),
	JSON_SIMPLE("JSON.simple", "com.github.cliftonlabs.json_simple.Jsonable", new Relocation("com.github.cliftonlabs.json_simple"), "https://repo1.maven.org/maven2/com/github/cliftonlabs/json-simple/4.0.0/json-simple-4.0.0.jar", "json-simple.jar"),
	JAVAX_ANNOTATION("Javax Annotation", "javax.annotation.Nullable", new Relocation("javax.annotation"), "https://repo1.maven.org/maven2/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar", "javax-annotation.jar"),
	KOTLIN_STDLIB("Kotlin Stdlib", "kotlin.Deprecated", new Relocation("kotlin"), "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.6.10/kotlin-stdlib-1.6.10.jar", "kotlin-stdlib.jar"),
	MAXMIND_DB_READER("MaxMind DB Reader", "com.maxmind.db.NodeCache", new Relocation("com.maxmind.db"), "https://repo1.maven.org/maven2/com/maxmind/db/maxmind-db/2.1.0/maxmind-db-2.1.0.jar", "maxmind-db-reader.jar"),
	MAXMIND_GEOIP2_API("MaxMind GeoIP2 API", "com.maxmind.geoip2.GeoIp2Provider", new Relocation("com.maxmind.geoip2"), "https://repo1.maven.org/maven2/com/maxmind/geoip2/geoip2/2.16.1/geoip2-2.16.1.jar", "maxmind-geoip2-api.jar"),
	MYSQL_CONNECTOR("MySQL Connector", "com.mysql.jdbc.Driver", new Relocation("com.mysql"), "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar", "mysql-driver.jar"),
	NV_WEBSOCKET_CLIENT("NV WebSocket Client", "com.neovisionaries.ws.client.Connectable", new Relocation("com.neovisionaries.ws.client"), "https://repo1.maven.org/maven2/com/neovisionaries/nv-websocket-client/2.14/nv-websocket-client-2.14.jar", "nv-websocket-client.jar"),
	OKHTTP("OkHttp", "okhttp3.Call", new Relocation("okhttp3"), "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/4.9.3/okhttp-4.9.3.jar", "ok-http.jar"),
	OKIO("Okio", "okio.Source", new Relocation("okio"), "https://repo1.maven.org/maven2/com/squareup/okio/okio/2.10.0/okio-2.10.0.jar", "okio.jar"),
	SLF4J_API("SLF4J API", "org.slf4j.IMarkerFactory", null, "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar", "slf4j-api.jar"),
	SQLITE_DRIVER("SQLite", "org.sqlite.JDBC", null, "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar", "sqlite-driver.jar"),
	TELEGRAM_BOTS("Telegram Bots", "org.telegram.telegrambots.Constants", new Relocation("org.telegram.telegrambots"), "https://repo1.maven.org/maven2/org/telegram/telegrambots/5.6.0/telegrambots-5.6.0.jar", "telegram-bots.jar"),
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
		MAXMIND_GEOIP2_API.getRelocation().add(IPLookupManager.LIBRARIES);
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