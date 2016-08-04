package fightingpit.VocabBuilder.Model;

/**
 * Created by abhinavgarg on 30/06/16.
 */
public class WordWithDetails implements Comparable {
    String mWord;
    String mMeaning;
    String mSentence;
    Integer mProgress;
    Boolean mFavourite;
    Boolean mOriginal;

    public WordWithDetails() {
    }

    public WordWithDetails(String mWord, String mMeaning, String mSentence, Integer mProgress,
                           Boolean mFavourite, Boolean mOriginal) {
        this.mWord = mWord;
        this.mMeaning = mMeaning;
        this.mSentence = mSentence;
        this.mProgress = mProgress;
        this.mFavourite = mFavourite;
        this.mOriginal = mOriginal;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    public String getMeaning() {
        return mMeaning;
    }

    public void setMeaning(String meaning) {
        mMeaning = meaning;
    }

    public String getSentence() {
        return mSentence;
    }

    public void setSentence(String sentence) {
        mSentence = sentence;
    }

    public Integer getProgress() {
        return mProgress;
    }

    public void setProgress(Integer progress) {
        mProgress = progress;
    }

    public Boolean isFavourite() {
        return mFavourite;
    }

    public Boolean isOriginal() {
        return mOriginal;
    }

    public void setFavourite(Boolean favourite) {
        mFavourite = favourite;
    }

    public void setOriginal(Boolean original) {
        mOriginal = original;
    }


    @Override
    public int hashCode() {
        String aTempString = this.getWord() + this.getMeaning();
        return aTempString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WordWithDetails))
            return false;
        if (obj == this)
            return true;

        WordWithDetails that = (WordWithDetails) obj;
        if (this.getWord().equalsIgnoreCase(that.getWord()) && this.getMeaning().equalsIgnoreCase
                (that
                .getMeaning())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Object obj) {
        if (obj instanceof WordWithDetails) {
            WordWithDetails that = (WordWithDetails) obj;
            Integer aResWord = this.getWord().compareTo(that.getWord());
            if (aResWord == 0) {
                return this.getMeaning().compareTo(that.getMeaning());
            } else {
                return aResWord;
            }
        } else {
            return -1;
        }
    }
}
