package main.java.com.TLU.studentmanagement.view.Dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import main.java.com.TLU.studentmanagement.main.Application;
import main.java.com.TLU.studentmanagement.model.Teacher;
import main.java.com.TLU.studentmanagement.model.User;
import main.java.com.TLU.studentmanagement.session.TeacherSession;
import main.java.com.TLU.studentmanagement.session.UserSession;

import main.java.com.TLU.studentmanagement.view.pages.Grades.AdminTeacherGradesPanel;
import main.java.com.TLU.studentmanagement.view.pages.Information.PersonalInfoPanel;
import main.java.com.TLU.studentmanagement.view.pages.Courses.CoursePanel;
import main.java.com.TLU.studentmanagement.view.pages.Grades.GradesPanel;
import main.java.com.TLU.studentmanagement.view.pages.Majors.MajorPanel;
import main.java.com.TLU.studentmanagement.view.pages.ScoreReport.ScoreReportPanel;
import main.java.com.TLU.studentmanagement.view.pages.Semesters.SemesterPanel;
import main.java.com.TLU.studentmanagement.view.pages.Student.StudentsPanel;
import main.java.com.TLU.studentmanagement.view.pages.Teachers.TeachersPanel;

import main.java.com.TLU.studentmanagement.view.pages.Transcripts.TranscriptPanel;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class HomeView extends JPanel {

  private JPanel contentPanel;
  private CardLayout cardLayout;
  private JLabel nameLabel;
  private JButton logoutButton;
  private JPanel navPanel;
  private JPanel selectedNavItem; // Thêm trường để lưu trữ navItem đã chọn

  // Mảng đường dẫn ảnh
  private String[] icons = {
      "/main/resources/images/account.png", // "Thông tin cá nhân"
      "/main/resources/images/course.png", // "Môn học"
      "/main/resources/images/grades.png", // "Phiếu báo điểm"
      "/main/resources/images/student.png", // "Thông tin Học Sinh"
      "/main/resources/images/teacher.png", // "Thông tin Giáo Viên
      "/main/resources/images/semester.png", // "Thông tin Học Kỳ
  };

  public HomeView() {
    initUI();

  }

  private void initUI() {
    setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel(new BorderLayout());

    navPanel = new JPanel(new MigLayout("wrap,fillx,insets 20", "fill,200:200"));
    navPanel.setBackground(new Color(42, 63, 84));
    navPanel.setPreferredSize(new Dimension(240, getHeight()));

    nameLabel = new JLabel(getWelcomeMessage());
    nameLabel.setForeground(Color.WHITE);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
    navPanel.add(nameLabel, "gapbottom 30, align center");

    logoutButton = new JButton("Đăng Xuất");
    logoutButton.setPreferredSize(new Dimension(80, 40));
    logoutButton.putClientProperty(FlatClientProperties.STYLE, "" +
        "[light]background:darken(@background,10%);" +
        "[dark]background:lighten(@background,10%);" +
        "borderWidth:0;" +
        "focusWidth:0;" +
        "innerFocusWidth:0;" +
        "font:16");
    logoutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performLogout();
      }
    });

    navPanel.add(logoutButton, "gapbottom 30, align center");

    contentPanel = new JPanel();
    cardLayout = new CardLayout();
    contentPanel.setLayout(cardLayout);

    User user = UserSession.getUser();
    Teacher teacher = TeacherSession.getTeacher();

    // Luôn thêm trang "Thông tin cá nhân" trước tiên
    addPage("Thông tin cá nhân", "TT cá nhân", navPanel, contentPanel, icons[0], new PersonalInfoPanel());
    addPage("Thông tin Môn học", "TT Môn học", navPanel, contentPanel, icons[1], new CoursePanel());

    if (user != null && !user.isGv() && !user.isAdmin()) {
      // Nếu là sinh viên, chỉ hiển thị các trang học sinh
      addPage("Phiếu báo điểm", "Phiếu báo điểm", navPanel, contentPanel, icons[2], new ScoreReportPanel());
    } else {
      // Nếu là admin hoặc giáo viên, hiển thị tất cả các trang
      addPage("Thông tin Sinh viên", "TT Sinh viên", navPanel, contentPanel, icons[3], new StudentsPanel());
      addPage("Thông tin Giáo Viên", "TT Giáo Viên", navPanel, contentPanel, icons[4], new TeachersPanel());
      addPage("Thông tin Học Kỳ", "TT Học Kỳ", navPanel, contentPanel, icons[5], new SemesterPanel());
      addPage("Thông tin bảng điểm", "TT Bảng điểm", navPanel, contentPanel, icons[2], new TranscriptPanel());
      addPage("Thông tin chuyên ngành", "TT Chuyên ngành", navPanel, contentPanel, icons[5], new MajorPanel());
    }

    mainPanel.add(navPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    add(mainPanel);

    // Tìm và gọi sự kiện click cho navItem đầu tiên
    if (navPanel.getComponentCount() > 2) { // Vì 2 component đầu là nameLabel và logoutButton
      JPanel firstNavItem = (JPanel) navPanel.getComponent(2); // Lấy navItem đầu tiên
      firstNavItem.dispatchEvent(new java.awt.event.MouseEvent(
          firstNavItem,
          java.awt.event.MouseEvent.MOUSE_CLICKED,
          System.currentTimeMillis(),
          0,
          0,
          0,
          1,
          false
      ));
    }
  }


  private void addPage(String pageName, String navItem, JPanel navPanel, JPanel contentPanel, String iconPath, JPanel pagePanel) {
    contentPanel.add(pagePanel, pageName);

    URL iconURL = getClass().getResource(iconPath);
    if (iconURL == null) {
      System.err.println("Không tìm thấy tài nguyên: " + iconPath);
      return;
    }

    try {
      ImageIcon icon = new ImageIcon(ImageIO.read(iconURL));
      Image scaledImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
      JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));

      JLabel navLabel = new JLabel(navItem);
      navLabel.setForeground(Color.WHITE);
      navLabel.setFont(new Font("Roboto", Font.BOLD, 14));

      JPanel navItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
      navItemPanel.setBackground(new Color(42, 63, 84));
      navItemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      navItemPanel.add(iconLabel);
      navItemPanel.add(navLabel);

      // Thêm lắng nghe sự kiện chuột để thay đổi màu nền khi click
      navItemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          cardLayout.show(contentPanel, pageName);
          if (selectedNavItem != null) {
            selectedNavItem.setBackground(new Color(42, 63, 84)); // Đặt lại màu nền cho mục đã chọn trước đó
          }
          navItemPanel.setBackground(Color.GRAY); // Đặt màu nền cho mục được chọn
          selectedNavItem = navItemPanel; // Cập nhật mục đã chọn
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          navItemPanel.setBackground(Color.GRAY); // Đổi màu nền khi rê chuột vào
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
          if (selectedNavItem != navItemPanel) {
            navItemPanel.setBackground(new Color(42, 63, 84)); // Đổi lại màu nền khi rê chuột ra
          }
        }
      });

      navPanel.add(navItemPanel, "gapbottom 20, growx");
    } catch (IOException ex) {
      System.err.println("Lỗi khi tải biểu tượng: " + iconPath);
      ex.printStackTrace();
    }
  }

  private void selectNavItem(String name) {
    Component[] components = navPanel.getComponents();
    for (Component component : components) {
      if (component instanceof JPanel) {
        JPanel navItemPanel = (JPanel) component;
        JLabel navLabel = (JLabel) navItemPanel.getComponent(1); // Lấy JLabel trong navItemPanel

        if (navLabel.getText().equals(name)) {
          navItemPanel.setBackground(Color.GRAY); // Thiết lập màu nền xám cho navItemPanel đã chọn
          cardLayout.show(contentPanel, name); // Chuyển đổi sang trang có tên là 'name'
          selectedNavItem = navItemPanel; // Cập nhật mục đã chọn
        } else {
          navItemPanel.setBackground(new Color(42, 63, 84)); // Thiết lập lại màu nền cho các navItemPanel khác
        }
      }
    }
  }

  private String getWelcomeMessage() {
    User user = UserSession.getUser();
    if (user != null) {
      return "Welcome, " + user.getFullName();
    } else {
      Teacher teacher = TeacherSession.getTeacher();
      if (teacher != null) {
        return "Welcome, " + teacher.getFullName();
      }
    }
    return "Welcome";
  }

  private void performLogout() {
    UserSession.clear();
    TeacherSession.clear();
    SwingUtilities.getWindowAncestor(this).dispose();
    new Application().setVisible(true);
  }
}
