package pl.utp.grafiki.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.utp.grafiki.domain.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);
	List<User> findByRoles(String roles);
	List<User> findByManager_Id(long manager_id);
	@Query("Select u from User u where u.roles like ?1")
	List<User> findByRole(String roles);
	@Modifying
	@Transactional
	@Query("UPDATE User u set u.name = ?1, u.surname = ?2, u.username = ?3 where u.id = ?4")
	void updateUserById(String name, String surname, String username, long id);
	@Modifying
	@Transactional
	@Query("UPDATE User u set u.roles = ?1 where u.id = ?2")
	void updateUserRole(String role, long id);
	@Modifying
	@Transactional
	@Query("UPDATE User u set u.active = ?1 where u.id = ?2")
	void updateUserActivity(boolean activity, long id);
	@Modifying
	@Transactional
	@Query("UPDATE User u set u.manager = ?1 where u.id = ?2")
	void updateUserManager(User manager, long id_user);
	@Modifying
	@Transactional
	@Query("UPDATE User u set u.password = ?1 where u.id = ?2")
	void setUserPassword(String password, long id_user);
}
