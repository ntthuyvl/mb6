/**
 * 
 */
package springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import pojobase.interfaces.AccountBase;

/**
 * Extends User with a salt property.
 * 
 * @author Mularien
 */
public class HibernateSaltSource implements SaltSource {
	@Autowired
	private AccountBase accountBase;

	public Object getSalt(UserDetails account) {
		try {
			return accountBase.get(account.getUsername()).getSalt();
		} catch (Exception e) {
			return null;
		}
	}
}
