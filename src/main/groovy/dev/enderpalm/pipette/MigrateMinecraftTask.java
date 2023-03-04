package dev.enderpalm.pipette;

import dev.enderpalm.pipette.util.FabricVersionRetriever;
import dev.enderpalm.pipette.util.FileHandler;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.util.*;

public class MigrateMinecraftTask extends DefaultTask {

    private String target;
    private final Map<String, String> properties = new HashMap<>();

    public MigrateMinecraftTask() {
    }

    @Option(option = "ver", description = "Minecraft version to use for dependencies")
    void setTarget(String target) {
        this.target = target;
    }

    @OptionValues("ver")
    Collection<String> getValidMinecraftVersions() {
        return FabricVersionRetriever.getInstance().listGameVersions();
    }

    @TaskAction
    void prepareDependencyVersions() {
        var retriever = FabricVersionRetriever.getInstance();
        var fileHandler = FileHandler.getInstance();
        var project = this.getProject();
        String stable = retriever.validateVersionAndFindStable(this.target);
        if (stable == null) {
            throw new IllegalArgumentException("Invalid Minecraft version: " + this.target);
        }
        var loader = retriever.getLatestLoaderVersion();
        var java = retriever.getJavaVersion(this.target, stable);

        this.properties.put("minecraft_version", this.target);
        this.properties.put("loader_version", loader);
        this.properties.put("yarn_mappings", retriever.getYarnMappingVersion(this.target));
        this.properties.put("fabric_version", retriever.getFabricApiVersion(this.target));

        fileHandler.modifyGradleProperties(project.getAnt(), this.properties);
        Object json = fileHandler.modifyFabricModJson(project.getProjectDir(), loader, stable, java);
        fileHandler.modifyMixinJson(project.getProjectDir(), json, java);
    }
}
