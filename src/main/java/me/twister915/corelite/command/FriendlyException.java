package me.twister915.corelite.command;

public interface FriendlyException {
    /**
     * Grabs a friendly version of the message to be displayed during an exception.
     * @param command The command that is attempting to get the friendly message.
     * @return A message to be displayed to the user during failure by default.
     */
    String getFriendlyMessage(CLCommand command);
}
