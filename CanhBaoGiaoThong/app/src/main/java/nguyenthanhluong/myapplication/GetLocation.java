package nguyenthanhluong.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class GetLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

        private Location location;
        // Đối tượng tương tác với Google API
        private GoogleApiClient gac;
        // Hiển thị vị trí
        private TextView tvLocation;
        private  TextView tvLocation2;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.get_location_main);
                tvLocation = (TextView) findViewById(R.id.tvLocation);
                tvLocation2=(TextView) findViewById(R.id.textView3);
                // Trước tiên chúng ta cần phải kiểm tra play services
                if (checkPlayServices()) {
                        // Building the GoogleApi client
                        buildGoogleApiClient();
                }
        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.getlocation_menu, menu);
                return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                        case R.id.back:
                                Intent mh2 = new Intent(GetLocation.this,MainActivity.class);
                                startActivity(mh2);
                                 finish();
                                break;
                }
                return super.onOptionsItemSelected(item);
        }

        public void dispLocation(View view) {
                getLocation();
                if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        // Hiển thị
                        tvLocation2.setText(latitude + ", " + longitude);
                        tvLocation2.setText(latitude + ", " + longitude);
                }else{
                        Toast.makeText(this, "Bật vị trí và thử lại", Toast.LENGTH_LONG).show();
                }
        }

        /**
         * Phương thức này dùng để hiển thị trên UI
         * */
        private void getLocation() {
                if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Kiểm tra quyền hạn
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                } else {
                        location = LocationServices.FusedLocationApi.getLastLocation(gac);

                        if (location != null) {
                             //   double latitude = location.getLatitude();
                               // double longitude = location.getLongitude();
                                // Hiển thị
                                //tvLocation.setText(latitude + ", " + longitude);
                                tvLocation.setText("Vị trí đã được bật");
                        } else {
                                tvLocation.setText("Hãy bật vị trí để xem thông tin");
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
                getLocation();
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
