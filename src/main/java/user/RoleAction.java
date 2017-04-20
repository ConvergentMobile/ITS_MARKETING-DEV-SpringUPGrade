package user;

import java.io.Serializable;

public class RoleAction implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String roleAction;
	protected String roleActionUrl;
	protected String roleType;
	
	public RoleAction() {
		
	}

	public RoleAction(String roleAction, String roleActionUrl) {
		this(roleAction, roleActionUrl, null);
	}
	
	public RoleAction(String roleAction, String roleActionUrl, String roleType) {
		super();
		this.roleAction = roleAction;
		this.roleActionUrl = roleActionUrl;
		this.roleType = roleType;
	}

	public String getRoleAction() {
		return roleAction;
	}

	public void setRoleAction(String roleAction) {
		this.roleAction = roleAction;
	}

	public String getRoleActionUrl() {
		return roleActionUrl;
	}

	public void setRoleActionUrl(String roleActionUrl) {
		this.roleActionUrl = roleActionUrl;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

}
