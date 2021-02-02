import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.*;
import java.util.regex.*;

public class Ten {
    private interface TenFunction {
        Object call(Object arg);
    }

    public static void main(String[] args) {
        TheOne one = new TheOne(args[0]);
        one.bind(new readFile()).bind(new filter_and_remove_stop_words()).bind(new sort()).bind(new top25()).printMe();
    }

    private static class TheOne {
        private Object value;

        TheOne(Object v) {
            value = v;
        }

        public TheOne bind(TenFunction func) {
            value = func.call(value);
            return this;
        }

        public void printMe() {
            System.out.println(value);
        }
    }

    private static class readFile implements TenFunction {
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

    private static class filter_and_remove_stop_words implements TenFunction {
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
            return frequencies;
        }
    }

    private static class sort implements TenFunction {

        @Override
        public Object call(Object arg) {
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

    private static class top25 implements TenFunction {
        @Override
        public Object call(Object arg) {
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