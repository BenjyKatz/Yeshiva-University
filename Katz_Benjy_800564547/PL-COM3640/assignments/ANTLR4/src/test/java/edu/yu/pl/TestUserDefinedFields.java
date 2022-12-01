package edu.yu.pl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestUserDefinedFields {

    private static Stream<Arguments> createNumExprTestParams() {
        return Stream.of(
                Arguments.of("3", 3),
                Arguments.of("3.0", 3.0),
                Arguments.of("-3", -3),
                Arguments.of("3 + 2 * 4", 11),
                Arguments.of("(3.0 + 2) * -4", -20.0),
                Arguments.of("(3.0 - 2) * ABS(-4)", 4.0),
                Arguments.of("(3.0 - 2) * ABS  (-4)", 4.0),
                Arguments.of("6 / 4", 1.5),
                Arguments.of("6 / 2", 3),
                Arguments.of("6 / 2.0", 3.0)
        );
    }
    @ParameterizedTest(name="Test numExpr {0}")
    @MethodSource("createNumExprTestParams")
    public void TestNumExpr(String udfText, Number expected) {
        var visitor = new UDFParseTreeVisitor();
        var udf = new UserDefinedField(udfText);
        assertEquals(expected, udf.evaluate(visitor));
    }

    private static Stream<Arguments> createStrExprTestParams() {
        return Stream.of(
                Arguments.of("'One'", "One"),
                Arguments.of("('One')", "One"),
                Arguments.of("'One' + 'Two'", "OneTwo")
        );
    }
    @ParameterizedTest(name="Test strExpr {0}")
    @MethodSource("createStrExprTestParams")
    public void TestNumExpr(String udfText, String expected) {
        var visitor = new UDFParseTreeVisitor();
        var udf = new UserDefinedField(udfText);
        assertEquals(expected, udf.evaluate(visitor));
    }


}
