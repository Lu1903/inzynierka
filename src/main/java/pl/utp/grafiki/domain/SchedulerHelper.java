package pl.utp.grafiki.domain;

import java.awt.GraphicsEnvironment;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.Scheduler;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import pl.utp.grafiki.repository.DayRepo;

public class SchedulerHelper {
	
	public static List<Day> fromEntriesToDays(List<Entry> entries, User user) {
		List<Day> days = new ArrayList<>();
		int month = getMonth();
		int year = getYear();
		
		for(Entry entry : entries) {
			int day = entry.getStart().getDayOfMonth();
			int hour_start = entry.getStart().getHour();
			int minutes_start = entry.getStart().getMinute();
			int hour_end = entry.getEnd().getHour();
			int minutes_end = entry.getEnd().getMinute();
			days.add(new Day(day, hour_start, minutes_start, hour_end, minutes_end, month, year, user));
		}
		return days;
	}
	
	public static List<Entry> fromDaysToEntries(List<Day> days){
		List<Entry> entries = new ArrayList<>();
		
		for(Day day : days) {
			int year = day.getYear();
			int month = day.getMonth();
			int dday = day.getDay();
			int hour_start = day.getHour_start();
			int minutes_start = day.getMinutes_start();
			int hour_end = day.getHour_end();
			int minutes_end = day.getMinutes_end();
			
			Entry entry = new Entry();
			entry.setStart(LocalDateTime.of(year, month, dday, hour_start, minutes_start, 0));
			entry.setEnd(LocalDateTime.of(year, month, dday, hour_end, minutes_end, 0));
			setEntryTitle(entry);
			entries.add(entry);
		}
		
		return entries;
	}
	
	public static void setEntryTitle(Entry entry) {
		String title=Integer.toString(entry.getStart().getHour())+":";
		String pom;
		if(entry.getStart().getMinute()==0) {
			pom="00";
		}else {
			pom="30";
		}
		title = title+pom+" - "+Integer.toString(entry.getEnd().getHour())+":";
		if(entry.getEnd().getMinute()==0) {
			pom="00";
		}else {
			pom="30";
		}
		title = title + pom;
		entry.setTitle(title);
	}
	
	public static Boolean checkMonthScheduleFull(DayRepo dayRepo, User user) {
		if(dayRepo.findByMonthAndUser(getMonth(), user).isEmpty()) {
			return false;
		}else {
			return true;
		}
	}
	
	public static int getLastDay() {
		return YearMonth.now()            
		         .plusMonths( 1 )
		         .atEndOfMonth().getDayOfMonth();
	}
	
	public static int getMonth() {
		Calendar cal = Calendar.getInstance();
		if((cal.get(Calendar.MONTH))==11) //0-january
			return 1;
		else
			return cal.get(Calendar.MONTH)+2; //+2 because 0 is January, we want "normal" month numbers
	}
	
	public static int getYear() {
		Calendar cal = Calendar.getInstance();
		if(getMonth()==12)
			return cal.get(Calendar.YEAR)+1;
		else
			return cal.get(Calendar.YEAR);
	}
	
	public static LocalDateTime startDate() {
		return LocalDateTime.of(getYear(), getMonth(), 1, 0, 0, 0);
	}
	
	public static LocalDateTime endDate() {
		return LocalDateTime.of(getYear(), getMonth(), getLastDay(), 23, 59, 59);
	}
	
	public static List<Entry> getEntriesFromCalendar(FullCalendar calendar) {
		return calendar.getEntries(startDate(), endDate());
	}
	
	public static Boolean checkEntry(Entry entry) {
		LocalDateTime start = entry.getStart();
		LocalDateTime end = entry.getEnd();
		if(start.getDayOfWeek().equals(DayOfWeek.SUNDAY) || entry.isAllDay() || (start.getDayOfMonth()!=end.getDayOfMonth()))
			return false;
		else
			if(start.getDayOfWeek().equals(DayOfWeek.SATURDAY) && (start.getHour()<9 || end.getHour()>17)) 
				return false;
			else
				if(start.getHour()<8 || end.getHour()==21 || end.getHour()>21)
					return false;
				else
					return true;
	}
	
	public static void setCalendar(FullCalendar calendar, VerticalLayout root, User user, DayRepo dayRepo, String height) {
		
		((Scheduler) calendar).setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");
		calendar.setFirstDay(DayOfWeek.MONDAY);
		calendar.setWeekNumbersVisible(true);
		calendar.gotoDate(LocalDate.of(getYear(), getMonth(), 1));
		calendar.setLocale(new Locale("pl"));
		//calendar.setHeightAuto();
		//calendar.setHeightFull();
		//calendar.setHeight(Integer.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight()));
		calendar.getElement().getStyle().set("height", height+"px");
		calendar.changeView(CalendarViewImpl.MONTH);
		calendar.addEntries(getEntries(dayRepo, user));
		root.add(calendar);
	}
	
	public static List<Entry> getEntries(DayRepo dayRepo, User user) {
		return fromDaysToEntries(dayRepo.findByUser(user));
	}
}
