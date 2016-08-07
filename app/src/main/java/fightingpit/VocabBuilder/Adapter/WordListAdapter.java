package fightingpit.VocabBuilder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import fightingpit.VocabBuilder.Engine.CommonUtils;
import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.Database.DatabaseMethods;
import fightingpit.VocabBuilder.Engine.GlobalApplication;
import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.Engine.TextToSpeechManager;
import fightingpit.VocabBuilder.Model.WordWithDetails;
import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 31/07/16.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    static Context mContext;
    ArrayList<WordWithDetails> mWordList; // Reference to the word list
    boolean[] mShowMeaning; // To hold if the word meanings are shown or hidden.
    DatabaseMethods mDatabaseMethods;
    SettingManager mSettingManager;
    TextToSpeechManager mTextToSpeechManager;

    public WordListAdapter() {
        mContext = ContextManager.getCurrentActivityContext();
        mSettingManager = new SettingManager();
        mDatabaseMethods = ((GlobalApplication) mContext.getApplicationContext())
                .getDatabaseMethods();
        mTextToSpeechManager = ((GlobalApplication) mContext.getApplicationContext())
                .getTextToSpeechManager();

        mDatabaseMethods.updateWordList();
        mWordList = mDatabaseMethods.getWordList();
        handleShuffle();
        mShowMeaning = new boolean[mWordList.size()];
        Arrays.fill(mShowMeaning, mSettingManager.showMeanings());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.model_single_word_in_word_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final WordWithDetails currentWord = mWordList.get(position);

        TextView word = viewHolder.word;
        TextView meaning = viewHolder.meaning;
        TextView sentence = viewHolder.sentence;
        final ImageView favourite = viewHolder.favourite;
        ImageView moreOptions = viewHolder.moreOptions;
        final ExpandableLayout meaningAndSentence = viewHolder.meaningAndSentence;
        final ImageView pronounce = viewHolder.pronounce;
        pronounce.setVisibility(View.GONE);

        // build display
        word.setText(currentWord.getWord());
        meaning.setText(currentWord.getMeaning());
        sentence.setText(currentWord.getSentence());

        if (currentWord.isFavourite()) {
            favourite.setImageResource(R.drawable.star_filled);
        } else {
            favourite.setImageResource(R.drawable.star_outline);
        }

        if (mShowMeaning[position]) {
            meaningAndSentence.expand(false);
        } else {
            meaningAndSentence.collapse(false);
        }

        final int aFinalPosition = position;
        // implement click listeners
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowMeaning[aFinalPosition] = !mShowMeaning[aFinalPosition];
                meaningAndSentence.toggle();
            }
        });
        meaningAndSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaningAndSentence.collapse();
                mShowMeaning[aFinalPosition] = false;
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentWord.isFavourite()) {
                    currentWord.setFavourite(false);
                    favourite.setImageResource(R.drawable.star_outline);
                    mDatabaseMethods.updateFavourite(currentWord.getWord(), currentWord.getMeaning
                            (), false);
                } else {
                    currentWord.setFavourite(true);
                    favourite.setImageResource(R.drawable.star_filled);
                    mDatabaseMethods.updateFavourite(currentWord.getWord(), currentWord.getMeaning
                            (), true);
                }
            }
        });

        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pronounce.getVisibility() == View.VISIBLE) {
                    pronounce.setVisibility(View.GONE);
                } else {
                    pronounce.setVisibility(View.VISIBLE);
                }
            }
        });

        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextToSpeechManager.speak(currentWord.getWord());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    private void handleShuffle() {
        if (mSettingManager.toShuffle()) {
            ArrayList<Integer> aShuffleSequence;
            Integer aWordListSize = mWordList.size();
            if (mSettingManager.getIntegerValue(mContext.getResources().getString(R.string
                    .shuffle_sequence_number_of_words)) == aWordListSize) {
                aShuffleSequence = CommonUtils.getShuffleSequence();
            } else {
                aShuffleSequence = CommonUtils.updateShuffleSequence(aWordListSize);
            }

            ArrayList<WordWithDetails> aTempList = new ArrayList<>();
            for (Integer i : aShuffleSequence) {
                aTempList.add(mWordList.get(i));
            }
            mWordList = aTempList;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.tv_mswiwl_word) TextView word;
        @BindView(R.id.tv_mswiwl_meaning) TextView meaning;
        @BindView(R.id.tv_mswiwl_sentence) TextView sentence;
        @BindView(R.id.iv_mswiwl_fav) ImageView favourite;
        @BindView(R.id.iv_mswiwl_dots) ImageView moreOptions;
        @BindView(R.id.ll_mswiwl_mean_sen) ExpandableLayout meaningAndSentence;
        @BindView(R.id.iv_mswiwl_pronounce) ImageView pronounce;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
