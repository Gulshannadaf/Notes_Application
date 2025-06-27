import java.io.*;
import java.util.*;

public class NotesApp {
    static final String NOTES_DIR = "notes";
    static final String LOG_FILE = "log.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        new File(NOTES_DIR).mkdir(); // Ensure directory exists
        while (true) {
            System.out.println("\n📒 Notes Manager");
            System.out.println("1. Create Note");
            System.out.println("2. Read Note");
            System.out.println("3. List Notes");
            System.out.println("4. Delete Note");
            System.out.println("5. Search Notes");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> createNote(sc);
                case 2 -> readNote(sc);
                case 3 -> listNotes();
                case 4 -> deleteNote(sc);
                case 5 -> searchNotes(sc);
                case 6 -> {
                    System.out.println("Exiting... 💤");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void createNote(Scanner sc) {
        System.out.print("Enter note title: ");
        String title = sc.nextLine();
        File noteFile = new File(NOTES_DIR + "/" + title + ".txt");

        try (FileWriter writer = new FileWriter(noteFile, true)) {
            System.out.println("Enter your note (type 'END' to finish):");
            String line;
            while (!(line = sc.nextLine()).equalsIgnoreCase("END")) {
                writer.write(line + System.lineSeparator());
            }
            log("Created/Updated note: " + title);
        } catch (IOException e) {
            System.out.println("Error writing note.");
            logException(e);
        }
    }

    static void readNote(Scanner sc) {
        System.out.print("Enter note title to read: ");
        String title = sc.nextLine();
        File noteFile = new File(NOTES_DIR + "/" + title + ".txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(noteFile))) {
            String line;
            System.out.println("\n--- " + title + " ---");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            log("Read note: " + title);
        } catch (IOException e) {
            System.out.println("Error reading note.");
            logException(e);
        }
    }

    static void listNotes() {
        File folder = new File(NOTES_DIR);
        String[] files = folder.list((dir, name) -> name.endsWith(".txt"));
        System.out.println("\n📁 Notes List:");
        if (files != null && files.length > 0) {
            Arrays.stream(files).forEach(f -> System.out.println("- " + f.replace(".txt", "")));
        } else {
            System.out.println("No notes found.");
        }
        log("Listed all notes");
    }

    static void deleteNote(Scanner sc) {
        System.out.print("Enter note title to delete: ");
        String title = sc.nextLine();
        File noteFile = new File(NOTES_DIR + "/" + title + ".txt");
        if (noteFile.delete()) {
            System.out.println("Deleted: " + title);
            log("Deleted note: " + title);
        } else {
            System.out.println("Note not found.");
        }
    }

    static void searchNotes(Scanner sc) {
        System.out.print("Enter keyword to search: ");
        String keyword = sc.nextLine().toLowerCase();
        File folder = new File(NOTES_DIR);
        String[] files = folder.list((dir, name) -> name.endsWith(".txt"));
        boolean found = false;

        if (files != null) {
            for (String filename : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(new File(NOTES_DIR, filename)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.toLowerCase().contains(keyword)) {
                            System.out.println("🔍 Found in " + filename.replace(".txt", "") + ": " + line);
                            found = true;
                        }
                    }
                } catch (IOException e) {
                    logException(e);
                }
            }
        }

        if (!found) System.out.println("No matches found.");
        log("Searched notes with keyword: " + keyword);
    }

    static void log(String action) {
        try (FileWriter logWriter = new FileWriter(LOG_FILE, true)) {
            logWriter.write(new Date() + ": " + action + "\n");
        } catch (IOException ignored) {}
    }

    static void logException(Exception e) {
        try (FileWriter logWriter = new FileWriter(LOG_FILE, true)) {
            logWriter.write("Exception: " + e.toString() + "\n");
            for (StackTraceElement elem : e.getStackTrace()) {
                logWriter.write("\t at " + elem + "\n");
            }
        } catch (IOException ignored) {}
    }
}
