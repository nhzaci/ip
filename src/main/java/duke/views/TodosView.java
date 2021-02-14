package duke.views;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import duke.models.Todo;

public class TodosView {
    /**
     * Explicit definition of the default constructor to create a new Todos View renderer
     */
    public TodosView() {}

    /**
     * Takes in an Optional Todo and renders it out into a String
     * @param todoToRender Optional Todo to be rendered
     * @return String which renders out how the information carried by the Todo
     */
    private static String renderTodoLine(Optional<? extends Todo> todoToRender) {
        return todoToRender.map(Todo::getMessage).orElse("Empty Todo");
    }

    /**
     * Takes in a matching TodosList and returns a rendered view of the todos with a specified
     * header
     *
     * @param matchingTodosList List of Optional Todos that matches the keywords to be rendered
     * @return String containing tasks that match the input keywords
     */
    public static String formatMatchedTodosToString(List<Optional<? extends Todo>> matchingTodosList) {
        return String.format("Here are the matching tasks in your list:\n%s",
                IntStream.range(0, matchingTodosList.size())
                        .mapToObj(idx -> String.format("%d.%s", idx + 1,
                                renderTodoLine(matchingTodosList.get(idx))))
                        .collect(Collectors.joining("\n")));
    }

    /**
     * Turns the todosList into a stream of messages from Todos and output them with a new line in
     * between each Todo
     * @param todosList List of optional todos passed in from TodosController
     * @return String containing rendered view of listed Todos
     */
    public static String formatListOfTodosToString(List<Optional<? extends Todo>> todosList) {
        return String.format("Here are the tasks in your list:\n%s",
                IntStream.range(0, todosList.size()).mapToObj(
                    idx -> String.format("%d.%s", idx + 1, renderTodoLine(todosList.get(idx))))
                        .collect(Collectors.joining("\n")));
    }

    /**
     * Returns "Got it, Task has been amended to:" when task is amended
     * @param updatedTodo Optional Todo object containing a new Todo to be printed
     * @return String showing todo that got updated
     */
    public static String updateTodoReply(Optional<? extends Todo> updatedTodo) {
        return String.format("Got it! Task has been amended to:\n%s\n.",
                renderTodoLine(updatedTodo));
    }

    /**
     * Returns "Got it, I've added this task:", followed by the message contained in the new todo
     * @param newTodo Optional Todo object containing a new Todo to be printed
     * @param listSize Integer list size taken to return the number of tasks user currently has
     * @return String showing todo that got added along with todos list size
     */
    public static String addTodoReply(Optional<? extends Todo> newTodo, int listSize) {
        return String.format("Got it! I've added this task:\n%s\nNow you have %d tasks in the list.",
                renderTodoLine(newTodo), listSize);
    }

    /**
     * Returns  "Noted. I've removed this task:", followed by message contained in new Todo
     * @param deletedTodo Optional Todo object containing the Todo to be deleted
     * @param listSize Integer list size taken to return number of tasks user currently has
     * @return String containing reply to deleting a Todo
     */
    public static String deleteTodoReply(Optional<? extends Todo> deletedTodo, int listSize) {
        return String.format("Noted. I've removed this task:\n%s\nNow you have %d tasks in the list.",
                renderTodoLine(deletedTodo), listSize);
    }

    /**
     * Adds text indicating todo is marked as done and renders the String to show the Todo
     * @param newTodo Optional Todo to be marked as Done
     * @return String containing reply to marking a todo as done
     */
    public static String markTodoAsDoneReply(Optional<? extends Todo> newTodo) {
        return String.format("Nice! I've marked this task as done:\n%s", renderTodoLine(newTodo));
    }
}
