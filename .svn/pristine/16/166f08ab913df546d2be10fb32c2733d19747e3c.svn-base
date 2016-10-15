package springsecurity;

import javax.naming.ldap.InitialLdapContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import pojo.Account;
import pojobase.interfaces.AccountBase;


/**
 * Custom Spring Security LDAP authenticator which tries to bind to an LDAP
 * server using the passed-in credentials; does <strong>not</strong> require
 * "master" credentials for an initial bind prior to searching for the passed-in
 * username.
 * 
 * @author Jason
 */
public class LdapAuthenticatorImpl implements LdapAuthenticator {

	private DefaultSpringSecurityContextSource contextFactory;
	private String principalPrefix = "";
	@Autowired
	private AccountBase accountBase;	

	public DirContextOperations authenticate(Authentication authentication) {

		// Grab the username and password out of the authentication object.
		// String principal = principalPrefix + authentication.getName();
		String principal = authentication.getName() + "@vms.com.vn";
		String password = "";
		if (authentication.getCredentials() != null) {
			password = authentication.getCredentials().toString();
		}

		// If we have a valid username and password, try to authenticate.
		if (!("".equals(principal.trim())) && !("".equals(password.trim()))) {
			try {
				InitialLdapContext ldapContext = (InitialLdapContext) contextFactory
						.getContext(principal, password);

				Account account = accountBase.get(authentication.getName());
				
				if (account == null) {					
					accountBase.add(authentication.getName());
				}
				DirContextOperations authAdapter = new DirContextAdapter();
				authAdapter.addAttributeValue("ldapContext", ldapContext);

				return authAdapter;
			} catch (Exception e) {
				throw new BadCredentialsException("login error " + e.toString());
			}
		} else {
			throw new BadCredentialsException("Blank username and/or password!");
		}
	}

	/**
	 * Since the InitialLdapContext that's stored as a property of an
	 * LdapAuthenticationToken is transient (because it isn't Serializable), we
	 * need some way to recreate the InitialLdapContext if it's null (e.g., if
	 * the LdapAuthenticationToken has been serialized and deserialized). This
	 * is that mechanism.
	 * 
	 * @param authenticator
	 *            the LdapAuthenticator instance from your application's context
	 * @param auth
	 *            the LdapAuthenticationToken in which to recreate the
	 *            InitialLdapContext
	 * @return
	 */
	static public InitialLdapContext recreateLdapContext(
			LdapAuthenticator authenticator, LdapAuthenticationToken auth) {
		DirContextOperations authAdapter = authenticator.authenticate(auth);
		InitialLdapContext context = (InitialLdapContext) authAdapter
				.getObjectAttribute("ldapContext");
		auth.setContext(context);
		return context;
	}

	public DefaultSpringSecurityContextSource getContextFactory() {
		return contextFactory;
	}

	/**
	 * Set the context factory to use for generating a new LDAP context.
	 * 
	 * @param contextFactory
	 */
	public void setContextFactory(
			DefaultSpringSecurityContextSource contextFactory) {
		this.contextFactory = contextFactory;
	}

	public String getPrincipalPrefix() {
		return principalPrefix;
	}

	/**
	 * Set the string to be prepended to all principal names prior to attempting
	 * authentication against the LDAP server. (For example, if the Active
	 * Directory wants the domain-name-plus backslash prepended, use this.)
	 * 
	 * @param principalPrefix
	 */
	public void setPrincipalPrefix(String principalPrefix) {
		if (principalPrefix != null) {
			this.principalPrefix = principalPrefix;
		} else {
			this.principalPrefix = "";
		}
	}

}
