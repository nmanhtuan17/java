package main.java.com.TLU.studentmanagement.view.pages.Teachers;

import main.java.com.TLU.studentmanagement.controller.teacher.TeacherController;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.view.pages.Majors.MajorPanel;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TeachersPanel extends JPanel {

  private TeacherController teacherController;
  private JTextField searchField;
  private JButton searchButton;
  private JButton reloadButton;
  private JButton addTeacherButton;
  private JTable teacherTable;
  private DefaultTableModel tableModel;

  public TeachersPanel() {
    teacherController = new TeacherController();
    initUI();
    loadTeachers();
  }

  private void initUI() {
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Thông tin giáo viên", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    add(titleLabel, BorderLayout.NORTH);

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    searchField = new JTextField(20);
    searchButton = new JButton("Tìm kiếm");
    reloadButton = new JButton("Tải lại trang");
    addTeacherButton = new JButton("Thêm giáo viên");
    addTeacherButton.setBackground(new Color(88, 86, 214));
    addTeacherButton.setForeground(Color.WHITE);
    addTeacherButton.setFont(new Font("Arial", Font.BOLD, 14));

    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchTeachers();
      }
    });

    reloadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchField.setText("");
        loadTeachers();
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Refresh success.");
      }
    });

    addTeacherButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openAddTeacherForm();
      }
    });

    // Check quyền Admin và Teacher
    boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
    boolean isTeacherAdmin = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();

    // Ẩn nút "Thêm khóa học" nếu không phải Admin
    if (!isAdmin && !isTeacherAdmin) {
      addTeacherButton.setVisible(false);
    }

    topPanel.add(new JLabel("Tìm kiếm:"));
    topPanel.add(searchField);
    topPanel.add(searchButton);
    topPanel.add(reloadButton);
    topPanel.add(addTeacherButton);

    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BorderLayout());
    containerPanel.add(topPanel, BorderLayout.NORTH);

    tableModel = new DefaultTableModel(new Object[]{"STT", "ID", "Mã giáo viên", "Họ và tên", "Thao tác"}, 0);
    teacherTable = new JTable(tableModel) {
      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (isCellSelected(row, column)) {
          comp.setBackground(new Color(88, 86, 214));
          comp.setForeground(Color.WHITE);
        } else {
          comp.setBackground(Color.WHITE);
          comp.setForeground(Color.BLACK);
        }
        return comp;
      }
    };
    teacherTable.setRowHeight(40);
    teacherTable.setIntercellSpacing(new Dimension(0, 1));
    teacherTable.setGridColor(new Color(220, 220, 220));

    // Set center alignment for all columns
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

    for (int i = 0; i < teacherTable.getColumnCount(); i++) {
      teacherTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    teacherTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
    teacherTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));

    // Hide the ID column
    teacherTable.getColumnModel().getColumn(1).setMinWidth(0);
    teacherTable.getColumnModel().getColumn(1).setMaxWidth(0);
    teacherTable.getColumnModel().getColumn(1).setWidth(0);


    // Ẩn cột Hành động nếu không phải Admin
    if (!isAdmin && !isTeacherAdmin) {
      teacherTable.removeColumn(teacherTable.getColumn("Thao tác"));
    } else {
      teacherTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
      teacherTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    containerPanel.add(new JScrollPane(teacherTable), BorderLayout.CENTER);
    add(containerPanel, BorderLayout.CENTER);
  }

  public void loadTeachers() {
    List<Teacher> teachers = teacherController.getAllTeachers();
    tableModel.setRowCount(0);
    int stt = 1;

    for (Teacher teacher : teachers) {
      tableModel.addRow(new Object[]{
          stt++,
          teacher.getId(),
          teacher.getMgv(),
          teacher.getFullName(),
          "Xem"
      });
    }
  }

  private void searchTeachers() {
    String keyword = searchField.getText().toLowerCase();
    List<Teacher> teachers = teacherController.searchTeachers(keyword);

    tableModel.setRowCount(0);
    int stt = 1;
    for (Teacher teacher : teachers) {
      tableModel.addRow(new Object[]{
          stt++,
          teacher.getId(),
          teacher.getMgv(),
          teacher.getFullName(),
          "Xem"
      });
    }
  }

  private void openAddTeacherForm() {
    // Implement this method to open the form for adding a new teacher
  }

  private void viewTeacher(int row) {
    String teacherId = (String) tableModel.getValueAt(row, 1);
    Teacher teacher = teacherController.getTeacherById(teacherId);

    if (teacher == null) {
      JOptionPane.showMessageDialog(null, "Không tìm thấy giáo viên cho ID này.");
      return;
    }

    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
    JDialog dialog = new JDialog(parentFrame, "Chi tiết giáo viên", true);
    dialog.setLayout(new BorderLayout());

    // Implement the TeacherDetail panel and add it to the dialog
    // TeacherDetail teacherDetailPanel = new TeacherDetail(teacher);
    // dialog.add(teacherDetailPanel, BorderLayout.CENTER);

    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(parentFrame);
    dialog.setVisible(true);
  }

  private void editTeacher(int row) {
    String teacherId = (String) tableModel.getValueAt(row, 1);
    if (teacherId == null || teacherId.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Không thể xác định ID giáo viên.");
      return;
    }

    Teacher teacher = teacherController.getTeacherById(teacherId);
    if (teacher == null) {
      JOptionPane.showMessageDialog(null, "Không tìm thấy giáo viên để chỉnh sửa.");
      return;
    }

    // Implement the EditTeacherForm and open it here
    // new EditTeacherForm((Frame) SwingUtilities.getWindowAncestor(this), teacher, this).setVisible(true);
  }

  private void deleteTeacher(int row) {
    String teacherId = (String) tableModel.getValueAt(row, 1);
    if (teacherId == null || teacherId.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Không thể xác định ID giáo viên.");
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa giáo viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      try {
        teacherController.deleteTeacher(teacherId);
        loadTeachers();
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Xóa giáo viên thành công.");
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Lỗi khi xóa giáo viên: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;

    public ButtonRenderer() {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 5, 0, 5);
      gbc.gridx = GridBagConstraints.RELATIVE;
      gbc.gridy = GridBagConstraints.CENTER;
      gbc.anchor = GridBagConstraints.CENTER;

      viewButton = new JButton("Xem");
      editButton = new JButton("Sửa");
      deleteButton = new JButton("Xóa");

      viewButton.setBackground(new Color(88, 86, 214));
      viewButton.setForeground(Color.WHITE);
      viewButton.setFont(new Font("Arial", Font.BOLD, 12));

      editButton.setBackground(new Color(88, 86, 214));
      editButton.setForeground(Color.WHITE);
      editButton.setFont(new Font("Arial", Font.BOLD, 12));

      deleteButton.setBackground(Color.RED);
      deleteButton.setForeground(Color.WHITE);
      deleteButton.setFont(new Font("Arial", Font.BOLD, 12));

      add(viewButton, gbc);
      add(editButton, gbc);
      add(deleteButton, gbc);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
      if (isSelected) {
        setBackground(new Color(88, 86, 214));
        setForeground(Color.WHITE);
      } else {
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
      }
      return this;
    }
  }

  public class ButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;
    private int currentRow;

    public ButtonEditor(JCheckBox checkBox) {
      super(checkBox);
      panel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 5, 0, 5);
      gbc.gridx = GridBagConstraints.RELATIVE;
      gbc.gridy = GridBagConstraints.CENTER;
      gbc.anchor = GridBagConstraints.CENTER;

      viewButton = new JButton("Xem");
      editButton = new JButton("Sửa");
      deleteButton = new JButton("Xóa");

      viewButton.setBackground(new Color(88, 86, 214));
      viewButton.setForeground(Color.WHITE);
      viewButton.setFont(new Font("Arial", Font.BOLD, 12));

      editButton.setBackground(new Color(88, 86, 214));
      editButton.setForeground(Color.WHITE);
      editButton.setFont(new Font("Arial", Font.BOLD, 12));

      deleteButton.setBackground(Color.RED);
      deleteButton.setForeground(Color.WHITE);
      deleteButton.setFont(new Font("Arial", Font.BOLD, 12));

      panel.add(viewButton, gbc);
      panel.add(editButton, gbc);
      panel.add(deleteButton, gbc);

      viewButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
          viewTeacher(currentRow);
        }
      });

      editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
          editTeacher(currentRow);
        }
      });

      deleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
          deleteTeacher(currentRow);
        }
      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
      currentRow = row;
      return panel;
    }

    @Override
    public Object getCellEditorValue() {
      return "";
    }
  }
}

