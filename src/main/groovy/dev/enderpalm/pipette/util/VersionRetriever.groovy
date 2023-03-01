package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper

class VersionRetriever {

    // Hostname for the Fabric's web service
    static String workingVersion
    static String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    static String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]

    static boolean validateMinecraftVersion(String version) {
        def versions = new JsonSlurper().parseText(getInputStream(meta, "/v2/versions/game").getText())
        Iterator it = versions.iterator()
        while (it.hasNext()) {
            def v = it.next()
            if (v.version == version) {
                workingVersion = version
                return true
            }
        }
        return false
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
