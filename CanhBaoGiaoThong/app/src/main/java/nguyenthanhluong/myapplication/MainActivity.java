package nguyenthanhluong.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

//Hướng dẫn nhúng vào một dấu chấm đỏ đánh dấu địa điểm trên bản đồ https://developers.google.com/maps/documentation/android-api/?hl=vi
//Đầu tiên khái báo thêm implements OnMapReadyCallback trên class
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap map;
    private Location location;
    private GoogleApiClient gac;
    private static final int REQUEST_CODE = 100;
    // Firebase
    FirebaseDatabase firebaseDataBase;
    DatabaseReference firebaseRef;
    Marker marker;

    // Widgets mapping
    EditText txtTextMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Chào bạn đã đến với Wmaps", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);

        firebaseDataBase = FirebaseDatabase.getInstance();
        firebaseRef = firebaseDataBase.getReference("TOADO");
        txtTextMsg = (EditText) findViewById(R.id.txtTextMsg);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    //firebase
    public void loadDataTuFirebaseAddVaoMap() {
        try {
            firebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Marker marker;
                    // Moi khi load du lieu thi xoa het market
                    map.clear();
                    // Lay tung market add vao map
                    for (DataSnapshot db : dataSnapshot.getChildren()) {

                        // Get each latlong data item
                        final LatLong toado = db.getValue(LatLong.class);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markericon);
                        marker = map.addMarker(new MarkerOptions()
                                .icon(icon)
                                .title("Cảnh báo")
                                .snippet(toado.getMessage())
                                .position(new LatLng(toado.getLatitude(), toado.getLongitude())));
                        //show phần info marker
                        marker.showInfoWindow();
                        marker.setTag(db.getKey());
                        removemf();
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception ex) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent mh2 = new Intent(MainActivity.this, GetLocation.class);
                startActivity(mh2);
                finish();
                break;
            case R.id.itexit:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Mình rất tiếc đã không giữ chân bạn lại", Toast.LENGTH_SHORT).show();
                // Tao su kien ket thuc app
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
                break;
            case R.id.mapTypeNone:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // khi map da sang sang
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            Toast.makeText(this, "Đã cho phép permssion", Toast.LENGTH_SHORT).show();
            location = LocationServices.FusedLocationApi.getLastLocation(gac);
            map.setMyLocationEnabled(true);
        }
        loadDataTuFirebaseAddVaoMap();

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker markers;
                Geocoder geocoder = new Geocoder(MainActivity.this);
                List<Address> list;
                try {
                    list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    return;
                }
                Address address = list.get(0);
                removem();
                MarkerOptions options = new MarkerOptions()
                        .title("Địa điểm của bạn")
                        .snippet(address.getLocality())
                        .draggable(true)
                        .position(new LatLng(latLng.latitude, latLng.longitude));
                marker = map.addMarker(options);
            }
        });

    }
    public void removemf(){

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //remove on firebase
                String id = (String) marker.getTag();
                firebaseRef.child(id).removeValue();
                marker.remove();
            }
        });

    }
    public void removem(){
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.remove();
            }
        });
    }

    // Them vi tri hien tai vao map va gui len Firebase
    public void addCurrentLocationMarker() {
        // Lay lai vi tri hien tai
        Location currentLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(gac);
        }

        if (currentLocation != null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            LatLng luong = new LatLng(latitude, longitude);
            //moveCemera là góc độ nhìn ngang,dọc,xéo
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(luong, 15));
            // Gui data len Firebase
            firebaseRef.push().setValue(new LatLong(latitude, longitude, txtTextMsg.getText().toString()));

        } else {
            Toast.makeText(this, "Hãy Bật vị trí", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }
    }

    public void onMarker(View view) {
        addCurrentLocationMarker();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cho phép", Toast.LENGTH_SHORT).show();
            } else {


                Toast.makeText(this, "Chưa cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }

    }



    public void Search(View v) throws IOException {

        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();
        if (et.getText().length() == 0) {
            Toast.makeText(this, "Bạn chưa nhập", Toast.LENGTH_LONG).show();
        } else {
            Geocoder geocoder = new Geocoder(this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(location, 1);
                if (!list.isEmpty()) {
                    Address add = list.get(0);
                    String locality = add.getLocality();
                    LatLng ll = new LatLng(add.getLatitude(), add.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                    removem();
                    map.moveCamera(update);
                    if (marker != null)
                        marker.remove();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(locality)
                            .position(new LatLng(add.getLatitude(), add.getLongitude()));
                    marker = map.addMarker(markerOptions);
                    Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Nhập sai", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                return;
            }
        }

    }

    /**
     * Tạo đối tượng google api client
     * */
    protected synchronized void buildGoogleApiClient() {
        if (gac == null) {
            gac = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    /**
     * Phương thức kiểm chứng google play services trên thiết bị
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1000).show();
            } else {
                Toast.makeText(this, "Thiết bị này không hỗ trợ.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Đã kết nối với google api, lấy vị trí
        initMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
        gac.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Lỗi kết nối: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();

    }

    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }
}
