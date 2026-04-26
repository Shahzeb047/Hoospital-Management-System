package hospitalms.appointments;

import hospitalms.database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class AppointmentPanel extends JPanel {

    private JComboBox<String> cmbPatient, cmbDoctor, cmbStatus;
    private JTextField txtDate, txtTime, txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public AppointmentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(235, 240, 248));
        initComponents();
        populateCombos();
        loadData();
    }

    private void initComponents() {

        // TITLE
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(235, 240, 248));
        titleBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        JLabel title = new JLabel("Appointment Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(10, 70, 130));
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // FORM
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(0, 230));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 12, 6, 12),
            BorderFactory.createLineBorder(new Color(180, 210, 240), 1)));

        // Row 1
        addLabel(form, "Patient *", 15, 20);
        cmbPatient = new JComboBox<>();
        cmbPatient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPatient.setBounds(90, 17, 240, 32);
        form.add(cmbPatient);

        addLabel(form, "Doctor *", 345, 20);
        cmbDoctor = new JComboBox<>();
        cmbDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbDoctor.setBounds(415, 17, 290, 32);
        form.add(cmbDoctor);

        addLabel(form, "Status", 720, 20);
        cmbStatus = new JComboBox<>(new String[]{"Pending","Completed","Cancelled"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBounds(775, 17, 150, 32);
        form.add(cmbStatus);

        // Row 2
        addLabel(form, "Date", 15, 68);
        txtDate = addField(form, 65, 65, 165, 32);
        txtDate.setText(LocalDate.now().toString());

        addLabel(form, "Time", 248, 68);
        txtTime = addField(form, 295, 65, 130, 32);
        txtTime.setText("09:00 AM");

        JLabel note = new JLabel("Format: yyyy-MM-dd  e.g. 2026-04-24");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(new Color(130, 140, 170));
        note.setBounds(440, 73, 280, 18);
        form.add(note);

        // BUTTONS
        int bx = 15, by = 115, bw = 118, bh = 40;
        JButton btnBook    = makeBtn("BOOK",    new Color(34, 139, 34),  bx,      by, bw, bh);
        JButton btnUpdate  = makeBtn("UPDATE",  new Color(30, 100, 200), bx+128,  by, bw, bh);
        JButton btnDelete  = makeBtn("DELETE",  new Color(200, 40, 40),  bx+256,  by, bw, bh);
        JButton btnClear   = makeBtn("CLEAR",   new Color(100, 100, 120),bx+384,  by, bw, bh);
        JButton btnRefresh = makeBtn("REFRESH", new Color(0, 150, 150),  bx+512,  by, bw, bh);
        form.add(btnBook); form.add(btnUpdate); form.add(btnDelete);
        form.add(btnClear); form.add(btnRefresh);

        // SEARCH
        addLabel(form, "Search:", 670, 124);
        txtSearch = addField(form, 740, 120, 175, 32);
        JButton btnSearch = makeBtn("GO", new Color(10, 80, 150), 924, 120, 55, 32);
        form.add(btnSearch);

        add(form, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"ID","Patient Name","Doctor Name","Date","Time","Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 225, 240));
        table.setSelectionBackground(new Color(0, 120, 200, 100));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(238, 245, 255));
                    setForeground(new Color(30, 40, 60));
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // TABLE HEADER - FIXED
        JTableHeader header = table.getTableHeader();
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

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 12, 12, 12),
            BorderFactory.createLineBorder(new Color(180, 210, 240))));
        add(sp, BorderLayout.CENTER);

        // EVENTS
        btnBook.addActionListener(e -> addRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> { populateCombos(); loadData(); });
        btnSearch.addActionListener(e -> searchData());
        txtSearch.addActionListener(e -> searchData());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { fillForm(); }
        });
    }

    private JLabel addLabel(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(50, 70, 100));
        l.setBounds(x, y, 90, 20);
        p.add(l); return l;
    }

    private JTextField addField(JPanel p, int x, int y, int w, int h) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBounds(x, y, w, h);
        f.setBackground(new Color(245, 248, 255));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 190, 230), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        p.add(f); return f;
    }

    private JButton makeBtn(String text, Color bg, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBounds(x, y, w, h);
        return b;
    }

    private void populateCombos() {
        cmbPatient.removeAllItems();
        cmbDoctor.removeAllItems();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT id,name FROM patients ORDER BY name");
            while (rs.next())
                cmbPatient.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT id,name,specialization FROM doctors ORDER BY name");
            while (rs.next())
                cmbDoctor.addItem(rs.getInt("id") + " - Dr." + rs.getString("name")
                    + " (" + rs.getString("specialization") + ")");
        } catch (Exception ex) {}
    }

    private void addRecord() {
        if (cmbPatient.getItemCount() == 0 || cmbDoctor.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please add patients and doctors first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date is required!",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int pid = Integer.parseInt(((String) cmbPatient.getSelectedItem()).split(" - ")[0]);
            int did = Integer.parseInt(((String) cmbDoctor.getSelectedItem()).split(" - ")[0]);
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO appointments (patient_id,doctor_id,date,time,status) VALUES(?,?,?,?,?)");
            ps.setInt(1, pid); ps.setInt(2, did);
            ps.setString(3, txtDate.getText().trim());
            ps.setString(4, txtTime.getText().trim());
            ps.setString(5, (String) cmbStatus.getSelectedItem());
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Appointment booked!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int pid = Integer.parseInt(((String) cmbPatient.getSelectedItem()).split(" - ")[0]);
            int did = Integer.parseInt(((String) cmbDoctor.getSelectedItem()).split(" - ")[0]);
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE appointments SET patient_id=?,doctor_id=?,date=?,time=?,status=? WHERE id=?");
            ps.setInt(1, pid); ps.setInt(2, did);
            ps.setString(3, txtDate.getText().trim());
            ps.setString(4, txtTime.getText().trim());
            ps.setString(5, (String) cmbStatus.getSelectedItem());
            ps.setInt(6, selectedId);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Appointment updated!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "DELETE FROM appointments WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate(); ps.close();
                clearForm(); loadData();
            } catch (Exception ex) {}
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT a.id,p.name,d.name,a.date,a.time,a.status " +
                "FROM appointments a JOIN patients p ON a.patient_id=p.id " +
                "JOIN doctors d ON a.doctor_id=d.id ORDER BY a.id DESC");
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
        } catch (Exception ex) {}
    }

    private void searchData() {
        String q = txtSearch.getText().trim();
        model.setRowCount(0);
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT a.id,p.name,d.name,a.date,a.time,a.status " +
                "FROM appointments a JOIN patients p ON a.patient_id=p.id " +
                "JOIN doctors d ON a.doctor_id=d.id " +
                "WHERE p.name LIKE ? OR d.name LIKE ? OR a.date LIKE ? OR a.status LIKE ?");
            ps.setString(1,"%" + q + "%"); ps.setString(2,"%" + q + "%");
            ps.setString(3,"%" + q + "%"); ps.setString(4,"%" + q + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
        } catch (Exception ex) {}
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) model.getValueAt(row, 0);
        txtDate.setText(model.getValueAt(row, 3).toString());
        txtTime.setText(model.getValueAt(row, 4).toString());
        cmbStatus.setSelectedItem(model.getValueAt(row, 5));
    }

    private void clearForm() {
        txtDate.setText(LocalDate.now().toString());
        txtTime.setText("09:00 AM");
        cmbStatus.setSelectedIndex(0);
        txtSearch.setText("");
        selectedId = -1;
        table.clearSelection();
        populateCombos();
    }
}