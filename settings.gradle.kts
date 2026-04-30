import java.net.URI

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://artifact.bytedance.com/repository/pangle/")
        maven("https://android-sdk.is.com/")
        maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven {
            url = URI.create("https://maven.3g.net.cn/repository/commerce/")
        }
        maven {
            url = URI.create("http://gomaven.3g.net.cn:8075/nexus/content/repositories/releases/")
            isAllowInsecureProtocol = true
        }
        maven {
            url = URI.create("http://gomaven.3g.net.cn:8075/nexus/content/repositories/commerce/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "Vitalo"
include(":app")
