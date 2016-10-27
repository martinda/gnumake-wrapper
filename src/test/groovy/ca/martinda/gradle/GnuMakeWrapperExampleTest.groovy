package ca.martinda.gradle

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BuildLogicFunctionalTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "can load plugin"() {
        given:
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['tasks'])
            .withPluginClasspath()
            .build()

        then:
        result.output.contains('runMake')
        result.task(":tasks").outcome == SUCCESS
    }

    def "can runMake"() {
        given:
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['runMake'])
            .withPluginClasspath()
            .build()

        then:
        println(result.output)
        result.task(":runMake").outcome == SUCCESS
    }
}

