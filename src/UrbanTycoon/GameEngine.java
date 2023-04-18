/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UrbanTycoon;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Felhasználó
 */
class GameEngine extends JPanel{
    
    private final int FPS = 240;
    private final int WIDTH = 80;
    private final int HEIGHT = 80;
    private final int FIELDSIZE = 20; //TODO
    private final int FIELDROWSNUM = 8;
    private final int FIELDCOLSNUM = 16;
    private final int INITIALMONEY = 100000;
    private final int INITIALRESIDENT = 20;
    private final int ZONEPRICE = 250;
    private final int ROADPRICE = 75;
    private final int STADIUMPRICE = 1000;
    private final int POLICESTATIONPRICE = 750;
    private final int FIRESTATIONPRICE = 750;
    private final double ANNUALFEEPERCENTAGE = 0.3; // playerBuildIt annualFee = price * ANNUALFEEPERCENTAGE
    private final int RESIDENTCAPACITY = 5;
    private final int WORKPLACECAPACITY = 18;
    private final double REFUND = 0.4;
    private final int RADIUS = 4;
    private final int CRITSATISFACTION = -5;
    
    
    private City city;
    private Date time;
    private boolean paused = false;
    private int speed;
    private final int[] minutesPerSecondIfSpeedIsIndex = {180,2880,43200}; // 3 ora, 2 nap, 30 nap
    private Image background;
    private Timer newFrameTimer;
    private Timer gameTickTimer;
    private final JButton pauseButton, timeSlowButton, timeAccButton, taxUpButton, taxDownButton, destroyButton, nominateIndButton, nominateResButton, nominateSerButton, buildRoadButton, buildStadiumButton, buildPSButton;
    private final JLabel moneyLabel,dateLabel;
    private int prevSelectedFieldX = -1;
    private int prevSelectedFieldY = -1;

//----------------------------
    //fv-ek
//-----------------------------
    
