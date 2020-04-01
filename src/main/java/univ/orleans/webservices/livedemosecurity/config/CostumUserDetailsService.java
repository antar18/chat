package univ.orleans.webservices.livedemosecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import univ.orleans.webservices.livedemosecurity.controller.MessageController;
import univ.orleans.webservices.livedemosecurity.modele.Utilisateur;

public class CostumUserDetailsService implements UserDetailsService {

    private static final String[] ROLES_ADMIN = {"ADMIN","USER"};
    private static final String[] ROLES_USER = {"USER"};

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Utilisateur utilisateur = MessageController.getUtilisateurs().get(s);
        if(utilisateur == null){
            throw new UsernameNotFoundException("user :"+s+"not found");
        }
        String[] roles = utilisateur.isAdmin() ? ROLES_ADMIN : ROLES_USER;
        UserDetails userDetails = User.builder()
                .username(utilisateur.getLogin())
                .password(passwordEncoder.encode(utilisateur.getPassword()))
                .roles(roles)
                .build();

        return userDetails;
    }
}
