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

import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.GlobalApplication;
import fightingpit.VocabBuilder.Model.WordWithDetails;
import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 31/07/16.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {
    ArrayList<WordWithDetails> mWordList;
    static Context mContext;
    boolean mToShowMeanings;

    public WordListAdapter(){
        mContext = ContextManager.getCurrentActivityContext();
        mWordList = ((GlobalApplication) mContext.getApplicationContext()).getWordListHelper()
                .getWordList();
        mToShowMeanings = (new SettingManager()).showMeanings();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView word;
        public TextView meaning;
        public TextView sentence;
        public ImageView favourite;
        public ImageView moreOptions;
        public ExpandableLayout meaningAndSentence;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            word = (TextView) itemView.findViewById(R.id.tv_mswiwl_word);
            meaning = (TextView) itemView.findViewById(R.id.tv_mswiwl_meaning);
            sentence = (TextView) itemView.findViewById(R.id.tv_mswiwl_sentence);
            favourite = (ImageView) itemView.findViewById(R.id.iv_mswiwl_fav);
            moreOptions = (ImageView) itemView.findViewById(R.id.iv_mswiwl_dots);
            meaningAndSentence = (ExpandableLayout) itemView.findViewById(R.id.ll_mswiwl_mean_sen);

        }
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final WordWithDetails currentWord = mWordList.get(position);

        TextView word = viewHolder.word;
        TextView meaning = viewHolder.meaning;
        TextView sentence = viewHolder.sentence;
        ImageView favourite = viewHolder.favourite;
        ImageView moreOptions = viewHolder.moreOptions;
        final ExpandableLayout meaningAndSentence = viewHolder.meaningAndSentence;

        word.setText(currentWord.getWord());
        meaning.setText(currentWord.getMeaning());
        sentence.setText(currentWord.getSentence());
        favourite.setImageResource(R.drawable.star_filled);
        if(!mToShowMeanings)
        {
            meaningAndSentence.collapse(false);
        }
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaningAndSentence.toggle();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }
}
