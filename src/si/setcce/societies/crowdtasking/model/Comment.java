package si.setcce.societies.crowdtasking.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.*;
import si.setcce.societies.crowdtasking.model.dao.TaskDao;

import java.util.Date;

@Entity
public class Comment {
    @Id
    private Long id;
    @Index
    @Load
    private Ref<Task> taskRef;
    @Ignore
    private Task task;
    @Load
    private Ref<CTUser> ownerRef;
    @Ignore
    private CTUser ownerUser;
    private String comment;
    @Index
    private Date posted;
    @Index
    private boolean execution = false;
    private int score; // number of likes (at the moment)

    public Comment() {
    }

    public Comment(Long taskId, CTUser user, String comment) {
        this.id = null;
        this.ownerRef = Ref.create(Key.create(CTUser.class, user.getId()));
        this.taskRef = Ref.create(Key.create(Task.class, taskId));
        this.comment = comment;
        this.posted = new Date();
        this.score = 0;
        this.ownerUser = user;
    }

    public Long getId() {
        return id;
    }

    public CTUser getOwner() {
        if (ownerUser != null) {
            return ownerUser;
        }
        if (ownerRef != null) {
            ownerUser = ownerRef.get();
        }
        return ownerUser;
    }

    public void setOwner(CTUser ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public boolean isExecution() {
        return execution;
    }

    public void setExecution(boolean execution) {
        this.execution = execution;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Task getTask() {
        if (task != null) {
            return task;
        }
        if (taskRef != null) {
            try {
                task = taskRef.get();
            } catch (Exception e) {
                System.out.println("Error in getTask() in Comment class: " + e.getMessage());
                task = TaskDao.getTaskById(taskRef.getKey().getId());
            }
        }
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTaskRef(Ref<Task> taskRef) {
        this.taskRef = taskRef;
    }

    public void setOwnerRef(Ref<CTUser> ownerRef) {
        this.ownerRef = ownerRef;
    }
}
