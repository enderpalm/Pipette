package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper
import org.gradle.internal.impldep.org.jetbrains.annotations.Nullable

class VersionRetriever {

    // Hostname for the Fabric's web service
    static String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    static String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]
    static String nextStable = "1.19.4"

    static @Nullable String validateVersionAndFindStable(String target) {
        def isValid = false
        @Nullable String stable = null
        Iterator validGameVersion = jsonSlurp("/v2/versions/game").iterator()
        while (validGameVersion.hasNext()) {
            def v = validGameVersion.next()
            stable = v.stable ? v.version : stable
            if (v.version == target) {
                isValid = true
                break
            }
        }
        return isValid ? (stable ?: nextStable) : null
    }

    static String getYarnMappingVersion(String target) {
        Iterator yarn = jsonSlurp("/v2/versions/yarn").iterator()
        while (yarn.hasNext()) {
            def v = yarn.next()
            if (v.gameVersion == target) return v.version
        }
        return "Error: No yarn mappings found for game version $target :("
    }

    static String getLatestLoaderVersion() {
        Iterator loader = jsonSlurp("/v2/versions/loader").iterator()
        while (loader.hasNext()) {
            def v = loader.next()
            if (v.stable) return v.version
        }
        return "Error: No loader version found :("
    }

    static String getFabricApiVersion(String target, String stable){
        return null
    }

    static Object jsonSlurp(String url) {
        return new JsonSlurper().parseText(getInputStream(meta, url).getText())
    }

    static InputStream getInputStream(String[] hostname, String path) {
        for (String host : hostname) {
            try {
                return new URL(host + path).openConnection().getInputStream()
            } catch (Exception e) {
                e.printf("Pipette: Failed to connect to %s :(", host + path)
            }
        }
        throw new Exception("Pipette: Failed to connect to any host :(")
    }

}
