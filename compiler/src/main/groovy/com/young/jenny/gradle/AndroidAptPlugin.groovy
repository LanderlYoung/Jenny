package com.young.jenny.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class AndroidAptPlugin implements Plugin<Project> {
    void apply(Project project) {
        def variants = null;
        if (project.plugins.findPlugin("com.android.application") || project.plugins.findPlugin("android")) {
            variants = "applicationVariants";
        } else if (project.plugins.findPlugin("com.android.library") || project.plugins.findPlugin("android-library")) {
            variants = "libraryVariants";
        } else {
            throw new ProjectConfigurationException("The android or android-library plugin must be applied to the project", null)
        }
        def aptConfiguration = project.configurations.create('apt').extendsFrom(project.configurations.compile)
        def aptTestConfiguration = project.configurations.create('androidTestApt').extendsFrom(project.configurations.androidTestCompile)
        project.extensions.create("apt", AndroidAptExtension)
        project.afterEvaluate {
            project.android[variants].all { variant ->
                configureVariant(project, variant, aptConfiguration)
                if (variant.testVariant) {
                    configureVariant(project, variant.testVariant, aptTestConfiguration)
                }
            }
        }
    }

    static void configureVariant(def project, def variant, def aptConfiguration) {

        if (aptConfiguration.empty) {
            // no apt dependencies, nothing to see here
            return;
        }

        def aptOutputDir = project.file(new File(project.buildDir, "generated/source/apt"))
        def aptOutput = new File(aptOutputDir, variant.dirName)

        variant.addJavaSourceFoldersToModel(aptOutput);
        def processorPath = aptConfiguration.getAsPath();

        variant.javaCompile.options.compilerArgs += [
                '-processorpath', processorPath,
                '-s', aptOutput
        ]

        project.apt.aptArguments.variant = variant
        project.apt.aptArguments.project = project
        project.apt.aptArguments.android = project.android

        def projectDependencies = aptConfiguration.allDependencies.withType(ProjectDependency.class)
        // There must be a better way, but for now grab the tasks that produce some kind of archive and make sure those
        // run before this javaCompile. Packaging makes sure that processor meta data is on the classpath
        projectDependencies.each { p ->
            def archiveTasks = p.dependencyProject.tasks.withType(AbstractArchiveTask.class)
            archiveTasks.each { t -> variant.javaCompile.dependsOn t.path }
        }

        variant.javaCompile.options.compilerArgs += project.apt.arguments()

        variant.javaCompile.doFirst {
            aptOutput.mkdirs()
        }
    }
}
