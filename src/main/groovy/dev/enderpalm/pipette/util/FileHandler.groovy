package dev.enderpalm.pipette.util

import org.gradle.api.AntBuilder

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
}
