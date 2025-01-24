package com.bih.nic.e_wallet.activities;

import static java.util.Arrays.stream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.adapters.ConsumerItemAdapter;
import com.bih.nic.e_wallet.asynkTask.BalanceLoader;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.BookNoEntity;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.retrofit.APIClient;
import com.bih.nic.e_wallet.retrofit.APIInterface;
import com.bih.nic.e_wallet.retrofitPoso.SmartConsumerDetail;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerListActivity extends AppCompatActivity {
    RecyclerView recycler_list_consumer;
    TextView text_no_data_found;
    Toolbar toolbar_sel_topup;
    ConsumerItemAdapter consumerItemAdapter;
    ArrayList<MRUEntity> mruEntities;
    RelativeLayout rel_search;
    private APIInterface apiInterface;

    ProgressDialog progressDialog;
    AlertDialog alertDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        toolbar_sel_topup=findViewById(R.id.toolbar_sel_topup);
        toolbar_sel_topup.setTitle("Search Consumer List");
        setSupportActionBar(toolbar_sel_topup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        text_no_data_found= findViewById(R.id.text_no_data_found);
        recycler_list_consumer=findViewById(R.id.recycler_list_consumer);
        rel_search=findViewById(R.id.rel_search);
        rel_search.setOnClickListener(v -> setUpDialog());

        String from=getIntent().getStringExtra("from");

        if (from.equalsIgnoreCase("adapter")){
            toolbar_sel_topup.setTitle(""+getIntent().getStringExtra("bookno"));
            mruEntities=new DataBaseHelper(ConsumerListActivity.this).getMRU(getIntent().getStringExtra("bookno"), CommonPref.getUserDetails(ConsumerListActivity.this).getUserID());
        }
        else if (from.equalsIgnoreCase("main")){
            mruEntities=new DataBaseHelper(ConsumerListActivity.this).getMRU(CommonPref.getUserDetails(ConsumerListActivity.this).getUserID(),getIntent().getStringArrayExtra("mstring"));
        }
        else {
            mruEntities=new DataBaseHelper(ConsumerListActivity.this).getMRU("",CommonPref.getUserDetails(ConsumerListActivity.this).getUserID());
        }
        if (mruEntities!=null) {
            if (mruEntities.size() > 0) {
                text_no_data_found.setVisibility(View.GONE);
                recycler_list_consumer.setVisibility(View.VISIBLE);
                consumerItemAdapter = new ConsumerItemAdapter(mruEntities, ConsumerListActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ConsumerListActivity.this);
                recycler_list_consumer.setLayoutManager(mLayoutManager);
                recycler_list_consumer.setItemAnimator(new DefaultItemAnimator());
                recycler_list_consumer.setAdapter(consumerItemAdapter);
            } else {
                text_no_data_found.setVisibility(View.VISIBLE);
                recycler_list_consumer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void setUpDialog() {
        final Dialog setup_dialog = new Dialog(ConsumerListActivity.this);
        // Include dialog.xml file
        setup_dialog.setContentView(R.layout.search_layout);
        // Set dialog title
        setup_dialog.setTitle("");
        setup_dialog.setCancelable(false);
        // set values for custom dialog components - text, image and button
        final EditText edit_con_id =  setup_dialog.findViewById(R.id.edit_con_id);
        final EditText edit_acount_no =  setup_dialog.findViewById(R.id.edit_acount_no);
        final EditText edit_book_no =  setup_dialog.findViewById(R.id.edit_book_no);

        edit_con_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0 && edit_acount_no.getText().toString().trim().length()>0){
                    edit_acount_no.setText("");
                }
                if (s.length()>0 && edit_book_no.getText().toString().trim().length()>0){
                    edit_book_no.setText("");
                }
            }
        });

        edit_acount_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                  if (s.length()>0 && edit_con_id.getText().toString().trim().length()>0){
                      edit_con_id.setText("");
                  }
                if (s.length()>0 && edit_book_no.getText().toString().trim().length()>0){
                    edit_book_no.setText("");
                }
            }
        });
        edit_book_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0 && edit_acount_no.getText().toString().trim().length()>0){
                    edit_acount_no.setText("");
                }
                if (s.length()>0 && edit_con_id.getText().toString().trim().length()>0){
                    edit_con_id.setText("");
                }
            }
        });
        ImageView close_setup =  setup_dialog.findViewById(R.id.img_close);
        close_setup.setOnClickListener(v -> setup_dialog.dismiss());
        Button search_for_mru =  setup_dialog.findViewById(R.id.search_for_mru);
        search_for_mru.setOnClickListener(v -> {
            // Close dialog
                UserInfo2 userInfo2= CommonPref.getUserDetails(ConsumerListActivity.this);
                if (edit_con_id.getText().toString().trim().length()>0) {
                    setup_dialog.dismiss();
                    new MRULoader(edit_con_id.getText().toString().trim()).execute(userInfo2.getUserID() + "|" +userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|" + edit_con_id.getText().toString().trim() + "|NA");
                }else if (edit_acount_no.getText().toString().trim().length()>0) {
                    setup_dialog.dismiss();
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|NA|" + edit_acount_no.getText().toString().trim());
                }
                else if (edit_book_no.getText().toString().trim().length()>0) {
                    setup_dialog.dismiss();
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() +"|"+edit_book_no.getText().toString().trim() +"|NA|NA");
                }
                else {
                    Toast.makeText(ConsumerListActivity.this, "Please Enter Cons Id or Account No or Book No", Toast.LENGTH_SHORT).show();
                }
        });
        setup_dialog.show();
    }


    private class MRULoader extends AsyncTask<String,Void,ArrayList<MRUEntity>> {
        private final ProgressDialog dialog1 = new ProgressDialog(ConsumerListActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(ConsumerListActivity.this).create();

        String conId=null;
        MRULoader(){

        }
        MRULoader(String conId){
          this.conId=conId;
        }
        @Override
        protected void onPreExecute() {
            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Loading...");
            this.dialog1.show();
        }

        @Override
        protected ArrayList<MRUEntity> doInBackground(String... strings) {
            String result="";
            ArrayList<MRUEntity> mruEntities=null;
                if (Utiilties.isOnline(ConsumerListActivity.this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        result = WebHandler.callByPostwithoutparameter(Urls_this_pro.DOWNLOAD_MRU_URL + reqString(String.valueOf(strings[0])));
                    }else{
                        Toast.makeText(ConsumerListActivity.this, "Your device must be KITKAT or Above !", Toast.LENGTH_SHORT).show();
                    }
                    if (result!=null) {
                        mruEntities= WebServiceHelper.mruParser(result);
                    }
                }else{
                    mruEntities=new DataBaseHelper(ConsumerListActivity.this).getMRU(CommonPref.getUserDetails(ConsumerListActivity.this).getUserID(),strings);
                }
            return mruEntities;
        }

        @Override
        protected void onPostExecute(ArrayList<MRUEntity> mruEntities) {
            super.onPostExecute(mruEntities);
            //if (this.dialog1.isShowing()) this.dialog1.cancel();
            if (mruEntities!=null) {
                long c=new DataBaseHelper(ConsumerListActivity.this).saveMru(mruEntities,CommonPref.getUserDetails(ConsumerListActivity.this).getUserID());
                if (c>0) {
                    recycler_list_consumer.setVisibility(View.VISIBLE);
                    text_no_data_found.setVisibility(View.GONE);
                    consumerItemAdapter = new ConsumerItemAdapter(mruEntities,ConsumerListActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ConsumerListActivity.this);
                    recycler_list_consumer.setLayoutManager(mLayoutManager);
                    recycler_list_consumer.setItemAnimator(new DefaultItemAnimator());
                    recycler_list_consumer.setAdapter(consumerItemAdapter);
                }else{
                    recycler_list_consumer.setVisibility(View.GONE);
                    text_no_data_found.setVisibility(View.VISIBLE);
                }
            }else{
                UserInfo2 userInfo2=CommonPref.getUserDetails(ConsumerListActivity.this);
                if(userInfo2.getUserID().startsWith("1") && this.conId!=null){
                    loadSmartConsumerDetails(this.conId);
                }else {
                    alertDialog.setTitle("MRU Downloaded");
                    alertDialog.setMessage(" MRU not Downloaded !");
                    alertDialog.setButton("OK", (dialog, which) -> alertDialog.dismiss());
                    alertDialog.show();
                }
            }
            if (this.dialog1.isShowing()) this.dialog1.cancel();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(),ConsumerListActivity.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString=encString.replaceAll("\\/","SSLASH").replaceAll("\\=","EEQUAL").replaceAll("\\+","PPLUS");
        return encString;
    }

    //newly added feature for smart meter serch
    private void loadSmartConsumerDetails(final String SmartConsumerId) {
       progressDialog = new ProgressDialog(ConsumerListActivity.this);
        apiInterface = APIClient.getClient(com.bih.nic.e_wallet.retrofit.Urls_this_pro.RETROFIT_BASE_URL3).create(APIInterface.class);
        Call<SmartConsumerDetail> call1 = apiInterface.getRuralConsumerDetails(SmartConsumerId);
        progressDialog = new ProgressDialog(ConsumerListActivity.this);
        progressDialog.setMessage("Loading Consumer NB Consumer Details");
        progressDialog.setCancelable(false);
        progressDialog.show();
        call1.enqueue(new Callback<SmartConsumerDetail>() {
            @Override
            public void onResponse(Call<SmartConsumerDetail> call, Response<SmartConsumerDetail> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                SmartConsumerDetail result = null;
                if (response.body() != null) result = response.body();
                if (result != null) {
                    MRUEntity mruEntity = getMRUOBJFromSMCD(result);
                    List<BookNoEntity> bookNoEntities = new DataBaseHelper(ConsumerListActivity.this).getBookNo(CommonPref.getUserDetails(ConsumerListActivity.this).getUserID());
                    int count=0;
                    for(BookNoEntity bookNoEntity: bookNoEntities){
                        if (bookNoEntity.getBookNo().equals(mruEntity)) {
                            count++;
                            break;
                        };
                    }
                    if (count>0) {
                        List<MRUEntity> mruEntities = Arrays.asList(mruEntity);
                        long c = new DataBaseHelper(ConsumerListActivity.this).saveMru(mruEntities, CommonPref.getUserDetails(ConsumerListActivity.this).getUserID());
                        if (c > 0) {
                            recycler_list_consumer.setVisibility(View.VISIBLE);
                            text_no_data_found.setVisibility(View.GONE);
                            consumerItemAdapter = new ConsumerItemAdapter(mruEntities, ConsumerListActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ConsumerListActivity.this);
                            recycler_list_consumer.setLayoutManager(mLayoutManager);
                            recycler_list_consumer.setItemAnimator(new DefaultItemAnimator());
                            recycler_list_consumer.setAdapter(consumerItemAdapter);
                        } else {
                            recycler_list_consumer.setVisibility(View.GONE);
                            text_no_data_found.setVisibility(View.VISIBLE);
                        }
                    }else{
                        recycler_list_consumer.setVisibility(View.GONE);
                        text_no_data_found.setText("Book is not found !");
                        text_no_data_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    recycler_list_consumer.setVisibility(View.GONE);
                    text_no_data_found.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(Call<SmartConsumerDetail> call, Throwable t) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(ConsumerListActivity.this);
               // mruLoader = (PayDetailsActivity.MRULoader) new PayDetailsActivity.MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
                Log.e("error", t.getMessage());
                t.printStackTrace();
                //Toast.makeText(ConsumerListActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
                call.cancel();
            }
        });
    }

    private MRUEntity getMRUOBJFromSMCD(SmartConsumerDetail smartConsumerDetail) {
        MRUEntity mruEntity2=new MRUEntity();
        mruEntity2.setMETER_TYPE((smartConsumerDetail.getMeterType().equals("SMART_PREPAID")||smartConsumerDetail.getMeterType().equals("SMART_POSTPAID"))?"PREPAID":"POSTPAID");
        mruEntity2.setACT_NO(smartConsumerDetail.getActNo());
        mruEntity2.setCNAME(smartConsumerDetail.getComName());
        mruEntity2.setBILL_NO(smartConsumerDetail.getBillNo());
        mruEntity2.setBOOK_NO(smartConsumerDetail.getBookNo());
        mruEntity2.setCON_ID(smartConsumerDetail.getConId());
        mruEntity2.setOLD_CON_ID(smartConsumerDetail.getOldConId());
        mruEntity2.setBILL_ADDR1(smartConsumerDetail.getAddress());
        mruEntity2.setFA_HU_NAME(smartConsumerDetail.getFatherName());
        mruEntity2.setLAST_PAY_DATE(smartConsumerDetail.getPrevPaidDt());
        mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getGrossAmt());
        mruEntity2.setMOBILE_NO(smartConsumerDetail.getMobNo());
        mruEntity2.setMETER_NO(smartConsumerDetail.getMeterNo());
        mruEntity2.setTARIFF_ID(smartConsumerDetail.getCategory());
        mruEntity2.setSUB_DIV_ID(smartConsumerDetail.getSubDivId());
        return mruEntity2;
    }



}