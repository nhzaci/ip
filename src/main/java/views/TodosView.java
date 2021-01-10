package views;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import models.Todo;

public class TodosView {
    String spacing = "---";

    public TodosView() {
    }

    /**
     * Takes in an Optional Todo and renders it out into a String
     * 
     * @param todoRender Optional Todo to be rendered
     * @return String which renders out how the information carried by the Todo
     */
    private String renderTodoLine(Optional<? extends Todo> todoToRender) {
        return todoToRender.map(todo -> todo.getMessage()).orElse("Empty Todo");
    }

    /**
     * Turns the todosList into a stream of messages from Todos and output them with
     * a new line in between each Todo
     * 
     * @param todosList List of optional todos passed in from TodosController
     */
    public void listTodos(List<Optional<? extends Todo>> todosList) {
        printWithSpacing(String.format("Here are the tasks in your list:\n%s",
                IntStream.range(0, todosList.size())
                        .mapToObj(idx -> String.format("%d.%s", idx + 1, renderTodoLine(todosList.get(idx))))
                        .collect(Collectors.joining("\n"))));
    }

    /**
     * Prints 'Got it, I've added this task:', followed by the message contained in
     * the new todo
     * 
     * @param newTodo Optional Todo object containing a new Todo to be printed
     * @throws Exception if the newTodo object is empty
     */
    public void added(Optional<? extends Todo> newTodo, int listSize) throws Exception {
        printWithSpacing(String.format("Got it! I've added this task:\n%s\nNow you have %d tasks in the list.",
                renderTodoLine(newTodo), listSize));
    }

    /**
     * Adds text indicating todo is marked as done and renders the String to show
     * the Todo
     * 
     * @param newTodo Optional Todo to be marked as Done
     * @throws Exception if newTodo object is empty
     */
    public void markAsDone(Optional<? extends Todo> newTodo) throws Exception {
        printWithSpacing(String.format("Nice! I've marked this task as done:\n%s", renderTodoLine(newTodo)));
    }

    /**
     * Private method printWithSpacing adds spacing around the text passed in to be
     * printed
     * 
     * @param text String to be printed with spacing put around it
     */
    private void printWithSpacing(String text) {
        System.out.println(String.format("\n%s\n%s\n%s\n", spacing, text, spacing));
    }
}
