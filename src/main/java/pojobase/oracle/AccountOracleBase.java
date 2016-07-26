package pojobase.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pojo.Account;
import pojobase.interfaces.AccountBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

@Repository
@Transactional
public class AccountOracleBase extends OracleBase implements AccountBase {

	@Autowired
	public AccountOracleBase(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
		super(dataSource);
		// TODO Auto-generated constructor stub
	}

	@Transactional
	@Override
	public void add(String user_name) throws Exception {
		Connection conn = null;
		PreparedStatement prpStm = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			prpStm = conn.prepareStatement("INSERT INTO thuynt.account(name) VALUES   (?)");
			prpStm.setString(1, user_name);
			prpStm.execute();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@Transactional
	public Account get(String name) {

		Account account = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement prpStm = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement("SELECT name from thuynt.account where name = ?");
			prpStm.setString(1, name);
			rs = prpStm.executeQuery();
			while (rs != null && rs.next()) {
				account = new Account();
				account.setName(name);
			}

			return account;
		} catch (Exception e) {
			return null;
		} finally {
			clean(conn, prpStm, rs);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional
	public List<String> getRoleList(String name) {
		List result = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement prpStm = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			prpStm = conn.prepareStatement("select role from account_action_v where role is not null and name=?");
			prpStm.setString(1, name);
			rs = prpStm.executeQuery();
			while (rs != null && rs.next()) {
				result.add(rs.getString("role"));
			}
			return result;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				clean(conn, prpStm, rs);
			} catch (Exception e) {
			}
		}
	}

}
