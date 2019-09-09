import java.util.Date;

public class Task {

    private String uuid;
    private String customerId;
    private Date insertedTime;
    private Date timeToProcess;
    private Date timeToLive;


    public Task(String uuid, String customerId, Date insertedTime) {
        this.uuid = uuid;
        this.customerId = customerId;
        this.insertedTime = insertedTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getInsertedTime() {
        return insertedTime;
    }

    public void setInsertedTime(Date insertedTime) {
        this.insertedTime = insertedTime;
    }

    public Date getTimeToProcess() {
        return timeToProcess;
    }

    public void setTimeToProcess(Date timeToProcess) {
        this.timeToProcess = timeToProcess;
    }

    public Date getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Date timeToLive) {
        this.timeToLive = timeToLive;
    }
}

