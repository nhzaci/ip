import controllers.AppController;

import models.Storage;

public class Duke {
    /** Constant to store the database path for Duke's commands */
    private static final String DATABASE_FILE_PATH = "data/duke.txt";
    /** Constant storing database directory path */
    private static final String DATABASE_DIRECTORY_PATH = "data/";
    /** Storage object to be passed to AppController for reading / writing to db */
    private Storage storage;

    public Duke(String filePath, String directoryPath) {
        this.storage = new Storage(filePath, directoryPath);
    }

    public static void main(String[] args) {
        new Duke(DATABASE_FILE_PATH, DATABASE_DIRECTORY_PATH).run();
    }

    public void run() {
        AppController appController = new AppController(this.storage);
        // start app logic
        appController.start();

    }
}
