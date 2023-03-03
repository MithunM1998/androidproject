package com.example.attandanceapp_retrofit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Upload extends AppCompatActivity {

    String path;
    private View mView;

    private ImageView mImageViewCamera;
    private ImageView mImageViewGallery;
    private LayoutInflater mInflater;
    private LinearLayout mLayoutMain;
    ViewGroup mParent;
    private Button mButtonUpload;
    private TextView mTextViewSuccess;
    UploadApi jsonApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mImageViewCamera = (ImageView) findViewById(R.id.activity_location_snapshot_imageview_camera);
        mImageViewGallery = (ImageView) findViewById(R.id.activity_location_snapshot_imageview_gallery);

        mLayoutMain = (LinearLayout) findViewById(R.id.activity_location_snapshot_main_layout);
        mInflater = LayoutInflater.from(Upload.this);

        clickListeners();
    }

    private void clickListeners() {
        mImageViewGallery.setOnClickListener(v->{
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);
            } else {
                ActivityCompat.requestPermissions(Upload.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },1);
            }
        });

        mImageViewCamera.setOnClickListener(v->{
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // intent.addCategory(Intent.CATEGORY_OPENABLE);

                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            } else {
                ActivityCompat.requestPermissions(Upload.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Context context = Upload.this;
            path = RealPathUtil.getRealPath(context, uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
           // binding.imageview.setImageBitmap(bitmap);

            mView = mInflater.inflate(R.layout.uploadsuccess,null);
            mParent = (ViewGroup) mLayoutMain.getParent();
            mParent.removeView(mLayoutMain);
            mParent.addView(mView);

            ImageView img = (ImageView) mView.findViewById(R.id.activity_upload_success_imageview);
            mButtonUpload = (Button) mView.findViewById(R.id.activity_upload_success_button_upload);
            Button btnBack = (Button) mView.findViewById(R.id.activity_upload_success_button_back);
            mTextViewSuccess = (TextView) mView.findViewById(R.id.activity_upload_success_textview_success);

            img.setImageBitmap(bitmap);

            mButtonUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://trac.suveechi.com/suvetracm/api/")
                            .addConverterFactory(GsonConverterFactory.create()).build();

                    File file = new File(path);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    MultipartBody.Part body = MultipartBody.Part.createFormData("", file.getName(), requestFile);

                    jsonApi=retrofit.create(UploadApi.class);
                    Call<model> call2=jsonApi.addCustomer(body);

                    call2.enqueue(new Callback<model>() {
                        @Override
                        public void onResponse(Call<model> call, Response<model> response) {

                            if (response.isSuccessful()) {

                                Log.d("json3", String.valueOf(response));
                                    LinearLayout successLayout = (LinearLayout) mView.findViewById(R.id.activity_upload_success_layout);
                                    successLayout.setVisibility(View.VISIBLE);
                                    mButtonUpload.setVisibility(View.GONE);
                                    mTextViewSuccess.setText("Upload Successful");



                                } else {
                                    Toast.makeText(getApplicationContext(), "not Added", Toast.LENGTH_SHORT).show();
                                }
                            }


                        @Override
                        public void onFailure(Call<model> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParent.removeView(mView);
                    mParent.addView(mLayoutMain);
                    mView = null;
                }
            });

        }else{
            if (requestCode==2){
                Bundle bundle=data.getExtras();
                Bitmap imageBitmap=(Bitmap) bundle.get("data");

                mView = mInflater.inflate(R.layout.uploadsuccess,null);
                mParent = (ViewGroup) mLayoutMain.getParent();
                mParent.removeView(mLayoutMain);
                mParent.addView(mView);

                ImageView img = (ImageView) mView.findViewById(R.id.activity_upload_success_imageview);
                mButtonUpload = (Button) mView.findViewById(R.id.activity_upload_success_button_upload);
                Button btnBack = (Button) mView.findViewById(R.id.activity_upload_success_button_back);
                mTextViewSuccess = (TextView) mView.findViewById(R.id.activity_upload_success_textview_success);

                img.setImageBitmap(imageBitmap);
                mButtonUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://trac.suveechi.com/suvetracm/api/")
                                .addConverterFactory(GsonConverterFactory.create()).build();

                        File file = new File(path);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                        MultipartBody.Part body = MultipartBody.Part.createFormData("", file.getName(), requestFile);

                        jsonApi=retrofit.create(UploadApi.class);
                        Call<model> call2=jsonApi.addCustomer(body);

                        call2.enqueue(new Callback<model>() {
                            @Override
                            public void onResponse(Call<model> call, Response<model> response) {

                                if (response.isSuccessful()) {

                                    Log.d("json3", String.valueOf(response));
                                    LinearLayout successLayout = (LinearLayout) mView.findViewById(R.id.activity_upload_success_layout);
                                    successLayout.setVisibility(View.VISIBLE);
                                    mButtonUpload.setVisibility(View.GONE);
                                    mTextViewSuccess.setText("Upload Successful");



                                } else {
                                    Toast.makeText(getApplicationContext(), "not Added", Toast.LENGTH_SHORT).show();
                                }
                            }


                            @Override
                            public void onFailure(Call<model> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParent.removeView(mView);
                        mParent.addView(mLayoutMain);
                        mView = null;
                    }
                });

            }
        }
    }
}