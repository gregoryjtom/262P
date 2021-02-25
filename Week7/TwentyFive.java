import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.function.Function;
import java.util.regex.*;

public class TwentyFive {
    private interface TwentyFiveFunction{
        Object call(Object arg);
    }

    public static void main(String[] args) {
        TheOne one = new TheOne(new getInput(),args);
        one.bind(new readFile()).bind(new filterAndRemoveStopWords()).bind(new frequencies()).bind(new sort()).bind(new top25()).execute();
    }

    private static class TheOne {
        private ArrayList<Object> funcs = new ArrayList<>();
        private String[] args;

        TheOne(Object func, String[] args) {
            funcs.add(func);
            this.args = args;
        }

        public TheOne bind(Object func) {
            funcs.add(func);
            return this;
        }

        public void execute(){
            Object value = args;
            for (Object func: funcs){
                // check if func is a TwentyFiveFunction (I/O function)
                if (func instanceof TwentyFiveFunction){
                    TwentyFiveFunction twentyFiveFunction = (TwentyFiveFunction) func;
                    value = twentyFiveFunction.call(value);
                }
                // else func is a Function (no I/O)
                else{
                    Function function = (Function) func;
                    value = function.apply(value);
                }
            }
            // print out value at the end:
            System.out.println(value);
        }
    }

    private static class getInput implements TwentyFiveFunction{
        @Override
        public Object call(Object arg) {
            String[] args = (String[]) arg;
            return args[0];
        }
    }

    private static class readFile implements TwentyFiveFunction {
        @Override
        public Object call(Object arg) {
            String file_path = (String) arg;
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
            return file_text.toString();
        }
    }

    private static class filterAndRemoveStopWords implements TwentyFiveFunction {
        @Override
        public Object call(Object arg) {
            String data = (String) arg;

            // load stop words
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
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            // make ArrayList for words
            ArrayList<String> filtered_words = new ArrayList<>();
            // search for words and add to filtered_words if not stop word
            Pattern pattern = Pattern.compile("[a-z]{2,}");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                String w = matcher.group();
                if (!stop_words.contains(w)) {
                    filtered_words.add(w);
                }
            }
            return filtered_words;
        }
    }

    private static class frequencies implements Function<Object,Object>{
        @Override
        public Object apply(Object arg) {
            ArrayList<String> filtered_words = (ArrayList<String>) arg;

            // make hashmap for frequencies
            HashMap<String, Integer> frequencies = new HashMap<>();

            for (String w: filtered_words) {
                if (frequencies.containsKey(w)) {
                    frequencies.put(w, frequencies.get(w) + 1);
                } else {
                    frequencies.put(w, 1);
                }
            }
            return frequencies;
        }
    }

    private static class sort implements Function<Object,Object> {

        @Override
        public Object apply(Object arg) {
            HashMap<String, Integer> frequencies = (HashMap<String, Integer>) arg;

            // create ArrayList to store sorted list

            // put all HashMap entries into freq_list
            ArrayList<Map.Entry<String, Integer>> freq_list = new ArrayList<>(frequencies.entrySet());

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
    }

    private static class top25 implements Function<Object,Object> {
        @Override
        public Object apply(Object arg) {
            ArrayList<Map.Entry<String, Integer>> freq_list = (ArrayList<Map.Entry<String, Integer>>) arg;
            StringBuilder top25_freqs = new StringBuilder("----TOP 25 WORDS----" + "\n");
            // adds 25 most frequent entries
            int i = 1;
            for (Map.Entry<String, Integer> entry : freq_list) {
                if (i++ > 25) {
                    break;
                }
                top25_freqs.append(entry.getKey() + ": " + entry.getValue());
                if (i <= 25){
                    top25_freqs.append("\n");
                }
            }
            return top25_freqs;
        }
    }
}