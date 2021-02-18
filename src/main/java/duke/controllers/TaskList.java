package duke.controllers;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import duke.exceptions.DukeBlankDetailsException;
import duke.exceptions.DukeBlankTaskException;
import duke.exceptions.DukeDateTimeParseException;
import duke.exceptions.DukeInvalidFlagException;
import duke.exceptions.DukeTaskIndexOutOfRangeException;
import duke.models.Deadline;
import duke.models.Event;
import duke.models.Flags;
import duke.models.Pair;
import duke.models.Parser;
import duke.models.Todo;

public class TaskList {
    /** index offset constant for 1-based indexing of todos to client */
    private static final int ONE_BASED_INDEX_OFFSET = 1;

    /** todosList contains the state of the todos */
    private final List<Optional<? extends Todo>> todos;

    /**
     * Constructor of TodosController which takes in an existing List of Optional Todos
     * @param todos is an existing List of Optional Todos
     */
    public TaskList(List<Optional<? extends Todo>> todos) {
        this.todos = todos;
    }

    /**
     * Returns int size of the todos list in the controller
     * @return size of todos list
     */
    public int todosSize() {
        return this.todos.size();
    }

    /**
     * Getter for todosList attr in TodosController
     *
     * @return List of Optionals of anything extending Todo contained in TodosController
     */
    public List<Optional<? extends Todo>> getTodos() {
        return this.todos;
    }

    /**
     * Takes in a list of keywords and prints todos with messages that contains any of the keywords
     * passed in
     * @param keywordList String list of keywords to be matched
     * @return list of todos checked to see if any strings match any given keyword
     */
    public List<Optional<? extends Todo>> findByKeyword(List<String> keywordList) {
        return this.todos.stream().filter(optTodo -> {
            // check if todo message contains keyword
            return optTodo.map(Todo::getMessage).map(message -> {
                // split message by space as delimiter
                List<String> messagesSplitByWhitespace = Arrays.asList(message.split(" "));
                // if any part of split message is contained in keywordList, return true
                return messagesSplitByWhitespace.stream().anyMatch(keywordList::contains);
            }).orElse(false);
        }).collect(Collectors.toList());
    }

    /**
     * Adds a new Todo to the todosList and returns a Pair of TaskList and Optional Todo back to UI
     * @param newTodoList contains the new Todo that must not be an empty array
     * @return Pair of new updated TaskList and added Todo
     * @throws DukeBlankTaskException when user types in 'todo' but has nothing afterwards
     */
    public Pair<TaskList, Optional<? extends Todo>> addTodo(List<String> newTodoList) throws DukeBlankTaskException {
        if (newTodoList.size() == 0) {
            throw new DukeBlankTaskException("The Todo you are trying to add cannot be blank!");
        }

        // create new Todo object
        Optional<? extends Todo> newTodoObject =
                Optional.of(new Todo(String.join(" ", newTodoList)));

        // return pair of tasklist and new created todo
        return new Pair<>(new TaskList(Stream.concat(this.todos.stream(), Stream.of(newTodoObject))
                .collect(Collectors.toList())), newTodoObject);
    }

    /**
     * Deletes a Todo from the list of the todos controller and returns a Pair of TaskList
     * and the deleted Todo
     * @param deleteTodoArgs is a list of size 1, containing the index of the todo to delete
     * @return Pair of updated TaskList and deleted Todo
     * @throws DukeBlankTaskException when user specifies the delete command without providing an
     *         index of the todo to delete
     * @throws DukeTaskIndexOutOfRangeException when user specifies an index that is out of the
     *         range of the list size of todos in the controller
     */
    public Pair<TaskList, Optional<? extends Todo>> deleteTodo(List<String> deleteTodoArgs)
            throws DukeBlankTaskException, DukeTaskIndexOutOfRangeException {
        // check if args is empty
        if (deleteTodoArgs.size() == 0) {
            throw new DukeBlankTaskException(
                    "Please input an index for the Todo you want to delete!");
        }

        // get index of todo to delete
        int idxDelete = Integer.parseInt(deleteTodoArgs.get(0)) - 1;
        if (idxDelete >= this.todos.size()) {
            throw new DukeTaskIndexOutOfRangeException("The index you input has an index that "
                            + "is beyond the range of the number of tasks you "
                            + "currently have. Please try again.");
        }

        // remove from stream and return pair
        return new Pair<>(new TaskList(
                IntStream.range(0, this.todos.size()).filter(idx -> idx != idxDelete)
                        .mapToObj(this.todos::get).collect(Collectors.toList())),
                this.todos.get(idxDelete));
    }

