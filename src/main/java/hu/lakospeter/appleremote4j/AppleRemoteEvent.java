package hu.lakospeter.appleremote4j;

import java.util.EventObject;

/**
 * Representation of an event fired by an {@link AppleRemote}.
 *
 * @author lakospeter
 */
public class AppleRemoteEvent extends EventObject {

    /**
     * Unique identifier for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The button that fired the event.
     */
    private final AppleRemote.Button button;

    /**
     * The raw output of iremotepipe for this event.
     */
    private final String message;


    /**
     * Simple constructor that gives values to fields.
     *
     * @param source The source of this event (usually an {@link AppleRemote}.
     * @param button The button that fired the event.
     * @param message The raw output of iremotepipe for this event.
     */
    public AppleRemoteEvent(final Object source, final AppleRemote.Button button, final String message) {
        super(source);
        this.button = button;
        this.message = message;
    }

    /**
     * Returns the button that fired the event.
     *
     * @return The button that fired the event.
     */
    public AppleRemote.Button getButton() {
        return button;
    }

    /**
     * Returns the raw output of iremotepipe for this event.
     *
     * @return The raw output of iremotepipe for this event.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of this AppleRemoteEvent, including its source, the button that fired the event
     *  and the raw output of iremotepipe for this event.
     *
     * @return A string representation of this AppleRemoteEvent.
     */
    @Override
    public String toString() {
        return "AppleRemoteEvent{" +
                "source=" + source +
                ", button=" + button +
                ", message='" + message + '\'' +
                '}';
    }
}
