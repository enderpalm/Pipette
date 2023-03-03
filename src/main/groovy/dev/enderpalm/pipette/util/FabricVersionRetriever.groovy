package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper
import groovy.xml.XmlSlurper
import org.gradle.internal.impldep.org.jetbrains.annotations.Nullable

class FabricVersionRetriever {

    // Hostname for the Fabric's web service
    final String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    final String[] maven = ["https://maven.fabricmc.net", "https://maven2.fabricmc.net"]
    static final Map<String, List<String>> specialVersionsMap = new HashMap<>()
    static final String specialVersionKey = "specialVersion"
    static final String nextStable = "1.19.4"

    static FabricVersionRetriever getInstance() {
        return new FabricVersionRetriever()
    }

    Collection<String> listGameVersions() {
        return jsonSlurp("/v2/versions/game").collect({ it.version })
    }

    int getJavaVersion(String target, String stable) {
        if (specialVersionsMap.containsKey(target)) {
            return specialVersionsMap.get(target)[1].toInteger()
        }
        @Nullable String minor = stable == specialVersionKey ? (target.find(~/\.[0-9][0-9]_/)  ?: target) : (stable.find(~/\.[0-9][0-9]\./) ?: stable.find(~/\.[0-9][0-9]/))
        if (minor == null) {
            throw new IllegalArgumentException("No Java version found for stable version: $stable :(")
        }
        // Modified from https://github.com/FabricMC/fabricmc.net/blob/main/scripts/src/lib/template/java.ts#L29
        int ver = minor.substring(1, minor.length() - 1).toInteger()
        return ver < 16 ? 8 : (ver == 16 ? 16 : 17)
    }

    @Nullable
    String validateVersionAndFindStable(String target) {
        if (specialVersionsMap.containsKey(target)) return specialVersionKey
        def isValid = false
        @Nullable String stable = null
        Iterator validGameVersion = jsonSlurp("/v2/versions/game").iterator()
        while (validGameVersion.hasNext()) {
            def ver = validGameVersion.next()
            stable = ver.stable ? ver.version : stable
            if (ver.version == target) {
                isValid = true
                break
            }
        }
        return isValid ? (stable ?: nextStable) : null
    }

    String getYarnMappingVersion(String target) {
        Iterator yarn = jsonSlurp("/v2/versions/yarn").iterator()
        while (yarn.hasNext()) {
            def map = yarn.next()
            if (map.gameVersion == target) return map.version
        }
        return "Error: No yarn mappings found for game version $target :("
    }

    String getLatestLoaderVersion() {
        Iterator loader = jsonSlurp("/v2/versions/loader").iterator()
        while (loader.hasNext()) {
            def load = loader.next()
            if (load.stable) return load.version
        }
        return "Error: No loader version found :("
    }

    String getFabricApiVersion(String target, String stable) {
        if (specialVersionsMap.containsKey(target)) return specialVersionsMap.get(target)[0]
        def xml = new XmlSlurper().parse(getInputStream(maven, "/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml"))
        String[] filter = [stable, stable.find(~/\b[0-9]\.[0-9][0-9]/), target.find(~/\b[0-9]\.[0-9][0-9]/)]
        for (String suffix : filter) {
            Iterator fabric = xml.versioning.versions.version.iterator().reverse()
            while (fabric.hasNext()) {
                String api = fabric.next()
                if (api.endsWith(suffix)) return api
            }
        }
        return "Error: No fabric api version found for game version $target :("
    }

    Object jsonSlurp(String url) {
        return new JsonSlurper().parseText(getInputStream(meta, url).getText())
    }

    InputStream getInputStream(String[] hostname, String path) {
        for (String host : hostname) {
            try {
                return new URL(host + path).openConnection().getInputStream()
            } catch (Exception e) {
                e.printf("Pipette: Failed to connect to %s :(", host + path)
            }
        }
        throw new Exception("Pipette: Failed to connect to any host :(")
    }

    static {
        for (def i = 1; i <= 7; i++) specialVersionsMap.put("1.18_experimental-snapshot-" + i, ["0.40.1+1.18_experimental", "17"].toList())
        specialVersionsMap.put("1.19_deep_dark_experimental_snapshot-1", ["0.58.0+1.19", "17"].toList())
        specialVersionsMap.put("20w14infinite", ["0.46.1+1.17", "8"].toList())
        specialVersionsMap.put("22w13oneblockatatime", ["0.48.1+22w13oneblockatatime", "17"].toList())
    }

}
