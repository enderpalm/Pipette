package dev.enderpalm.pipette;

import dev.enderpalm.pipette.util.FabricVersionRetriever;
import dev.enderpalm.pipette.util.FileHandler;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrepareDependencyVersionsTask extends DefaultTask {

    private String minecraft;
    private final Map<String, String> properties = new HashMap<>();

    public PrepareDependencyVersionsTask() {
    }

    @Option(option = "minecraft", description = "Minecraft version to use for dependencies")
    void setMinecraft(String minecraft) {
        this.minecraft = minecraft;
    }

    @OptionValues("minecraft")
    Collection<String> getSupportedMinecraftVersions() {
        return FabricVersionRetriever.getInstance().listGameVersions();
    }

    @TaskAction
    void prepareDependencyVersions() {
        var retriever = FabricVersionRetriever.getInstance();
        String stable = retriever.validateVersionAndFindStable(this.minecraft);
        if (stable == null) {
            throw new IllegalArgumentException("Invalid Minecraft version: " + this.minecraft);
        }
        this.properties.put("minecraft_version", this.minecraft);
        this.properties.put("yarn_mappings", retriever.getYarnMappingVersion(this.minecraft));
        this.properties.put("loader_version", retriever.getLatestLoaderVersion());
        this.properties.put("fabric_version", retriever.getFabricApiVersion(this.minecraft, stable));
        FileHandler.getInstance().modifyGradleProperties(this.getProject().getAnt(), this.properties);
    }
}
