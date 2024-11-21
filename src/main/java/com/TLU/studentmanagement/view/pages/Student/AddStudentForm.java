package main.java.com.TLU.studentmanagement.view.pages.Student;

import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.controller.majors.MajorController;
import main.java.com.TLU.studentmanagement.controller.teacher.TeacherController;
import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.view.pages.Courses.CoursePanel;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddStudentForm extends JDialog {

  private JTextField nameField, msvField, classField, emailField, genderField, yearField;
  private JComboBox<String> gvcnComboBox, majorComboBox;
  private List<Teacher> teachers;
  private List<Major> majors;
  private StudentsPanel studentsPanel;
  private CoursePanel coursePanel;
  private UserController userController;

  public AddStudentForm(JFrame parent, List<Teacher> teachers, List<Major> majors, StudentsPanel studentsPanel) {
    super(parent, "Thêm sinh viên", true);
    this.teachers = teachers;
    this.majors = majors;
    this.studentsPanel = studentsPanel;
    userController = new UserController();
    initUI();
  }

  private void initUI() {
    setLayout(new GridLayout(10, 2));

    add(new JLabel("Tên:"));
    nameField = new JTextField();
    add(nameField);

    add(new JLabel("Mã sinh viên:"));
    msvField = new JTextField();
    add(msvField);

    add(new JLabel("Năm:"));
    yearField = new JTextField();
    add(yearField);

    add(new JLabel("Giáo viên chủ nhiệm:"));
    gvcnComboBox = new JComboBox<>();
    populateTeacherComboBox(); // Populate the combo box with teachers
    add(gvcnComboBox);

    add(new JLabel("Giới tính:"));
    genderField = new JTextField();
    add(genderField);

    add(new JLabel("Lớp:"));
    classField = new JTextField();
    add(classField);

    add(new JLabel("Email:"));
    emailField = new JTextField();
    add(emailField);

    add(new JLabel("Chuyên ngành:"));
    majorComboBox = new JComboBox<>();
    populateMajorComboBox(); // Populate the combo box with majors
    add(majorComboBox);

    JButton addButton = new JButton("Thêm");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addStudent();
      }
    });
    add(addButton);

    JButton cancelButton = new JButton("Hủy");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    add(cancelButton);

    pack();
    setLocationRelativeTo(getParent());
  }

  private void populateTeacherComboBox() {
    gvcnComboBox.removeAllItems();
    for (Teacher teacher : teachers) {
      gvcnComboBox.addItem(teacher.getFullName());
    }
  }

  private void populateMajorComboBox() {
    majorComboBox.removeAllItems();
    for (Major major : majors) {
      majorComboBox.addItem(major.getName());
    }
  }

  private void addStudent() {
    String name = nameField.getText();
    String msv = msvField.getText();
    String year = yearField.getText();
    String gvcnName = (String) gvcnComboBox.getSelectedItem();
    String gender = genderField.getText();
    String className = classField.getText();
    String email = emailField.getText();
    String majorName = (String) majorComboBox.getSelectedItem();

    Teacher selectedTeacher = null;
    for (Teacher teacher : teachers) {
      if (teacher.getFullName().equals(gvcnName)) {
        selectedTeacher = teacher;
        break;
      }
    }

    Major selectedMajor = null;
    for (Major major : majors) {
      if (major.getName().equals(majorName)) {
        selectedMajor = major;
        break;
      }
    }

    if (selectedTeacher != null && selectedMajor != null) {
      try {
        User user = new User();
        user.setFullName(name);
        user.setMsv(msv);
        user.setYear(year);
        user.setGvcn(selectedTeacher.getId());
        user.setGender(gender);
        user.setClassName(className);
        user.setEmail(email);
        user.setMajorId(selectedMajor.getId());

        JSONObject response = userController.createUser(user);
        if (response != null && response.has("status") && response.getString("status").equals("error")) {
          // Xử lý lỗi từ server
          String errorMessage = response.getString("message");
          Notifications.getInstance().show(Notifications.Type.ERROR, errorMessage);
        } else {
          // Xử lý thành công
          Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm sinh viên thành công.");
          studentsPanel.getAllStudents();
          dispose();
        }
//                coursePanel.getAllCourses(); // Refresh courses or students list
      } catch (Exception ex) {
        ex.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Add student error.");
      }
    } else {
      Notifications.getInstance().show(Notifications.Type.ERROR, "Selected teacher or major not found.");
    }
  }

  public void refreshTeachers() {
    try {
      teachers = TeacherController.getAllTeachers();
      populateTeacherComboBox();
    } catch (Exception ex) {
      ex.printStackTrace();
      Notifications.getInstance().show(Notifications.Type.ERROR, "Refresh teachers error.");
    }
  }

  public void refreshMajors() {
    try {
      majors = MajorController.getAllMajors();
      populateMajorComboBox();
    } catch (Exception ex) {
      ex.printStackTrace();
      Notifications.getInstance().show(Notifications.Type.ERROR, "Refresh majors error.");
    }
  }

  public static void showAddStudentForm(Component parent, List<Teacher> teachers, List<Major> majors, StudentsPanel studentsPanel) {
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
    AddStudentForm form = new AddStudentForm(frame, teachers, majors, studentsPanel);
    form.setVisible(true);
  }
}
