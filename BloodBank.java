import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class BloodBank extends JFrame implements ActionListener {

    JTextField nameField, bloodField, phoneField;
    JButton addButton, viewButton, searchButton, countButton, editButton;
    ArrayList<Donor> donorList = new ArrayList<>();
    File file = new File("donors.dat");

    public BloodBank() {

        loadData();

        setTitle("Blood Bank Management System");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int c = JOptionPane.showConfirmDialog(
                        BloodBank.this,
                        "Exit program?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        BloodBackgroundPanel panel = new BloodBackgroundPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setContentPane(panel);

        add(createLabel("Donor Name:"));
        nameField = new JTextField();
        add(nameField);

        add(createLabel("Blood Group:"));
        bloodField = new JTextField();
        add(bloodField);

        add(createLabel("Phone Number:"));
        phoneField = new JTextField();
        add(phoneField);

        nameField.addActionListener(e -> bloodField.requestFocus());
        bloodField.addActionListener(e -> phoneField.requestFocus());
        phoneField.addActionListener(e -> addButton.requestFocus());

        addButton = new JButton("Add Donor");
        viewButton = new JButton("View Donors");
        searchButton = new JButton("Search Donor");
        countButton = new JButton("Total Donors");
        editButton = new JButton("Edit Donor"); 

        JButton[] buttons = {addButton, viewButton, searchButton, countButton, editButton};

        for (JButton b : buttons) {
            b.setBackground(new Color(178, 34, 34));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 14));
            b.addActionListener(this);
            add(b);
        }

        JLabel slogan = new JLabel("Donate Blood, Save Life √", SwingConstants.CENTER);
        slogan.setFont(new Font("Arial", Font.BOLD, 18));
        slogan.setForeground(new Color(178, 34, 34));
        add(slogan);

        setVisible(true);
    }

    JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 16));
        return l;
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {
            String name = nameField.getText();
            String blood = bloodField.getText().toUpperCase();
            String phone = phoneField.getText();

            if (name.isEmpty() || blood.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields");
            } else {
                donorList.add(new Donor(name, blood, phone));
                saveData();
                JOptionPane.showMessageDialog(this, "Donor Added");
                nameField.setText("");
                bloodField.setText("");
                phoneField.setText("");
                nameField.requestFocus();
            }
        }

        else if (e.getSource() == viewButton) {
            if (donorList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No donors");
            } else {
                String s = "";
                for (Donor d : donorList) s += d + "\n";
                JOptionPane.showMessageDialog(this, s);
            }
        }

        else if (e.getSource() == searchButton) {
            String bg = JOptionPane.showInputDialog(this, "Enter Blood Group");
            if (bg == null || bg.isEmpty()) return;
            String r = "";
            for (Donor d : donorList)
                if (d.getBloodGroup().equalsIgnoreCase(bg)) r += d + "\n";
            JOptionPane.showMessageDialog(this, r.isEmpty() ? "No donor found" : r);
        }

        else if (e.getSource() == countButton) {
            JOptionPane.showMessageDialog(this, "Total Donors: " + donorList.size());
        }

        else if (e.getSource() == editButton) {
            String searchPhone = JOptionPane.showInputDialog(this, "Enter phone number of donor to edit:");
            if (searchPhone == null || searchPhone.isEmpty()) return;

            Donor found = null;
            for (Donor d : donorList) {
                if (d.getPhone().equals(searchPhone)) {
                    found = d;
                    break;
                }
            }

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Donor not found");
                return;
            }

            String newName = JOptionPane.showInputDialog(this, "Enter new name:", found.name);
            String newBlood = JOptionPane.showInputDialog(this, "Enter new blood group:", found.bloodGroup);
            String newPhone = JOptionPane.showInputDialog(this, "Enter new phone:", found.phone);

            if (newName != null && newBlood != null && newPhone != null &&
                !newName.isEmpty() && !newBlood.isEmpty() && !newPhone.isEmpty()) {
                found.name = newName;
                found.bloodGroup = newBlood.toUpperCase();
                found.phone = newPhone;
                saveData();
                JOptionPane.showMessageDialog(this, "Donor Updated");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Edit cancelled.");
            }
        }
    }

    void saveData() {
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file));
            o.writeObject(donorList);
            o.close();
        } catch (Exception e) {}
    }

    void loadData() {
        try {
            ObjectInputStream i = new ObjectInputStream(new FileInputStream(file));
            donorList = (ArrayList<Donor>) i.readObject();
            i.close();
        } catch (Exception e) {}
    }

    class Donor implements Serializable {
        String name, bloodGroup, phone;
        Donor(String n, String b, String p) {
            name = n;
            bloodGroup = b;
            phone = p;
        }
        String getBloodGroup() { return bloodGroup; }
        String getPhone() { return phone; }
        public String toString() {
            return "Name: " + name + ", Blood: " + bloodGroup + ", Phone: " + phone;
        }
    }

    class BloodBackgroundPanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(245, 250, 255),
                    0, getHeight(), new Color(255, 220, 220));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(200, 0, 0, 20));
            for (int i = 0; i < getWidth(); i += 50) g2.drawLine(i, 0, i, getHeight());
            for (int j = 0; j < getHeight(); j += 50) g2.drawLine(0, j, getWidth(), j);

            g2.setColor(new Color(220, 0, 0, 40));
            g2.setFont(new Font("Arial", Font.BOLD, 140));
            g2.drawString("🩸", getWidth()/2 - 60, getHeight()/2 + 50);

            g2.setColor(new Color(178, 34, 34));
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("BLOOD BANK", 150, getHeight() - 40);

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Hospital Blood Donation System", 200, getHeight() - 15);
        }
    }

    public static void main(String[] args) {
        new BloodBank();
    }
}
