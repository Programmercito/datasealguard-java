package org.osbo.core.datasealguard.json;
import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 *
 * @author programmercito
 */
public class JsonUtil {

    private static final Gson gson = new Gson();

    public static <T> T fromJson(String json, Type classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static String convert(Object objeto) {
        String json = gson.toJson(objeto);
        return json;
    }

}
