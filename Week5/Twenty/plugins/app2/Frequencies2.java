import java.util.*;

public class Frequencies2 implements Week5Frequencies{
    public Frequencies2(){}
    
    public ArrayList<Map.Entry<String,Integer>> top25(ArrayList<String> word_list){
        WordFrequencyController wfController = new WordFrequencyController();
        ArrayList<Map.Entry<String,Integer>> top25_list = null;
        try {
            top25_list = wfController.run(word_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top25_list;
    }

    private static class WordFrequencyController{
        // controls the messaging of different components
        private WordFrequencyManager word_freq_manager = new WordFrequencyManager();

        public ArrayList<Map.Entry<String,Integer>> run(ArrayList<String> word_list) throws Exception{
            for (String w: word_list){
                word_freq_manager.dispatch(new String[]{"increment_count",w});
            }

            ArrayList<Map.Entry<String,Integer>> top25_list = word_freq_manager.dispatch(new String[]{"sorted"});
            return top25_list;
        }
    }

    private static class WordFrequencyManager{
        // keeps the word frequency data
        private HashMap<String,Integer> word_freqs = new HashMap<String,Integer>();

        public ArrayList<Map.Entry<String,Integer>> dispatch(String[] message) throws Exception{
            if(message[0].equals("increment_count")){
                increment_count(message[1]);
                return null;
            }
            else if (message[0].equals("sorted")){
                return sorted();
            }
            else{
                throw new Exception("Message not understood: " + message[0]);
            }
        }

        private void increment_count(String word){
            if (word_freqs.containsKey(word)){
                word_freqs.put(word, word_freqs.get(word) + 1);
            }
            else{
                word_freqs.put(word,1);
            }
        }

        private ArrayList<Map.Entry<String,Integer>> sorted(){
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
            ArrayList<Map.Entry<String,Integer>> top25_list = new ArrayList<>(freq_list.subList(0,25));
            return top25_list;
        }
    }
}
