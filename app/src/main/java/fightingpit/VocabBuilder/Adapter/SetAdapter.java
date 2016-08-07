package fightingpit.VocabBuilder.Adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.Database.DatabaseMethods;
import fightingpit.VocabBuilder.Model.SetDetails;
import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 06/08/16.
 */
public class SetAdapter extends BaseAdapter {

    ArrayList<SetDetails> mSetDetailsArrayList;
    boolean[] mOriginalSelectedValue;


    public SetAdapter(ArrayList<SetDetails> setDetailsArrayList) {
        mSetDetailsArrayList = setDetailsArrayList;
        mOriginalSelectedValue = new boolean[getCount()];
        for (int i = 0; i < getCount(); i++) {
            mOriginalSelectedValue[i] = getItem(i).isSelected();
        }
    }

    @Override
    public int getCount() {
        return mSetDetailsArrayList.size();
    }

    @Override
    public SetDetails getItem(int position) {
        return mSetDetailsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(ContextManager.getCurrentActivityContext()).inflate
                    (R.layout.model_set_selector,
                            parent, false);
        }

        TextView aSetName = (TextView) convertView.findViewById(R.id.tv_mss_SetName);
        final CheckBox aCheckBox = (CheckBox) convertView.findViewById(R.id.cb_mss_CheckBox);
        RelativeLayout aRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_mss);

        SetDetails aItem = getItem(position);
        aSetName.setText(aItem.getNameOfSet());
        aCheckBox.setChecked(aItem.isSelected());

        aCheckBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        aRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aCheckBox.isChecked()) {
                    getItem(position).setSelected(false);
                    aCheckBox.setChecked(false);
                } else {
                    getItem(position).setSelected(true);
                    aCheckBox.setChecked(true);
                }
            }
        });
        return convertView;
    }

    public boolean updateSelectedSetInDB() {
        DatabaseMethods aDatabaseMethods = new DatabaseMethods();
        int aCountSelected = 0;
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).isSelected()) {
                aCountSelected++;
            }
        }
        if (aCountSelected > 0) {
            for (int i = 0; i < getCount(); i++) {
                SetDetails aSetDetails = getItem(i);
                if (mOriginalSelectedValue[i] != aSetDetails.isSelected()) {
                    aDatabaseMethods.updateSetSelected(aSetDetails.getNumberOfSet(), aSetDetails
                            .isSelected());
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
