import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.regex.*;

public class Nine {
    private interface NineFunction{
        void call(Object arg, NineFunction func);
    }

    public static void main(String[] args) {
       readFile(args[0],new filter_and_remove_stop_words());
    }

    private static void readFile(String file_path, NineFunction func){
        StringBuilder file_text = new StringBuilder();
        try {
            // analyze text file line by line
            File text_file = new File(file_path);
            Scanner myReader = new Scanner(text_file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine().toLowerCase();
                file_text.append(line);
                file_text.append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        func.call(file_text.toString(),new sort());
    }

    private static class filter_and_remove_stop_words implements NineFunction{
        @Override
        public void call(Object arg, NineFunction func) {
            String data = (String) arg;

            // load stop words
            ArrayList<String> stop_words = new ArrayList<String>();
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

            // make hashmap for frequencies
            HashMap<String, Integer> frequencies = new HashMap<>();
            // search for words and add to frequencies if not stop word
            Pattern pattern = Pattern.compile("[a-z]{2,}");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                String w = matcher.group();
                if (!stop_words.contains(w)) {
                    if (frequencies.containsKey(w)) {
                        frequencies.put(w, frequencies.get(w) + 1);
                    } else {
                        frequencies.put(w, 1);
                    }
                }
            }

            // call next function and pass in frequencies
            func.call(frequencies,new print_text());
        }
    }

    private static class sort implements NineFunction{

        @Override
        public void call(Object arg, NineFunction func) {
            HashMap<String,Integer> frequencies = (HashMap<String,Integer>) arg;

            // create ArrayList to store sorted list
            ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

            // put all HashMap entries into freq_list
            for(Map.Entry<String, Integer> e: frequencies.entrySet())
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
            func.call(freq_list,new no_op());
        }
    }

    private static class print_text implements NineFunction{
        @Override
        public void call(Object arg, NineFunction func) {
            ArrayList<Map.Entry<String,Integer>> freq_list = (ArrayList<Map.Entry<String,Integer>>) arg;
            // print 25 most frequent entries
            System.out.println("----TOP 25 WORDS----");
            int i = 1;
            for (Map.Entry<String, Integer> entry : freq_list) {
                if (i++ > 25) {
                    break;
                }
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            func.call(null,null);
        }
    }

    private static class no_op implements NineFunction{
        @Override
        public void call(Object arg, NineFunction func) {
            return;
        }
    }

}