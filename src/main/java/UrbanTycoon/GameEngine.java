/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UrbanTycoon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JTextField;
//tryagain commit

/**
 *
 * @author Felhasználó
 */
class GameEngine extends JPanel {

    private final int FPS = 240;
    private int FIELDSIZE;

    private final int FIELDROWSNUM = 8;
    private final int FIELDCOLSNUM = 16;
    private final int INITIALMONEY = 100000;
    private final int INITIALRESIDENT = 20;
    private final int ZONEPRICE = 250;
    private final int ROADPRICE = 75;
    private final int STADIUMPRICE = 1000;
    private final int POLICESTATIONPRICE = 750;
    private final int FIRESTATIONPRICE = 750;
    private final int FORESTPRICE = 500;
    private final double ANNUALFEEPERCENTAGE = 0.3; // playerBuildIt annualFee = price * ANNUALFEEPERCENTAGE
    private final int RESIDENTCAPACITY = 5;
    private final int WORKPLACECAPACITY = 18;
    private final double REFUND = 0.4;
    private final int RADIUS = 3;
    private final int CRITSATISFACTION = -5;
    private final int MOVEINATLEASTSATISFACTION = 5;
    private final double CHANCEOFFIRE = 0.05; // default 0.05 (change it 0.0 for tests)
    private final JFrame saveGameFrame = new JFrame("Save game");
    private final JFrame loadGameFrame = new JFrame("Load game");
    private final JPanel saveGamePanel = new JPanel();
    private final JPanel loadGamePanel = new JPanel();

    private City city;
    private Date time;

    private boolean paused = false;

    private int speed;
    private final int[] minutesPerSecondIfSpeedIsIndex = { 180, 2880, 43200 }; // 3 ora, 2 nap, 30 nap
    private final Timer newFrameTimer;
    private final Timer gameTickTimer;
    private final Dimension screenSize;
    public JComboBox<String> savesList;

    private int prevSelectedFieldX = -1;
    private int prevSelectedFieldY = -1;

