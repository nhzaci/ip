package duke.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import duke.exceptions.DukeBlankDetailsException;
import duke.exceptions.DukeBlankTaskException;
import duke.exceptions.DukeCommandNotFoundException;
import duke.exceptions.DukeDateTimeParseException;
import duke.models.Deadline;
import duke.models.Event;
import duke.models.Parser;
import duke.models.Todo;

public class TaskListTest {
    private final TaskList taskList = new TaskList(new ArrayList<>());

    @Test
    void addTask() {
        // command to pass into parser
        String command = "todo hello world";
        // initialise parser
        Parser parser = new Parser(Arrays.asList(command.split(" ")));
        // get command args from parser
        List<String> commandArgs = null;
        try {
            commandArgs = parser.getCommandArgs();
        } catch (DukeCommandNotFoundException e) {
            e.printStackTrace();
        }
        // pass commandArgs into TasksList to create todo
        List<Optional<? extends Todo>> todosList = null;
        try {
            assert commandArgs != null;
            todosList = taskList.addTodo(commandArgs).getFirst().getTodos();
        } catch (DukeBlankTaskException e) {
            e.printStackTrace();
        }
        // get raw message of one item in todosList
        assert todosList != null;
        String rawMessage = todosList.get(0).map(Todo::getRawMessage).orElse("Something went wrong");
        // rawMessage should be "hello world"
        assertEquals("hello world", rawMessage);
    }

    @Test
    void addDeadline() {
        // command to pass into parser
        String command = "deadline finish hello world /by 21/12/2020 2359";
        // initialise parser
        Parser parser = new Parser(Arrays.asList(command.split(" ")));
        // get command args from parser
        List<String> commandArgs = null;
        try {
            commandArgs = parser.getCommandArgs();
        } catch (DukeCommandNotFoundException e) {
            e.printStackTrace();
        }
        // pass commandArgs into TasksList to create todo
        List<Optional<? extends Todo>> todosList = null;
        try {
            assert commandArgs != null;
            try {
                todosList = taskList.addDeadline(commandArgs).getFirst().getTodos();
            } catch (DukeBlankDetailsException | DukeDateTimeParseException e) {
                e.printStackTrace();
            }
        } catch (DukeBlankTaskException e) {
            e.printStackTrace();
        }
        // get raw message of one item in todosList
        assert todosList != null;
        String rawMessage = todosList.get(0).map(Todo::getRawMessage).orElse("Something went wrong");
        String deadlineString = todosList.get(0).map(todo -> ((Deadline) todo).getDeadline())
                .orElse("Something went wrong");
        // rawMessage should be "hello world"
        assertEquals("finish hello world", rawMessage);
        assertEquals("21/12/2020 2359", deadlineString);
    }

    @Test
    void addEvent() {
        // command to pass into parser
        String command = "event say hello world /at 21/12/2021 1300";
        // initialise parser
        Parser parser = new Parser(Arrays.asList(command.split(" ")));
        // get command args from parser
        List<String> commandArgs = null;
        try {
            commandArgs = parser.getCommandArgs();
        } catch (DukeCommandNotFoundException e) {
            e.printStackTrace();
        }
        // pass commandArgs into TasksList to create todo
        List<Optional<? extends Todo>> todosList = null;
        try {
            assert commandArgs != null;
            try {
                todosList = taskList.addEvent(commandArgs).getFirst().getTodos();
            } catch (DukeBlankDetailsException | DukeDateTimeParseException e) {
                e.printStackTrace();
            }
        } catch (DukeBlankTaskException e) {
            e.printStackTrace();
        }
        // get raw message of one item in todosList
        assert todosList != null;
        String rawMessage = todosList.get(0).map(Todo::getRawMessage).orElse("Something went wrong");
        String dateTimeString =
                todosList.get(0).map(todo -> ((Event) todo).getEventTime()).orElse("Something went wrong");
        // rawMessage should be "hello world"
        assertEquals("say hello world", rawMessage);
        assertEquals("21/12/2021 1300", dateTimeString);
    }
}
