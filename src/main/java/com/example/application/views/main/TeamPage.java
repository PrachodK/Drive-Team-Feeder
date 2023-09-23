package com.example.application.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("team")
public class TeamPage extends VerticalLayout implements HasUrlParameter<String> {

    private String teamNumbers;
    private VerticalLayout contentLayout;
    private Tab selectedTab;
    private List<Anchor> blueAllianceLinks = new ArrayList<>();
    Anchor blueAllianceLink;
String capability;

    // Define buttons for the game piece capabilities
    private Button conesButton;
    private Button cubesButton;
    Component sampleBarGraph;
    private Button topButton;
    private Button middleButton;
    private Button bottomButton;
    private Button twoXSubstationButton;
    private Button oneXSubstationButton;
    private Button groundIntakeButton;
    private Button noPreferenceButton;
    private int currentChartIndex = 0;
    private List<Chart> charts = new ArrayList<>();
    private VerticalLayout teamContent = new VerticalLayout();
    int teleop_avg_points_t1, teleop_avg_points_t2, teleop_avg_points_t3;
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    HorizontalLayout buttonLayout2 = new HorizontalLayout();
    Image teamImage = new Image("images/team2637.png", "Team 2637");

    Connection conn = DriverManager.getConnection("jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3646270", "sql3646270", "n6fbY8TtbY");


    public TeamPage() throws SQLException, ClassNotFoundException {
        setSizeFull();
        teamImage.setWidth("100px"); // Adjust the width as needed
        teamImage.setHeight("100px"); // Adjust the height as needed



    }
    private void showPreviousChart() {
        currentChartIndex = (currentChartIndex - 1 + charts.size()) % charts.size();
        updateChart();
    }

    private void showNextChart() {
        currentChartIndex = (currentChartIndex + 1) % charts.size();
        updateChart();
    }

