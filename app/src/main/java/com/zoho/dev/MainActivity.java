package com.zoho.dev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zoho.crm.sdk.android.zcrmandroid.activity.ZCRMBaseActivity;

public class MainActivity extends ZCRMBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Zoho CRM");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.signout:
                new AlertDialog.Builder(this)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
                return true;
            default:
                return false;
        }
    }

    public void logout()
    {
        super.logout(new OnLogoutListener() {
            @Override
            public void onLogoutSuccess() {
                System.out.println(">> Logout Success");
            }

            @Override
            public void onLogoutFailed() {
                System.out.println(">> Logout Failed");
            }
        });
    }

    public void getContacts(View view) throws IllegalAccessException, InstantiationException {
        //Intent openContactsMenu = new Intent(context.getApplicationContext(), ContactsActivity.class);
        //context.startActivity(openContactsMenu);
        startActivity(new Intent(getApplicationContext(), ContactsActivity.class));
    }
}
