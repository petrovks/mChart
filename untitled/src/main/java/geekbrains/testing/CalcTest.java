package geekbrains.testing;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CalcTest {

    @ParameterizedTest
    @MethodSource("dataTestForAfterFour")
    public void testSearchNumbers(int[] arr, int[] result) {
        Assertions.assertArrayEquals(result, MainApp.searchNumbers(arr));
    }

    public static Stream<Arguments> dataTestForAfterFour() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{0,1,2,3,4,5,6},new int[]{5,6}));
        out.add(Arguments.arguments(new int[]{3,4,5,6,0,1,2},new int[]{5,6,0,1,2}));
        out.add(Arguments.arguments(new int[]{0,1,2,4,4,5,6},new int[]{5,6}));
        out.add(Arguments.arguments(new int[]{3,4,5,6,0,1,4},new int[]{}));
        return out.stream();
    }

    @Test
    public void TestArrayForHaveFour() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            MainApp.searchNumbers(new int[]{10,0,5,8,6,7,9});
                });
    }

    @ParameterizedTest
    @MethodSource("dataTestOneAndFour")
    public void testOneAndFourChecking(int[] arr, boolean result) {
        Assertions.assertEquals(MainApp.checkingOneAndFour(arr), result);
    }

    public static Stream<Arguments> dataTestOneAndFour() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{0,1,2,3,4,5,6},true));
        out.add(Arguments.arguments(new int[]{3,7,5,6,0,5,2},false));
        out.add(Arguments.arguments(new int[]{0,1,2,4,4,5,6},true));
        out.add(Arguments.arguments(new int[]{3,4,5,6,0,5,4},true));
        return out.stream();
    }
}


