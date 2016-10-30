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
    ArrayList<String> point = new ArrayList<String>();
    ArrayList<String> addss;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        Intent intent = getIntent();
        addss = (ArrayList<String>) intent.getSerializableExtra("address"); // 도착지 주소이름
        ArrayList<String> poi = (ArrayList<String>) intent.getSerializableExtra("point");  // 도착지 위도,경도


        if(addss != null)
        {
            for(int i=0; i < addss.size(); i++)  // 리스트 안에 전달 받은 주소이름과 위도경도를 추가한다.
            {
                address.add(addss.get(i));
                point.add(poi.get(i));
            }
        }
        else
        {
            address.add("검색 결과가 존재하지 않습니다.\n\t 돌아가려면 클릭해주세요.");
            //String not_search = "검색 결과가 존재하지 않습니다.";
            //Intent back = new Intent(Listview.this,MainActivity.class);
            // back.putExtra("not_search",not_search);
            //startActivity(back);
            //finish();
        }


        ArrayAdapter<String> Adapter;  // 어텝터 연결
        Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, address);

        ListView list =  (ListView) findViewById(R.id.list);
        list.setAdapter(Adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()  // list 클릭이벤트
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(address.get(position) != null) // position은 우리가 선택한 list의 번수
                {
                    Intent intent = new Intent(Listview.this, MainActivity.class);

                    if(addss != null) {
                        intent.putExtra("des_info", address.get(position));
                        String la_po = point.get(position).replace("Lat", "").replace("Lon", "").trim(); // 위도
                        String lo_po = point.get(position).replace("Lat", "").replace("Lon", "").trim(); // 경도
                        la_po = la_po.substring(0, 10); // 0번부터 10번까지의 문자열 반환 (위도)
                        lo_po = lo_po.substring(10);   // 10번 이후부터 끝까지 문자열 반환 (경도)
                        intent.putExtra("point_la", Double.valueOf(la_po));
                        intent.putExtra("point_lo", Double.valueOf(lo_po));
                        //moveTaskToBack(true);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        startActivity(intent);
                        finish();
                    }

                }
                else
                    Toast.makeText(getApplicationContext(), "도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();;

            }
        });

        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(new ColorDrawable(Color.WHITE));
        list.setDividerHeight(2);
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}