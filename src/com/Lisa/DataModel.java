package com.Lisa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by lisa on 4/21/15.
 */

public class DataModel {

    private static final String PROTOCOL = "jdbc:derby:";
    private static final String DB_NAME = "recordStoreDB";
    private static final String USER = "user";
    private static final String PASS = "password";

    private static LinkedList<Statement> allStatements = new LinkedList<Statement>();
    private static Statement statement = null;
    private static Connection connection = null;
    private static ResultSet resultSet = null;

    PreparedStatement preparedStatement = null;

    // Constructor
    public DataModel() {

        openDatabaseConnections();
//        createTableSQL();
//        createTestConsignorDataSQL();
//        createTestAlbumDataSQL();
    }

    protected void createTableSQL() {

        String createAlbumsTableSQL = "CREATE TABLE albums (albumId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                "consignerId INT, artist VARCHAR(45), title VARCHAR(45), size INT, condition INT, price FLOAT, date_consigned DATE, status INT, date_sold DATE, date_notified DATE)";
        String createAlbumTableAction = "Create album table";
        executeSqlUpdate(createAlbumsTableSQL, createAlbumTableAction);

        String createConsignorsTableSQL = "CREATE TABLE consignors (consignorId int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, name varchar(30), phone varchar(30), amount_owed FLOAT)";
        String createConsignorsTableAction = "Create consignors table";
        executeSqlUpdate(createConsignorsTableSQL, createConsignorsTableAction);

