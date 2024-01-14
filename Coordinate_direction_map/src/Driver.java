import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Driver {

    public static int animationSec;
    public static ArrayList<TripPoint> trip;
    public static BufferedImage arrow;

    public static void main(String[] args) throws IOException {
        // Read trip data from the CSV file
        TripPoint.readFile("triplog.csv");
        TripPoint.h2StopDetection();

        // Load arrow image for displaying trip markers
        arrow = ImageIO.read(new File("arrow.png"));

        // Create frame
        JFrame frame = new JFrame("Map Viewer");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create top panel for input selections
        JPanel topPanel = new JPanel();
        frame.add(topPanel, BorderLayout.NORTH);

        // Add play button
        JButton play = new JButton("Play");
        topPanel.add(play);

        // Add checkbox to enable/disable stops
        JCheckBox includeStops = new JCheckBox("Include Stops");
        topPanel.add(includeStops);

        // Add dropdown to pick animation time
        String[] timeList = {"Animation Time", "15", "30", "60", "90"};
        JComboBox<String> animationTime = new JComboBox<>(timeList);
        topPanel.add(animationTime);

        // Set up map viewer
        JMapViewer mapViewer = new JMapViewer();
        frame.add(mapViewer);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Set tile source for map viewer
        mapViewer.setTileSource(new OsmTileSource.TransportMap());

        // Add listeners to components
        play.addActionListener(e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    mapViewer.removeAllMapMarkers(); // Remove all markers from the map
                    mapViewer.removeAllMapPolygons();

                    if (includeStops.isSelected()) {
                        trip = TripPoint.getTrip();
                    } else {
                        trip = TripPoint.getMovingTrip();
                    }
                    plotTrip(animationSec, trip, mapViewer);
                    return null;
                }
            };
            worker.execute();
        });

        animationTime.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object selectedItem = animationTime.getSelectedItem();
                if (selectedItem instanceof String) {
                    String selectedString = (String) selectedItem;
                    if (!selectedString.equals("Animation Time")) {
                        animationSec = Integer.parseInt(selectedString);
                        System.out.println("Updated to " + animationSec);
                    }
                }
            }
        });

        // Set the map center and zoom level
        mapViewer.setDisplayPosition(new Coordinate(34.82, -107.99), 6);
    }
 // plot the given trip ArrayList with animation time in seconds
    public static void plotTrip(int seconds, ArrayList<TripPoint> trip, JMapViewer map) throws IOException {
    	// amount of time between each point in milliseconds
    	long delayTime = (seconds * 1000) / trip.size();

    	Coordinate c1;
    	Coordinate c2 = null;
    	MapMarker marker;
    	MapMarker prevMarker = null;
    	MapPolygonImpl line;


    	double angle;

    	for (int i = 0; i < trip.size(); ++i) {
    		c1 = new Coordinate(trip.get(i).getLat(), trip.get(i).getLon());

            if (i != 0) {
                c2 = new Coordinate(trip.get(i-1).getLat(), trip.get(i-1).getLon());
                angle = calculateAngle(c2.getLat(), c2.getLon(), c1.getLat(), c1.getLon());
                BufferedImage rotatedArrow = rotateArrow(arrow, angle);
                marker = new IconMarker(c1, rotatedArrow);
            } else {
                marker = new IconMarker(c1, arrow);
            }
    		map.addMapMarker(marker);
    		if (i != 0) {
    			c2 = new Coordinate(trip.get(i-1).getLat(), trip.get(i-1).getLon());
    		}
    		if (c2 != null) {
    			line = new MapPolygonImpl(c1, c2, c2);


    			line.setColor(Color.RED);


    			line.setStroke(new BasicStroke(3));
    			map.addMapPolygon(line);
    			map.removeMapMarker(prevMarker);
    		}
    		if (c2 != null) {
    		    angle = calculateAngle(c1.getLat(), c1.getLon(), c2.getLat(), c2.getLon());
    		}

    		try {
				Thread.sleep(delayTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		prevMarker = marker;
    	}

    }

    public static BufferedImage rotateArrow(BufferedImage arrow, double angle) {
        double rotationRequired = Math.toRadians(angle);
        double locationX = arrow.getWidth() / 2.0;
        double locationY = arrow.getHeight() / 2.0;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(arrow, null);
    }

    public static double calculateAngle(double lat1, double lon1, double lat2, double lon2) {
        double dLon = lon2 - lon1;
        double dLat = lat2 - lat1;
        double angle = Math.toDegrees(Math.atan2(dLat, dLon));
        return 90 - angle;
    }
}