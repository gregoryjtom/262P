import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Thirty {
    public static BlockingQueue<String> word_space = new ArrayBlockingQueue<>(200000);
    public static BlockingQueue<HashMap<String,Integer>> freq_space = new ArrayBlockingQueue<>(40);
    public static ArrayList<String> stop_words = new ArrayList<>();

    public static void main(String[] args) {
        loadStopWords();
        populateWordSpace(args[0]);

        // create workers
        ArrayList<ProcessWords> workers = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            workers.add(new ProcessWords());
        }
        // start workers
        for (ProcessWords worker: workers){
            worker.start();
        }
        // wait for workers to end
        for (ProcessWords worker: workers){
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // merge frequency results and print
        combineAndPrintSorted();
    }

    private static void loadStopWords() {
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
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void populateWordSpace(String filePath){
        try {
            // analyze text file line by line
            File text_file = new File(filePath);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                // search for words
                Pattern pattern = Pattern.compile("[a-z]{2,}");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String word = matcher.group();
                    word_space.put(word);
                }
            }
            myReader.close();
        }
        catch (Exception e){
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    private static void combineAndPrintSorted(){
        HashMap<String,Integer> all_freqs = new HashMap<>();
        while (!freq_space.isEmpty()){
            try {
                HashMap<String,Integer> freqs = freq_space.take();
                for (String key: freqs.keySet()){
                    int count;
                    if (all_freqs.containsKey(key)){
                        count = all_freqs.get(key) + freqs.get(key);
                    }
                    else{
                        count = freqs.get(key);
                    }
                    all_freqs.put(key,count);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // put all HashMap entries into freq_list
        ArrayList<Map.Entry<String, Integer>> freq_list = new ArrayList<>(all_freqs.entrySet());

        // create comparator to use to sort list
        Comparator<Map.Entry<String, Integer>> myComparator = (e1, e2) -> {
            Integer int1 = e1.getValue();
            Integer int2 = e2.getValue();
            return int2.compareTo(int1);
        };

        // sort
        freq_list.sort(myComparator);

        // print 25 most frequent entries
        System.out.println("----TOP 25 WORDS----");
        int i = 1;
        for (Map.Entry<String, Integer> entry : freq_list) {
            if (i++ > 25) {
                break;
            }
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    static class ProcessWords extends Thread{
        public void run(){
            HashMap<String,Integer> word_freqs = new HashMap<>();
            while (true){
                String word = null;
                try {
                    word = word_space.poll(1, TimeUnit.SECONDS);
                    if (word == null){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!stop_words.contains(word)){
                    if (word_freqs.containsKey(word)){
                        word_freqs.put(word, word_freqs.get(word) + 1);
                    }
                    else{
                        word_freqs.put(word,1);
                    }
                }
            }
            try {
                freq_space.put(word_freqs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
