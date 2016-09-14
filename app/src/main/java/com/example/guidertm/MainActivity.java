package com.example.guidertm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String TAG="PAAR";
    TMapView mMapView = null;
    RelativeLayout mapContainer = null;
    EditText my_location;
    EditText my_destination;
    Button VR;
    Button Search;
    Button roadservice;
    Context mContext;
    Geocoder coder;
    CameraOverlayview mOverlayview;
    public static double latitude_plic ;
    public static double longitude_plic ;
    public static double des_latitude_plic ;
    public static double des_longitude_plic ;
    public static TMapPoint point2;

    class MyListenerClass implements View.OnClickListener {
        public void onClick(View v) {
            if (v.getId() == R.id.search) {
                findAllPoi();
                    }
            else if (v.getId() == R.id.VR)
            {
                String end = my_destination.getText().toString();

                if (end == null || end.length() == 0) {
                showToast("검색어가 입력되지 않았습니다.");
                return;
            }
                Toast.makeText(getApplicationContext(), "도착지 : " + end, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                intent.putExtra("latitude_id",String.valueOf(des_latitude_plic));  // CameraOverlayview 에 목적지값 전송 - cameraActivity 통해서 경유
                intent.putExtra("longitude_id",String.valueOf(des_longitude_plic ));
                startActivity(intent);

                  /*
                List<Address> des_location = null;

                try {    //  도착위치 값 지오코딩.
                    des_location = coder.getFromLocationName(end, 5);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "입출력오류 :" + e.getMessage() + "", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                des_latitude_plic = des_location.get(0).getLatitude();
                des_longitude_plic = des_location.get(0).getLongitude();*/

            }
            else if (v.getId() == R.id.road)
            {
                String query = my_destination.getText().toString();
                if (query == null || query.length() == 0) {
                    showToast("검색어가 입력되지 않았습니다.");
                    return;
                }

                drawPedestrianPath();
                naviGuide();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapContainer = (RelativeLayout) findViewById(R.id.Tmap);

        mMapView = new TMapView(this);
        mapContainer.addView(mMapView);
        mMapView.setSKPMapApiKey("a9125b4b-89ae-37f1-9eb1-21dfdc5fb1d7");
        mMapView.setLanguage(TMapView.LANGUAGE_KOREAN);  // 지도 언어 설정
        mMapView.setMapType(TMapView.MAPTYPE_STANDARD);  // 지도 타입 표준
        mMapView.setIconVisibility(true);    // 현재위치 아이콘을 나타낼 것인지 표시
        mMapView.setZoomLevel(17);
        mMapView.MapZoomIn();
        mMapView.MapZoomOut();
        my_location = (EditText) findViewById(R.id.start_edit);
        my_location.setEnabled(false);  // 입력 불가.
        my_destination = (EditText) findViewById(R.id.destination_edit); // 검색창
        my_destination.setEnabled(false);
        Search = (Button) findViewById(R.id.search); // 검색버튼
        roadservice = (Button) findViewById(R.id.road); // 길찾기버튼
        VR = (Button) findViewById(R.id.VR);  // VR버튼
        mContext = this;
        Intent intent = getIntent();
        my_destination.setText(intent.getStringExtra("des_info")); // listview 에서 돌아온 도착지 name
        Double la_point = intent.getDoubleExtra("point_la",0);       // listview 에서 돌아온 위도, 경도
        Double lo_point = intent.getDoubleExtra("point_lo",0);
        des_latitude_plic = la_point;
        des_longitude_plic = lo_point;
        Log.d(TAG, "des=" + String.valueOf( des_latitude_plic));
        Log.d(TAG, "des=" + String.valueOf( des_longitude_plic));


        mOverlayview = new CameraOverlayview(this);
        coder = new Geocoder(getApplicationContext(), Locale.KOREA);

        //Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.run);
        //Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        //mMapView.setIcon(bitmap);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                latitude_plic = location.getLatitude();
                longitude_plic = location.getLongitude();
                Log.d(TAG, "qwe2 = " + String.valueOf(point2));
                if(point2 != null)
                {
                    drawPedestrianPath();
                }
                mOverlayview.setCurrentPoint(latitude_plic,longitude_plic);  // 현재위치 업데이트를 위해 mOverlayview에 값 전송

                my_location.setText("현 위치");
                mMapView.setCenterPoint(longitude_plic, latitude_plic);
                mMapView.setLocationPoint(longitude_plic, latitude_plic);
                mMapView.setTrackingMode(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 3, locationListener);

        MyListenerClass buttonListener = new MyListenerClass();
        Search.setOnClickListener(buttonListener);
        roadservice.setOnClickListener(buttonListener);
        VR.setOnClickListener(buttonListener);
    }
    public void findAllPoi() {  // 검색 함수

        AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 팝업창을 띄워주기 위해 생성
        builder.setTitle("Guide road Search part,,");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strData = input.getText().toString();
                TMapData tmapdata = new TMapData();

                tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        Intent intent = new Intent(MainActivity.this,Listview.class);
                        ArrayList<String> search_list = new ArrayList<String>();
                        ArrayList<String> point_list = new ArrayList<String>();

                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem  item = poiItem.get(i);
                            String[] address = new String[poiItem.size()];
                            String[] point = new String[poiItem.size()];
                            address[i] = String.valueOf(item.getPOIName().toString());
                            point[i] =  String.valueOf(item.getPOIPoint().toString());
                            search_list.add(address[i]);
                            point_list.add(point[i]);

                            intent.putExtra("address",search_list);
                            intent.putExtra("point",point_list);
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();  // 취소시 팝업창 닫기
            }
        });
        builder.show();
    }
    public void drawPedestrianPath()   // 길찾기 함수
    {
        final TMapPoint point1 = new TMapPoint(latitude_plic,longitude_plic);
        /*final TMapPoint*/ point2 = new TMapPoint(des_latitude_plic,des_longitude_plic);


        TMapData tmapdata = new TMapData();
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {

            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                polyLine.setLineWidth(10);
                double Distance = polyLine.getDistance();
                Log.d(TAG, "qwe = " + String.valueOf(Distance));

                mMapView.addTMapPath(polyLine);

                mMapView.zoomToTMapPoint ( point1,point2 );  // 자동 zoomlevel 조정
            }
        });
    }

    public void naviGuide() {
        final TMapPoint point1 = new TMapPoint(latitude_plic,longitude_plic);
        final TMapPoint point2 = new TMapPoint(des_latitude_plic,des_longitude_plic);

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH,point1, point2, new TMapData.FindPathDataAllListenerCallback(){
            @Override
            public void onFindPathDataAll(Document doc) {
                doc.getDocumentElement().normalize();//dom tree가 xml문서의 구조대로 완성
                Element root = doc.getDocumentElement();//루트 노드 가져오기
                getNode(root);

            }
        });
    }

    public static void getNode(Node n) {

        for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch.getNodeType() == Node.ELEMENT_NODE) {

                System.out.println(ch.getNodeName());

                getNode(ch);

            } else if (ch.getNodeType() == Node.TEXT_NODE && ch.getNodeValue().trim().length() != 0) {

                System.out.println(ch.getNodeValue());

            }
        }
    }


   /* private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(parsing).get();
                Elements links = doc.select("div.list_content_wrap span.txt_section");

                //htmlContentInStringFormat = links.text().trim() + "\n";

                for (org.jsoup.nodes.Element link : links)
                {

                    htmlContentInStringFormat += (link.attr("abs:href")
                            +link.text().trim() + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext() ,htmlContentInStringFormat , Toast.LENGTH_SHORT).show();
        }
    }
}*/

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

