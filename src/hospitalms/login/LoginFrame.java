package hospitalms.login;

import hospitalms.dashboard.DashboardFrame;
import hospitalms.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Hospital Management System - Login");
        setSize(480, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(5, 60, 120),
                    getWidth(), getHeight(), new Color(0, 150, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Top icon area
        JPanel iconPanel = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(175, 5, 90, 90);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g2.drawString("H+", 195, 60);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setBounds(0, 10, 480, 100);
        main.add(iconPanel);

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 110, 480, 32);
        main.add(lblTitle);

        JLabel lblSub = new JLabel("SYSTEM", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(180, 230, 255));
        lblSub.setBounds(0, 140, 480, 25);
        main.add(lblSub);

        // White card
        JPanel card = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        card.setOpaque(false);
        card.setBounds(45, 180, 390, 265);
        main.add(card);

        JLabel lblSign = new JLabel("Sign In", SwingConstants.CENTER);
        lblSign.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSign.setForeground(new Color(10, 70, 130));
        lblSign.setBounds(0, 18, 390, 30);
        card.add(lblSign);

        JSeparator line = new JSeparator();
        line.setBounds(30, 55, 330, 2);
        line.setForeground(new Color(220, 230, 245));
        card.add(line);

        // Username label
        JLabel lblU = new JLabel("USERNAME");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblU.setForeground(new Color(80, 100, 130));
        lblU.setBounds(30, 65, 200, 20);
        card.add(lblU);

        // Username field
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(30, 85, 330, 40);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 205, 235), 2),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        txtUsername.setBackground(new Color(245, 248, 255));
        card.add(txtUsername);

        // Password label
        JLabel lblP = new JLabel("PASSWORD");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblP.setForeground(new Color(80, 100, 130));
        lblP.setBounds(30, 133, 200, 20);
        card.add(lblP);

        // Password field
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(30, 153, 330, 40);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 205, 235), 2),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        txtPassword.setBackground(new Color(245, 248, 255));
        card.add(txtPassword);

        // Login button
        JButton btnLogin = new JButton("LOGIN") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover())
                    g2.setColor(new Color(0, 100, 180));
                else
                    g2.setColor(new Color(10, 80, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent()) / 2 - 3);
            }
        };
        btnLogin.setOpaque(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(30, 205, 330, 45);
        card.add(btnLogin);

        // Status label
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(255, 230, 100));
        lblStatus.setBounds(0, 455, 480, 22);
        main.add(lblStatus);

        // Hint
        JLabel hint = new JLabel("Default Login:  admin / admin123   or   user / user123",
            SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(160, 215, 255));
        hint.setBounds(0, 472, 480, 20);
        main.add(hint);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        setContentPane(main);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("  Please enter both username and password!");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return;

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                ps.close();
                new DashboardFrame(username, role).setVisible(true);
                this.dispose();
            } else {
                lblStatus.setText("  Invalid username or password. Try again!");
                txtPassword.setText("");
            }
            ps.close();
        } catch (SQLException ex) {
            lblStatus.setText("  DB Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}