import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.regex.*;

public class TwentyTwo{
    public static void main(String[] args) {
        TwentyTwo s = new TwentyTwo();
        String filepath = null;
        // check if argument was given
        if (args.length > 0){
            filepath = args[0];
        }
        else{
            System.out.println("An input file is required!");
            System.exit(0);
        }
        s.printTop25(s.sortFrequencies(s.countWordsInFile(filepath,s.loadStopWords())));
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
            Collections.addAll(stop_words, split_stop_words);
            myReader.close();

        } catch (FileNotFoundException e) {
            // if file does not exist, return empty
            System.out.println("An error occurred while opening ../stop_words.txt.");
            e.printStackTrace();
            System.exit(0);
        }
        return stop_words;
    }

    private HashMap<String, Integer> countWordsInFile(Object filepath, Object stop_words) {
        // check if filepath is not a string or stop_words is not a list
        if (!(filepath instanceof String)){
            System.out.println("Filepath is not a string!");
            System.exit(0);
        }
        String filepath_string = (String) filepath;
        if (filepath_string.length() == 0){
            System.out.println("Filepath cannot be empty!");
            System.exit(0);
        }
        if (!(stop_words instanceof List)){
            System.out.println("Stop words must be a list!");
            System.exit(0);
        }
        ArrayList<String> stop_words_list = (ArrayList<String>) stop_words;
        // check if stop words is empty
        if (stop_words_list.size() == 0){
            System.out.println("Stop words cannot be empty!");
            System.exit(0);
        }
        // make HashMap for frequencies
        HashMap<String, Integer> frequencies = new HashMap<>();
        try {
            // analyze text file line by line
            File text_file = new File(filepath_string);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                // search for words
                Pattern pattern = Pattern.compile("[a-z]{2,}");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String w = matcher.group();
                    if (!stop_words_list.contains(w)) {
                        if (frequencies.containsKey(w)) {
                            frequencies.put(w, frequencies.get(w) + 1);
                        } else {
                            frequencies.put(w, 1);
                        }
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
            e.printStackTrace();
            System.exit(0);
        }
        return frequencies;
    }

    private ArrayList<Map.Entry<String,Integer>> sortFrequencies(Object frequencies){
        // check if frequencies is not a map
        if (!(frequencies instanceof Map)){
            System.out.println("Frequencies must be a Map object!");
            System.exit(0);
        }

        HashMap<String,Integer> frequencies_map = (HashMap<String,Integer>) frequencies;
        // check if frequencies is empty
        if (frequencies_map.isEmpty()){
            System.out.println("Frequencies map must not be empty!");
            System.exit(0);
        }

        // create ArrayList to store sorted list
        ArrayList<Map.Entry<String, Integer>> freq_list = new ArrayList<>(frequencies_map.entrySet());

        // create comparator to use to sort list
        Comparator<Map.Entry<String, Integer>> myComparator = (e1, e2) -> {

            Integer int1 = e1.getValue();
            Integer int2 = e2.getValue();
            return int2.compareTo(int1);
        };

        // sort and return
        freq_list.sort(myComparator);
        return freq_list;
    }

    private void printTop25(Object freq_list){
        // check if freq_list is not a list
        if (!(freq_list instanceof List)){
            System.out.println("Sorted frequencies is not a list!");
            System.exit(0);
        }

        ArrayList<Map.Entry<String,Integer>> sorted_freqs = (ArrayList<Map.Entry<String, Integer>>) freq_list;
        // check if freq_list is less than 25 entries long
        if (sorted_freqs.size() < 25){
            System.out.println("There is not 25 words!");
        }

        // print 25 most frequent entries
        System.out.println("----TOP 25 WORDS----");
        int i = 1;
        for (Map.Entry<String, Integer> entry : sorted_freqs) {
            if (i++ > 25) {
                break;
            }
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

