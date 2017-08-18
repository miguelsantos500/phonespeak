package pt.ipleiria.estg.dei.phonespeak.exceptions;

/**
 * Created by Evilbrain on 20-11-2016.
 */

public class NoChannelsException extends Exception {

    public NoChannelsException() {
        super("There are no Channels to show!");
    }
}
