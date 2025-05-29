pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
//            setUrl("https://github.com/kshoji/javax.sound.midi-for-Android/raw/develop/javax.sound.midi/repository")
            setUrl("https://gitee.com/LIZENGQIANG/javax.sound.midi-for-Android/raw/develop/javax.sound.midi/repository")
        }
//        flatDir {
//            setDirs("libs")
////            dirs 'libs' //就是你放aar的目录地址
//        }
    }
}

rootProject.name = "QinPlus"
include(":app")
include(":android-pdfview")
include(":MIDIDriver")
include(":wheelview")
include(":circletextimageview")
include(":pickerview")
include(":pWheelview")
