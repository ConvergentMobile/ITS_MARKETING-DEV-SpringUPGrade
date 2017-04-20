package user;

import java.io.Serializable;

public class Login implements Serializable {
	private Long loginId;
	private String username;
	private String password;
	private Long userId;
	private Integer siteId;
	private String network;
	private String networkId;
	
	public Login() {
		
	}
	
	public Login(String username, String password, Long userId, Integer siteId) {
		super();
		this.username = username;
		this.password = password;
		this.userId = userId;
		this.siteId = siteId;
	}
	public Long getLoginId() {
		return loginId;
	}
	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
}
