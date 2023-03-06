package dev.enderpalm.pipette

import spock.lang.Specification
import dev.enderpalm.pipette.util.FabricVersionRetriever

class FabricVersionRetrieverTest extends Specification {

    def "Validate version and find stable"() {
        expect:
        FabricVersionRetriever.getInstance().validateVersionAndFindStable(minecraftVersion) == expected
        println("Raw version: ${minecraftVersion}, Stable: ${expected}")
        where:
        minecraftVersion               | expected
        "1.17.1"                       | "1.17.1"
        "1.17.2"                       | null
        "1.19.4-pre1"                  | "1.19.4"
        "22w45a"                       | "1.19.3"
        "1.18_experimental-snapshot-1" | "specialVersion"
        "18w43b"                       | "1.14"
        "20w14a"                       | "1.16"
    }

    def "Latest Fabric loader version"() {
        when:
        def loader = FabricVersionRetriever.getInstance().getLatestLoaderVersion()

        then:
        println(loader)
    }

    def "Yarn mapping version"() {
        expect:
        FabricVersionRetriever.getInstance().getYarnMappingVersion(minecraftVersion) == expected
        where:
        minecraftVersion                         | expected
        "1.19_deep_dark_experimental_snapshot-1" | "1.19_deep_dark_experimental_snapshot-1+build.4"
        "1.17.2"                                 | null
        "1.19.4-pre1"                            | "1.19.4-pre1+build.6"
        "22w45a"                                 | "22w45a+build.18"
        "1.14.3-pre2"                            | "1.14.3-pre2+build.18"
        "1.18_experimental-snapshot-7"           | "1.18_experimental-snapshot-7+build.4"
    }

    def "Fabric Api Version"() {
        given:
        def instance = FabricVersionRetriever.getInstance()
        def stable = instance.validateVersionAndFindStable(minecraftVersion as String)
        def computed = instance.getFabricApiVersion(minecraftVersion as String)
        println("Raw version: ${minecraftVersion}, Stable: ${stable}, Fabric API: ${computed}")

        expect:
        computed == expected

        where: // based on wiki's data, not Fabric website
        minecraftVersion                         | expected
        "1.18.1"                                 | "0.46.6+1.18"
        "1.18"                                   | "0.44.0+1.18"
        "1.19"                                   | "0.58.0+1.19"
        "1.18-rc4"                               | "0.43.1+1.18"
        "19w37a"                                 | "0.3.2+build.230-1.15"
        "1.16.2-rc1"                             | "0.17.1+build.394-1.16"
        "1.14.4"                                 | "0.28.5+1.14"
        "1.19_deep_dark_experimental_snapshot-1" | "0.58.0+1.19"
        "1.18_experimental-snapshot-6"           | "0.40.1+1.18_experimental"

    }

    def "Java Version"() {
        given:
        def instance = FabricVersionRetriever.getInstance()
        def available = instance.listGameVersions()
        def java = instance.getJavaVersion(minecraftVersion, available)
        println("Raw version: ${minecraftVersion}, Java: ${java}")

        expect:
        java == expected

        where:
        minecraftVersion                         | expected
        "1.19.3"                                 | 17
        "1.14.4"                                 | 8
        "1.16.3"                                 | 8
        "1.15"                                   | 8
        "1.16.3"                                 | 8
        "20w14a"                                 | 8
        "1.18_experimental-snapshot-4"           | 17
        "1.19_deep_dark_experimental_snapshot-1" | 17
        "1.18"                                   | 17
        "1.16"                                   | 8
        "21w19a"                                 | 16
        "1.17.1"                                 | 16
    }

    def "List game versions"(){
        when:
        def versions = FabricVersionRetriever.getInstance().listGameVersions()

        then:
        println("Versions: ${versions}")
    }
}