    /**
     * Command can contain -t flag for time and -m flag for message update, then updates task respectively,
     * if no flag, update whole task
     * @param updateTodoCommandArgsSplitByWhitespace params passed in from user in CLI
     * @return pair of updated tasklist and updatedtask to be returned as user feedback
     * @throws DukeBlankTaskException If there is no specified task description for the new updated task
     * @throws DukeTaskIndexOutOfRangeException if the input index is out of range of task list size
     * @throws DukeDateTimeParseException if there is an error in the date time input by the user
     * @throws DukeInvalidFlagException if user inputs more than one flag into the update method
     * @throws DukeBlankDetailsException if the user does not add a /at or /by flag for Event or Deadlines to amend
     *          and if no -m or -t flags are used
     */
    public Pair<TaskList, Optional<? extends Todo>> updateTodo(List<String> updateTodoCommandArgsSplitByWhitespace)
            throws DukeBlankTaskException, DukeTaskIndexOutOfRangeException, DukeDateTimeParseException,
            DukeInvalidFlagException, DukeBlankDetailsException {
        if (updateTodoCommandArgsSplitByWhitespace.size() == 0) {
            throw new DukeBlankTaskException("The new task you are trying to update it to cannot be blank");
        }

        // check if the command contains more than one flag
        // flag -> means contains '-' as first char and length of 2
        long noOfFlags = updateTodoCommandArgsSplitByWhitespace.stream()
                .filter(arg -> arg.equals("-m") || arg.equals("-t"))
                .count();

        if (noOfFlags > 1) {
            throw new DukeInvalidFlagException("Please use only a single dash flag in your update command");
        }

        // updateTodo = [idx, flag with message OR full message with time]
        int idxToUpdate = Integer.parseInt(updateTodoCommandArgsSplitByWhitespace.get(0)) - ONE_BASED_INDEX_OFFSET;

        if (idxToUpdate < 0 || idxToUpdate >= this.todos.size()) {
            throw new DukeTaskIndexOutOfRangeException("The index you specified for the task does not exist, "
                    + "please try again");
        }

        // get todo to be updated if in range
        Optional<? extends Todo> todoToUpdate = this.todos.get(idxToUpdate);

        // get flag from command
        Flags flag = Parser.getFlag(updateTodoCommandArgsSplitByWhitespace.get(1));

        // if there's a flag included, the message to start iterating starts later
        int idxToStartIterating = (flag != Flags.NONE ? 2 : 1);

        ArrayList<String> todoMessageArgs = new ArrayList<>();
        ArrayList<String> todoEventTimeArgs = new ArrayList<>();

        // iterate through list to find where escape character is
        // once found, everything after is part of the deadline
        updateTodoCommandArgsSplitByWhitespace
                .subList(idxToStartIterating, updateTodoCommandArgsSplitByWhitespace.size())
                .forEach(substring -> {
                    if (substring.equals("/by") || substring.equals("/at")) {
                        todoEventTimeArgs.add(substring);
                    } else if (todoEventTimeArgs.size() == 0) {
                        todoMessageArgs.add(substring);
                    } else {
                        todoEventTimeArgs.add(substring);
                    }
        });

        if (todoMessageArgs.size() == 0) {
            throw new DukeBlankTaskException("Please enter a task description to update your current task");
        }

        Optional<? extends Todo> updatedTodo;
        // do stateful operation of returning a new object depending on what type it is and what flag was used
        try {
            updatedTodo = todoToUpdate.map(todo -> {
                if (todo instanceof Event) {
                    Event event = (Event) todo;
                    // @formatter:off
                    switch(flag) {
                    case MESSAGE:
                        return event.updateMessage(String.join(" ", todoMessageArgs));
                    case TIME:
                        return event.updateTime(String.join(" ", todoMessageArgs));
                    case NONE:
                        return event.update(
                                String.join(" ", todoMessageArgs),
                                String.join(" ", todoEventTimeArgs.subList(1, todoEventTimeArgs.size()))
                        );
                    }
                } else if (todo instanceof Deadline) {
                    Deadline deadline = (Deadline) todo;
                    // @formatter:off
                    switch(flag) {
                    case MESSAGE:
                        return deadline.updateMessage(String.join(" ", todoMessageArgs));
                    case TIME:
                        return deadline.updateTime(String.join(" ", todoMessageArgs));
                    case NONE:
                        return deadline.update(
                                String.join(" ", todoMessageArgs),
                                String.join(" ", todoEventTimeArgs.subList(1, todoEventTimeArgs.size()))
                        );
                    }
                }
                return todo.updateMessage(String.join(" ", todoMessageArgs));
            });
        } catch (DateTimeParseException e) {
            throw new DukeDateTimeParseException(
                    "Please format your date to be DD/MM/YYYY HHMM");
        } catch (Exception e) {
            if (flag == Flags.NONE) {
                throw new DukeBlankDetailsException("Please ensure you have entered the date if "
                        + "you are updating an Event or a Deadline, after the task description"
                        + "after adding a /at or /by or use a -m flag to update only the message");
            }
        }

        return new Pair<>(new TaskList(IntStream.range(0, this.todos.size())
                .mapToObj(idx -> idx == idxToUpdate
                        ? updatedTodo
                        : this.todos.get(idx))
                .collect(Collectors.toList())), updatedTodo);
    }

