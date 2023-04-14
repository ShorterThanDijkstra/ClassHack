package com.github.std.classhack.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.std.classhack.classreader.ClassFile;

import javax.swing.*;
import java.awt.*;

public class Graphic implements UI {
    private static class MainFrame extends JFrame {
        private final JTextArea infoDisplay = new JTextArea();
        private final JTextArea basicDisplay = new JTextArea();

        private ClassFile classFile;

        private void openFile(String file) {
            System.out.println("openFile");
        }

        private void menuBar(String file) {
            JMenuBar menuBar = new JMenuBar();

            JButton open = new JButton("Open");
            open.addActionListener(e -> openFile(file));
            open.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 10));
            menuBar.add(open);

            JButton constant = new JButton("ConstantPool");
            constant.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 10));

            constant.addActionListener(e -> showConstantPool());
            menuBar.add(constant);

            JButton fieldsMethods = new JButton("Fields&Methods");
            fieldsMethods.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 10));
            fieldsMethods.addActionListener(e -> showFieldsMethods());
            menuBar.add(fieldsMethods);


            setJMenuBar(menuBar);
        }

        private void showFieldsMethods() {
            System.out.println("showFieldsMethods");
        }

        private void showConstantPool() {
            System.out.println("showConstantPool");
        }

        public MainFrame(String file) {
            FlatLightLaf.setup();
            setTitle("Class Hacker");
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            menuBar(file);

            JPanel mainPanel = new JPanel(new GridLayout(1, 3));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
            add(mainPanel);
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
