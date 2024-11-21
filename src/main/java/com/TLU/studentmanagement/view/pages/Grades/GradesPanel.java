package main.java.com.TLU.studentmanagement.view.pages.Grades;

import javax.swing.*;
import java.awt.*;

public class GradesPanel extends JPanel {
  public GradesPanel() {
    setLayout(new BorderLayout());
    JLabel label = new JLabel("Thông tin bảng điểm", JLabel.CENTER);
    add(label, BorderLayout.CENTER);
  }
}
