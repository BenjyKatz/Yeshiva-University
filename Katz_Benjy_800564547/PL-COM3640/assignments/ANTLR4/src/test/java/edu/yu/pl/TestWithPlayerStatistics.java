package edu.yu.pl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TestWithPlayerStatistics {
    private static Map<String, Player> playerMap;

    private static Map<String, UserDefinedField> userFields ;

    @BeforeAll
    static void beforeAll() throws IOException, CsvException {
        // Read in the player base statistics from the test file
        // and create a static map of Player objects to be used in all tests

        var csvReader = new CSVReader(new InputStreamReader(TestWithPlayerStatistics.class.getClassLoader()
                .getResourceAsStream("mlb_2021_batter_stats.csv")));

        String [] colNames = csvReader.readNext();
        String [] colTypes = csvReader.readNext();

        playerMap = csvReader.readAll().stream().collect(
                Collectors.toMap(
                        line -> line[0],
                        line -> {
                            var p = new Player(line[0]);
                            for (int i=0; i < line.length; i++) {
                                Object value;
                                if (colTypes[i].equals("int")) value = Integer.parseInt(line[i]);
                                else if (colTypes[i].equals("double")) value = Double.parseDouble(line[i]);
                                else value = line[i];
                                p.putProperty(colNames[i], value);
                            }
                            return p;
                        }));

        // Read in the additional user-defined statistical formulas
        // that will be evaluated and resolved for each player
        userFields = new CSVReader(new InputStreamReader(TestWithPlayerStatistics.class.getClassLoader()
                .getResourceAsStream("additional_stats_defined.csv")))
                .readAll()
                .stream()
                .collect(Collectors.toMap(line -> line[0], line -> new UserDefinedField(line[1])));
    }

    @ParameterizedTest(name="PA, AVG, OBP Stats for {0}")
    @CsvFileSource(resources = "/additional_stats_expected_set1.csv", numLinesToSkip = 1)
    void TestPlayerStats_Set1(String player, int PA, double AVG, double OBP) {
        // Retrieve player object from global map
        var pl = playerMap.get(player);

        // Create new visitor
        var visitor = new UDFParseTreeVisitor();

        // Set field values from the Player statistics
        pl.getAllProperties().forEach(visitor::setReferenceField);

        // Set user-defined field definitions to allow for user formulas that
        // reference other user formulas
        userFields.forEach(visitor::setReferenceField);

        // Evaluate each user-defined statistics and check against the expected.
        assertEquals(PA, userFields.get("PA").evaluate(visitor));
        assertEquals(AVG, (Double)userFields.get("AVG").evaluate(visitor), 0.0005);
        assertEquals(OBP, (Double)userFields.get("OBP").evaluate(visitor), 0.0005);
    }

    @ParameterizedTest(name="TB, SLG, OPS Stats for {0}")
    @CsvFileSource(resources = "/additional_stats_expected_set2.csv", numLinesToSkip = 1)
    void TestPlayerStats_Set2(String player, int TB, double SLG, double OPS) {
        // Retrieve player object from global map
        var pl = playerMap.get(player);
        assertNotNull(pl, "Player not found");

        // Create new visitor
        var visitor = new UDFParseTreeVisitor();

        // Set field values from the Player statistics
        pl.getAllProperties().forEach(visitor::setReferenceField);

        // Set user-defined field definitions to allow for user formulas that
        // reference other user formulas
        userFields.forEach(visitor::setReferenceField);

        // Evaluate each user-defined statistics and check against the expected.
        assertEquals(TB, userFields.get("TB").evaluate(visitor));
        assertEquals(SLG, (Double)userFields.get("SLG").evaluate(visitor), 0.0005);
        assertEquals(OPS, (Double)userFields.get("OPS").evaluate(visitor), 0.0005);
    }

    @ParameterizedTest(name="Additional Derived Stats for {0}")
    @CsvFileSource(resources = "/additional_stats_expected_set3.csv", numLinesToSkip = 1)
    void TestPlayerStats_Set3(String player, String playerInfo, boolean homeRunThreat, boolean infielder,
                              String ageBracket) {
        // Retrieve player object from global map
        var pl = playerMap.get(player);
        assertNotNull(pl, "Player not found");

        // Create new visitor
        var visitor = new UDFParseTreeVisitor();

        // Set field values from the Player statistics
        pl.getAllProperties().forEach(visitor::setReferenceField);

        // Set user-defined field definitions to allow for user formulas that
        // reference other user formulas
        userFields.forEach(visitor::setReferenceField);

        // Evaluate each user-defined statistics and check against the expected.

        assertEquals(playerInfo, userFields.get("playerInfo").evaluate(visitor));

        assertEquals(homeRunThreat, userFields.get("homeRunThreat").evaluate(visitor));
        assertEquals(infielder, userFields.get("infielder").evaluate(visitor));
        assertEquals(ageBracket, userFields.get("ageBracket").evaluate(visitor));
    }

}