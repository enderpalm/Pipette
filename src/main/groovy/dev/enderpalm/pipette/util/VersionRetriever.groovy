package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper

class VersionRetriever {

    // Hostname for the Fabric's web service
    static String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    static String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]

    static boolean validateMinecraftVersion(String version) {
        Iterator validGameVersion = jsonSlurp("/v2/versions/game").iterator()
        while (validGameVersion.hasNext()) {
            def v = validGameVersion.next()
            if (v.version == version) {
                return true
            }
        }
        return false
    }

    static String getYarnMappingVersion(String minecraftVersion) {
        Iterator yarn = jsonSlurp("/v2/versions/yarn").iterator()
        while (yarn.hasNext()) {
            def v = yarn.next()
            if (v.gameVersion == minecraftVersion) return v.version
        }
        return "Error: No yarn mappings found for game version $minecraftVersion :("
    }

    static String getLatestLoaderVersion() {
        Iterator loader = jsonSlurp("/v2/versions/loader").iterator()
        while (loader.hasNext()) {
            def v = loader.next()
            if (v.stable) return v.version
        }
        return "Error: No loader version found :("
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
