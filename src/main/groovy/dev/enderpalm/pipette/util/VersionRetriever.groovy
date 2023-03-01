package dev.enderpalm.pipette.util

class VersionRetriever {

    // Hostname for the Fabric's web service
    String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]

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
