package dev.enderpalm.pipette.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.AntBuilder

import java.nio.file.Paths

class FileHandler {

    static FileHandler getInstance() {
        return new FileHandler()
    }

    void modifyGradleProperties(AntBuilder ant, Map<String, String> properties) {
        ant.propertyfile(file: "gradle.properties") {
            properties.each { key, value ->
                entry(key: key, value: value)
            }
        }
    }

    Object modifyFabricModJson(File projectDir, String loader, String minecraft, int java) {
        def path = Paths.get(projectDir.getAbsolutePath(), "src", "main", "resources", "fabric.mod.json")
        def json = new JsonSlurper().parseText(path.toFile().text)
        json.depends.fabricloader = ">=$loader"
        json.depends.minecraft = "~$minecraft"
        json.depends.java = ">=$java"
        path.toFile().text = JsonOutput.prettyPrint(JsonOutput.toJson(json))
        return json
    }

    void modifyMixinJson(File projectDir, Object fabricJson ,int java){
        String[] mixinList = fabricJson.mixins
        Iterator mixin = mixinList.iterator()
        while (mixin.hasNext()) {
            def path = Paths.get(projectDir.getAbsolutePath(), "src", "main", "resources", mixin.next())
            def json = new JsonSlurper().parseText(path.toFile().text)
            json.compatibilityLevel = "JAVA_$java"
            path.toFile().text = JsonOutput.prettyPrint(JsonOutput.toJson(json))
        }
    }
}
