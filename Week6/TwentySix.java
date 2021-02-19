import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwentySix {
    public static void main(String[] args)
    {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:tfc.db");
            createDbSchema(connection);
            loadFileIntoDatabase(args[0],connection);

            // query the database to find top words
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC");
            int i = 0;
            while (rs.next()) {
                if (i >= 25) {
                    break;
                }
                System.out.println(rs.getString("value") + " - " + rs.getInt("C"));
                i++;
            }

            // query number of unique words with z
             ResultSet rsZ = statement.executeQuery("SELECT COUNT(DISTINCT value) as C FROM words WHERE value LIKE '%z%'");
            while (rs.next()){
                System.out.println("Number of unique words with z: " + rsZ.getInt("C"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    private static void createDbSchema(Connection connection){
        try
        {
            // create a statement from connection
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            connection.setAutoCommit(false); // turn off autocommit

            statement.executeUpdate("drop table if exists words");
            statement.executeUpdate("CREATE TABLE words (id INTEGER PRIMARY KEY AUTOINCREMENT, value)");
            connection.commit();
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    private static void loadFileIntoDatabase(String filepath, Connection connection){
        ArrayList<String> words = extractWords(filepath);

        // add words to database
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM words");
            int row = 0;
            while(rs.next()){
                row = rs.getInt("MAX(id)");
            }
            String insertSql = "INSERT INTO words VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertSql);
            for (String w: words){
                pstmt.setInt(1,row);
                pstmt.setString(2,w);
                pstmt.executeUpdate();
                row++;
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static ArrayList<String> extractWords(String filepath){
        ArrayList<String> stop_words = new ArrayList<>();
        ArrayList<String> filtered_words = new ArrayList<>();

        try {
            // load stop words file
            File stop_words_file = new File("../stop_words.txt");
            Scanner myReader = new Scanner(stop_words_file);
            String list = "";
            // read the one line into a string and split
            while (myReader.hasNext()) {
                list = myReader.next();
            }
            String[] split_stop_words = list.split(",");
            // add each word to ArrayList
            for (int i = 0; i < split_stop_words.length; i++) {
                stop_words.add(split_stop_words[i]);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            // analyze text file line by line
            File text_file = new File(filepath);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                // search for words
                Pattern pattern = Pattern.compile("[a-z]{2,}");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String w = matcher.group();
                    if (!stop_words.contains(w)) {
                        filtered_words.add(w);
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return filtered_words;
    }
}
