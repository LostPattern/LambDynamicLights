import lambdynamiclights.Constants

plugins {
	id("lambdynamiclights")
}

repositories {
	maven {
		name = "Gegy"
		url = uri("https://maven.gegy.dev")
	}
	maven {
		name = "NeoForged"
		url = uri("https://maven.neoforged.net/releases")
	}
}

base.archivesName.set(Constants.NAME + "-api")

dependencies {
	neoForge("net.neoforged:neoforge:21.1.66")
}

// Configure the maven publication.
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])

			groupId = "$group.lambdynamiclights"
			artifactId = "lambdynamiclights-api"

			pom {
				name.set("LambDynamicLights (API)")
				description.set("API for LambDynamicLights, a mod which adds dynamic lighting to Minecraft.")
			}
		}
	}
}