    public GameEngine() {
        super();
        background = new ImageIcon("data/graphics/background.jpeg").getImage(); // ide majd valami más háttér kerül
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!paused){
                    //System.out.println("Clicked! Position: x = " + e.getX() + " y = " + e.getY());
                    fieldSelect(e.getX(),e.getY());
                }
            }
        });
        
        newGame();
        
        this.pauseButton = new JButton("pause");
        pauseButton.addActionListener((ActionEvent ae)->togglePause());
        this.add(pauseButton);
        
        this.timeSlowButton = new JButton("slow Time");
        timeSlowButton.addActionListener((ActionEvent ae)->slowDownTime());
        this.add(timeSlowButton);
        
        this.timeAccButton = new JButton("acc Time");
        timeAccButton.addActionListener((ActionEvent ae)->speedUpTime());
        this.add(timeAccButton);
        
        this.taxUpButton = new JButton("inc Tax");
        taxUpButton.addActionListener((ActionEvent ae)->increaseTax());
        this.add(taxUpButton);
        
        this.taxDownButton = new JButton("lower Tax");
        taxDownButton.addActionListener((ActionEvent ae)->lowerTax());
        this.add(taxDownButton);
        
        this.destroyButton = new JButton("Destroy");
        destroyButton.addActionListener((ActionEvent ae)->tryDenominateOrDestroyZone());
        this.add(destroyButton);
        
        this.nominateIndButton = new JButton("nominate industrial");
        nominateIndButton.addActionListener((ActionEvent ae)->city.selectField(IndustrialZone.class));
        this.add(nominateIndButton);
        
        this.nominateResButton = new JButton("nominate residential");
        nominateResButton.addActionListener((ActionEvent ae)->city.selectField(ResidentialZone.class));
        this.add(nominateResButton);
        
        this.nominateSerButton = new JButton("nominate service");
        nominateSerButton.addActionListener((ActionEvent ae)->city.selectField(ServiceZone.class));
        this.add(nominateSerButton);
        
        this.buildRoadButton = new JButton("build Road");
        buildRoadButton.addActionListener((ActionEvent ae)->city.build(Road.class));
        this.add(buildRoadButton);
        
        this.buildStadiumButton = new JButton("build Stad");
        buildStadiumButton.addActionListener((ActionEvent ae)->city.build(Stadium.class));
        this.add(buildStadiumButton);
        
        this.buildPSButton = new JButton("build PS");
        buildPSButton.addActionListener((ActionEvent ae)->city.build(PoliceStation.class));
        this.add(buildPSButton);
        
        this.moneyLabel = new JLabel("Funds: ");
        this.add(moneyLabel);
        this.dateLabel = new JLabel(time.toString());
        this.add(dateLabel);
    }
    
    @Override
    protected void paintComponent(Graphics grphcs){
        super.paintComponent(grphcs);
        grphcs.drawImage(background, 0, 0, (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight(), null); // háttérkép kirajzolása
        for (int i = 0; i < FIELDROWSNUM; i++) {
            for (int j = 0; j < FIELDCOLSNUM; j++) {
                if (!city.getFields()[i][j].isFree()) {
                    city.getFields()[i][j].getBuilding().draw(grphcs);
                } else { // Buildable is null so cannot draw it -> have to call Field draw method
                    city.getFields()[i][j].draw(grphcs);
                }
                moneyLabel.setText("Funds: " + city.getBudget());
                dateLabel.setText(time.toString());
            }
        }

    }
    
    private void newGame() {
        city = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION, INITIALMONEY, ZONEPRICE, ROADPRICE, STADIUMPRICE, POLICESTATIONPRICE, FIRESTATIONPRICE, ANNUALFEEPERCENTAGE, RESIDENTCAPACITY, WORKPLACECAPACITY, REFUND, RADIUS, WIDTH, HEIGHT);
        // date alaphelyzetbe
        time = new Date(1980,1,1,0,0);
        paused = false;
        speed = 1;
        
        newFrameTimer = new Timer(1000 / FPS, new NewFrameListener());
        newFrameTimer.start();
        gameTickTimer = new Timer(1000,new GameTickListener());
        gameTickTimer.start();
    }
    
    private void saveGame() {
        /* RÉSZFELADAT: Perzisztencia */
    }
    
    private void loadGame() {
        /* RÉSZFELADAT: perzisztencia */
    }
    
    private void speedUpTime() {
        if (speed < 3) {
            speed++;
        }
    }
    
    private void slowDownTime() {
        if (speed > 1) {
            speed--;
        }
    }
    
    private void togglePause() {
        paused = !paused;
    }
    
    private void increaseTax(){
        city.increaseTax();
    }
    
    private void lowerTax(){
        city.lowerTax();
    }
    
    private void fieldSelect(int mouseX, int mouseY){
        int fieldIndexX,fieldIndexY;
        
        fieldIndexX = (int)Math.floor(mouseX / (double)WIDTH)-1;
        fieldIndexY = (int)Math.floor(mouseY / (double)HEIGHT)-1;
        
        if(fieldIndexX >=0 && fieldIndexX < FIELDCOLSNUM && fieldIndexY >= 0 && fieldIndexY < FIELDROWSNUM){
            if (prevSelectedFieldX != -1 && prevSelectedFieldY != -1) { // ha már van selected Field
                if (city.getFields()[prevSelectedFieldY][prevSelectedFieldX].isFree()) {
                    city.getFields()[prevSelectedFieldY][prevSelectedFieldX].unselect();
                } else {
                    boolean accessible = true;
                    if(city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding() instanceof Zone zone && !zone.isBuiltUp() && !city.isAccessibleOnRoad(city.getFields()[prevSelectedFieldY][prevSelectedFieldX]))
                        accessible = false;
                    city.getFields()[prevSelectedFieldY][prevSelectedFieldX].getBuilding().unselect(accessible);
                }
            }
            
            city.fieldSelect(fieldIndexX,fieldIndexY);
            if (city.getFields()[fieldIndexY][fieldIndexX].isFree()) {
                city.getFields()[fieldIndexY][fieldIndexX].select();
            } else {
                boolean accessible = true;
                if(city.getFields()[fieldIndexY][fieldIndexX].getBuilding() instanceof Zone zone && !zone.isBuiltUp() && !city.isAccessibleOnRoad(city.getFields()[fieldIndexY][fieldIndexX]))
                    accessible = false;
                city.getFields()[fieldIndexY][fieldIndexX].getBuilding().select(accessible);
            }
            
            // elmenteni x, y indexeket, hogy legközelebbi kiválasztáskor visszarakja unselected-re
            prevSelectedFieldX = fieldIndexX;
            prevSelectedFieldY = fieldIndexY;
        }
    }
    
    // itt minden a ( city.selectedField : Field )-del dolgozik
    
    private void showFieldInfo(){
        String fieldInfo = city.getFieldInfo();
        //TODO csinálni vele vmit. pl uj jFrame-ben, vagy másik grafikus popup-ban
    }
    
    private void nominateAsIndustrialZone(){
        city.nominateAsIndustrialZone();
    }
    
    private void nominateAsServiceZone(){
        city.nominateAsServiceZone();
    }
    
    private void nominateAsResidentialZone(){
        city.nominateAsResidentialZone();
    }
    
    private void buildRoad(){
        city.buildRoad();
    }
    
    private void buildPoliceStation(){
        city.buildPoliceStation();
    }
    
    private void buildFireStation(){
        
    }
    
    private void buildStadium(){
        city.buildStadium();
    }
    
    private void tryDenominateOrDestroyZone(){
        city.tryDenominateOrDestroyZone();
    }
    
            
    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(!paused){
                
                repaint();
            }
        }
    }
    class GameTickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae){
            if(!paused){
                int howManyMinutes = minutesPerSecondIfSpeedIsIndex[speed-1];
                Date prevTime = new Date(time.getYear(),time.getMonth(),time.getDay(),time.getHour(),time.getMinute());
                time.nMinutesElapsed(howManyMinutes);
                if(prevTime.getYear() != time.getYear()) city.yearElapsed();
                int modelPerformTicks = time.howManyDaysPassed(prevTime);
                city.performTicks(modelPerformTicks);
            }
        }
    }
}
