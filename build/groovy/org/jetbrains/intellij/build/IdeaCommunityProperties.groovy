// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.intellij.build

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.jetbrains.intellij.build.python.PythonCommunityPluginModules

/**
 * @author nik
 */
@CompileStatic
class IdeaCommunityProperties extends BaseIdeaProperties {
  IdeaCommunityProperties(String home) {
    baseFileName = "idea"
    platformPrefix = "Idea"
    applicationInfoModule = "intellij.idea.community.resources"
    additionalIDEPropertiesFilePaths = ["$home/build/conf/ideaCE.properties".toString()]
    toolsJarRequired = true
    buildCrossPlatformDistribution = true

    productLayout.productApiModules = JAVA_API_MODULES
    productLayout.productImplementationModules =  JAVA_IMPLEMENTATION_MODULES + [
      "intellij.platform.duplicates.analysis",
      "intellij.platform.structuralSearch",
      "intellij.java.structuralSearch",
      "intellij.java.typeMigration",
      "intellij.platform.main"
    ]
    productLayout.additionalPlatformJars.put("resources.jar", "intellij.idea.community.resources")
    productLayout.bundledPluginModules = BUNDLED_PLUGIN_MODULES
    productLayout.mainModules = ["intellij.idea.community.main"]
    productLayout.compatiblePluginsToIgnore = PythonCommunityPluginModules.PYCHARM_ONLY_PLUGIN_MODULES
    productLayout.allNonTrivialPlugins = CommunityRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS + [
      CommunityRepositoryModules.androidPlugin([:]),
      CommunityRepositoryModules.groovyPlugin([])
    ]
    productLayout.classesLoadingOrderFilePath = "$home/build/order.txt"

    mavenArtifacts.forIdeModules = true

    versionCheckerConfig = [
      "": 52,
      "lib/idea_rt.jar": 47,
      "lib/forms_rt.jar": 48,
      "lib/annotations.jar": 49,
      "lib/util.jar": 50,
      "lib/rt/debugger-agent.jar": 50,
      "lib/rt/debugger-agent-storage.jar": 50,
      "lib/external-system-rt.jar": 50,
      "lib/jshell-frontend.jar": 53,
      "lib/sa-jdwp": -1,  // ignored
      "plugins/Groovy/lib/groovy_rt.jar": 49,
      "plugins/Groovy/lib/groovy-rt-constants.jar": 49,
      "plugins/coverage/lib/coverage_rt.jar": 49,
      "plugins/junit/lib/junit-rt.jar": 47,
      "plugins/gradle/lib/gradle-tooling-extension-api.jar": 50,
      "plugins/gradle/lib/gradle-tooling-extension-impl.jar": 50,
      "plugins/maven/lib/maven-server-api.jar": 50,
      "plugins/maven/lib/maven2-server-impl.jar": 50,
      "plugins/maven/lib/maven3-server-common.jar": 50,
      "plugins/maven/lib/maven30-server-impl.jar": 50,
      "plugins/maven/lib/maven3-server-impl.jar": 50,
      "plugins/maven/lib/artifact-resolver-m2.jar": 50,
      "plugins/maven/lib/artifact-resolver-m3.jar": 50,
      "plugins/maven/lib/artifact-resolver-m31.jar": 50,
      "plugins/xpath/lib/rt/xslt-rt.jar": 48,
      "plugins/xslt-debugger/lib/xslt-debugger-engine.jar": 49,
      "plugins/xslt-debugger/lib/rt/xslt-debugger-engine-impl.jar": 49,
      "plugins/cucumber-java/lib/cucumber-jvmFormatter.jar": 50
    ]
  }

  @Override
  @CompileDynamic
  void copyAdditionalFiles(BuildContext buildContext, String targetDirectory) {
    super.copyAdditionalFiles(buildContext, targetDirectory)
    buildContext.ant.copy(todir: targetDirectory) {
      fileset(file: "$buildContext.paths.communityHome/LICENSE.txt")
      fileset(file: "$buildContext.paths.communityHome/NOTICE.txt")
    }
    buildContext.ant.copy(todir: "$targetDirectory/bin") {
      fileset(dir: "$buildContext.paths.communityHome/build/conf/ideaCE/common/bin")
    }
  }

