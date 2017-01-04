<%@ page import="java.util.List" %>
<%@ page import="com.theah64.scd.models.User" %>
<%@ page import="com.theah64.scd.database.tables.Users" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 8/12/16
  Time: 6:36 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>API Status</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body><%--
final String id = rs.getString(COLUMN_ID);
final String name = rs.getString(COLUMN_NAME);
final String imei = rs.getString(COLUMN_IMEI);
final String email = rs.getString(COLUMN_EMAIL);
final long totalRequests = rs.getLong(COLUMN_AS_TOTAL_REQUESTS);
final long totalDownloads = rs.getLong(COLUMN_AS_TOTAL_DOWNLOADS);
final long totalTracks = rs.getLong(COLUMN_AS_TOTAL_TRACKS);
final String lastHit = rs.getString(COLUMN_AS_LAST_HIT);
final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);--%>
<div class="container">
    <h2>SoundCloudDownloader statistics</h2>
    <p>Shows live status of the soundclouddownloader.</p>
    <table class="table table-striped table-bordered table-hover">
        <thead>
        <tr>
            <td>ID</td>
            <td>Name</td>
            <td>IMEI</td>
            <td>EMail</td>
            <td>Total requests</td>
            <td>Total downloads</td>
            <td>Total tracks</td>
            <td>Last hit</td>
            <td>isActive</td>
        </tr>
        </thead>
        <tbody>

        <%
            final List<User> users = Users.getInstance().getAll();
            if (users != null) {
                for (final User user : users) {
        %>
        <tr>
            <td><%=user.getId()%>
            </td>
            <td><%=user.getName()%>
            </td>
            <td><%=user.getIMEI()%>
            </td>
            <td><%=user.getEmail()%>
            </td>
            <td><%=user.getTotalRequests()%>
            </td>
            <td><%=user.getTotalDownloads()%>
            </td>
            <td><%=user.getTotalTracks()%>
            </td>
            <td><a target="_blank" href="<%=user.getLastHit()%>">Click here</a>
            </td>
            <td><%=user.isActive() ? "YES" : "NO"%>
            </td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>

</div>
</body>
</html>
