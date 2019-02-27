package mitv.projector;

import android.util.Log;

import android.os.Parcelable;
import android.os.Parcel;

public class ProjectorInfo implements Parcelable {
    static final String TAG = "ProjectorInfo";
    /** 光机类型： LED光源 **/
    public static final int LED_PROJECTOR = 0;
    /** 光机类型： 激光光源 **/
    public static final int LASER_PROJECTOR = 1;

    private String vendorName;
    private String serialNo;

    private int lightType = 0;

    private int majorVersion = 0;

    private int minorVersion = 0;

    private int patchVersion = 0;

    /**
     * 光机信息，厂商名字，光源类型(LED_PROJECTOR,LASER_PROJECTOR), flash build 版本号
     */
    public ProjectorInfo(String name, String serialno, int type, int major, int minor, int patch) {
        Log.e(TAG, "ProjectorInfo name:" + name + " Type:" + type + " major:" + major +
                " minor:" + minor + " patch:" + patch);

        vendorName = name;
        serialNo = serialno;
        lightType = type;
        majorVersion = major;
        minorVersion = minor;
        patchVersion = patch;
    }

    public String GetSerialNo() {
        return serialNo;
    }

    public String GetVendorName() {
        return vendorName;
    }
    /**
     * 获取光机光源类型
     *
     * @return <pre>
     * {@link #LED_PROJECTOR}
     * {@link #LASER_PROJECTOR}
     * </pre>
     */
    public int GetProjctorLightType() {
        return lightType;
    }

    /**
     * 获取光机flash build版本信息
     *
     * @return major: xx,minor: xx,patch: xx
     */
    public String GetFlashBuildVersion() {
        StringBuffer s = new StringBuffer();

        s.append("major: ").append(majorVersion).append(",");
        s.append("minor: ").append(minorVersion).append(",");
        s.append("patch: ").append(patchVersion);
        return s.toString();
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
            dest.writeString(vendorName);
            dest.writeString(serialNo);
            dest.writeInt(lightType);
            dest.writeInt(majorVersion);
            dest.writeInt(minorVersion);
            dest.writeInt(patchVersion);
        }
    }

    /**
     * Implement the Parcelable interface.
     *
     * @hide
     */
    public static final Creator<ProjectorInfo> CREATOR =
            new Creator<ProjectorInfo>() {
                public ProjectorInfo createFromParcel(Parcel in) {
                    String vendorname = in.readString();
                    String serialno = in.readString();
                    int type = in.readInt();
                    int major = in.readInt();
                    int minor = in.readInt();
                    int patch = in.readInt();

                    ProjectorInfo proInfo = new ProjectorInfo(vendorname, serialno, type, major, minor, patch);

                    return proInfo;
                }

                public ProjectorInfo[] newArray(int size) {
                    return new ProjectorInfo[size];
                }
            };
}
