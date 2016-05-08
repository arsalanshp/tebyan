package net.tebyan.filesharingapp.model;

import org.json.JSONObject;

public class ReadJSONTaskInput {
    public String baseUrl;
    public JSONObject data;
    public Boolean needsLogin;
    public String url;

    public ReadJSONTaskInput() {
        this.data = new JSONObject();
        this.needsLogin = Boolean.valueOf(false);
    }
}
