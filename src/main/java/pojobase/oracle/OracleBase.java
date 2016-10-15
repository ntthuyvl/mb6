package pojobase.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

@Service
@Transactional
public class OracleBase {

	// private org.apache.commons.dbcp.BasicDataSource dataSource;
	private static org.apache.tomcat.jdbc.pool.DataSource dataSource;
	public static boolean debug = true;
	public static String ct = "6";

	public static void syslog(String s) {
		if (debug)
			System.out.println(s);
	}

	void clean(Connection conn, PreparedStatement prpStm, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
		}
		try {
			prpStm = conn.prepareStatement(
					"	BEGIN		COMMIT; 	EXECUTE IMMEDIATE 'ALTER SESSION CLOSE DATABASE LINK gold';"
							+ "	EXCEPTION		WHEN OTHERS		THEN			NULL;	END;");
			prpStm.execute();
		} catch (SQLException e) {
		}
		try {
			prpStm.close();
		} catch (SQLException e) {
		}
		try {
			conn.commit();
			conn.close();
		} catch (SQLException e) {
		}
		// System.out.println("resource has cleaned");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	List<Map<String, String>> getError(Exception e) {
		// TODO Auto-generated catch block
		List result = new ArrayList<Map<String, String>>();
		Map error = new HashMap<String, String>();
		error.put("error", e.toString());
		result.add(error);
		e.printStackTrace();
		return result;
	}

	@Transactional
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Autowired
	public OracleBase(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
