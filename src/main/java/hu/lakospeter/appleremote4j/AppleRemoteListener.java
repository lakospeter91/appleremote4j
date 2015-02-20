package hu.lakospeter.appleremote4j;

import java.util.EventListener;

/**
 * Listener for {@link AppleRemote} events.
 *
 * @author lakospeter
 */
public interface AppleRemoteListener extends EventListener {

    /**
     * Called when the user presses the Volume Up button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeUpPressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user starts holding down the Volume Up button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeUpHoldStarted(final AppleRemoteEvent e) {}

    /**
     * Called when the user stops holding down the Volume Up button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeUpHoldStopped(AppleRemoteEvent e) {}



    /**
     * Called when the user presses the Volume Down button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeDownPressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user starts holding down the Volume Down button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeDownHoldStarted(final AppleRemoteEvent e) {}

    /**
     * Called when the user stops holding down the Volume Down button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void volumeDownHoldStopped(final AppleRemoteEvent e) {}



    /**
     * Called when the user presses the Previous button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void previousPressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user starts holding down the Previous button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void previousHoldStarted(final AppleRemoteEvent e) {}

    /**
     * Called when the user stops holding down the Previous button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void previousHoldStopped(final AppleRemoteEvent e) {}



    /**
     * Called when the user presses the Next button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void nextPressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user starts holding down the Next button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void nextHoldStarted(final AppleRemoteEvent e) {}

    /**
     * Called when the user stops holding down the Next button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void nextHoldStopped(final AppleRemoteEvent e) {}



    /**
     * Called when the user presses the Play/Pause button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void playPausePressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user holds down the Play/Pause button on the remote.
     *
     * <br><br>
     *
     * Note: holding the Play/Pause button only generates one event in iremotepipe:
     *  when the button is being held down for a particular amount of time.
     *  It doesn't fire separate events for when the hold is started and stopped.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void playPauseHeld(final AppleRemoteEvent e) {}



    /**
     * Called when the user presses the Menu button on the remote.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void menuPressed(final AppleRemoteEvent e) {}

    /**
     * Called when the user holds down the Menu button on the remote.
     *
     * <br><br>
     *
     * Note: holding the Menu button only generates one event in iremotepipe:
     *  when the button is being held down for a particular amount of time.
     *  It doesn't fire separate events for when the hold is started and stopped.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void menuHeld(final AppleRemoteEvent e) {}


    /**
     * Called when the user presses the Select button on the remote.
     * This button can only be found on the aluminum remote, not the white one.
     *
     * <br><br>
     *
     * Note: only <i>pressing</i> the Select button generates an event in iremotepipe.
     *  Holding down this button does not result in any output.
     *
     * @param e The {@link AppleRemoteEvent} that describes the event.
     */
    default void selectPressed(final AppleRemoteEvent e) {}

}
