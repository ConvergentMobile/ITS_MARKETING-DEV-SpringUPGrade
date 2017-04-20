package security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import user.RoleAction;

//custom class to include role and roleActions which are used to populate the User object in the controller
public class LTSUserDetails extends User {
	private static final long serialVersionUID = 1L;
	
	private String role;
	private List<RoleAction> roleActions;
	
	public LTSUserDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, String role, List<RoleAction> roleActions) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.role = role;
		this.roleActions = roleActions;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<RoleAction> getRoleActions() {
		return roleActions;
	}

	public void setRoleActions(List<RoleAction> roleActions) {
		this.roleActions = roleActions;
	}
	
}
