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

import fightingpit.VocabBuilder.Adapter.WordListAdapter;
import fightingpit.VocabBuilder.Engine.ContextManager;

/**
 * Created by abhinavgarg on 01/08/16.
 */
public class WordListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list_fragment, container, false);

        RecyclerView rvWordList = (RecyclerView) rootView
                .findViewById(R.id.rv_wlf_word_list);
        rvWordList.addItemDecoration(
                new DividerItemDecoration(ContextManager.getCurrentActivityContext(), R.drawable
                        .word_list_divider));

        // Initialize contacts
        // Create adapter passing in the sample user data
        WordListAdapter adapter = new WordListAdapter();
        // Attach the adapter to the recyclerview to populate items
        rvWordList.setAdapter(adapter);
        // Set layout manager to position the items
        rvWordList.setLayoutManager(new LinearLayoutManager(ContextManager.getCurrentActivityContext()));

        return rootView;
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
