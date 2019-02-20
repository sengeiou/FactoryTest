package mitv.keystone;

import android.util.Log;

import android.os.Parcelable;
import android.os.Parcel;

public class KeystonePoint implements Parcelable {
    static final String TAG = "KeystonePoint";
    public int x;
    public int y;

    /**
     *
     */
    public KeystonePoint(int x, int y) {
        Log.e(TAG, "KeystonePoint x:" + x + " y:" + y);

        this.x = x;
        this.y = y;
    }

    public KeystonePoint(){
        x = 0;
        y = 0;
    }

    public int set(int x,int y){
        this.x = x;
        this.y = y;
        return 0;
    }

    /**
     * Implement the Parcelable interface
     *
     * @hide
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Implement the Parcelable interface.
     *
     * @hide
     */
    public void writeToParcel(Parcel dest, int flags) {
        synchronized (this) {
            dest.writeInt(x);
            dest.writeInt(y);
        }
    }

    /**
     * Implement the Parcelable interface.
     *
     * @hide
     */
    public static final Creator<KeystonePoint> CREATOR =
            new Creator<KeystonePoint>() {
                public KeystonePoint createFromParcel(Parcel in) {
                    int x = in.readInt();
                    int y = in.readInt();

                    KeystonePoint proInfo = new KeystonePoint(x, y);

                    return proInfo;
                }

                public KeystonePoint[] newArray(int size) {
                    return new KeystonePoint[size];
                }
            };

    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
