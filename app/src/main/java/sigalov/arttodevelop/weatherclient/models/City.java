package sigalov.arttodevelop.weatherclient.models;


public class City {
    private Integer id;
    private String name;

    public City(Integer id, String name)
    {
        this.id = id;
        this.name = name;

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
