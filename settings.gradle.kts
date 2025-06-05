import java.nio.file.Files
import java.nio.file.Path

rootProject.name = "rsmod"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.openrs2.org/repository/openrs2-snapshots")
    }
}

include(
    "api",
    "content",
    "engine",
    "server"
)

includeProjects(project(":api"))
includeProjects(project(":content"))
includeProjects(project(":engine"))
includeProjects(project(":server"))

fun includeProjects(pluginProject: ProjectDescriptor) {
    val projectPath = pluginProject.projectDir.toPath()
    Files.walk(projectPath).forEach {
        if (!Files.isDirectory(it)) {
            return@forEach
        }
        searchProject(pluginProject.name, projectPath, it)
    }
}

fun searchProject(parentName: String, root: Path, currentPath: Path) {
    val hasBuildFile = Files.exists(currentPath.resolve("build.gradle.kts"))
    if (!hasBuildFile) {
        return
    }
    val relativePath = root.relativize(currentPath)
    val projectName = relativePath.toString().replace(File.separator, ":")
    include("$parentName:$projectName")
}
//include("content:items")
//findProject(":content:items")?.name = "items"
//include("content:items:food")
//findProject(":content:items:food")?.name = "food"
//include("content:skills:smithing")
//findProject(":content:skills:smithing")?.name = "smithing"
//include("content:skills:mining")
//findProject(":content:skills:mining")?.name = "mining"
//include("content:travel:canoe:spirittree")
//findProject(":content:travel:canoe:spirittree")?.name = "spirittree"
//include("content:travel:spirittree")
//findProject(":content:travel:spirittree")?.name = "spirittree"
//include("content:items:food:jewellery")
//findProject(":content:items:food:jewellery")?.name = "jewellery"
//include("content:items:jewellery")
//findProject(":content:items:jewellery")?.name = "jewellery"

