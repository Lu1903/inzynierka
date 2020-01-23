package pl.utp.grafiki.views.admin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.utp.grafiki.domain.AdminHelper;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Secured("ROLE_ADMIN")
@PageTitle("Edytuj aktywność")
@Route("editActivityAdmin")
public class EditAdminActivity extends AdminMenu {

	User openedUser;
	List<User> users;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	UserRepo userRepo;
	Checkbox activity;
	
	@Autowired
	public EditAdminActivity(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@PostConstruct
	public void show() {
		setGrid();
	}
	
	public void setGrid() {
		this.users = AdminHelper.getUsers(currentPrincipalName, userRepo, false);
		Grid<User> uusers = new Grid<>();
		Editor<User> editor = uusers.getEditor();
		Binder<User> binder = new Binder<>(User.class);
		editor.setBinder(binder);
		editor.setBuffered(true);
		uusers.setItems(users);
		Grid.Column<User> nameColumn = uusers.addColumn(User::getName).setHeader("Imię");
		Grid.Column<User> surnameColumn = uusers.addColumn(User::getSurname).setHeader("Nazwisko");
		Grid.Column<User> roles = uusers.addComponentColumn(person -> {
   		this.activity = new Checkbox("AKTYWNY", person.isActive());
   	        return new HorizontalLayout(activity);
		});
		roles.setVisible(false);
		
		Collection<Button> editButtons = Collections
		        .newSetFromMap(new WeakHashMap<>());

		Grid.Column<User> editorColumn = uusers.addComponentColumn(person -> {
		    Button edit = new Button("Edytuj");
		    edit.addClassName("edit");
		    edit.addClickListener(e -> {
		    	
		    	editor.editItem(person);
		    	this.openedUser = person;
		    });
		    edit.setEnabled(!editor.isOpen());
		    editButtons.add(edit);
		    return edit;
		});
		
		editor.addOpenListener(e -> {
			editButtons.stream()
	        .forEach(button -> button.setEnabled(!editor.isOpen()));
			roles.setVisible(editor.isOpen());
		});
		editor.addCloseListener(e -> {
			editButtons.stream()
		    .forEach(button -> button.setEnabled(!editor.isOpen()));
			roles.setVisible(editor.isOpen());
		});

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
			editActivity(activity, openedUser.getId());
			editor.refresh();
			editor.cancel();
			UI.getCurrent().getPage().reload();
		});
		this.add(uusers);
	}
	
	@Transactional
	private void editActivity(Checkbox activity, long id) {
		userRepo.updateUserActivity(activity.getValue(), id);
	}
}