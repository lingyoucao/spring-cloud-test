package com.newland.bd.ms.learning.dashboard.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lcs on 2018/3/21.
 *
 * @author lcs
 */
public class DataHelper {

    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static void main(String[] args) {
        System.out.println(DataHelper.stampToDate("1522468462"));
    }


}
