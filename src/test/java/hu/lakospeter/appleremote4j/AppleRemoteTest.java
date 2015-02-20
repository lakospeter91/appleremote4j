package hu.lakospeter.appleremote4j;

/**
 * Simple tester for appleremote4j that prints remote events on the screen.
 *  Hold down the Play/Pause button to exit.
 *
 *  @author lakospeter
 */
public class AppleRemoteTest implements AppleRemoteListener  {

    /**
     * An {@link AppleRemote} whose events we are listening to.
     */
    private final AppleRemote appleRemote;

    /**
     * Creates an {@link AppleRemote} instance, stores it in {@link #appleRemote}
     *  and adds this AppleRemoteTest to it as a listener.
     */
    private AppleRemoteTest() {
        appleRemote = new AppleRemote();
        appleRemote.addAppleRemoteListener(this);
    }

    /**
     * Prints out the fact that the Volume Up button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeUpPressed(final AppleRemoteEvent e) {
        System.out.println("volumeUpPressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Volume Up button has been started to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeUpHoldStarted(final AppleRemoteEvent e) {
        System.out.println("volumeUpHoldStarted fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Volume Up button has been stopped to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeUpHoldStopped(final AppleRemoteEvent e) {
        System.out.println("volumeUpHoldStopped fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Volume Down button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeDownPressed(final AppleRemoteEvent e) {
        System.out.println("volumeDownPressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Volume Down button has been started to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeDownHoldStarted(final AppleRemoteEvent e) {
        System.out.println("volumeDownHoldStarted fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Volume Down button has been stopped to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void volumeDownHoldStopped(final AppleRemoteEvent e) {
        System.out.println("volumeDownHoldStopped fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Previous button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void previousPressed(final AppleRemoteEvent e) {
        System.out.println("previousPressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Previous button has been started to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void previousHoldStarted(final AppleRemoteEvent e) {
        System.out.println("previousHoldStarted fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Previous button has been stopped to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void previousHoldStopped(final AppleRemoteEvent e) {
        System.out.println("previousHoldStopped fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Next button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void nextPressed(final AppleRemoteEvent e) {
        System.out.println("nextPressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Next button has been started to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void nextHoldStarted(final AppleRemoteEvent e) {
        System.out.println("nextHoldStarted fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Next button has been stopped to be held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void nextHoldStopped(final AppleRemoteEvent e) {
        System.out.println("nextHoldStopped fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Play/Pause button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void playPausePressed(final AppleRemoteEvent e) {
        System.out.println("playPausePressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Play/Pause button has been held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void playPauseHeld(final AppleRemoteEvent e) {
        System.out.println("playPauseHeld fired. Event: " + e);

        // another way to stop the AppleRemote
        // appleRemote.stopRunning();
        appleRemote.removeAppleRemoteListener(this);
        System.out.println("Exiting test.");
    }

    /**
     * Prints out the fact that the Menu button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void menuPressed(final AppleRemoteEvent e) {
        System.out.println("menuPressed fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Menu button has been held down.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void menuHeld(AppleRemoteEvent e) {
        System.out.println("menuHeld fired. Event: " + e);
    }

    /**
     * Prints out the fact that the Select button has been pressed.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    @Override
    public void selectPressed(AppleRemoteEvent e) {
        System.out.println("selectPressed fired. Event: " + e);
    }

    /**
     * Creates a new AppleRemoteTest instance.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new AppleRemoteTest();
    }
}
