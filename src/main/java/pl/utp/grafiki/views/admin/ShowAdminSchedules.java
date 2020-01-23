package pl.utp.grafiki.views.admin;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import pl.utp.grafiki.domain.AdminHelper;
import pl.utp.grafiki.domain.Day;
import pl.utp.grafiki.domain.SchedulerHelper;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.DayRepo;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings("serial")
@Secured("ROLE_ADMIN")
@Route(value="showAdminSchedules")
public class ShowAdminSchedules extends AdminMenu{
	
	DayRepo dayRepo;
	UserRepo userRepo;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	Grid<User> gridUser;
	FullCalendar calendar;
	VerticalLayout root;
	public static String VIEW="MONTHLY";
	String height;
	
	@Autowired
	public ShowAdminSchedules(DayRepo dayRepo, UserRepo userRepo) {
		this.dayRepo = dayRepo;
		this.userRepo = userRepo;
		height = Integer.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight()-100);
	}
	
	@PostConstruct
	public void show() {
		setGrid();
	}
	
	public void setGrid() {
		root = new VerticalLayout();
		calendar = FullCalendarBuilder.create().withScheduler().build();
		gridUser = new Grid<>();
		root.add(gridUser, addButtons());
		AdminHelper.setGrid(gridUser, currentPrincipalName, userRepo, calendar, dayRepo, root, height);
		this.add(root);
	}
	
	public HorizontalLayout addButtons() {
		Anchor saveYours = new Anchor(getStreamResource("grafik.txt", generateContent(false)), "Pobierz swoje");
        saveYours.getElement().setAttribute("download",true);
        
        Anchor saveAll = new Anchor(getStreamResource("grafik_wszystkie.txt", generateContent(true)), "Pobierz wszystkie");
        saveAll.getElement().setAttribute("download",true);
        
        Button weeklyView = new Button("Widok tygodnia");
        weeklyView.addClickListener(click->{
        	calendar.changeView(CalendarViewImpl.AGENDA_WEEK);
        	VIEW="WEEKLY";
        });
        Button monthlyView = new Button("Widok miesiąca");
        monthlyView.addClickListener(click->{
        	calendar.changeView(CalendarViewImpl.MONTH);
        	VIEW="MONTHLY";
        });
        Button previous = new Button("Poprzedni");
		previous.addClickListener(click1->{
			calendar.previous();
		});
		Button next = new Button("Następny");
		next.addClickListener(click1->{
			calendar.next();
		});
		return new HorizontalLayout(saveYours, saveAll, weeklyView, monthlyView, previous, next);
	}
	
	public String generateContent(Boolean all) {
		return AdminHelper.generateTop()+AdminHelper.generateAllUsersLines(currentPrincipalName, dayRepo, userRepo, all);
	}
	
	public StreamResource getStreamResource(String filename, String content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content.getBytes()));
    }
	
	public static void saveNewSchedule(DayRepo dayRepo, User user, FullCalendar calendar) {
		List<Day> daysToDelete = dayRepo.findByMonthAndUser(SchedulerHelper.getMonth(), user);
		dayRepo.deleteAll(daysToDelete);
		dayRepo.saveAll(SchedulerHelper.fromEntriesToDays(SchedulerHelper.getEntriesFromCalendar(calendar), user));
	}
}
