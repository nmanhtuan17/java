package main.java.com.TLU.studentmanagement.view.pages.Courses;

import main.java.com.TLU.studentmanagement.controller.courses.CourseController;
import main.java.com.TLU.studentmanagement.model.Course;
import main.java.com.TLU.studentmanagement.model.Major;
import raven.toast.Notifications;

import javax.swing.*;
import java.util.List;

public class UpdateCourseForm {

  public static void showUpdateCourseForm(Course course, CoursePanel coursePanel, List<Major> majors) {
    JTextField nameField = new JTextField(course.getName());
    JTextField codeField = new JTextField(course.getCode());
    JTextField creditField = new JTextField(String.valueOf(course.getCredit()));

    JComboBox<String> majorComboBox = new JComboBox<>();
    Major currentMajor = null;

    for (Major major : majors) {
      majorComboBox.addItem(major.getName());
      if (major.getId().equals(course.getMajorId())) {
        currentMajor = major;
      }
    }

    if (currentMajor != null) {
      majorComboBox.setSelectedItem(currentMajor.getName());
    }

    Object[] message = {
        "Tên khóa học:", nameField,
        "Mã:", codeField,
        "Số tín chỉ:", creditField,
        "Chuyên ngành:", majorComboBox
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Cập nhật khóa học", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      String updatedName = nameField.getText();
      String updatedCode = codeField.getText();
      int updatedCredit;
      try {
        updatedCredit = Integer.parseInt(creditField.getText());
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Số tín chỉ phải là số.");
        return;
      }

      String selectedMajorName = (String) majorComboBox.getSelectedItem();
      Major selectedMajor = null;

      for (Major major : majors) {
        if (major.getName().equals(selectedMajorName)) {
          selectedMajor = major;
          break;
        }
      }

      if (selectedMajor != null) {
        try {
          CourseController.updateCourse(course.getId(), updatedName, updatedCode, updatedCredit, selectedMajor.getId());
          Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cập nhật khóa học thành công.");
          coursePanel.getAllCourses();
        } catch (Exception ex) {
          ex.printStackTrace();
          Notifications.getInstance().show(Notifications.Type.ERROR, "Lỗi khi cập nhật khóa học.");
        }
      } else {
        Notifications.getInstance().show(Notifications.Type.ERROR, "Select major not found.");
      }
    }
  }
}
