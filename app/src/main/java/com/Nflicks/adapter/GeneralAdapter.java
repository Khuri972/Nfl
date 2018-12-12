package com.Nflicks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nflicks.R;
import com.Nflicks.model.GeneralModel;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by CRAFT BOX on 1/24/2017.
 */

public class GeneralAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<GeneralModel> data = new ArrayList<>();
    private LayoutInflater inflater1 = null;
    String type;

    public interface Intercommunication {
        public void Check(String type);
    }

    public GeneralAdapter(Context context, ArrayList<GeneralModel> da, String type) {
        this.context = context;
        data = da;
        this.type = type;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            inflater1 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater1.inflate(R.layout.list_general, null);
        }
        TextView name;
        final CheckBox check;
        name = (TextView) vi.findViewById(R.id.list_general_name);
        check = (CheckBox) vi.findViewById(R.id.list_general_check);
        LinearLayout linear = (LinearLayout) vi.findViewById(R.id.list_general_linear);

        check.setVisibility(View.VISIBLE);
        check.setChecked(data.get(position).isChecked());
        name.setText("" + data.get(position).getName());

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                GeneralModel da = data.get(position);
                da.setChecked(b);
                try {
                    Intercommunication intercommunication = (Intercommunication) context;
                    intercommunication.Check(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (data.get(position).isChecked()) {
                        GeneralModel da = data.get(position);
                        da.setChecked(false);
                        check.setChecked(false);

                        Intercommunication intercommunication = (Intercommunication) context;
                        intercommunication.Check(type);
                    } else {
                        GeneralModel da = data.get(position);
                        da.setChecked(true);
                        check.setChecked(true);
                        Intercommunication intercommunication = (Intercommunication) context;
                        intercommunication.Check(type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return vi;
    }
}
