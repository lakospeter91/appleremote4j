package hu.lakospeter.appleremote4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  Representation of the Apple Remote.
 *  It starts iremotepipe, reads its output and fires events of the
 *  listeners that are subscribed for this AppleRemote.
 *
 *  @author lakospeter
 */
public class AppleRemote extends Thread {

    /*
        IRemotePipe possible outputs:

        {"type":"up","hold":false,"pressed":true} - Volume Up Press
        {"type":"up","hold":true,"pressed":true} - Volume Up Hold Start
        {"type":"up","hold":true,"pressed":false} - Volume Up Hold Stop

        {"type":"down","hold":false,"pressed":true} - Volume Down Press
        {"type":"down","hold":true,"pressed":true} - Volume Down Hold Start
        {"type":"down","hold":true,"pressed":false} - Volume Down Hold Stop

        {"type":"left","hold":false,"pressed":true} - Previous Press
        {"type":"left","hold":true,"pressed":true} - Previous Hold Start
        {"type":"left","hold":true,"pressed":false} - Previous Hold Stop

        {"type":"right","hold":false,"pressed":true} - Next Press
        {"type":"right","hold":true,"pressed":true} - Next Hold Start
        {"type":"right","hold":true,"pressed":false} - Next Hold Stop

        {"type":"play","hold":false,"pressed":true} - Play/Pause Press
        {"type":"sleep","hold":false,"pressed":true} - Play/Pause Hold

        {"type":"menu","hold":false,"pressed":true} - Menu Press
        {"type":"menu","hold":true,"pressed":true} - Menu Hold

        {"type":"ok","hold":false,"pressed":true} - Select Press (aluminum remote only)
     */

    /**
     * The file name of the iremotepipe program (only the file name, not the path).
     */
    private static final String IREMOTEPIPE_FILE_NAME = "iremotepipe";

    /**
     * The full path of the iremotepipe program (including the file name).
     */
    private static final String IREMOTEPIPE_PATH_AND_FILE_NAME = System.getProperty("user.home")
                                                    + "/Library/Application Support/hu.lakospeter.appleremote4j/"
                                                    + IREMOTEPIPE_FILE_NAME;

    /*
        CopyOnWriteArrayList is a List implementation backed up by a copy-on-write array.
        This implementation is similar in nature to CopyOnWriteArraySet. No synchronization is necessary,
        even during iteration, and iterators are guaranteed never to throw ConcurrentModificationException.
        This implementation is well suited to maintaining event-handler lists, in which change is infrequent,
        and traversal is frequent and potentially time-consuming.

        http://docs.oracle.com/javase/tutorial/collections/implementations/list.html
     */

    /**
     * List of all the listeners of this AppleRemote.
     */
    private List<AppleRemoteListener> appleRemoteListeners = new CopyOnWriteArrayList<>();

    /**
     * The iremotepipe process that was started by this AppleRemote and whose output this AppleRemote processes.
     */
    private Process iRemotePipeProcess;

    /**
     * Indicates whether this AppleRemote ({@link Thread}) should be running.
     */
    private boolean shouldBeRunning;

    /**
     * All of the button types on the white and the aluminum remotes.
     */
    public enum Button {
        VOLUME_UP,
        VOLUME_DOWN,
        PREVIOUS,
        NEXT,
        PLAY_PAUSE,
        MENU,
        SELECT
    }


    /**
     * The sole purpose of the constructor is to start this AppleRemote ({@link Thread}).
     */
    public AppleRemote() {
        start();
    }

    /**
     * Adds the specified new {@link AppleRemoteListener} to the list of listeners of this
     *  AppleRemote, if it doesn't already contain this particular listener.
     *
     * @param appleRemoteListener The new listener to be added to the list of listeners of this AppleRemote.
     */
    public void addAppleRemoteListener(final AppleRemoteListener appleRemoteListener) {
        if (!appleRemoteListeners.contains(appleRemoteListener)) {
            appleRemoteListeners.add(appleRemoteListener);
        }
    }

    /**
     * Removes the specified {@link AppleRemoteListener} from the list of listeners of this
     *  AppleRemote, if it is present.
     *  If after removing this listener, the list of listeners becomes empty, it stops this AppleRemote (stops the
     *  {@link Thread}.
     *
     * @param appleRemoteListener The listener to be removed from the list of listeners of this AppleRemote.
     */
    public void removeAppleRemoteListener(final AppleRemoteListener appleRemoteListener) {
        appleRemoteListeners.remove(appleRemoteListener);
        if (appleRemoteListeners.isEmpty()) {
            stopRunning();
        }
    }

