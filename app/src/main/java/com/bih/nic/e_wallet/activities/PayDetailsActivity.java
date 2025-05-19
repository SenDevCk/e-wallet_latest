package com.bih.nic.e_wallet.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.asynkTask.BalanceLoader;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.MRUEntity;
import com.bih.nic.e_wallet.entity.Statement;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.retrofit.APIClient;
import com.bih.nic.e_wallet.retrofit.APIInterface;
import com.bih.nic.e_wallet.retrofitPoso.SmartConsumerDetail;
import com.bih.nic.e_wallet.retrofitPoso.SmartMeterBalanceRequest;
import com.bih.nic.e_wallet.retrofitPoso.SmartMeterDetail;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.Urls_this_pro;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.bih.nic.e_wallet.utilitties.WebHandler;
import com.bih.nic.e_wallet.webservices.WebServiceHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chandan on 1/26/2018.
 */
public class PayDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_pay,check_smt_balance;
    Context context;
    androidx.appcompat.widget.Toolbar toolbar_pay;
    MRUEntity mruEntity;
    String flag_unbilled;
    TextView text_deleer_name, text_acc_no, text_con_id, text_meter_type, text_old_con_id, text_meter_no, text_book_no;
    TextView text_address, text_payable_amount;
    EditText edit_mobile2, edit_payable_amount;
    CheckBox check_term;
    double bal = 0.00;
    BalanceLoader balance_loader;
    MRULoader mruLoader;

    String key = "_USER_DETAILS";
    ProgressDialog progressDialog;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_details);
        toolbar_pay = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_pay);
        toolbar_pay.setTitle("Pay here (Dec2)");
        setSupportActionBar(toolbar_pay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        flag_unbilled = getIntent().getStringExtra("flag");
        context = PayDetailsActivity.this;
        init();
    }

    private void init() {
        if (getIntent().hasExtra("object")) {
            mruEntity = (MRUEntity) getIntent().getSerializableExtra("object");
            text_deleer_name = findViewById(R.id.text_deleer_name);
            text_acc_no = findViewById(R.id.text_acc_no);
            text_con_id = findViewById(R.id.text_con_id);
            text_meter_type = findViewById(R.id.text_meter_type);
            text_old_con_id = findViewById(R.id.text_old_con_id);
            edit_mobile2 = findViewById(R.id.edit_mobile2);
            text_meter_no = findViewById(R.id.text_meter_no);
            text_book_no = findViewById(R.id.text_book_no);
            text_address = findViewById(R.id.text_address);
            text_payable_amount = findViewById(R.id.text_payable_amount);
            edit_payable_amount = findViewById(R.id.edit_payable_amount);
            check_term = findViewById(R.id.check_term);
            text_deleer_name.setText("" + mruEntity.getCNAME());
            text_acc_no.setText("A/c- " + mruEntity.getACT_NO());
            text_con_id.setText("" + mruEntity.getCON_ID());
            text_meter_type.setText("" + mruEntity.getMETER_TYPE());
            text_old_con_id.setText("" + mruEntity.getOLD_CON_ID());
            if (!mruEntity.getMOBILE_NO().equals("NA"))
                edit_mobile2.setText("" + mruEntity.getMOBILE_NO());
            edit_mobile2.setEnabled(false);
            text_meter_no.setText("" + mruEntity.getMETER_NO());
            text_book_no.setText("" + mruEntity.getBOOK_NO());
            text_address.setText("" + mruEntity.getFA_HU_NAME() + " , " + mruEntity.getBILL_ADDR1());
            text_payable_amount.setText("" + mruEntity.getPAYBLE_AMOUNT());
        }
        button_pay = findViewById(R.id.button_pay);
        check_smt_balance = findViewById(R.id.check_smt_balance);
        check_smt_balance.setVisibility(View.GONE);
        UserInfo2 userInfo2 = CommonPref.getUserDetails(context);
        //condition for NBPDCL
        if (userInfo2.getUserID().startsWith("1") && mruEntity.getMETER_TYPE().equals("PREPAID")){
            check_smt_balance.setVisibility(View.VISIBLE);
            check_smt_balance.setOnClickListener(this);
        } else {
            check_smt_balance.setVisibility(View.GONE);
        }
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
            } else*/
            if (text_payable_amount.getText().toString().equals("Please connect to internet !")) {
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
        else if (v.getId() == R.id.check_smt_balance) {
            loadSmartMeterBalance(new SmartMeterBalanceRequest(mruEntity.getCON_ID().trim()));
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
        //&& mruEntity.getMETER_TYPE().equals("PREPAID")
        if(Utiilties.isOnline(PayDetailsActivity.this) && userInfo2.getUserID().startsWith("1")){
            loadSmartConsumerDetails(mruEntity.getCON_ID());
        }
        else if (Utiilties.isOnline(PayDetailsActivity.this)) {
            mruLoader = (MRULoader) new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
        } else {
            text_payable_amount.setText("Please connect to internet !");
            text_payable_amount.setTextColor(getResources().getColor(R.color.colorAccent));
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
                        if (jsonObject.has("accountBalance")) {
                            SharedPreferences prefs = getSharedPreferences(key,
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("WalletAmount", jsonObject.getString("accountBalance"));
                            editor.commit();
                            //balanceListner.balanceReceived(Double.parseDouble(jsonObject.getString("accountBalance")));
                            toolbar_pay.setSubtitle("Avl Balance(Rs.):" + jsonObject.getString("accountBalance") + "/-");
                        } else {
                            Toast.makeText(PayDetailsActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PayDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
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
        //if (mruEntity1.getMETER_TYPE().equals("PREPAID")) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.smat_bal_menu);
        item.setVisible(mruEntity.getMETER_TYPE().equals("PREPAID"));
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ref_menu: {
                //AlertDialogForPrinter();
                UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                if (Utiilties.isOnline(PayDetailsActivity.this)) {
                    if(userInfo2.getUserID().startsWith("1") && mruEntity.getMETER_TYPE().equals("PREPAID")){
                        loadSmartConsumerDetails(mruEntity.getCON_ID());
                    }
                    else{
                        mruLoader = (MRULoader) new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
                    }
                } else {
                    text_payable_amount.setText("Please connect to internet !");
                    text_payable_amount.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                break;
            }
            case R.id.smat_bal_menu: {
                loadSmartMeterBalance(new SmartMeterBalanceRequest(mruEntity.getCON_ID().trim()));
                break;
            }
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    private void loadSmartMeterBalance(SmartMeterBalanceRequest smartMeterBalanceRequest) {
        apiInterface = APIClient.getClient(com.bih.nic.e_wallet.retrofit.Urls_this_pro.RETROFIT_BASE_URL2).create(APIInterface.class);
        Call<SmartMeterDetail> call1 = apiInterface.getSmartMeterBalance(smartMeterBalanceRequest);
        progressDialog = new ProgressDialog(PayDetailsActivity.this);
        progressDialog.setMessage("Loading Smartmeter Balance....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        call1.enqueue(new Callback<SmartMeterDetail>() {
            @Override
            public void onResponse(Call<SmartMeterDetail> call, Response<SmartMeterDetail> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                SmartMeterDetail result = null;
                if (response.body() != null) result = response.body();
                AlertDialog.Builder builder = new AlertDialog.Builder(PayDetailsActivity.this);
                builder.setTitle( mruEntity.getCON_ID());
                if (result != null) {
                    builder.setMessage(Html.fromHtml("Hello "+mruEntity.getCNAME()+","+"\n" + "  " + "your Smart meter balance " + ((result.getMeterBalance()==null)?"not found .":"is <b style=\"color:Tomato;\">:"+result.getMeterBalance()) + "</b> " + ((result.getConnectionStatus() == 0) ? "You are connected." : (result.getConnectionStatus() == 1) ? "you are disconnected." : " .Found some <b style=\"color:Tomato;\">:error !.</b>")))
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                                // User cancels the dialog.
                                dialog.dismiss();
                            });
                } else {
                    builder.setMessage("Details not found !")
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                                // User cancels the dialog.
                                dialog.dismiss();});
                }
                // Create the AlertDialog object and return it.
                AlertDialog alert11 = builder.create();
                alert11.show();
            }

            @Override
            public void onFailure(Call<SmartMeterDetail> call, Throwable t) {
                Log.e("error", t.getMessage());
                t.printStackTrace();
                Toast.makeText(PayDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
                call.cancel();
            }
        });
    }

    private void loadSmartConsumerDetails(final String SmartConsumerId) {
        apiInterface = APIClient.getClient(com.bih.nic.e_wallet.retrofit.Urls_this_pro.RETROFIT_BASE_URL3).create(APIInterface.class);
        Call<SmartConsumerDetail> call1 = apiInterface.getRuralConsumerDetails(SmartConsumerId);
        progressDialog = new ProgressDialog(PayDetailsActivity.this);
        progressDialog.setMessage("Loading Consumer NB Consumer Details");
        progressDialog.setCancelable(false);
        progressDialog.show();
        call1.enqueue(new Callback<SmartConsumerDetail>() {
            @Override
            public void onResponse(Call<SmartConsumerDetail> call, Response<SmartConsumerDetail> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                SmartConsumerDetail result = null;
                if (response.body() != null) result = response.body();
                AlertDialog.Builder builder = new AlertDialog.Builder(PayDetailsActivity.this);
                builder.setTitle( mruEntity.getCON_ID());
                if (result != null) {
                    mruEntity = getMRUOBJFromSMCD(result);
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(context);
                    if (userInfo2.getUserID().startsWith("1") && mruEntity.getMETER_TYPE().equals("PREPAID")){
                        check_smt_balance.setVisibility(View.VISIBLE);
                        check_smt_balance.setOnClickListener(PayDetailsActivity.this);
                    } else {
                        check_smt_balance.setVisibility(View.GONE);
                    }
                    text_payable_amount.setText("" + mruEntity.getPAYBLE_AMOUNT());
                    text_payable_amount.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                    text_old_con_id.setText((mruEntity.getOLD_CON_ID()==null)?"N/A":mruEntity.getOLD_CON_ID());
                    text_meter_no.setText((mruEntity.getMETER_NO()==null)?"N/A":mruEntity.getMETER_NO());
                    text_address.setText("" + mruEntity.getBILL_ADDR1());
                    text_book_no.setText("" + mruEntity.getBOOK_NO());
                    /*if(mruEntity.getMOBILE_NO()==null){
                        edit_mobile2.setEnabled(true);
                        edit_mobile2.setText("");
                    }else {
                        edit_mobile2.setText("" + mruEntity.getMOBILE_NO());
                    }*/
                    edit_mobile2.setText("" + mruEntity.getMOBILE_NO());
                    text_meter_type = findViewById(R.id.text_meter_type);
                    mruEntity.setPAYBLE_AMOUNT(""+mruEntity.getPAYBLE_AMOUNT());
                    balance_loader = (BalanceLoader) new BalanceLoader(PayDetailsActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo());
                } else {
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                    mruLoader = (MRULoader) new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
                    /*builder.setMessage("Details not found !")
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                                // User cancels the dialog.
                                dialog.dismiss();
                            });*/
                }
                // Create the AlertDialog object and return it.
                //AlertDialog alert11 = builder.create();
                //alert11.show();
            }

            @Override
            public void onFailure(Call<SmartConsumerDetail> call, Throwable t) {
                UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                mruLoader = (MRULoader) new MRULoader().execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo() + "|NA|" + mruEntity.getCON_ID().trim() + "|NA");
                Log.e("error", t.getMessage());
                t.printStackTrace();
                Toast.makeText(PayDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Date date=null,newDate=null,currentDate=null;
        try {
            SimpleDateFormat formatter =  new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
            date = formatter.parse(smartConsumerDetail.getDueDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -10);  // Subtract 10 days
            newDate = calendar.getTime();
            currentDate=calendar.getTime();
            if (currentDate.equals(newDate) || currentDate.before(newDate)){
                mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getPromptAmt());
            }else if(currentDate.equals(date) || currentDate.before(date)){
                mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getGrossAmt());
            } else if (currentDate.after(date)) {
                mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getNetAmt());
            } else{
                mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getNetAmt());
            }
        }catch(Exception e){
            e.printStackTrace();
            mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getNetAmt());
        }
        //mruEntity2.setPAYBLE_AMOUNT(smartConsumerDetail.getGrossAmt());
        mruEntity2.setMOBILE_NO(smartConsumerDetail.getMobNo());
        mruEntity2.setMETER_NO(smartConsumerDetail.getMeterNo());
        mruEntity2.setTARIFF_ID(smartConsumerDetail.getCategory());
        mruEntity2.setSUB_DIV_ID(smartConsumerDetail.getSubDivId());
        return mruEntity2;
    }

    public void AlertDialogForCheckDetails(final MRUEntity mruEntity1) {
        final Dialog dialog = new Dialog(PayDetailsActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialog.setContentView(R.layout.pay_details_dialog);
        dialog.setTitle("Verify Details");
        TextView text_consumer_name = (TextView) dialog.findViewById(R.id.text_consumer_name);
        TextView text_address = (TextView) dialog.findViewById(R.id.text_address);
        TextView text_ac_no = (TextView) dialog.findViewById(R.id.text_ac_no);
        TextView text_con_id = (TextView) dialog.findViewById(R.id.text_con_id);
        TextView text_old_con_id = (TextView) dialog.findViewById(R.id.text_old_con_id);
        TextView text_book_no = (TextView) dialog.findViewById(R.id.text_book_no);
        TextView text_mobile = (TextView) dialog.findViewById(R.id.text_mobile);
        TextView text_meter_no = (TextView) dialog.findViewById(R.id.text_meter_no);
        TextView total_amount = (TextView) dialog.findViewById(R.id.total_amount);
        TextView text_amount_paid = (TextView) dialog.findViewById(R.id.text_amount_paid);
        final TextView check_con_pay = (TextView) dialog.findViewById(R.id.check_con_pay);
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
                if ((statement.getTransStatus().equals("TI") || statement.getTransStatus().equals("TP") || statement.getTransStatus().equals("TC")) && Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()), statement.getPayDate()) <= 15) {
                    Toast.makeText(PayDetailsActivity.this, "You already requested for this Consumer payment ! Please either syncronize or verify statement ! ", Toast.LENGTH_SHORT).show();
                    check_con_pay.setVisibility(View.VISIBLE);
                    check_con_pay.setText("You already requested for this Consumer payment ! Please either syncronize or verify statement ! You can request for another payment for this Consumer after " + (15 - Utiilties.getNoOfMinutes(Utiilties.convertStringToTimestampSlash(Utiilties.getCurrentDateWithTime()), statement.getPayDate())) + "minuts / total 15 minuts ! ");
                    check_con_pay.startAnimation(fade_in);
                } else if (mruEntity1.getMETER_TYPE().equals("PREPAID")) {
                    payForPrepaid(mruEntity1, check_con_pay, fade_in);
                } else {
                    if (mruEntity1.getCON_ID().startsWith("2")) {
                        pay(mruEntity1, check_con_pay, fade_in);
                    } else {
                        if (mruEntity1.getTARIFF_ID().equalsIgnoreCase("KJ")) {
                            pay_North_KJ(mruEntity1, check_con_pay, fade_in);
                        } else {
                            pay_North(mruEntity1, check_con_pay, fade_in);
                        }
                    }
                }
            } else {
                if (mruEntity1.getMETER_TYPE().equals("PREPAID")) {
                    payForPrepaid(mruEntity1, check_con_pay, fade_in);
                } else if (mruEntity1.getCON_ID().startsWith("2")) {
                    pay(mruEntity1, check_con_pay, fade_in);
                } else {
                    if (mruEntity1.getTARIFF_ID().equalsIgnoreCase("KJ")) {
                        pay_North_KJ(mruEntity1, check_con_pay, fade_in);
                    } else {
                        pay_North(mruEntity1, check_con_pay, fade_in);
                    }
                }
            }
        });

        dialog.show();
    }

    private void payForPrepaid(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        UserInfo2 userInfo2 = CommonPref.getUserDetails(context);
        double paying_amt = ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT()) * 5) > 10000) ? (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT()) * 5) : 10000;
        Log.e("paying_amt", String.valueOf(paying_amt));
        if (Double.parseDouble(edit_payable_amount.getText().toString()) <= 0) {
            check_con_pay.setVisibility(View.VISIBLE);
            check_con_pay.setText(" You can't pay Rs. " + edit_payable_amount.getText().toString());
            check_con_pay.startAnimation(fade_in);
        }
       /* else if (userInfo2.getUserID().startsWith("2") && Double.parseDouble(edit_payable_amount.getText().toString()) > paying_amt) {
            check_con_pay.setVisibility(View.VISIBLE);
            check_con_pay.setText(" You can't pay more than Rs. " + paying_amt);
            check_con_pay.startAnimation(fade_in);
        }*/
        else {
            navigationIntent(mruEntity1);
        }
    }

    private void pay(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 ");
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()));
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);

            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 ");
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 ");
                check_con_pay.startAnimation(fade_in);

            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < 50) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. 50 ");
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
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

    private void pay_North(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < 0) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + 0);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()));
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
    }

    private void pay_North_KJ(MRUEntity mruEntity1, final TextView check_con_pay, Animation fade_in) {
        int max = 200;
        int min = (int) (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 0.083);
        int compareval = 0;
        if (max > min) {
            compareval = min;
        } else {
            compareval = max;
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 0) {
            if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble("" + 0)) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + 0);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) >= 1 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 50) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble("" + compareval)) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + compareval);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 50 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 100) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 5);
                check_con_pay.startAnimation(fade_in);

            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble("" + compareval)) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + compareval);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 100 && Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) <= 1000) {
            if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3 < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 3);
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble("" + compareval)) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + compareval);
                check_con_pay.startAnimation(fade_in);
            } else {
                navigationIntent(mruEntity1);
            }
        }
        if (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) > 1000) {
            if ((Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5) < Double.parseDouble(edit_payable_amount.getText().toString())) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay more than Rs./- " + (Double.parseDouble(mruEntity1.getPAYBLE_AMOUNT().trim()) * 1.5));
                check_con_pay.startAnimation(fade_in);
            } else if (Double.parseDouble(edit_payable_amount.getText().toString()) < Double.parseDouble("" + compareval)) {
                check_con_pay.setVisibility(View.VISIBLE);
                check_con_pay.setText(" You can't pay less than Rs. " + compareval);
                check_con_pay.startAnimation(fade_in);
            } else {
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
        if (balance_loader != null) {
            if (balance_loader.getStatus() == AsyncTask.Status.RUNNING) {
                balance_loader.cancel(true);
            }
        }
        if (mruLoader != null) {
            if (mruLoader.getStatus() == AsyncTask.Status.RUNNING) {
                mruLoader.cancel(true);
            }
        }
        super.onDestroy();
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
                result = WebHandler.callByPostwithoutparameter(Urls_this_pro.DOWNLOAD_MRU_URL + reqString(String.valueOf(strings[0])));
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
                    mruEntity = mruEntities.get(0);
                    text_payable_amount.setText("" + mruEntities.get(0).getPAYBLE_AMOUNT());
                    text_payable_amount.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                    text_old_con_id.setText("" + mruEntities.get(0).getOLD_CON_ID());
                    text_meter_no.setText("" + mruEntities.get(0).getMETER_NO());
                    text_address.setText("" + mruEntities.get(0).getBILL_ADDR1());
                    edit_mobile2.setText("" + mruEntities.get(0).getMOBILE_NO());
                    text_meter_type = findViewById(R.id.text_meter_type);
                    mruEntity.setPAYBLE_AMOUNT(mruEntities.get(0).getPAYBLE_AMOUNT());
                    UserInfo2 userInfo2 = CommonPref.getUserDetails(PayDetailsActivity.this);
                    balance_loader = (BalanceLoader) new BalanceLoader(PayDetailsActivity.this).execute(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo());
                    //loadBalance(reqString(userInfo2.getUserID() + "|" + userInfo2.getPassword() + "|" + userInfo2.getImeiNo() + "|" + userInfo2.getImeiNo()));
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
}


