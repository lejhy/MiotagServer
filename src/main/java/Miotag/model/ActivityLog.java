package Miotag.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ActivityLog {
    @Id
    @GeneratedValue
    private long id;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private int length;
    private int score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
