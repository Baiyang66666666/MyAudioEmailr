package com.com6103.email.utils;

import com.com6103.email.entity.TTSRequest;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    public static String getOrDefault(String str) {
        return str == null ? "" : str;
    }

    /**
     * Converts a time string in format "HH:mm" to a cron expression that represents the same time
     *
     * @param time the time string to convert
     * @return the cron expression representing the same time
     * @throws ParseException if the input time string is not in the correct format
     */
    public static String convertToCron(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = sdf.parse(time);
        String minutes = Integer.toString(date.getMinutes());
        String hours = Integer.toString(date.getHours());

        return String.format("0 %s %s ? * *", minutes, hours);
    }

    /**
     * Transposes an ArrayList by reversing its order.
     *
     * @param arrayList the ArrayList to transpose
     * @return a new ArrayList with the elements of the original ArrayList in reverse order
     */
    public static <T> List<T> transposeArrayList(List<T> arrayList) {
        List<T> transposedList = new ArrayList<T>();
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            transposedList.add(arrayList.get(i));
        }
        return transposedList;
    }


    /**
     * Sets the HttpEntity with the given TTSRequest and Content-Type header.
     *
     * @param ttsRequest the TTSRequest to set in the HttpEntity
     * @return the HttpEntity with the TTSRequest and Content-Type header
     */
    public static HttpEntity setHttpEntity(TTSRequest ttsRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity<>(ttsRequest, headers);
        return httpEntity;
    }

    /**
     * Converts a time string in format "HH:mm" to a cron expression that represents the same time, but one hour earlier.
     *
     * @param time the time string to convert
     * @return the cron expression representing the same time, but one hour earlier
     * @throws ParseException if the input time string is not in the correct format
     */
    public static String convertToCronMinus(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -1); // 将时间提前一小时
        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        String hours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        return String.format("0 %s %s ? * *", minutes, hours);
    }

}
