package pl.utp.grafiki.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("serial")
@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Zaloguj się")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
	public static final String ROUTE = "login";

	private LoginForm login = new LoginForm(createPolishI18n());
	
	UserRepo userRepo;
	PasswordEncoder passwordEncoder;
	VerticalLayout root;
	
	@Autowired
	public LoginView(UserRepo userRepo, PasswordEncoder passwordEncoder){
		login.setAction("login");
		login.setForgotPasswordButtonVisible(false);
		
		Button rejestracja = new Button("Zarejestruj się");
		rejestracja.addClickListener( e-> {
			rejestracja.getUI().ifPresent(ui -> ui.navigate("register"));
		});
		//getElement().appendChild(login.getElement());
		//getElement().appendChild(new Label("Nie masz konta?").getElement());
		//getElement().appendChild(rejestracja.getElement());
		
		root = new VerticalLayout(login, new HorizontalLayout(new Label("Nie masz konta?"), rejestracja));
		getElement().appendChild(root.getElement());
		
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostConstruct
	private void createSuperUser() {
		if(!userRepo.findByUsername("Superuser").isPresent()) {
			userRepo.save(new User("Paweł", "Orlikowski", "porlikowski@greatcall.pl", "Superuser", passwordEncoder.encode("B0813aran()16"), "ROLE_SUPERUSER,ROLE_ADMIN,ROLE_USER", true));
		}
	}
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
			login.setError(true);
		}
	}
	
	private LoginI18n createPolishI18n() {
	    final LoginI18n i18n = LoginI18n.createDefault();

	    i18n.setHeader(new LoginI18n.Header());
	    i18n.getHeader().setTitle("Zaloguj się na swoje konto");
	    i18n.getForm().setUsername("Użytkownik:");
	    i18n.getForm().setTitle("Zaloguj się:");
	    i18n.getForm().setSubmit("Zaloguj");
	    i18n.getForm().setPassword("Hasło:");
	    i18n.getErrorMessage().setTitle("Użytkownik/hasło nieprawidłowe");
	    i18n.getErrorMessage()
	        .setMessage("Sprawdź nazwę użytkownika i hasło, i spróbuj ponownie");
	    return i18n;
	}
}
