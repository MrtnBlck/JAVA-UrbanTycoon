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
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Felhasználó
 */
class GameEngine extends JPanel{
    
    private final int FPS = 240;
    private final int FIELDSIZE = 20; //TODO
    private final int FIELDROWSNUM = 20;
    private final int FIELDCOLSNUM = 20;
    private final int INITIALMONEY = 1000;
    private final int INITIALRESIDENT = 20;
    private final int ZONEPRICE = 250;
    private final int RESIDENTCAPACITY = 5;
    private final int WORKPLACECAPACITY = 18;
    private final double REFUND = 0.4;
    private final int RADIUS = 4;
    private final int CRITSATISFACTION = -5;
    
    
    private City city;
    private Date time;
    private boolean paused = false;
    private int speed = 1;
    private final int[] minutesPerSecondIfSpeedIsIndex = {180,2880,43200}; // 3 ora, 2 nap, 30 nap
    private Image background;
    private Timer newFrameTimer;
    private Timer gameTickTimer;
    
//----------------------------
    //fv-ek
//-----------------------------
    
    public GameEngine() {
        super();
        background = new ImageIcon("data/graphics/tempBackground.jpeg").getImage(); // ide majd valami más háttér kerül
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!paused){
                    System.out.println("Clicked! Position: x = " + e.getX() + " y = " + e.getY());
                }
            }
        });
        newGame();
    }
    
    @Override
    protected void paintComponent(Graphics grphcs){
        super.paintComponent(grphcs);
        grphcs.drawImage(background, 0, 0, (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight(), null); // háttérkép kirajzolása
        /* TODO: mezők kirajzolása */
    }
    
    private void newGame() {
        city = new City(INITIALRESIDENT, FIELDSIZE, FIELDROWSNUM, FIELDCOLSNUM, CRITSATISFACTION, INITIALMONEY, ZONEPRICE);
        // date alaphelyzetbe
        time = new Date(1980,1,1,0,0);
        paused = false;
        speed = 3;
        
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
        int fieldIndexX=0,fieldIndexY=0;
        // TODO math magic required
        city.fieldSelect(fieldIndexX,fieldIndexY);
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
                System.out.println(modelPerformTicks);
                city.performTicks(modelPerformTicks);
            }
        }
    }
}