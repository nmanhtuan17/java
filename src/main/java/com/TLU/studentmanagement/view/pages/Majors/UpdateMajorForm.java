package main.java.com.TLU.studentmanagement.view.pages.Majors;

import main.java.com.TLU.studentmanagement.controller.majors.MajorController;
import main.java.com.TLU.studentmanagement.model.Major;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateMajorForm extends JDialog {

  private JTextField nameField;
  private JTextField codeField;
  private Major major;

  public UpdateMajorForm(Window parent, Major major) {
    super(parent, "Cập nhật chuyên ngành", ModalityType.APPLICATION_MODAL);
    this.major = major;
    initUI();
  }

  private void initUI() {
    setLayout(new GridLayout(3, 2, 10, 10));
    setSize(300, 150);
    setLocationRelativeTo(getParent());

    JLabel nameLabel = new JLabel("Tên chuyên ngành:");
    nameField = new JTextField(major.getName());

    JLabel codeLabel = new JLabel("Mã chuyên ngành:");
    codeField = new JTextField(major.getCode());

    JButton updateButton = new JButton("Cập nhật");
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name = nameField.getText();
        String code = codeField.getText();

        try {
          MajorController.updateMajor(major.getId(), name, code);
          Notifications.getInstance().show(Notifications.Type.SUCCESS, "Chuyên ngành đã được cập nhật!");
          dispose(); // Đóng form sau khi cập nhật thành công
        } catch (Exception ex) {
          ex.printStackTrace();
          Notifications.getInstance().show(Notifications.Type.ERROR, "Error: " + ex.getMessage());
        }
      }
    });

    add(nameLabel);
    add(nameField);
    add(codeLabel);
    add(codeField);
    add(new JLabel()); // Empty cell
    add(updateButton);
  }
}
