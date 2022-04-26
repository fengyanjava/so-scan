package com.github.fengyanjava.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScannerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        project.task("scanTheCode") {
//            doLast {
//                println("I am plugin ~ !")
//            }
//        }

        def android = project.extensions.findByType(AppExtension.class)
        android.registerTransform(new ScannerTransform())
    }
}