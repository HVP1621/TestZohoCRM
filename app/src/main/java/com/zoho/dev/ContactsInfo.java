package com.zoho.dev;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoho.crm.library.crud.ZCRMField;
import com.zoho.crm.library.crud.ZCRMLayout;
import com.zoho.crm.library.crud.ZCRMModule;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.crud.ZCRMSection;
import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;
import com.zoho.crm.sdk.android.zcrmandroid.activity.ZCRMBaseActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactsInfo extends ZCRMBaseActivity {
    ProgressBar mProgress;
    SwipeRefreshLayout refreshLayout;
    GridLayout layout;
    private ZCRMRecord zcrmRecord;
    private ZCRMLayout zcrmLayout;
    ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.contactsinfo_base);
        getSupportActionBar().setTitle("Siddharth");
        setTable();
    }

    public void setTable() {
        this.layout = findViewById(R.id.grid_layout);
        this.layout.setRowCount(100);
        this.layout.setColumnCount(50);

        refreshLayout = findViewById(R.id.modulerefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mProgress = findViewById(R.id.moduleprogress);
        mProgress.setVisibility(ProgressBar.VISIBLE);

        dialog = ProgressDialog.show(ContactsInfo.this, "",
                "Loading. Please wait...", true); //No I18N

        APImodeRunner runner = new APImodeRunner();
        runner.execute();
    }

    @SuppressLint("NewApi")
    public void loadTable() throws JSONException, ZCRMException {
        System.out.println(">> record : "+zcrmRecord.getEntityId());
        System.out.println(">> layout : "+zcrmLayout.getId());
        int row = 0,col = 0,count = 0;
        List<ZCRMSection> sections = zcrmLayout.getSections();
        List<ZCRMField> fields = new ArrayList<>();
        int id = 2000;

        System.out.println(">> section count : "+sections.size());
        Iterator itr = sections.iterator();
        while (itr.hasNext())
        {
            ZCRMSection zcrmSection = (ZCRMSection) itr.next();
            col = 0;
            TextView SectionText = setSection(zcrmSection.getName(),id,count,col,row);
            this.layout.addView(SectionText);
            row++;
            count++;

            fields = zcrmSection.getAllFields();
            Iterator itr_arr = fields.iterator();

            while(itr_arr.hasNext())
            {
                ZCRMField field = (ZCRMField)itr_arr.next();

                col = 1;
                TextView textView = setFieldName(field.getDisplayName(), id, count, col, row);
                textView.setTextColor(getColor(R.color.colorPrimary));
                this.layout.addView(textView);

                count++;

                col = 20;
                String field_value = getValue(field.getApiName());
                TextView textview2 = setFieldValue(field_value, id, count, col, row);
                this.layout.addView(textview2);

                row = row + 2;
                count++;

            }
        }

        dialog.dismiss();
        refreshLayout.setRefreshing(false);
        mProgress.setVisibility(ProgressBar.INVISIBLE);
    }

    private String getValue(String fieldAPIname) throws JSONException, ZCRMException {

        if(fieldAPIname.equals("Owner"))
        {
            return zcrmRecord.getOwner().getFullName();
        }
        else if(fieldAPIname.equals("Created_by"))
        {
            return zcrmRecord.getCreatedBy().getFullName();
        }
        else if (fieldAPIname.equals("Modified_By"))
        {
            return zcrmRecord.getModifiedBy().getFullName();
        }
        else if (fieldAPIname.equals("Modified_Time"))
        {
            return zcrmRecord.getModifiedTime();
        }
        else if(fieldAPIname.equals("Layout"))
        {
            return zcrmLayout.getName();
        }else if(!zcrmRecord.getData().containsKey(fieldAPIname))
        {
            return "";
        }
        else if(zcrmRecord.getFieldValue(fieldAPIname) instanceof ZCRMRecord)
        {
            ZCRMRecord record = (ZCRMRecord) zcrmRecord.getFieldValue(fieldAPIname);
            return record.getLookupLabel();
        }
        else if(zcrmRecord.getFieldValue(fieldAPIname) == null || "null".equals(String.valueOf(zcrmRecord.getFieldValue(fieldAPIname)))) {
            return "";
        }
        else {
            return String.valueOf(zcrmRecord.getFieldValue(fieldAPIname));
        }
    }

    private TextView setSection(String sectionName, int id, int count, int col, int row) {
        System.out.println(">> Section : "+sectionName);
        TextView Sectiontext = new TextView(this);
        Sectiontext.clearComposingText();
        Sectiontext.setText(null);
        Sectiontext.setText(sectionName);
        Sectiontext.setId(id + count);
        Sectiontext.setTextAppearance(this, android.R.style.TextAppearance_Large);
        GridLayout.Spec rowspan3 = GridLayout.spec(row, 1);
        GridLayout.Spec colspan3 = GridLayout.spec(col, 25);
        GridLayout.LayoutParams lp_Sectiontext = new GridLayout.LayoutParams(rowspan3, colspan3);
        lp_Sectiontext.height = GridLayout.LayoutParams.WRAP_CONTENT;
        lp_Sectiontext.width = GridLayout.LayoutParams.MATCH_PARENT;
        Sectiontext.setLayoutParams(lp_Sectiontext);

        return Sectiontext;
    }

    private TextView setFieldName(String fieldName, int id, int count, int col, int row) {
        System.out.println(">> field name : "+fieldName);
        TextView textview = new TextView(getApplicationContext());
        textview.clearComposingText();
        textview.setText(null);
        textview.setText(fieldName);
        textview.setId(id + count);
        textview.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        GridLayout.Spec rowspan1 = GridLayout.spec(row, 1);
        GridLayout.Spec colspan1 = GridLayout.spec(col, 9);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowspan1, colspan1);
        lp.height = 100;
        lp.width = 500;
        textview.setLayoutParams(lp);

        return textview;

    }

    private TextView setFieldValue(String fieldValue, int id, int count, int col, int row)
    {
        System.out.println(">> field value : "+fieldValue);
        TextView textview2 = new TextView(getApplicationContext());
        textview2.clearComposingText();
        textview2.setText(null);
        textview2.setText(fieldValue);
        textview2.setId(id + count);
        textview2.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        GridLayout.Spec rowspan2 = GridLayout.spec(row, 1);
        GridLayout.Spec colspan2 = GridLayout.spec(col, 3);
        GridLayout.LayoutParams lpp = new GridLayout.LayoutParams(rowspan2, colspan2);
        lpp.height = 100;
        lpp.width = 500;
        textview2.setLayoutParams(lpp);

        return textview2;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.record_view_page_drawer, menu);
        return true;
    }

    class APImodeRunner extends AsyncTask<String, String, String>
    {
        private String resp;
        @Override
        protected String doInBackground(String... params)
        {
            try
            {

                zcrmRecord = (ZCRMRecord) ZCRMModule.getInstance(com.zoho.sample_app.ListViewAdapter.moduleAPIname).getRecord(com.zoho.sample_app.ListViewAdapter.idClicked).getData();
                if(zcrmRecord.getLayout() != null) {
                    zcrmLayout = (ZCRMLayout) ZCRMModule.getInstance(com.zoho.sample_app.ListViewAdapter.moduleAPIname).getLayoutDetails(zcrmRecord.getLayout().getId()).getData();
                }else {
                    zcrmLayout = ((List<ZCRMLayout>) ZCRMModule.getInstance(com.zoho.sample_app.ListViewAdapter.moduleAPIname).getLayouts().getData()).get(0);
                }
                resp = "Success"; //no I18N
            } catch (Exception e)
            {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                loadTable();
            } catch (ZCRMException | JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}