    /**
     * Takes in the list containing details about the new deadline and returns a Pair of new
     * TaskList with updated tasks and the new deadline added
     * @param deadlineCommandArgsSplitByWhitespace takes in list of arguments provided to the command for processing into
     *        a Deadline object
     * @return Pair of TaskList and added Deadline
     * @throws DukeBlankTaskException Exception is thrown when user does not add in any details
     *         after typing the 'deadline' command
     * @throws DukeBlankDetailsException Exception is thrown when user tries to define an deadline,
     *         without adding /by details for the even
     * @throws DukeDateTimeParseException Exception is thrown when date time passed into CLI is of
     *         the wrong format
     */
    public Pair<TaskList, Optional<? extends Todo>> addDeadline(List<String> deadlineCommandArgsSplitByWhitespace)
            throws DukeBlankTaskException, DukeBlankDetailsException, DukeDateTimeParseException {
        if (deadlineCommandArgsSplitByWhitespace.size() == 0) {
            throw new DukeBlankTaskException("The Deadline you are trying to add cannot be blank!");
        }

        ArrayList<String> newDeadlineMessages = new ArrayList<>();
        ArrayList<String> newDeadlineDateTimeStrings = new ArrayList<>();

        // iterate through list to find where escape character is
        // once found, everything after is part of the deadline
        deadlineCommandArgsSplitByWhitespace.stream().forEach(substring -> {
            if (substring.equals("/by")) {
                newDeadlineDateTimeStrings.add(substring);
            } else if (newDeadlineDateTimeStrings.size() == 0) {
                newDeadlineMessages.add(substring);
            } else {
                newDeadlineDateTimeStrings.add(substring);
            }
        });

        // if no message, throw exception
        if (newDeadlineMessages.size() == 0) {
            throw new DukeBlankTaskException("Please define a task message for your Deadline");
        }

        // if no deadline input or /by without any deadline, throw exception
        if (newDeadlineDateTimeStrings.size() <= 1) {
            // @formatter:off
            String exceptionMessage = "Please add a /by followed by the deadline time and date in DD/MM/YYYY "
                    + "HHMM to specify a time and date for the Deadline task. If there is no time for "
                    + "this deadline perhaps consider creating a todo instead.";
            throw new DukeBlankDetailsException(exceptionMessage);
        }

        // Create new Deadline object, slicing newDeadlineDateTimeStrings array from index 1 since we
        // added the '/by' which shouldn't be in the actual Deadline object
        // creating a new deadline might throw an exception if the date time is in the
        // wrong format
        Optional<Deadline> newDeadline;
        try {
            newDeadline = Optional.of(new Deadline(String.join(" ", newDeadlineMessages),
                    String.join(" ", newDeadlineDateTimeStrings.subList(1, newDeadlineDateTimeStrings.size()))));
        } catch (DateTimeParseException e) {
            throw new DukeDateTimeParseException(
                    "Please format your date after /by to be DD/MM/YYYY HHMM");
        }

        // return new pair
        return new Pair<>(new TaskList(Stream.concat(this.todos.stream(), Stream.of(newDeadline))
                .collect(Collectors.toList())), newDeadline);
    }

