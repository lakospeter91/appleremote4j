OPTS="-framework Foundation -framework IOKit -framework Cocoa -std=c99 -O2 -Wall -force_cpusubtype_ALL -mmacosx-version-min=10.5 -arch i386 -arch x86_64"
gcc $OPTS src/main/obj-c/unconed/AppleRemote.m src/main/obj-c/unconed/AppleRemoteDelegate.m src/main/obj-c/unconed/iremotepipe.m -o src/main/resources/iremotepipe
