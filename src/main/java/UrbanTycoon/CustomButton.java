package UrbanTycoon;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomButton extends JButton {
    private boolean selected = false;
    private boolean disabled = false;
    private String type;
    private String name;
    private Runnable func;

    ImageIcon icon, sIcon, dIcon;

    public CustomButton(String pictureName, int size, String type, String name) {
        super();
        this.type = type;
        this.name = name;
        icon = new ImageIcon(
                new ImageIcon(pictureName + ".png").getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        dIcon = new ImageIcon(
                new ImageIcon(pictureName + "Disabled.png").getImage().getScaledInstance(size, size,
                        Image.SCALE_SMOOTH));
        if (type == "actionButton") {
            sIcon = new ImageIcon(
                    new ImageIcon(pictureName + "Selected.png").getImage().getScaledInstance(size, size,
                            Image.SCALE_SMOOTH));
        }
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setIcon(icon);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setPreferredSize(new Dimension(size, size));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (func != null && !disabled) {
                    func.run();
                }
            }

        });
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public void disable() {
        this.disabled = true;
        this.selected = false;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setIcon(dIcon);
    }

    public void enable() {
        this.disabled = false;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setIcon(icon);
    }

    public void unselect() {
        this.selected = false;
        setIcon(icon);
    }

    public void select() {
        this.selected = true;
        setIcon(sIcon);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Runnable getFunc() {
        return func;
    }

    public void setFunc(Runnable func) {
        this.func = func;
    }

}
