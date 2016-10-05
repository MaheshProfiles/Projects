package com.jeevan.gatedb;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.jeevan.GateModel.ProfileMaster;
import com.jeevan.database.DBHelper;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    public Button button;
    public TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutmobileno, inputLayoutcity, inputLayoutstate, inputLayoutarea, inputLayoutoffered, inputLayoutrequired, inputLayoutPassword, inputLayoutconfirmpassword;
    public EditText inputName, inputEmail, inputmobileno, inputPassword, inputcity, inputstate, inputarea, inputoffered, inputrequired, inputconfirm;

    public DBHelper db;
    public Spinner spinner1, spinner2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //spinner for services offered
        spinner1 = (Spinner) findViewById(R.id.services_offered);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.services_offered, android.R.layout.simple_spinner_item);
        spinner1.setAdapter(adapter);

        //spinner for services required
        spinner2 = (Spinner) findViewById(R.id.services_required);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.services_required, android.R.layout.simple_spinner_item);
        spinner2.setAdapter(adapter1);

        db = new DBHelper(ProfileActivity.this);

        //layouts for edit texts
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputEmail.setEnabled(false);
        inputEmail.setText(getIntent().getStringExtra("email"));
        Intent i = getIntent();
        i.getStringExtra("email");
        inputmobileno = (EditText) findViewById(R.id.input_mob);
        inputmobileno.setEnabled(false);
        inputmobileno.setText(getIntent().getStringExtra("mobile"));
        Intent i2 = getIntent();
        i2.getStringExtra("mobile");
        inputLayoutcity = (TextInputLayout) findViewById(R.id.input_layout_city);
        inputLayoutstate = (TextInputLayout) findViewById(R.id.input_layout_state);
        inputLayoutarea = (TextInputLayout) findViewById(R.id.input_layout_area);
//        inputLayoutoffered = (TextInputLayout) findViewById(R.id.input_layout_offered);
//        inputLayoutrequired = (TextInputLayout) findViewById(R.id.input_layout_req);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutconfirmpassword = (TextInputLayout) findViewById(R.id.input_layout_confirmPassword);

        //ids for edit texts
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputmobileno = (EditText) findViewById(R.id.input_mob);
        inputcity = (EditText) findViewById(R.id.input_city);
        inputstate = (EditText) findViewById(R.id.input_state);
        inputarea = (EditText) findViewById(R.id.input_area);
//        inputoffered = (EditText) findViewById(R.id.input_offered);
//        inputrequired = (EditText) findViewById(R.id.input_req);
        inputPassword = (EditText) findViewById(R.id.password);
        inputconfirm = (EditText) findViewById(R.id.confirm_password);

        button = (Button) findViewById(R.id.btn_sign_up);
        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View view) {
                if (inputName.getText().toString().isEmpty() && inputEmail.getText().toString().isEmpty()
                        && inputmobileno.getText().toString().isEmpty() && inputcity.getText().toString().isEmpty()
                        && inputstate.getText().toString().isEmpty() && inputarea.getText().toString().isEmpty()
                        && inputPassword.getText().toString().isEmpty() && inputconfirm.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Fields Cannot be empty", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (inputPassword.getText().toString().length() < 8
                        || (inputconfirm.getText().toString().length() < 8)) {
                    Snackbar.make(view, "Password must be at-least 6 characters", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (!inputPassword.getText().toString().equals(inputconfirm.getText().toString())) {
                        Snackbar.make(view, "Passwords Doesn't Match", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        ProfileMaster master = new ProfileMaster();
                        master.setUsername(inputName.getText().toString());
                        master.setEmail(inputEmail.getText().toString());
                        master.setMobile(inputmobileno.getText().toString());
                        master.setCity(inputcity.getText().toString());
                        master.setState(inputstate.getText().toString());
                        master.setArea(inputarea.getText().toString());
                        master.setOffered(spinner1.getSelectedItem().toString());
                        master.setRequired(spinner2.getSelectedItem().toString());
                        master.setPassword(inputconfirm.getText().toString());
//                        master.setConfirmpassword(inputconfirm.getText().toString());
                        master.setCreatedAt(new Date());
                        Log.e("values", master + "");
                        db.createProfileUser(master);

                        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setCancelable(false);
        builder.setMessage("your registration process is not completed Do u want to Exit ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent i=new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}



