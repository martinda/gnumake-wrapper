package ca.martinda.gradle

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class GnuMakeWrapperExampleTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    String makefile

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
        result.task(":tasks").outcome == SUCCESS
    }

    def "can call a user Makefile"() {
        given:
        makefile = this.getClass().getClassLoader().getResource('Makefile').getFile()
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
            import org.ysb33r.gradle.gnumake.GnuMakeBuild
            task runMakeAll(type: GnuMakeBuild) {
                targets = ['all','help']
                makefile = "${makefile}"
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['runMakeAll'])
            .withPluginClasspath()
            .build()

        then:
        result.output.contains('Hogwards')
        result.task(":runMakeAll").outcome == SUCCESS
    }

    def "can configure usign the gnumake project extension"() {
        given:
        makefile = this.getClass().getClassLoader().getResource('Makefile').getFile()
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
            gnumake {
                makefile = "${makefile}"
            }
            import org.ysb33r.gradle.gnumake.GnuMakeBuild
            task runMakeAll(type: GnuMakeBuild) {
                targets = ['all','help']
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['runMakeAll'])
            .withPluginClasspath()
            .build()

        then:
        result.output.contains('Hogwards')
        result.task(":runMakeAll").outcome == SUCCESS
    }

    def "user can include a provided makefile"() {
        given:
        makefile = this.getClass().getClassLoader().getResource('MakefileWithInclude').getFile()
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
            gnumake {
                makefile = "${makefile}"
            }
            import org.ysb33r.gradle.gnumake.GnuMakeBuild
            task runMakeAll(type: GnuMakeBuild) {
                targets = ['all','help']
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['runMakeAll'])
            .withPluginClasspath()
            .build()

        then:
        result.output.contains('A provided variable exists: value')
        result.task(":runMakeAll").outcome == SUCCESS
    }

    def "user can put target name on the gradle command line"() {
        given:
        makefile = this.getClass().getClassLoader().getResource('MakefileWithInclude').getFile()
        buildFile << """
            plugins {
                id 'ca.martinda.gnumake-wrapper'
            }
            gnumake {
                makefile = "${makefile}"
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(['tasks', '--all', 'makeAll', 'makeHelp'])
            .withPluginClasspath()
            .build()

        then:
        result.output.contains('Hello world')
        result.output.contains('Hogwards')
        result.task(":makeAll").outcome == SUCCESS
    }
}

