package hospitalms.dashboard;

import hospitalms.database.DBConnection;
import hospitalms.patients.PatientPanel;
import hospitalms.doctors.DoctorPanel;
import hospitalms.appointments.AppointmentPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {

    private String loggedUser, userRole;
    private JPanel contentPanel;

    public DashboardFrame(String username, String role) {
        this.loggedUser = username;
        this.userRole = role;
        initComponents();
    }

    private void initComponents() {
        setTitle("Hospital Management System");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 80, 140));
        header.setPreferredSize(new Dimension(0, 65));
        header.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblUser = new JLabel("User: " + loggedUser + "   Role: " + userRole);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(180, 225, 255));
        header.add(lblUser, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 30, 50));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 12, 25, 12));

        JLabel menuLabel = new JLabel("  MAIN MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuLabel.setForeground(new Color(100, 160, 220));
        menuLabel.setMaximumSize(new Dimension(196, 30));
        sidebar.add(menuLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(50, 80, 110));
        sep.setMaximumSize(new Dimension(196, 2));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] labels   = {"Dashboard", "Patients", "Doctors", "Appointments", "Logout"};
        String[] prefix   = {"[#]", "[+]", "[D]", "[A]", "[X]"};
        Color[]  bgColors = {
            new Color(0, 120, 200),
            new Color(0, 160, 100),
            new Color(130, 60, 180),
            new Color(200, 120, 0),
            new Color(180, 30, 30)
        };

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JButton btn = createSideBtn(prefix[i], labels[i], bgColors[i]);
            btn.addActionListener(e -> handleNav(idx));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        add(sidebar, BorderLayout.WEST);

        // CONTENT
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(235, 240, 248));
        add(contentPanel, BorderLayout.CENTER);

        showHome();
    }

    private JButton createSideBtn(String prefix, String label, Color bg) {
        JButton btn = new JButton() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(0, 0, 44, getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(prefix, (44 - fm.stringWidth(prefix)) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 2);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString(label, 54, getHeight() / 2 + 5);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(196, 46));
        btn.setPreferredSize(new Dimension(196, 46));
        return btn;
    }

    private void handleNav(int idx) {
        contentPanel.removeAll();
        switch (idx) {
            case 0: showHome(); return;
            case 1: contentPanel.add(new PatientPanel(),     BorderLayout.CENTER); break;
            case 2: contentPanel.add(new DoctorPanel(),      BorderLayout.CENTER); break;
            case 3: contentPanel.add(new AppointmentPanel(), BorderLayout.CENTER); break;
            case 4:
                int c = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) {
                    DBConnection.closeConnection();
                    new hospitalms.login.LoginFrame().setVisible(true);
                    this.dispose();
                }
                return;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHome() {
        contentPanel.removeAll();
        JPanel home = new JPanel(null);
        home.setBackground(new Color(235, 240, 248));

        // Page Title
        JLabel lbl = new JLabel("Dashboard Overview");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(new Color(10, 60, 110));
        lbl.setBounds(25, 20, 400, 38);
        home.add(lbl);

        JLabel sub = new JLabel("Welcome,  " + loggedUser + "   |   Role: " + userRole);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(100, 120, 150));
        sub.setBounds(25, 58, 500, 22);
        home.add(sub);

        // Stat Cards
        int[]    stats  = getStats();
        String[] titles = {"Total Patients", "Total Doctors", "Appointments", "Pending"};
        String[] badges = {"P", "D", "A", "!"};
        Color[]  colors = {
            new Color(0, 150, 120),
            new Color(20, 130, 220),
            new Color(140, 50, 190),
            new Color(220, 130, 0)
        };

        for (int i = 0; i < 4; i++) {
            JPanel card = makeStatCard(badges[i], titles[i],
                    String.valueOf(stats[i]), colors[i]);
            card.setBounds(25 + i * 225, 92, 210, 125);
            home.add(card);
        }

        // Recent Appointments Label
        JLabel recLbl = new JLabel("Recent Appointments");
        recLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recLbl.setForeground(new Color(10, 60, 110));
        recLbl.setBounds(25, 232, 300, 30);
        home.add(recLbl);

        // Table with FIXED headers
        String[]   cols = {"ID", "Patient Name", "Doctor Name", "Date", "Time", "Status"};
        Object[][] data = getRecentAppointments();

        JTable tbl = new JTable(data, cols);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(30);
        tbl.setEnabled(false);
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(210, 220, 235));
        tbl.setFillsViewportHeight(true);

        // Alternate row colors
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(238, 244, 255));
                setForeground(new Color(30, 40, 60));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // FIXED TABLE HEADER
        JTableHeader header = tbl.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = new JLabel(val != null ? val.toString() : "");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(new Color(10, 80, 150));
                lbl.setOpaque(true);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(5, 50, 120)),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return lbl;
            }
        });

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBounds(25, 268, 880, 310);
        sp.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));
        home.add(sp);

        contentPanel.add(home, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel makeStatCard(String badge, String title, String value, Color color) {
        JPanel card = new JPanel(null) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 8, getHeight(), 6, 6);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 235)));

        JPanel circle = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), 35));
                g2.fillOval(0, 0, 46, 46);
                g2.setColor(color);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(badge, (46 - fm.stringWidth(badge)) / 2, 30);
            }
        };
        circle.setOpaque(false);
        circle.setBounds(18, 12, 46, 46);
        card.add(circle);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valLbl.setForeground(color);
        valLbl.setBounds(18, 58, 140, 42);
        card.add(valLbl);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(120, 135, 160));
        titleLbl.setBounds(18, 100, 185, 18);
        card.add(titleLbl);

        return card;
    }

    private int[] getStats() {
        int[] s = new int[4];
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return s;
            ResultSet rs;
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM patients");
            if (rs.next()) s[0] = rs.getInt(1);
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM doctors");
            if (rs.next()) s[1] = rs.getInt(1);
            rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM appointments");
            if (rs.next()) s[2] = rs.getInt(1);
            rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM appointments WHERE status='Pending'");
            if (rs.next()) s[3] = rs.getInt(1);
        } catch (Exception e) {}
        return s;
    }

    private Object[][] getRecentAppointments() {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return new Object[0][0];
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT a.id, p.name, d.name, a.date, a.time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors  d ON a.doctor_id  = d.id " +
                "ORDER BY a.id DESC LIMIT 10");
            while (rs.next())
                rows.add(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)});
        } catch (Exception e) {}
        return rows.toArray(new Object[0][]);
    }
}