    /**
     * Takes in the list containing details about the new deadline and returns a Pair of updated
     * TaskList and added Event
     * @param newEventCommandArgs takes in list of arguments provided to the command for processing into a
     *        Event object
     * @return Pair of updated TaskList and added Event
     * @throws DukeBlankTaskException Exception is thrown when user does not add in any details
     *         after typing the 'event' command
     * @throws DukeBlankDetailsException Exception is thrown when user tries to define an event,
     *         without adding /at details for the event
     * @throws DukeDateTimeParseException Exception is thrown when date time passed into CLI is of
     *         the wrong format
     */
    public Pair<TaskList, Optional<? extends Todo>> addEvent(List<String> newEventCommandArgs)
            throws DukeBlankDetailsException, DukeBlankTaskException, DukeDateTimeParseException {
        // if list is empty, throw error
        if (newEventCommandArgs.size() == 0) {
            throw new DukeBlankTaskException("The Event you are trying to add cannot be blank!");
        }

        ArrayList<String> newEventMessages = new ArrayList<>();
        // newEventDateTimeStrings will contain /at command
        ArrayList<String> newEventDateTimeStrings = new ArrayList<>();

        // iterate through list to find where escape character is
        // once found, everything after is part of the deadline
        newEventCommandArgs.forEach(substring -> {
            if (substring.equals("/at")) {
                newEventDateTimeStrings.add(substring);
            } else if (newEventDateTimeStrings.size() == 0) {
                newEventMessages.add(substring);
            } else {
                newEventDateTimeStrings.add(substring);
            }
        });

        // if no message, throw exception
        if (newEventMessages.size() == 0) {
            throw new DukeBlankTaskException("Please define a task message for your Event");
        }

        // if no event input or /at without any event, throw exception
        if (newEventDateTimeStrings.size() <= 1) {
            // @formatter:off
            String exceptionMessage = "Please add a /at followed by the event time and date in DD/MM/YYYY "
                            + "HHMM to specify a time and date for the Event task. If there is no time for "
                            + "this event perhaps consider creating a todo instead.";
            throw new DukeBlankDetailsException(exceptionMessage);
        }

        // Create new Event object, slicing newEventDateTimeStrings array from index 1 since we
        // added the '/at' which shouldn't be in the actual Event object
        // Creating an event might throw an exception if the date is in the wrong format
        Optional<Event> newEvent;
        try {
            newEvent = Optional.of(new Event(String.join(" ", newEventMessages),
                    String.join(" ", newEventDateTimeStrings.subList(1, newEventDateTimeStrings.size()))));
        } catch (DateTimeParseException e) {
            throw new DukeDateTimeParseException(
                    "Please format your date after /at to be DD/MM/YYYY HHMM");
        }

        // return new pair
        return new Pair<>(new TaskList(Stream.concat(this.todos.stream(), Stream.of(newEvent))
                .collect(Collectors.toList())), newEvent);
    }

    /**
     * Index of todo passed in to be marked as done is lesser than length of todosList,
     * else there would be an ArrayOutOfBoundsException thrown
     * @param doneCommandArgs should be a List of Strings with size 1 containing one argument that is the ID of
     *        which todo to mark as done and uses a 1-based indexing of the todos
     * @return Pair of TaskList and todo that is marked as done
     * @throws DukeTaskIndexOutOfRangeException Exception is thrown when the user specifies a task
     *         index that is out of range.
     */
    public Pair<TaskList, Optional<? extends Todo>> markAsDone(List<String> doneCommandArgs) throws DukeTaskIndexOutOfRangeException {
        int idxIsDone = Integer.parseInt(doneCommandArgs.get(0)) - ONE_BASED_INDEX_OFFSET;
        if (idxIsDone >= this.todos.size()) {
            throw new DukeTaskIndexOutOfRangeException("The index you input has an index that is "
                            + "beyond the range of the number of tasks you currently have. "
                            + "Please try again.");
        }

        // Get Todo to be marked as done
        Optional<? extends Todo> doneTodo = this.todos.get(idxIsDone).map(Todo::markAsDone);

        // Get new TaskList containing the new Todo
        TaskList newTaskListWithTodoMarkedAsDone = new TaskList(IntStream.range(0, this.todos.size())
                .mapToObj(idx -> {
                    // check if idx is equal to done object
                    // if it is, return done object,
                    // else return original object
                    return idx == idxIsDone
                            ? doneTodo
                            : this.todos.get(idx);
                }).collect(Collectors.toList()));

        // return Pair of new Task List and todo that's done
        return new Pair<>(newTaskListWithTodoMarkedAsDone, doneTodo);
    }
}
