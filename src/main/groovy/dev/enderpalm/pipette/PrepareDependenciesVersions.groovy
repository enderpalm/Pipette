package dev.enderpalm.pipette

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.options.Option

abstract class PrepareDependenciesVersions extends DefaultTask{

    private String minecraft
    private String loader
    private String yarn
    private String fabric

    @Option(option = "minecraft", description = "Minecraft version")
    void setMinecraftVersion(String minecraft) {
        this.minecraft = minecraft
    }

}