    private void updateChart() {
        removeAll();
        // Add your existing content back here
        // ...

        add(charts.get(currentChartIndex));
    }
    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        teamNumbers = parameter;
        try {
            initTeamPage();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTeamPage() throws SQLException, ClassNotFoundException {
        // Parse team numbers from parameter
        String[] teams = teamNumbers.split(",");
        int teamNumber1 = Integer.parseInt(teams[0]);
        int teamNumber2 = Integer.parseInt(teams[1]);
        int teamNumber3 = teams.length > 2 ? Integer.parseInt(teams[2]) : 0;

        Chart chart1 = createTeamChart(teamNumber1);
        Chart chart2 = createTeamChart(teamNumber2);
        Chart chart3 = createTeamChart(teamNumber3);

        charts.add(chart1);
        charts.add(chart2);
        charts.add(chart3);


        Button prevButton = new Button("Previous");
        prevButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        prevButton.addClickListener(event -> showPreviousChart());

        Button nextButton = new Button("Next");
        nextButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextButton.addClickListener(event -> showNextChart());

        buttonLayout2 = new HorizontalLayout(prevButton, nextButton);

        // Create buttons for each team
        Tabs teamTabs = new Tabs();
        contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setFlexGrow(10); // Allow contentLayout to occupy available space

        List<Component> teamContentList = new ArrayList<>();

        for (String team : teams) {
            int teamNumber = Integer.parseInt(team);
            String teamTabLabel = "Team " + teamNumber;

            Tab teamTab = new Tab(teamTabLabel);
            teamTabs.add(teamTab);

            // Create content for each team
            VerticalLayout teamContent = createTeamContent(teamNumber1);
            teamContent.setVisible(false); // Initially hide content
            contentLayout.add(teamContent);
            teamContentList.add(teamContent);

            // Create Blue Alliance link for each team
            String blueAllianceLinkUrl = "https://www.thebluealliance.com/team/" + teamNumber;
            blueAllianceLink = new Anchor(blueAllianceLinkUrl, "Team " + teamNumber + " Blue Alliance");
            blueAllianceLink.setTarget("_blank");
            blueAllianceLink.setVisible(false); // Initially hide the Blue Alliance link
            blueAllianceLinks.add(blueAllianceLink);
        }

        // Display the content of the selected team and show the corresponding Blue Alliance link
        teamTabs.addSelectedChangeListener(event -> {
            if (selectedTab != null) {
                selectedTab.removeClassName("selected");
            }
            selectedTab = event.getSelectedTab();
            selectedTab.addClassName("selected");

            int selectedIndex = teamTabs.getSelectedIndex();
            teamContentList.forEach(component -> component.setVisible(false));
            teamContentList.get(selectedIndex).setVisible(true);

            charts.forEach(component -> component.setVisible(false));
            charts.get(selectedIndex).setVisible(true);

            // Show the Blue Alliance link for the selected team
            blueAllianceLinks.forEach(link -> link.setVisible(false));
            blueAllianceLinks.get(selectedIndex).setVisible(true);
        });

        // Show the content and Blue Alliance link for the initially selected team
        int initialSelectedIndex = teamTabs.getSelectedIndex();
        teamContentList.get(initialSelectedIndex).setVisible(true);
        blueAllianceLinks.get(initialSelectedIndex).setVisible(true);
        charts.get(initialSelectedIndex).setVisible(true);


        // Fetch game piece capabilities from your database (replace with your logic)


        // Add components to the layout
        boolean teamHasCone = fetchConeCapability(teamNumber1);
        boolean teamHasCube = fetchCubeCapability(teamNumber1);
        boolean teamHasTop = fetchTopCapability(teamNumber1);
        boolean teamHasMiddle = fetchMiddleCapability(teamNumber1);
        boolean teamHasBottom = fetchBottomCapability(teamNumber1);
        boolean teamHasTwoXSubstation = fetchTwoXSubstationCapability(teamNumber1);
        boolean teamHasOneXSubstation = fetchOneXSubstationCapability(teamNumber1);
        boolean teamHasGroundIntake = fetchGroundIntakeCapability(teamNumber1);
        boolean teamHasNoPreference = fetchNoPreferenceCapability(teamNumber1);

        // Create buttons for game piece capabilities
        conesButton = createCapabilityButton("Cones", teamHasCone);
        cubesButton = createCapabilityButton("Cubes", teamHasCube);
        topButton = createCapabilityButton("Top", teamHasTop);
        middleButton = createCapabilityButton("Middle", teamHasMiddle);
        bottomButton = createCapabilityButton("Bottom", teamHasBottom);
        twoXSubstationButton = createCapabilityButton("2x Substation", teamHasTwoXSubstation);
        oneXSubstationButton = createCapabilityButton("1x Substation", teamHasOneXSubstation);
        groundIntakeButton = createCapabilityButton("Ground Intake", teamHasGroundIntake);
        noPreferenceButton = createCapabilityButton("No Preference", teamHasNoPreference);
        noPreferenceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonsLayout.add(
                conesButton, cubesButton, topButton, middleButton, bottomButton, twoXSubstationButton, oneXSubstationButton, groundIntakeButton, noPreferenceButton
        );
        HorizontalLayout hLayout = new HorizontalLayout();
        teamImage.setMaxWidth("40px");
        teamImage.setMaxHeight("40px");
        Button imgButton = new Button(teamImage);
        imgButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        imgButton.setMaxHeight("300px");
        imgButton.setMaxWidth("300px");
        hLayout.add(imgButton, teamTabs);
        add(hLayout);
        blueAllianceLinks.forEach(link -> add(link));
        add(buttonsLayout);
        imgButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        sampleBarGraph = createTeamChart(teamNumber1);
        add(sampleBarGraph);
        add(buttonLayout2);

        // Add Blue Alliance links to the layout
        setAlignItems(Alignment.CENTER);
    }
    private void addData(int teamNumber1) throws SQLException, ClassNotFoundException {
        buttonsLayout.removeAll();
        remove(contentLayout);
        remove(sampleBarGraph);

        contentLayout.setVisible(false);
        boolean teamHasCone = fetchConeCapability(teamNumber1);
        boolean teamHasCube = fetchCubeCapability(teamNumber1);
        boolean teamHasTop = fetchTopCapability(teamNumber1);
        boolean teamHasMiddle = fetchMiddleCapability(teamNumber1);
        boolean teamHasBottom = fetchBottomCapability(teamNumber1);
        boolean teamHasTwoXSubstation = fetchTwoXSubstationCapability(teamNumber1);
        boolean teamHasOneXSubstation = fetchOneXSubstationCapability(teamNumber1);
        boolean teamHasGroundIntake = fetchGroundIntakeCapability(teamNumber1);
        boolean teamHasNoPreference = fetchNoPreferenceCapability(teamNumber1);


        // Create buttons for game piece capabilities
        conesButton = createCapabilityButton("Cones", teamHasCone);
        cubesButton = createCapabilityButton("Cubes", teamHasCube);
        topButton = createCapabilityButton("Top", teamHasTop);
        middleButton = createCapabilityButton("Middle", teamHasMiddle);
        bottomButton = createCapabilityButton("Bottom", teamHasBottom);
        twoXSubstationButton = createCapabilityButton("2x Substation", teamHasTwoXSubstation);
        oneXSubstationButton = createCapabilityButton("1x Substation", teamHasOneXSubstation);
        groundIntakeButton = createCapabilityButton("Ground Intake", teamHasGroundIntake);
        noPreferenceButton = createCapabilityButton("No Preference", teamHasNoPreference);
        noPreferenceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        buttonsLayout.add(
                conesButton, cubesButton, topButton, middleButton, bottomButton, twoXSubstationButton, oneXSubstationButton, groundIntakeButton, noPreferenceButton
        );
        add(buttonsLayout);
        add(sampleBarGraph);
        add(buttonLayout2);

        createTeamContent(teamNumber1);
    }
    private VerticalLayout createTeamContent(int teamNumber1) throws SQLException, ClassNotFoundException {
        // Create a layout for team-specific content

            teamContent.removeAll();


            teamContent = new VerticalLayout();
            teamContent.setSizeFull();

            // Add components related to team data and charts here
            Chart teamChart = createTeamChart(teamNumber1);
            teamChart.setSizeFull();
            teamChart.setVisible(false);

            TextArea teamCommentsArea = new TextArea();
            teamCommentsArea.setWidth("100%");
            teamCommentsArea.setHeight("300px");
            teamCommentsArea.setVisible(false);

            // You can add other components and data for each team as needed

            teamContent.add(teamChart, teamCommentsArea);

        return teamContent;

    }

    private Chart createTeamChart(int teamNumber1) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3646270", "sql3646270", "n6fbY8TtbY");

        String sql = "SELECT teleop_avg_points FROM TeamSummary2 WHERE team_number = " + teamNumber1;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        teleop_avg_points_t1 = rs.getInt("teleop_avg_points");


        Chart chart = new Chart(ChartType.BAR);
        chart.setMaxWidth("500px");
        chart.setMaxHeight("500px");
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Avg Teleop Graph");
        String teamNum1 = String.valueOf(teamNumber1);
        XAxis x = new XAxis();
        x.setCategories(teamNum1); // Add your categories
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.getLabels().setStep(5); // Set step to 5
        y.setMin(0); // Set the minimum value for the y-axis
        y.setMax(30); // Set the maximum value for the y-axis
        x.setTitle("Team Number");
        y.setTitle("Teleop Avg Points");
        conf.addyAxis(y);

        ListSeries series = new ListSeries("Avg Teleop Points");
        series.setData(teleop_avg_points_t1);

        conf.addSeries(series);

        chart.setSizeFull();
        return chart;
    }

