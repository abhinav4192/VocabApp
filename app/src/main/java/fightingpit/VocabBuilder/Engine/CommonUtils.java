package fightingpit.VocabBuilder.Engine;

import java.util.ArrayList;
import java.util.Collections;

import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 07/08/16.
 */
public class CommonUtils {

    // Shuffling methods

    /**
     * Returns the shuffle sequence currently stored in shared preference
     * @return Shuffled ArrayList of integers
     */
    public static ArrayList<Integer> getShuffleSequence(){
        ArrayList<Integer> aReturnList = new ArrayList<>();
        String[] aShuffleArraySequence = (new SettingManager()).getValue(ContextManager
                .getCurrentActivityContext().getResources().getString(R.string.shuffle_sequence))
                .split(",");
        for(String s: aShuffleArraySequence){
            aReturnList.add(Integer.parseInt(s));
        }
        return aReturnList;
    }

    /**
     * Creates a new Shuffling sequence and stores it in shared preferences.
     * Also stores in shared preferences the number of words in the shuffling sequence. This will be
     * useful to know when to create a new Shuffling sequence. If the number of words in current
     * list does not match the number of words in shared preference, a new Shuffling sequence
     * should be requested.
     *
     * @param iNumberOfWords Number of words that the word list to be shuffled contains
     * @return Shuffled ArrayList of integers
     */

    public static ArrayList<Integer> updateShuffleSequence(Integer iNumberOfWords){
        ArrayList<Integer> aShuffleSequence = new ArrayList<>();
        for(int i=0;i<iNumberOfWords;i++){
            aShuffleSequence.add(i);
        }
        Collections.shuffle(aShuffleSequence);
        String aShuffleSequenceString = "";
        for(Integer i: aShuffleSequence){
            aShuffleSequenceString = aShuffleSequenceString +  i.toString() + ",";
        }
        SettingManager aSettingManager = new SettingManager();
        aSettingManager.updateValue(ContextManager.getCurrentActivityContext()
                .getResources().getString(R.string.shuffle_sequence), aShuffleSequenceString);
        aSettingManager.updateValue(ContextManager.getCurrentActivityContext()
                .getResources().getString(R.string.shuffle_sequence_number_of_words), iNumberOfWords);
        return aShuffleSequence;

    }
}
