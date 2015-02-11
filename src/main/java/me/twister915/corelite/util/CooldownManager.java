package me.twister915.corelite.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class CooldownManager {
    private final Map<String, Date> cooldownMilliseconds = new HashMap<String, Date>();

    public void testCooldown(String key, Long time, TimeUnit unit, Boolean reset) throws CooldownUnexpiredException {
        //Get the last time this cooldown was stored
        Date lastFiredDate = cooldownMilliseconds.get(key);
        //And get now
        Date currentDate = new Date();
        //If we don't have a previous countdown
        if (lastFiredDate == null) {
            this.cooldownMilliseconds.put(key, currentDate);
            return;
        }
        //See how long ago that was in milliseconds
        long millisecondsPassed = currentDate.getTime() - lastFiredDate.getTime();
        //And see how long we're supposed to wait
        long milliseconds = unit.toMillis(time);
        //If we're supposed to wait longer than we have
        if (milliseconds >= millisecondsPassed) {
            //The cooldown has yet to expire
            if (reset) this.cooldownMilliseconds.put(key, currentDate);
            throw new CooldownUnexpiredException(unit.toMillis(milliseconds-millisecondsPassed), unit);
        }
        this.cooldownMilliseconds.put(key, currentDate);
    }

    public void testCooldown(String key, Long time, TimeUnit unit) throws CooldownUnexpiredException {
        testCooldown(key, time, unit, false);
    }

    public void testCooldown(String key, Long seconds) throws CooldownUnexpiredException {
        testCooldown(key, seconds, TimeUnit.SECONDS);
    }
}
