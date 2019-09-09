import com.alibaba.fastjson.JSON;

import java.util.*;

public class RoundRobin implements GetTaskFromTodo {
    static int MAX_TASKS_PROCESSING = 100;

    private final List<Task> todoList = new ArrayList<>();
    private final List<Task> processingList = new ArrayList<>();

    // maps to associate customer/task & customer/their processing task number
    private final Map<String,PriorityQueue<Task>> customerTaskMap = new HashMap<>();
    private final Map<String,Integer> customertNumMap = new HashMap<>();

    @Override
    public void startWork() {

        // read files
        String taskPath = "./Data/task.json";
        String taskContent = FileUtil.readFile(taskPath);
        List<Task> taskList = JSON.parseArray(taskContent, Task.class);

        String customerPath = "./Data/customer.json";
        String customerContent = FileUtil.readFile(customerPath);
        List<Customer> customerList = JSON.parseArray(customerContent, Customer.class);

        todoList.addAll(taskList);

        // designate tasks to customers
        for (int i=0;i<customerList.size();i++){
            PriorityQueue<Task> tasksOfCustomer = new PriorityQueue<Task>(new DateComparator());
            for (Task t: taskList){
                if(customerList.get(i).getUuid().equals(t.getCustomerId())){
                    tasksOfCustomer.add(t);
                }
            }
            customerTaskMap.put(customerList.get(i).getUuid(),tasksOfCustomer);
        }


        while(true){
            //  each costomer take turns to put his/her tasks from todoList to processingList
            for (Customer c: customerList){
                if(!todoList.isEmpty()
                        && processingList.size() < MAX_TASKS_PROCESSING){
                    if(!customerTaskMap.get(c.getUuid()).isEmpty()){
                        final Task nextTask = customerTaskMap.get(c.getUuid()).poll();
                        final Customer customer = c;
                        final long nextTaskNeedTime = (long) (((customer.getTaskMaxSeconds() * 1000 - customer.getTaskMinSeconds() * 1000) * Math.random())
                                + customer.getTaskMinSeconds() * 1000);
                        long lastTaskTimeToLive = System.currentTimeMillis() + nextTaskNeedTime;
                        nextTask.setTimeToLive(new Date(lastTaskTimeToLive));
                        processingList.add(nextTask);
                        todoList.remove(nextTask);

                        if(customertNumMap.containsKey(c.getUuid())){
                            customertNumMap.put(c.getUuid(),customertNumMap.get(c.getUuid())+1);
                        }else{
                            customertNumMap.put(c.getUuid(),1);
                        }


                        System.out.println("--------------------- RR ---------------------");
                        System.out.println("task: "+nextTask.getUuid()+ ", with the earliest insertedTime of customer: " + customer.getUuid() + " has been added to processing, and it will take " + nextTaskNeedTime / 1000 + "s to precess. \n");
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
                        for(Map.Entry<String,Integer> entry: customertNumMap.entrySet()){
                            System.out.println("customer: "+entry.getKey()+ " now has "+entry.getValue()+" task being processed.");
                        }
                        System.out.println("--------------------------------------------");
                    }else{
                        continue;
                    }

                }

                //  tasks from processingList get back to todoList when it finished
                if (!processingList.isEmpty()){

                    for (int i = 0; i < processingList.size(); i++) {

                        Task currentTask = processingList.get(i);

                        if (new Date().after(currentTask.getTimeToLive())) {

                            Task completedTask = currentTask;
                            processingList.remove(i);
                            completedTask.setInsertedTime(new Date());
                            completedTask.setTimeToLive(null);
                            todoList.add(completedTask);
                            customerTaskMap.get(completedTask.getCustomerId()).add(completedTask);

                            if(customertNumMap.get(c.getUuid())>1)
                            customertNumMap.put(c.getUuid(),customertNumMap.get(c.getUuid())-1);


                            System.out.println("********************* RR *********************");
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
                            System.out.println();
                            System.out.println("********************************************");
                        }

                    }
                }
            }
        }

    }
}
