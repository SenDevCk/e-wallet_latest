package com.bih.nic.e_wallet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bih.nic.e_wallet.R;
import com.bih.nic.e_wallet.asynkTask.LoginLoader;
import com.bih.nic.e_wallet.asynkTask.SmsVerificationService;
import com.bih.nic.e_wallet.dataBaseHandler.DataBaseHelper;
import com.bih.nic.e_wallet.entity.UserInfo2;
import com.bih.nic.e_wallet.retrofit.APIClient;
import com.bih.nic.e_wallet.retrofit.APIInterface;
import com.bih.nic.e_wallet.retrofit.Urls_this_pro;
import com.bih.nic.e_wallet.smsRecever.SmsListener;
import com.bih.nic.e_wallet.smsRecever.SmsReceiver;
import com.bih.nic.e_wallet.utilitties.CommonPref;
import com.bih.nic.e_wallet.utilitties.GlobalVariables;
import com.bih.nic.e_wallet.utilitties.MarshmallowPermission;
import com.bih.nic.e_wallet.utilitties.Utiilties;
import com.mukesh.OtpView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 10;
    EditText edit_user_name, edit_pass;
    Button button_login;
    ToggleButton toggleButton;
    String version = "";
    TelephonyManager tm;
    private static String imei = "";
    MarshmallowPermission MARSHMALLOW_PERMISSION;
    String serial_id = "";
    TextView text_ver1, text_head;
    SmsReceiver smsReceiver;
    IntentFilter filter;
    private boolean initse;
    private ProgressDialog dialog1;
    private SmsVerificationService smsVerificationService;
    private APIInterface apiInterface;
    ProgressDialog progressDialog;
    private AlertDialog alertDialog = null;
    OtpView otp_view;
    CountDownTimer countDownTimer;

    int counter=0;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        DataBaseHelper db = new DataBaseHelper(this);
        try {
            db.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            db.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Call some material design APIs here
            if (checkAndRequestPermissions()) {
                //init2();
                if (!initse) init();
            }

        } else {
            if (!initse) init();
        }


    }

    private void init() {
        dialog1 = new ProgressDialog(LoginActivity.this);
        text_head = (TextView) findViewById(R.id.text_head);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/header_font.ttf");
        text_head.setTypeface(face);
        edit_user_name = (EditText) findViewById(R.id.edit_user_name);
        edit_pass = (EditText) findViewById(R.id.edit_pass);
        text_ver1 = (TextView) findViewById(R.id.text_ver);
        toggleButton = (ToggleButton) findViewById(R.id.watch_pass);
        button_login =  findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edit_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edit_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        if (CommonPref.getUserDetails(LoginActivity.this) != null) {
            if (!CommonPref.getUserDetails(LoginActivity.this).getUserID().trim().equals("")) {
                edit_user_name.setText("" + CommonPref.getUserDetails(LoginActivity.this).getUserID());
            }
        }
        initse = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        readPhoneState();
    }

    @Override
    public void onClick(View v) {
        button_login.setEnabled(false);
        if(Debug.isDebuggerConnected()){
            Toast.makeText(this, "Debugging not allowed", Toast.LENGTH_SHORT).show();
        }
        else if (edit_user_name.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter User Name", Toast.LENGTH_SHORT).show();
        } else if (edit_pass.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {
            filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            smsReceiver = new SmsReceiver();
            registerReceiver(smsReceiver, filter);
            SmsReceiver.bindListener(messageText -> {
                // text_resend.setVisibility(View.GONE);
                Log.d("activity", "" + messageText);
                dialog1.dismiss();
                //if(smsVerificationService!=null && smsVerificationService.getStatus()!=SmsVerificationService.Status.RUNNING) {
                if(!messageText.isEmpty()) {
                        //smsVerification(reqString(edit_user_name.getText().toString().trim() + "|" + imei.trim() + "|" + messageText.split(" ")[0].trim()));
                        smsVerificationService = (SmsVerificationService) new SmsVerificationService(LoginActivity.this, imei, serial_id).execute(edit_user_name.getText().toString().trim() + "|" + imei.trim() + "|" + messageText.split(" ")[0].trim());
                }else {
                    System.err.println("SmsReceiver.bindListener Empty message received !");
                    Toast.makeText(this, "Empty message received !"+messageText, Toast.LENGTH_SHORT).show();
                }
                //}
            });
            //new LoginLoader(LoginActivity.this,imei,serial_id).execute(edit_user_name.getText().toString() + "|" + edit_pass.getText().toString() + "|" + imei + "|" + serial_id);
           if (counter==0) {
               counter++;
               loginUsingRetrofit(reqString(edit_user_name.getText().toString() + "|" + edit_pass.getText().toString() + "|" + imei + "|" + serial_id));
           }
        }

    }



    private void loginUsingRetrofit(String reqString) {
        apiInterface = APIClient.getClient(Urls_this_pro.RETROFIT_BASE_URL).create(APIInterface.class);
        Call<UserInfo2> call1 = apiInterface.doLogin(reqString);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        call1.enqueue(new Callback<UserInfo2>() {
            @Override
            public void onResponse(Call<UserInfo2> call, Response<UserInfo2> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                UserInfo2 result = null;
                if (response.body() != null) result = response.body();
                if (result == null) {
                    button_login.setEnabled(true);
                    alertDialog.setTitle("Login Unsuccessful");
                    alertDialog.setMessage("Try after some time !");
                    alertDialog.setButton("OK", (dialog, which) -> {
                        //edit_user_name.setFocusable(true);
                    });
                    alertDialog.show();
                } else if (result != null && !result.getAuthenticated()) {
                    CommonPref.setUserDetails(LoginActivity.this, result);
                    if (result.getMessageString().contains("ENTRY")) {
                        AlertDialogForCheckDetails(true);
                    } else if (result.getMessageString().contains("AUTO")) {
                        //AlertDialogForCheckDetails(false);
//                        dialog1.setCancelable(false);
//                        dialog1.setMessage("Waiting for SMS...");
//                        dialog1.show();
                        countDownTimer = new CountDownTimer(30000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                long seconds=millisUntilFinished / 1000;
                                dialog1.setCanceledOnTouchOutside(false);
                                dialog1.setMessage("Waiting For Otp for 30 seconds... remaining"+(seconds));
                                dialog1.show();
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                dialog1.dismiss();
                                AlertDialogForOTP();
                            }

                        }.start();
                    } else {
                        String version1 = "App Version : " + version + " ( " + imei + " )";
                        ((TextView) findViewById(R.id.text_ver)).setText(version1);
                        alertDialog.setTitle("Login Unsuccessful");
                        alertDialog.setMessage("" + result.getMessageString());
                        alertDialog.setButton("OK", (dialog, which) -> edit_user_name.setFocusable(true));
                        alertDialog.show();
                    }
                } else {
                    Intent cPannel = new Intent(LoginActivity.this, MainActivity.class);
                    if (Utiilties.isOnline(LoginActivity.this)) {
                        if (result != null) {
                            if (imei.equalsIgnoreCase(result.getImeiNo())) {
                                try {
                                    result.setPassword(edit_pass.getText().toString());
                                    GlobalVariables.LoggedUser = result;
                                    CommonPref.setUserDetails(LoginActivity.this, result);
                                    startActivity(cPannel);
                                    finish();
                                } catch (Exception ex) {
                                    button_login.setEnabled(true);
                                    ex.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "Login failed due to Some Error !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                button_login.setEnabled(true);
                                String version1 = "App Version : " + version + " ( " + imei + " )";
                                ((TextView) findViewById(R.id.text_ver)).setText(version1);
                                alertDialog.setTitle("Device Not Registered");
                                alertDialog.setMessage("" + result.getMessageString());
                                alertDialog.setButton("OK", (dialog, which) -> edit_user_name.setFocusable(true));
                                alertDialog.show();
                            }
                        }
                    } else {
                        if (CommonPref.getUserDetails(LoginActivity.this) != null) {
                            GlobalVariables.LoggedUser = result;
                            if (GlobalVariables.LoggedUser.getUserID().equalsIgnoreCase(edit_user_name.getText().toString().trim()) && GlobalVariables.LoggedUser.getPassword().equalsIgnoreCase(edit_pass.getText().toString().trim())) {
                                startActivity(cPannel);
                                finish();

                            } else {
                                button_login.setEnabled(true);
                                Toast.makeText(LoginActivity.this, "User name and password not matched !", Toast.LENGTH_LONG).show();
                                String version1 = "App Version : " + version + " ( " + imei + " )";
                                ((TextView) findViewById(R.id.text_ver)).setText(version1);
                            }
                        } else {
                            button_login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Please enable internet connection for first time login.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfo2> call, Throwable t) {
                button_login.setEnabled(true);
                Log.e("error", t.getMessage());
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
                call.cancel();
            }
        });
    }

    public void AlertDialogForCheckDetails(boolean flag) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialog.setContentView(R.layout.otp_dialog);
        dialog.setTitle("Enter PIN");
        otp_view = (OtpView) dialog.findViewById(R.id.otp_view);
        otp_view.setEnabled(flag);
        Button verify_pay =  dialog.findViewById(R.id.verify);
        Button go_to_home =  dialog.findViewById(R.id.go_to_home);
        // if button is clicked, close the custom dialog
        go_to_home.setOnClickListener(v -> dialog.dismiss());
        verify_pay.setOnClickListener(v -> {
            dialog.dismiss();
            //UserInfo2 userInfo2 = CommonPref.getUserDetails(LoginActivity.this);
            //new SmsVerificationService(LoginActivity.this, imei, serial_id).execute(edit_user_name.getText().toString().trim() + "|" + imei.trim() + "|" + otp_view.getOTP());
            smsVerification(reqString(edit_user_name.getText().toString().trim() + "|" + imei.trim() + "|" + otp_view.getOTP()));
        });
        dialog.show();
    }

    private void smsVerification(String reqString) {
        apiInterface = APIClient.getClient(Urls_this_pro.RETROFIT_BASE_URL).create(APIInterface.class);
        Call<String> call1 = apiInterface.firstOtp(reqString);
        progressDialog = new ProgressDialog(LoginActivity.this);
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
                    Log.e("res", "res varification :" + result);
                    if (result.contains("SUCCESS")) {
                        new LoginLoader(LoginActivity.this, imei, serial_id).execute(edit_user_name.getText().toString() + "|" + edit_pass.getText().toString() + "|" + imei + "|" + serial_id);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Server Problem", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error", t.getMessage());
                Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing()) progressDialog.dismiss();
                call.cancel();
            }
        });
    }


    @SuppressLint({"HardwareIds"})
    public void readPhoneState() {
        MARSHMALLOW_PERMISSION = new MarshmallowPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE);
        if (MARSHMALLOW_PERMISSION.result == -1 || MARSHMALLOW_PERMISSION.result == 0) {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    assert tm != null;
                    imei = tm.getDeviceId();
                } else {
                    //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) ;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    assert tm != null;
                    imei = tm.getDeviceId();
                } else {
                    //imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    imei = Utiilties.getIMEI_forAndroid10(LoginActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                serial_id = Build.getSerial();
            } else {
                // serial_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                serial_id = imei;
            }
        } else {
            serial_id = Build.SERIAL;
        }
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.e("App Version : ", "" + version + " ( " + imei + "/" + serial_id + " )");
            String version1 = "App Version : " + version ;
            ((TextView) findViewById(R.id.text_ver)).setText(version1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String reqString(String req_string) {
        byte[] chipperdata = rsaEncrypt(req_string.getBytes());
        Log.e("chiperdata", new String(chipperdata));
        String encString = Base64.encodeToString(chipperdata, Base64.NO_WRAP);
        Log.e("string", "" + encString);
        encString = encString.replaceAll("\\/", "SSLASH").replaceAll("\\=", "EEQUAL").replaceAll("\\+", "PPLUS");
        Log.e("string", "" + encString);
        return encString;
    }

    byte[] rsaEncrypt(byte[] data) {
        try {
            //getKeyPath();
            //PublicKey pubKey = readKeyFromFile();
            PublicKey pubKey = readKeyFromFile();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] cipherData = cipher.doFinal(data);
            return cipherData;
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    PublicKey readKeyFromFile() throws IOException {
        ObjectInputStream oin = null;
        try (InputStream in = getKeyPath();) {
            oin = new ObjectInputStream(new BufferedInputStream(in));
            PublicKey pubKey = (PublicKey) oin.readObject();
            return pubKey;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    public InputStream getKeyPath() {
        InputStream app;
        try {
            app = getApplicationContext().getAssets().open("public.key");

        } catch (Exception ex) {
            System.out.println(ex);
            app = null;
        }
        return app;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean checkAndRequestPermissions() {
        int read_media;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        }else {
            read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        int storage2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int couselocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int read_sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int receve_sms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetooth_scan = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        int bluetooth_Connect = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (read_sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (bluetooth_scan != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (bluetooth_Connect != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (bluetooth != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        if (read_media != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)?Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (couselocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (receve_sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!initse) init();
                } else {
                    if (!initse) init();
                }
                break;
        }
    }

    public void AlertDialogForOTP() {
        final Dialog dialogOtp = new Dialog(LoginActivity.this);
        /*	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before*/
        dialogOtp.setContentView(R.layout.otp_dialog);
        // set the custom dialogOtp components - text, image and button
        //final EditText otpEdit = (EditText) dialogOtp.findViewById(R.id.enter_otp);
        final EditText otp_view = (EditText) dialogOtp.findViewById(R.id.otp_view);
        final Button button_submit = (Button) dialogOtp.findViewById(R.id.button_submit);
        final TextView text_timer = (TextView) dialogOtp.findViewById(R.id.text_timer);
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secs= millisUntilFinished / 1000;
                text_timer.setVisibility(View.VISIBLE);
                text_timer.setText(Html.fromHtml("<b style=\"color:White;\">" + (secs) + "</b> "));
                text_timer.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                text_timer.setVisibility(View.GONE);
                dialogOtp.dismiss();
            }

        }.start();
        // if button is clicked, close the custom dialogOtp
        SmsReceiver.bindListener(messageText -> {
            //Log.d("activity",""+messageText);
            otp_view.setText(messageText.split(" ")[0].trim());
        });
        button_submit.setOnClickListener(v -> {
            if (otp_view.getText().length() < 6) {
                Toast.makeText(LoginActivity.this, "Enter Valid OTP !", Toast.LENGTH_SHORT).show();
            } else {
                button_submit.setClickable(false);
                dialogOtp.dismiss();
                new SmsVerificationService(LoginActivity.this, imei, imei).execute(otp_view.getText().toString().trim());
            }

        });
        dialogOtp.show();
    }
}



