package sigalov.arttodevelop.weatherclient.models.Response;

public class ServerErrorResponse {

    private String cod;
    private String message;

    public String getCode() {
        return cod;
    }

    public void setCode(String code) {
        this.cod = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String error) {
        this.message = error;
    }

}
