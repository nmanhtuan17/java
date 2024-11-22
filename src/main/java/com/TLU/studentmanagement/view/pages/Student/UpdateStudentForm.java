package main.java.com.TLU.studentmanagement.view.pages.Student;

import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UpdateStudentForm extends JDialog {
  private JTextField nameField, msvField, yearField, genderField, classField, emailField;
  private JComboBox<String> gvcnComboBox, majorComboBox;
  private User user;
  private List<Teacher> teachers;
  private List<Major> majors;
  private StudentsPanel studentsPanel;

  public UpdateStudentForm(User user, StudentsPanel studentsPanel, List<Teacher> teachers, List<Major> majors) {
    this.user = user;
    this.studentsPanel = studentsPanel;
    this.teachers = teachers;
    this.majors = majors;

    setTitle("Cập nhật sinh viên");
    setModal(true);
    setLayout(new GridLayout(10, 2, 10, 10));
    setSize(400, 400);
    setLocationRelativeTo(null);

    Border padding = new EmptyBorder(10, 10, 10, 10);
    JPanel panel = (JPanel) getContentPane();
    panel.setBorder(padding);
    panel.setLayout(new GridLayout(10, 2, 10, 10));

    add(new JLabel("Tên:"));
    nameField = new JTextField(user.getFullName());
    add(nameField);

    add(new JLabel("Mã sinh viên:"));
    msvField = new JTextField(user.getMsv());
    add(msvField);
//
//        add(new JLabel("Năm:"));
//        yearField = new JTextField(user.getYear());
//        add(yearField);

    add(new JLabel("Giáo viên chủ nhiệm:"));
    gvcnComboBox = new JComboBox<>();
    populateTeacherComboBox();
    gvcnComboBox.setSelectedItem(user.getGvcn());
    add(gvcnComboBox);

    add(new JLabel("Giới tính:"));
    genderField = new JTextField(user.getGender());
    add(genderField);

    add(new JLabel("Lớp:"));
    classField = new JTextField(user.getClassName());
    add(classField);

    add(new JLabel("Email:"));
    emailField = new JTextField(user.getEmail());
    add(emailField);

    add(new JLabel("Chuyên ngành:"));
    majorComboBox = new JComboBox<>();
    populateMajorComboBox();
    majorComboBox.setSelectedItem(user.getMajorId());
    add(majorComboBox);

    JButton updateButton = new JButton("Cập nhật");
    updateButton.addActionListener(e -> updateUser());
    add(updateButton);

    JButton cancelButton = new JButton("Hủy");
    cancelButton.addActionListener(e -> dispose());
    add(cancelButton);

    pack();
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

  private void updateUser() {
    String updatedName = nameField.getText();
    String updatedMsv = msvField.getText();
    String selectedTeacherName = (String) gvcnComboBox.getSelectedItem();
    String updatedGender = genderField.getText();
    String updatedClassName = classField.getText();
    String updatedEmail = emailField.getText();
    String selectedMajorName = (String) majorComboBox.getSelectedItem();

    Teacher selectedTeacher = null;
    for (Teacher teacher : teachers) {
      if (teacher.getFullName().equals(selectedTeacherName)) {
        selectedTeacher = teacher;
        break;
      }
    }

    Major selectedMajor = null;
    for (Major major : majors) {
      if (major.getName().equals(selectedMajorName)) {
        selectedMajor = major;
        break;
      }
    }

    if (selectedTeacher != null && selectedMajor != null) {
      user.setFullName(updatedName);
      user.setMsv(updatedMsv);
//            user.setYear(updatedYear);
      user.setGvcn(selectedTeacher.getId());
      user.setGender(updatedGender);
      user.setClassName(updatedClassName);
      user.setEmail(updatedEmail);
      user.setMajorId(selectedMajor.getId());

      try {
        UserController.updateUser(user.getId(), user);
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cập nhật sinh viên thành công.");
        studentsPanel.refreshTable();
        dispose();
      } catch (Exception ex) {
        ex.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Lỗi khi cập nhật sinh viên.");
      }
    } else {
      Notifications.getInstance().show(Notifications.Type.ERROR, "Giáo viên chủ nhiệm hoặc chuyên ngành không tìm thấy.");
    }
  }

  public static void showUpdateStudentForm(Component parent, User user, StudentsPanel studentsPanel, List<Teacher> teachers, List<Major> majors) {
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
    UpdateStudentForm form = new UpdateStudentForm(user, studentsPanel, teachers, majors);
    form.setVisible(true);
  }
}
