package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WcWidthTest {

    @Test
    void shouldReturn1ForStandardCharacters() {
        assertEquals(1, WcWidth.calculateWidth('A'));
        assertEquals(1, WcWidth.calculateWidth('z'));
        assertEquals(1, WcWidth.calculateWidth('9'));
        assertEquals(1, WcWidth.calculateWidth(' '));
    }

    @Test
    void shouldReturn2ForWideCharacters() {
        assertEquals(2, WcWidth.calculateWidth(0x1F680)); // 🚀
        assertEquals(2, WcWidth.calculateWidth(0x4E2D));  // 中
        assertEquals(2, WcWidth.calculateWidth(0x231A));  // ⌚
    }

    @Test
    void shouldReturn0ForZeroWidthCharacters() {
        assertEquals(0, WcWidth.calculateWidth(0x0300)); // Combining Grave Accent
        assertEquals(0, WcWidth.calculateWidth(0x200B)); // Zero Width Space
    }
}
