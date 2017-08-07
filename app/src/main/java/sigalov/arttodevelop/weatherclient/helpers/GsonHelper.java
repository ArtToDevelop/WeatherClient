package sigalov.arttodevelop.weatherclient.helpers;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class GsonHelper {

    private GsonHelper() {}

    public static JsonObject getJsonObjectOrThrow(@NonNull JsonObject jsonObject, String memberName) throws Exception
    {
        if(!jsonObject.has(memberName))
            throw new Exception(String.format("JsonObject has not element '%s'", memberName));

        JsonElement jsonElement = jsonObject.get(memberName);

        if(!jsonElement.isJsonObject())
            throw new Exception(String.format("Member '%s' is not JsonObject", memberName));

        return jsonElement.getAsJsonObject();
    }

    public static Double getAsDoubleOrThrow(@NonNull JsonObject jsonObject, String memberName) throws Exception
    {
        JsonElement jInnerElement = getJsonElementOrThrow(jsonObject, memberName);

        // Json type
        if(!jInnerElement.isJsonPrimitive())
            throw new Exception(String.format("Element '%s' is not JsonPrimitive", memberName));

        JsonPrimitive jsonPrimitive = jInnerElement.getAsJsonPrimitive();

        return jsonPrimitive.getAsDouble();
    }

    private static JsonElement getJsonElementOrThrow(@NonNull JsonObject jsonObject, String memberName) throws Exception
    {
        if(!jsonObject.has(memberName))
            throw new Exception(String.format("JsonObject has not element '%s'", memberName));

        return jsonObject.get(memberName);
    }
}
