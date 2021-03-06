package main.java.main;

import main.java.controller.Controller;
import main.java.corrections.Correction;
import main.java.corrections.Datacollection;
import main.java.human.Dozent;
import main.java.human.Student;
import main.java.human.Tutor;
import main.java.human.User;
import main.java.texts.HandIn;
import main.java.texts.Question;
import main.java.userinputs.Parser;
import main.java.userinputs.Terminal;
import java.util.*;

import main.java.controller.UserEnum;

/**
 * The type Main.
 *
 * @author Hannes Schniz
 * @version 1.0
 */
public class Main {

    private static final int ONE = 1;

    private static final int ZERO = 0;

    private static final int TWO = 2;

    private static final int THREE = 3;

    private static final int FOUR = 4;

    private static final int SIX = 6;

    private static Controller cntrl;

    private static Terminal terminal;

    private static Parser parser;

    private static HashMap<String, CommandsEnum> commands;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        setup();
        boolean go = true;
        while (go) {
            try {
                String input = terminal.readln();
                if (!input.equals("")) {
                    String[] userInput = parser.parseInput(input);
                    if (!commands.containsKey(userInput[ZERO])) {
                        terminal.println("command does not exist");
                    } else {
                        CommandsEnum command = commands.get(userInput[ZERO]);
                        switch (command) {
                            case ADD_INSTRUCTOR:
                                parser.checkName(userInput[ONE]);
                                cntrl.addUser(new Dozent(userInput[ONE]));
                                break;
                            case LIST_INSTRUCTOR:
                                listInstructors();
                                break;
                            case ADD_TUTOR:
                                parser.checkName(userInput[ONE]);
                                cntrl.addUser(new Tutor(userInput[ONE]));
                                break;
                            case LIST_TUTOR:
                                listTutors();
                                break;
                            case ADD_STUDENT:
                                addStudent(userInput);
                                break;
                            case LIST_STUDENT:
                                listStudents();
                                break;
                            case ADD_ASIGNMENT:
                                addAssignment(userInput);
                                break;
                            case SUBMIT:
                                handIn(userInput);
                                break;
                            case LIST_SOLUTION:
                                listSolutions(userInput);
                                break;
                            case ADD_REVIEW:
                                addReview(userInput);
                                break;
                            case LIST_REVIEW:
                                listReview(userInput);
                                break;
                            case SEARCH_PLAGIARISM:
                                searchPlagiarism(userInput);
                                break;
                            case ADD_PLAGIARISM:
                                addPlagiarism(userInput);
                                break;
                            case SUMMARY_TASKS:
                                summary();
                                break;
                            case RESET:
                                cntrl = new Controller();
                                break;
                            case QUIT:
                                go = false;
                                break;
                            default:
                                break;

                        }
                    }
                }
            }
            catch (IllegalArgumentException e) {
                terminal.println(e.getMessage());
            }
        }
    }

    /**
     * Sets .
     */
    public static void setup() {
        cntrl = new Controller();
        terminal = new Terminal();
        parser = new Parser();
        commands = new HashMap<String, CommandsEnum>();
        fillMap();
    }

    /**
     * Add assignment.
     *
     * @param userinput the userinput
     */
    public static void addAssignment(String[] userinput) {
        if (userinput.length != TWO) {
            terminal.printError("wrong input");
            return;
        }
        try {
            int idx = cntrl.getIndex();
            Question newQuestion = new Question(userinput[1], idx);
            cntrl.addQuestion(newQuestion);
            terminal.println("assignment id(" + idx + ")");
        }
        catch (IllegalArgumentException e) {
            terminal.printError(e.getMessage());
        }
    }

    /**
     * Print user.
     *
     * @param key the key
     */
    public static void printUser(UserEnum key) {
        User[] listUsers = cntrl.getUserList(key);
        String[] nameString = new String[listUsers.length];
        if (key.equals(UserEnum.STUDENT)) {
            for (int i = 0; i < listUsers.length; i++) {
                nameString[i] = listUsers[i].getName() + " (" + ((Student) listUsers[i]).getId() + ")";
            }
        }
        else {
            for (int i = 0; i < listUsers.length; i++) {
                nameString[i] = listUsers[i].getName();
            }
        }
        terminal.printlines(nameString);

    }

    /**
     * List instructors.
     */
    public static void listInstructors() {
        if (cntrl.hasUser(UserEnum.DOZENT)) {
            printUser(UserEnum.DOZENT);
        }
    }

    /**
     * List tutors.
     */
    public static void listTutors() {
        if (cntrl.hasUser(UserEnum.TUTOR)) {
            printUser(UserEnum.TUTOR);
        }
    }

    /**
     * Add student.
     *
     * @param userInput the user input
     */
    public static void addStudent(String[] userInput) {
        if (userInput.length != THREE) {
            terminal.printError("wrong input");
            return;
        }
        parser.checkName(userInput[ONE]);
        if (!userInput[TWO].matches("[0-9]{5}")) {
            terminal.println("id wrong length");
            return;
        }
        cntrl.addUser(new Student(userInput[ONE], parser.parseNumber(userInput[TWO])));
    }

    /**
     * List students.
     */
    public static void listStudents() {
        if (cntrl.hasUser(UserEnum.STUDENT)) {
            printUser(UserEnum.STUDENT);
        }
    }

    /**
     * Hand in.
     *
     * @param userInput the user input
     */
    public static void handIn(String[] userInput) {
        if (userInput.length != FOUR) {
            terminal.printError("wrong input");
            return;
        }
        try {
            int id = parser.parseNumber(userInput[ONE]);
            int studentId = parser.parseNumber(userInput[TWO]);
            Question question = cntrl.getQuestion(id);
            HandIn newHandIn = new HandIn(userInput[THREE], question, getStudent(studentId));
            cntrl.handIn(newHandIn);
        }
        catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
            terminal.printError(e.getMessage());
        }
    }

    private static Student getStudent(int studentId) throws ClassNotFoundException {
        User[] studentList = cntrl.getUserList(UserEnum.STUDENT);
        for (User user : studentList) {
            Student current = (Student) user;
            if (current.getId() == studentId) {
                return (Student) user;
            }
        }
        throw new ClassNotFoundException("No such Student found");
    }

    /**
     * List solutions.
     *
     * @param userInput the user input
     */
    public static void listSolutions(String[] userInput) {
        if (userInput.length != TWO) {
            terminal.printError("wrong input");
            return;
        }
        HandIn[] handIns = cntrl.getSolutions(parser.parseNumber(userInput[ONE]));
        for (HandIn handIn : handIns) {
            Student student = (Student) handIn.getProducer();
            String name = student.getName() + " ";
            String id = '(' + student.getId() + "): ";
            String text = handIn.getText();
            terminal.println(name + id + text);
        }
    }

    /**
     * Add review.
     *
     * @param userInput the user input
     */
    public static void addReview(String[] userInput) {
        if (userInput.length != SIX) {
            terminal.printError("wrong input");
            return;
        }
        String tutor = userInput[ONE];
        int questionId = parser.parseNumber(userInput[TWO]);
        int studentId = parser.parseNumber(userInput[THREE]);
        int mark = parser.parseNumber(userInput[FOUR]);
        String comment = userInput[userInput.length - 1];
        cntrl.addReview(questionId, studentId, mark, comment, tutor);
    }

    /**
     * List review.
     *
     * @param userInput the user input
     */
    public static void listReview(String[] userInput) {
        if (userInput.length != TWO) {
            terminal.printError("wrong input");
            return;
        }
        try {
            List<Correction> corrections = cntrl.getCorrections(parser.parseNumber(userInput[ONE]));
            for (Correction current : corrections) {
                String tutor = current.getProducer().getName() + ": ";
                String review = current.getText() + " ";
                String numbers = "[" + ((Student) current.getOriginal().getProducer()).getId()
                        + " " + current.getMark() + "]";
                terminal.println(tutor + review + numbers);
            }
        }
        catch (IllegalArgumentException e) {
            terminal.printError(e.getMessage());
        }

    }

    /**
     * Search plagiarism.
     *
     * @param userInput the user input
     */
    public static void searchPlagiarism(String[] userInput) {
        if (userInput.length != TWO) {
            terminal.printError("wrong input");
            return;
        }
        try {
            ArrayList<Datacollection> plagiarisms = cntrl.search(parser.parseNumber(userInput[ONE]));
            plagiarisms.sort(new Comparator<>() {
                @Override
                public int compare(Datacollection o1, Datacollection o2) {
                    return Double.compare(o2.getPercent(), o1.getPercent());
                }
            });
            for (Datacollection current : plagiarisms) {
                if (current.getPercent() > 0) {
                    String studentOne = current.getStudentOne().getId() + " ";
                    String studentTwo = current.getStudentTwo().getId() + " ";
                    String longestString = current.getBiggestString() + " ";
                    String count = current.getLength() + " ";
                    String percent = String.valueOf(current.getPercent());
                    if (percent.substring(percent.indexOf('.')).length() < THREE) {
                        percent = percent + "0";
                    }
                    percent = percent + " ";
                    terminal.println(studentOne + studentTwo + longestString + count + percent);
                }
            }
        }
        catch (IllegalArgumentException e) {
            terminal.printError(e.getMessage());
        }
    }

    /**
     * Add plagiarism.
     *
     * @param userInput the user input
     */
    public static void addPlagiarism(String[] userInput) {
        if (userInput.length != FOUR) {
            terminal.printError("wrong input");
            return;
        }
        try {
            int studentId = parser.parseNumber(userInput[TWO]);
            int questionId = parser.parseNumber(userInput[THREE]);
            String name = userInput[ONE];
            String print = cntrl.markPlagiarism(studentId, questionId, name);
            terminal.println(print);
        }
        catch (IllegalArgumentException e) {
            terminal.printError(e.getMessage());
        }
    }

    /**
     * Summary.
     */
    public static void summary() {
        ArrayList<String[]> list = cntrl.getSummary();
        for (String[] lines : list) {
            if (lines[TWO].length() < FOUR && !lines[TWO].equals("-")) {
                lines[TWO] = lines[TWO] + "0";
            }
            terminal.println(lines[ZERO] + ": " + lines[ONE] + " [" + lines[TWO] + ", "
                    + lines[THREE] + " / " + lines[FOUR] + "]");
        }
    }

    private static void fillMap() {
        commands.put("instructor", CommandsEnum.ADD_INSTRUCTOR);
        commands.put("list-instructors", CommandsEnum.LIST_INSTRUCTOR);
        commands.put("tutor", CommandsEnum.ADD_TUTOR);
        commands.put("list-tutors", CommandsEnum.LIST_TUTOR);
        commands.put("student", CommandsEnum.ADD_STUDENT);
        commands.put("list-students", CommandsEnum.LIST_STUDENT);
        commands.put("assignment", CommandsEnum.ADD_ASIGNMENT);
        commands.put("list-solutions", CommandsEnum.LIST_SOLUTION);
        commands.put("review", CommandsEnum.ADD_REVIEW);
        commands.put("list-reviews", CommandsEnum.LIST_REVIEW);
        commands.put("search-plagiarism", CommandsEnum.SEARCH_PLAGIARISM);
        commands.put("mark-plagiarism", CommandsEnum.ADD_PLAGIARISM);
        commands.put("summary-tasks", CommandsEnum.SUMMARY_TASKS);
        commands.put("reset", CommandsEnum.RESET);
        commands.put("quit", CommandsEnum.QUIT);
        commands.put("submit", CommandsEnum.SUBMIT);
    }
}
