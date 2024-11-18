package main.java.com.TLU.studentmanagement.view.pages.Transcripts;

import main.java.com.TLU.studentmanagement.controller.transcripts.TranscriptController;
import main.java.com.TLU.studentmanagement.controller.semesters.SemesterController;
import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.model.Transcript;
import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.model.User;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EditTranscriptForm extends JDialog {

    private JComboBox<String> studentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    private TranscriptController transcriptController;
    private TranscriptPanel parentPanel;
    private List<User> students;
    private List<Semester> semesters;
    private Transcript transcript;

    public EditTranscriptForm(Frame parent, List<User> students, List<Semester> semesters, Transcript transcript, TranscriptPanel parentPanel) {
        super(parent, "Chỉnh sửa bảng điểm", true);
        this.students = students;
        this.semesters = semesters;
        this.transcript = transcript;
        this.parentPanel = parentPanel;
        this.transcriptController = new TranscriptController();
        initUI();
        populateFields();
    }

    private void initUI() {
        // Use GridBagLayout for more flexible layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        studentComboBox = new JComboBox<>();
        semesterComboBox = new JComboBox<>();
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");

        // Populate combo boxes
        for (User student : students) {
            studentComboBox.addItem(student.getFullName() + " - " + student.getMsv());
        }

        for (Semester semester : semesters) {
            semesterComboBox.addItem(semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear());
        }

        // Add components to the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Mã sinh viên:"), gbc);

        gbc.gridx = 1;
        add(studentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Học kỳ:"), gbc);

        gbc.gridx = 1;
        add(semesterComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveButton, gbc);

        gbc.gridy = 3;
        add(cancelButton, gbc);

        // Set button actions
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTranscript();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Set default form properties
        setSize(400, 200);
        setLocationRelativeTo(getParent());
    }

    private void populateFields() {
        if (transcript != null) {
            String studentDisplay = getStudentCodeById(transcript.getStudentId());
            String semesterDisplay = getSemesterDisplayById(transcript.getSemesterId());

            studentComboBox.setSelectedItem(studentDisplay);
            semesterComboBox.setSelectedItem(semesterDisplay);
        }
    }

    private void updateTranscript() {
        try {
            String studentDisplay = (String) studentComboBox.getSelectedItem();
            String semesterDisplay = (String) semesterComboBox.getSelectedItem();

            String studentId = getStudentIdByDisplay(studentDisplay);
            String semesterId = getSemesterIdByDisplay(semesterDisplay);

            if (studentId != null && semesterId != null) {
                // Create a new Transcript object with all required fields
                Transcript updatedTranscript = new Transcript(studentId, semesterId);
                updatedTranscript.setId(transcript.getId()); // Preserve the existing transcript ID

                int result = transcriptController.updateTranscript(transcript.getId(), updatedTranscript);

                if (result == 1) {
                    parentPanel.loadTranscripts(); // Tải lại danh sách bảng điểm trong panel cha
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Cập nhật bảng điểm thành công.");
                    dispose(); // Đóng form
                } else if (result == -1) {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Bảng điểm của sinh viên đã tồn tại.");
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, "Không thể cập nhật bảng điểm. Vui lòng thử lại sau.");
                }
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Dữ liệu không hợp lệ.");
            }
        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Không thể cập nhật bảng điểm. Vui lòng thử lại sau.");
        }
    }

    private String getStudentNameById(String studentId) {
        for (User student : students) {
            if (student.getId().equals(studentId)) {
                return student.getFullName(); // Return the full name of the student
            }
        }
        return null;
    }

    private String getStudentCodeById(String studentId) {
        for (User student : students) {
            if (student.getId().equals(studentId)) {
                return student.getMsv(); // Return the student code
            }
        }
        return null;
    }

    private String getStudentDisplayById(String studentId) {
        for (User student : students) {
            if (student.getId().equals(studentId)) {
                return student.getFullName() + " - " + student.getMsv();
            }
        }
        return null;
    }

    private String getSemesterDisplayById(String semesterId) {
        for (Semester semester : semesters) {
            if (semester.getId().equals(semesterId)) {
                return semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear();
            }
        }
        return null;
    }

    private String getStudentIdByDisplay(String displayText) {
        for (User student : students) {
            if ((student.getFullName() + " - " + student.getMsv()).equals(displayText)) {
                return student.getId();
            }
        }
        return null;
    }

    private String getSemesterIdByDisplay(String displayText) {
        for (Semester semester : semesters) {
            if ((semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear()).equals(displayText)) {
                return semester.getId();
            }
        }
        return null;
    }
}
