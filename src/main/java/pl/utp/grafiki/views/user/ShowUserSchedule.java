package pl.utp.grafiki.views.user;

import java.awt.GraphicsEnvironment;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.utp.grafiki.domain.SchedulerHelper;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.DayRepo;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Secured("ROLE_USER")
@Route(value="showUserSchedule")
//@HtmlImport("frontend://style.js")
//@Tag("style")
@HtmlImport("./styles/my-style.js")
@PageTitle("Mój grafik")
public class ShowUserSchedule extends UserMenu {
	
	UserRepo userRepo;
	DayRepo dayRepo;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	User user;
	VerticalLayout root;
	HorizontalLayout buttons;
	FullCalendar calendar;
	String height;
	
	@Autowired
	public ShowUserSchedule(UserRepo userRepo, DayRepo dayRepo) {
		this.userRepo = userRepo;
		this.dayRepo = dayRepo;
		height = Integer.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight()-100);
	}
	
	@PostConstruct
	public void show() {
		this.user = userRepo.findByUsername(currentPrincipalName).get();
		root = new VerticalLayout();
		buttons = new HorizontalLayout();
		this.add(root);
		calendar = FullCalendarBuilder.create().withScheduler().build();
		
		Button changeViewWeek = new Button("Tydzień");
		changeViewWeek.addClickListener(click->{
			calendar.changeView(CalendarViewImpl.BASIC_WEEK);
		});
		Button changeViewMonth = new Button("Miesiąc");
		changeViewMonth.addClickListener(click->{
			calendar.changeView(CalendarViewImpl.MONTH);
		});
		Button previous = new Button("Poprzedni");
		previous.addClickListener(click->{
			calendar.previous();
		});
		Button next = new Button("Następny");
		next.addClickListener(click->{
			calendar.next();
		});
		buttons.add(changeViewWeek, changeViewMonth, previous, next);
		root.add(buttons);
		SchedulerHelper.setCalendar(calendar, root, user, dayRepo, height);
	}

}
