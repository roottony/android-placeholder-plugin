package com.roottony.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 *
 * @author Anton Rutkevich <roottony@gmail.com>
 */
public class PlaceholderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if(project.hasProperty("android")) {
            PlaceholderExtension noLogs = project.extensions.create(
                    "placeholder", PlaceholderExtension
            );

            def android = project.android

            project.afterEvaluate {
                if (android.hasProperty('applicationVariants')) {
                    android.applicationVariants.all { variant ->
                        addReplacementActions(project, variant, noLogs)
                    }
                } else if (android.hasProperty('libraryVariants')) {
                    android.libraryVariants.all { variant ->
                        addReplacementActions(project, variant, noLogs)
                    }
                }
            }
        }
    }

    def addReplacementActions(Project project, def variant, PlaceholderExtension extension) {

        // Using separate task

//        Task processPlaceholders = project.task("process${variant.name.capitalize()}Placeholders")
//
//        processPlaceholders << {
//            def buildConfigFolder = variant.generateBuildConfig.sourceOutputDir
//            def buildConfigFile = project.fileTree(buildConfigFolder).find {
//                it.name == "BuildConfig.java"
//            }
//
//            extension.replacements.each { replacement ->
//                project.ant.replace(
//                        file: buildConfigFile,
//                        token: "#${replacement.key}",
//                        value: replacement.value
//                )
//            }
//        }
//
//        variant.javaCompile.dependsOn processPlaceholders
//        processPlaceholders.dependsOn variant.generateBuildConfig


        // Using doLast

        Task task = variant.generateBuildConfig
        task.inputs.property("replacements", extension.replacements )

        task << {
            //println "Performing replacements on ${sourceOutputDir}"
            def buildConfigFile = project.fileTree(sourceOutputDir).find {
                it.name == "BuildConfig.java"
            }

            //println "File: ${buildConfigFile}"
            extension.replacements.each { replacement ->
                project.ant.replace(
                        file: buildConfigFile,
                        token: "#${replacement.key}",
                        value: replacement.value
                )
            }

            //println "Properties: \n${inputs.properties}"
        }

    }

}
