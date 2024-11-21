package main.java.com.TLU.studentmanagement.view.pages.Semesters;

import com.formdev.flatlaf.FlatClientProperties;
import main.java.com.TLU.studentmanagement.controller.grades.GradeController;
import main.java.com.TLU.studentmanagement.controller.semesters.SemesterController;
import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;

import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SemesterPanel extends JPanel {

  private JButton addButton;
  private SemesterController semesterController;
  private JTable semesterTable;
  private SemesterTableModel semesterTableModel;

  public SemesterPanel() {
    semesterController = new SemesterController();
    initUI();
    getAllSemesters();
  }

  private void initUI() {
    setLayout(new BorderLayout());

    // Set the theme
    UIManager.put("TitlePane.background", new Color(240, 240, 240));
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

    // Panel cho tiêu đề và các nút
    JPanel topPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel("Thông tin học kỳ", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    topPanel.add(titleLabel, BorderLayout.CENTER);

    semesterTableModel = new SemesterTableModel();
    semesterTable = new JTable(semesterTableModel);
    semesterTable.setFillsViewportHeight(true);

    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);

    semesterTable.setRowHeight(40);

    semesterTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    semesterTable.setShowVerticalLines(true);
    semesterTable.setShowHorizontalLines(true);

    for (int i = 0; i < semesterTable.getColumnCount(); i++) {
      semesterTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    JTableHeader header = semesterTable.getTableHeader();
    header.setFont(header.getFont().deriveFont(Font.BOLD, 18));
    header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
    header.setBackground(new Color(240, 240, 240));
    header.setForeground(Color.BLACK);

    semesterTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
    semesterTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor());

    // Ẩn cột Hành động nếu không phải Admin

    // Check quyền Admin và Teacher
    boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
    boolean isTeacher = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();

    if (!isAdmin && !isTeacher) {
      semesterTable.removeColumn(semesterTable.getColumnModel().getColumn(3));
    } else {
      semesterTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
      semesterTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor());

    }


    addButton = new JButton("Thêm học kỳ");
    addButton.setFocusPainted(false);
    addButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
    addButton.setFont(new Font("Arial", Font.BOLD, 14));
    addButton.setBackground(new Color(88, 86, 214));  // Accent color
    addButton.setForeground(Color.WHITE);
    addButton.setPreferredSize(new Dimension(150, 40));
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
          showAddSemesterForm();
        } else {
          Notifications.getInstance().show(Notifications.Type.WARNING, "Bạn không có quyền xem thông tin này.");
        }
      }
    });

    // Ẩn nút "Thêm khóa học" nếu không phải Admin
    if (!isAdmin && !isTeacher) {
      addButton.setVisible(false);
    }

    // Panel cho các nút
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//        buttonPanel.add(refreshButton);
    buttonPanel.add(addButton);

    topPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(topPanel, BorderLayout.NORTH);

    // Add a JScrollPane with padding around the table
    JScrollPane scrollPane = new JScrollPane(semesterTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding to the scroll pane
    add(scrollPane, BorderLayout.CENTER);
  }

  private void showAddSemesterForm() {
    JTextField semesterField = new JTextField();
    JTextField groupField = new JTextField();
    JTextField yearField = new JTextField();

    Object[] message = {
        "Học kỳ:", semesterField,
        "Nhóm:", groupField,
        "Năm:", yearField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Thêm học kỳ", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      String semester = semesterField.getText();
      String group = groupField.getText();
      String year = yearField.getText();

      try {
        JSONObject response = semesterController.createSemester(semester, group, year);
        if (response != null && response.has("status") && response.getString("status").equals("error")) {
          // Xử lý lỗi từ server
          String errorMessage = response.getString("message");
          Notifications.getInstance().show(Notifications.Type.ERROR, errorMessage);
        } else {
          // Xử lý thành công
          Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm học kỳ thành công");
          getAllSemesters();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Error: " + ex.getMessage());
      }
    }
  }

  protected void getAllSemesters() {
    if (UserSession.getUser() != null || TeacherSession.getTeacher() != null) {
      try {
        List<Semester> semesters = SemesterController.getAllSemesters();
        semesterTableModel.setSemesters(semesters);
      } catch (Exception ex) {
        ex.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Error: " + ex.getMessage());
      }
    } else {
      Notifications.getInstance().show(Notifications.Type.WARNING, "Bạn không có quyền xem thông tin này.");
    }
  }

  private void deleteSemester(Semester semester) {
    int option = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa học kỳ " + semester.getSemester() + " không?", "Xóa học kỳ", JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.YES_OPTION) {
      try {
        SemesterController.deleteSemester(semester.getId());
        getAllSemesters();
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
      }
    }
  }

  private class SemesterTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Học kỳ", "Nhóm", "Năm", "Hành động"};
    private List<Semester> semesters = new ArrayList<>();

    public void setSemesters(List<Semester> semesters) {
      this.semesters = semesters;
      fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
      return semesters.size();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Semester semester = semesters.get(rowIndex);
      switch (columnIndex) {
        case 0:
          return semester.getSemester();
        case 1:
          return semester.getGroup();
        case 2:
          return semester.getYear();
        case 3:
          return "Hành động";
        default:
          throw new IllegalArgumentException("Invalid column index");
      }
    }

    @Override
    public String getColumnName(int columnIndex) {
      return columnNames[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 3;
    }
  }

  private class ButtonRenderer extends JButton implements TableCellRenderer {
    private final JButton editButton;
    private final JButton deleteButton;


    public ButtonRenderer() {
      setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

      editButton = new JButton("Sửa");
      editButton.setFont(new Font("Arial", Font.BOLD, 14));
      editButton.setForeground(Color.WHITE);
      editButton.setFocusPainted(false);
      editButton.setOpaque(true);
      editButton.setBorderPainted(false);
      editButton.setBackground(new Color(88, 86, 214));  // Accent color

      deleteButton = new JButton("Xóa");
      deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
      deleteButton.setForeground(Color.WHITE);
      deleteButton.setFocusPainted(false);
      deleteButton.setOpaque(true);
      deleteButton.setBorderPainted(false);
      deleteButton.setBackground(new Color(255, 69, 58));  // Red color for delete

      add(editButton);
      add(deleteButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      return this;
    }
  }

  private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton editButton;
    private final JButton deleteButton;
    private Semester currentSemester;

    public ButtonEditor() {
      editButton = new JButton("Sửa");
      deleteButton = new JButton("Xóa");

      editButton.setFont(new Font("Arial", Font.BOLD, 14));
      editButton.setForeground(Color.WHITE);
      editButton.setFocusPainted(false);
      editButton.setOpaque(true);
      editButton.setBorderPainted(false);
      editButton.setBackground(new Color(88, 86, 214));  // Accent color

      deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
      deleteButton.setForeground(Color.WHITE);
      deleteButton.setFocusPainted(false);
      deleteButton.setOpaque(true);
      deleteButton.setBorderPainted(false);
      deleteButton.setBackground(new Color(255, 69, 58));  // Red color for delete


      editButton.addActionListener(e -> {

        int row = semesterTable.getSelectedRow();
        if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
          if (row != -1) {
            currentSemester = semesterTableModel.semesters.get(row);
            UpdateSemesterForm.showUpdateSemesterForm(currentSemester, SemesterPanel.this);
          }
        } else {
          Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
        }

      });

      deleteButton.addActionListener(e -> {
        int row = semesterTable.getSelectedRow();
        if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
          if (row != -1) {
            currentSemester = semesterTableModel.semesters.get(row);
            deleteSemester(currentSemester);
          }
        } else {
          Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
        }
      });

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      return new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)) {{
        add(editButton);
        add(deleteButton);
      }};
    }

    @Override
    public Object getCellEditorValue() {
      return "Hành động";
    }

    @Override
    public boolean stopCellEditing() {
      fireEditingStopped();
      return true;
    }
  }
}
