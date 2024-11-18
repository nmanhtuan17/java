package main.java.com.TLU.studentmanagement.view.pages.Student;

import com.formdev.flatlaf.FlatClientProperties;
import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.controller.majors.MajorController;
import main.java.com.TLU.studentmanagement.controller.teacher.TeacherController;
import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.view.pages.Courses.AddCourseForm;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

//import static main.java.com.TLU.studentmanagement.view.pages.Student.UpdateStudentForm.showUpdateStudentForm;

public class StudentsPanel extends JPanel {

    private JButton addButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JTextField searchField;
    private JTable studentsTable;
    private StudentsTableModel studentsTableModel;
    private List<User> students;
    private List<Major> majors;
    private List<Teacher> teachers;

    public StudentsPanel() {
        initUI();
        getAllStudents();
        getAllMajors();
        getAllTeachers();
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
        JLabel titleLabel = new JLabel("Thông tin sinh viên", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Panel cho các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Ô tìm kiếm
        searchField = new JTextField(20);
        buttonPanel.add(searchField);

        // Nút Tìm kiếm
        searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                if (!keyword.isEmpty()) {
                    searchStudents(keyword);
                } else {
                    getAllStudents(); // If search field is empty, refresh with all students
                }
            }
        });
        buttonPanel.add(searchButton);

        // Nút Refresh
        refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(88, 86, 214));  // Accent color
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAllStudents();
                getAllMajors();
                getAllTeachers();
                searchField.setText("");
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Refresh success.");
            }
        });
        buttonPanel.add(refreshButton);

        // Nút Thêm sinh viên
        addButton = new JButton("Thêm sinh viên");
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
                    AddStudentForm.showAddStudentForm(StudentsPanel.this, teachers, majors, StudentsPanel.this);
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                }
            }
        });
        // Check quyền Admin và Teacher
        boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
        boolean isTeacherAdmin = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();


        // Ẩn nút "Thêm khóa học" nếu không phải Admin
        if (!isAdmin && !isTeacherAdmin) {
            addButton.setVisible(false);
        }

        buttonPanel.add(addButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        studentsTableModel = new StudentsTableModel();
        studentsTable = new JTable(studentsTableModel);
        studentsTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        studentsTable.setRowHeight(40);
        studentsTable.setBackground(Color.WHITE);
        studentsTable.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        studentsTable.setShowVerticalLines(true);
        studentsTable.setShowHorizontalLines(true);
        studentsTable.setGridColor(new Color(220, 220, 220));

        for (int i = 0; i < studentsTable.getColumnCount(); i++) {
            studentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = studentsTable.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);

        // Thiết lập độ rộng các cột
        TableColumnModel columnModel = studentsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // STT
        columnModel.getColumn(1).setPreferredWidth(150);  // Tên
        columnModel.getColumn(2).setPreferredWidth(100);  // Mã sinh viên
        columnModel.getColumn(3).setPreferredWidth(100);  // GVCN
        columnModel.getColumn(4).setPreferredWidth(100);  // Chuyên ngành
        columnModel.getColumn(5).setPreferredWidth(80);   // Lớp
        columnModel.getColumn(6).setPreferredWidth(200);  // Hành động

        studentsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        studentsTable.getColumnModel().getColumn(6).setCellEditor(new ActionEditor());

        // Add a JScrollPane with padding around the table
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding to the scroll pane
        add(scrollPane, BorderLayout.CENTER);
    }

    public void getAllStudents() {
        if (TeacherSession.getTeacher() != null || UserSession.getUser() != null) {
            try {
                students = UserController.getAllUsers();
                studentsTableModel.setStudents(students);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Bạn không có quyền xem thông tin này.");
        }
    }

    public void getAllMajors() {
        try {
            majors = MajorController.getAllMajors();
            // Call refreshMajors() on any open AddCourseForm instances
            for (Window window : Window.getWindows()) {
                if (window instanceof AddStudentForm) {
                    ((AddStudentForm) window).refreshMajors();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void getAllTeachers() {
        try {
            teachers = TeacherController.getAllTeachers();
            // Call refreshMajors() on any open AddCourseForm instances
            for (Window window : Window.getWindows()) {
                if (window instanceof AddStudentForm) {
                    ((AddStudentForm) window).refreshTeachers();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void deleteStudent(User student) {
        int option = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa sinh viên " + student.getFullName() + " không?", "Xóa sinh viên", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                UserController.deleteUser(student.getId());
                getAllStudents();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Delete success.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    public void searchStudents(String keyword) {
        try {
            students = UserController.searchStudents(keyword);
            System.out.println("Keyword: " + keyword);
            if (students.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Không tìm thấy sinh viên.");
            }
            studentsTableModel.setStudents(students);
        } catch (Exception e) {
            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm sinh viên: " + e.getMessage());
        }
    }


//    public void refreshTable() {
//        // Lấy lại danh sách sinh viên từ cơ sở dữ liệu
//        List<User> students = UserController.getAllUsers();
//
//        // Cập nhật bảng sinh viên với danh sách mới
//        // Giả sử bạn có một JTable để hiển thị danh sách sinh viên
//        DefaultTableModel model = (DefaultTableModel) studentsTable.getModel();
//        model.setRowCount(0); // Xóa các hàng hiện tại
//
//        for (User student : students) {
//            model.addRow(new Object[]{student.getMsv(), student.getFullName(), student.getClassName(), student.getMajorName()});
//        }
//    }

    public void refreshTable() {
        // Lấy lại danh sách sinh viên từ cơ sở dữ liệu
        List<User> updatedStudents = UserController.getAllUsers();

        // Cập nhật dữ liệu cho StudentsTableModel
        studentsTableModel.setStudents(updatedStudents);
    }


    private class StudentsTableModel extends AbstractTableModel {

        private final String[] columnNames = {"STT", "Tên", "Mã sinh viên", "GVCN", "Chuyên ngành", "Lớp", "Hành động"};
        private List<User> students = new ArrayList<>();

        public void setStudents(List<User> students) {
            this.students = students;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User student = students.get(rowIndex);

            String majorName = "";
            for (Major major : majors) {
                if (major.getId().equals(student.getMajorId())) {
                    majorName = major.getName();
                }
            }

            String gvcnName = "";
            for (Teacher teacher : teachers) {
                if (teacher.getId().equals(student.getGvcn())) {
                    gvcnName = teacher.getFullName();
                }
            }

            switch (columnIndex) {
                case 0:
                    return rowIndex + 1; // STT
                case 1:
                    return student.getFullName();
                case 2:
                    return student.getMsv();
                case 3:
                    return gvcnName;
                case 4:
                    return majorName;
                case 5:
                    return student.getClassName();
                case 6:
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
            return columnIndex == 6;
        }
    }

    private class ActionRenderer extends JPanel implements TableCellRenderer {

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));

            boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
            boolean isTeacherAdmin = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();

            if (!isAdmin && !isTeacherAdmin) {
                JButton viewButton = createButton("Xem chi tiết");
                add(viewButton);
            } else {
                JButton editButton = createButton("Sửa");
                JButton deleteButton = createButton("Xóa");
                JButton viewButton = createButton("Xem chi tiết");
                add(editButton);
                add(deleteButton);
                add(viewButton);

            }
        }

        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorderPainted(true);

            // Create empty border with padding
            Border emptyBorder = BorderFactory.createEmptyBorder(2, 4, 2, 4);

            // Create rounded border
            Border roundedBorder = new RoundedBorder(10); // 10 is the radius of curvature

            // Combine empty border with rounded border
            Border compoundBorder = BorderFactory.createCompoundBorder(roundedBorder, emptyBorder);

            // Set the compound border to the button
            button.setBorder(compoundBorder);

//            Border compoundBorder = BorderFactory.createCompoundBorder(
//                    BorderFactory.createLineBorder(new Color(0, 0, 0)),
//                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
//            );
//            button.setBorder(compoundBorder);

//            button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
//            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//            button.setBackground(new Color(88, 86, 214));  // Accent color
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private final JButton viewButton;
        private User currentStudent;

        public ActionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            editButton = createButton("Sửa");
            deleteButton = createButton("Xóa");
            viewButton = createButton("Xem chi tiết");

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
                        // Add logic to update student
                        showUpdateStudentForm(currentStudent);
                        fireEditingStopped();
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
                        deleteStudent(currentStudent);
                        fireEditingStopped();
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                    }
                }
            });

            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (UserSession.getUser() != null || TeacherSession.getTeacher() != null) {
                        // Thêm logic để xem chi tiết sinh viên
                        StudentDetail studentDetail = new StudentDetail(currentStudent, teachers, majors);
                        studentDetail.setVisible(true);
                        fireEditingStopped();
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                    }
                }
            });

            boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
            boolean isTeacherAdmin = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();

            if (!isAdmin && !isTeacherAdmin) {
                panel.add(viewButton);
            } else {
                panel.add(editButton);
                panel.add(deleteButton);
                panel.add(viewButton);

            }
//            panel.add(editButton);
//            panel.add(deleteButton);
//            panel.add(viewButton);
        }

        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorderPainted(true);

            // Create empty border with padding
            Border emptyBorder = BorderFactory.createEmptyBorder(2, 4, 2, 4);

            // Create rounded border
            Border roundedBorder = new RoundedBorder(10); // 10 is the radius of curvature

            // Combine empty border with rounded border
            Border compoundBorder = BorderFactory.createCompoundBorder(roundedBorder, emptyBorder);

            // Set the compound border to the button
            button.setBorder(compoundBorder);

//            Border compoundBorder = BorderFactory.createCompoundBorder(
//                    BorderFactory.createLineBorder(new Color(0, 0, 0)),
//                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
//            );
//            button.setBorder(compoundBorder);

//            button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
//            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//            button.setBackground(new Color(88, 86, 214));  // Accent color
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentStudent = studentsTableModel.students.get(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel;
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }
    }

    static class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Adjust insets to reduce spacing between border and text
            int top = 2; // Top padding
            int left = 4; // Left padding
            int bottom = 2; // Bottom padding
            int right = 4; // Right padding
            return new Insets(top, left, bottom, right);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void showUpdateStudentForm(User student) {
        UpdateStudentForm.showUpdateStudentForm(this, student, this, teachers, majors);
    }
}
