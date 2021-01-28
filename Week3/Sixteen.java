import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.function.*;
import java.util.regex.*;

public class Sixteen {
    public static void main(String[] args) {
        EventManager em = new EventManager();
        new DataStorage(em);
        new StopWordFilter(em);
        new WordFrequencyController(em);
        new WordFrequencyApplication(em);
        new ZCounter(em);
        em.publish(new String[]{"run",args[0]});
    }

    private static class EventManager{
        static HashMap<String,ArrayList<Consumer<String[]>>> subscriptions = new HashMap<>();

        private static void subscribe(String event_type, Consumer<String[]> handler){
            if (subscriptions.containsKey(event_type)){
                subscriptions.get(event_type).add(handler);
            }
            else{
                subscriptions.put(event_type,new ArrayList<>(Arrays.asList(handler)));
            }
        }

        private static void publish(String[] event){
            String event_type = event[0];
            if (subscriptions.containsKey(event_type)){
                for (Consumer<String[]> h: subscriptions.get(event_type)){
                    h.accept(event);
                }
            }
        }
    }

    private static class DataStorage{
        static private EventManager event_manager;
        static private ArrayList<String> data = new ArrayList<>();

        DataStorage(EventManager em){
            event_manager = em;
            event_manager.subscribe("load", (String[] event)->load(event));
            event_manager.subscribe("start", (String[] event)->produce_words(event));
        }

        private static void load(String[] event){
            String path_to_file = event[1];
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
                        data.add(word);
                    }
                }
                myReader.close();
            }
            catch (FileNotFoundException e){
                System.out.println("An error occured.");
                e.printStackTrace();
            }
        }

        private static void produce_words(String[] event){
            for (String w : data){
                event_manager.publish(new String[]{"word",w});
            }
            event_manager.publish(new String[]{"eof"});
        }
    }

    private static class StopWordFilter{
        static private EventManager event_manager;
        static private ArrayList<String> stop_words = new ArrayList<>();

        StopWordFilter(EventManager em){
            event_manager = em;
            event_manager.subscribe("load", (String[] event)->load(event));
            event_manager.subscribe("word", (String[] event)->is_stop_word(event));
        }

        private static void load(String[] event){
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

        private static void is_stop_word(String[] event){
            String word = event[1];
            if (!stop_words.contains(word)){
                event_manager.publish(new String[]{"valid_word", word});
            }
        }
    }

    private static class WordFrequencyController{
        static private EventManager event_manager;
        static private HashMap<String,Integer> word_freqs = new HashMap<>();

        WordFrequencyController(EventManager em){
            event_manager = em;
            event_manager.subscribe("valid_word", (String[] event)->increment_count(event));
            event_manager.subscribe("print", (String[] event)->print_freqs(event));
        }

        private static void increment_count(String[] event){
            String word = event[1];
            if (word_freqs.containsKey(word)){
                word_freqs.put(word, word_freqs.get(word) + 1);
            }
            else{
                word_freqs.put(word,1);
            }
        }

        private static void print_freqs(String[] event){
            ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

            for(Map.Entry<String, Integer> e: word_freqs.entrySet())
            {
                freq_list.add(e);
            }

            // create comparator to use to sort list
            Comparator<Map.Entry<String, Integer>> myComparator = (e1, e2) -> {
                Integer int1 = e1.getValue();
                Integer int2 = e2.getValue();
                return int2.compareTo(int1);
            };

            // sort and return
            Collections.sort(freq_list,myComparator);
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

    private static class ZCounter{
      static private EventManager event_manager;
      static int zcount;

      ZCounter(EventManager em){
          event_manager = em;
          event_manager.subscribe("valid_word", (String[] event)->count(event));
          event_manager.subscribe("print", (String[] event)->printZCount(event));
      }

      private static void count(String[] event){
        String word = event[1];
        if (word.indexOf('z') >= 0){
          zcount++;
        }
      }

      private static void printZCount(String[] event){
        System.out.println("Words with z: " + zcount);
      }
    }

    private static class WordFrequencyApplication{
        static private EventManager event_manager;

        WordFrequencyApplication(EventManager em){
            event_manager = em;
            event_manager.subscribe("run", (String[] event)->run(event));
            event_manager.subscribe("eof", (String[] event)->stop(event));
        }

        private static void run(String[] event){
            String path_to_file = event[1];
            event_manager.publish(new String[]{"load", path_to_file});
            event_manager.publish(new String[]{"start"});
        }

        private static void stop(String[] event){
            event_manager.publish(new String[]{"print"});
        }
    }

}