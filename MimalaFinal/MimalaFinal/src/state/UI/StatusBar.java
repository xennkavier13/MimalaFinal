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
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();

        // 1. Draw Background Image (stretched to fit component bounds)
        if (backgroundIcon != null) {
            g2d.drawImage(backgroundIcon.getImage(), 0, 0, width, height, this);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.RED);
            g2d.drawString("BG Missing", 5, height / 2);
        }

        // 2. Draw Foreground Image (clipped based on value)
        if (foregroundIcon != null) {
            int foregroundWidth = (int) (width * this.value);
            if (foregroundWidth > 0) {
                // Use clipping for more robustness
                // Define the clipping rectangle based on the calculated width
                Rectangle clipRect = new Rectangle(0, 0, foregroundWidth, height);
                g2d.setClip(clipRect);

                // Draw the foreground image, potentially stretched to the full component size,
                // but only the part within the clip rectangle will be visible.
                g2d.drawImage(foregroundIcon.getImage(), 0, 0, width, height, this);

                // It's good practice to reset the clip, although g2d.dispose() should handle it.
                g2d.setClip(null);
            }
        } else {
            // Fallback if foreground is missing but value > 0
            int foregroundWidth = (int) (width * this.value);
            if (foregroundWidth > 0) {
                g2d.setColor(Color.CYAN); // Example stamina color fallback
                g2d.fillRect(0, 0, foregroundWidth, height);
                g2d.setColor(Color.RED);
                g2d.drawString("FG Missing", 5, height / 2 + 15);
            }
        }

        g2d.dispose();
    }
}