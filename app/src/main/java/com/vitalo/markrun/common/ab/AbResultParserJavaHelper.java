package com.vitalo.markrun.common.ab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class AbResultParserJavaHelper {

    /**
     * 这个写法在kotlin上跑起来有问题
     * 先转移到java语法下创建
     */
    public static Gson createGson(JsonDeserializer<Map<String, AbConfigResponse.Data>> deserializer){
        Type type = new TypeToken<Map<String, AbConfigResponse.Data>>(){}.getType();
        return new GsonBuilder().registerTypeAdapter(type, deserializer).create();
    }

}