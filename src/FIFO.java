import com.alibaba.fastjson.JSON;
import java.util.*;

public class FIFO implements GetTaskFromTodo {

    static int MAX_TASKS_PROCESSING = 100;

    final Queue<Task> todoList = new LinkedList<>();
    final List<Task> processingList = new ArrayList<>();

    final Map<String, Customer> customerIdMap = new HashMap<>();


    @Override
    public void startWork() {

        // Read Files
        String taskPath = "./Data/task.json";
        String taskContent = FileUtil.readFile(taskPath);
        List<Task> taskList = JSON.parseArray(taskContent, Task.class);

        String customerPath = "./Data/customer.json";
        String customerContent = FileUtil.readFile(customerPath);
        List<Customer> customerList = JSON.parseArray(customerContent, Customer.class);

        // Sort tasks by insert time
        Collections.sort(taskList,new DateComparator());

        for (int i = 0; i < taskList.size(); i++) {
            todoList.add(taskList.get(i));
        }

        // map task to customer to get processing time
        for (Task t : taskList) {
            for (Customer c : customerList) {
                if (t.getCustomerId().equals(c.getUuid())) {
                    customerIdMap.put(t.getCustomerId(), c);
                }
            }
        }


        while (true) {

            // from todoList to processingList based on insertedTime

            if (!todoList.isEmpty()
                    && processingList.size() < MAX_TASKS_PROCESSING) {

                final Task nextTask = todoList.poll();
                final Customer customer = customerIdMap.get(nextTask.getCustomerId());
                final long nextTaskNeedTime = (long) (((customer.getTaskMaxSeconds() * 1000 - customer.getTaskMinSeconds() * 1000) * Math.random())
                        + customer.getTaskMinSeconds() * 1000);
                long lastTaskTimeToLive = System.currentTimeMillis() + nextTaskNeedTime;
                nextTask.setTimeToLive(new Date(lastTaskTimeToLive));
                processingList.add(nextTask);

                System.out.println("---------------------FIFO-------------------");
                System.out.println("put " + nextTask.getUuid() + " to processing, and it will take " + nextTaskNeedTime / 1000 + "s to precess. \n");
                System.out.println("Todo list: (Size: "+ todoList.size()+")");
                for (Task t : todoList) {
                    System.out.print(t.getUuid() + " ");
                }

                System.out.println();
                System.out.println("Processing list:(Size: "+ processingList.size()+")");
                for (Task t : processingList) {
                    System.out.print(t.getUuid() + " ");
                }
                System.out.println();
                System.out.println("--------------------------------------------");

            }


            // from processingList to todoList

            if (!processingList.isEmpty()) {

                for (int i = 0; i < processingList.size(); i++) {

                    Task currentTask = processingList.get(i);

                    if (new Date().after(currentTask.getTimeToLive())) {

                        Task completedTask = currentTask;
                        processingList.remove(i);
                        completedTask.setInsertedTime(new Date());
                        completedTask.setTimeToLive(null);
                        todoList.add(completedTask);

                        System.out.println("*********************FIFO*******************");
                        System.out.println("put " + completedTask.getUuid() + " back to todo list\n");

                        System.out.println("Todo list: (Size: "+ todoList.size()+")");
                        for (Task t : todoList) {
                            System.out.print(t.getUuid() + " ");
                        }

                        System.out.println();
                        System.out.println("Processing list:(Size: "+ processingList.size()+")");
                        for (Task t : processingList) {
                            System.out.print(t.getUuid() + " ");
                        }
                        System.out.println();
                        System.out.println("********************************************");
                    }

                }

            }
        }

    }

}