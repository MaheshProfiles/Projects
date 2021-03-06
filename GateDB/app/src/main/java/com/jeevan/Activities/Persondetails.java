package com.jeevan.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeevan.gatedb.R;

public class Persondetails extends AppCompatActivity {
    ImageView imageView;
    TextView name, email, mobile, about;
    ImageButton call, msg;
    private Context mcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persondetails);
        imageView = (ImageView) findViewById(R.id.prfl);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        mobile = (TextView) findViewById(R.id.mobile);
        about = (TextView) findViewById(R.id.description);
        imageView.setImageResource(getIntent().getIntExtra("img_id", 00));
        name.setText("Name:" + getIntent().getStringExtra("Name"));
        email.setText("Email:" + getIntent().getStringExtra("Email"));
        mobile.setText("Mobile:" + getIntent().getStringExtra("Mobile"));
        about.setText("About:" + getIntent().getStringExtra("About"));
        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+919738514840"));
                    if (ActivityCompat.checkSelfPermission(mcon, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {// TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        return;
                    }
                    mcon.startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(mcon, "Your call has failed...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msgIntent = new Intent(Intent.ACTION_VIEW);
                msgIntent.putExtra("sms_body", "default content");
                msgIntent.setType("vnd.android-dir/mms-sms");
                msgIntent.putExtra("address", new String("9738514840"));
                msgIntent.putExtra("sms_body", "Test ");
                mcon.startActivity(msgIntent);
            }

        });
        // Details();
    }



    // public void Details() {

    // final String[] names = {"Mahesh", "Swetha", "Honey", "Jeevan", "Ravi", "Hemanth", "Sreeram"};
    //final int[] pid = {R.drawable.carpenter1, R.drawable.carpenter1, R.drawable.carpenter1, R.drawable.carpenter1, R.drawable.carpenter1, R.drawable.carpenter1, R.drawable.carpenter1};
    // final String[] mobilenum = {"9738514840", "9035113859", "9963435655", "9912027195", "9912027195", "8123461173", "77431592"};
    // final String[] emails = {"Mahesh123@gmail.com", "Swetha77@gmail.com", "Honey143@gmail.com", "Jeevan808@gmail.com", "Ravi867@gmail.com", "Hemanth436@gmail.com", "Sreeram456@gmail.com"};
    //final String[] descp = {"Mahesh", "Swetha", "Honey", "Jeevan", "Ravi", "Hemanth", "Sreeram"};
    // for (int i = 0; i <= names.length; i++) {
    //     name.setText(names[i]);
    //   email.setText(emails[i]);
    //   mobile.setText(mobilenum[i]);
    // about.setText(descp[i]);
    //  imageView.setImageResource(pid[i]);


}
//}


//}

