package yes.mediumdifficulty.elytratime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeDetailsTest {
    @Test
    void zeroDurabilityClampsToZero() {
        assertEquals(0, TimeDetails.timeRemaining(0, 0));
    }

    @Test
    void negativeDurabilityClampsToZero() {
        assertEquals(0, TimeDetails.timeRemaining(0, -5));
    }

    @Test
    void lastDurabilityPointIsZeroSeconds() {
        // 1 action left without unbreaking: breaks this second -> 0m 0s, never negative
        assertEquals(0, TimeDetails.timeRemaining(0, 1));
    }

    @Test
    void vanillaElytraWithoutUnbreaking() {
        // vanilla elytra has 432 durability: 432 - 1 damage already taken by the model
        assertEquals(431, TimeDetails.timeRemaining(0, 432));
    }

    @Test
    void unbreakingLevelsExtendTime() {
        assertEquals(863, TimeDetails.timeRemaining(1, 432));
        assertEquals(1295, TimeDetails.timeRemaining(2, 432));
        assertEquals(1727, TimeDetails.timeRemaining(3, 432));
    }

    @Test
    void negativeUnbreakingIsSafe() {
        // defensive: even a bogus negative level must not produce negative time
        assertEquals(0, TimeDetails.timeRemaining(-2, 1));
    }
}
