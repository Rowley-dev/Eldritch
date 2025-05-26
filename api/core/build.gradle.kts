plugins {
    id("base-conventions")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.guice)
    implementation(libs.rsprot.api)
    implementation(projects.api.account)
    implementation(projects.api.areaChecker)
    implementation(projects.api.cache)
    implementation(projects.api.db)
    implementation(projects.api.gameProcess)
    implementation(projects.api.hunt)
    implementation(projects.api.market)
    implementation(projects.api.npc)
    implementation(projects.api.player)
    implementation(projects.api.pwHash)
    implementation(projects.api.random)
    implementation(projects.api.realm)
    implementation(projects.api.registry)
    implementation(projects.api.repo)
    implementation(projects.api.route)
    implementation(projects.api.serverConfig)
    implementation(projects.api.stats.levelmod)
    implementation(projects.api.stats.xpmod)
    implementation(projects.api.totp)
    implementation(projects.api.type.typeBuilders)
    implementation(projects.api.type.typeEditors)
    implementation(projects.api.type.typeReferences)
    implementation(projects.api.type.typeResolver)
    implementation(projects.api.type.typeUpdater)
    implementation(projects.api.type.typeVerifier)
    implementation(projects.api.utils.utilsLogging)
    implementation(projects.engine.game)
    implementation(projects.engine.module)
    implementation(projects.engine.routefinder)
}
