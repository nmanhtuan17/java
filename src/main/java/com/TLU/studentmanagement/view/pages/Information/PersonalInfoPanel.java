package main.java.com.TLU.studentmanagement.view.pages.Information;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import main.java.com.TLU.studentmanagement.controller.UserController;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import raven.toast.Notifications;


public class PersonalInfoPanel extends JPanel {
  private JTable personalInfoTable;
  private JTable contactInfoTable;
  private JButton updateButton;
  private JButton refreshButton;

  public PersonalInfoPanel() {
    // Apply FlatLaf theme settings
    FlatLaf.setup(new FlatLightLaf());

    // Set the theme
    UIManager.put("TitlePane.background", new Color(240, 240, 240));
    UIManager.put("Toast.background", new Color(240, 240, 240));
    UIManager.put("Button.arc", 10);
    UIManager.put("Component.arc", 10);
    UIManager.put("Button.margin", new Insets(4, 6, 4, 6));
    UIManager.put("TextComponent.arc", 10);
    UIManager.put("TextField.margin", new Insets(4, 6, 4, 6));
    UIManager.put("PasswordField.margin", new Insets(4, 6, 4, 6));
    UIManager.put("ComboBox.padding", new Insets(4, 6, 4, 6));
    UIManager.put("TitlePane.unifiedBackground", false);
    UIManager.put("TitlePane.buttonSize", new Dimension(35, 23));
    UIManager.put("TitlePane.background", new Color(230, 230, 230));
    UIManager.put("TitlePane.foreground", Color.BLACK);

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // Panel thông tin cá nhân
    JPanel personalInfoPanel = new JPanel(new BorderLayout(10, 10));
    personalInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Thông tin cá nhân", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), Color.BLACK));

    String[] personalColumns = {"Thông tin", "Chi tiết"};
    Object[][] personalData = {{"Mã", ""}, {"Họ tên", ""}, {"Giới tính", ""}, {"CMND/CCCD", ""}, {"Lớp sinh viên", ""}, {"Ngành học", ""}, {"Năm học", ""}, {"Ngày sinh", ""}, {"Quyền", ""}};
    personalInfoTable = new JTable(personalData, personalColumns);
    personalInfoTable.setEnabled(false);  // Disable editing
    personalInfoTable.setRowHeight(30);
    personalInfoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    personalInfoTable.setFont(new Font("Arial", Font.PLAIN, 14));
    personalInfoTable.setBackground(Color.WHITE);
    personalInfoTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JScrollPane personalScrollPane = new JScrollPane(personalInfoTable);
    personalScrollPane.setBorder(BorderFactory.createEmptyBorder());
    personalInfoPanel.add(personalScrollPane, BorderLayout.CENTER);

    // Panel thông tin liên lạc
    JPanel contactInfoPanel = new JPanel(new BorderLayout(10, 10));
    contactInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), "Thông tin liên lạc", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 18), Color.BLACK));

    String[] contactColumns = {"Thông tin", "Chi tiết"};
    Object[][] contactData = {{"Điện thoại", ""}, {"Email cá nhân", ""}, {"Quốc gia", ""}, {"Địa chỉ", ""}};
    contactInfoTable = new JTable(contactData, contactColumns);
    contactInfoTable.setEnabled(false);  // Disable editing
    contactInfoTable.setRowHeight(30);
    contactInfoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    contactInfoTable.setFont(new Font("Arial", Font.PLAIN, 14));
    contactInfoTable.setBackground(Color.WHITE);
    contactInfoTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JScrollPane contactScrollPane = new JScrollPane(contactInfoTable);
    contactScrollPane.setBorder(BorderFactory.createEmptyBorder());
    contactInfoPanel.add(contactScrollPane, BorderLayout.CENTER);

    JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    tablesPanel.add(personalInfoPanel);
    tablesPanel.add(contactInfoPanel);

    // Panel cho tiêu đề và các nút
    JPanel topPanel = new JPanel(new BorderLayout());

    // Nút Refresh
    refreshButton = new JButton("Làm mới");
    refreshButton.setFocusPainted(false);
    refreshButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
    refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
    refreshButton.setBackground(new Color(88, 86, 214));  // Accent color
    refreshButton.setForeground(Color.WHITE);
    refreshButton.setPreferredSize(new Dimension(120, 40));
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadData();  // Refresh data
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Refresh success.");
      }
    });

    JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topButtonPanel.add(refreshButton);

    topPanel.add(topButtonPanel, BorderLayout.EAST);

    updateButton = new JButton("Cập nhật thông tin cá nhân");
    updateButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
    updateButton.setFont(new Font("Arial", Font.BOLD, 14));
    updateButton.setBackground(new Color(88, 86, 214));  // Accent color
    updateButton.setForeground(Color.WHITE);
    updateButton.setPreferredSize(new Dimension(250, 40));
    updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(updateButton);

    add(topPanel, BorderLayout.NORTH);
    add(tablesPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    updateButton.addActionListener(e -> showUpdateModal());

    loadData();
  }


  private void showUpdateModal() {
    // Create a panel for the form fields
    JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));

    // Initialize fields with existing user information
    JTextField fullNameField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField dobField = new JTextField();
    JTextField genderField = new JTextField();

    User user = UserSession.getUser();
    try {


      panel.add(new JLabel("Họ tên:"));
      panel.add(fullNameField);
      panel.add(new JLabel("Email:"));
      panel.add(emailField);
      panel.add(new JLabel("Điện thoại:"));
      panel.add(phoneField);
      panel.add(new JLabel("Địa chỉ:"));
      panel.add(addressField);
      panel.add(new JLabel("Ngày sinh:"));
      panel.add(dobField);
      panel.add(new JLabel("Giới tính:"));
      panel.add(genderField);

      // Create a scroll pane for the panel to allow scrolling if needed
      JScrollPane scrollPane = new JScrollPane(panel);
      scrollPane.setPreferredSize(new Dimension(400, 300));  // Set the preferred size for the scroll pane

      int result = JOptionPane.showConfirmDialog(this, scrollPane, "Cập nhật thông tin cá nhân", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (result == JOptionPane.OK_OPTION) {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String dob = dobField.getText();
        String gender = genderField.getText();

        // Call the API to update user information
        boolean isSuccess = UserController.updateUserInformation(fullName, email, phone, address, dob, gender);

        if (isSuccess) {
          // Reload user details after successful update
          try {
            user = UserSession.getUser();
            if (user != null) {
              UserController.fetchUserDetails(user.getId()); // Fetch new user details
              loadData(); // Reload the data into the panel
            }
          } catch (Exception ex) {
            Logger.getLogger(PersonalInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
          }
        } else {
          JOptionPane.showMessageDialog(this, "Cập nhật thông tin không thành công.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public void loadData() {
    try {
      User user = UserSession.getUser();

      if (user != null) {
        // Gọi API để lấy thông tin người dùng
        User userDetails = UserController.fetchUserDetails(user.getId());

        // Cập nhật dữ liệu bảng cá nhân
        personalInfoTable.setValueAt(userDetails.getMsv() != null ? userDetails.getMsv() : "N/A", 0, 1);
        personalInfoTable.setValueAt(userDetails.getFullName() != null ? userDetails.getFullName() : "N/A", 1, 1);
        personalInfoTable.setValueAt(userDetails.getGender() != null ? userDetails.getGender() : "N/A", 2, 1);
        personalInfoTable.setValueAt("N/A", 3, 1);
        personalInfoTable.setValueAt(userDetails.getClassName() != null ? userDetails.getClassName() : "N/A", 4, 1);
        personalInfoTable.setValueAt(userDetails.getMajorName() != null ? userDetails.getMajorName() : "N/A", 5, 1);
        personalInfoTable.setValueAt(userDetails.getYear() != null ? userDetails.getYear() : "N/A", 6, 1);
        personalInfoTable.setValueAt(userDetails.getDob() != null ? userDetails.getDob() : "N/A", 7, 1);
        personalInfoTable.setValueAt(userDetails.isAdmin() ? "Admin" : "Student", 8, 1);

        // Cập nhật dữ liệu bảng liên lạc
        contactInfoTable.setValueAt(userDetails.getPhone() != null ? userDetails.getPhone() : "N/A", 0, 1);
        contactInfoTable.setValueAt(userDetails.getEmail() != null ? userDetails.getEmail() : "N/A", 1, 1);
        contactInfoTable.setValueAt(userDetails.getCountry() != null ? userDetails.getCountry() : "N/A", 2, 1);
        contactInfoTable.setValueAt(userDetails.getAddress() != null ? userDetails.getAddress() : "N/A", 3, 1);
      } else {
        // Nếu không có người dùng, xóa dữ liệu trong bảng
        clearTableData();
      }
    } catch (Exception ex) {
      Logger.getLogger(PersonalInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
  }


  private void clearTableData() {
    for (int row = 0; row < personalInfoTable.getRowCount(); row++) {
      for (int col = 1; col < personalInfoTable.getColumnCount(); col++) {
        personalInfoTable.setValueAt("N/A", row, col);
      }
    }

    for (int row = 0; row < contactInfoTable.getRowCount(); row++) {
      for (int col = 1; col < contactInfoTable.getColumnCount(); col++) {
        contactInfoTable.setValueAt("N/A", row, col);
      }
    }
  }


}
