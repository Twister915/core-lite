package me.twister915.corelite.command;

public final class EmptyHandlerException extends CommandException implements FriendlyException {
    public EmptyHandlerException() {
        super("There was no handler found for this command!");
    }

    @Override
    public String getFriendlyMessage(CLCommand command) {
        return getMessage();
    }
}