    // Create buttons for game piece capabilities
    private Button createCapabilityButton(String label, boolean enabled) {
        Button button = new Button(label);
        button.setEnabled(enabled);
        return button;
    }

    // Implement database fetching logic for game piece capabilities
    private boolean fetchConeCapability(int teamNumber) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT capableGamePiece FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("capableGamePiece");
        return capability != null && capability.contains("Cone");
    }

    private boolean fetchCubeCapability(int teamNumber) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT capableGamePiece FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("capableGamePiece");
        return capability != null && capability.contains("Cube");
    }

    private boolean fetchTopCapability(int teamNumber) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT nodeLevelsCapable FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("nodeLevelsCapable");
        return capability != null && capability.contains("Top");
    }

    private boolean fetchMiddleCapability(int teamNumber) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT nodeLevelsCapable FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("nodeLevelsCapable");
        return capability != null && capability.contains("Middle");
    }

    private boolean fetchBottomCapability(int teamNumber) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT nodeLevelsCapable FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("nodeLevelsCapable");
        return capability != null && capability.contains("Bottom");
    }

    private boolean fetchTwoXSubstationCapability(int teamNumber) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT preferredIntake FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("preferredIntake");
        return capability != null && capability.contains("Double substation");
    }

    private boolean fetchOneXSubstationCapability(int teamNumber) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT preferredIntake FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("preferredIntake");
        return capability != null && capability.contains("Single substation");
    }

    private boolean fetchGroundIntakeCapability(int teamNumber) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT preferredIntake FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("preferredIntake");
        return capability != null && capability.contains("Ground intake");
    }

    private boolean fetchNoPreferenceCapability(int teamNumber) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String sql = "SELECT preferredIntake FROM PitData WHERE teamNum = " + teamNumber;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        capability = rs.getString("preferredIntake");
        return capability != null && capability.contains("No Preference");
    }

    // Implement database fetching logic for teleop_avg_points
    private double fetchTeleopAvgPoints(int teamNumber) throws SQLException, ClassNotFoundException {
        double teleopAvgPoints = 0;
        return teleopAvgPoints;
    }
}