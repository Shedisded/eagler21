plugins {
	id("java-library")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven("https://libraries.minecraft.net")
}

sourceSets {
	named("main") {
		java.setSrcDirs(listOf("src/game/java"))
		resources.setSrcDirs(listOf("src/game/resources"))
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.compilerArgs.addAll(listOf("-nowarn", "-proc:none", "-Xmaxerrs", "10000"))
	options.isDeprecation = false
	options.isFork = true
	options.forkOptions.memoryMaximumSize = "4g"
}

val lwjglVersion = "3.3.3"
val lwjglNatives = "natives-linux"
val nettyVersion = "4.2.7.Final"

dependencies {
	api("at.yawk.lz4:lz4-java:1.8.1")
	api("com.google.code.gson:gson:2.13.2")
	api("com.google.guava:failureaccess:1.0.3")
	api("com.google.guava:guava:33.5.0-jre")
	api("com.ibm.icu:icu4j:77.1")
	api("com.mojang:authlib:7.0.61")
	api("com.mojang:blocklist:1.0.10")
	api("com.mojang:brigadier:1.3.10")
	api("com.mojang:datafixerupper:9.0.19")
	api("com.mojang:logging:1.6.11") {
		exclude(group = "org.apache.logging.log4j")
	}
	api("commons-codec:commons-codec:1.19.0")
	api("commons-io:commons-io:2.20.0")
	api("io.netty:netty-buffer:$nettyVersion")
	api("io.netty:netty-codec-base:$nettyVersion")
	api("io.netty:netty-codec-compression:$nettyVersion")
	api("io.netty:netty-codec-http:$nettyVersion")
	api("io.netty:netty-common:$nettyVersion")
	api("io.netty:netty-handler:$nettyVersion")
	api("io.netty:netty-resolver:$nettyVersion")
	api("io.netty:netty-transport:$nettyVersion")
	api("io.netty:netty-transport-classes-epoll:$nettyVersion")
	api("io.netty:netty-transport-classes-kqueue:$nettyVersion")
	api("io.netty:netty-transport-native-unix-common:$nettyVersion")
	api("it.unimi.dsi:fastutil:8.5.18")
	api("net.sf.jopt-simple:jopt-simple:5.0.4")
	api("org.apache.commons:commons-compress:1.28.0")
	api("org.apache.commons:commons-lang3:3.19.0")
	api("org.slf4j:slf4j-simple:2.0.17")
	api("org.jcraft:jorbis:0.0.17")
	api("org.joml:joml:1.10.8")
	api("org.jspecify:jspecify:1.0.0")
	api("org.slf4j:slf4j-api:2.0.17")
	compileOnly("org.jetbrains:annotations:26.0.2")

	api("org.lwjgl:lwjgl:$lwjglVersion")
	api("org.lwjgl:lwjgl-freetype:$lwjglVersion")
	api("org.lwjgl:lwjgl-glfw:$lwjglVersion")
	api("org.lwjgl:lwjgl-jemalloc:$lwjglVersion")
	api("org.lwjgl:lwjgl-openal:$lwjglVersion")
	api("org.lwjgl:lwjgl-opengl:$lwjglVersion")
	api("org.lwjgl:lwjgl-stb:$lwjglVersion")
	api("org.lwjgl:lwjgl-tinyfd:$lwjglVersion")
	runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-freetype:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-jemalloc:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")
	runtimeOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:$lwjglNatives")
	runtimeOnly("com.mojang:patchy:2.2.10")
	runtimeOnly("io.netty:netty-transport-native-epoll:$nettyVersion:linux-x86_64")
}

tasks.register<JavaExec>("runClient") {
	group = "minecraft"
	description = "Run the Minecraft 1.21.11 client"
	classpath = sourceSets["main"].runtimeClasspath
	mainClass = "net.minecraft.client.main.Main"
	workingDir = file("run")
	doFirst { workingDir.mkdirs() }
	// Bootstrap.wrapStreams() points System.err at slf4j; slf4j-simple must
	// cache the real stream up front or logging recurses into itself.
	jvmArgs("-Xmx4G", "-Dorg.slf4j.simpleLogger.cacheOutputStream=true")
	args(
		"--accessToken", "0",
		"--version", "1.21.11-port",
		"--gameDir", ".",
		"--assetsDir", rootProject.file("game/assets").absolutePath,
		"--assetIndex", "29",
		"--username", "Dev",
		"--offlineDeveloperMode",
	)
}
