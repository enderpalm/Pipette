package dev.enderpalm.pipette.util

import groovy.json.JsonSlurper
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull
import org.gradle.internal.impldep.org.jetbrains.annotations.Nullable

class FabricVersionRetriever {

    // Hostname for the Fabric's web service
    final String[] meta = ["https://meta.fabricmc.net", "https://meta2.fabricmc.net"]
    final String[] modrinth = ["https://api.modrinth.com"]
    static final Map<String, List<String>> specialVersionsMap = new HashMap<>()
    static final String specialVersionKey = "specialVersion"
    static final String nextStable = "1.19.4"

    static FabricVersionRetriever getInstance() {
        return new FabricVersionRetriever()
    }

    @NotNull List<String> listGameVersions() {
        List<String> validVersions = new ArrayList<>()
        Iterator gameVersions = jsonSlurp(modrinth, "/v2/project/P7dR8mSH/version").iterator()
        while (gameVersions.hasNext()) {
            def version = gameVersions.next()
            String[] iteratedVersion = version.game_versions
            iteratedVersion.each { ver ->
                if (validVersions.empty || ver != validVersions.last()) validVersions.add(ver)
            }
        }
        return validVersions
    }

    int getJavaVersion(String target, String stable) {
        if (specialVersionsMap.containsKey(target)) {
            return specialVersionsMap.get(target)[1].toInteger()
        }

        @Nullable String minor = stable == specialVersionKey ? (target.find(~/\.\d+\d_/)  ?: target) : (stable.find(~/\.\d+\d\./) ?: stable.find(~/\.\d+\d/))
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
        Iterator validGameVersion = jsonSlurp(meta,"/v2/versions/game").iterator()
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

    @Nullable String getYarnMappingVersion(@NotNull String target) {
        Iterator yarn = jsonSlurp(meta,"/v2/versions/yarn").iterator()
        while (yarn.hasNext()) {
            def map = yarn.next()
            if (map.gameVersion == target) return map.version
        }
        return null
    }

    @Nullable String getLatestLoaderVersion() {
        Iterator loader = jsonSlurp(meta,"/v2/versions/loader").iterator()
        while (loader.hasNext()) {
            def load = loader.next()
            if (load.stable) return load.version
        }
        return null
    }

    @Nullable String getFabricApiVersion(@NotNull String target) {
        if (specialVersionsMap.containsKey(target)) return specialVersionsMap.get(target)[0]
        Iterator apiVersions = jsonSlurp(modrinth,"/v2/project/P7dR8mSH/version").iterator()
        while (apiVersions.hasNext()) {
            def api = apiVersions.next()
            if (api.game_versions.contains(target)) return api.version_number
        }
        return null
    }

    Object jsonSlurp(String[] hostname, String url) {
        return new JsonSlurper().parseText(getInputStream(hostname, url).getText())
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
