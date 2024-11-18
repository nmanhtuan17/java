package main.java.com.TLU.studentmanagement.view.pages.Majors;

import com.formdev.flatlaf.FlatClientProperties;
import main.java.com.TLU.studentmanagement.controller.majors.MajorController;
import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;
import main.java.com.TLU.studentmanagement.view.pages.Courses.CoursePanel;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MajorPanel extends JPanel {

    private JButton addButton;
    private JTable majorTable;
    private MajorTableModel majorTableModel;
    private MajorController majorController;

    public MajorPanel() {
        majorController = new MajorController();
        initUI();
        getAllMajors();
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
        JLabel titleLabel = new JLabel("Thông tin chuyên ngành", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);


        // Tạo bảng với dữ liệu và tiêu đề cột
        majorTableModel = new MajorTableModel();
        majorTable = new JTable(majorTableModel);
        majorTable.setFillsViewportHeight(true);

        // Căn giữa các giá trị trong bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        majorTable.setRowHeight(40); // Tùy chọn: Thay đổi chiều cao của các hàng

        // Áp dụng FlatLaf cho JTable
        majorTable.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Table.gridColor")));
        majorTable.setBackground(UIManager.getColor("Table.background"));
        majorTable.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
        majorTable.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));
        majorTable.setGridColor(UIManager.getColor("Table.gridColor"));
        majorTable.setShowVerticalLines(true);
        majorTable.setShowHorizontalLines(true);

        // Căn giữa các giá trị trong bảng
        for (int i = 0; i < majorTable.getColumnCount(); i++) {
            majorTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Customize headers
        JTableHeader header = majorTable.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        // Thêm các cột Sửa và Xóa
        majorTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        // Xử lý các sự kiện nhấn nút
        majorTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor());

        // Check quyền Admin và Teacher
        boolean isAdmin = UserSession.getUser() != null && UserSession.getUser().isAdmin();
        boolean isTeacherAdmin = TeacherSession.getTeacher() != null && TeacherSession.getTeacher().isAdmin();

        // Ẩn cột Hành động nếu không phải Admin
        if (!isAdmin && !isTeacherAdmin) {
            majorTable.removeColumn(majorTable.getColumnModel().getColumn(2));
        } else {
            majorTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
            majorTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor());
        }

        // Add a JScrollPane with padding around the table
        JScrollPane scrollPane = new JScrollPane(majorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding to the scroll pane
        add(scrollPane, BorderLayout.CENTER);

        // Nút Thêm khóa học
        addButton = new JButton("Thêm chuyên ngành");
        addButton.setFocusPainted(false);
        addButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(88, 86, 214));  // Accent color
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(240, 40));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
                    showAddMajorForm();
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                }
            }
        });

        // Ẩn nút "Thêm khóa học" nếu không phải Admin
        if (!isAdmin && !isTeacherAdmin) {
            addButton.setVisible(false);
        }

        // Panel cho các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(addButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void showAddMajorForm() {
        JDialog dialog = new JDialog((Frame) null, "Thêm chuyên ngành", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(400, 200);
        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();

        dialog.add(new JLabel("Tên chuyên ngành:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Mã chuyên ngành:"));
        dialog.add(codeField);

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        nameField.setPreferredSize(new Dimension(200, 40));
        codeField.setPreferredSize(new Dimension(200, 40));
        okButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));

        okButton.addActionListener(e -> {
            String name = nameField.getText();
            String code = codeField.getText();

            try {
                JSONObject response = majorController.createMajor(name, code);
                if (response != null && response.has("status") && response.getString("status").equals("error")) {
                    // Xử lý lỗi từ server
                    String errorMessage = response.getString("message");
                    Notifications.getInstance().show(Notifications.Type.ERROR, errorMessage);
                } else if (response.has("message") && response.getString("message").equals("Create success")) {
                    // Xử lý thành công
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm chuyên ngành thành công");
                    getAllMajors();
                    dialog.dispose(); // Đóng dialog sau khi thêm thành công
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(okButton);
        dialog.add(cancelButton);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    private void showUpdateMajorForm(Major major) {
        JTextField nameField = new JTextField(major.getName());
        JTextField codeField = new JTextField(major.getCode());

        Object[] message = {
                "Tên chuyên ngành:", nameField,
                "Mã chuyên ngành:", codeField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Cập nhật chuyên ngành", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String code = codeField.getText();

            try {
                MajorController.updateMajor(major.getId(), name, code);
                getAllMajors(); // Refresh the list after updating the major
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }

    private void getAllMajors() {
        if (UserSession.getUser() != null || TeacherSession.getTeacher() != null) {
            try {
                List<Major> majors = MajorController.getAllMajors();
                majorTableModel.setMajors(majors);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");

        }
    }

    private void deleteMajor(Major major) {
        int option = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa chuyên ngành " + major.getName() + " không?", "Xóa chuyên ngành", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                MajorController.deleteMajor(major.getId());
                majorTableModel.majors.remove(major); // Xóa chuyên ngành khỏi danh sách trong model
                majorTableModel.fireTableDataChanged(); // Thông báo cho model rằng dữ liệu đã thay đổi
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            }
        }
    }


    private class MajorTableModel extends AbstractTableModel {

        private final String[] columnNames = {"Tên chuyên ngành", "Mã chuyên ngành", "Hành động"};
        private List<Major> majors = new ArrayList<>();

        public void setMajors(List<Major> majors) {
            this.majors = majors;
            fireTableDataChanged(); // Cập nhật dữ liệu bảng khi thay đổi danh sách chuyên ngành
        }

        @Override
        public int getRowCount() {
            return majors.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Major major = majors.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return major.getName();
                case 1:
                    return major.getCode();
                case 2:
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
            return columnIndex == 2; // Chỉ cột Hành động mới có thể chỉnh sửa
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
        private Major currentMajor;

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
                    int row = majorTable.getSelectedRow();
                    if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
                        if (row != -1) {
                            currentMajor = majorTableModel.majors.get(row);
                            showUpdateMajorForm(currentMajor);
                        }
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Access denied");
                    }
            });

            deleteButton.addActionListener(e -> {
                int row = majorTable.getSelectedRow();
                if (UserSession.getUser() != null && UserSession.getUser().isAdmin()) {
                    if (row != -1) {
                        currentMajor = majorTableModel.majors.get(row);
                        deleteMajor(currentMajor);
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
            // Đảm bảo hành động nút được thực hiện khi nhấn vào nút
            fireEditingStopped();
            return true;
        }
    }
}
