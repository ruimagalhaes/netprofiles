package com.whitesmith.ruimagalhaes.netprofiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import com.fortysevendeg.swipelistview.SwipeListView;
import android.widget.TextView;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavNetworks extends Activity {

    public static final String PREFS_NAME = "NetworkProfilesPrefs";
    public static final String NETWORK = "network";
    static final String NETWORK_SET = "networl_set";
    static final int PICK_NETWORK_REQUEST = 200;
    public static final String NETWORK_ARRAY = "NETWORK_ARRAY";

    private AnimationAdapter mAnimAdapter;
    private SwipeListView network_list;
    private List<String> your_networks_array;
    private BaseAdapter yourNetworksAdapter;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_networks);
        getActionBar().setIcon(R.drawable.action_bar_icon);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.action_bar_custom, null);
        ((TextView)v.findViewById(R.id.title)).setText(getResources().getString(R.string.your_profiles));
        getActionBar().setCustomView(v);

        settings = getSharedPreferences(PREFS_NAME, 0);

        your_networks_array = new ArrayList<String>(settings.getStringSet(NETWORK_SET, new HashSet<String>()));
        network_list = (SwipeListView) findViewById(R.id.my_networks_list);
        yourNetworksAdapter = new YourNetworksAdapter(this);
        mAnimAdapter = new SwingBottomInAnimationAdapter(yourNetworksAdapter);
        mAnimAdapter.setAbsListView(network_list);
        network_list.setAdapter(mAnimAdapter);
        setBackgroundListView();

    }

    public void openNetworkPicker(View view) {
        Intent i = new Intent(this,AllWiFiNetworks.class);

        Bundle b=new Bundle();
        String[] currentNetworksArray = your_networks_array.toArray(new String[your_networks_array.size()]);

        b.putStringArray(NETWORK_ARRAY, currentNetworksArray);
        i.putExtras(b);

        startActivityForResult(i, PICK_NETWORK_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_NETWORK_REQUEST) {
            if (resultCode == RESULT_OK) {
                String home_network_name = data.getExtras().getString(NETWORK);
                your_networks_array.add(home_network_name);
                updateList(your_networks_array);
            }
        }
    }

    private void updateList(List<String> networks){
        SharedPreferences.Editor editor = settings.edit();
        Set<String> set = new HashSet<String>(networks);
        editor.putStringSet(NETWORK_SET, set);
        editor.commit();
        yourNetworksAdapter.notifyDataSetChanged();
        setBackgroundListView();
    }

    private void setBackgroundListView() {
        if(yourNetworksAdapter.getCount() == 0)
            findViewById(R.id.no_network_view).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.no_network_view).setVisibility(View.GONE);
    }

    public class YourNetworksAdapter extends ArrayAdapter<String> {

        private final Context mContext;

        public YourNetworksAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            FrameLayout view = (FrameLayout) convertView;
            if (view == null) {
                view = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.row_my_networks_layout, parent, false);
            }

            TextView label = (TextView) view.findViewById(R.id.label);
            TextView state = (TextView) view.findViewById(R.id.state);
            String [] ringModes = getResources().getStringArray(R.array.ring_modes);
            int mode = settings.getInt(your_networks_array.get(position), -1);
            switch (mode) {
                case -1:
                    state.setText("Swipe to set ringer mode");
                    break;
                case 0:
                    state.setText(ringModes[0]);
                    break;
                case 1:
                    state.setText(ringModes[1]);
                    break;
                case 2:
                    state.setText(ringModes[2]);
                    break;
                case 3:
                    state.setText(ringModes[3]);
                    break;
                default:
                    break;
            }

            final String network_name = your_networks_array.get(position).substring(1,your_networks_array.get(position).length()-1);
            label.setText(network_name);

            View.OnClickListener option_listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String networkSSID = your_networks_array.get(position);
                    SharedPreferences.Editor editor = settings.edit();
                    switch (view.getId()){

                        case R.id.silence_option:
                            editor.putInt(networkSSID, 0);
                            editor.commit();
                            yourNetworksAdapter.notifyDataSetChanged();
                            network_list.closeAnimate(position);
                            break;
                        case R.id.vibrate_option:
                            editor.putInt(networkSSID, 1);
                            editor.commit();
                            yourNetworksAdapter.notifyDataSetChanged();
                            network_list.closeAnimate(position);
                            break;
                        case R.id.ringer_option:
                            editor.putInt(networkSSID, 2);
                            editor.commit();
                            yourNetworksAdapter.notifyDataSetChanged();
                            network_list.closeAnimate(position);
                            break;
                        case R.id.trash_option:
                           // network_list.dismiss(position);
                            network_list.closeAnimate(position);
                            your_networks_array.remove(position);
                            updateList(your_networks_array);
                            break;
                        default:
                            break;
                    }
                }
            };

            view.findViewById(R.id.vibrate_option).setOnClickListener(option_listener);
            view.findViewById(R.id.silence_option).setOnClickListener(option_listener);
            view.findViewById(R.id.ringer_option).setOnClickListener(option_listener);
            view.findViewById(R.id.trash_option).setOnClickListener(option_listener);
            return view;
        }

        @Override
        public int getCount() {
            return your_networks_array.size();
        }

        public String getNetwork(int position){
            return your_networks_array.get(position);
        }
    }

}
