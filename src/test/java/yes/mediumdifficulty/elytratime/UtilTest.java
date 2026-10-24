package yes.mediumdifficulty.elytratime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    private static final String DEFAULT_TIME_FORMAT = "[M]m [S]s";

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("14m 5s", Util.formatTime(14 * 60 + 5, DEFAULT_TIME_FORMAT));
    }

    @Test
    void exactlyOneMinute() {
        assertEquals("1m 0s", Util.formatTime(60, DEFAULT_TIME_FORMAT));
    }

    @Test
    void zeroTime() {
        assertEquals("0m 0s", Util.formatTime(0, DEFAULT_TIME_FORMAT));
    }

    @Test
    void secondsOnly() {
        assertEquals("0m 59s", Util.formatTime(59, DEFAULT_TIME_FORMAT));
    }

    @Test
    void placeholderOnlyReplacedOnce() {
        assertEquals("[10] [10]", Util.formatTime(600, "[[M]] [[M]]"));
    }
}