  @Override
  WindowsDistributionCustomizer createWindowsCustomizer(String projectHome) {
    return new WindowsDistributionCustomizer() {
      {
        icoPath = "$projectHome/platform/icons/src/idea_CE.ico"
        icoPathForEAP = "$projectHome/build/conf/ideaCE/win/images/idea_CE_EAP.ico"
        installerImagesPath = "$projectHome/build/conf/ideaCE/win/images"
        fileAssociations = ["java", "groovy", "kt"]
      }

      @Override
      String getFullNameIncludingEdition(ApplicationInfoProperties applicationInfo) { "IntelliJ IDEA Community Edition" }

      @Override
      String getFullNameIncludingEditionAndVendor(ApplicationInfoProperties applicationInfo) { "IntelliJ IDEA Community Edition" }

      @Override
      String getUninstallFeedbackPageUrl(ApplicationInfoProperties applicationInfo) {
        "https://www.jetbrains.com/idea/uninstall/?edition=IC-${applicationInfo.majorVersion}.${applicationInfo.minorVersion}"
      }

      @Override
      String getBaseDownloadUrlForJre() { "https://download.jetbrains.com/idea" }
    }
  }

  @Override
  LinuxDistributionCustomizer createLinuxCustomizer(String projectHome) {
    return new LinuxDistributionCustomizer() {
      {
        iconPngPath = "$projectHome/platform/icons/src/icon_CE_128.png"
        iconPngPathForEAP = "$projectHome/build/conf/ideaCE/linux/images/icon_CE_EAP_128.png"
        snapName = "intellij-idea-community"
        snapDescription =
          "The most intelligent Java IDE. Every aspect of IntelliJ IDEA is specifically designed to maximize developer productivity. " +
          "Together, powerful static code analysis and ergonomic design make development not only productive but also an enjoyable experience."
        extraExecutables = [
          "plugins/Kotlin/kotlinc/bin/kotlin",
          "plugins/Kotlin/kotlinc/bin/kotlinc",
          "plugins/Kotlin/kotlinc/bin/kotlinc-js",
          "plugins/Kotlin/kotlinc/bin/kotlinc-jvm",
          "plugins/Kotlin/kotlinc/bin/kotlin-dce-js"
        ]
      }

      @Override
      String getRootDirectoryName(ApplicationInfoProperties applicationInfo, String buildNumber) { "idea-IC-$buildNumber" }
    }
  }

  @Override
  MacDistributionCustomizer createMacCustomizer(String projectHome) {
    return new MacDistributionCustomizer() {
      {
        icnsPath = "$projectHome/build/conf/ideaCE/mac/images/idea.icns"
        urlSchemes = ["idea"]
        associateIpr = true
        fileAssociations = ["java", "groovy", "kt"]
        enableYourkitAgentInEAP = false
        bundleIdentifier = "com.jetbrains.intellij.ce"
        dmgImagePath = "$projectHome/build/conf/ideaCE/mac/images/dmg_background.tiff"
        icnsPathForEAP = "$projectHome/build/conf/ideaCE/mac/images/communityEAP.icns"
      }

      @Override
      String getRootDirectoryName(ApplicationInfoProperties applicationInfo, String buildNumber) {
        applicationInfo.isEAP ? "IntelliJ IDEA ${applicationInfo.majorVersion}.${applicationInfo.minorVersionMainPart} CE EAP.app"
                              : "IntelliJ IDEA CE.app"
      }
    }
  }

  @Override
  String getSystemSelector(ApplicationInfoProperties applicationInfo) { "IdeaIC${applicationInfo.majorVersion}.${applicationInfo.minorVersionMainPart}" }

  @Override
  String getBaseArtifactName(ApplicationInfoProperties applicationInfo, String buildNumber) { "ideaIC-$buildNumber" }

  @Override
  String getOutputDirectoryName(ApplicationInfoProperties applicationInfo) { "idea-ce" }
}