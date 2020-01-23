package pl.utp.grafiki.views.user;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.stefan.fullcalendar.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.utp.grafiki.domain.Day;
import pl.utp.grafiki.domain.SchedulerHelper;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.DayRepo;
import pl.utp.grafiki.repository.UserRepo;

@SuppressWarnings( "serial")
@Secured("ROLE_USER")
@Route(value="saveUserSchedule")
@PageTitle("Zapisz grafik")
public class SaveUserSchedule extends UserMenu {

	VerticalLayout root;
	HorizontalLayout buttons;
	FullCalendar calendar;
	UserRepo userRepo;
	DayRepo dayRepo;
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	String currentPrincipalName = authentication.getName();
	User user;
	List<Entry> entries = new ArrayList<>();
	Button saveSchedule;
	
	
	@Autowired
	public SaveUserSchedule(UserRepo userRepo, DayRepo dayRepo) {
		this.userRepo = userRepo;
		this.dayRepo = dayRepo;
	}
	
	@PostConstruct
	private void show() {
		this.user = userRepo.findByUsername(currentPrincipalName).get();
		root = new VerticalLayout();
		buttons = new HorizontalLayout();
		this.add(root);
		addButtons();
		setCalendar();
	}
	
	private void addButtons() {
		this.saveSchedule = new Button("Zapisz");
		saveSchedule.addClickListener(click->{
			saveSchedule();
		});
		Button previous = new Button("Poprzedni");
		previous.addClickListener(click->{
			calendar.previous();
		});
		Button next = new Button("Następny");
		next.addClickListener(click->{
			calendar.next();
		});
		
		if(!SchedulerHelper.checkMonthScheduleFull(dayRepo, user)) {
			buttons.add(saveSchedule);
		}else {
			root.add(new Label("Twój grafik został zapisany. W celu edycji skontaktuj się ze swoim kierownikiem."));
			setDays();
		}
		
		buttons.add(previous, next);
		root.add(buttons);
	}
	
	private void setDays() {
		List<Day> days = dayRepo.findByMonthAndUser(SchedulerHelper.getMonth(), user);
		
		for(Day day : days) {
			int dday = day.getDay();
			int hour_start = day.getHour_start();
			int minutes_start = day.getMinutes_start();
			int hour_end = day.getHour_end();
			int minutes_end = day.getMinutes_end();
			int year = day.getYear();
			int month = day.getMonth();
			Entry entry = new Entry();
			entry.setStart(LocalDateTime.of(year, month, dday, hour_start, minutes_start, 0));
			entry.setEnd(LocalDateTime.of(year, month, dday, hour_end, minutes_end, 0));
			this.entries.add(entry);
		}
	}
	
	private void setCalendar() {
		this.calendar = FullCalendarBuilder.create().withScheduler().build();
		((Scheduler) calendar).setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");
		calendar.setFirstDay(DayOfWeek.MONDAY);
		calendar.setWeekNumbersVisible(true);
		calendar.gotoDate(LocalDate.of(SchedulerHelper.getYear(), SchedulerHelper.getMonth(), 1));
		calendar.setBusinessHours(new BusinessHours(LocalTime.of(8, 0), LocalTime.of(20, 30),BusinessHours.DEFAULT_BUSINESS_WEEK), 
								  new BusinessHours(LocalTime.of(9, 0), LocalTime.of(17, 00),DayOfWeek.SATURDAY));
		calendar.setLocale(new Locale("pl"));
		calendar.setTimeslotsSelectable(true);
		calendar.setHeightAuto();
		calendar.changeView(CalendarViewImpl.AGENDA_WEEK);
		calendar.addTimeslotsSelectedListener((event) -> {
			Entry entry = new Entry();
			
			entry.setStart(calendar.getTimezone().convertToUTC(event.getStartDateTime()));
			entry.setEnd(calendar.getTimezone().convertToUTC(event.getEndDateTime()));
			
			if(!SchedulerHelper.checkEntry(entry)) {
				entry.setColor("#ff0000");
			}else {
				entry.setColor("#0000ff");
			}
			entry.setEditable(false);
			
			if(!entry.isAllDay() && (entry.getStart().getDayOfMonth()==entry.getEnd().getDayOfMonth()) && checkEvents(calendar, entry)) {
				calendar.addEntry(entry);
			}
			
		});
		
		calendar.addEntryClickedListener(event->{
			calendar.removeEntry(event.getEntry());
		});
		calendar.addEntries(this.entries);
		root.add(calendar);
		root.setFlexGrow(1, calendar);
	}
	
	private Boolean checkEvents(FullCalendar calendar, Entry entry) {
		List<Entry> list = calendar.getEntries();
		int day = entry.getStart().getDayOfMonth();
		int howMuch = 0;
		for(Entry checkEntry : list) {
			if(checkEntry.getStart().getDayOfMonth()==day) {
				howMuch++;
			}
		}
		if(howMuch==2) {
			return false;
		}else {
			return true;
		}
	}
	
	@Transactional
	private void saveSchedule() {
		List<Entry> entries = SchedulerHelper.getEntriesFromCalendar(calendar);
		if(checkScheduleAfterClickSave(entries)==true) {
			dayRepo.saveAll(SchedulerHelper.fromEntriesToDays(entries, user));
			this.saveSchedule.setEnabled(false);
			Notification.show("Twój grafik został zapisany.");
		}else {
			Notification.show("Ustaw prawidłowe godziny pracy.");
		}
	}
	
	private Boolean checkScheduleAfterClickSave(List<Entry> entries) {
		for(Entry entry : entries) {
			if(SchedulerHelper.checkEntry(entry)==false) {
				return false;
			}
		}
		return true;
	}
}
