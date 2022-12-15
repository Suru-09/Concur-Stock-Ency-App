package entity;

import com.google.gson.Gson;

public class RequestDeserializer {

private String serializedRequest;

    public static Request toRequest(String requestJson)
    {
        Gson gson = new Gson();
        Request request = gson.fromJson(requestJson, Request.class);

        return request;
    }
}
