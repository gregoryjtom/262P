import java.util.*;

public class Frequencies1 implements Week5Frequencies{
    public ArrayList<Map.Entry<String,Integer>> top25(ArrayList<String> word_list){
        HashMap<String,Integer> frequencies = new HashMap<>();
        ArrayList<Map.Entry<String,Integer>> freq_list = new ArrayList<>();

        // count frequencies
        for (String w: word_list){
            if (frequencies.containsKey(w)){
                frequencies.put(w, frequencies.get(w)+1);
            }
            else{
                frequencies.put(w,1);
            }
        }

        // add to ArrayList for sorting
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
        Collections.sort(freq_list,myComparator);
        ArrayList<Map.Entry<String,Integer>> top25_list = (ArrayList<Map.Entry<String, Integer>>) freq_list.subList(0,25);
        return top25_list;
    }
}
