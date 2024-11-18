package main.java.com.TLU.studentmanagement.view.pages.Transcripts;

import main.java.com.TLU.studentmanagement.controller.grades.GradeController;
import main.java.com.TLU.studentmanagement.controller.transcripts.TranscriptController;
import main.java.com.TLU.studentmanagement.controller.courses.CourseController;
import main.java.com.TLU.studentmanagement.model.Course;
import main.java.com.TLU.studentmanagement.model.Transcript;
import main.java.com.TLU.studentmanagement.model.Grade;
import org.json.JSONException;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;

public class TranscriptDetail extends JPanel {
    private TranscriptController transcriptController;
    private GradeController gradeController;
    private CourseController courseController;
    private static Transcript transcript;
    private JTable gradeTable;
    private static DefaultTableModel tableModel;

    public TranscriptDetail(Transcript transcript) {
        this.transcript = transcript;
        this.transcriptController = new TranscriptController();
        this.gradeController = new GradeController();
        this.courseController = new CourseController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Bảng điểm sinh viên " + transcript.getStudentName() + " - " + transcript.getStudentCode(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton addButton = new JButton("Thêm điểm");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddGradeForm();
            }
        });
        topPanel.add(addButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Số thứ tự", "Mã môn", "Tên môn", "Số TC", "Điểm giữa kỳ", "Điểm cuối kỳ", "Điểm tổng kết", "Trạng thái", "Hành động"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(tableModel);
        gradeTable.setRowHeight(40);

        // Hide the ID column
        gradeTable.getColumnModel().getColumn(0).setMinWidth(0);
        gradeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        gradeTable.getColumnModel().getColumn(0).setPreferredWidth(0);


        // Thêm dữ liệu vào bảng
        loadTableData();

        // Cài đặt Renderer và Editor cho cột hành động
        gradeTable.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
        gradeTable.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(gradeTable), BorderLayout.CENTER);
    }

    public void loadTableData() {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện tại

        for (int i = 0; i < transcript.getGrades().size(); i++) {
            Grade grade = transcript.getGrades().get(i);
            System.out.println(grade.toString());
            Course course = null;

            try {
                // Lấy thông tin khóa học từ API bằng courseId
                course = courseController.getCourseById(grade.getCourseId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Đảm bảo course không bị null
            if (course == null) {
                course = new Course(); // Tạo đối tượng Course rỗng để tránh NullPointerException
            }

            // Tạo dữ liệu hàng và thêm vào tableModel
            Object[] rowData = {
                    grade.getId(),
                    i + 1,
                    course.getCode() != null ? course.getCode() : "Không có thông tin",
                    course.getName() != null ? course.getName() : "Không có thông tin",
                    course.getCredit() != null ? course.getCredit() : "Không có thông tin",
                    grade.getMidScore(),
                    grade.getFinalScore(),
                    grade.getAverageScore(),
                    grade.getStatus(),
                    "Sửa, Xóa"
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAddGradeForm() {
        // Tạo panel chính cho form thêm điểm
        JPanel formPanel = new JPanel(new GridLayout(5, 2));

        // Tạo JComboBox để chọn mã môn
        JComboBox<Course> courseComboBox = new JComboBox<>();
        // Thêm các mã môn vào JComboBox
        try {
            for (Course course : CourseController.getAllCourses()) {
                courseComboBox.addItem(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tạo JTextField để hiển thị tên môn
        JTextField courseCodeField = new JTextField();
        courseCodeField.setEditable(false);

        // Tạo JTextField để nhập điểm giữa kỳ
        JTextField midScoreField = new JTextField();

        // Tạo JTextField để nhập điểm cuối kỳ
        JTextField finalScoreField = new JTextField();

        // Thêm các thành phần vào panel
        formPanel.add(new JLabel("Tên môn:"));
        formPanel.add(courseComboBox);
        formPanel.add(new JLabel("Mã môn:"));
        formPanel.add(courseCodeField);
        formPanel.add(new JLabel("Điểm giữa kỳ:"));
        formPanel.add(midScoreField);
        formPanel.add(new JLabel("Điểm cuối kỳ:"));
        formPanel.add(finalScoreField);

        // Tạo JDialog để hiển thị form thêm điểm
        JDialog addGradeDialog = new JDialog((Frame) null, "Thêm điểm", true);
        addGradeDialog.setSize(680, 300);
        addGradeDialog.setLocationRelativeTo(this);

        // Thêm panel vào dialog
        addGradeDialog.add(formPanel, BorderLayout.CENTER);

        // Thêm nút xác nhận và hủy
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Hủy");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        addGradeDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện khi chọn mã môn
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Course selectedCourse = (Course) courseComboBox.getSelectedItem();
                if (selectedCourse != null) {
                    courseCodeField.setText(selectedCourse.getCode());
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút OK
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Course selectedCourse = (Course) courseComboBox.getSelectedItem();
                if (selectedCourse == null) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn mã môn.");
                    return;
                }

                double midScore;
                double finalScore;

                try {
                    midScore = Double.parseDouble(midScoreField.getText());
                    finalScore = Double.parseDouble(finalScoreField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Điểm không hợp lệ. Vui lòng nhập lại.");
                    return;
                }

                Grade newGrade = new Grade();
                newGrade.setCourseId(selectedCourse.getId());
                newGrade.setCourseName(selectedCourse.getName());
                newGrade.setCourseCode(selectedCourse.getCode());
                newGrade.setMidScore(midScore);
                newGrade.setFinalScore(finalScore);

                // Tính toán averageScore và làm tròn đến 2 chữ số thập phân
                double rawAverageScore = (midScore * 0.3) + (finalScore * 0.7);
                BigDecimal bd = new BigDecimal(rawAverageScore).setScale(2, RoundingMode.HALF_UP);
                double roundedAverageScore = bd.doubleValue();

                newGrade.setAverageScore(roundedAverageScore);
                newGrade.setStatus(roundedAverageScore >= 4.0 ? "Pass" : "Fail");

                // Thêm transcriptId từ TranscriptDetail
                newGrade.setTranscriptId(transcript.getId());

                // Tạo biến để lưu trạng thái tạo điểm
                final boolean[] gradeCreated = {false};

                // Cập nhật điểm trên server
                try {
                    // Gọi createGrade và xử lý phản hồi từ server
                    String response = gradeController.createGrade(newGrade); // Cập nhật phương thức createGrade để trả về phản hồi
                    System.out.println("Server response: " + response);


                    if (response.equals("Grade of course already exists for semester")) {
                        // Xử lý lỗi từ server
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Điểm đã tồn tại trong kỳ");
                    } else {
                        // Điểm đã được tạo thành công
                        gradeCreated[0] = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tạo điểm: " + ex.getMessage());
                }

                // Kiểm tra xem điểm có được tạo thành công không
                if (gradeCreated[0]) {
                    // Thêm điểm vào danh sách điểm của bảng điểm
                    transcript.getGrades().add(newGrade);

                    // Tải lại dữ liệu bảng điểm
                    loadTableData();

                    // Đóng dialog
                    addGradeDialog.dispose();
                }
            }
        });



        // Xử lý sự kiện khi nhấn nút Hủy
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGradeDialog.dispose();
            }
        });

        // Hiển thị dialog
        addGradeDialog.setVisible(true);
    }




    // Lớp ButtonRenderer để hiển thị các nút trong cột hành động
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Lớp ButtonEditor để xử lý sự kiện của các nút trong cột hành động
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            // Initialize the panel and buttons
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            // Add buttons to the panel
            panel.add(editButton);
            panel.add(deleteButton);

            // Add action listeners
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = gradeTable.getSelectedRow();
                    if (row >= 0) {
                        showEditDialog(row);
                        // Stop editing to ensure control is released properly
                        stopCellEditing();
                        loadTableData();
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = gradeTable.getSelectedRow();
                    if (row >= 0) {
                        deleteGrade(row);
                        // Stop editing to ensure control is released properly
//                        stopCellEditing();
                        loadTableData();
                    }
                }
            });
        }

        private void showEditDialog(int row) {
            Grade grade = transcript.getGrades().get(row);

            String transcriptId = (String) tableModel.getValueAt(row, 0 );
            System.out.println(transcriptId);

            JTextField midScoreField = new JTextField(String.valueOf(grade.getMidScore()), 10);
            JTextField finalScoreField = new JTextField(String.valueOf(grade.getFinalScore()), 10);

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Điểm giữa kỳ:"));
            panel.add(midScoreField);
            panel.add(new JLabel("Điểm cuối kỳ:"));
            panel.add(finalScoreField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Chỉnh sửa điểm", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double midScore = Double.parseDouble(midScoreField.getText());
                    double finalScore = Double.parseDouble(finalScoreField.getText());

                    // Cập nhật đối tượng Grade
                    grade.setMidScore(midScore);
                    grade.setFinalScore(finalScore);
                     grade.setAverageScore((midScore * 0.3) + (finalScore * 0.7)); // Tùy thuộc vào logic tính điểm trung bình

                    System.out.println(grade.toString());

                    // Cập nhật dữ liệu trên máy chủ
                    int responseCode = gradeController.updateGrade(transcriptId, grade);

                    System.out.println("Response code: " + responseCode);

                    switch (responseCode) {
                        case HttpURLConnection.HTTP_OK:
                            // Tải lại dữ liệu bảng
                            loadTableData();
                            break;
                        case HttpURLConnection.HTTP_BAD_REQUEST:
                            JOptionPane.showMessageDialog(null, "Yêu cầu không hợp lệ. Vui lòng kiểm tra dữ liệu và thử lại.");
                            break;
                        case HttpURLConnection.HTTP_INTERNAL_ERROR:
                            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xử lý yêu cầu. Vui lòng thử lại sau.");
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Cập nhật điểm thất bại. Mã lỗi: " + responseCode);
                            break;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Điểm không hợp lệ. Vui lòng nhập lại.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi cập nhật điểm. Vui lòng thử lại.");
                    ex.printStackTrace();
                }
            }
        }



        private void deleteGrade(int row) {
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa môn học này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Grade grade = transcript.getGrades().get(row);

                // Remove the grade from the list
                transcript.getGrades().remove(row);

                // Delete the data on the server
                gradeController.deleteGrade(grade.getId());

                // Reload the table data
                loadTableData();
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
