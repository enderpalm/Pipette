package dev.enderpalm.pipette

import spock.lang.Specification
import dev.enderpalm.pipette.util.VersionRetriever

class VersionRetrieverTest extends Specification {

    def "validate Minecraft version"() {
        expect:
        VersionRetriever.validateMinecraftVersion(minecraftVersion) == expected
        where:
        minecraftVersion               | expected
        "1.17.1"                       | true
        "1.17.2"                       | false
        "1.19.4-pre1"                  | true
        "22w45a"                       | true
        "1.18_experimental-snapshot-1" | true
    }

    //* -- Update this test when loader version changes --
    def "get latest loader version"() {
        given:
        def loader = VersionRetriever.getLatestLoaderVersion()

        expect:
        loader.matches("0.14.14")
    }
     //*/
}
