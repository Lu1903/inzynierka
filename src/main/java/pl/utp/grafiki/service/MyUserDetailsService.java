package pl.utp.grafiki.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.utp.grafiki.domain.User;
import pl.utp.grafiki.repository.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {
 
    @Autowired
    private UserRepo userRepository;
    
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
        	return user.get();
        }else {
        	throw new UsernameNotFoundException("User not found by name: " + username);
        }
        
        
    }
   
}