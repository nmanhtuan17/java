package main.java.com.TLU.studentmanagement.view.pages.ScoreReport;

import com.formdev.flatlaf.FlatLightLaf;
import main.java.com.TLU.studentmanagement.controller.semesters.SemesterController;
import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.util.HttpUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScoreReportPanel extends JPanel {

    private JComboBox<String> semesterComboBox;
    private JComboBox<String> programComboBox;
    private DefaultTableModel model;
    private JTable table;

    public ScoreReportPanel() {
        initUI();
        loadSemesterData();
        loadProgramData();
    }

    public void initUI() {
        setLayout(new BorderLayout());
        // Apply FlatLaf theme
        FlatLightLaf.install();
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

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Thêm padding cho header

        JLabel programLabel = new JLabel("Chương trình đào tạo");
        programComboBox = new JComboBox<>(); // Khởi tạo JComboBox rỗng

        JLabel semesterLabel = new JLabel("Học kỳ - Nhóm - Năm học");
        semesterComboBox = new JComboBox<>(new String[]{"Tất cả"});

        headerPanel.add(programLabel);
        headerPanel.add(programComboBox);
        headerPanel.add(semesterLabel);
        headerPanel.add(semesterComboBox);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Thêm padding cho content

        table = new JTable();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"STT", "Mã môn học", "Tên môn học", "Số TC", "Điểm quá trình", "Điểm cuối kỳ", "Điểm tổng kết", "Kết quả"});
        table.setModel(model);

        // Custom renderer for table header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setOpaque(false);
        tableHeader.setBackground(new Color(0x2A3F54));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 48));
        tableHeader.setFont(new Font("Arial", Font.BOLD, 20)); // Điều chỉnh font cho tiêu đề bảng

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(0x2A3F54));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 20)); // Điều chỉnh font cho tiêu đề bảng

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Custom renderer for table cells with border
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 14)); // Điều chỉnh font cho nội dung cell
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220))); // Thiết lập border cho các ô
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Increase row height
        table.setRowHeight(48);
        table.setBackground(Color.WHITE);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 220, 220));

        JScrollPane tableScrollPane = new JScrollPane(table);

        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Add action listeners for JComboBox
        semesterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchTranscriptData();
            }
        });

        programComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchTranscriptData();
            }
        });
    }

    private void loadSemesterData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Semester> semesters = SemesterController.getAllSemesters();
                    for (Semester semester : semesters) {
                        String comboBoxItem = semester.getSemester() + " - " + semester.getGroup() + " - " + semester.getYear();
                        semesterComboBox.addItem(comboBoxItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadProgramData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Lấy thông tin User hiện tại từ UserSession
                    User user = UserSession.getUser();
                    if (user != null) {
                        // Thêm thông tin chương trình đào tạo vào JComboBox
                        programComboBox.addItem(user.getMajorName());
//                        System.out.println(user.getMajorId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

//    private void fetchTranscriptData() {
//        SwingWorker<Void, Void> worker = new SwingWorker<>() {
//            @Override
//            protected Void doInBackground() throws Exception {
//                try {
//                    // Lấy thông tin học kỳ được chọn
//                    String selectedSemester = (String) semesterComboBox.getSelectedItem();
//                    String semesterId = "Tất cả".equals(selectedSemester) ? null : getSemesterIdFromComboBox(selectedSemester);
//
//                    // Lấy ID người dùng từ UserSession
//                    User user = UserSession.getUser();
//                    String userId = user != null ? user.getId() : null;
//
//                    if (userId != null) {
//                        // Gọi API để lấy dữ liệu điểm
//                        String url = "http://localhost:8080/api/transcript/student/" + userId;
//                        if (semesterId != null) {
//                            url += "/semester/" + semesterId;
//                        }
//
//                        String response = HttpUtil.sendGet(url);
//                        JSONObject jsonResponse = new JSONObject(response);
//                        JSONArray transcriptData = jsonResponse.getJSONArray("data");
//
//                        // Clear existing rows
//                        DefaultTableModel model = (DefaultTableModel) table.getModel();
//                        model.setRowCount(0);
//
//                        // Thêm dữ liệu mới vào bảng
//                        for (int i = 0; i < transcriptData.length(); i++) {
//                            JSONObject item = transcriptData.getJSONObject(i);
//                            model.addRow(new Object[]{
//                                    i + 1,
//                                    item.getString("courseCode"),
//                                    item.getString("courseName"),
//                                    item.getInt("credit"),
//                                    item.getFloat("midScore"),
//                                    item.getFloat("finalScore"),
//                                    item.getDouble("averageScore"),
//                                    item.getString("status")
//                            });
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    get();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        worker.execute();
//    }


    private void fetchTranscriptData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Lấy thông tin học kỳ được chọn
                    String selectedSemester = (String) semesterComboBox.getSelectedItem();
                    String semesterId = "Tất cả".equals(selectedSemester) ? null : getSemesterIdFromComboBox(selectedSemester);

                    // Lấy ID người dùng từ UserSession
                    User user = UserSession.getUser();
                    String userId = user != null ? user.getId() : null;

                    if (userId != null) {
                        // Gọi API để lấy dữ liệu điểm
                        String url;
                        boolean isAllSemesters = semesterId == null;
                        if (isAllSemesters) {
                            // Gọi API để lấy tất cả điểm
                            url = "http://localhost:8080/api/transcript/student/" + userId;
                        } else {
                            // Gọi API để lấy điểm của học kỳ cụ thể
                            url = "http://localhost:8080/api/transcript/student/" + userId + "/semester/" + semesterId;
                        }

                        String response = HttpUtil.sendGet(url);
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray transcriptDataArray = jsonResponse.getJSONArray("data");

                        // Clear existing rows
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.setRowCount(0);

                        if (isAllSemesters) {
                            // Thêm dữ liệu từ API lấy tất cả điểm
                            for (int i = 0; i < transcriptDataArray.length(); i++) {
                                JSONObject grade = transcriptDataArray.getJSONObject(i);
                                model.addRow(new Object[]{
                                        i + 1,
                                        grade.getString("courseCode"),
                                        grade.getString("courseName"),
                                        grade.getInt("credit"),
                                        grade.getFloat("midScore"),
                                        grade.getFloat("finalScore"),
                                        grade.getDouble("averageScore"),
                                        grade.getString("status")
                                });
                            }
                        } else {
                            // Thêm dữ liệu từ API lấy điểm của học kỳ cụ thể
                            for (int i = 0; i < transcriptDataArray.length(); i++) {
                                JSONObject transcriptData = transcriptDataArray.getJSONObject(i);
                                JSONArray grades = transcriptData.getJSONArray("grades");

                                for (int j = 0; j < grades.length(); j++) {
                                    JSONObject grade = grades.getJSONObject(j);
                                    model.addRow(new Object[]{
                                            j + 1,
                                            grade.getString("courseCode"),
                                            grade.getString("courseName"),
                                            grade.getInt("credit"),
                                            grade.getFloat("midScore"),
                                            grade.getFloat("finalScore"),
                                            grade.getDouble("averageScore"),
                                            grade.getString("status")
                                    });
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }




    private String getSemesterIdFromComboBox(String semesterComboBoxItem) {
        // Parse the semester ID from the comboBoxItem string
        // Example implementation, you need to adapt it based on your actual data format
        // Assuming the comboBoxItem format is "Semester - Group - Year"
        // and you need to find the ID in the semester data
        for (int i = 0; i < semesterComboBox.getItemCount(); i++) {
            String item = semesterComboBox.getItemAt(i);
            if (item.equals(semesterComboBoxItem)) {
                // Fetch the corresponding ID for the selected semester
                // You need to implement this based on how you map the semester items to IDs
                return getSemesterId(item);
            }
        }
        return null; // Return null if not found
    }

    private String getSemesterId(String semesterDescription) {
        // This method should return the semester ID based on the description
        // Implement this method based on your data source for semesters
        // Example implementation might involve looking up from a map or a list
        // This is a placeholder example
        try {
            List<Semester> semesters = SemesterController.getAllSemesters();
            for (Semester semester : semesters) {
                String description = semester.getSemester() + " - " + semester.getGroup() + " - " + semester.getYear();
                if (description.equals(semesterDescription)) {
                    return semester.getId(); // Assuming Semester class has getId() method
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return semesterDescription;
    }
}
