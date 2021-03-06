package com.whitesmith.ruimagalhaes.netprofiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AllWiFiNetworks extends Activity {

    private WifiManager wifi;
    private ListView network_list;
    private List<WifiConfiguration> results;
    private EditText editsearch;
    private NetworkListAdapter networkListAdapter;
    private AnimationAdapter mAnimAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_wifi_networks_layout);
        getActionBar().setIcon(R.drawable.action_bar_icon);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.action_bar_custom, null);
        ((TextView)v.findViewById(R.id.title)).setText("Add Network");
        getActionBar().setCustomView(v);

        network_list = (ListView) findViewById(R.id.network_list);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
        results= wifi.getConfiguredNetworks();
        List<WifiConfiguration> results_filtered;
        if(results!=null){
            results_filtered = new ArrayList<WifiConfiguration>();
            Bundle bundle = getIntent().getExtras();
            if(bundle != null){
                String[] network_array = bundle.getStringArray(FavNetworks.NETWORK_ARRAY);
                for (WifiConfiguration network : results) {
                    if(!Arrays.asList(network_array).contains(network.SSID)) {
                        results_filtered.add(network);
                    }
                }
            }

            networkListAdapter = new NetworkListAdapter(this, results_filtered);
            network_list.setAdapter(networkListAdapter);
            network_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent();
                    i.putExtra(FavNetworks.NETWORK, networkListAdapter.getNetwork(position).SSID);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });

            mAnimAdapter = new SwingBottomInAnimationAdapter(networkListAdapter);
            mAnimAdapter.setAbsListView(network_list);
            network_list.setAdapter(mAnimAdapter);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_network_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_networks);
        SearchView searchView = (SearchView) menuItem.getActionView();

        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        editsearch = (EditText) searchView.findViewById(searchTextId);
        if(editsearch != null){
            editsearch.addTextChangedListener(textWatcher);
            editsearch.setHintTextColor(getResources().getColor(R.color.white));
            editsearch.setHint("Network Name");
        }

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        if (searchPlateView != null) {
            searchPlateView.setBackgroundColor(getResources().getColor(R.color.red));
        }

        return super.onCreateOptionsMenu(menu);

    }

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
            if(networkListAdapter != null){
                networkListAdapter.filter(text);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }

    };

    public class NetworkListAdapter extends ArrayAdapter<String> {
        private final Context mContext;
        private List<WifiConfiguration> networks;
        private List<WifiConfiguration> networks_raw;
        private String searchText;

        public NetworkListAdapter(Context context, List<WifiConfiguration> networks) {
            this.mContext = context;
            this.networks = new ArrayList<WifiConfiguration>(networks);
            this.networks_raw = new ArrayList<WifiConfiguration>(networks);
            this.searchText = "";
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);
            TextView label = (TextView) rowView.findViewById(R.id.label);
            String network_ssid = networks.get(position).SSID.substring(1, networks.get(position).SSID.length()-1);

            String name = network_ssid.toLowerCase(Locale.getDefault());
            Spannable spanText = Spannable.Factory.getInstance().newSpannable(network_ssid);
            int start = name.indexOf(searchText);
            spanText.setSpan(new BackgroundColorSpan(mContext.getResources().getColor(R.color.light_red)),start, start+searchText.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            label.setText(spanText);

            return rowView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            if (charText.length() == 0) {
                searchText = "";
                networks = new ArrayList<WifiConfiguration>(networks_raw);
            }
            else
            {
                searchText = charText;
                networks.clear();
                for (WifiConfiguration config : networks_raw)
                {
                    if (config.SSID.toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        networks.add(config);
                    }
                }
            }
            Log.e(String.valueOf(networks.size()), String.valueOf(networks_raw.size()));

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return networks.size();
        }

        public WifiConfiguration getNetwork(int position){
            return networks.get(position);
        }
    }

}