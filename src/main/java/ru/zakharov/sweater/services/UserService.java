package ru.zakharov.sweater.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.zakharov.sweater.model.User;
import ru.zakharov.sweater.repos.UsersRepo;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        else throw new UsernameNotFoundException("User with name "+username+" not found!");
    }
}
