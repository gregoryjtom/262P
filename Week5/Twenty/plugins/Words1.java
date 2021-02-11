import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

public class Words1 implements Week5Words{
    public ArrayList<String> extract_words(String filepath){
        ArrayList<String> stop_words = loadStopWords();
        ArrayList<String> filtered_words = filterWordsInFile(filepath,stop_words);
        return filtered_words;
    }

    private ArrayList<String> loadStopWords() {
        ArrayList<String> stop_words = new ArrayList<>();
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
        return stop_words;
    }

    private ArrayList<String> filterWordsInFile(String filepath, ArrayList<String> stop_words) {
        ArrayList<String> filtered_words = new ArrayList<>();
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
