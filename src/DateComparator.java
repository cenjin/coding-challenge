import java.util.Comparator;

public class DateComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Task t1 = (Task)o1;
        Task t2 = (Task)o2;
        if (t1.getInsertedTime().before(t2.getInsertedTime())) {
            return -1;
        } else if (t1.getInsertedTime().after(t2.getInsertedTime())) {
            return 1;
        } else return 0;
    }
}
