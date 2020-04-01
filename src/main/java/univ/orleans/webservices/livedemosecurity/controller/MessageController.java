package univ.orleans.webservices.livedemosecurity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import univ.orleans.webservices.livedemosecurity.modele.Message;
import univ.orleans.webservices.livedemosecurity.modele.Utilisateur;

import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@RestController
@RequestMapping("/api")
public class MessageController {
    private static List<Message> messages = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1L);

    //stock des utilisateurs
    private static Map<String, Utilisateur> utilisateurs = new HashMap<>();
    public static Map<String,Utilisateur> getUtilisateurs(){
        return utilisateurs;
    }
    static {
        Utilisateur fred = new Utilisateur("fred","fred",false);
        Utilisateur admin = new Utilisateur("admin","admin",true);
        utilisateurs.put(fred.getLogin(),fred);
        utilisateurs.put(admin.getLogin(),admin);
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> create(@RequestBody Message message){
        //il n'a pas d'id juste du texte
        Message message1 = new Message(counter.getAndIncrement(),message.getTexte());
        messages.add(message1);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(message1.getId()).toUri();
        return ResponseEntity.created(location).body(message1);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAll(){
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("messages/{id}")
    public ResponseEntity<Message> findById(@PathVariable("id") Long id){
        Optional<Message> message = messages.stream()
                .filter(m -> m.getId() == id)
                .findAny();
        if(message.isPresent()){
            return ResponseEntity.ok().body(message.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity deleteById(@PathVariable("id") Long id){
        for (int i = 0; i<messages.size();i++){
            if(messages.get(i).getId() == id){
                messages.remove(i);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/utilisateurs")
    public ResponseEntity<Utilisateur> addUtilisateur(@RequestBody Utilisateur utilisateur){
        Predicate<String> isOk = s -> (s != null) && (s.length() >= 2);
        if(!isOk.test(utilisateur.getLogin()) || !isOk.test(utilisateur.getPassword())){
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(utilisateur.getLogin()).toUri();
        utilisateurs.put(utilisateur.getLogin(),utilisateur);
        return ResponseEntity.created(location).body(utilisateur);
    }

//    @GetMapping("/utilisateurs/{login}")
//    public ResponseEntity<Utilisateur> findUtilisateurById(Principal principal, @PathVariable("login") String login){
//
//        if(!principal.getName().equals(login)){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        if(utilisateurs.containsKey(login)){
//            return ResponseEntity.ok().body(utilisateurs.get(login));
//        }
//        else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/utilisateurs/{login}")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<Utilisateur> findUtilisateurById2(@PathVariable("login") String login){
        if(utilisateurs.containsKey(login)){
            return ResponseEntity.ok().body(utilisateurs.get(login));
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
