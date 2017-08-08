package sigalov.arttodevelop.weatherclient.models;


import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    public static final String TableName = "CITY";

    private Integer serverId;
    private Integer id;
    private String name;

    public City(Integer serverId, String name)
    {
        this.serverId = serverId;
        this.name = name;
    }

    private City(Parcel in) {
        serverId = in.readInt();
        id = in.readInt();
        name = in.readString();
    }

    public Integer getServerId() {
        return serverId;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(serverId);
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
