package edu.uga.cs.superfinalstatecapital;

public class State {
    private long id;
    private String name;
    private String capital;
    private String city2;
    private String city3;

    public State() {
        this.id = -1;
        this.name = null;
        this.capital = null;
        this.city2 = null;
        this.city3 = null;
    }

    public State(String name, String capital, String city2, String city3) {
        this.id = -1;
        this.name = name;
        this.capital = capital;
        this.city2 = city2;
        this.city3 = city3;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital != null ? capital : "";
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCity2() {
        return city2 != null ? city2 : "";
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getCity3() {
        return city3 != null ? city3 : "";
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (Capital: " + capital + ")";
    }
}