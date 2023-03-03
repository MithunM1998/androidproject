package com.example.attandanceapp_retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView mtextViewIn,mtextViewOut,mtextViewLastIn,mtextViewLastOut;
    model m=new model();
    Api jsonApi;
    Api2 jsonApi2;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    Date mDate;
    EditText mEditTextFromDate,mEditTextToDate,mReason;
    Button submit,next;


    private int mYearStart;
    private int mMonthStart;
    private int mDayStart;
    private String mFromDate;
    private String mToDate;
    private String reason;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtextViewIn=findViewById(R.id.activity_attendance_textview_in);
        mtextViewOut=findViewById(R.id.activity_attendance_textview_out);
        mtextViewLastIn=findViewById(R.id.lastIn);
        mtextViewLastOut=findViewById(R.id.lastOut);

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // dialog.setContentView(R.layout.dialog_box_apply_leave);

        mEditTextFromDate=findViewById(R.id.fromDate);
        mEditTextToDate=findViewById(R.id.toDate);
        mReason=findViewById(R.id.reason);
        submit=findViewById(R.id.submit);
        next=findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Upload.class);
                startActivity(intent);
            }
        });
        mDate= Calendar.getInstance().getTime();

        Retrofit retrofit=new Retrofit.Builder().baseUrl("http://trac.suveechi.com/suvetracm/api/").addConverterFactory(GsonConverterFactory.create()).build();

        jsonApi=retrofit.create(Api.class);
        jsonApi2=retrofit.create(Api2.class);
        mtextViewIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            attandanceIn();
            }


        });

        mtextViewOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attandanceOut();
            }
        });

        mEditTextFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = MainActivity.this.getCurrentFocus();
                if (view1 == null) {
                    view1 = new View(MainActivity.this);
                }
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                final Calendar calendarStart = Calendar.getInstance();
                mYearStart = calendarStart.get(Calendar.YEAR);
                mMonthStart = calendarStart.get(Calendar.MONTH);
                mDayStart = calendarStart.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mFromDate = year + "-" + String.format("%02d-%02d", (monthOfYear + 1), dayOfMonth);
                        mEditTextFromDate.setText(mFromDate);
                    }
                }, mYearStart, mMonthStart, mDayStart);
                // Subtract 6 days from Calendar updated date
                //calendarStart.add(Calendar.DATE, -6);
                // Set the Calendar new date as minimum date of date picker
                //dpd.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
                dpd.show();
            }
        });


        mEditTextToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = MainActivity.this.getCurrentFocus();
                if (view1 == null) {
                    view1 = new View(MainActivity.this);
                }
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                final Calendar calendarStart = Calendar.getInstance();
                mYearStart = calendarStart.get(Calendar.YEAR);
                mMonthStart = calendarStart.get(Calendar.MONTH);
                mDayStart = calendarStart.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mToDate = year + "-" + String.format("%02d-%02d", (monthOfYear + 1), dayOfMonth);
                        mEditTextToDate.setText(mToDate);
                    }
                }, mYearStart, mMonthStart, mDayStart);
                // Subtract 6 days from Calendar updated date
                //calendarStart.add(Calendar.DATE, -6);
                // Set the Calendar new date as minimum date of date picker
                //dpd.getDatePicker().setMinDate(calendarStart.getTimeInMillis());
                dpd.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFromDate = mEditTextFromDate.getText().toString().trim();
                mToDate = mEditTextToDate.getText().toString().trim();
                reason = mReason.getText().toString().trim();

                if (mFromDate.isEmpty() || mToDate.isEmpty() || reason.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the details...", Toast.LENGTH_SHORT).show();
                }else{
                    Call<model> call2=jsonApi2.createPost(m);

                    call2.enqueue(new Callback<model>() {
                        @Override
                        public void onResponse(Call<model> call, Response<model> response) {
                            if (response.isSuccessful()){
                                Log.d("json2", String.valueOf(response));
                                Toast.makeText(MainActivity.this, "fromdate"+mFromDate+"  "+"toDate"+mToDate+" "+"Leave applied successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<model> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void attandanceIn() {
      /*  String status=m.getStatus();
        String msg=m.getMsg();
        Log.d("st",status);
        Log.d("ms",msg);*/
        Call<model> call=jsonApi.createPost(m);

        call.enqueue(new Callback<model>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<model> call, Response<model> response) {
                if (response.isSuccessful()){
                    Log.d("json", String.valueOf(response));

                   // try {
                     //   JSONObject jsonObject = new JSONObject(response);
                       // if (jsonObject.getString("status").equals("yes")){
                            final int color = 0x88EAEAEA;
                            final Drawable drawable = new ColorDrawable(color);

                            mtextViewIn.setEnabled(false);
                            mtextViewOut.setEnabled(true);
                            mtextViewLastIn.setText("Last In Time: " + mDateFormat.format(mDate));

                            Toast.makeText(MainActivity.this, "Attandance updated successfully", Toast.LENGTH_SHORT).show();
                        //}
                   // }catch (JSONException e){
                     //   e.printStackTrace();
                    //}
                }
            }

            @Override
            public void onFailure(Call<model> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attandanceOut(){
        Call<model> call=jsonApi.createPost(m);

        call.enqueue(new Callback<model>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<model> call, Response<model> response) {
                if (response.isSuccessful()){
                    Log.d("json", String.valueOf(response));

                    // try {
                    //   JSONObject jsonObject = new JSONObject(response);
                    // if (jsonObject.getString("status").equals("yes")){
                    final int color = 0x88EAEAEA;
                    final Drawable drawable = new ColorDrawable(color);

                    mtextViewOut.setEnabled(false);
                    mtextViewIn.setEnabled(true);
                    mtextViewLastOut.setText("Last Out Time: " + mDateFormat.format(mDate));

                    Toast.makeText(MainActivity.this, "Attandance updated successfully", Toast.LENGTH_SHORT).show();

                   // Log.d("out","Mess"+m.getStatus());
                    //}
                    // }catch (JSONException e){
                    //   e.printStackTrace();
                    //}
                }
            }

            @Override
            public void onFailure(Call<model> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
}