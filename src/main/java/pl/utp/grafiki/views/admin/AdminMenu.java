package pl.utp.grafiki.views.admin;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("serial")
@Secured("ROLE_ADMIN")
public class AdminMenu extends VerticalLayout{
	
	Button showConsultants;
	Button editSchedules;
	Button editActivity;
	Button logout;
	Button editPassword;
	HorizontalLayout root = new HorizontalLayout();
	
	public AdminMenu() {
		add(root);
		showMenu();
	}
	
	private void showMenu() {
		showConsultants = new Button("Edytuj konsultantów");
		showConsultants.addClickListener(click->{
			showConsultants.getUI().ifPresent(ui -> ui.navigate("showAdminConsultants"));
		});
		editSchedules = new Button("Edytuj grafiki");
		editSchedules.addClickListener(click->{
			editSchedules.getUI().ifPresent(ui -> ui.navigate("showAdminSchedules"));
		});
		editActivity = new Button("Edytuj aktywność");
		editActivity.addClickListener(click->{
			editActivity.getUI().ifPresent(ui -> ui.navigate("editActivityAdmin"));
		});
		editPassword = new Button("Edytuj hasło");
		editPassword.addClickListener(click->{
			editPassword.getUI().ifPresent(ui -> ui.navigate("setNewPasswordAdmin"));
		});
		logout = new Button("Wyloguj się");
		logout.addClickListener(event1 -> {
			SecurityContextHolder.clearContext();
			UI.getCurrent().getSession().close();
		    UI.getCurrent().getPage().reload();
			});
		root.add(showConsultants, editSchedules, editActivity, editPassword, logout);
	}

}
