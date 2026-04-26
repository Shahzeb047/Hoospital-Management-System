package hospitalms.patients;

import hospitalms.database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PatientPanel extends JPanel {

    private JTextField txtName, txtAge, txtContact, txtAddress, txtSearch;
    private JComboBox<String> cmbGender, cmbBlood;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public PatientPanel() {
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
        JLabel title = new JLabel("Patient Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(10, 70, 130));
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // FORM PANEL
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(0, 240));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 12, 6, 12),
            BorderFactory.createLineBorder(new Color(180, 210, 240), 1)));

        // Row 1 - Fields
        addLabel(form, "Name *",  15, 20);
        txtName   = addField(form, 85, 17, 210, 32);
        addLabel(form, "Age",    312, 20);
        txtAge    = addField(form, 352, 17, 70, 32);
        addLabel(form, "Gender", 438, 20);
        cmbGender = addCombo(form, new String[]{"Male","Female","Other"}, 498, 17, 120, 32);
        addLabel(form, "Blood",  634, 20);
        cmbBlood  = addCombo(form,
            new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"}, 682, 17, 90, 32);

        // Row 2 - Fields
        addLabel(form, "Contact",  15, 68);
        txtContact = addField(form, 85, 65, 195, 32);
        addLabel(form, "Address", 297, 68);
        txtAddress = addField(form, 367, 65, 418, 32);

        // BUTTONS
        int bx = 15, by = 115, bw = 118, bh = 40;
        JButton btnAdd     = makeBtn("ADD",     new Color(34, 139, 34),  bx,           by, bw, bh);
        JButton btnUpdate  = makeBtn("UPDATE",  new Color(30, 100, 200), bx+128,       by, bw, bh);
        JButton btnDelete  = makeBtn("DELETE",  new Color(200, 40, 40),  bx+256,       by, bw, bh);
        JButton btnClear   = makeBtn("CLEAR",   new Color(100, 100, 120),bx+384,       by, bw, bh);
        JButton btnRefresh = makeBtn("REFRESH", new Color(0, 150, 150),  bx+512,       by, bw, bh);
        form.add(btnAdd); form.add(btnUpdate); form.add(btnDelete);
        form.add(btnClear); form.add(btnRefresh);

        // SEARCH
        addLabel(form, "Search:", 670, 124);
        txtSearch = addField(form, 740, 120, 175, 32);
        JButton btnSearch = makeBtn("GO", new Color(10, 80, 150), 924, 120, 55, 32);
        form.add(btnSearch);

        add(form, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"ID","Name","Age","Gender","Blood Group","Contact","Address"};
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

        // Alternate row colors
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

    // HELPERS
    private JLabel addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(50, 70, 100));
        l.setBounds(x, y, 85, 20);
        p.add(l);
        return l;
    }

    private JTextField addField(JPanel p, int x, int y, int w, int h) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBounds(x, y, w, h);
        f.setBackground(new Color(245, 248, 255));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 190, 230), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        p.add(f);
        return f;
    }

    private JComboBox<String> addCombo(JPanel p, String[] items, int x, int y, int w, int h) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setBounds(x, y, w, h);
        p.add(c);
        return c;
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

    // CRUD
    private void addRecord() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO patients (name,age,gender,blood_group,contact,address) VALUES(?,?,?,?,?,?)");
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtAge.getText().trim());
            ps.setString(3, (String) cmbGender.getSelectedItem());
            ps.setString(4, (String) cmbBlood.getSelectedItem());
            ps.setString(5, txtContact.getText().trim());
            ps.setString(6, txtAddress.getText().trim());
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Patient added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient from table first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE patients SET name=?,age=?,gender=?,blood_group=?,contact=?,address=? WHERE id=?");
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtAge.getText().trim());
            ps.setString(3, (String) cmbGender.getSelectedItem());
            ps.setString(4, (String) cmbBlood.getSelectedItem());
            ps.setString(5, txtContact.getText().trim());
            ps.setString(6, txtAddress.getText().trim());
            ps.setInt(7, selectedId);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Patient updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient first!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete this patient?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                    "DELETE FROM patients WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate(); ps.close();
                JOptionPane.showMessageDialog(this, "Patient deleted!",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM patients ORDER BY id DESC");
            while (rs.next())
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("age"),
                    rs.getString("gender"), rs.getString("blood_group"),
                    rs.getString("contact"), rs.getString("address")});
        } catch (Exception ex) {}
    }

    private void searchData() {
        String q = txtSearch.getText().trim();
        model.setRowCount(0);
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM patients WHERE name LIKE ? OR contact LIKE ? OR blood_group LIKE ?");
            ps.setString(1, "%" + q + "%");
            ps.setString(2, "%" + q + "%");
            ps.setString(3, "%" + q + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("age"),
                    rs.getString("gender"), rs.getString("blood_group"),
                    rs.getString("contact"), rs.getString("address")});
        } catch (Exception ex) {}
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) model.getValueAt(row, 0);
        txtName.setText(model.getValueAt(row, 1).toString());
        Object age = model.getValueAt(row, 2);
        txtAge.setText(age != null ? age.toString() : "");
        cmbGender.setSelectedItem(model.getValueAt(row, 3));
        cmbBlood.setSelectedItem(model.getValueAt(row, 4));
        Object contact = model.getValueAt(row, 5);
        txtContact.setText(contact != null ? contact.toString() : "");
        Object address = model.getValueAt(row, 6);
        txtAddress.setText(address != null ? address.toString() : "");
    }

    private void clearForm() {
        txtName.setText(""); txtAge.setText("");
        txtContact.setText(""); txtAddress.setText(""); txtSearch.setText("");
        cmbGender.setSelectedIndex(0); cmbBlood.setSelectedIndex(0);
        selectedId = -1; table.clearSelection();
    }
}