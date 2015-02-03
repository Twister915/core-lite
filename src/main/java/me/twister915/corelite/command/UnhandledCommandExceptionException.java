package me.twister915.corelite.command;

import lombok.Getter;

public class UnhandledCommandExceptionException extends CommandException {
    @Getter
    private final Exception causingException;
    public UnhandledCommandExceptionException(Exception e) {
        super("Unhandled exception " + e.getMessage());
        this.causingException = e;
    }
}
