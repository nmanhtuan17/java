package main.java.com.TLU.studentmanagement.view.pages.Courses;

import main.java.com.TLU.studentmanagement.controller.courses.CourseController;
import main.java.com.TLU.studentmanagement.controller.majors.MajorController;
import main.java.com.TLU.studentmanagement.model.Major;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddCourseForm extends JDialog {

  private JTextField nameField;
  private JTextField codeField;
  private JTextField creditField;
  private JComboBox<String> majorComboBox;
  private List<Major> majors;
  private CoursePanel coursePanel;
  private CourseController courseController;

  public AddCourseForm(JFrame parent, List<Major> majors, CoursePanel coursePanel) {
    super(parent, "Thêm khóa học", true);
    this.majors = majors;
    this.coursePanel = coursePanel;
    courseController = new CourseController();
    initUI();
  }

  private void initUI() {
    setLayout(new GridLayout(5, 2, 10, 10)); // Set gaps between each field input

    Border padding = new EmptyBorder(10, 10, 10, 10); // 10 pixels padding on all sides
    JPanel panel = (JPanel) getContentPane();
    panel.setBorder(padding);
    panel.setLayout(new GridLayout(5, 2, 10, 10)); // Adding gaps between fields

    add(new JLabel("Tên:"));
    nameField = new JTextField();
    add(nameField);

    add(new JLabel("Mã:"));
    codeField = new JTextField();
    add(codeField);

    add(new JLabel("Số tín chỉ:"));
    creditField = new JTextField();
    add(creditField);

    add(new JLabel("Chuyên ngành:"));
    majorComboBox = new JComboBox<>();
    populateMajorComboBox(); // Populate the combo box with majors
    add(majorComboBox);

    JButton addButton = new JButton("Thêm");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addCourse();
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

  private void populateMajorComboBox() {
    majorComboBox.removeAllItems();
    for (Major major : majors) {
      majorComboBox.addItem(major.getName());
    }
  }

  private void addCourse() {
    String name = nameField.getText();
    String code = codeField.getText();
    int credit = Integer.parseInt(creditField.getText());
    String majorName = (String) majorComboBox.getSelectedItem();
    Major selectedMajor = null;

    for (Major major : majors) {
      if (major.getName().equals(majorName)) {
        selectedMajor = major;
        break;
      }
    }

    if (selectedMajor != null) {
      try {
        JSONObject response = courseController.createCourse(name, code, credit, selectedMajor.getId());
        if (response != null && response.has("status") && response.getString("status").equals("error")) {
          // Xử lý lỗi từ server
          String errorMessage = response.getString("message");
          Notifications.getInstance().show(Notifications.Type.ERROR, errorMessage);
        } else if (response.has("message") && response.getString("message").equals("Create course success")) {
          // Xử lý thành công
          Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm khóa học thành công.");
          coursePanel.getAllCourses();
          dispose();
        }

      } catch (Exception ex) {
        ex.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Add course error.");
      }
    } else {
      Notifications.getInstance().show(Notifications.Type.ERROR, "Selected major not found.");
    }
  }

  public void refreshMajors() {
    try {
      majors = MajorController.getAllMajors();
      populateMajorComboBox(); // Update the combo box with the new majors list
    } catch (Exception ex) {
      ex.printStackTrace();
      Notifications.getInstance().show(Notifications.Type.ERROR, "Refresh majors error.");
    }
  }

  public static void showAddCourseForm(Component parent, List<Major> majors, CoursePanel coursePanel) {
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
    AddCourseForm form = new AddCourseForm(frame, majors, coursePanel);
    form.setVisible(true);
  }
}
