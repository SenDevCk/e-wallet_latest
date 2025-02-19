package com.bih.nic.e_wallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.GrivanceListAdapter;
import com.bih.nic.e_wallet.asynkTask.GrievanceListService;
import com.bih.nic.e_wallet.entity.GrivanceEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.interfaces.GrivanceListBinder;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import java.util.ArrayList;
public class GrivanceListActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    GrivanceListAdapter grivanceListAdapter;
    ListView list_Grivance_Itm;
    ArrayList<GrivanceEntity> grivanceEntities;
    Toolbar toolbar;
    TextView text_no_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grivance_list);
        toolbar =  findViewById(R.id.toolbar_gri_list);
        toolbar.setTitle("Grievances");
        toolbar.setSubtitle("Swip bellow to update list");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        list_Grivance_Itm =  findViewById(R.id.list_Grivance_Itm);
        text_no_data =  findViewById(R.id.text_no_data);
        text_no_data.setVisibility(View.GONE);
        loadpageData();
        swipeRefreshLayout =  findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadpageData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void loadpageData() {
        GrievanceListService.grievanceListBinderMethod(new GrivanceListBinder() {
            @Override
            public void grivanceFound(ArrayList<GrivanceEntity> grivanceEntityArrayList) {
                if (grivanceEntityArrayList != null) {
                    toolbar.setSubtitle("Total Records : ( " + grivanceEntityArrayList.size() + " )");
                    if (grivanceEntityArrayList.size() > 0) {
                        grivanceEntities = grivanceEntityArrayList;
                        grivanceListAdapter = new GrivanceListAdapter(GrivanceListActivity.this, grivanceEntityArrayList);
                        list_Grivance_Itm.setAdapter(grivanceListAdapter);
                    }else {
                        text_no_data.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void grivanceNotFound(String response) {
                if (grivanceEntities != null) {
                    text_no_data.setVisibility(View.VISIBLE);
                    grivanceEntities.clear();
                    grivanceListAdapter.notify();
                    Toast.makeText(GrivanceListActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    toolbar.setSubtitle("" + response);
                }
            }
        });
        UserInfo2 userInfo2 = CommonPref.getUserDetails(GrivanceListActivity.this);
        if (Utiilties.isOnline(GrivanceListActivity.this)) {
            new GrievanceListService(GrivanceListActivity.this).execute(userInfo2.getUserID().trim() + "|" + userInfo2.getPassword().trim() + "|" + userInfo2.getImeiNo().trim() + "|" + userInfo2.getSerialNo().trim());
        } else {
            Toast.makeText(this, "Please Enable Internet Connection !", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
