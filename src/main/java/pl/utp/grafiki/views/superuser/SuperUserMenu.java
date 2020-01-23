package pl.utp.grafiki.views.superuser;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("serial")
@Secured("ROLE_SUPERUSER")
public class SuperUserMenu extends VerticalLayout{

	Button showConsultants;
	Button editSchedules;
	Button logout;
	Button editRoles;
	Button editActivity;
	Button editManager;
	Button setPassword;
	HorizontalLayout root = new HorizontalLayout();
	
	public SuperUserMenu() {
		add(root);
		showMenu();
	}
	
	private void showMenu() {
		showConsultants = new Button("Edytuj użytkowników");
		showConsultants.addClickListener(click->{
			showConsultants.getUI().ifPresent(ui -> ui.navigate("showSuperUserConsultants"));
		});
		editRoles = new Button("Edytuj uprawnienia");
		editRoles.addClickListener(click->{
			editRoles.getUI().ifPresent(ui -> ui.navigate("editRoles"));
		});
		editActivity = new Button("Edytuj aktywność");
		editActivity.addClickListener(click->{
			editActivity.getUI().ifPresent(ui -> ui.navigate("editActivity"));
		});
		editManager = new Button("Edytuj kierownika");
		editManager.addClickListener(click->{
			editManager.getUI().ifPresent(ui -> ui.navigate("editManager"));
		});
		setPassword = new Button("Ustaw nowe hasło");
		setPassword.addClickListener(click->{
			setPassword.getUI().ifPresent(ui -> ui.navigate("setNewPassword"));
		});
		editSchedules = new Button("Edytuj grafiki");
		editSchedules.addClickListener(click->{
			editSchedules.getUI().ifPresent(ui -> ui.navigate("showSuperUserSchedules"));
		});
		logout = new Button("Wyloguj się");
		logout.addClickListener(event1 -> {
			SecurityContextHolder.clearContext();
			UI.getCurrent().getSession().close();
		    UI.getCurrent().getPage().reload();
			});
		root.add(showConsultants, editSchedules, editRoles, editActivity, editManager, setPassword, logout);
	}
}
