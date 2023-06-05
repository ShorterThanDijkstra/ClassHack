package com.github.std.classhack.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.std.classhack.classreader.ClassFile;
import com.github.std.classhack.classreader.ClassReader;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Graphic implements UI {
    private static class MainFrame extends JFrame {
        private final JTextPane basicDisplay = new JTextPane();
        private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        private ClassFileShow classShow;

        private void openFile() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);

            int res = fileChooser.showOpenDialog(this);
//            FileFilter fileFilter = new FileFilter() {
//                @Override
//                public boolean accept(File f) {
//                    return !f.isDirectory() && f.getName().endsWith(".class");
//                }
//
//                @Override
//                public String getDescription() {
//                    return "class file";
//                }
//            };
//            fileChooser.addChoosableFileFilter(fileFilter);
//            fileChooser.setFileFilter(fileFilter);
            if (res != JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
            try {
                File file = fileChooser.getSelectedFile();
                initClassFileShow(file);
                showBasic();
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(this, e.toString());
            }
        }

        private void initClassFileShow(File file) throws IOException {
            ClassReader classReader = new ClassReader(new FileInputStream(file));
            ClassFile classFile = classReader.getClassFile();
            classShow = new ClassFileShow(classFile, file.getPath());
        }

        private void insertText(JTextPane display, String str) {
            // 设置文本样式
            StyledDocument doc = display.getStyledDocument();
            Style style = doc.addStyle("Black", null);
            StyleConstants.setForeground(style, Color.BLACK);

            // 添加文本到文本区域
            try {
                display.setText("");
                doc.insertString(0, str, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }

        private void showBasic() {
            insertText(basicDisplay, classShow.showBasic());
        }

        private void showFieldsMethods() {
            JTextPane display = new JTextPane();
            insertText(display, classShow.showFieldsMethods());
            splitPane.setRightComponent(scrollText(display, "Fields&Methods"));
        }

        private void showConstantPool() {
            JTextPane display = new JTextPane();
            insertText(display, classShow.showConstantPool());
            splitPane.setRightComponent(scrollText(display, "Constant Pool"));

        }

        private void menuBar() {
            JMenuBar menuBar = new JMenuBar();

            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);
            JMenuItem open = new JMenuItem("Open Class File");
            open.addActionListener(e -> openFile());
            fileMenu.add(open);

            JMenu classMenu = new JMenu("Class");
            menuBar.add(classMenu);

            JMenuItem constantPool = new JMenuItem("Constant Pool");
            classMenu.add(constantPool);
            constantPool.addActionListener(e -> showConstantPool());

            JMenuItem fieldsMethods = new JMenuItem("Fields&Methods");
            classMenu.add(fieldsMethods);
            fieldsMethods.addActionListener(e -> showFieldsMethods());

            setJMenuBar(menuBar);
        }


        public MainFrame(String filename) {
            init();
            File file = new File(filename);
            try {
                initClassFileShow(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            showBasic();
        }

        private void init() {
            FlatLightLaf.setup();

            setVisible(true);
            setTitle("Class Hacker");
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            menuBar();

            JPanel mainPanel = new JPanel(new BorderLayout());
            splitPane.setDividerLocation(this.getWidth() / 3);
            Font font = new Font("宋体", Font.PLAIN, 16);

            basicDisplay.setFont(font);

            basicDisplay.setEditable(false);
            splitPane.setLeftComponent(scrollText(basicDisplay, "Basic"));
            mainPanel.add(splitPane);
            add(mainPanel);
        }

        public MainFrame() {
            init();
            openFile();

        }

        private JPanel scrollText(JTextPane display, String title) {
            JScrollPane scroll = new JScrollPane(display);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(new TitledBorder(new EtchedBorder(), title));
            panel.add(scroll);
            return panel;
        }


    }


    @Override
    public void show() {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    @Override
    public void show(String filename) {
        SwingUtilities.invokeLater(() -> new MainFrame(filename));
    }


}
