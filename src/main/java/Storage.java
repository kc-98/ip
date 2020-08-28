import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that handles any storage related changes.
 *
 * @author Kai Chao
 * @version 1.0
 * @since 26-08-2020
 */
public class Storage {

    private String dirPath;
    private String filePath;

    Storage(String dirPath, String filePath) {
        this.dirPath = dirPath;
        this.filePath = filePath;
    }

    /**
     * Loads data from the hard drive, creates any missing directories or files.
     *
     * @return An arraylist consisting of the saved tasks.
     */
    //handles loading of data
    public List<Task> loadData() {
        try {
            File dataDir = new File(this.dirPath);
            File tasks = new File(this.filePath);

            if (dataDir.exists()) {
                //directory exists, now check if tasks.txt exists
                boolean isCreated = tasks.createNewFile();
                if (isCreated) {
                    //tasks.txt does not exist
                    return new ArrayList<>();
                } else {
                    //tasks.txt exists
                    List<Task> current = new ArrayList<>();
                    BufferedReader br = new BufferedReader(new FileReader(this.filePath));
                    String line = br.readLine();

                    while (line != null) {
                        //parses the string to become a task
                        current.add(readTaskFromFile(line));
                        line = br.readLine();
                    }
                    br.close();
                    return current;
                }
            } else {
                //if directory does not exist, make directory and tasks txt file
                if (dataDir.mkdir()) {
                    //data folder directory successful, make tasks.txt file now
                    tasks.createNewFile();
                    return new ArrayList<>();
                } else {
                    //fail to make data folder directory
                    System.out.println("Error: Directory failed to be created");
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Creates a new tasks.txt file with updated tasks that overwrites the existing one.
     *
     * @param taskList The new taskList.
     */
    public void updateTaskFile(TaskList taskList) {
        try {
            //create temp text file
            String tempDir = this.dirPath + "/temp.txt";
            File temp = new File(tempDir);

            File oldFile = new File(this.filePath);
            if (temp.createNewFile()) {
                BufferedWriter output = new BufferedWriter(new FileWriter(temp, true));
                String toAppend;
                for (int i = 0; i < taskList.noOfTasks(); i++) {
                    Task curr = taskList.getTask(i);
                    //adds the updated task list to temp file by converting it to the
                    //form: <type>!@%<status>!@%<description>!@%<date/time(if applicable)>
                    //so that our parser can read
                    if (curr instanceof ToDo) {
                        ToDo todo = (ToDo) curr;
                        toAppend = "T!@%" + (todo.isDone ? "1!@%" : "0!@%") + todo.description
                                + "!@%";
                    } else if (curr instanceof Deadline) {
                        Deadline deadline = (Deadline) curr;
                        toAppend = "D!@%" + (deadline.isDone ? "1!@%" : "0!@%")
                                + deadline.description + "!@%"
                                + (deadline.localDate != null ? deadline.localDate : "") + "!@%"
                                + (deadline.localTime != null ? deadline.localTime : "");
                    } else {
                        Event event = (Event) curr;
                        toAppend = "E!@%" + (event.isDone ? "1!@%" : "0!@%") + event.description
                                + "!@%" + (event.localDate != null ? event.localDate : "") + "!@%"
                                + (event.localTime != null ? event.localTime : "");
                    }
                    output.write(toAppend);
                    output.newLine();
                }
                output.close();

                //checks if old file is deleted and new file is renamed
                if (oldFile.delete()) {
                    if (!temp.renameTo(oldFile)) {
                        System.out.println("Error in renaming new file.");
                    }
                } else {
                    System.out.println("Error in deleting old file");
                }
            } else {
                System.out.println("Error: temp file not created");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads a string of line in the file and returns a task.
     *
     * @param string The string to be read and converted into a task.
     * @return The converted task.
     */
    public Task readTaskFromFile(String string) {
        //<type>!@%<status>!@%<description>!@%<date>(if applicable)!@%<time>(if applicable)
        String[] data = string.split("!@%");
        String type = data[0];
        String status = data[1];
        String description = data[2];
        String date = "";
        String time = "";

        //checks if the data consists of date and time using the split array size
        if (data.length == 4) {
            date = data[3];
        } else if (data.length == 5) {
            date = data[3];
            time = data[4];
        }

        //returns a task based on type, marks a task as done if status is 1
        if (type.equals("T")) {
            ToDo todo = new ToDo(description);
            if (status.equals("1")) {
                todo.markDone();
            }
            return todo;
        } else if (type.equals("D")) {
            Deadline deadline = new Deadline(description, date, time);
            if (status.equals("1")) {
                deadline.markDone();
            }
            return deadline;
        } else {
            Event event = new Event(description, date, time);
            if (status.equals("1")) {
                event.markDone();
            }
            return event;
        }
    }
}
