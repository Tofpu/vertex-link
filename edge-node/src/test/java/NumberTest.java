import org.h2.util.Bits;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class NumberTest {
    @Test
    void name() {
        System.out.println(64078867874189312L & 4095);
        System.out.println(64078867886772224L & 4095);
        System.out.println(64078867895160832L & 4095);
        System.out.println(64078867903549440L & 4095);

        System.out.println(64078867874189312L << 42);
        System.out.println(64078867903549440L << 42);

        System.out.println((5 << 2 | 2) + " " + Integer.toBinaryString(5 << 2 | 2));
        System.out.println((22 >> 2) + " " + Integer.toBinaryString(22 >> 2));
        // 10110
        // 1011000

        // 101
        // 10110 + 2
        // 1011001 + 1

        // 001011001
        // 101100100
        // wrong (100111101)

        // +1011001
        // -0100110 (~)
        // ...
        //  001011001
        //  010011000 (<< 2)
        //  011000001

        // 001011001
        // 101100100
        // 001000000

        // 1011001
        // 1011001
        // 1111111 (&)
        // 1011001 (|)
        // 0000000 (^)

        // 1011001
        // 0000000 <- 1011001 (^)
        // 1011001 (|)
        // 0100110 (^)

        // 1011001
        // 0100110 (^ + ^)
        //

        System.out.println(((5 << 2 | 2) << 2 | 1) + " " + Integer.toBinaryString((5 << 2 | 2) << 2 | 1));
        System.out.println((89 ^ (89 << 2)) + " " + Integer.toBinaryString(89 ^ (89 << 2)));
    }

    public String toPrettyBinaryRepresentation(ByteBuffer byteBuffer) {
//        System.out.println("test: " + byteBuffer.getLong());
        return Arrays.toString(byteBuffer.array()).replace("[", "").replace(", ", "").replace("]", "");
    }
}
