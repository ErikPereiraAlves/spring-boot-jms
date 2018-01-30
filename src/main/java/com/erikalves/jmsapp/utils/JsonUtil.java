package com.erikalves.jmsapp.utils;

import com.erikalves.jmsapp.models.Product;
import com.google.gson.Gson;

public class JsonUtil {

    public static String JsonConvert(Object obj){

       return new Gson().toJson(obj);
    }

    //type psvm for shortcut
    public static void main(String[] args) {

        Product product =  new Product("1", "Mentos", "Mentos powermings", "Mentos powermings", "google.com/mentos", 1.5, DateUtils.getCurrentTimestamp(), DateUtils.getCurrentTimestamp() );
        System.out.println(JsonUtil.JsonConvert(product));
    }

}
