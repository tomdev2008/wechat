<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.json.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.lang.*"%>
<%
 	Connection con = null;
	PreparedStatement psmt = null;
	ResultSet rs = null;
	try {
		con = com.glaf.core.jdbc.DBConnectionFactory.getConnection();
		con.setAutoCommit(false);
		psmt = con.prepareStatement(" drop table Test_ACT  ");
		psmt.executeUpdate();
		psmt.close();
		con.commit();
		con.setAutoCommit(true);
    } catch (Exception ex) {
		ex.printStackTrace();
	} finally {
		com.glaf.core.util.JdbcUtils.close(rs);
		com.glaf.core.util.JdbcUtils.close(psmt);
		com.glaf.core.util.JdbcUtils.close(con);
	}
	try {
			Thread.sleep(2000);
			con = com.glaf.core.jdbc.DBConnectionFactory.getConnection();
			con.setAutoCommit(false);
			psmt = con.prepareStatement(" CREATE TABLE Test_ACT (ID_ VARCHAR(50) NOT NULL,PROCESSNAME_ VARCHAR(50),PROCESSINSTANCEID_ VARCHAR(50),STATUS_ INTEGER,WFSTATUS_ INTEGER,PRIMARY KEY(ID_))  ");
			psmt.executeUpdate();
			psmt.close();
			con.commit();
		    con.setAutoCommit(true);
        } catch (Exception ex) {
		   ex.printStackTrace();
		} finally {
			com.glaf.core.util.JdbcUtils.close(rs);
			com.glaf.core.util.JdbcUtils.close(psmt);
			com.glaf.core.util.JdbcUtils.close(con);
		}

		try{
			con = com.glaf.core.jdbc.DBConnectionFactory.getConnection();
			con.setAutoCommit(false);
			for(int i=0; i<100; i++){
				psmt = con.prepareStatement(" insert into Test_ACT(ID_) values(?) ");
				psmt.setInt(1, i+1);
				psmt.executeUpdate();
				psmt.close();
			}
			con.commit();
		    con.setAutoCommit(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			com.glaf.core.util.JdbcUtils.close(rs);
			com.glaf.core.util.JdbcUtils.close(psmt);
			com.glaf.core.util.JdbcUtils.close(con);
		}
%>
<script type="text/javascript">
     location.href="index.jsp";
</script>