# dexter

Dexter consists of two parts:
* libdexter - the core library providing all functionality
* android-gui - an Android front-end

## Downloading dependencies

First thing you need to do after cloning the repository is downloading libraries that Dexter is dependant on. Do this by executing:
```
./init.sh
```

## Building

The project can be built with:
```
./gradlew assemble              # build everything
./gradlew :libdexter:assemble   # build only libdexter
```

Note that building the Android project, you will need to have the Android SDK installed and might also need to specify its location using the local.properties file (see local.properties.example).

Those who have Gradle installed in their system can replace `./gradlew` with simple `gradle`.

### Speeding up the build

If you build the project frequently, you might want to consider running gradle as a daemon. 

```
./gradlew --daemon <args>
```

## Running

To execute Dexter as a command-line tool, build libdexter and then call:
```
./run_console.sh
```

The Android application can be installed using Gradle with:
```
./gradlew installDebug
```

## IDE project files

Preferred IDE for developing Dexter is the Android Studio built on top of IntelliJ IDEA, because it integrates Gradle nicely and more easily handles multi-project workspaces. Eclipse is also supported, but one must import all the dependant projects manually and has to run Gradle from the command-line. Project files can be generated with:
```
./gradlew idea     # Android Studio, IntelliJ IDEA
./gradlew eclise   # Eclipse
```

### Project Lombok

Libdexter is written in Java but with the extension provided by [Project Lombok](projectlombok.org). Their website provides information how to install plugins for the IDE of your choice.

## Credits

* app icon: [dAKirby309](http://dakirby309.deviantart.com/art/Metro-UI-Icon-Set-725-Icons-280724102)
