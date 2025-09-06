
import java.sql.*;
import java.util.*;

// --- Model ---
class Student {
    private int id;
    private String name;
    private int age;
    private String course;

    public Student(int id, String name, int age, String course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public Student(String name, int age, String course) {
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + age + " | " + course;
    }
}

// --- DAO (Database operations) ---
class StudentDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, age, course) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setInt(2, student.getAge());
            stmt.setString(3, student.getCourse());
            stmt.executeUpdate();
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("course")
                ));
            }
        }
        return list;
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET name=?, age=?, course=? WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setInt(2, student.getAge());
            stmt.setString(3, student.getCourse());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

// --- Main Application ---
public class StudentApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentDAO dao = new StudentDAO();

        while (true) {
            System.out.println("\n--- Student Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice = Integer.parseInt(sc.nextLine());

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Age: ");
                        int age = Integer.parseInt(sc.nextLine());
                        System.out.print("Course: ");
                        String course = sc.nextLine();
                        dao.addStudent(new Student(name, age, course));
                        System.out.println("Student added!");
                    }
                    case 2 -> {
                        List<Student> students = dao.getAllStudents();
                        System.out.println("\n--- Student List ---");
                        for (Student s : students) {
                            System.out.println(s);
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter ID to update: ");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.print("New Name: ");
                        String name = sc.nextLine();
                        System.out.print("New Age: ");
                        int age = Integer.parseInt(sc.nextLine());
                        System.out.print("New Course: ");
                        String course = sc.nextLine();
                        dao.updateStudent(new Student(id, name, age, course));
                        System.out.println("Student updated!");
                    }
                    case 4 -> {
                        System.out.print("Enter ID to delete: ");
                        int id = Integer.parseInt(sc.nextLine());
                        dao.deleteStudent(id);
                        System.out.println("Student deleted!");
                    }
                    case 5 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
