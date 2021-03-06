package CSCI5308.GroupFormationTool.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import CSCI5308.GroupFormationTool.SystemConfig;
import CSCI5308.GroupFormationTool.AccessControl.*;

public class CustomAuthenticationManager implements AuthenticationManager
{
	private Logger log = Logger.getLogger(CustomAuthenticationManager.class.getName());
	private static final String ADMIN_BANNER_ID = "B-000000";
	
	private Authentication checkAdmin(String password, User u, Authentication authentication) throws AuthenticationException
	{
		if (password.equals(u.getPassword()))
		{
			List<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
			rights.add(new SimpleGrantedAuthority("ADMIN"));
			UsernamePasswordAuthenticationToken token;
			token = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
																			authentication.getCredentials(),
																			rights);
			return token;
		}
		else
		{
			log.log(Level.SEVERE, "Encountered Invalid Credentials Exception");
			throw new BadCredentialsException("1000");
		}
	}
	
	private Authentication checkNormal(String password, User u, Authentication authentication) throws AuthenticationException
	{
		IPasswordEncryption passwordEncryption = SystemConfig.instance().getPasswordEncryption();
		if (passwordEncryption.matches(password, u.getPassword()))
		{
			List<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
			rights.add(new SimpleGrantedAuthority("USER"));
			UsernamePasswordAuthenticationToken token;
			token = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
																			authentication.getCredentials(),
																			rights);
			log.log(Level.INFO, "Token generated for the user " + u.getBannerID());
			return token;
		}
		else
		{
			log.log(Level.SEVERE, "Encountered Invalid Credentials Exception");
			throw new BadCredentialsException("1000");
		}
	}
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{
		String bannerID = authentication.getPrincipal().toString();
		String password = authentication.getCredentials().toString();
		IUserPersistence userDB = SystemConfig.instance().getUserDB();
		User u;
		try
		{
			u = new User(bannerID, userDB);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Encountered Authentication Service Exception while authenticating the user");
			throw new AuthenticationServiceException("1000");
		}
		if (u.isValidUser())
		{
			if (bannerID.toUpperCase().equals(ADMIN_BANNER_ID))
			{
				return checkAdmin(password, u, authentication);
			}
			else
			{
				return checkNormal(password, u, authentication);
			}
		}
		else
		{
			log.log(Level.SEVERE, "Encountered Invalid Credentials Exception");
			throw new BadCredentialsException("1001");
		}			
	}
}
