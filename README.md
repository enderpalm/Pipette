# Pipette  
![CI](https://github.com/enderpalm/Pipette/actions/workflows/dev-build.yml/badge.svg)

Simple Gradle plugin providing Fabric dependency versions (other dependencies planned) and updates Minecraft mapping accordingly, cuz I'm lazy to copy them from [Fabric's web](https://fabricmc.net/develop/) and some API versions they provided aren't compatible with their own mod set `:(`

> **Note** :  Fabric API and Minecraft versions are retrieved from [Modrinth API](https://api.modrinth.com/v2/project/P7dR8mSH/version) while other Fabric stuffs are retrieved from [FabricMC web service](https://meta.fabricmc.net), making some Minecraft version unavailable :(

## Usage Guide
### Integration
To use this plugin, you have to include it in your `build.gradle` file. Note that you might also want to add `gradlePluginPortal()` in **settings.gradle**, just in case it doesn't present.


**Groovy Gradle**
```gradle
// -- build.gradle -- // 
plugins{
  id "dev.enderpalm.pipette" version "1.+"
}

// -- settings.gradle -- //
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

**Alternative method for getting plugin** </br>
Pipette can be retrieved from GitHub Package repository which requires your own **GitHub token with read-access** and use it as shown below: 
```gradle
pluginManagement {
    repositories {
        maven{
            name = 'GitHubPackages'
            url = uri("https://maven.pkg.github.com/enderpalm/Pipette")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```
- Useful links on [GitHub Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#creating-a-personal-access-token-classic) and [Consuming Maven package from GitHub package registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

### Task Command

Pipette `migrateMinecraft` task accepts `--ver` as arg to set target Minecraft version, like this:
```cmd
./gradlew migrateMinecraft --ver "1.19.2"
```
After running this command, your **fabric.mod.json**, **\*.mixins.json**, **gradle.properties** should have theirs token changed according with prompted `--ver`. Gradle will rebuild your project and downloading dependencies and mapping automatically. *PS: I only tested it in IntelliJ Idea, VScode and Eclipse aren't yet tested.*

> **Warning** Due to error of Fabric loader unknown to me, you need to delete `fabric-api` key in `depends` block in `fabric.mod.json`, or loader will split out **Incompatible mod set** error.

<p align=center> Made with <b>🤍</b> using <a href="https://github.com/enderpalm/Pipette/blob/master/LICENSE">MIT License</a></p>
  
  

