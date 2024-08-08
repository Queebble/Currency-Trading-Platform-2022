package Common.Panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author "Hovercraft Full of Eels", "Rodrigo Azevedo"
 *
 * This is an improved version of Hovercraft Full of Eels (https://stackoverflow.com/users/522444/hovercraft-full-of-eels)
 * answer on StackOverflow: https://stackoverflow.com/a/8693635/753012
 *
 * GitHub user @maritaria has made some performance improvements which can be found in the comment section of this Gist.
 */
public class GraphPanel extends JPanel {
    private int padding = 25;
    private int labelPadding = 75;
    private final Color lineColor = new Color(2, 192, 118, 255);
    private final Color gridColor = new Color(42, 47, 54, 255);
    private final Color graphColor = new Color(25,27,32, 255);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private int numberXDivisions = 9;
    private List<Double> prices;
    private List<LocalDateTime> date;
    private ZoneId zoneId = ZoneId.systemDefault();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Initialises a new graph with given price/data information
     * @param price the price the asset sold for
     * @param date the datetime for the x axis
     */
    public GraphPanel(List<Double> price, List<LocalDateTime> date) {
        this.prices = price;
        this.date = date;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (getMaxDate() - getMinDate());
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            int x1 = (int) ((date.get(i).atZone(zoneId).toEpochSecond() - getMinDate()) * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxScore() - prices.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(graphColor);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(graphColor);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            if (prices.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y0);
                g2.setColor(Color.WHITE);
                String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y0);
        }

        // and for x axis
        for (int i = 0; i < numberXDivisions + 1; i++) {
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / numberXDivisions + padding + labelPadding;
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            if (date.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x0, padding);
                g2.setColor(Color.WHITE);
                String xLabel = LocalDateTime.ofInstant(Instant.ofEpochMilli((long) ((((getMinDate() + (getMaxDate() - getMinDate()) * ((i * 1.0) / numberXDivisions)) * 1000)))), zoneId).format(dtf);
                //String xLabel = date.get(0).format(dtf);
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth + 25, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x0, y1);
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * gets the minimum price boundary
     * @return the minimum price - 1
     */
    private double getMinScore() {
        double minPrice = Double.MAX_VALUE;
        for (Double price : prices) {
            minPrice = Math.min(minPrice, price);
        }
        return minPrice - 1;
    }

    /**
     * gets the maximum price boundary
     * @return the maximum price + 1
     */
    private double getMaxScore() {
        double maxPrice = Double.MIN_VALUE;
        for (Double price : prices) {
            maxPrice = Math.max(maxPrice, price);
        }
        return maxPrice + 1;
    }

    /**
     * gets the minimum date
     * @return the min date
     */
    private long getMinDate() {
        if (date.size() == 0) {
            return LocalDateTime.now().atZone(zoneId).toEpochSecond();
        }
        return date.get(0).atZone(zoneId).toEpochSecond();
    }

    /**
     * gets the maximum date
     * @return the max date
     */
    private long getMaxDate() {
        int i = date.size();
        if (i == 0) {
            return LocalDateTime.now().atZone(zoneId).toEpochSecond();
        }
        return date.get(i-1).atZone(zoneId).toEpochSecond();
    }

    /**
     * sets the price on the graph
     * @param prices price to be set
     */
    public void setPrices(List<Double> prices) {
        this.prices = prices;
        invalidate();
        this.repaint();
    }

    /**
     * sets the date on the graph
     * @param date the date to be set
     */
    public void setDates(List<LocalDateTime> date) {
        this.date = date;
        invalidate();
        this.repaint();
    }

}