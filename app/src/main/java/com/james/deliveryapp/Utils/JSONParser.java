package com.james.deliveryapp.Utils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.james.deliveryapp.Entities.DeliveryItem;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

public class JSONParser {
    // parse json string to corresponding List<DeliveryItem>
    public static List<DeliveryItem> getDeliveryItemList(String jsonString) throws JSONException {
        JSONArray responseJson;
        Gson gson =  new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        responseJson = new JSONArray(jsonString);
        return gson.fromJson(responseJson.toString(), new TypeToken<List<DeliveryItem>>(){}.getType());
    }
}
