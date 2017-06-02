html>
<body>

<%@ page import = "java.sql.*" isThreadSafe="false" %>

<%
  String sDbDrv = "org.postgresql.Driver";
  String sDbUrl = "jdbc:postgresql://localhost:5432/postgres";
  String sUsr   = "postgres";
  String sPwd   = "123";
  String sTable = "";
  String sSql   = "";
  if( request.getParameterNames().hasMoreElements() == true )
  {
    sDbDrv = request.getParameter( "prmDbDrv" );
    sDbUrl = request.getParameter( "prmDbUrl" );
    sUsr   = request.getParameter( "prmUsr" );
    sPwd   = request.getParameter( "prmPwd" );
    sTable = request.getParameter( "prmTab" );
    sSql   = request.getParameter( "prmSql" );
    if( null != sTable && 0 <  sTable.length() &&
       (null == sSql   || 0 == sSql.length())  )
      sSql = "SELECT * FROM " + sTable;
  }
%>

<form method="post"><pre>
Db-Treiber   <input type="text"     name="prmDbDrv" value='<%= sDbDrv %>' size=60><br>
Db-URL       <input type="text"     name="prmDbUrl" value='<%= sDbUrl %>' size=60><br>
Benutzer     <input type="text"     name="prmUsr"   value='<%= sUsr   %>' size=60><br>
Kennwort     <input type="password" name="prmPwd"   value='<%= sPwd   %>' size=60><br>
Tabellenname <input type="text"     name="prmTab"   value='<%= sTable %>' size=60><br>
SQL-Kommando <input type="text"     name="prmSql"   value='<%= sSql   %>' size=60>
             (nach Aenderung anderer Parameter muss SQL-Kommando geloescht werden)<br>
             <input type="submit" name="submit" value="Datenbanktabelle anzeigen">
</pre></form>

<%
  if( request.getParameterNames().hasMoreElements() == true
      && null != sDbDrv && 0 < sDbDrv.length()
      && null != sDbUrl && 0 < sDbUrl.length()
      && null != sSql   && 0 < sSql.length() )
  {
    Connection cn = null;
    Statement  st = null;
    ResultSet  rs = null;
    try {
      Class.forName(sDbDrv);
      cn = DriverManager.getConnection(sDbUrl,sUsr,sPwd);
      st = cn.createStatement();
      rs = st.executeQuery( sSql );
      ResultSetMetaData rsmd = rs.getMetaData();
      int n = rsmd.getColumnCount();
      out.println( "<table border=1 cellspacing=0><tr>" );
      for( int i=1; i<=n; i++ )    // Achtung: erste Spalte mit 1 statt 0
        out.println( "<th>" + rsmd.getColumnName( i ) + "</th>" );
      while( rs.next() )
      {
        out.println( "</tr><tr>" );
        for( int i=1; i<=n; i++ )  // Achtung: erste Spalte mit 1 statt 0
          out.println( "<td>" + rs.getString( i ) + "</td>" );
      }
      out.println( "</tr></table>" );
    } finally {
      try { if( null != rs ) rs.close(); } catch( Exception ex ) {/*ok*/}
      try { if( null != st ) st.close(); } catch( Exception ex ) {/*ok*/}
      try { if( null != cn ) cn.close(); } catch( Exception ex ) {/*ok*/}
    }
  }
%>

</body>
</html>