package znurgl.ignitetest.data;

/**
 * Created by Gergo Bakos (znurgl@gmail.com) on 18/07/2018.
 */
public class Event {
    private String name;
    private Long timestamp;
    private String description;

    public Event(String name, Long timestamp, String description) {
        this.name = name;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
