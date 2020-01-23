package pl.utp.grafiki.views.user;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.annotations.Widgetset;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


@SuppressWarnings("serial")
@Secured("ROLE_USER")
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class UserMenu extends VerticalLayout {

	Button saveSchedule;
	Button showSchedule;
	Button logout;
	HorizontalLayout root = new HorizontalLayout();
	
	public UserMenu() {
		this.add(root);
		makeMenu();
	}
	
	private void makeMenu() {
		saveSchedule = new Button("Zapisz grafik");
		saveSchedule.addClickListener( e-> {
			saveSchedule.getUI().ifPresent(ui -> ui.navigate("saveUserSchedule"));
		});
		showSchedule = new Button("Pokaż grafik");
		showSchedule.addClickListener(e->{
			showSchedule.getUI().ifPresent(ui -> ui.navigate("showUserSchedule"));
		});
		logout = new Button("Wyloguj się");
		logout.addClickListener(event1 -> {
			SecurityContextHolder.clearContext();
			UI.getCurrent().getSession().close();
		    UI.getCurrent().getPage().reload();
			});
		root.add(saveSchedule, showSchedule, logout);
	}
	
}
