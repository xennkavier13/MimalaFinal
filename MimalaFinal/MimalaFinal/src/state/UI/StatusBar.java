package state.UI; // Or your relevant package

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StatusBar extends JPanel {

    private ImageIcon backgroundIcon;
    private ImageIcon foregroundIcon;
    private double value; // 0.0 to 1.0
    private String backgroundPath; // Store paths for potential reloading
    private String foregroundPath;

    public StatusBar(String backgroundPath, String foregroundPath) {
        this.backgroundPath = backgroundPath;
        this.foregroundPath = foregroundPath;
        setOpaque(false); // Make panel transparent
        loadImages(backgroundPath, foregroundPath);
        this.value = 1.0; // Start full
        // Crucial for null layout: Set size explicitly based on background image if possible
        // Do this *after* loading images.
        if (backgroundIcon != null) {
            setPreferredSize(new Dimension(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight()));
        } else {
            setPreferredSize(new Dimension(200, 20)); // Default fallback size
        }
    }




    private void loadImages(String backgroundPath, String foregroundPath) {
        try {
            URL bgUrl = getClass().getResource(backgroundPath);
            URL fgUrl = getClass().getResource(foregroundPath);

            if (bgUrl != null) {
                backgroundIcon = new ImageIcon(bgUrl);
            } else {
                System.err.println("Error: Status bar background image not found: " + backgroundPath);
                backgroundIcon = null; // Ensure it's null if not found
            }

            if (fgUrl != null) {
                foregroundIcon = new ImageIcon(fgUrl);
            } else {
                System.err.println("Error: Status bar foreground image not found: " + foregroundPath);
                foregroundIcon = null; // Ensure it's null if not found
            }
        } catch (Exception e) {
            System.err.println("Error loading status bar images.");
            e.printStackTrace();
            backgroundIcon = null;
            foregroundIcon = null;
        }
    }

    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value)); // Clamp between 0 and 1
        repaint(); // Request a redraw when value changes
    }

    public double getValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Important for non-opaque components

        // Basic checks
        if (backgroundIcon == null || foregroundIcon == null) {
            // Draw a simple fallback if images aren't loaded
            Graphics2D g2dErr = (Graphics2D) g.create();
            g2dErr.setColor(Color.RED);
            g2dErr.fillRect(0, 0, getWidth(), getHeight());
            g2dErr.setColor(Color.WHITE);
            g2dErr.drawString("Status Bar Icons Missing!", 5, getHeight() / 2);
            g2dErr.dispose();
            return;
        }

        int compWidth = getWidth();
        int compHeight = getHeight();
        int fgIconWidth = foregroundIcon.getIconWidth(); // Original width of foreground image
        int fgIconHeight = foregroundIcon.getIconHeight(); // Original height of foreground image

        Graphics2D g2d = (Graphics2D) g.create(); // Create copy for safe modifications

        // 1. Draw Background Image (Scaled to fit component bounds)
        // We still scale the background once here. If background is static,
        // pre-scaling it once elsewhere could be even better.
        g2d.drawImage(backgroundIcon.getImage(), 0, 0, compWidth, compHeight, this);

        // 2. Calculate the portion of the FOREGROUND image to draw
        // How much width of the *original* foreground image corresponds to the current value?
        int sourceFgWidth = (int) (fgIconWidth * this.value);

        if (sourceFgWidth > 0) {
            // How much width should this portion occupy *on the component*?
            int destFgWidth = (int) (compWidth * this.value);

            // Draw only the necessary part of the foreground image.
            // This overload draws a sub-rectangle from the source image
            // onto a destination rectangle on the component.
            g2d.drawImage(foregroundIcon.getImage(),
                    // Destination Rectangle (on the component panel)
                    0, 0, destFgWidth, compHeight,
                    // Source Rectangle (from the original foreground image file)
                    0, 0, sourceFgWidth, fgIconHeight,
                    this); // ImageObserver
        }

        g2d.dispose(); // Release the graphics copy
    }
}