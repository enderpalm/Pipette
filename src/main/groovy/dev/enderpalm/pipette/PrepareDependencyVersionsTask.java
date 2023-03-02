package dev.enderpalm.pipette;

import dev.enderpalm.pipette.util.FabricVersionRetriever;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.util.Collection;

public class PrepareDependencyVersionsTask extends DefaultTask {

    private String minecraft;

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
        var loader = retriever.getLatestLoaderVersion();
        var yarn = retriever.getYarnMappingVersion(this.minecraft);
        var api = retriever.getFabricApiVersion(this.minecraft, stable);
    }
}
