package pl.utp.grafiki.domain;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import pl.utp.grafiki.repository.DayRepo;
import pl.utp.grafiki.repository.UserRepo;
import pl.utp.grafiki.views.admin.ShowAdminSchedules;

public class AdminHelper {
	
	public static long getUserId(String currentPrincipalName, UserRepo userRepo) {
		User user = userRepo.findByUsername(currentPrincipalName).get();
		return user.getId();
	}
	
	public static List<User> getUsers(String currentPrincipalName, UserRepo userRepo, Boolean all) {
		if(currentPrincipalName.equals("Superuser")|| all) {
			return userRepo.findAll();
		}else {
			return userRepo.findByManager_Id(getUserId(currentPrincipalName, userRepo));
		}
	}
	
	public static void setGrid(Grid<User> uusers, String currentPrincipalName, UserRepo userRepo, FullCalendar calendar, DayRepo dayRepo, VerticalLayout root, String height) {
		Editor<User> editor = uusers.getEditor();
		Binder<User> binder = new Binder<>(User.class);
		editor.setBinder(binder);
		editor.setBuffered(true);
		uusers.setItems(getUsers(currentPrincipalName, userRepo, false));
		Grid.Column<User> nameColumn = uusers.addColumn(User::getName).setHeader("Imię");
		Grid.Column<User> surnameColumn = uusers.addColumn(User::getSurname).setHeader("Nazwisko");
		
		Collection<Button> editButtons = Collections
		        .newSetFromMap(new WeakHashMap<>());

		Grid.Column<User> editorColumn = uusers.addComponentColumn(person -> {
		    Button edit = new Button("Edytuj");
		    edit.addClassName("edit");
		    edit.addClickListener(e -> {
		    	calendar.removeAllEntries();
		    	SchedulerHelper.setCalendar(calendar, root, person, dayRepo, height);
		    	uusers.select(person);
		    });
		    editButtons.add(edit);
		    return edit;
		});
		
		Grid.Column<User> saveColumn = uusers.addComponentColumn(person1 -> {
		    Button save= new Button("Zapisz zmiany");
		    save.addClassName("save");
		    save.addClickListener(e -> {
		    	ShowAdminSchedules.saveNewSchedule(dayRepo, person1, calendar);
		    });
		    editButtons.add(save);
		    return save;
		});
		
		calendar.setTimeslotsSelectable(true);
		calendar.addTimeslotsSelectedListener((event) -> {
			Entry entry = new Entry();
			if(ShowAdminSchedules.VIEW.equals("WEEKLY")){
				entry.setStart(calendar.getTimezone().convertToUTC(event.getStartDateTime()));
				entry.setEnd(calendar.getTimezone().convertToUTC(event.getEndDateTime()));
				if(!SchedulerHelper.checkEntry(entry)) {
					entry.setColor("#ff0000");
				}else {
					entry.setColor("#0000ff");
				}
				entry.setEditable(false);
				
				if(!entry.isAllDay() && (entry.getStart().getDayOfMonth()==entry.getEnd().getDayOfMonth())) {
					calendar.addEntry(entry);
				}
			}else {
				entry.setStart(calendar.getTimezone().convertToUTC(event.getStartDateTime()));
				entry.setEnd(calendar.getTimezone().convertToUTC(event.getStartDateTime()));
				new MonthDialog(calendar, entry, true).open();
			}
		});
		
		calendar.addEntryClickedListener(event->{
				calendar.removeEntry(event.getEntry());
		});
	}
	
	public static String generateTop() {
		String top=",";
		int days = SchedulerHelper.getLastDay();
		int month = YearMonth.now().plusMonths(1).getMonthValue();
		int year = YearMonth.now().plusMonths(1).getYear();
		String date;
		for(int i=1; i<=days; i++) {
			if(!checkIfSunday(i, month, year)) {
				date=i+"/"+month+"/"+year;
				for(int j=1; j<=4; j++) {
					top=top+date+",";
				}
			}
		}
		return top+getLineSeparator();
	}
	
	public static Boolean checkIfSunday(int day, int month, int year) {
		if(YearMonth.of(year, month).atDay(day).getDayOfWeek()==DayOfWeek.SUNDAY)
			return true;
		return false;
	}
	
	public static List<Day> getDaysByUserMonthAsc(DayRepo dayRepo, User user){
		return dayRepo.findByUserAndMonthOrderByDayAscHourStartAsc(user, SchedulerHelper.getMonth());
	}
	
	public static String generateOneUserLine(User user, DayRepo dayRepo) {
		List<Day> days = getDaysByUserMonthAsc(dayRepo, user);
		List<Integer> listOfDays = generateListOfDays();
		String line = user.getName()+" "+user.getSurname()+",";
		int i=0;
		Day day=new Day();
		if(days.size()!=0) {
			//line=line+getLineSeparator();
		//}else {
			for(int listDay : listOfDays) {
				if(i!=(days.size()-1)) {
					//line=line+getLineSeparator();
				//}else {
					if(!(i>=days.size())) {
						day = days.get(i);
					}
					if(checkIfSameDate(listDay, day)) {
						i++;
						line=line+generateHours(day);
					}else {
						line=line+",,";
					}
				}
			}
		}
		return line+getLineSeparator();
	}
	
	public static List<Integer> generateListOfDays(){
		List<Integer> listOfDays = new ArrayList<>();
		int howMuchDays = SchedulerHelper.getLastDay();
		int month = SchedulerHelper.getMonth();
		int year = SchedulerHelper.getYear();
		for(int i=1; i<=howMuchDays; i++) {
			if(!checkIfSunday(i, month, year)) {
				listOfDays.add(i);
				listOfDays.add(i);
			}
		}
		return listOfDays;
	}
	
	public static Boolean checkIfSameDate(int listDay, Day day) {
		return listDay==day.getDay();
	}
	
	public static String generateAllUsersLines(String currentPrincipalName, DayRepo dayRepo, UserRepo userRepo, Boolean all) {
		List<User> list = new ArrayList<>();
		if(all) {
			list = getUsers(currentPrincipalName, userRepo, true);
		}else {
			list = getUsers(currentPrincipalName, userRepo, false);
		}
		String lines = "";
		for(User user : list) {
			lines=lines+generateOneUserLine(user, dayRepo);
		}
		return lines;
	}
	
	public static String getLineSeparator() {
		return System.getProperty("line.separator"); 
	}
	
	public static String generateHours(Day day) {
		String hour=Integer.toString(day.getHour_start());
		if(day.getMinutes_start()==0) {
			hour=hour+":00,";
		}else {
			hour=hour+":30,";
		}
		hour=hour+Integer.toString(day.getHour_end());
		if(day.getMinutes_end()==0) {
			hour=hour+":00,";
		}else {
			hour=hour+":30,";
		}
		
		return hour;
	}
	
}
