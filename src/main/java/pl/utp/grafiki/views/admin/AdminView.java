package pl.utp.grafiki.views.admin;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Route(value="admin")
@Secured("ROLE_ADMIN")
public class AdminView extends AdminMenu {
	
	public static final String ROUTE = "admin";
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	VerticalLayout root;
	UserRepo userRepo;

	@Autowired
    public AdminView(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    
    @PostConstruct
    public void show() {
    	User user = userRepo.findByUsername(currentPrincipalName).get();
    	root = new VerticalLayout(new Label("Cześć, "+user.getName()+"! Wybierz co chcesz zrobić."));
    	this.add(root);
    }

}