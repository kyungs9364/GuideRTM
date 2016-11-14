package com.example.guidertm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

    ArrayAdapter<String> Adapter;
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> point = new ArrayList<String>();
    ArrayList<String> sub_address = new ArrayList<String>();
    ArrayList<String> addss;

    private ArrayList<Custom_List_Data> Array_Data;
    private Custom_List_Data data;
    public static Custom_List_Adapter adapter;


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int) (height*.8));

        Array_Data = new ArrayList<Custom_List_Data>();

        Intent intent = getIntent();
        addss = (ArrayList<String>) intent.getSerializableExtra("address"); // 도착지 주소이름
        sub_address = (ArrayList<String>) intent.getSerializableExtra("sub_address");

        ArrayList<String> poi = (ArrayList<String>) intent.getSerializableExtra("point");  // 도착지 위도,경도


        if(addss != null)
        {
            for(int i=0; i < addss.size(); i++)  // 리스트 안에 전달 받은 주소이름과 위도경도를 추가한다.
            {
                data = new Custom_List_Data(R.drawable.listitemicon,(addss.get(i)), sub_address.get(i).replace("null",""));
                Array_Data.add(data);
                address.add(addss.get(i));
                point.add(poi.get(i));
            }
        }
        else
        {
            data = new Custom_List_Data(R.drawable.err,"결과값이 존재하지 않습니다.", "뒤로가려면 클릭해주세요." );
            Array_Data.add(data);
            address.add("결과값이 존재하지 않습니다.");
            //String not_search = "검색 결과가 존재하지 않습니다.";
            //Intent back = new Intent(Listview.this,MainActivity.class);
            // back.putExtra("not_search",not_search);
            //startActivity(back);
            //finish();
        }


        // 어텝터 연결
        adapter = new Custom_List_Adapter(this,
                android.R.layout.simple_list_item_1, Array_Data);

        ListView list =  (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

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
                        ((MainActivity)MainActivity.mContext).finish();
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        ((MainActivity)MainActivity.mContext).finish();
                        startActivity(intent);
                        finish();
                    }

                }
                else
                    Toast.makeText(getApplicationContext(), "도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();;

            }
        });

        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(new ColorDrawable(Color.LTGRAY));
        list.setDividerHeight(2);
    }

    protected void onPause() {
        super.onPause();

        Log.e("popck"," = 3" );
    }
    protected void onDestroy() {
        super.onDestroy();
        if(((MainActivity)MainActivity.mContext).search_list != null) {
            ((MainActivity)MainActivity.mContext).search_list.clear();
            ((MainActivity)MainActivity.mContext).point_list.clear();
            ((MainActivity)MainActivity.mContext).sub_address_list.clear();
        }
        Log.e("popck"," = 4" );
    }
}