//package main.java.com.TLU.studentmanagement.view.pages.Grades;
//
//import main.java.com.TLU.studentmanagement.controller.courses.CourseController;
//import main.java.com.TLU.studentmanagement.controller.grades.GradeController;
//import main.java.com.TLU.studentmanagement.model.Course;
//import main.java.com.TLU.studentmanagement.model.Grade;
//import main.java.com.TLU.studentmanagement.model.Transcript;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.List;
//
//public class AddGradeForm extends JDialog {
//    private GradeController gradeController;
//    private CourseController courseController;
//    private JComboBox<Course> courseComboBox;
//    private JTextField midScoreField;
//    private JTextField finalScoreField;
//    private JButton submitButton;
//    private JButton cancelButton;
//    private Transcript transcript;
//    private Grade newGrade; // Thêm thuộc tính để lưu điểm mới
//
//    public AddGradeForm(Frame owner, GradeController gradeController, CourseController courseController, Transcript transcript) {
//        super(owner, "Thêm điểm", true);
//        this.gradeController = gradeController;
//        this.courseController = courseController;
//        this.transcript = transcript;
//        initUI();
//        loadCourses();
//    }
//
//    public AddGradeForm() {
//
//    }
//
//    private void initUI() {
//        setLayout(new BorderLayout());
//        setSize(500, 240);
//        setLocationRelativeTo(null);
//
//        JPanel formPanel = new JPanel(new GridLayout(4, 2));
//        courseComboBox = new JComboBox<>();
//        midScoreField = new JTextField();
//        finalScoreField = new JTextField();
//
//        formPanel.add(new JLabel("Môn học:"));
//        formPanel.add(courseComboBox);
//        formPanel.add(new JLabel("Điểm giữa kỳ:"));
//        formPanel.add(midScoreField);
//        formPanel.add(new JLabel("Điểm cuối kỳ:"));
//        formPanel.add(finalScoreField);
//
//        JPanel buttonPanel = new JPanel();
//        submitButton = new JButton("Thêm");
//        cancelButton = new JButton("Hủy");
//
//        submitButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addGrade();
//            }
//        });
//
//        cancelButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//            }
//        });
//
//        buttonPanel.add(submitButton);
//        buttonPanel.add(cancelButton);
//
//        add(formPanel, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private void loadCourses() {
//        try {
//            List<Course> courses = courseController.getAllCourses();
//            for (Course course : courses) {
//                courseComboBox.addItem(course);
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Không thể tải danh sách môn học.");
//            e.printStackTrace();
//        }
//    }
//
//    private void addGrade() {
//        try {
//            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
//            double midScore = Double.parseDouble(midScoreField.getText());
//            double finalScore = Double.parseDouble(finalScoreField.getText());
//
//            newGrade = new Grade();
//            newGrade.setCourseId(selectedCourse.getId());
//            newGrade.setTranscriptId(transcript.getId());
//            newGrade.setMidScore(midScore);
//            newGrade.setFinalScore(finalScore);
//            newGrade.setAverageScore(midScore * 0.3 + finalScore * 0.7);
//
//            gradeController.createGrade(newGrade);
//
//            dispose();
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Điểm không hợp lệ. Vui lòng nhập lại.");
//        }
//    }
//
//    public Grade getNewGrade() {
//        return newGrade;
//    }
//}
