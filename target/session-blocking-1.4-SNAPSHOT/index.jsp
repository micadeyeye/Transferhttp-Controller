<%@page import="org.mobicents.servlet.sip.example.*, java.sql.*, java.util.Calendar"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>TransferHTTP Controller - Home</title>
<link href="layout.css" rel="stylesheet" type="text/css" />
</head>

<body><table width="100%" border="0" align="center" cellpadding="5" cellspacing="0" class="maintable">
      <tr>
        <td height="114" align="center"><img name="Banner" src="Images/Banner.gif" width="606" height="96" border="0" id="Banner" alt="" /></td>
      </tr>
      <tr bgcolor="#D5D5D5">
        <td align="center" bgcolor="#B2C1FF" class="trline"><table border="0" width="100% cellspacing="0"  cellpadding="0"><tr><td align="left">
Welcome Michael Adeyeye <font>&lt;</font><%out.println(sessiontracking.usersipaddr);%><font>&gt;</font></td><td align="right"><a href="index.jsp?cmd=logout">log out</a></td></tr></table></td>
  </tr>
   <tr align="left" bgcolor="#D5D5D5">
                <td bgcolor="#B2C1FF" class="trlinebase"><table width="100%" border="0" cellspacing="0" cellpadding="0">
 <tr><td align="center"><a href="myaccount.jsp">My Account</a></td><td align="center"><strong><a href="sessiontracking.jsp">Session Tracking and Pickup</a></strong></td><td align="right"><strong>Search By</strong>
<select type="select-one" name="searchselect">
<option>SIP URI (From)</option>
<option>SIP URI (To)</option>
<option>SIP Method</option>
<option>Date</option>
<option>Referred URL</option>
</select>
<input type="text" name="searchfield" size="30"><input type="button" name="searchbutton" value="Go">
</td></tr></table>
</td>
  </tr>
      <tr>
        <td><table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td>
</td>
  </tr>
  <tr>
    <td>        
  <%
 try{
            Statement stmt;
            ResultSet rs;
            Class.forName(
               "com.mysql.jdbc.Driver");
            String url =
                    "jdbc:mysql://localhost:3306/messageblocking";
            Connection con =
                    DriverManager.getConnection(
                    url, "mike", "mk4nd");
            //Get a Statement object
            stmt  = con.createStatement();
            rs  = stmt.executeQuery("select * from sessiontracking");

            out.println ("<table border=\"1\" class=\"userdetails\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td align=\"center\"><strong>ID</strong></td><td align=\"center\"><strong>SIP URI (From)</strong></td><td align=\"center\"><strong>SIP URI (To)</strong></td><td align=\"center\"><strong>SIP Method</strong></td><td align=\"center\"><strong>Date</strong></td><td align=\"center\"><strong>Status</strong></td><td align=\"center\"><strong>Referred URL</strong></td></tr>");
            while(rs  .next()){
               int the_id= rs.getInt("int_id");
                String the_from = rs.getString("str_SIP_URI_from");
                String the_to = rs.getString("str_SIP_URI_to");
                String the_date = rs.getString("str_date");
                String the_sipmethod = rs.getString("str_sipmethod");
                String the_status = rs.getString("str_status");
                String the_url = rs.getString("str_url");
                out.println("<tr><td align=\"center\">" + the_id + "</td><td>" + the_from + "</td><td>" + the_to + "</td><td>" + the_sipmethod +
                        "</td><td>" + the_date + "</td><td>" + the_status + "</td><td><a href=\"send?to=" + sessiontracking.usersipaddr + "\">" + the_url + "</a></td></tr>");
            }//end while loop
            out.println ("</tr></table>");

            con.close ();
        }
        catch(Exception e) {
            e.printStackTrace();
        }//end catch
           %>
</td>
  </tr>
</table>
</td>
      </tr>
<tr><td align="center" class ="footer">&copy;<% 
Calendar cal = Calendar.getInstance();
 out.println(cal.get(Calendar.YEAR));  %> UCT CRG LAB<br /> 
      Contact: Michael Adeyeye {micadeyeye@gmail.com}</td>
  </tr>
    </table>

</body>
</html>
