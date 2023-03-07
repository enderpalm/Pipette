package dev.enderpalm.pipette

import spock.lang.Specification
import spock.lang.TempDir
import org.gradle.testkit.runner.GradleRunner

class MigrateMinecraftTest extends Specification{
    @TempDir File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'pipette'
            }
        """
    }

    def "List Minecraft version"(){
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("migrateMinecraft","--ver","list")
                .withPluginClasspath()
                .build()

        then:
        println(result.output)

    }
}
