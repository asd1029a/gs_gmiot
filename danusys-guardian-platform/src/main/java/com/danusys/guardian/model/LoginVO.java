package com.danusys.guardian.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
public class LoginVO implements UserDetails {
	private String id;
	private String pwd;
	private int loginFailCount;
	private Date lastPwdUpdateDate;
	private String name;
	private String birthday;
	private String phoneNo;
	private String email;
	private String authority;
	private String mediaAuthority;
	private String authorityOld;
	private String address;
	private Date joinDate;
	private Date leaveDate;
	private Date createDate;
	private String deleted;
	private String deleteId;
	private Date deleteDate;
	private String strLoginFailCount;
	private String strLastPwdUpdateDate;
	private String strJoinDate;
	private String strLeaveDate;
	private String strCreateDate;
	private String strDeleteDate;
	private int Ecnt;
	private String flag;
	private String key;
	private String deptmnt;
	private String rank;
	private String orgPwd;
	private ArrayList<String> seqNoList;
	private String seqNo;
	private String sessionId;
	private String rowPerPage;
	private String USER_NM_KO;
	private String USER_NM_EN;
	private String USE_TY_CD;
	private String GRP_ID;
	private String GRP_NM_KO;
	private String AUTH_LVL_NM;
	private int authLvl;
	
	private int grpLvl;
	private int grpId;
	private String vmsSvrIp;
	
	//DISPLAY_SET session
	private String loginBgTit;
	private String loginBgTitSub;
	private String loginBg;
	private String introTit;
	private String introBgTit;
	private String introBgTitSub;
	private String introBg1;
	private String introBg2;
	private String introBg3;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		auth.add(new SimpleGrantedAuthority(authority.toUpperCase()));
		return auth;
	}
	@Override
	public String getPassword() {
		return this.pwd;
	}
	@Override
	public String getUsername() {
		return this.id;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
	
    
}