        String createPaymentsTableSQL = "CREATE TABLE payments (paymentId int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, consignerId int, date_paid DATE, amount_paid FLOAT)";
        String createPaymentsTableAction = "Create payments table";
        executeSqlUpdate(createPaymentsTableSQL, createPaymentsTableAction);
    }

    protected static void openDatabaseConnections() {
        try {
            connection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true", USER, PASS);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            allStatements.add(statement);

        } catch (SQLException sqlException) {
            System.out.println("Could not establish connection.");
            System.out.println(sqlException);
        }
    }

    protected static void executeSqlUpdate(String sql, String sqlAction) {
        try {
            statement.executeUpdate(sql);
            System.out.println(sqlAction + " succeeded.");

        } catch (SQLException sqlException) {
            System.out.println(sqlAction + " failed. Could not execute SQL statement.");
            System.out.println(sqlException);
        }
    }

    protected static ResultSet executeSqlQuery(String sql, String sqlAction) {

        try {
            resultSet = statement.executeQuery(sql);
            System.out.println(sqlAction + " succeeded.");

        } catch (SQLException sqlException) {
            System.out.println(sqlAction + " failed. Could not execute SQL query.");
            System.out.println(sqlException);
        }

        return resultSet;
    }

    private static void createTestConsignorDataSQL() {

        String sqlAction = "Insert consigner";

        // Test data SQL
        String addConsignor1 = "INSERT INTO consignors (name, phone) VALUES ('Eric Makela', '612-518-2421')" ;
        executeSqlUpdate(addConsignor1, sqlAction);

        String addConsignor2 = "INSERT INTO consignors (name, phone) VALUES ('Anitra Budd', '612-618-3421')" ;
        executeSqlUpdate(addConsignor2, sqlAction);

        String addConsignor3 = "INSERT INTO consignors (name, phone) VALUES ('Neil Taylor', '612-718-4421')" ;
        executeSqlUpdate(addConsignor3, sqlAction);

        String addConsignor4 = "INSERT INTO consignors (name, phone) VALUES ('Anj Ronay', '612-818-5421')" ;
        executeSqlUpdate(addConsignor4, sqlAction);

        String addConsignor5 = "INSERT INTO consignors (name, phone) VALUES ('Shauna Jemai', '612-918-6421')" ;
        executeSqlUpdate(addConsignor5, sqlAction);

        String addConsignor6 = "INSERT INTO consignors (name, phone) VALUES ('Garret Ferderber', '612-018-7421')" ;
        executeSqlUpdate(addConsignor6, sqlAction);
    }

    private static void createTestAlbumDataSQL() {

        try {
            FileReader reader = new FileReader("Albums.txt");
            BufferedReader buffReader = new BufferedReader(reader);
            String line;
            String[] splitLine;
            String artist;
            String title;
            Random randomNumberGenerator = new Random();

            while (true) {
                // Iterate through lines until there are none left

                try {
                    line = buffReader.readLine();
                    splitLine = line.split("%");
                    artist = "'" + splitLine[0] + "'";
                    title = "'" + splitLine[1]+ "'";

                } catch (IOException ioe) {
                    System.out.println("Could not open or read Albums.txt");
                    System.out.println(ioe.toString());
                    buffReader.close();
                    break;

                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    System.out.println("Index out of bounds exception.");
                    System.out.println(aiobe.toString());
                    buffReader.close();
                    break;
                }

                int size = randomNumberGenerator.nextInt(6) + 1;
                int condition = randomNumberGenerator.nextInt(6) + 1;
                int status = randomNumberGenerator.nextInt(2) + 1;

                int month = randomNumberGenerator.nextInt(12) + 1;
                int day = randomNumberGenerator.nextInt(28) + 1;
                int year = randomNumberGenerator.nextInt(15) + 1;
                String stringMonth = "";
                String stringDay = "";
                String stringYear = "";

                if (month < 10) {
                    stringMonth = "0" + String.valueOf(month);
                } else {
                    stringMonth = String.valueOf(month);
                }

                if (day < 10) {
                    stringDay = "0" + String.valueOf(day);
                } else {
                    stringDay = String.valueOf(day);
                }

                if (year < 10) {
                    stringYear = "200" + String.valueOf(year);
                } else {
                    stringYear = "20" + String.valueOf(year);
                }

                String stringDate = stringYear + "-" + stringMonth + "-" + stringDay;
                java.sql.Date dateConsigned = java.sql.Date.valueOf(stringDate);

                // Add each line to database
                executeAddAlbumSql(artist, title, size, condition, dateConsigned, status);
            }
            buffReader.close();

        } catch (IOException ioe) {
            System.out.println("Could not open or read Albums.txt");
            System.out.println(ioe.toString());

        }
    }

    protected static void executeAddAlbumSql(String artist, String title, int size, int condition, java.sql.Date date_consigned, int status) {

        try {
            String psInsertSql = "INSERT INTO albums (artist, title, size, condition, date_consigned, status) " +
                    "VALUES ( ?, ?, ? , ?, ?, ? )";
            PreparedStatement psAlbum = connection.prepareStatement(psInsertSql);
            allStatements.add(psAlbum);
            psAlbum.setString(1, artist);
            psAlbum.setString(2, title);
            psAlbum.setInt(3, size);
            psAlbum.setInt(4, condition);
            psAlbum.setDate(5, date_consigned);
            psAlbum.setInt(6, status);
            psAlbum.executeUpdate();
            System.out.println("Added album " + title);

        } catch (SQLException sqlException) {
            System.out.println("Could not add album.");
            System.out.println(sqlException);
        }
    }

    public static ArrayList<Consignor> getConsignors() {
        ArrayList<Consignor> consignorList = new ArrayList<Consignor>();
        String getNamesAction = "Get consignor names";
        String getNamesSql = "SELECT * FROM consignors";

        try {
            ResultSet consignorRS = executeSqlQuery(getNamesSql, getNamesAction);
            while (consignorRS.next()) {
                String consignorName = consignorRS.getString("name");
                String consignorPhone = consignorRS.getString("phone");
                int id = consignorRS.getInt("consignorId");
                Consignor newConsignor = new Consignor(consignorName, consignorPhone, id);
                consignorList.add(newConsignor);
            }

        } catch (SQLException sqle) {
            System.out.println("Failed to read result set.");
            System.out.println(sqle);
        }

        return consignorList;
    }

    public static void addAlbum(Album album) {
        System.out.println("Ready to add " + album);
    }

    public static void closeDbConnections() {

        try {
            if (resultSet != null) {
                resultSet.close();
                System.out.println("Result set closed.");
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        // Close all of the statements. Stored a reference to each statement in
        // allStatements so we can loop over all of them and close them all.
        for (Statement statement : allStatements) {

            if (statement != null) {
                try {
                    statement.close();
                    System.out.println("Statement closed.");

                } catch (SQLException sqle) {
                    System.out.println("Error closing statement.");
                    sqle.printStackTrace();
                }
            }
        }

        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}