package dev.enderpalm.pipette

import org.gradle.api.Plugin
import org.gradle.api.Project

class PipettePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.getTasks().register("prepareDependencyVersions", PrepareDependencyVersionsTask.class, task -> {
            task.setGroup("other")
            task.setDescription("Pipette: Compute Fabric & dependency versions from Minecraft version")
        })
    }
}
