package hospitalms.doctors;

import hospitalms.database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DoctorPanel extends JPanel {

    private JTextField txtName, txtPhone, txtSearch;
    private JComboBox<String> cmbSpec, cmbAvail;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public DoctorPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(235, 240, 248));
        initComponents();
        loadData();
    }

    private void initComponents() {

        // TITLE
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(235, 240, 248));
        titleBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        JLabel title = new JLabel("Doctor Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(10, 70, 130));
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // FORM
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(0, 210));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 12, 6, 12),
            BorderFactory.createLineBorder(new Color(180, 210, 240), 1)));

        addLabel(form, "Name *", 15, 20);
        txtName = addField(form, 85, 17, 220, 32);

        addLabel(form, "Specialization", 320, 20);
        String[] specs = {"Cardiologist","Neurologist","Pediatrician",
            "Dermatologist","Orthopedic","General Physician",
            "Gynecologist","ENT Specialist","Ophthalmologist","Psychiatrist","Dentist"};
        cmbSpec = addCombo(form, specs, 435, 17, 200, 32);

        addLabel(form, "Phone", 650, 20);
        txtPhone = addField(form, 710, 17, 175, 32);

        addLabel(form, "Availability", 15, 68);
        String[] avail = {"Mon-Fri","Mon-Wed","Wed-Fri","Weekends","Daily","By Appointment"};
        cmbAvail = addCombo(form, avail, 115, 65, 180, 32);

        // BUTTONS
        int bx = 15, by = 112, bw = 118, bh = 40;
        JButton btnAdd     = makeBtn("ADD",     new Color(34, 139, 34),  bx,      by, bw, bh);
        JButton btnUpdate  = makeBtn("UPDATE",  new Color(30, 100, 200), bx+128,  by, bw, bh);
        JButton btnDelete  = makeBtn("DELETE",  new Color(200, 40, 40),  bx+256,  by, bw, bh);
        JButton btnClear   = makeBtn("CLEAR",   new Color(100, 100, 120),bx+384,  by, bw, bh);
        JButton btnRefresh = makeBtn("REFRESH", new Color(0, 150, 150),  bx+512,  by, bw, bh);
        form.add(btnAdd); form.add(btnUpdate); form.add(btnDelete);
        form.add(btnClear); form.add(btnRefresh);

        // SEARCH
        addLabel(form, "Search:", 670, 122);
        txtSearch = addField(form, 740, 118, 175, 32);
        JButton btnSearch = makeBtn("GO", new Color(10, 80, 150), 924, 118, 55, 32);
        form.add(btnSearch);

        add(form, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"ID","Name","Specialization","Phone","Availability"};
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
        btnAdd.addActionListener(e -> addRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
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
        l.setBounds(x, y, 115, 20);
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

    private JComboBox<String> addCombo(JPanel p, String[] items, int x, int y, int w, int h) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setBounds(x, y, w, h);
        p.add(c); return c;
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

    private void addRecord() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO doctors (name,specialization,phone,availability) VALUES(?,?,?,?)");
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, (String) cmbSpec.getSelectedItem());
            ps.setString(3, txtPhone.getText().trim());
            ps.setString(4, (String) cmbAvail.getSelectedItem());
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Doctor added!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE doctors SET name=?,specialization=?,phone=?,availability=? WHERE id=?");
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, (String) cmbSpec.getSelectedItem());
            ps.setString(3, txtPhone.getText().trim());
            ps.setString(4, (String) cmbAvail.getSelectedItem());
            ps.setInt(5, selectedId);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Doctor updated!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete this doctor?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "DELETE FROM doctors WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate(); ps.close();
                clearForm(); loadData();
            } catch (Exception ex) {}
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM doctors ORDER BY id DESC");
            while (rs.next())
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                    rs.getString("specialization"), rs.getString("phone"),
                    rs.getString("availability")});
        } catch (Exception ex) {}
    }

    private void searchData() {
        String q = txtSearch.getText().trim();
        model.setRowCount(0);
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM doctors WHERE name LIKE ? OR specialization LIKE ?");
            ps.setString(1, "%" + q + "%");
            ps.setString(2, "%" + q + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"),
                    rs.getString("specialization"), rs.getString("phone"),
                    rs.getString("availability")});
        } catch (Exception ex) {}
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) model.getValueAt(row, 0);
        txtName.setText(model.getValueAt(row, 1).toString());
        cmbSpec.setSelectedItem(model.getValueAt(row, 2));
        Object phone = model.getValueAt(row, 3);
        txtPhone.setText(phone != null ? phone.toString() : "");
        cmbAvail.setSelectedItem(model.getValueAt(row, 4));
    }

    private void clearForm() {
        txtName.setText(""); txtPhone.setText(""); txtSearch.setText("");
        cmbSpec.setSelectedIndex(0); cmbAvail.setSelectedIndex(0);
        selectedId = -1; table.clearSelection();
    }
}