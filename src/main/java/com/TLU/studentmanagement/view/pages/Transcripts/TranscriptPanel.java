package main.java.com.TLU.studentmanagement.view.pages.Transcripts;

import main.java.com.TLU.studentmanagement.controller.transcripts.TranscriptController;
import main.java.com.TLU.studentmanagement.controller.semesters.SemesterController;
import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.model.Transcript;
import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TranscriptPanel extends JPanel {

  private TranscriptController transcriptController;
  private JTextField searchField;
  private JButton searchButton;
  private JButton reloadButton;
  private JButton addTranscriptButton;
  private JTable transcriptTable;
  private DefaultTableModel tableModel;

  private List<User> students;
  private List<Semester> semesters;

  private JPanel detailPanel;

  public TranscriptPanel() {
    transcriptController = new TranscriptController();
    initUI();
    loadStudentAndSemesterData();
    loadTranscripts();
  }

  private void initUI() {
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Thông tin bảng điểm", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    add(titleLabel, BorderLayout.NORTH);

    detailPanel = new JPanel(new BorderLayout());
    detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(detailPanel, BorderLayout.SOUTH);

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    searchField = new JTextField(20);
    searchButton = new JButton("Tìm kiếm");
    reloadButton = new JButton("Tải lại trang");
    addTranscriptButton = new JButton("Thêm bảng điểm");
    addTranscriptButton.setBackground(new Color(88, 86, 214));
    addTranscriptButton.setForeground(Color.WHITE);
    addTranscriptButton.setFont(new Font("Arial", Font.BOLD, 14));

    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchTranscripts();
      }
    });

    reloadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadStudentAndSemesterData();
        searchField.setText("");
        loadTranscripts();
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Refresh success.");
      }
    });

    addTranscriptButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        openAddTranscriptForm();
      }
    });

    topPanel.add(new JLabel("Tìm kiếm:"));
    topPanel.add(searchField);
    topPanel.add(searchButton);
    topPanel.add(reloadButton);
    topPanel.add(addTranscriptButton);

    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BorderLayout());
    containerPanel.add(topPanel, BorderLayout.NORTH);

    tableModel = new DefaultTableModel(new Object[]{"STT", "ID", "Mã sinh viên", "Sinh viên", "Học kỳ", "Thao tác"}, 0);
    transcriptTable = new JTable(tableModel) {
      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);
        if (isCellSelected(row, column)) {
          comp.setBackground(new Color(88, 86, 214)); // Background color for selected row
          comp.setForeground(Color.WHITE); // Text color for selected row
        } else {
          comp.setBackground(Color.WHITE); // Background color for unselected rows
          comp.setForeground(Color.BLACK); // Text color for unselected rows
        }
        return comp;
      }
    };
    transcriptTable.setRowHeight(40);
    transcriptTable.setIntercellSpacing(new Dimension(0, 1));
    transcriptTable.setGridColor(new Color(220, 220, 220));

    // Set center alignment for all columns
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

    for (int i = 0; i < transcriptTable.getColumnCount(); i++) {
      transcriptTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    transcriptTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
    transcriptTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));

    // Ẩn cột ID
    transcriptTable.getColumnModel().getColumn(1).setMinWidth(0);
    transcriptTable.getColumnModel().getColumn(1).setMaxWidth(0);
    transcriptTable.getColumnModel().getColumn(1).setWidth(0);

    containerPanel.add(new JScrollPane(transcriptTable), BorderLayout.CENTER);
    add(containerPanel, BorderLayout.CENTER);
  }


  private void loadStudentAndSemesterData() {
    try {
      students = UserController.getAllUsers();
      semesters = SemesterController.getAllSemesters();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void loadTranscripts() {
    List<Transcript> transcripts = transcriptController.getAllTranscripts();
    tableModel.setRowCount(0);
    int stt = 1; // Khởi tạo số thứ tự

    for (Transcript transcript : transcripts) {
      String semesterName = getSemesterNameById(transcript.getSemesterId());

      tableModel.addRow(new Object[]{
          stt++, // Thêm số thứ tự vào cột đầu tiên
          transcript.getId(), // Thêm transcriptId vào đây
          transcript.getStudentCode(), // Mã sinh viên
          transcript.getStudentName(), // Tên sinh viên
          semesterName,
          "Xem"
      });
    }
  }


  private String getSemesterNameById(String semesterId) {
    for (Semester semester : semesters) {
      if (semester.getId().equals(semesterId)) {
        return semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear();
      }
    }
    return null;
  }

  private void searchTranscripts() {
    loadStudentAndSemesterData(); // Đảm bảo dữ liệu đã được nạp
    String keyword = searchField.getText().toLowerCase();
    List<Transcript> transcripts = transcriptController.searchTranscripts(keyword);

    // In ra dữ liệu để kiểm tra
    System.out.println("Số lượng bảng điểm tìm thấy: " + transcripts.size());
    for (Transcript transcript : transcripts) {
      System.out.println("Transcript ID: " + transcript.getId() +
          ", Student ID: " + transcript.getStudentId() +
          ", Semester ID: " + transcript.getSemesterId());
    }

    // Xóa tất cả các hàng trong bảng hiện tại
    tableModel.setRowCount(0);

    // Thêm dữ liệu vào bảng
    int stt = 1; // Khởi tạo số thứ tự
    for (Transcript transcript : transcripts) {
      String studentName = transcript.getStudentName();
      String semesterName = getSemesterNameById(transcript.getSemesterId());

      // In ra giá trị để kiểm tra
      System.out.println("Student Name: " + transcript.getStudentCode() + studentName + ", Semester Name: " + semesterName);

      tableModel.addRow(new Object[]{
          stt++, // Số thứ tự
          transcript.getId(), // ID bảng điểm
          transcript.getStudentCode(), // Mã sinh viên
          studentName != null ? studentName : "Không tìm thấy", // Tên sinh viên
          semesterName != null ? semesterName : "Không tìm thấy", // Tên học kỳ
          "Xem" // Cột hành động
      });
    }
  }


  private void openAddTranscriptForm() {
    new AddTranscriptForm(
        (Frame) SwingUtilities.getWindowAncestor(this),
        students,
        semesters,
        this
    ).setVisible(true);
  }

  private void viewTranscript(int row) {
    // Lấy transcriptId từ cột thứ 1
    String transcriptId = (String) tableModel.getValueAt(row, 1);

    // Lấy Transcript từ controller
    Transcript transcript = transcriptController.getTranscriptById(transcriptId);

    // Kiểm tra nếu transcript không tồn tại
    if (transcript == null) {
      JOptionPane.showMessageDialog(null, "Không tìm thấy bảng điểm cho ID này.");
      return;
    }

    // Lấy JFrame làm parent của JDialog
    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

    // Tạo một JDialog modal
    JDialog dialog = new JDialog(parentFrame, "Chi tiết bảng điểm", true);
    dialog.setLayout(new BorderLayout());

    // Tạo và thêm TranscriptDetail vào dialog
    TranscriptDetail transcriptDetailPanel = new TranscriptDetail(transcript);
    dialog.add(transcriptDetailPanel, BorderLayout.CENTER);

    // Thiết lập kích thước và vị trí của dialog
    dialog.setSize(800, 600); // Có thể thay đổi kích thước tùy thuộc vào yêu cầu
    dialog.setLocationRelativeTo(parentFrame); // Đặt vị trí của dialog

    // Hiển thị dialog
    dialog.setVisible(true);
  }


  private void editTranscript(int row) {
    String transcriptId = (String) tableModel.getValueAt(row, 1); // Lấy transcriptId từ cột thứ 2
    if (transcriptId == null || transcriptId.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Không thể xác định ID bảng điểm.");
      return;
    }

    // Lấy bảng điểm cần chỉnh sửa
    Transcript transcript = transcriptController.getTranscriptById(transcriptId);
    if (transcript == null) {
      JOptionPane.showMessageDialog(null, "Không tìm thấy bảng điểm để chỉnh sửa.");
      return;
    }

    // Mở form chỉnh sửa bảng điểm
    new EditTranscriptForm(
        (Frame) SwingUtilities.getWindowAncestor(this),
        students,
        semesters,
        transcript,
        this
    ).setVisible(true);
  }


  private void deleteTranscript(int row) {
    loadStudentAndSemesterData(); // Đảm bảo dữ liệu đã được nạp

    // Lấy thông tin từ bảng
    String transcriptId = (String) tableModel.getValueAt(row, 1); // Lấy transcriptId từ cột thứ 2

    // Xác nhận rằng chúng ta có được transcriptId hợp lệ
    if (transcriptId == null || transcriptId.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Không thể xác định ID bảng điểm.");
      return;
    }

    // Xác nhận xóa
    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa bảng điểm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      try {
        transcriptController.deleteTranscript(transcriptId);
        loadTranscripts(); // Tải lại danh sách bảng điểm sau khi xóa
        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Xóa bảng điểm thành công.");
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Lỗi khi xóa bảng điểm: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }


  private String getStudentIdByDisplay(String studentDisplay) {
    String msv = studentDisplay.split(" - ")[1];
    for (User student : students) {
      if (student.getMsv().equals(msv)) {
        return student.getId();
      }
    }
    return null;
  }

  private String getSemesterIdByDisplay(String semesterDisplay) {
    for (Semester semester : semesters) {
      if (semesterDisplay.contains(semester.getSemester()) &&
          semesterDisplay.contains(semester.getGroup()) &&
          semesterDisplay.contains(semester.getYear())) {
        return semester.getId();
      }
    }
    return null;
  }

  public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;

    public ButtonRenderer() {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(0, 5, 0, 5); // Padding between buttons
      gbc.gridx = GridBagConstraints.RELATIVE;
      gbc.gridy = GridBagConstraints.CENTER;
      gbc.anchor = GridBagConstraints.CENTER; // Center vertically and horizontally

      viewButton = new JButton("Xem");
      editButton = new JButton("Sửa");
      deleteButton = new JButton("Xóa");

      // Set button colors
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
        setBackground(new Color(88, 86, 214)); // Light blue color for selection
        setForeground(Color.WHITE); // Text color for selection
      } else {
        setBackground(Color.WHITE); // Background color for unselected rows
        setForeground(Color.BLACK); // Text color for unselected rows
      }
      return this;
    }
  }

  public class ButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;

    public ButtonEditor(JCheckBox checkBox) {
      super(checkBox);
      panel = new JPanel();
      panel.setLayout(new GridBagLayout());
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
          int row = transcriptTable.getSelectedRow();
          viewTranscript(row);
        }
      });

      editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
          int row = transcriptTable.getSelectedRow();
          editTranscript(row);
        }
      });

      deleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
          int row = transcriptTable.getSelectedRow();
          deleteTranscript(row);
          loadTranscripts();
          loadStudentAndSemesterData();
        }
      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
      if (isSelected) {
        panel.setBackground(new Color(88, 86, 214)); // Light blue color for selection
      } else {
        panel.setBackground(Color.WHITE); // Background color for unselected rows
      }
      return panel;
    }

    @Override
    public Object getCellEditorValue() {
      return null;
    }
  }

}


