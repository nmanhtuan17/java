package main.java.com.TLU.studentmanagement.view.login;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import net.miginfocom.swing.MigLayout;
import main.java.com.TLU.studentmanagement.manager.FormsManager;
import raven.toast.Notifications;

public class Login extends JPanel {

  public Login() {
    init();
  }

  private void init() {
    setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
    txtUsername = new JTextField();
    txtPassword = new JPasswordField();
    chRememberMe = new JCheckBox("Remember me");
    cmdLogin = new JButton("Login");

    // Set preferred size for input fields and button
    txtUsername.setPreferredSize(new Dimension(300, 48));
    txtPassword.setPreferredSize(new Dimension(300, 48));
    cmdLogin.setPreferredSize(new Dimension(100, 40));


    JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill,300:350"));
    panel.putClientProperty(FlatClientProperties.STYLE, "" +
        "arc:20;" +
        "[light]background:darken(@background,3%);" +
        "[dark]background:lighten(@background,3%)");

    txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
        "showRevealButton:true");
    cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
        "[light]background:darken(@background,10%);" +
        "[dark]background:lighten(@background,10%);" +
        "borderWidth:0;" +
        "focusWidth:0;" +
        "innerFocusWidth:0;" +
        "font:16");

    txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
    txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

    JLabel lbTitle = new JLabel("Welcome");
    JLabel description = new JLabel("Please sign in to access your account");
    lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
        "font:bold + 20");
    description.putClientProperty(FlatClientProperties.STYLE, "" +
        "[light]foreground:lighten(@foreground, 30%);" +
        "[dark]foreground:darken(@foreground, 30%)");


    JLabel lblUsername = new JLabel("Username");
    lblUsername.putClientProperty(FlatClientProperties.STYLE, "font:14;");

    JLabel lblPassword = new JLabel("Password");
    lblPassword.putClientProperty(FlatClientProperties.STYLE, "font:14;");
    ;
    panel.add(lbTitle);
    panel.add(description);
    panel.add(lblUsername, "gapy 12");
    panel.add(txtUsername);
    panel.add(lblPassword, "gapy 12");
    panel.add(txtPassword);
    panel.add(chRememberMe, "grow 0");
    panel.add(cmdLogin, "gapy 10");
//        panel.add(createSignupLabel(), "gapy 10");
    add(panel);

    setPreferredSize(new Dimension(400, 500));

//        cmdLogin.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String username = txtUsername.getText();
//                String password = new String(txtPassword.getPassword());
//
//                if (username.equals("admin") && password.equals("password")) {
//                    // Show success notification
//                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "This is a success message.");
//
//                } else {
//                    // Show error notification
//                    Notifications.getInstance().show(Notifications.Type.ERROR, "Invalid username or password.");
//                }
//            }
//        });
  }

  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private JCheckBox chRememberMe;
  private JButton cmdLogin;

  public String getUsername() {
    return txtUsername.getText();
  }

  public String getPassword() {
    return new String(txtPassword.getPassword());
  }

  public void addLoginListener(ActionListener loginListener) {
    cmdLogin.addActionListener(loginListener);
  }

//    private Component createSignupLabel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
//        panel.putClientProperty(FlatClientProperties.STYLE, "" +
//                "background:null");
//        JButton cmdRegister = new JButton("<html><a href=\"#\">Sign up</a></html>");
//        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
//                "border:3,3,3,3");
//        cmdRegister.setContentAreaFilled(false);
//        cmdRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        cmdRegister.addActionListener(e -> {
//            FormsManager.getInstance().showForm(new Register());
//        });
//        JLabel label = new JLabel("Don't have an account ?");
//        label.putClientProperty(FlatClientProperties.STYLE, "" +
//                "[light]foreground:lighten(@foreground,30%);" +
//                "[dark]foreground:darken(@foreground,30%)");
//        panel.add(label);
//        panel.add(cmdRegister);
//        return panel;
//    }

}