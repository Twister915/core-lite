package me.twister915.corelite.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.twister915.corelite.command.CommandException;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = false)
@Data
public final class CooldownUnexpiredException extends CommandException {
    private final Long timeRemaining;
    private final TimeUnit timeUnit;

    public CooldownUnexpiredException(Long timeRemaining, TimeUnit timeUnit) {
        super("Unexpired cooldown");
        this.timeRemaining = timeRemaining;
        this.timeUnit = timeUnit;
    }
}
