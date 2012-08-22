package org.mobicents.servlet.sip.example;

/**
 * This is a utility file that keeps record of all session transfer request.
 *  
 * @author Michael Adeyeye
 */
import java.sql.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class sessiontracking {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String usersipaddr = "sip:receiver@127.0.0.1:5091";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }

    public static void storesessioninfo(String from, String to, String sipmethod, String tranxstatus, String referredurl) {
    //public static void storesessioninfo(String from, String to, String sipmethod, String tranxstatus) {

        try {
            Statement stmt;
            ResultSet rs;
            Class.forName("com.mysql.jdbc.Driver");
            String url =
                    "jdbc:mysql://localhost:3306/messageblocking";
            Connection con =
                    DriverManager.getConnection(
                    url, "mike", "mk4nd");

            String currentDate = now();

            //Get a Statement object
            stmt = con.createStatement();
            String sqlstatement = "insert into sessiontracking(str_SIP_URI_from, str_SIP_URI_to, str_sipmethod, str_date, str_status, str_url) values('" + from + "', '" + to + "', '" + sipmethod + "', '" + currentDate + "', '" + tranxstatus + "', '" + referredurl + "')";
            //String sqlstatement = "insert into sessiontracking(str_SIP_URI_from, str_SIP_URI_to, str_sipmethod, str_date, str_status) values('" + from + "', '" + to + "', '" + sipmethod + "', '" + currentDate + "', '" + tranxstatus + "')";
            stmt.executeUpdate(sqlstatement);

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }//end catch
    }//end main
}//end sessiontracking

