package fightingpit.vocabbuilder.Model;

/**
 * Created by abhinavgarg on 30/06/16.
 */
public class WordWithDetails {
    String mWord;
    String mMeaning;
    String mSentence;
    Integer mProgress;
    Boolean mFavourite;
    Boolean mOriginal;

    public WordWithDetails(String mWord, String mMeaning, String mSentence, Integer mProgress, Boolean mFavourite, Boolean mOriginal) {
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

    public String getMeaning() {
        return mMeaning;
    }

    public String getSentence() {
        return mSentence;
    }

    public Integer getProgress() {
        return mProgress;
    }

    public Boolean isFavourite() {
        return mFavourite;
    }

    public Boolean isOriginal() {
        return mOriginal;
    }

    public void setProgress(Integer progress) {
        mProgress = progress;
    }

    public void setFavourite(Boolean favourite) {
        mFavourite = favourite;
    }

    public void setOriginal(Boolean original) {
        mOriginal = original;
    }
}
