package pl.utp.grafiki.views.superuser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import pl.utp.grafiki.domain.AdminHelper;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Route("showSuperUserConsultants")
@PageTitle("Edycja użytkowników")
@Secured("ROLE_SUPERUSER")
public class ShowSuperuserConsultants extends SuperUserMenu {

	User openedUser;
	List<User> users;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	UserRepo userRepo;
	
	@Autowired
	public ShowSuperuserConsultants(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@PostConstruct
	private void show() {
		setGrid();
	}

	private void setGrid() {
		this.users = AdminHelper.getUsers(currentPrincipalName, userRepo, false);
		Grid<User> uusers = new Grid<>();
		Editor<User> editor = uusers.getEditor();
		Binder<User> binder = new Binder<>(User.class);
		editor.setBinder(binder);
		editor.setBuffered(true);
		uusers.setItems(users);
		Grid.Column<User> nameColumn = uusers.addColumn(User::getName).setHeader("Imię");
		Grid.Column<User> surnameColumn = uusers.addColumn(User::getSurname).setHeader("Nazwisko");
		Grid.Column<User> usernameColumn = uusers.addColumn(User::getUsername).setHeader("Nazwa użytkownika");
		//Grid.Column<User> passwordColumn = uusers.addColumn(User::getPassword).setHeader("Hasło");
		//Grid.Column<User> rolesColumn = uusers.addColumn(User::getRoles).setHeader("Role");
		//Grid.Column<User> activeColumn = uusers.addColumn(User::isActive).setHeader("Aktywność");
		
		TextField nameField = new TextField();
		binder.forField(nameField).bind("name");
		nameColumn.setEditorComponent(nameField);

		TextField surnameField = new TextField();
		binder.forField(surnameField).bind("surname");
		surnameColumn.setEditorComponent(surnameField);
		
		TextField usernameField = new TextField();
		binder.forField(usernameField).bind("username");
		usernameColumn.setEditorComponent(usernameField);
		
		/*TextField passwordField = new TextField();
		binder.forField(passwordField).bind("password");
		passwordColumn.setEditorComponent(passwordField);*/
		
		/*TextField rolesField = new TextField();
		binder.forField(rolesField).bind("roles");
		rolesColumn.setEditorComponent(rolesField);*/
		
		/*TextField activeField = new TextField();
		binder.forField(activeField).withConverter();
		activeColumn.setEditorComponent(activeField);*/
		

		Collection<Button> editButtons = Collections
		        .newSetFromMap(new WeakHashMap<>());

		Grid.Column<User> editorColumn = uusers.addComponentColumn(person -> {
		    Button edit = new Button("Edytuj");
		    edit.addClassName("edit");
		    edit.addClickListener(e -> {
		    	editor.editItem(person);
		    	nameField.focus();
		    	this.openedUser = person;
		    });
		    edit.setEnabled(!editor.isOpen());
		    editButtons.add(edit);
		    return edit;
		});

		editor.addOpenListener(e -> editButtons.stream()
		        .forEach(button -> button.setEnabled(!editor.isOpen())));
		editor.addCloseListener(e -> editButtons.stream()
		        .forEach(button -> button.setEnabled(!editor.isOpen())));

		Button save = new Button("Zapisz", e -> editor.save());
		save.addClassName("save");

		Button cancel = new Button("Anuluj", e -> editor.cancel());
		cancel.addClassName("cancel");

		// Add a keypress listener that listens for an escape key up event.
		// Note! some browsers return key as Escape and some as Esc
		uusers.getElement().addEventListener("keyup", event -> editor.cancel())
		        .setFilter("event.key === 'Escape' || event.key === 'Esc'");

		Div buttons = new Div(save, cancel);
		editorColumn.setEditorComponent(buttons);

		editor.addSaveListener(event -> {
			updateUser(nameField.getValue(), surnameField.getValue(), usernameField.getValue(), this.openedUser.getId());
			editor.refresh();
			editor.cancel();
		});
		
		this.add(uusers);
		
	}
	void updateUser(String name, String surname, String username, long id) {
		userRepo.updateUserById(name, surname, username, id);
	}
}
