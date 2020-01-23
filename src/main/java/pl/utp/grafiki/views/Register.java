package pl.utp.grafiki.views;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Route(value = Register.ROUTE)
@PageTitle("Zarejestruj się")
public class Register extends VerticalLayout{
	
	public static final String ROUTE = "register";
	
	UserRepo userRepo;
	PasswordEncoder passwordEncoder;
	
	TextField name;
	TextField surname;
	TextField email;
	TextField username;
	PasswordField password;
	ComboBox<User> managersSelection;
	Button register;
	Label account;
	Button logIn;
	Binder<User> binder;
	
	@Autowired
	public Register(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		makeFields();
	}
	
	@PostConstruct
	private void afterConstruct() {
		setManagers();
		makeButtonRegister();
	}
	
	private void makeFields() {
		this.binder = new Binder<>(User.class);
		
		this.name = new TextField("Podaj imię:");
		name.setRequired(true);
		Label nameStatus = new Label();
		nameStatus.getStyle().set("color", "Red");
		binder.forField(name)
			.asRequired()
			.withValidator(name1-> name1.length()>=3, "Imię musi posiadać przynajmniej 3 znaki.")
			.withValidationStatusHandler(status->{
				nameStatus.setText(status.getMessage().orElse(""));
		        nameStatus.setVisible(status.isError());
		})
		.bind(User::getName, User::setName);
		
		
		this.surname = new TextField("Podaj nazwisko:");
		surname.setRequired(true);
		Label surnameStatus = new Label();
		surnameStatus.getStyle().set("color", "Red");
		binder.forField(surname)
			.asRequired()
			.withValidator(name1-> name1.length()>=2, "Nazwisko musi posiadać przynajmniej 2 znaki.")
			.withValidationStatusHandler(status->{
				surnameStatus.setText(status
		                .getMessage().orElse(""));
				surnameStatus.setVisible(status.isError());
		})
		.bind(User::getSurname, User::setSurname);
		
		this.email = new TextField("Podaj email:");
		email.setRequired(true);
		Label emailStatus = new Label();
		emailStatus.getStyle().set("color", "Red");
		binder.forField(email)
			.asRequired()
			.withValidator(name1-> checkEmail(), "Email niepoprawny.")
			.withValidationStatusHandler(status->{
				emailStatus.setText(status
		                .getMessage().orElse(""));
				emailStatus.setVisible(status.isError());
		})
		.bind(User::getEmail, User::setEmail);
		
		this.username = new TextField("Podaj login:");
		username.setRequired(true);
		Label usernameStatus = new Label();
		usernameStatus.getStyle().set("color", "Red");
		binder.forField(username)
			.asRequired()
			.withValidator(name1-> name1.length()>=6, "Nazwa użytkownika musi posiadać przynajmniej 6 znaków.")
			.withValidationStatusHandler(status->{
				usernameStatus.setText(status
		                .getMessage().orElse(""));
				usernameStatus.setVisible(status.isError());
		})
		.bind(User::getUsername, User::setUsername);
		
		this.password = new PasswordField("Podaj hasło:");
		password.setRequired(true);
		Label passwordStatus = new Label();
		passwordStatus.getStyle().set("color", "Red");
		binder.forField(password)
			.asRequired()
			.withValidator(name1-> checkPassword(), "Hasło musi posiadać przynajmniej 8 znaków i nie więcej niż 20. Musi posiadać przynajmniej jedną cyfrę,"
					+ "jedną małą literę, jedną wielką literę, jeden symbol specjalny [@#$%].")
			.withValidationStatusHandler(status->{
				passwordStatus.setText(status
		                .getMessage().orElse(""));
				passwordStatus.setVisible(status.isError());
		})
		.bind(User::getUsername, User::setUsername);
		
		this.managersSelection = new ComboBox<>("Wybierz swojego kierownika:");
		this.register = new Button("Zarejestruj się");
		this.register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.account = new Label("Masz już konto?");
		this.logIn = new Button("Zaloguj się");
		logIn.addClickListener( e-> {
			logIn.getUI().ifPresent(ui -> ui.navigate("login"));
		});
		this.add(name, nameStatus, surname, surnameStatus, email, emailStatus, username, usernameStatus, password, passwordStatus, managersSelection, register, account, logIn);
		
		binder.setBean(new User());
	}
	
	private List<User> getManagers(){
		return userRepo.findByRoles("ROLE_ADMIN,ROLE_USER");
	}
	
	private void setManagers() {
		managersSelection.setItems(getManagers());
		managersSelection.setItemLabelGenerator(execution -> execution.getName()+" "+execution.getSurname());
	}
	
	@Transactional
	private void registerUser(User user) {
			userRepo.save(user);
	}
	
	private User makeUserFromFields() {
		if(managersSelection.equals(null)) {
			return new User(name.getValue(), surname.getValue(), email.getValue(), username.getValue(), (passwordEncoder.encode(password.getValue())), "ROLE_USER", true);
		}else {
			return new User(name.getValue(), surname.getValue(), email.getValue(), username.getValue(), (passwordEncoder.encode(password.getValue())), "ROLE_USER", true, managersSelection.getValue());
		}
	}
	
	private void makeButtonRegister() {
		this.register.addClickListener(e->{
			if(checkIfFieldIsCorrect()) {
				if(checkIfUsernameUsed(makeUserFromFields())==true) {
					Notification.show("Nazwa użytkownika zajęta, wybierz inną");
				}else {
					registerUser(makeUserFromFields());
					clearElements();
					Notification.show("Konto zostało utworzone. Możesz się zalogować.");
				}
			}else {
				Notification.show("Formularz został wypełniony niepoprawnie. Popraw błędy.");
			}
			
		});
	}
	private Boolean checkIfUsernameUsed(User user) {
		if(userRepo.findByUsername(user.getUsername()).isPresent()) {
			return true;
		}
		return false;
	}
	
	private void clearElements() {
		this.name.clear();
		this.surname.clear();
		this.email.clear();
		this.username.clear();
		this.password.clear();
		this.managersSelection.clear();
	}
	
	private Boolean checkIfFieldIsCorrect() {
		if(this.binder.isValid()) {
			return true;
		}
		return false;
	}
	
	private Boolean checkPassword() {
		Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})");
		String pass = this.password.getValue();
		Matcher matcher = pattern.matcher(pass);
		return matcher.matches();
	}
	
	private Boolean checkEmail() {
		Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
		String email = this.email.getValue();
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
