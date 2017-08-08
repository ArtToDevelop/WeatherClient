package sigalov.arttodevelop.weatherclient.models;


public class City {
    public static final String TableName = "CITY";

    private Integer serverId;
    private Integer id;
    private String name;

    public City(Integer serverId, String name)
    {
        this.serverId = serverId;
        this.name = name;
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
}
