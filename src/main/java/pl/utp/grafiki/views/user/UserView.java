package pl.utp.grafiki.views.user;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;

import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;


@SuppressWarnings("serial")
@Route(value="user")
@Secured("ROLE_USER")
public class UserView extends UserMenu
{
	
	UserRepo userRepo;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	User user;
	
	@Autowired
	public UserView(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@PostConstruct
	public void show() {
		this.user = userRepo.findByUsername(currentPrincipalName).get();
		showProfile();
	}
	
	public void showProfile() {
		this.add(new Label("Twój profil:"));
		this.add(new Label("Twoje imię: "+user.getName()));
		this.add(new Label("Twoje nazwisko: "+user.getSurname()));
		this.add(new Label("Twoja nazwa użytkownika: "+user.getUsername()));
		this.add(new Label("Twój email: "+user.getEmail()));
		try {
			this.add(new Label("Twój kierownik: "+user.getManager().getName()+" "+user.getManager().getSurname()));
		}catch(NullPointerException e) {
			this.add(new Label("Twój kierownik: brak."));
			this.add(new Label("Jeśli jesteś kierownikiem skontaktuj się z administratorem systemu."));
			this.add(new Label("Jeśli jesteś konsultantem skontaktuj się z kierownikiem."));
		}
	}
	
}
