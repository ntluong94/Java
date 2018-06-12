package nguyenthanhluong.myapplication;

/**
 * Created by mmq on 05/08/2017.
 */

public class LatLong {
    double latitude;
    double longitude;
    String message;


    public LatLong(double latitude, double longitude, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }

    public LatLong() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
