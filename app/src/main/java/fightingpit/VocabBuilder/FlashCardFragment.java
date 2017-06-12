package fightingpit.VocabBuilder;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fightingpit.VocabBuilder.Adapter.WordListAdapter;
import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.Database.DatabaseMethods;
import fightingpit.VocabBuilder.Engine.GlobalApplication;
import fightingpit.VocabBuilder.Engine.SettingManager;
import fightingpit.VocabBuilder.Model.WordWithDetails;

/**
 * Created by abhinavgarg on 12/06/17.
 */
public class FlashCardFragment extends Fragment {

    Context mContext;
    DatabaseMethods mDatabaseMethods;
    ArrayList<WordWithDetails> mWordList; // Reference to the word list
    TextView mWord;
    Integer mCurrentIndex = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.flash_card_fragment, container, false);

        mWord = (TextView) rootView.findViewById(R.id.tv_fcf_word);
        Button next = (Button) rootView.findViewById(R.id.bt_fcf_next);

        mContext = ContextManager.getCurrentActivityContext();
        mDatabaseMethods = ((GlobalApplication) mContext.getApplicationContext())
                .getDatabaseMethods();
        mWordList = mDatabaseMethods.getWordList();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: This will crash on no word case. Fix it.
                mDatabaseMethods.updateWordProgress(mDatabaseMethods.getRandomWordListIndex(),true);
                updateView();

            }
        });
        updateView();
        return rootView;
    }

    private void updateView(){
        mCurrentIndex = mDatabaseMethods.getRandomWordListIndex();
        if(mCurrentIndex < 0){
            mWord.setText("No word found");
        } else{
            mWord.setText(mWordList.get(mCurrentIndex).getWord());
        }
    }
}
