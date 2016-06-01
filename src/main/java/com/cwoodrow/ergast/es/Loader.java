package com.cwoodrow.ergast.es;

import com.cwoodrow.ergast.es.model.*;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Date;
import java.util.stream.Collectors;

public class Loader {

    public static final String F1_INDEX = "f1";
    public static final String F1_RESULT_TYPE = "result";

    public static void main(String[] args) throws IOException {
        String jdbcURL = args.length == 2 ? args[0] : "jdbc:mysql://localhost:3306/ergast?user=root";
        String esURL = args.length == 2 ? args[0] : "http://localhost:9200";

        System.out.println("Start loading data into ES :" + esURL + " from : " + jdbcURL);


        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(esURL)
                .multiThreaded(true)
                .readTimeout(60000)
                .build());
        JestClient client = factory.getObject();

        dropAndCreateIndex(client);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ergast?user=root")) {
            FluentJdbc fluentJdbc = new FluentJdbcBuilder()
                    .build();
            Bulk.Builder bulkBuilder = new Bulk.Builder().defaultIndex(F1_INDEX).defaultType(F1_RESULT_TYPE);

            Query query = fluentJdbc.queryOn(connection);
            String sql = "" +
                    "select *\n" +
                    "from results\n" +
                    "join status on results.statusid = status.statusid\n" +
                    "join drivers on results.driverid = drivers.driverid\n" +
                    "join races on results.raceid = races.raceid\n" +
                    "join circuits on races.circuitid = circuits.circuitid\n" +
                    "join constructors on results.constructorid = constructors.constructorid\n" +
                    "join seasons on races.year = seasons.year;";

            query.select(sql)
                    .iterateResult(
                            resultSet -> mapToF1Result(resultSet),
                            f1Result -> indexResult(f1Result, bulkBuilder));

            client.execute(bulkBuilder.build());
        } catch (SQLException | IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("Data successuly loaded");
    }

    private static void indexResult(F1Result f1Result, Bulk.Builder bulkBuilder) {
        Index index = new Index.Builder(f1Result).build();
        bulkBuilder.addAction(index);
    }

    private static F1Result mapToF1Result(ResultSet resultSet) {
        try {
            //long resultId = resultSet.getLong("resultId");
            //long raceId = resultSet.getLong("raceId");
            //long driverId = resultSet.getLong("driverId");
            long constructorId = resultSet.getLong("constructorId");
            long number = resultSet.getLong("number");
            long grid = resultSet.getLong("grid");
            long position = resultSet.getLong("position");
            String positionText = resultSet.getString("positionText");
            long positionOrder = resultSet.getLong("positionOrder");
            double points = resultSet.getDouble("points");
            long laps = resultSet.getLong("laps");
            String time = resultSet.getString("time");
            long milliseconds = resultSet.getLong("milliseconds");
            long fastestLap = resultSet.getLong("fastestLap");
            long rank = resultSet.getLong("rank");
            String fastestLapTime = resultSet.getString("fastestLapTime");
            String fastestLapSpeed = resultSet.getString("fastestLapSpeed");
            //long statusId = resultSet.getLong("statusId");
            String status = resultSet.getString("status");

            // driverID
            String driverRef = resultSet.getString("driverRef");
            long numberDriver = resultSet.getLong(21);
            String code = resultSet.getString("code");
            String forename = resultSet.getString("forename");
            String surname = resultSet.getString("surname");
            Date dob = resultSet.getDate("dob");
            String nationality = resultSet.getString("nationality");
            String url = resultSet.getString("url");
            com.cwoodrow.ergast.es.model.Driver driver = new com.cwoodrow.ergast.es.model.Driver(driverRef, numberDriver, code, forename, surname, dob, nationality, url);

            // reaceID
            long year = resultSet.getLong("year");
            long round = resultSet.getLong("round");
            long circuitId = resultSet.getLong("circuitId");
            String name = resultSet.getString("name");
            Date date = resultSet.getDate("date");
            Time timeRace = resultSet.getTime(36);
            String urlRace = resultSet.getString(37);
            Race race = new Race(year, round, circuitId, name, date, timeRace, urlRace);

//            long circuitId = resultSet.getLong("circuitId");
            String circuitRef = resultSet.getString("circuitRef");
            String nameCircuit = resultSet.getString(40);
            String location = resultSet.getString("location");
            String country = resultSet.getString("country");
            double lat = resultSet.getDouble("lat");
            double lng = resultSet.getDouble("lng");
            long alt = resultSet.getLong("alt");
            String urlCircuit = resultSet.getString(46);
            Circuit circuit = new Circuit(circuitRef, nameCircuit, location, country, lat, lng, alt, urlCircuit);

//            long constructorId=resultSet.getLong("constructorId");
            String constructorRef = resultSet.getString("constructorRef");
            String constructorName = resultSet.getString(49);
            String constructorNationality = resultSet.getString(50);
            String constructorUrl = resultSet.getString(51);
            Constructor constructor = new Constructor(constructorId, constructorRef, constructorName, constructorNationality, constructorUrl);

            long seasonYear = resultSet.getLong(52);
            String seasonUrl = resultSet.getString(53);
            Season season = new Season(seasonYear, seasonUrl);

            return new F1Result(number, grid, position, positionText, positionOrder,
                    points, laps, time, milliseconds, fastestLap, rank, fastestLapTime, fastestLapSpeed, status,
                    driver, race, circuit, constructor, season);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dropAndCreateIndex(JestClient client) throws IOException {
        IndicesExists footballIndexExists = new IndicesExists.Builder(F1_INDEX).build();
        JestResult indexExistsResult = client.execute(footballIndexExists);
        if (indexExistsResult.getResponseCode() == 200) {
            DeleteIndex deleteFootballIndex = new DeleteIndex.Builder(F1_INDEX).build();
            client.execute(deleteFootballIndex);
        }

        CreateIndex createIndex =
                new CreateIndex.Builder(F1_INDEX)
                        .build();
        client.execute(createIndex);


        putMappingFromFile(client, F1_RESULT_TYPE, "/mappings/f1-result.json");
    }

    private static void putMappingFromFile(JestClient client, String type, String mappingFilePath) throws IOException {
        String mappingFile;
        try (BufferedReader buffer = new BufferedReader( new InputStreamReader(Loader.class.getResourceAsStream(mappingFilePath)))) {
            mappingFile = buffer.lines()
                    .filter(line->!line.startsWith("//"))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PutMapping putMapping = new PutMapping.Builder(F1_INDEX, type, mappingFile).build();
        client.execute(putMapping);
    }
}
