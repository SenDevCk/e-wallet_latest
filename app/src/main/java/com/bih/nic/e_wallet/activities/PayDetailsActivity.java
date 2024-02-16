package com.bih.nic.e_wallet.activities;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.e_wallet.R;
//import com.bih.nic.e_wallet.asynkTask.BalanceLoader;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.interfaces.BalanceListner;
import com.bih.nic.e_wallet.retrofit.APIClient;
import com.bih.nic.e_wallet.retrofit.APIInterface;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by NIC2 on 1/26/2018.
 */
public class PayDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_pay;
Context context;
    androidx.appcompat.widget.Toolbar toolbar_pay;
    MRUEntity mruEntity;
    String flag_unbilled;
    TextView text_deleer_name, text_acc_no, text_con_id, text_old_con_id, text_meter_no, text_book_no;
    TextView text_address, text_payable_amount;
    EditText edit_mobile2, edit_payable_amount;
    CheckBox check_term;
    double bal = 0.00;
    //BalanceLoader balance_loader;
    MRULoader mruLoader;

    String key = "_USER_DETAILS";

    private APIInterface apiInterface;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_details);
        toolbar_pay = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_pay);
        toolbar_pay.setTitle("Pay here (Dec2)");
        setSupportActionBar(toolbar_pay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        flag_unbilled=getIntent().getStringExtra("flag");
        context=PayDetailsActivity.this;
        init();
    }
    private void init() {
        if (getIntent().hasExtra("object")) {
            mruEntity = (MRUEntity) getIntent().getSerializableExtra("object");
            text_deleer_name =  findViewById(R.id.text_deleer_name);
            text_acc_no =  findViewById(R.id.text_acc_no);
            text_con_id =  findViewById(R.id.text_con_id);
            text_old_con_id =  findViewById(R.id.text_old_con_id);
            edit_mobile2 =  findViewById(R.id.edit_mobile2);
            text_meter_no =  findViewById(R.id.text_meter_no);
            text_book_no =  findViewById(R.id.text_book_no);
            text_address =  findViewById(R.id.text_address);
            text_payable_amount =  findViewById(R.id.text_payable_amount);
            edit_payable_amount =  findViewById(R.id.edit_payable_amount);
            check_term =  findViewById(R.id.check_term);
            text_deleer_name.setText(mruEntity.getCNAME());
            text_acc_no.setText("A/c- " + mruEntity.getACT_NO());
            text_con_id.setText(""+mruEntity.getCON_ID());
            text_old_con_id.setText(""+mruEntity.getOLD_CON_ID());
            if (!mruEntity.getMOBILE_NO().equals("NA"))
                edit_mobile2.setText(""+mruEntity.getMOBILE_NO());
            edit_mobile2.setEnabled(false);
            text_meter_no.setText(""+mruEntity.getMETER_NO());
            text_book_no.setText(""+mruEntity.getBOOK_NO());
            text_address.setText(""+mruEntity.getFA_HU_NAME() + " , " + mruEntity.getBILL_ADDR1());
            text_payable_amount.setText(""+mruEntity.getPAYBLE_AMOUNT());
        }
        button_pay =  findViewById(R.id.button_pay);
        button_pay.setOnClickListener(this);
//        BalanceLoader.bindmListener(balance -> {
//            bal = balance;
//            toolbar_pay.setSubtitle("Avl Balance(Rs.):" + String.valueOf(balance) + "/-");
//        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_pay) {
          /*  if (edit_mobile2.getText().toString().trim().equals("") || edit_mobile2.getText().toString().trim().length() < 10) {
                Toast.makeText(PayDetailsActivity.this, "Enter Valid Mobile No !", Toast.LENGTH_SHORT).show();
            } else*/ if (text_payable_amount.getText().toString().equals("Please connect to internet !")) {
                Toast.makeText(PayDetailsActivity.this, "Please connect to internet !", Toast.LENGTH_SHORT).show();
            } else if (edit_payable_amount.getText().toString().trim().equals("") || edit_payable_amount.getText().toString().trim().equals("0")) {
                Toast.makeText(PayDetailsActivity.this, "Please enter Payable amount !", Toast.LENGTH_SHORT).show();
            }
           /* else if (!text_payable_amount.getText().toString().equalsIgnoreCase("Please connect to internet !")) {
                if (Double.parseDouble(text_payable_amount.getText().toString().trim())<=Double.parseDouble(edit_payable_amount.getText().toString().trim()) ) {
                    Toast.makeText(PayDetailsActivity.this, "Amount to be pay must be less or equal to Total Amount!", Toast.LENGTH_SHORT).show();
                }

            }
            else if (Double.parseDouble(edit_payable_amount.getText().toString().trim()) > bal) {
                Toast.makeText(PayDetailsActivity.this, "Low Balance !", Toast.LENGTH_SHORT).show();
            } */
            else if (!check_term.isChecked()) {
                Toast.makeText(PayDetailsActivity.this, "Please check terms !", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialogForCheckDetails(mruEntity);
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo2 userInfo2 = CommonPref.getUserDetails(context);
        if (Utiilties.isOnline(PayDetailsActivity.this)) {
                mruLoader = (MRULoader) new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
        } else {
            text_payable_amount.setText("Please connect to internet !");
            text_payable_amount.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }
    private class MRULoader extends AsyncTask<String, Void, ArrayList<MRUEntity>> {
        private final ProgressDialog dialog1 = new ProgressDialog(PayDetailsActivity.this);
        //private final AlertDialog alertDialog = new AlertDialog.Builder(PayDetailsActivity.this).create();
        @Override
        protected void onPreExecute() {
            this.dialog1.setCanceledOnTouchOutside(false);
            this.dialog1.setMessage("Loading...");
            this.dialog1.show();
        }
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<MRUEntity> doInBackground(String... strings) {
            String result = "";
            ArrayList<MRUEntity> mruEntities = null;
            if (Utiilties.isOnline(PayDetailsActivity.this)) {
                result = WebHandler.callByPostwithoutparameter( Urls_this_pro.DOWNLOAD_MRU_URL + reqString(String.valueOf(strings[0])));
                if (result != null) {
                    mruEntities = WebServiceHelper.mruParser(result);
                }
            } else {
                Toast.makeText(PayDetailsActivity.this, "No Internet connection !", Toast.LENGTH_SHORT).show();
            }

            return mruEntities;
        }

        @Override
        protected void onPostExecute(ArrayList<MRUEntity> mruEntities) {
            super.onPostExecute(mruEntities);
            if (this.dialog1.isShowing()) this.dialog1.cancel();
            if (mruEntities != null) {
                if (mruEntities.size() > 0) {
                    mruEntity=mruEntities.get(0);
                    text_payable_amount.setText(""+mruEntities.get(0).getPAYBLE_AMOUNT());
                    text_payable_amount.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                    text_old_con_id.setText(""+mruEntities.get(0).getOLD_CON_ID());
                    text_meter_no.setText(""+mruEntities.get(0).getMETER_NO());
                    text_address.setText(""+mruEntities.get(0).getBILL_ADDR1());
                    edit_mobile2.setText(""+mruEntities.get(0).getMOBILE_NO());

                    mruEntity.setPAYBLE_AMOUNT(mruEntities.get(0).getPAYBLE_AMOUNT());
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                    //balance_loader = (BalanceLoader) new BalanceLoader(PayDetailsActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo());
                    loadBalance(reqString(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo()));
                } else {
                    Toast.makeText(PayDetailsActivity.this, "No record found !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                text_payable_amount.setText("Please connect to internet !");
                text_payable_amount.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    private void loadBalance(String reqString) {
        apiInterface = APIClient.getClient(com.bih.nic.e_wallet.retrofit.Urls_this_pro.RETROFIT_BASE_URL).create(APIInterface.class);
        Call<String> call1 = apiInterface.loadBalance(reqString);
        progressDialog = new ProgressDialog(PayDetailsActivity.this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                String result = null;
                if (response.body() != null) result = response.body();
                if (result != null) {
                    try {
                            JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.has("accountBalance")) {
                            SharedPreferences prefs = getSharedPreferences(key,
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("WalletAmount", jsonObject.getString("accountBalance"));
                            editor.commit();
                            //balanceListner.balanceReceived(Double.parseDouble(jsonObject.getString("accountBalance")));
                            toolbar_pay.setSubtitle("Avl Balance(Rs.):" + jsonObject.getString("accountBalance") + "/-");
                        }else{
                            Toast.makeText(PayDetailsActivity.this, ""+result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PayDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PayDetailsActivity.this, "Server Problem", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error", t.getMessage());
                Toast.makeText(PayDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
                call.cancel();
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String reqString(String req_string) {
        byte[] chipperdata = Utiilties.rsaEncrypt(req_string.getBytes(), PayDetailsActivity.this);
        Log.e("chiperdata", new String(chipperdata));
        String encString = android.util.Base64.encodeToString(chipperdata, android.util.Base64.NO_WRAP);//.getEncoder().encodeToString(chipperdata);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        return encString;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.pay_details_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ref_menu: {
                //AlertDialogForPrinter();
                UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                if (Utiilties.isOnline(PayDetailsActivity.this)) {
                    new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getSerialNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
                } else {
                    text_payable_amount.setText("Please connect to internet !");
                    text_payable_amount.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                break;
            }
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
    public void AlertDialogForCheckDetails(final MRUEntity mruEntity1) {
        final Dialog dialog = new Dialog(PayDetailsActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialog.setContentView(R.layout.pay_details_dialog);
        dialog.setTitle("Verify Details");
        TextView text_consumer_name =  dialog.findViewById(R.id.text_consumer_name);
        TextView text_address =  dialog.findViewById(R.id.text_address);
        TextView text_ac_no =  dialog.findViewById(R.id.text_ac_no);
        TextView text_con_id =  dialog.findViewById(R.id.text_con_id);
        TextView text_old_con_id =  dialog.findViewById(R.id.text_old_con_id);
        TextView text_book_no =  dialog.findViewById(R.id.text_book_no);
        TextView text_mobile =  dialog.findViewById(R.id.text_mobile);
        TextView text_meter_no =  dialog.findViewById(R.id.text_meter_no);
        TextView total_amount =  dialog.findViewById(R.id.total_amount);
        TextView text_amount_paid =  dialog.findViewById(R.id.text_amount_paid);
        final TextView check_con_pay =  dialog.findViewById(R.id.check_con_pay);
        final Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        check_con_pay.setVisibility(View.GONE);
        text_consumer_name.setText("" + mruEntity1.getCNAME());
        text_address.setText(mruEntity1.getBILL_ADDR1());
        text_ac_no.setText(" A/C- " + mruEntity1.getACT_NO());
        text_con_id.setText(" Con ID :-" + mruEntity1.getCON_ID());
        text_old_con_id.setText(" Old Con ID :-" + mruEntity1.getOLD_CON_ID());
        text_book_no.setText(" Book No :-" + mruEntity1.getBOOK_NO());
        text_mobile.setText(" Mobile : " + edit_mobile2.getText().toString().trim());
        text_meter_no.setText(" Meter No. : " + mruEntity.getMETER_NO());
        total_amount.setText(" Total Amount: " + mruEntity1.getPAYBLE_AMOUNT().trim());
        text_amount_paid.setText(" Amount paying: Rs." + edit_payable_amount.getText().toString().trim());
        Button verify_pay = (Button) dialog.findViewById(R.id.verify_pay);
        Button cancel_pay = (Button) dialog.findViewById(R.id.cancel_pay);
        // if button is clicked, close the custom dialog
        cancel_pay.setOnClickListener(v -> dialog.dismiss());
        verify_pay.setOnClickListener(v -> {
            //dialog.dismiss();
            UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this.getApplicationContext());
            Statement statement = new DataBaseHelper(PayDetailsActivity.this).getTransStatus(userInfo2, mruEntity1.getCON_ID(), Utiilties.getDateString("dd/MM/yyyy"));
            if (statement != null) {
                if ((statement.getTransStatus().equals("TI") || statement.getTransStatus().equals("TP") || statement.getTransStatus().equals("TC")) && Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()),statement.getPayDate()) <= 15) {
                    Toast.makeText(PayDetailsActivity.this, "You already requested for this Consumer payment ! Please either syncronize or verify statement ! ", Toast.LENGTH_SHORT).show();
                    check_con_pay.setVisibility(View.VISIBLE);
                    check_con_pay.setText("You already requested for this Consumer payment ! Please either syncronize or verify statement ! You can request for another payment for this Consumer after "+(15-Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()),statement.getPayDate()))+"minuts / total 15 minuts ! ");
                    check_con_pay.startAnimation(fade_in);
                } else {
                    if(mruEntity1.getCON_ID().startsWith("2")) {
                        pay(mruEntity1, check_con_pay, fade_in);

                    }else{
                        if(mruEntity1.getTARIFF_ID().equalsIgnoreCase("KJ")){
                            pay_North_KJ(mruEntity1, check_con_pay, fade_in);
                        }else {
                            pay_North(mruEntity1, check_con_pay, fade_in);
                        }
                    }
                }
            } else {
                if(mruEntity1.getCON_ID().startsWith("2")) {
                    pay(mruEntity1, check_con_pay, fade_in);
                }else{
                      if(mruEntity1.getTARIFF_ID().equalsIgnoreCase("KJ")){
                        pay_North_KJ(mruEntity1, check_con_pay, fade_in);
                    }else {
                        pay_North(mruEntity1, check_con_pay, fade_in);
                    }
                }
            }
        });

        dialog.show();
    }

  /*  private void pay(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {

       if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            navigationIntent(mruEntity1);
        }


        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
               *//* if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*
          //  } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.0833) ) {
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble("50")))  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50" );
                check_con_pay.startAnimation(fade_in);
               *//* if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*
            }
                else {
                navigationIntent(mruEntity1);
            }
        }

        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            //if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
               *//* if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*

           // } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.0833) ) {
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble("50")))  {
                    check_con_pay.setVisibility(View.VISIBLE);
                    check_con_pay.setText("You can't pay less than Rs. 50");
                    check_con_pay.startAnimation(fade_in);
               *//* if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*

            } else {
                navigationIntent(mruEntity1);
            }
        }

        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5));
                check_con_pay.startAnimation(fade_in);
                *//*if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*
         //   }else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.0833) ) {
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < (Double.parseDouble("50")))  {
                check_con_pay.setVisibility(View.VISIBLE);
               // check_con_pay.setText(" You can't pay less than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.0833);
                check_con_pay.setText(" You can't pay less than Rs. 50 " );
                check_con_pay.startAnimation(fade_in);
               *//* if (!check_con_pay.isChecked()) {
                    Toast.makeText(PayDetailsActivity.this, "Please Check and Conform Payment !", Toast.LENGTH_SHORT).show();
                } else {
                    navigationIntent(mruEntity1);
                }*//*
            }

            else {
                navigationIntent(mruEntity1);
            }
        }
    }*/

    private void pay(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 " );
                check_con_pay.startAnimation(fade_in);
            }else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            }else if(Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())){
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+ Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()));
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);

            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 " );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 ");
                check_con_pay.startAnimation(fade_in);

            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 " );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
    }
    private void pay_North(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
       if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < 0)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+0);
                check_con_pay.startAnimation(fade_in);
            }else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            }else if(Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())){
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+ Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()));
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083 );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083 );
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083)  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083 );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
    }
    private void pay_North_KJ(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        int max=200;
        int min= (int) (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083);
        int compareval=0;
        if(max>min){
            compareval=min;
        }else{
            compareval=max;
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(""+0))  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+0);
                check_con_pay.startAnimation(fade_in);
            }else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            }else if(Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(""+compareval)){
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+ compareval);
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);

            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(""+compareval))  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+compareval );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(""+compareval))  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+compareval );
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())*1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(""+compareval))  {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. "+compareval );
                check_con_pay.startAnimation(fade_in);
            }
            else {
                navigationIntent(mruEntity1);
            }
        }
    }
    private void navigationIntent(MRUEntity mruEntity2) {
        Intent intent = new Intent(PayDetailsActivity.this, PinCodeActivity.class);
        intent.putExtra("object", mruEntity2);
        intent.putExtra("amount", edit_payable_amount.getText().toString().trim());
        intent.putExtra("mobile", edit_mobile2.getText().toString().trim());
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
//        if (balance_loader != null) {
//            if (balance_loader.getStatus() == AsyncTask.Status.RUNNING) {
//                balance_loader.cancel(true);
//            }
//        }
        if (mruLoader != null) {
            if (mruLoader.getStatus() == AsyncTask.Status.RUNNING) {
                mruLoader.cancel(true);
            }
        }
        super.onDestroy();
    }
}
