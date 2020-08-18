import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Duke {
    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
        System.out.println("Hello! I'm Duke\n" +
                "What can I do for you today? (type: \"help\" to view list of commands)\n" +
                "=========================================================================");

        Scanner scanner = new Scanner(System.in);
        List<Task> pastInputs = new ArrayList<>();
        boolean terminated = false;

        while (!terminated) {
            String userInput = scanner.nextLine();
            //determining user input type via the first word
            String[] splitInput = userInput.split(" ");

            if (splitInput[0].equals("bye")) {
                terminated = true;
                System.out.println("Duke says: Goodbye and have a nice day! :D");
                scanner.close();
            } else if (splitInput[0].equals("help")) {
                System.out.println("list: displays a sequential view of past inputs\n" +
                        "bye: terminates program");
            } else if (splitInput[0].equals("list")){
                if (pastInputs.size() == 0) {
                    System.out.println("Duke says: No past inputs found");
                } else {
                    System.out.println("Here are your tasks:");
                    for (int i = 1; i <= pastInputs.size(); i++) {
                        System.out.println(i + ". " + pastInputs.get(i - 1));
                    }
                    System.out.println("If you wish to mark a task as completed, input \"done <task number>\"");
                }
            } else if (splitInput[0].equals("done")){
                //checks the formatting of user input
                if (splitInput.length <= 2) {
                    try {
                        int taskNumber = Integer.parseInt(splitInput[1]);
                        Task doneTask = pastInputs.get(taskNumber - 1);
                        doneTask.markDone();
                        pastInputs.set(taskNumber - 1, doneTask);
                        System.out.println("Duke says: Good Job! I've marked this task as done:");
                        System.out.println(doneTask);
                    } catch(NumberFormatException ex) {
                        pastInputs.add(new Task(userInput));
                        System.out.println("Duke added into your task list: " + userInput);
                    } catch (Exception ex) {
                        System.out.println("Duke says: Please try again with a valid task number");
                    }
                } else {
                    pastInputs.add(new Task(userInput));
                    System.out.println("Duke added into your task list: " + userInput);
                }
            } else {
                pastInputs.add(new Task(userInput));
                System.out.println("Duke added into your task list: " + userInput);
            }
            System.out.println("=========================================================================");
        }
    }
}
