package fightingpit.VocabBuilder.Model;

/**
 * Created by abhinavgarg on 04/08/16.
 */
public class SetDetails {
    String mNameOfSet;
    Integer mNumberOfSet;
    boolean mIsSelected;


    public SetDetails() {
    }

    public SetDetails(String nameOfSet, Integer numberOfSet, boolean isSelected) {
        mNameOfSet = nameOfSet;
        mNumberOfSet = numberOfSet;
        mIsSelected = isSelected;
    }

    public String getNameOfSet() {
        return mNameOfSet;
    }

    public void setNameOfSet(String nameOfSet) {
        mNameOfSet = nameOfSet;
    }

    public Integer getNumberOfSet() {
        return mNumberOfSet;
    }

    public void setNumberOfSet(Integer numberOfSet) {
        mNumberOfSet = numberOfSet;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}