    /**
     * Stops this AppleRemote (stops the {@link Thread}.
     * Also kills the iremotepipe process.
     */
    public void stopRunning() {
        shouldBeRunning = false;
        if (iRemotePipeProcess != null && iRemotePipeProcess.isAlive()) {
            iRemotePipeProcess.destroy();
        }
    }

    /**
     * Starts the iremotepipe process and continuously parses its output until {@link #stopRunning()} is called,
     *  or the last {@link AppleRemoteListener} is removed from the list of listeners.
     */
    @Override
    public void run() {
        shouldBeRunning = true;

        try {
            createIRemotePipeIfNotExist();
            iRemotePipeProcess = startIRemotePipe();
        } catch (Exception ex) {
            System.err.println("Error: Could not start iremotepipe.");
            ex.printStackTrace();
        }

        if (iRemotePipeProcess != null && iRemotePipeProcess.isAlive()) {
            try {
                processIRemotePipeOutput(iRemotePipeProcess);
            } catch (IOException ex) {
                System.err.println("Error when reading iremotepipe's output.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creates the iremotepipe executable in
     * {@link #IREMOTEPIPE_PATH_AND_FILE_NAME} if it does not exist yet.
     *
     * <br><br>
     *
     * If the file doesn't exist, it creates the parent directories that don't exist,
     * copies iremotepipe from the appleremote4j jar to
     * {@link #IREMOTEPIPE_PATH_AND_FILE_NAME}, and makes it executable.
     *
     * @throws Exception if the file or directory creation, or exporting from the jar, or changing the new file's
     *          permissions does not succeed.
     */
    public void createIRemotePipeIfNotExist() throws Exception {
        final File iRemotePipeFile = new File(IREMOTEPIPE_PATH_AND_FILE_NAME);
        if (!iRemotePipeFile.exists() || iRemotePipeFile.isDirectory()) {
            iRemotePipeFile.getParentFile().mkdirs();
            iRemotePipeFile.createNewFile();
            exportIRemotePipeFromJar();
            iRemotePipeFile.setExecutable(true);
        }
    }

    /**
     * Takes the iremotepipe file within the appleremote4j jar,
     * and copies it to {@link #IREMOTEPIPE_PATH_AND_FILE_NAME}.
     *
     * @throws Exception if the iremotepipe file cannot be found inside the jar, or an I/O error occurs.
     */
    private void exportIRemotePipeFromJar() throws Exception {
        final InputStream inputStream = AppleRemote.class.getResourceAsStream("/" + IREMOTEPIPE_FILE_NAME);
        if(inputStream == null) {
            throw new Exception("Could not find iremotepipe in the appleremote4j jar file.");
        }
        final OutputStream outputStream = new FileOutputStream(IREMOTEPIPE_PATH_AND_FILE_NAME);

        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readBytes);
        }

        inputStream.close();
        outputStream.close();
    }

    /**
     * Starts the iremotepipe process and returns its {@link Process}.
     *
     * @return The {@link Process} of iremotepipe.
     * @throws IOException If the iremotepipe process could not be started (e.g. the path is invalid).
     */
    private Process startIRemotePipe() throws IOException {
        final String[] command = {IREMOTEPIPE_PATH_AND_FILE_NAME};
        return Runtime.getRuntime().exec(command);
    }

    /**
     * Continuously parses the iremotepipe process' output until {@link #stopRunning()} is called,
     *  or the last {@link AppleRemoteListener} is removed from the list of listeners.
     *
     * @param iRemotePipeProcess The {@link Process} of iremotepipe, whose output is to be parsed.
     * @throws IOException If an I/O error occurs while reading the output.
     */
    private void processIRemotePipeOutput(final Process iRemotePipeProcess) throws IOException {
        String line;

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(iRemotePipeProcess.getInputStream()));

        while(shouldBeRunning && (line = bufferedReader.readLine()) != null) {
            processIRemotePipeOutputLine(line);
        }

        bufferedReader.close();
    }

    /**
     * Processes one line of iremotepipe's output (the output of one event of the remote).
     *
     * @param line One line of iremotepipe's output.
     */
    private void processIRemotePipeOutputLine(final String line) {
        String[] data = line.substring(1, line.length() - 1).replace("\"", "").split(",");
        final String type = data[0].split(":")[1];
        final boolean held = data[1].split(":")[1].equals("true");
        final boolean pressed = data[2].split(":")[1].equals("true");

        switch (type) {
            case "up":
                        if (held) {
                            if (pressed) {
                                fireVolumeUpHoldStarted(line);
                            } else {
                                fireVolumeUpHoldStopped(line);
                            }
                        } else {
                            fireVolumeUpPressed(line);
                        }
                        break;
            case "down":
                        if (held) {
                            if (pressed) {
                                fireVolumeDownHoldStarted(line);
                            } else {
                                fireVolumeDownHoldStopped(line);
                            }
                        } else {
                            fireVolumeDownPressed(line);
                        }
                        break;
            case "left":
                        if (held) {
                            if (pressed) {
                                firePreviousHoldStarted(line);
                            } else {
                                firePreviousHoldStopped(line);
                            }
                        } else {
                            firePreviousPressed(line);
                        }
                        break;
            case "right":
                        if (held) {
                            if (pressed) {
                                fireNextHoldStarted(line);
                            } else {
                                fireNextHoldStopped(line);
                            }
                        } else {
                            fireNextPressed(line);
                        }
                        break;
            case "play":
                        firePlayPausePressed(line);
                        break;
            case "sleep":
                        firePlayPauseHeld(line);
                        break;
            case "menu":
                        if (held) {
                            fireMenuHeld(line);
                        } else {
                            fireMenuPressed(line);
                        }
                        break;
            case "ok":
                        fireSelectPressed(line);
                        break;
        }
    }

    /**
     * Called when the user presses the Volume Up button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeUpPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeUpPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_UP, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeUpPressed(event);
        }
    }

    /**
     * Called when the user starts holding down the Volume Up button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeUpHoldStarted(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeUpHoldStarted(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_UP, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeUpHoldStarted(event);
        }
    }

    /**
     * Called when the user stops holding down the Volume Up button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeUpHoldStopped(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeUpHoldStopped(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_UP, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeUpHoldStopped(event);
        }
    }

    /**
     * Called when the user presses the Volume Down button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeDownPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeDownPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_DOWN, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeDownPressed(event);
        }
    }

    /**
     * Called when the user starts holding down the Volume Down button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeDownHoldStarted(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeDownHoldStarted(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_DOWN, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeDownHoldStarted(event);
        }
    }

    /**
     * Called when the user stops holding down the Volume Down button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#volumeDownHoldStopped(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireVolumeDownHoldStopped(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.VOLUME_DOWN, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.volumeDownHoldStopped(event);
        }
    }

    /**
     * Called when the user presses the Previous button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#previousPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void firePreviousPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.PREVIOUS, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.previousPressed(event);
        }
    }

    /**
     * Called when the user starts holding down the Previous button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#previousHoldStarted(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void firePreviousHoldStarted(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.PREVIOUS, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.previousHoldStarted(event);
        }
    }

    /**
     * Called when the user stops holding down the Previous button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#previousHoldStopped(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void firePreviousHoldStopped(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.PREVIOUS, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.previousHoldStopped(event);
        }
    }

    /**
     * Called when the user presses the Next button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#nextPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireNextPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.NEXT, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.nextPressed(event);
        }
    }

    /**
     * Called when the user starts holding down the Next button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#nextHoldStarted(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireNextHoldStarted(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.NEXT, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.nextHoldStarted(event);
        }
    }

    /**
     * Called when the user stops holding down the Next button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#nextHoldStopped(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireNextHoldStopped(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.NEXT, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.nextHoldStopped(event);
        }
    }

    /**
     * Called when the user presses the Play/Pause button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#playPausePressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void firePlayPausePressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.PLAY_PAUSE, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.playPausePressed(event);
        }
    }

    /**
     * Called when the user holds down the Play/Pause button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#playPauseHeld(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void firePlayPauseHeld(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.PLAY_PAUSE, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.playPauseHeld(event);
        }
    }

    /**
     * Called when the user presses the Menu button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#menuPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireMenuPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.MENU, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.menuPressed(event);
        }
    }

    /**
     * Called when the user holds down the Menu button on the remote.
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#menuHeld(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireMenuHeld(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.MENU, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.menuHeld(event);
        }
    }

    /**
     * Called when the user presses the Select button on the remote (aluminum remote only).
     *
     * <br><br>
     *
     * Fires the {@link AppleRemoteListener#selectPressed(AppleRemoteEvent)} method
     *  of every {@link AppleRemoteListener} that is subscribed to this AppleRemote.
     *
     * @param iRemotePipeOutput The raw output of iremotepipe for this event.
     */
    private void fireSelectPressed(final String iRemotePipeOutput) {
        AppleRemoteEvent event = new AppleRemoteEvent(this, Button.SELECT, iRemotePipeOutput);
        for (AppleRemoteListener appleRemoteListener : appleRemoteListeners) {
            appleRemoteListener.selectPressed(event);
        }
    }

    /**
     * Returns a string representation of this AppleRemote, including its hash code.
     *
     * @return A string representation of this AppleRemote.
     */
    @Override
    public String toString() {
        return "AppleRemote@" + hashCode();
    }

}