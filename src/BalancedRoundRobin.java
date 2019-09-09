import com.alibaba.fastjson.JSON;
import java.util.*;


public class BalancedRoundRobin implements GetTaskFromTodo {

    static int MAX_TASKS_PROCESSING = 100;

    // two lists
    private final List<Task> todoList = new ArrayList<>();
    private final List<Task> processingList = new ArrayList<>();

    // maps to associate customer/task & customer/their processing task number
    private final Map<String,PriorityQueue<Task>> customerTaskMap = new HashMap<>();
    private final Map<String,Integer> customerNumMap = new HashMap<>();

    private int minNumOfTasks = Integer.MAX_VALUE;

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
            for (Customer c: customerList){
                // get tasks from todoList to processingList
                if(!todoList.isEmpty()
                        && processingList.size() < MAX_TASKS_PROCESSING){
                    if(!customerTaskMap.get(c.getUuid()).isEmpty()){

                        // balance
                        if(customerNumMap.getOrDefault(c.getUuid(),0) - minNumOfTasks < 2){

                            final Task nextTask = customerTaskMap.get(c.getUuid()).poll();
                            final Customer customer = c;

                            // task time needed, calculated by associated customer
                            final long nextTaskNeedTime = (long) (((customer.getTaskMaxSeconds() * 1000 - customer.getTaskMinSeconds() * 1000) * Math.random())
                                    + customer.getTaskMinSeconds() * 1000);
                            long lastTaskTimeToLive = System.currentTimeMillis() + nextTaskNeedTime;
                            nextTask.setTimeToLive(new Date(lastTaskTimeToLive));

                            processingList.add(nextTask);
                            todoList.remove(nextTask);

                            if(customerNumMap.containsKey(c.getUuid())){
                                customerNumMap.put(c.getUuid(), customerNumMap.get(c.getUuid())+1);
                            }else{
                                customerNumMap.put(c.getUuid(),1);
                            }


                            minNumOfTasks = getMinValue(customerNumMap);


                            System.out.println("--------------------- BRR ---------------------");

                            System.out.println("task: "+nextTask.getUuid()+ ", with the earliest insertedTime of customer: "
                                    + customer.getUuid() + " has been added to processing, and it will take "
                                    + nextTaskNeedTime / 1000 + "s to precess. \n");

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

                            for(Map.Entry<String,Integer> entry: customerNumMap.entrySet()){
                                System.out.print("customer: "+entry.getKey()+
                                        " now has "+entry.getValue()+" task being processed. ");
                                System.out.println();
                            }
                            System.out.println("--------------------------------------------");
                        }

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
                            completedTask.setInsertedTime(new Date());
                            completedTask.setTimeToLive(null);

                            //move back
                            processingList.remove(i);
                            todoList.add(completedTask);
                            customerTaskMap.get(completedTask.getCustomerId()).add(completedTask);

                            customerNumMap.put(c.getUuid(), customerNumMap.get(c.getUuid())-1);


                            System.out.println("********************* BRR *********************");
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
                            for(Map.Entry<String,Integer> entry: customerNumMap.entrySet()){
                                System.out.println("customer: "+entry.getKey()+ " now has "+entry.getValue()+" task being processed.");
                            }
                            System.out.println();
                            System.out.println("********************************************");
                        }

                    }
                }
            }
        }

    }

    // get min value of customerNumMap
    private int getMinValue(Map<String, Integer> map) {
        if (map == null) return 0;
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return (int)obj[0];
    }
}
