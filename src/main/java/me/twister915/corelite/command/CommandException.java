package me.twister915.corelite.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommandException extends Exception {
    private final String message;
}
