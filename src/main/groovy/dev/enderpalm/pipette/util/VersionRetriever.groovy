package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper

class VersionRetriever {

    // Hostname for the Fabric's web service
    static String workingVersion
    static String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    static String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]

    static boolean validateMinecraftVersion(String version) {
        def validVersions = jsonSlurp("/v2/versions/game")
        Iterator it = validVersions.iterator()
        while (it.hasNext()) {
            def v = it.next()
            if (v.version == version) {
                workingVersion = version
                return true
            }
        }
        return false
    }

    static String getLatestLoaderVersion() {
        def loader = jsonSlurp("/v2/versions/loader")
        Iterator it = loader.iterator()
        while (it.hasNext()) {
            def v = it.next()
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
