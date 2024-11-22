package main.java.com.TLU.studentmanagement.view.pages.Teachers;

import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.controller.teacher.TeacherController;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.view.pages.Courses.CoursePanel;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTeacherForm extends JDialog {
  private JTextField nameField, mgvField;
  private TeachersPanel teachersPanel;
  private CoursePanel coursePanel;

  public AddTeacherForm(JFrame parent, TeachersPanel _teachersPanel) {
    super(parent, "Thêm giáo viên", true);
    this.teachersPanel = _teachersPanel;
    initUI();
  }

  private void initUI() {
//    setLayout(new GridLayout(5, 2, 10, 10));
    Border padding = new EmptyBorder(10, 10, 10, 10);
    JPanel panel = (JPanel) getContentPane();
    panel.setBorder(padding);
    panel.setLayout(new GridLayout(5, 4, 10, 10)); // Adding gaps between fields
    setSize(400, 400);

    add(new JLabel("Tên:"));
    nameField = new JTextField();
    add(nameField);

    add(new JLabel("Mã giáo viên:"));
    mgvField = new JTextField();
    add(mgvField);


    JButton addButton = new JButton("Thêm");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addTeacher();
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

  private void addTeacher() {
    String name = nameField.getText();
    String mgv = mgvField.getText();


    try {


      TeacherController.createTeacher(mgv, name);
//      if (response != null && response.has("status") && response.getString("status").equals("error")) {
//        // Xử lý lỗi từ server
//        String errorMessage = response.getString("message");
//        Notifications.getInstance().show(Notifications.Type.ERROR, errorMessage);
//      } else {
//        // Xử lý thành công
//        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm thành công.");
//        teachersPanel.loadTeachers();
//
//      }
      dispose();
    } catch (Exception ex) {
      ex.printStackTrace();
      Notifications.getInstance().show(Notifications.Type.ERROR, "error.");
    }
  }

  public static void showTeacherForm(Component parent, TeachersPanel teacherPanel) {
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(parent);
    AddTeacherForm form = new AddTeacherForm(frame, teacherPanel);
    form.setVisible(true);
  }
}
