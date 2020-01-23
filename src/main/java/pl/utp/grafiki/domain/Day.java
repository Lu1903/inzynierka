package pl.utp.grafiki.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Day {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="day_id", nullable = false)
	private long id;
	@Column(nullable = false)
	private int day;
	@Column(name="hour_start", nullable = false)
	private int hourStart;
	@Column(nullable = false)
	private int minutes_start;
	@Column(nullable = false)
	private int hour_end;
	@Column(nullable = false)
	private int minutes_end;
	@Column(nullable = false)
	private int month; // 1-styczeń 12-grudzień
	@Column(nullable = false)
	private int year;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;
	
	public Day() {
		
	}

	public Day(int day, int hour_start, int minutes_start, int hour_end, int minutes_end, int month, int year, User user) {
		this.day = day;
		this.hourStart = hour_start;
		this.minutes_start = minutes_start;
		this.hour_end = hour_end;
		this.minutes_end = minutes_end;
		this.month = month;
		this.year = year;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour_start() {
		return hourStart;
	}

	public void setHour_start(int hour_start) {
		this.hourStart = hour_start;
	}

	public int getMinutes_start() {
		return minutes_start;
	}

	public void setMinutes_start(int minutes_start) {
		this.minutes_start = minutes_start;
	}

	public int getHour_end() {
		return hour_end;
	}

	public void setHour_end(int hour_end) {
		this.hour_end = hour_end;
	}

	public int getMinutes_end() {
		return minutes_end;
	}

	public void setMinutes_end(int minutes_end) {
		this.minutes_end = minutes_end;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
