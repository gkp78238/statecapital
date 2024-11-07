package edu.uga.cs.superfinalstatecapital;

/**
 * The State class represents a U.S. state and its related information.
 * Each state has an ID, name, capital, and two additional major cities.
 */
public class State {

    // Instance variables
    private long id; // Unique identifier for the state
    private String name; // Name of the state
    private String capital; // Capital city of the state
    private String city2; // Second major city in the state
    private String city3; // Third major city in the state

    /**
     * Default constructor.
     * Initializes the state with default values.
     */
    public State() {
        this.id = -1;
        this.name = null;
        this.capital = null;
        this.city2 = null;
        this.city3 = null;
    }

    /**
     * Parameterized constructor.
     * Initializes the state with specified values.
     *
     * @param name    the name of the state
     * @param capital the capital city of the state
     * @param city2   the second major city in the state
     * @param city3   the third major city in the state
     */
    public State(String name, String capital, String city2, String city3) {
        this.id = -1; // Default ID for new state objects
        this.name = name;
        this.capital = capital;
        this.city2 = city2;
        this.city3 = city3;
    }

    /**
     * Gets the ID of the state.
     *
     * @return the ID of the state
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the state.
     *
     * @param id the new ID of the state
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the name of the state.
     * If the name is null, returns an empty string.
     *
     * @return the name of the state
     */
    public String getName() {
        return name != null ? name : "";
    }

    /**
     * Sets the name of the state.
     *
     * @param name the new name of the state
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the capital city of the state.
     * If the capital is null, returns an empty string.
     *
     * @return the capital city of the state
     */
    public String getCapital() {
        return capital != null ? capital : "";
    }

    /**
     * Sets the capital city of the state.
     *
     * @param capital the new capital city of the state
     */
    public void setCapital(String capital) {
        this.capital = capital;
    }

    /**
     * Gets the second major city of the state.
     * If the city is null, returns an empty string.
     *
     * @return the second major city of the state
     */
    public String getCity2() {
        return city2 != null ? city2 : "";
    }

    /**
     * Sets the second major city of the state.
     *
     * @param city2 the new second major city of the state
     */
    public void setCity2(String city2) {
        this.city2 = city2;
    }

    /**
     * Gets the third major city of the state.
     * If the city is null, returns an empty string.
     *
     * @return the third major city of the state
     */
    public String getCity3() {
        return city3 != null ? city3 : "";
    }

    /**
     * Sets the third major city of the state.
     *
     * @param city3 the new third major city of the state
     */
    public void setCity3(String city3) {
        this.city3 = city3;
    }

    /**
     * Returns a string representation of the state.
     *
     * @return a string in the format: "ID: Name (Capital: Capital)"
     */
    @Override
    public String toString() {
        return id + ": " + name + " (Capital: " + capital + ")";
    }
}
