# Pipette  
![CI](https://github.com/enderpalm/Pipette/actions/workflows/dev-build.yml/badge.svg)

Simple Gradle plugin providing Fabric dependency versions (other dependencies planned) and updates Minecraft mapping accordingly, cuz I'm lazy to copy them from [Fabric's web](https://fabricmc.net/develop/) and some API versions they provided aren't compatible with their own mod set `:(`

> **Note** :  Fabric API and Minecraft versions are retrieved from [Modrinth API](https://api.modrinth.com/v2/project/P7dR8mSH/version) while other Fabric stuffs are retrieved from [FabricMC web service](https://meta.fabricmc.net), resulting in little amount of versions left unavailable :(

## Usage Guide
### Integration
To use this plugin, you have to include it in your `build.gradle` file. Noted that you might also want to add `gradlePluginPortal()` in **settings.gradle**, just in case it doesn't present.


**Groovy Gradle**
```groovy
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

### Task Command

Pipette `migrateMinecraft` task accepts `--ver` as arg to set target Minecraft version, like this:
```cmd
./gradlew migrateMinecraft --ver "1.19.2"
```
After running this command, your **fabric.mod.json**, **\*.mixins.json**, **gradle.properties** should change theirs token respected to inputted `--ver` and gradle should start downloading dependencies and mapping automatically.

<p align=center> Made with <b>♥</b> using <a href="https://github.com/enderpalm/Pipette/blob/master/LICENSE">MIT License</a></p>
  
  

