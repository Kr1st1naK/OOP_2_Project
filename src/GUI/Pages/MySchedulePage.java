package GUI.Pages;

import Database.Models.StudySession;
import GUI.common.AuthenticationController;
import GUI.common.Navigator;
import GUI.common.Sidebar;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MySchedulePage extends JPanel {
    private AuthenticationController authController;
    private int userId;
    private String userName;
    Navigator navigator;

    public MySchedulePage() {}

    public MySchedulePage(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.authController = new AuthenticationController();
        navigator = new Navigator();
        Sidebar sidebar = new Sidebar(navigator, userId, userName);

        setLayout(new BorderLayout());

        add(sidebar, BorderLayout.WEST);

        createAndShowGUI();
    }

    private void createAndShowGUI() {
        this.setLayout(new BorderLayout());

        // Title and button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("My Study Schedule", JLabel.CENTER);
        JButton backButton = new JButton("Dashboard");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(backButton, BorderLayout.EAST);
        this.add(titlePanel, BorderLayout.NORTH);

        // Button event
        backButton.addActionListener(e -> {
            navigator.navigateToMainPage(this, userId, userName);
        });
        // Main panel for schedules
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<StudySession> sessions = authController.getAllStudySessions();
        Map<Integer, ArrayList<StudySession>> sessionsByScheduleId = groupSessionsByScheduleId(sessions);

        for (Map.Entry<Integer, ArrayList<StudySession>> entry : sessionsByScheduleId.entrySet()) {
            Integer scheduleId = entry.getKey();
            ArrayList<StudySession> scheduleSessions = entry.getValue();

            JLabel scheduleLabel = new JLabel("Schedule ID: " + scheduleId);
            scheduleLabel.setFont(new Font("Serif", Font.BOLD, 18));
            scheduleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            mainPanel.add(scheduleLabel);

            String[] columnNames = {"Subject Name", "Date", "Start Time", "End Time", "Status"};
            Object[][] data = new Object[scheduleSessions.size()][5];

            for (int i = 0; i < scheduleSessions.size(); i++) {
                StudySession session = scheduleSessions.get(i);
                data[i][0] = authController.getSubjectNameFromSession(session.getSubject_id());
                data[i][1] = session.getSession_date();
                data[i][2] = session.getStart_time();
                data[i][3] = session.getEnd_time();
                data[i][4] = session.getStatus();
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model) {
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            // Custom renderer for the subject name column
            table.getColumnModel().getColumn(0).setCellRenderer(new SubjectNameRenderer());

            // Table customization
            table.setFillsViewportHeight(true);
            table.setRowHeight(30);
            table.setFont(new Font("SansSerif", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            mainPanel.add(scrollPane);

            // Add delete button
            JButton deleteButton = new JButton("Delete Schedule");
            deleteButton.addActionListener(e -> deleteSchedule(scheduleId));
            mainPanel.add(deleteButton);
        }

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        this.add(mainScrollPane, BorderLayout.CENTER);

        // Footer with user info
        JLabel footerLabel = new JLabel("Logged in as: " + userName, JLabel.CENTER);
        footerLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(footerLabel, BorderLayout.SOUTH);
    }

    private void deleteSchedule(int scheduleId) {
        // Implement the logic to delete the schedule by its ID
        // You need to add a method in your AuthenticationController to handle this
        boolean success = authController.deleteSessionByScheduleId(scheduleId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Schedule deleted successfully.");
            // Refresh the page after deletion
            removeAll();
            createAndShowGUI();
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete the schedule.");
        }
    }

    private Map<Integer, ArrayList<StudySession>> groupSessionsByScheduleId(ArrayList<StudySession> sessions) {
        Map<Integer, ArrayList<StudySession>> sessionsByScheduleId = new TreeMap<>();
        for (StudySession session : sessions) {
            int scheduleId = session.getSchedule_id();
            sessionsByScheduleId.putIfAbsent(scheduleId, new ArrayList<>());
            sessionsByScheduleId.get(scheduleId).add(session);
        }
        return sessionsByScheduleId;
    }

    private static class SubjectNameRenderer extends DefaultTableCellRenderer {
        private static final Color[] COLORS = {Color.PINK, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE, Color.MAGENTA};

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(COLORS[row % COLORS.length]);
            setHorizontalAlignment(JLabel.CENTER);
            return cell;
        }
    }
}
