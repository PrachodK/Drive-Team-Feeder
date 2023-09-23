package com.example.application.views.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

import java.sql.*;

@Route("")
public class HomePage extends VerticalLayout {

    private IntegerField teamField1 = new IntegerField("Team 1");
    private IntegerField teamField2 = new IntegerField("Team 2");
    private IntegerField teamField3 = new IntegerField("Team 3");
    private Button submitButton = new Button("Submit");

    public HomePage() {
        // Set background color for the home page
        getStyle().set("background-color", "#01a5ec");
        getStyle().set("width", "100%");
        getStyle().set("height", "100vh"); // Set background to cover the whole screen

        // Create a header text
        H1 header = new H1("Drive Team Feeder");
        header.addClassName("home-page-h1-1");
        header.getStyle().set("color", "white"); // Set header text color

        // Style the search fields to be more bold and bigger
        styleField(teamField1);
        styleField(teamField2);
        styleField(teamField3);

        // Center align the header and search fields vertically
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Configure submit button behavior
        submitButton.setEnabled(false);
        submitButton.addClickListener(event -> {
            try {
                handleSubmit();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Add change listeners to fields for validation
        teamField1.addValueChangeListener(e -> validateFields());
        teamField2.addValueChangeListener(e -> validateFields());
        teamField3.addValueChangeListener(e -> validateFields());

        add(header, teamField1, teamField2, teamField3, submitButton);
    }

    private void styleField(IntegerField field) {
        field.getStyle().set("font-weight", "bold");
        field.getStyle().set("font-size", "20px");
    }

    private void handleSubmit() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3646270", "sql3646270", "n6fbY8TtbY");
        Integer team1Value = teamField1.getValue();
        Integer team2Value = teamField2.getValue();
        Integer team3Value = teamField3.getValue();

        if ((team1Value != null && team2Value != null) || (team1Value != null && team3Value != null) || (team2Value != null && team3Value != null)) {
            // Create a comma-separated string of non-null values
            StringBuilder teamNumbers = new StringBuilder();
            if (team1Value != null) {
                teamNumbers.append(team1Value);
            }
            if (team2Value != null) {
                if (teamNumbers.length() > 0) {
                    teamNumbers.append(",");
                }
                teamNumbers.append(team2Value);
            }
            if (team3Value != null) {
                if (teamNumbers.length() > 0) {
                    teamNumbers.append(",");
                }
                teamNumbers.append(team3Value);
            }

            // Check if each team number is in the database
            String[] teamNumbersArray = teamNumbers.toString().split(",");
            boolean allTeamsInDatabase = true;

            for (String teamNumber : teamNumbersArray) {
                int teamNum = Integer.parseInt(teamNumber);
                String sql = "SELECT teleop_avg_points FROM TeamSummary2 WHERE team_number = " + teamNum;

                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    if (!rs.next()) {
                        allTeamsInDatabase = false;
                        break; // Exit the loop if any team is not found in the database
                    }
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle any database-related errors here
                }
            }

            if (allTeamsInDatabase) {
                Notification.show("All teams are in the database!");
                getUI().ifPresent(ui -> ui.navigate("team/" + teamNumbers.toString()));
            } else {
                Notification.show("Some teams are not in the database.");
            }
        } else {
            Notification.show("Please fill in at least 2 team fields.");
        }
    }

    private void validateFields() {
        int filledFields = 0;
        if (teamField1.getValue() != null) {
            filledFields++;
        }
        if (teamField2.getValue() != null) {
            filledFields++;
        }
        if (teamField3.getValue() != null) {
            filledFields++;
        }

        submitButton.setEnabled(filledFields >= 2);
    }
}