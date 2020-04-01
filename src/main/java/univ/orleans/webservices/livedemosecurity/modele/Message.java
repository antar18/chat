package univ.orleans.webservices.livedemosecurity.modele;

public class Message {
    private long id;
    private String texte;

    public Message(long id, String texte) {
        this.id = id;
        this.texte = texte;
    }

    public long getId() {
        return id;
    }


    public String getTexte() {
        return texte;
    }
}
