public class Customer {
    private String uuid;
    private long taskMinSeconds;
    private long taskMaxSeconds;

    public Customer(String uuid, long taskMinSeconds, long taskMaxSeconds) {
        this.uuid = uuid;
        this.taskMinSeconds = taskMinSeconds;
        this.taskMaxSeconds = taskMaxSeconds;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTaskMinSeconds() {
        return taskMinSeconds;
    }

    public void setTaskMinSeconds(long taskMinSeconds) {
        this.taskMinSeconds = taskMinSeconds;
    }

    public long getTaskMaxSeconds() {
        return taskMaxSeconds;
    }

    public void setTaskMaxSeconds(long taskMaxSeconds) {
        this.taskMaxSeconds = taskMaxSeconds;
    }
}
