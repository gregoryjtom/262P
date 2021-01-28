import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.function.*;
import java.util.regex.*;

public class Thirteen {
    public static void main(String[] args) {
        // data_storage_obj
        HashMap<String,Object> data_storage_obj = new HashMap<>();
        data_storage_obj.put("data",new ArrayList<String>());
        data_storage_obj.put("init",(Consumer<String>) (String path_to_file)->extract_words(data_storage_obj, path_to_file) );
        data_storage_obj.put("words",(Supplier<ArrayList<String>>) ()-> (ArrayList<String>) data_storage_obj.get("data"));

        // stop_words_obj
        HashMap<String,Object> stop_words_obj = new HashMap<>();
        stop_words_obj.put("stop_words",new ArrayList<String>());
        stop_words_obj.put("init",(Runnable) ()->load_stop_words(stop_words_obj));
        stop_words_obj.put("is_stop_word",(Function<String,Boolean>) (String word)-> {
            ArrayList<String> stop_words_list = (ArrayList<String>) stop_words_obj.get("stop_words");
            return stop_words_list.contains(word);
        });

        // word_freqs_obj
        HashMap<String,Object> word_freqs_obj = new HashMap<>();
        word_freqs_obj.put("freqs",new HashMap<String,Integer>());
        word_freqs_obj.put("increment_count",(Consumer<String>) (String w)->increment_count(word_freqs_obj,w));
        word_freqs_obj.put("sorted",(Supplier<ArrayList<Map.Entry<String,Integer>>>) ()-> sorted((HashMap<String,Integer>) word_freqs_obj.get("freqs")));

        // get the methods to run
        Consumer<String> data_init = (Consumer<String>) data_storage_obj.get("init");
        Runnable stop_init = (Runnable) stop_words_obj.get("init");

        Supplier<ArrayList<String>> words = (Supplier<ArrayList<String>>) data_storage_obj.get("words");
        Function<String,Boolean> is_stop_word = (Function<String,Boolean>) stop_words_obj.get("is_stop_word");
        Consumer<String> inc_count = (Consumer<String>) word_freqs_obj.get("increment_count");

        // run the methods
        data_init.accept(args[0]);
        stop_init.run();
        ArrayList<String> word_list = words.get();
        for (String w: word_list){
            if (!is_stop_word.apply(w)){
                inc_count.accept(w);
            }
        }

        // start of 13.2
        word_freqs_obj.put("top25",(Runnable) ()->printTop25(word_freqs_obj));
        Runnable finalTop25 = (Runnable) word_freqs_obj.get("top25");
        finalTop25.run();
    }

    private static void extract_words(HashMap<String,Object> obj, String path_to_file){
        // extract ArrayList from obj
        ArrayList<String> words = (ArrayList) obj.get("data");
        // read words in file
        try {
            // analyze text file line by line
            File text_file = new File(path_to_file);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                // search for words
                Pattern pattern = Pattern.compile("[a-z]{2,}");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String word = matcher.group();
                    words.add(word);
                }
            }
            myReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    private static void load_stop_words(HashMap<String,Object> obj){
        // extract ArrayList from obj
        ArrayList<String> stop_words = (ArrayList) obj.get("stop_words");
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
    }

    private static void increment_count(HashMap<String,Object> obj, String w){
        // extract frequencies from obj
        HashMap<String,Integer> word_freqs = (HashMap<String,Integer>) obj.get("freqs");
        if (word_freqs.containsKey(w)){
            word_freqs.put(w, word_freqs.get(w) + 1);
        }
        else{
            word_freqs.put(w,1);
        }
    }

    private static ArrayList<Map.Entry<String,Integer>> sorted(HashMap<String, Integer> word_freqs){
        // create ArrayList to store sorted words
        ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

        for(Map.Entry<String, Integer> e: word_freqs.entrySet())
        {
            freq_list.add(e);
        }

        // create comparator to use to sort list
        Comparator<Map.Entry<String, Integer>> myComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(
                    Map.Entry<String, Integer> e1,
                    Map.Entry<String, Integer> e2) {

                Integer int1 = e1.getValue();
                Integer int2 = e2.getValue();
                return int2.compareTo(int1);
            }
        };

        // sort and return
        Collections.sort(freq_list,myComparator);
        return freq_list;
    }

    private static void printTop25(HashMap<String,Object> obj){
      Supplier<ArrayList<Map.Entry<String,Integer>>> word_freqs_sorted = (Supplier<ArrayList<Map.Entry<String,Integer>>>) obj.get("sorted");
      ArrayList<Map.Entry<String,Integer>> freq_list = word_freqs_sorted.get();

      System.out.println("----TOP 25 WORDS----");
      int i = 1;
      for (Map.Entry<String, Integer> entry : freq_list) {
          if (i++ > 25) {
              break;
          }
          System.out.println(entry.getKey() + ": " + entry.getValue());
      }
    }
}