package no.kristiania.http;

public class Cookie {
    private String name;
    private String value;

    public Cookie(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String toString(){
        StringBuffer s = new StringBuffer();

        s.append(name + "=" + value);

        return s.toString();
    }
}
