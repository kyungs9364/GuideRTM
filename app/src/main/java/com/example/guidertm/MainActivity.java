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
import android.widget.ImageView;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String TAG="PAAR";
    String Distance;
    TMapView mMapView = null;
    RelativeLayout mapContainer = null;
    EditText my_location;
    EditText my_destination;
    Button VR;
    Button Search;
    Button roadservice;
    ImageView update;
    public static Context mContext;  // 타 액티비티에서 변수or함수를 사용가능하게함
    Geocoder coder;
    CameraOverlayview mOverlayview;
    CameraActivity mCameraActivity;
    public static double latitude_plic ;
    public static double longitude_plic ;
    public static double des_latitude_plic ;
    public static double des_longitude_plic ;
    public static TMapPoint point2;
    public static  double Ddistance;
    public static List<NodeData> nodeDatas=new ArrayList<NodeData>();


    class MyListenerClass implements View.OnClickListener {
        public void onClick(View v) {
            if (v.getId() == R.id.search) {
                findAllPoi();
                    }
            else if (v.getId() == R.id.VR)
            {

                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                intent.putExtra("latitude_id",String.valueOf(des_latitude_plic));  // CameraOverlayview 에 목적지값 전송 - cameraActivity 통해서 경유
                intent.putExtra("longitude_id",String.valueOf(des_longitude_plic ));
                intent.putExtra("node",(Serializable)nodeDatas);
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
                VR.setEnabled(true);
                drawPedestrianPath();
                naviGuide();
            }
            else if (v.getId() == R.id.update)
            {
                if(point2 != null)
                {
                    VR.setEnabled(true);
                    drawPedestrianPath();
                    naviGuide();
                }
                else
                {
                    showToast("길찾기를 진행해 주십시오.");
                }

            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapContainer = (RelativeLayout) findViewById(R.id.Tmap);

        mMapView = new TMapView(this);
        mapContainer.addView(mMapView);
        mMapView.setSKPMapApiKey("c8e531e6-e9cf-3f33-81d8-809a113a8adb");
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
        VR.setEnabled(false);

        update = (ImageView)findViewById(R.id.update);
        mContext = this;  // 타 액티비티에서 접근 가능하게 함.
        Intent intent = getIntent();
        my_destination.setText(intent.getStringExtra("des_info")); // listview 에서 돌아온 도착지 name
        Double la_point = intent.getDoubleExtra("point_la",0);       // listview 에서 돌아온 위도, 경도
        Double lo_point = intent.getDoubleExtra("point_lo",0);
        des_latitude_plic = la_point;
        des_longitude_plic = lo_point;
        Log.d(TAG, "des=" + String.valueOf( des_latitude_plic));
        Log.d(TAG, "des=" + String.valueOf( des_longitude_plic));


        mOverlayview = new CameraOverlayview(this);
        mCameraActivity = new CameraActivity();
        coder = new Geocoder(getApplicationContext(), Locale.KOREA);

        //Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.run);
        //Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        //mMapView.setIcon(bitmap);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                latitude_plic = location.getLatitude();
                longitude_plic = location.getLongitude();

                mOverlayview.setCurrentPoint(latitude_plic,longitude_plic,Ddistance);  // 현재위치 업데이트를 위해 mOverlayview에 값 전송
                mCameraActivity.setCurrent(latitude_plic,longitude_plic);

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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, locationListener);

        MyListenerClass buttonListener = new MyListenerClass();
        Search.setOnClickListener(buttonListener);
        roadservice.setOnClickListener(buttonListener);
        VR.setOnClickListener(buttonListener);
        update.setOnClickListener(buttonListener);
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
        point2 = new TMapPoint(des_latitude_plic,des_longitude_plic);

        Log.d(TAG, "point location= " + point1 + point2);


        TMapData tmapdata = new TMapData();
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {

            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                polyLine.setLineWidth(10);
                Ddistance = polyLine.getDistance();
                Log.d(TAG, " main_distance " + Ddistance);

               /* if (Ddistance < 1000.0)
                {
                    String distance = longDouble2String(0,Ddistance);
                    Distance = distance + "m";
                    Log.d(TAG, "m = " + Distance);
                }
                else if(Ddistance >= 1000.0)
                {
                    float fDistance = (float) Ddistance / 1000;
                    String fdistance = longDouble2String(1,fDistance);
                    Distance = fdistance + "km";
                    Log.d(TAG, "km = " + Distance);
                }*/

                mMapView.addTMapPath(polyLine);

            }
        });
    }

    public void naviGuide() {
        TMapPoint point1 = new TMapPoint(latitude_plic, longitude_plic);
        TMapPoint point2 = new TMapPoint(des_latitude_plic, des_longitude_plic);
        TMapData tmapdata = new TMapData();

        mMapView.zoomToTMapPoint ( point1,point2 );  // 자동 zoomlevel 조정
        mMapView.setCenterPoint(longitude_plic, latitude_plic);

        Log.d(TAG, "point location2= " + point1 + point2);


        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH,point1, point2, new TMapData.FindPathDataAllListenerCallback(){
            @Override
            public void onFindPathDataAll(Document doc) {
                doc.getDocumentElement().normalize();
                Element root = doc.getDocumentElement();
                int length = root.getElementsByTagName("Placemark").getLength();
                for(int i=0; i<length; i++) {
                    String a="";
                    Node placemark = root.getElementsByTagName("Placemark").item(i);
                    Node tmapindex = ((Element)placemark).getElementsByTagName("tmap:index").item(0);
                    String index=tmapindex.getTextContent();
                    Node nodeType = ((Element)placemark).getElementsByTagName("tmap:nodeType").item(0);
                    String nodetype=nodeType.getTextContent();
                    Node coordinate = ((Element)placemark).getElementsByTagName("coordinates").item(0);
                    String coordinates=coordinate.getTextContent();
                    if(nodeType.getTextContent().equals("POINT")) {
                        Node turnType = ((Element) placemark).getElementsByTagName("tmap:turnType").item(0);
                        a = Turntype(turnType.getTextContent());
                        nodeDatas.add(new NodeData(index,nodetype,coordinates,a));
                    }
                    else nodeDatas.add(new NodeData(index, nodetype, coordinates,a));

                }
            }
        });
    }


    public String Turntype(String c)
    {
        switch (c)
        {
            case "11" : c="직진"; break;
            case "12" : c="좌회전"; break;
            case "13" : c="우회전"; break;
            case "14" : c="U-turn"; break;
            case "16" : c="8시 방향 좌회전"; break;
            case "17" : c="10방향 좌회전"; break;
            case "18" : c="2시 방향 우회전"; break;
            case "19" : c="4시 방향 우회전"; break;
            case "184" : c="첫번째 경유지"; break;
            case "186" : c="두번째 경유지"; break;
            case "187" : c="세번째 경유지"; break;
            case "188" : c="네번째 경유지"; break;
            case "189" : c="다섯번째 경유지"; break;
            case "125" : c="육교"; break;
            case "126" : c="지하보도"; break;
            case "127" : c="계단 진입"; break;
            case "128" : c="경사로 진입"; break;
            case "129" : c="계단 + 경사로 진입"; break;
            case "200" : c="출발지"; break;
            case "201" : c="목적지"; break;
            case "211" : c="횡단보도"; break;
            case "212" : c="좌측 횡단보도"; break;
            case "213" : c="우측 횡단보도"; break;
            case "214" : c="8시 방향 횡단보도"; break;
            case "215" : c="10시 방향 횡단보도"; break;
            case "216" : c="2시 방향 횡단보도"; break;
            case "217" : c="4시 방향 횡단보도"; break;
            case "218" : c="엘리베이터"; break;
        }


        return c;
    }

    /*public static String longDouble2String(int size, double value) {   // 소수점 이하 자리를 짜르는 함수
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(size);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }*/

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        nodeDatas.clear();
        VR.setEnabled(false);
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

