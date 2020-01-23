package pl.utp.grafiki.views.superuser;

import javax.annotation.PostConstruct;

import org.springframework.security.access.annotation.Secured;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route("superuser")
@Secured("ROLE_SUPERUSER")
public class SuperUserView extends SuperUserMenu {

	public SuperUserView() {
		
	}
	
	@PostConstruct
	public void show() {
		add(new Label("Wybierz co chcesz zrobić."));
	}
}
