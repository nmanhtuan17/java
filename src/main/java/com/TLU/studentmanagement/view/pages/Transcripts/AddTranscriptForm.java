package main.java.com.TLU.studentmanagement.view.pages.Transcripts;

import main.java.com.TLU.studentmanagement.controller.transcripts.TranscriptController;
import main.java.com.TLU.studentmanagement.controller.semesters.SemesterController;
import main.java.com.TLU.studentmanagement.controller.UserController;
import main.java.com.TLU.studentmanagement.model.Transcript;
import main.java.com.TLU.studentmanagement.model.Semester;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.util.HttpUtil;
import org.json.JSONObject;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddTranscriptForm extends JDialog {

    private JComboBox<String> studentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton addButton;
    private JButton cancelButton;

    private TranscriptController transcriptController;
    private TranscriptPanel parentPanel;
    private List<User> students;
    private List<Semester> semesters;

    public AddTranscriptForm(Frame parent, List<User> students, List<Semester> semesters, TranscriptPanel parentPanel) {
        super(parent, "Thêm bảng điểm mới", true);
        this.students = students;
        this.semesters = semesters;
        this.parentPanel = parentPanel;
        this.transcriptController = new TranscriptController();
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(3, 2, 10, 10));

        // Initialize components
        studentComboBox = new JComboBox<>();
        semesterComboBox = new JComboBox<>();
        addButton = new JButton("Thêm");
        cancelButton = new JButton("Hủy");

        // Populate combo boxes
        for (User student : students) {
            studentComboBox.addItem(student.getFullName() + " - " + student.getMsv());
        }

        for (Semester semester : semesters) {
            semesterComboBox.addItem(semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear());
        }

        // Add components to the form
        add(new JLabel("Sinh viên:"));
        add(studentComboBox);
        add(new JLabel("Học kỳ:"));
        add(semesterComboBox);
        add(addButton);
        add(cancelButton);

        // Set button actions
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTranscript();
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

    private void addTranscript() {
        try {
            String studentDisplay = (String) studentComboBox.getSelectedItem();
            String semesterDisplay = (String) semesterComboBox.getSelectedItem();

            String studentId = getStudentIdByDisplay(studentDisplay);
            String semesterId = getSemesterIdByDisplay(semesterDisplay);

            if (studentId != null && semesterId != null) {
                Transcript newTranscript = new Transcript(studentId, semesterId);
                int result = transcriptController.createTranscript(newTranscript);

                switch (result) {
                    case 1:
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Thêm bảng điểm thành công.");
                        parentPanel.loadTranscripts(); // Tải lại danh sách bảng điểm trong panel cha
                        dispose(); // Đóng form
                        break;

                    case -1:
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Bảng điểm đã tồn tại.");
                        break;

                    case -2:
                        // Phục hồi bảng điểm
                        String transcriptId = transcriptController.getTranscriptId(); // Giả định phương thức để lấy transcriptId

                        int responseOption = JOptionPane.showConfirmDialog(
                                this,
                                "Bảng điểm đã bị xóa. Bạn có muốn khôi phục không?",
                                "Khôi phục bảng điểm",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (responseOption == JOptionPane.YES_OPTION) {
                            int restoreResult = transcriptController.restoreTranscript(transcriptId);

                            if (restoreResult == 1) {
                                Notifications.getInstance().show(Notifications.Type.SUCCESS, "Bảng điểm đã được khôi phục.");
                                parentPanel.loadTranscripts(); // Tải lại danh sách bảng điểm trong panel cha
                                dispose(); // Đóng form
                            } else {
                                Notifications.getInstance().show(Notifications.Type.ERROR, "Không thể khôi phục bảng điểm. Vui lòng thử lại sau.");
                            }
                        }
                        break;

                    default:
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Không thể thêm bảng điểm. Vui lòng thử lại sau.");
                        break;
                }
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Dữ liệu không hợp lệ.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, "Không thể thêm bảng điểm. Vui lòng thử lại sau.");
        }
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
