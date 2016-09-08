package com.example.guidertm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 경석 on 2016-09-08.
 */
public class Listview extends Activity {

    ArrayList<String> address = new ArrayList<String>();
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        Intent intent = getIntent();
        ArrayList<String> addss = (ArrayList<String>) intent.getSerializableExtra("address");


        for(int i=0; i < addss.size(); i++)
        {
            address.add(addss.get(i));
        }

        ArrayAdapter<String> Adapter;
        Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, address);

        ListView list =  (ListView) findViewById(R.id.list);
        list.setAdapter(Adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(address.get(position) != null)
                {
                    Intent intent = new Intent(Listview.this,MainActivity.class);
                    intent.putExtra("des_info",address.get(position));
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();;

            }
        });

        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(new ColorDrawable(Color.WHITE));
        list.setDividerHeight(2);
    }
}
