package dev.enderpalm.pipette

import spock.lang.Specification
import dev.enderpalm.pipette.util.VersionRetriever

class VersionRetrieverTest extends Specification {

    def "Validate version and find stable"() {
        expect:
        VersionRetriever.validateVersionAndFindStable(minecraftVersion) == expected
        println("Raw version: ${minecraftVersion}, Stable: ${expected}")
        where:
        minecraftVersion               | expected
        "1.17.1"                       | "1.17.1"
        "1.17.2"                       | null
        "1.19.4-pre1"                  | "1.19.4"
        "22w45a"                       | "1.19.3"
        "1.18_experimental-snapshot-1" | "1.18"
        "18w43b"                       | "1.14"
    }

    //* -- Update this test when loader version changes --
    def "Latest Fabric loader version"() {
        given:
        def loader = VersionRetriever.getLatestLoaderVersion()

        expect:
        loader.matches("0.14.14")
    }
    //*/

    def "Yarn mapping version"() {
        expect:
        VersionRetriever.getYarnMappingVersion(minecraftVersion) == expected
        where:
        minecraftVersion                         | expected
        "1.19_deep_dark_experimental_snapshot-1" | "1.19_deep_dark_experimental_snapshot-1+build.4"
        "1.17.2"                                 | "Error: No yarn mappings found for game version 1.17.2 :("
        "1.19.4-pre1"                            | "1.19.4-pre1+build.6"
        "22w45a"                                 | "22w45a+build.18"
        "1.14.3-pre2"                            | "1.14.3-pre2+build.18"
    }

    def "Fabric Api Version"() {
        given:
        def stable = VersionRetriever.validateVersionAndFindStable(minecraftVersion as String)
        def computed = VersionRetriever.getFabricApiVersion(minecraftVersion as String, stable)
        println("Raw version: ${minecraftVersion}, Stable: ${stable}, Fabric API: ${computed}")

        expect:
        computed == expected

        where: // based on wiki's data, not Fabric website
        minecraftVersion | expected
        "1.18.1"         | "0.46.6+1.18"
        "1.18"           | "0.46.6+1.18"
        "1.19"           | "0.58.0+1.19"
        "1.18-rc4"       | "0.46.6+1.18"
        "19w37a"         | "0.28.5+1.15"
        "1.16.2-rc2"     | "0.42.0+1.16"
        "1.19.3-rc2"     | "0.75.1+1.19.3"

    }
}
