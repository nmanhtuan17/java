package main.java.com.TLU.studentmanagement.view.pages.Student;

import main.java.com.TLU.studentmanagement.model.Major;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentDetail extends JDialog {
    public StudentDetail(User user, List<Teacher> teachers, List<Major> majors) {
        setTitle("Chi tiết sinh viên");
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5); // reduced margin between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addLabelAndValue(contentPanel, "Tên:", user.getFullName(), gbc, 0);
        addSeparator(contentPanel, gbc, 1);
        addLabelAndValue(contentPanel, "Mã sinh viên:", user.getMsv(), gbc, 2);
        addSeparator(contentPanel, gbc, 3);
        addLabelAndValue(contentPanel, "Năm:", user.getYear(), gbc, 4);
        addSeparator(contentPanel, gbc, 5);
        addLabelAndValue(contentPanel, "Giới tính:", user.getGender(), gbc, 6);
        addSeparator(contentPanel, gbc, 7);
        addLabelAndValue(contentPanel, "Lớp:", user.getClassName(), gbc, 8);
        addSeparator(contentPanel, gbc, 9);
        addLabelAndValue(contentPanel, "Email:", user.getEmail(), gbc, 10);
        addSeparator(contentPanel, gbc, 11);

        // Find and display teacher's name
        String teacherName = getTeacherName(user.getGvcn(), teachers);
        addLabelAndValue(contentPanel, "Giáo viên chủ nhiệm:", teacherName, gbc, 12);
        addSeparator(contentPanel, gbc, 13);

        // Find and display major's name
        String majorName = getMajorName(user.getMajorId(), majors);
        addLabelAndValue(contentPanel, "Chuyên ngành:", majorName, gbc, 14);
        addSeparator(contentPanel, gbc, 15);

        // Add close button
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        contentPanel.add(closeButton, gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addLabelAndValue(JPanel panel, String label, String value, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(value), gbc);
    }

    private void addSeparator(JPanel panel, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(1, 10));
        panel.add(separator, gbc);
        gbc.gridwidth = 1;
    }

    private String getTeacherName(String teacherId, List<Teacher> teachers) {
        for (Teacher teacher : teachers) {
            if (teacher.getId().equals(teacherId)) {
                return teacher.getFullName();
            }
        }
        return "Không xác định";
    }

    private String getMajorName(String majorId, List<Major> majors) {
        for (Major major : majors) {
            if (major.getId().equals(majorId)) {
                return major.getName();
            }
        }
        return "Không xác định";
    }
}
