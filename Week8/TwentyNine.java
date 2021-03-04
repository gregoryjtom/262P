import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.*;

public class TwentyNine {
    public static void main(String[] args) throws Exception{
        WordFrequencyManager wfManager = new WordFrequencyManager();

        StopWordManager stopWordManager = new StopWordManager();
        send(stopWordManager, new Object[]{"init",wfManager});

        DataStorageManager storageManager = new DataStorageManager();
        send(storageManager, new Object[]{"init", args[0], stopWordManager});

        WordFrequencyController wfController = new WordFrequencyController();
        send(wfController, new Object[]{"run", storageManager});

        wfManager.join();
        stopWordManager.join();
        storageManager.join();
        wfController.join();
    }

    public static void send(ActiveWFObject receiver, Object[] message){
        try {
            receiver.queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DataStorageManager extends ActiveWFObject{
        // models the contents of the file
        private ArrayList<String> data = new ArrayList<>();
        private ActiveWFObject stop_word_manager;

        @Override
        public void dispatch(Object[] message){
            if (message[0].equals("init")){
                init(Arrays.copyOfRange(message,1,message.length));
            }
            else if (message[0].equals("send_word_freqs")){
                process_words(Arrays.copyOfRange(message,1,message.length));
            }
            else{
                send(stop_word_manager, message);
            }
        }

        private void init(Object[] message){
            stop_word_manager = (ActiveWFObject) message[1];
            // read words in file
            try {
                // analyze text file line by line
                File text_file = new File((String) message[0]);
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

        private void process_words(Object[] message){
            Object recipient = message[0];
            for (String w: data){
                send(stop_word_manager, new Object[]{"filter",w});
            }
            send(stop_word_manager,new Object[]{"top25",recipient});
        }
    }

    private static class StopWordManager extends ActiveWFObject{
        // models the stop word filter
        private ArrayList<String> stop_words = new ArrayList<String>();
        private ActiveWFObject word_freqs_manager;

        @Override
        public void dispatch(Object[] message){
            if(message[0].equals("init")){
                init(Arrays.copyOfRange(message,1,message.length));
            }
            else if (message[0].equals("filter")){
                filter(Arrays.copyOfRange(message,1,message.length));
            }
            else{
                send(word_freqs_manager,message);
            }
        }

        private void init(Object[] message){
            word_freqs_manager = (ActiveWFObject) message[0];
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

        private void filter(Object[] message){
            String word = (String) message[0];
            if (!stop_words.contains(word)){
                send(word_freqs_manager,new Object[]{"word",word});
            }
        }
    }

    private static class WordFrequencyManager extends ActiveWFObject{
        // keeps the word frequency data
        private HashMap<String,Integer> word_freqs = new HashMap<String,Integer>();

        @Override
        public void dispatch(Object[] message){
            if(message[0].equals("word")){
                increment_count(Arrays.copyOfRange(message,1,message.length));
            }
            else if (message[0].equals("top25")){
                top25(Arrays.copyOfRange(message,1,message.length));
            }
        }

        private void increment_count(Object[] message){
            String word = (String) message[0];
            if (word_freqs.containsKey(word)){
                word_freqs.put(word, word_freqs.get(word) + 1);
            }
            else{
                word_freqs.put(word,1);
            }
        }

        private void top25(Object[] message){
            ActiveWFObject recipient = (ActiveWFObject) message[0];

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
            send(recipient,new Object[]{"top25",freq_list});
        }
    }

    private static class WordFrequencyController extends ActiveWFObject{
        // controls the messaging of different components
        private ActiveWFObject storage_manager;

        @Override
        public void dispatch(Object[] message) throws Exception{
            if (message[0].equals("run")){
                init(Arrays.copyOfRange(message,1,message.length));
            }
            else if (message[0].equals("top25")){
                display(Arrays.copyOfRange(message,1,message.length));
            }
            else{
                throw new Exception("Message not understood: " + message[0]);
            }
        }

        private void init(Object[] message){
            storage_manager = (ActiveWFObject) message[0];
            send(storage_manager, new Object[]{"send_word_freqs",this});
        }

        private void display(Object[] message) throws Exception{
            ArrayList<Map.Entry<String,Integer>> freq_list = (ArrayList<Map.Entry<String,Integer>>) message[0];
            System.out.println("----TOP 25 WORDS----");
            int i = 1;
            for (Map.Entry<String, Integer> entry : freq_list) {
                if (i++ > 25) {
                    break;
                }
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            send(storage_manager,new Object[]{"die"});
            stopMe = true;
        }
    }
}


abstract class ActiveWFObject extends Thread{
    public BlockingQueue<Object[]> queue = new ArrayBlockingQueue<>(40);
    public boolean stopMe = false;
    public ActiveWFObject(){
        start();
    }

    public void run(){
        while (!stopMe){
            try {
                Object[] message = queue.take();
                dispatch(message);
                if (message[0].equals("die")){
                    stopMe = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    abstract void dispatch(Object[] message) throws Exception;
}

