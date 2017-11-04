package tools;

import java.util.HashMap;

public class SvgData {
    private HashMap<String,String> data;

    public SvgData(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}
