import com.modrinth.minotaur.dependencies.ModDependency
import lambdynamiclights.Constants
import lambdynamiclights.Utils
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
	id("lambdynamiclights")
	`maven-publish`
	id("com.gradleup.shadow").version("8.3.3")
	id("com.modrinth.minotaur").version("2.+")
	id("net.darkhax.curseforgegradle").version("1.1.+")
}

base.archivesName.set(Constants.NAME)

if (!(System.getenv("CURSEFORGE_TOKEN") != null || System.getenv("MODRINTH_TOKEN") != null || System.getenv("LDL_MAVEN") != null)) {
	//version = (version as String) + "-local"
}
logger.lifecycle("Preparing version ${version}...")

repositories {
	mavenLocal()
	mavenCentral()
	maven {
		name = "Terraformers"
		url = uri("https://maven.terraformersmc.com/releases/")
	}
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev")
	}
	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")
	}
	maven {
		name = "Ladysnake Libs"
		url = uri("https://maven.ladysnake.org/releases")
	}
	maven { url = uri("https://maven.wispforest.io/releases") }
	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
	maven {
		name = "NeoForged"
		url = uri("https://maven.neoforged.net/releases")
	}
	maven {
		url = uri("https://maven.su5ed.dev/releases")
	}

	maven {
		name = "OctoStudios"
		url = uri("https://maven.octo-studios.com/releases")
	}

	mavenLocal()
}

loom {
	accessWidenerPath = file("src/main/resources/lambdynlights.accesswidener")
}

dependencies {
	//implementation(project(":api", configuration = "namedElements"))

	//modImplementation(libs.fabric.api)

	//implementation(libs.nightconfig.core)
	//implementation(libs.nightconfig.toml)
	modImplementation(libs.spruceui)
	include(libs.spruceui)
	modImplementation(libs.pridelib)
	include(libs.pridelib)


	//modImplementation(libs.modmenu) {
	//	this.isTransitive = false
	//}

	// Mod compatibility
	//modCompileOnly(libs.trinkets)
	modCompileOnly(libs.accessories)

	//modRuntimeOnly(libs.sodium)

	//forgeRuntimeLibrary(project(":api", configuration = "namedElements"))
	forgeRuntimeLibrary("org.jetbrains:annotations:26.0.1")

	forgeRuntimeLibrary(libs.yumi.commons.core) {
		isTransitive = false
	}
	forgeRuntimeLibrary(libs.yumi.commons.collections) {
		isTransitive = false
	}
	forgeRuntimeLibrary(libs.yumi.commons.event) {
		isTransitive = false
	}

	//shadow(project(":api", configuration = "namedElements")) {
	//	isTransitive = false
	//}
	shadow(libs.yumi.commons.core) {
		isTransitive = false
	}
	shadow(libs.yumi.commons.collections) {
		isTransitive = false
	}
	shadow(libs.yumi.commons.event) {
		isTransitive = false
	}
	//shadow(libs.nightconfig.core)
	//shadow(libs.nightconfig.toml)

	neoForge("net.neoforged:neoforge:21.1.66")
	modCompileOnly("top.theillusivec4.curios:curios-neoforge:9.0.15+1.21.1")

}

tasks.processResources {
	val version = project.version
	inputs.property("version", version)

	filesMatching("META-INF/neoforge.mods.toml") {
		expand("version" to version)
	}
}

tasks.shadowJar {
	dependsOn(tasks.jar)
	configurations = listOf(project.configurations["shadow"])
	destinationDirectory.set(file("${project.layout.buildDirectory.get()}/devlibs"))
	archiveClassifier.set("dev")

	//relocate("com.electronwill.nightconfig", "dev.lambdaurora.lambdynlights.shadow.nightconfig")
}

tasks.remapJar {
	dependsOn(tasks.shadowJar)
}

modrinth {
	projectId = project.property("modrinth_id") as String
	versionName = "LambDynamicLights ${Constants.VERSION} (${Constants.mcVersion()})"
	uploadFile.set(tasks.remapJar.get())
	loaders.set(listOf("fabric", "quilt"))
	gameVersions.set(listOf(Constants.mcVersion()))
	versionType.set(Constants.getVersionType())
	syncBodyFrom.set(Utils.parseReadme(project))
	dependencies.set(
		listOf(
			ModDependency("P7dR8mSH", "required")
		)
	)

	// Changelog fetching
	val changelogContent = Utils.fetchChangelog(project)

	if (changelogContent != null) {
		changelog.set(changelogContent)
	} else {
		afterEvaluate {
			tasks.modrinth.get().isEnabled = false
		}
	}
}

tasks.modrinth {
	dependsOn(tasks.modrinthSyncBody)
}

tasks.register<TaskPublishCurseForge>("curseforge") {
	this.group = "publishing"

	val token = System.getenv("CURSEFORGE_TOKEN")
	if (token != null) {
		this.apiToken = token
	} else {
		this.isEnabled = false
		return@register
	}

	// Changelog fetching
	var changelogContent = Utils.fetchChangelog(project)

	if (changelogContent != null) {
		changelogContent = "Changelog:\n\n${changelogContent}"
	} else {
		this.isEnabled = false
		return@register
	}

	val mainFile = upload(project.property("curseforge_id"), tasks.remapJar.get())
	mainFile.releaseType = Constants.getVersionType()
	mainFile.addGameVersion(Constants.mcVersion())
	mainFile.addModLoader("Fabric", "Quilt")
	mainFile.addJavaVersion("Java 21", "Java 22")
	mainFile.addEnvironment("Client")

	mainFile.displayName = "LambDynamicLights ${Constants.VERSION} (${Constants.mcVersion()})"
	mainFile.addRequirement("fabric-api")
	mainFile.addOptional("modmenu")
	mainFile.addIncompatibility("optifabric")

	mainFile.changelogType = "markdown"
	mainFile.changelog = changelogContent
}

// Configure the maven publication.
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])

			groupId = "$group.lambdynamiclights"
			artifactId = "lambdynamiclights-runtime"

			pom {
				name.set("LambDynamicLights")
				description.set("Adds dynamic lighting to Minecraft.")
			}
		}
	}
}
