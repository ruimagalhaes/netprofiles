package com.whitesmith.ruimagalhaes.netprofiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListViewTouchListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class FavNetworks extends Activity {

    public static final String PREFS_NAME = "NetworkProfilesPrefs";
    public static final String NETWORK = "network";
    static final String NETWORK_SET = "networl_set";
    static final int PICK_NETWORK_REQUEST = 200;

    //private ListView network_list;
    private SwipeListView network_list;
    private List<String> your_networks_array;
    private YourNetworksAdapter yourNetworksAdapter;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_networks);

        settings = getSharedPreferences(PREFS_NAME, 0);

        your_networks_array = new ArrayList<String>(settings.getStringSet(NETWORK_SET, new HashSet<String>()));

        network_list = (SwipeListView) findViewById(R.id.my_networks_list);


        network_list.setSwipeListViewListener(new BaseSwipeListViewListener() {
                                     @Override
                                     public void onClickFrontView(int position) {
                                         Dialog ringDialog = getRingDialog(yourNetworksAdapter.getNetwork(position));
                                         ringDialog.show();
                                     }
                                     @Override
                                        public void onDismiss(int[] reverseSortedPositions) {
                                            your_networks_array.remove(reverseSortedPositions[0]);
                                            updateList(your_networks_array);
                                        }
                                   });


        yourNetworksAdapter = new YourNetworksAdapter(this);
        network_list.setAdapter(yourNetworksAdapter);
        updateNetworks();
    }

    private void updateNetworks() {
        yourNetworksAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fav_networks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent i = new Intent(this,AllWiFiNetworks.class);
            startActivityForResult(i, PICK_NETWORK_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        updateNetworks();
    }

    public class YourNetworksAdapter extends ArrayAdapter<String> {
        private final Context context;

        public YourNetworksAdapter(Context context) {
            super(context, R.layout.row_layout);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_my_networks_layout, parent, false);
            TextView label = (TextView) rowView.findViewById(R.id.label);
            TextView state = (TextView) rowView.findViewById(R.id.state);
            String [] ringModes = getResources().getStringArray(R.array.ring_modes);
            int mode = settings.getInt(your_networks_array.get(position), -1);
            switch (mode) {
                case -1:
                    state.setText("Tap to set ring mode");
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

            label.setText(your_networks_array.get(position));

            return rowView;
        }

        @Override
        public int getCount() {
            return your_networks_array.size();
        }

        public String getNetwork(int position){
            return your_networks_array.get(position);
        }
    }

    private Dialog getRingDialog(String network) {
        final String networkSSID = network;
        int mode = settings.getInt(networkSSID, -1);
        //if(mode == -1) mode = 3;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings for "+networkSSID)
                .setSingleChoiceItems(R.array.ring_modes, mode,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(networkSSID, position);
                        editor.commit();
                        dialog.dismiss();
                        yourNetworksAdapter.notifyDataSetChanged();
                        /*if(-1 < position && position < 3)
                            Toast.makeText(getBaseContext(), "All set!", Toast.LENGTH_SHORT).show();*/
                    }
                });

        return builder.create();
    }
}
