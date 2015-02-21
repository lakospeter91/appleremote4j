# appleremote4j

Java library for the Apple Remote (both the aluminum and white models). It is a wrapper for unconed's iremotepipe: [https://github.com/unconed/iremotepipe]


### System Requirements

* OS X 10.5 or newer
* JDK 1.8
* IR receiver


Tested on: OS X 10.10.2 on a mid-2007 iMac with both the aluminum and the white remotes.


###Supported Events

* Volume Up Pressed
* Volume Up Hold Started
* Volume Up Hold Stopped
* Volume Down Pressed
* Volume Down Hold Started
* Volume Down Hold Stopped
* Previous Pressed
* Previous Hold Started
* Previous Hold Stopped
* Next Pressed
* Next Hold Started
* Next Hold Stopped
* Play/Pause Pressed
* Play/Pause Held
* Menu Pressed
* Menu Held
* Select Pressed (aluminum model only)


### How To Use

##### As a library

If you just want to use appleremote4j in one of your projects.

1. Clone this repository to your local machine.
2. Copy the `appleremote4j-x.y.jar` file from the `lib` directory to somewhere in your project's folder.
3. Add this jar as a dependency.
4. Enjoy! There is a very simple example that explains how to use appleremote4j in `src/test/java/hu/lakospeter/appleremote4j/AppleRemoteTest.java`.

##### Development

If you want to modify appleremote4j before using it.

1. Clone this repository to your local machine.
2. Import as a gradle project in your favorite IDE.
3. Make your modifications.
4. Run the `jar` task in gradle.
5. Locate the jar file you just built in the `build/libs` folder.
6. Use this jar in your project as described above.
 
You can also modify unconed's iremotepipe if you want. Its source code is located under `src/main/obj-c/unconed`. When you are done, compile iremotepipe by running `./build-iremotepipe.sh` in the project's root. The resulting binary will be `src/main/resources/iremotepipe`.

##### A Basic Example

1. Create a class (e.g. `MyAppleRemoteListener`) that implements `AppleRemoteListener`, and instanciate it.
`MyAppleRemoteListener myAppleRemoteListener = new MyAppleRemoteListener();`
2. Create an `AppleRemote` object.
`AppleRemote appleRemote = new AppleRemote();`
3.  Add your `AppleRemoteListener` to this `AppleRemote`.
`appleRemote.addAppleRemoteListener(myAppleRemoteListener);`
4.  Write your code in the event listener methods in your `AppleRemoteListener` (e.g. `playPauseHeld()`). Use the `AppleRemoteEvent` object that you get as a parameter.
5. There are two ways to stop the `AppleRemote` from running. Either:
`appleRemote.removeAppleRemoteListener(myAppleRemoteListener);`
(if this was the last `AppleRemoteListener` that was listening to this `AppleRemote`) Or:
`appleRemote.stopRunning();`


### How It Works

When an `AppleRemote` object is created, it checks if `~/Library/Application Support/iremotepipe` exists. If not, it copies the iremotepipe binary from `appleremote4j-x.y.jar` to the path above.

It executes the iremotepipe binary, and continously parses its output in the background, firing the appropriate events on every `AppleRemoteListener` that listens to it.

The `AppleRemote` stops listening when its last `AppleRemoteListener` is removed, or when its `stopRunning()` method is invoked.


### Example

There is a simple example in `src/test/java/hu/lakospeter/appleremote4j/AppleRemoteTest.java`. Run it, and try pushing and holding buttons on your Apple Remote to try it out. Hold down the Play/Pause button to exit the test.





[https://github.com/unconed/iremotepipe]:https://github.com/unconed/iremotepipe