    public GameEngine(Dimension screenSize, int fieldSize) {
        super();
        this.FIELDSIZE = fieldSize;
        this.screenSize = screenSize;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!paused) {
                    // System.out.println("Clicked! Position: x = " + e.getX() + " y = " +
                    // e.getY());
                    fieldSelect(e.getX(), e.getY());
                }
            }
        });

        city = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION,
                MOVEINATLEASTSATISFACTION, INITIALMONEY,
                ZONEPRICE, ROADPRICE, STADIUMPRICE, POLICESTATIONPRICE, FIRESTATIONPRICE, FORESTPRICE,
                ANNUALFEEPERCENTAGE,
                RESIDENTCAPACITY, WORKPLACECAPACITY, REFUND, CHANCEOFFIRE, RADIUS, screenSize);
        time = new Date(1980, 1, 1, 0, 0);
        speed = 1;

        savesList = new JComboBox();
        JTextField saveNameTextField = new JTextField();
        JButton confirmSaveButton = new JButton("Save");
        confirmSaveButton.addActionListener((var ae) -> saveGame(saveNameTextField.getText() + ".sav"));
        saveGamePanel.add(saveNameTextField);
        saveNameTextField.setPreferredSize(new Dimension(80, 20));
        saveGamePanel.add(confirmSaveButton);
        saveGameFrame.add(saveGamePanel);
        saveGameFrame.setPreferredSize(new Dimension(300, 100));

        JButton confirmLoadButton = new JButton("Load");
        confirmLoadButton.addActionListener((var ae) -> loadGame(savesList.getItemAt(savesList.getSelectedIndex())));
        loadGamePanel.add(confirmLoadButton);
        loadGamePanel.add(savesList);
        loadGameFrame.add(loadGamePanel);
        loadGameFrame.setPreferredSize(new Dimension(300, 100));

        newFrameTimer = new Timer(1000 / FPS, new NewFrameListener());
        newFrameTimer.start();
        gameTickTimer = new Timer(1000, new GameTickListener());
        gameTickTimer.start();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        for (int i = 0; i < FIELDROWSNUM; i++) {
            for (int j = 0; j < FIELDCOLSNUM; j++) {
                if (!city.getFields()[i][j].isFree()) {
                    city.getFields()[i][j].getBuilding().draw(grphcs);
                } else { // Buildable is null so cannot draw it -> have to call Field draw method
                    city.getFields()[i][j].draw(grphcs);
                }
                // would be better to call this methods outside of paintcomponent
                UrbanTycoonGUI.changeLabels(time.toString(), city.getResidents().size(), city.getSatisfaction(),
                        city.getTax() + "$", city.getBudget() + "$");
                UrbanTycoonGUI.checkActionPrice(city.getBudget(), ZONEPRICE, ROADPRICE, STADIUMPRICE,
                        POLICESTATIONPRICE, FIRESTATIONPRICE, FORESTPRICE);
            }
        }
    }

    void newGame() {
        city.restart(INITIALRESIDENT, FIELDROWSNUM, FIELDCOLSNUM, INITIALMONEY);
        // date alaphelyzetbe
        time = new Date(1980, 1, 1, 0, 0);
        paused = false;
        speed = 1;
    }

    public void initSave() {
        paused = true;
        saveGameFrame.pack();
        saveGameFrame.setVisible(true);
    }

    private void saveGame(String saveName) {
        File[] usedFiles = getFiles();
        for (File f : usedFiles)
            if (f.getName().equals(saveName)) {
                JFrame frame = new JFrame("Létező mentés!");
                JPanel panel = new JPanel();
                frame.add(panel);
                panel.add(new JLabel("Felülírod?"));
                JButton confirmButton = new JButton("Igen");
                JButton rejectButton = new JButton("Nem");
                confirmButton.addActionListener((var ae) -> {
                    frame.dispose();
                    saveInto(f);
                    paused = false;
                    saveGameFrame.setVisible(false);
                });
                rejectButton.addActionListener((var ae) -> frame.dispose());
                panel.add(confirmButton);
                panel.add(rejectButton);
                frame.setPreferredSize(new Dimension(300, 100));
                frame.pack();
                frame.setVisible(true);
                return;
            }
        File f = new File("data/persistence/saves/" + saveName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.exit(1);
        }
        saveInto(f);
        paused = false;
        saveGameFrame.setVisible(false);
    }

    private void saveInto(File f) {
        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println(time.toString());
            pw.print(city.saveGame());
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public void initLoad(JComboBox<String> savesList) {
        paused = true;
        savesList.removeAllItems();
        File[] saves = getFiles();
        for (File f : saves) {
            savesList.addItem(f.getName().substring(0, f.getName().length() - 4));
        }
        loadGameFrame.pack();
        loadGameFrame.setVisible(true);
    }

    private void loadGame(String fileName) {
        try (Scanner s = new Scanner(new File("data/persistence/saves/" + fileName + ".sav"))) {
            time = new Date(s.nextLine());
            city = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION,
                    MOVEINATLEASTSATISFACTION, INITIALMONEY,
                    ZONEPRICE, ROADPRICE, STADIUMPRICE, POLICESTATIONPRICE, FIRESTATIONPRICE, FORESTPRICE,
                    ANNUALFEEPERCENTAGE,
                    RESIDENTCAPACITY, WORKPLACECAPACITY, REFUND, CHANCEOFFIRE, RADIUS, screenSize);
            city.loadGame(s, true);
            loadGameFrame.setVisible(false);
            paused = false;
            speed = 1;
            prevSelectedFieldX = -1;
            prevSelectedFieldY = -1;
            repaint();
        } catch (FileNotFoundException e) {
            System.exit(100);
        }
    }

    public int getSpeed() {
        return speed;
    }

    public Date getTime() {
        return time;
    }

    public int getZONEPRICE() {
        return ZONEPRICE;
    }

    public int getROADPRICE() {
        return ROADPRICE;
    }

    public int getSTADIUMPRICE() {
        return STADIUMPRICE;
    }

    public int getPOLICESTATIONPRICE() {
        return POLICESTATIONPRICE;
    }

    public int getFIRESTATIONPRICE() {
        return FIRESTATIONPRICE;
    }

    public int getFORESTPRICE() {
        return FORESTPRICE;
    }

    private File[] getFiles() {
        return new File("data/persistence/saves").listFiles();
    }

    public boolean isPaused() {
        return paused;
    }

    public void speedUpTime() {
        if (speed < 3) {
            speed++;
        }
    }

    public void slowDownTime() {
        if (speed > 1) {
            speed--;
        }
    }

    public void togglePause() {
        paused = !paused;
    }

    public void increaseTax() {
        city.increaseTax();
    }

    public void lowerTax() {
        city.lowerTax();
    }

    private void fieldSelect(int mouseX, int mouseY) {
        int fieldIndexX, fieldIndexY;

        fieldIndexX = (int) Math
                .floor((mouseX - city.getFields()[0][0].getX()) / (double) city.getFields()[0][0].getWidth());
        fieldIndexY = (int) Math
                .floor((mouseY - city.getFields()[0][0].getY()) / (double) city.getFields()[0][0].getHeight());

        if (fieldIndexX >= 0 && fieldIndexX < FIELDCOLSNUM && fieldIndexY >= 0 && fieldIndexY < FIELDROWSNUM) {
            if (prevSelectedFieldX != -1 && prevSelectedFieldY != -1) { // ha már van selected Field
                if (city.getFields()[prevSelectedFieldY][prevSelectedFieldX].isFree()) {
                    city.getFields()[prevSelectedFieldY][prevSelectedFieldX].unselect();
                } else {
                    boolean accessible = true;
                    if (city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding() instanceof Zone zone
                            && !zone.isBuiltUp()
                            && !city.isAccessibleOnRoad(city.getFields()[prevSelectedFieldY][prevSelectedFieldX]))
                        accessible = false;
                    city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding().unselect(accessible);
                }
            }
            if (city.getFields()[fieldIndexY][fieldIndexX].isFree()) {
                city.getFields()[fieldIndexY][fieldIndexX].select();
            } else {
                boolean accessible = true;
                if (city.getFields()[fieldIndexY][fieldIndexX].getBuilding() instanceof Zone zone && !zone.isBuiltUp()
                        && !city.isAccessibleOnRoad(city.getFields()[fieldIndexY][fieldIndexX]))
                    accessible = false;
                city.getFields()[fieldIndexY][fieldIndexX].getBuilding().select(accessible);
            }

            city.fieldSelect(fieldIndexY, fieldIndexX);
            // elmenteni x, y indexeket, hogy legközelebbi kiválasztáskor visszarakja
            // unselected-re
            prevSelectedFieldX = fieldIndexX;
            prevSelectedFieldY = fieldIndexY;
        }
    }

    // itt minden a ( city.selectedField : Field )-del dolgozik

    public void tryDenominateOrDestroyZone() {
        city.tryDenominateOrDestroyZone();
    }

    public String budgetInfo() {
        return "<html><h2><font color=#00a605>Annual incomes</font><h2></html>\nResident tax: " + city.getTax()
                + "$/residential  (" + city.getResidentsNum() +
                " residents)\nEmployer tax: " + city.getTax() + "$/employer  (" + city.getResidentsNum()
                + " employers)\n\n" +
                "<html><h2><font color=#fc1c03>Annual outcomes</font></h2></html>\nRoad maintenance fee: "
                + city.getAnnualFee(ROADPRICE) + "$/road  (" + city.countField(Road.class) +
                " roads)\nStadium maintenance fee: " + city.getAnnualFee(STADIUMPRICE) + "$/stadium  ("
                + city.countField(Stadium.class) + " stadiums)" +
                "\nPolice station maintenance fee: " + city.getAnnualFee(POLICESTATIONPRICE) + "$/police station  ("
                + city.countField(PoliceStation.class) + " police stations)" +
                "\nFire station maintenance fee: " + city.getAnnualFee(FIRESTATIONPRICE) + "$/fire station  ("
                + city.countField(FireStation.class) + " fire stations)\n\n" +
                "<html><h3><font color=#00a605>Annual income:</font></h3></html>\n+ $"
                + city.getTax() * city.getResidentsNum() * 2 +
                "\n<html><h3><font color=#fc1c03>Annual outcome:</font></h3></html>\n- $" + city.calculateAnnualFee();
    }

    public void zoneInfoPopup() {
        if (zoneInfo() == null || "".equals(zoneInfo()))
            return;
        Zone selectedZone = (Zone) city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding();
        String title;
        title = (selectedZone instanceof ResidentialZone ? "Residential zone (" : "Workplace zone (")
                + selectedZone.getPeopleNum() + "/"
                + selectedZone.getCapacity() + ")";
        new PopupInfo(null, zoneInfo(), title);
    }

    public static boolean showConfirmationDialog(String message, String title) {
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private String zoneInfo() {
        if (prevSelectedFieldX != -1 && prevSelectedFieldY != -1
                && !city.getFields()[prevSelectedFieldY][prevSelectedFieldX].isFree()
                && city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding() instanceof Zone) {
            Field selectedField = city.getFields()[prevSelectedFieldY][prevSelectedFieldX];
            Buildable selectedBuilding = selectedField.getBuilding();
            if (selectedBuilding instanceof ResidentialZone residentialZone) {
                String residents = "\n<html><h2><font color=#00a605>Residents</font></h2></html>\n";
                int number = 0;
                for (Resident person : city.getResidents()) {
                    if (person.getHome() == residentialZone) {
                        residents += "#" + ++number + ": " + person.toString() + "\n";
                    }
                }
                return residents;
            } else if (selectedBuilding instanceof Workplace workplace) {
                String residents = "\n<html><h2><font color=#00a605>Employers</font></h2></html>\n";
                int number = 0;
                for (Resident person : city.getResidents()) {
                    if (person.getWorkplace() == workplace) {
                        residents += "#" + ++number + ": " + person.toString() + "\n";
                    }
                }
                return residents;
            } else {
                throw new IllegalArgumentException("Selected field must be ResidentialZone or Workplace!");
            }
        }
        return "";
    }

    private void gameOver() {
        new PopupInfo(new JFrame(), "You lost!\nCity satisfaction is critical.", "Game over");
        newGame();
    }

    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused) {

                repaint();
            }
        }
    }

    class GameTickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused) {
                int howManyMinutes = minutesPerSecondIfSpeedIsIndex[speed - 1];
                Date prevTime = new Date(time.getYear(), time.getMonth(), time.getDay(), time.getHour(),
                        time.getMinute());
                time.nMinutesElapsed(howManyMinutes);
                if (prevTime.getDay() != time.getDay()) {
                    city.dayElapsed(new Date(time));
                }
                if (prevTime.getMonth() != time.getMonth()) {
                    city.monthElapsed(new Date(time));
                }
                if (prevTime.getYear() != time.getYear()) {
                    city.yearElapsed();
                }

                int modelPerformTicks = time.howManyDaysPassed(prevTime);
                city.performTicks(modelPerformTicks);
                if (city.getSatisfaction() <= CRITSATISFACTION)
                    gameOver();
            }
        }
    }

    public City getCity() {
        return city;
    }
}
