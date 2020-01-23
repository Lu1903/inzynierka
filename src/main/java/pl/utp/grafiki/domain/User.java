package pl.utp.grafiki.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
@Entity
public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id") @NotNull
	private long id;
	@Column @NotNull 
	private String name;
	@Column @NotNull
	private String surname;
	@Column @NotNull
	private String email;
	@Column(unique=true) @NotNull 
	private String username;
	@Column @NotNull
	private String password;
	@Column @NotNull
	private String roles = "";
	@Column @NotNull
	private boolean active;
	
	@ManyToOne
	protected User manager;
	@OneToMany(mappedBy="manager")
	protected List<User> consultants;
	
	public User() {
		
	}
	
	public User(String name, String surname, String email, String username, String password, String roles, Boolean active) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.active = true;
		this.manager = null;
	}
	
	public User(String name, String surname, String email, String username, String password, String roles, Boolean active, User manager) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.active = true;
		this.manager = manager;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public List<User> getConsultants() {
		return consultants;
	}

	public void setConsultants(List<User> consultants) {
		this.consultants = consultants;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authority = new ArrayList<GrantedAuthority>();
		List<String> auth = new ArrayList<String>();
		if(this.roles.length()>0) {
			auth.addAll(Arrays.asList(this.roles.split(",")));
			auth.forEach(e->{
				authority.add(new SimpleGrantedAuthority(e.toString().toUpperCase()));
			});
		}
		
		return authority;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		if(active) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		if(active) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		if(active) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		if(active) {
			return true;
		}
		return false;
	}
	
}
