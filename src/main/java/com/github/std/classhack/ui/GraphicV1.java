package com.github.std.classhack.ui;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GraphicV1 implements UI {
    private static class MainFrame extends JFrame {
        private final JTextArea basicDisplay = new JTextArea();
        private final JTextArea constantPoolDisplay = new JTextArea();
        private final JTextArea fieldsMethodsDisplay = new JTextArea();

        private void openFile(String file) {
            fillClassInfo(file);
        }


        public MainFrame(String file) {
            FlatLightLaf.setup();
            setTitle("Class Hacker");
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            JMenuItem menuItem = new JMenuItem("open file");
            menuItem.addActionListener(e -> openFile(file));
            menu.add(menuItem);
            menuBar.add(menu);
            setJMenuBar(menuBar);

            JPanel mainPanel = new JPanel(new GridLayout(1, 3));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
            basicDisplay.setEnabled(false);
            constantPoolDisplay.setEnabled(false);
            fieldsMethodsDisplay.setEnabled(false);
            mainPanel.add(infoPanel(basicDisplay, "Basic"));
            mainPanel.add(infoPanel(constantPoolDisplay, "Constant Pool"));
            mainPanel.add(infoPanel(fieldsMethodsDisplay, "Fields&Methods"));

            fillClassInfo(file);
            add(mainPanel);
        }

        private void fillClassInfo(String file) {
            basicDisplay.append(file);
            constantPoolDisplay.append(file);
            fieldsMethodsDisplay.append(file);
        }

        private JPanel infoPanel(JTextArea display, String title) {
            JScrollPane scroll = new JScrollPane(display);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(new TitledBorder(new EtchedBorder(),title));
            panel.add(scroll);
            return panel;
        }

    }


    @Override
    public void show(String file) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame("test");
            frame.setVisible(true);
        });
    }

}
