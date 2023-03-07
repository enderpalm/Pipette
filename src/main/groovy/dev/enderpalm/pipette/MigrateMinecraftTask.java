package dev.enderpalm.pipette;

import dev.enderpalm.pipette.util.FabricVersionRetriever;
import dev.enderpalm.pipette.util.FileHandler;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class MigrateMinecraftTask extends DefaultTask {

    private String target;
    private final Map<String, String> properties = new HashMap<>();
    private static final List<String> validVersions = FabricVersionRetriever.getInstance().listGameVersions();
    private static final Map<String, Function<Void, String>> keywords = new HashMap<>();

    public MigrateMinecraftTask() {
    }

    @Option(option = "ver", description = "Minecraft version to use for dependencies")
    void setTarget(String target) {
        this.target = target;
    }

    @OptionValues("ver")
    Collection<String> getValidMinecraftVersions() {
        validVersions.addAll(keywords.keySet());
        return validVersions;
    }

    @TaskAction
    void prepareDependencyVersions() {
        var retriever = FabricVersionRetriever.getInstance();
        var fileHandler = FileHandler.getInstance();
        var project = this.getProject();
        if (keywords.containsKey(this.target)) {
            keywords.get(this.target).apply(null);
            return;
        }
        String stable = retriever.validateVersionAndFindStable(this.target);
        if (stable == null) {
            throw new IllegalArgumentException("Invalid Minecraft version: " + this.target);
        }
        var loader = retriever.getLatestLoaderVersion();
        var java = retriever.getJavaVersion(this.target, validVersions);

        this.properties.put("minecraft_version", this.target);
        this.properties.put("loader_version", loader);
        this.properties.put("yarn_mappings", retriever.getYarnMappingVersion(this.target));
        this.properties.put("fabric_version", retriever.getFabricApiVersion(this.target));

        fileHandler.modifyGradleProperties(project.getAnt(), this.properties);
        Object json = fileHandler.modifyFabricModJson(project.getProjectDir(), loader, stable, java);
        fileHandler.modifyMixinJson(project.getProjectDir(), json, java);
    }

    static @NotNull String bold(String text) {
        return "\033[0;1m" + text + "\033[0;0m";
    }

    static {
        keywords.put("list", (Void) -> {
            int wrapLength = 86, lineLength = 0;
            List<String> specialVersions = FabricVersionRetriever.getSpecialVersionsMap().keySet().stream().toList();
            List<String> normalVersions = validVersions.stream().filter(version -> !specialVersions.contains(version)).toList();
            System.out.println("List of available Minecraft versions:\n");

            System.out.println(bold("Release & Dev versions") + " <release>: <dev>");
            var instance = FabricVersionRetriever.getInstance();
            String stable = instance.validateVersionAndFindStable(normalVersions.get(0));
            for (int i = 0; i < normalVersions.size(); i++) {
                String current = normalVersions.get(i);
                String nextStable = i + 1 < normalVersions.size() ? instance.validateVersionAndFindStable(normalVersions.get(i + 1)) : null;
                String spacer = Pattern.matches("\\d+\\.\\d+\\d\\.\\d", current) ? "" : "  ";
                StringBuilder builder = new StringBuilder();
                if (stable.equals(current)) {
                    builder.append("\n\t").append(spacer).append(current).append(": ");
                    if (nextStable != null && !nextStable.equals(stable)) builder.append("none");
                    lineLength = 0;
                }
                else if (i == 0) builder.append("\t  none: ").append(current).append(", ");
                else {
                    builder.append(current);
                    if (nextStable != null && nextStable.equals(stable)) builder.append(", ");
                    lineLength += current.length() + 2;
                    if (lineLength > wrapLength) {
                        builder.append("\n\t\t\t");
                        lineLength = 0;
                    }
                }
                System.out.print(builder);
                stable = nextStable;
            }

            lineLength = 0;
            System.out.print("none\n\n" + bold("Special versions\n\t"));
            for (String version : specialVersions) {
                System.out.print(version);
                if (!version.equals(specialVersions.get(specialVersions.size() - 1))) System.out.print(", ");
                lineLength += version.length() + 2;
                if (lineLength > wrapLength) {
                    System.out.print("\n\t");
                    lineLength = 0;
                }
            }
            return null;
        });
    }

}
