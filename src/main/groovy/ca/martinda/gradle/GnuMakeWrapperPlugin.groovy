package ca.martinda.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.RuleSource

import org.ysb33r.gradle.gnumake.GnuMakeBuild
import org.ysb33r.gradle.gnumake.GnuMakePlugin

public class GnuMakeWrapperPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(GnuMakePlugin.class)
    }

    static class Rules extends RuleSource {
        @Mutate
        void createTask(ModelMap<Task> tasks) {
            tasks.withType(GnuMakeBuild) {
                includeDirs += [this.class.getClassLoader().getResource('makefiles').getFile()]
                println(includeDirs)
            }
        }
    }
}
