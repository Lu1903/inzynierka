package pl.utp.grafiki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.utp.grafiki.domain.Day;
import pl.utp.grafiki.domain.User;

public interface DayRepo extends JpaRepository<Day, Long> {

	List<Day> findByMonthAndUser(int month, User user);
	List<Day> findByUser(User user);
	
	//@Query("Select u FROM Day u WHERE (u.month = month and u.user=user) ORDER BY u.day, u.hour_start ASC")
	List<Day> findByUserAndMonthOrderByDayAscHourStartAsc(User user, int month);
}
