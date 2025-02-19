package com.bih.nic.e_wallet.activities;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.ShowMsg;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.util.ArrayList;
import java.util.HashMap;
public class ConfigurePrinterActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
   // private ActionBar actionBar;
    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;
    Toolbar toolbar_con_printer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_printer);
        toolbar_con_printer=findViewById(R.id.toolbar_con_printer);
        toolbar_con_printer.setTitle("Printer List");
        setSupportActionBar(toolbar_con_printer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mContext = this;
        Button button = findViewById(R.id.btnRestart);
        button.setOnClickListener(this);
        mPrinterList = new ArrayList<HashMap<String, String>>();
        mPrinterListAdapter = new SimpleAdapter(this, mPrinterList, R.layout.list_at, new String[] { "PrinterName", "Target" },
                new int[] { R.id.PrinterName, R.id.Target });
        ListView  list = findViewById(R.id.lstReceiveData);
        list.setAdapter(mPrinterListAdapter);
        list.setOnItemClickListener(this);
        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "start", mContext);
        }

        restartDiscovery();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    @Override
    public void onClick(View v) {
        // Do nothing
        if (v.getId() == R.id.btnRestart) {
        //    Toast.makeText(getApplicationContext(), "restart button clicked", Toast.LENGTH_SHORT).show();
            restartDiscovery();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> item  = mPrinterList.get(position);
        CommonPref.setPrinterMacAddress(getApplicationContext(),item.get("Target"));
        Toast.makeText(getApplicationContext(), item.get("Target"), Toast.LENGTH_LONG).show();
        finish();
    }
    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    ShowMsg.showException(e, "stop", mContext);
                    return;
                }
            }
        }
        mPrinterList.clear();
        mPrinterListAdapter.notifyDataSetChanged();
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "stop", mContext);
        }
    }
    private final DiscoveryListener mDiscoveryListener = deviceInfo -> runOnUiThread(new Runnable() {
        @Override
        public synchronized void run() {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("PrinterName", deviceInfo.getDeviceName());
            item.put("Target", deviceInfo.getTarget());
            mPrinterList.add(item);
            mPrinterListAdapter.notifyDataSetChanged();
        }
    });
}

