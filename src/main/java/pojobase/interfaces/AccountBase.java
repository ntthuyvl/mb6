package pojobase.interfaces;

import java.util.List;
import pojo.Account;

/**
 * 
 * @author Nabeel Ali Memon
 * 
 */

public interface AccountBase {
	public Account get(String name);

	public List<String> getRoleList(String name);

	void add(String user_name) throws Exception;
}
