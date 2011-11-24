package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.filechooser.*;

public class MainPanel extends JPanel {
    private final JTextArea log = new JTextArea();
    public MainPanel() {
        super(new BorderLayout());
        final JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder("JFileChooser"));
        p.add(new JButton(new AbstractAction("readOnly") {
            @Override public void actionPerformed(ActionEvent ae) {
                UIManager.put("FileChooser.readOnly", Boolean.TRUE);
                JFileChooser fileChooser = new JFileChooser(".");
                int retvalue = fileChooser.showOpenDialog(p);
                if(retvalue==JFileChooser.APPROVE_OPTION) {
                    log.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        }));
        p.add(new JButton(new AbstractAction("Rename only File#canWrite()==true") {
            @Override public void actionPerformed(ActionEvent ae) {
                UIManager.put("FileChooser.readOnly", Boolean.FALSE);
                JFileChooser fileChooser = new JFileChooser(".") {
                    @Override protected void setUI(ComponentUI ui) {
                        if(ui instanceof com.sun.java.swing.plaf.windows.WindowsFileChooserUI) {
                            super.setUI(WindowsCanWriteFileChooserUI.createUI(this));
                        }else{
                            super.setUI(CanWriteFileChooserUI.createUI(this));
                        }
                    }
                };
//         ActionMap am = fc.getActionMap();
//         //WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,editFileName,pressed F2
//         final Action editFileNameAction = am.get("editFileName");
//         am.put("editFileName", new AbstractAction("editFileName2") {
//             @Override public void actionPerformed(ActionEvent e) {
//                 File file = fc.getSelectedFile();
//                 if(file!=null && file.canWrite()) {
//                     editFileNameAction.actionPerformed(e);
//                 }
//             }
//         });
//                 Action newFolder = am.get("New Folder");
//                 newFolder.setEnabled( false );

                int retvalue = fileChooser.showOpenDialog(p);
                if(retvalue==JFileChooser.APPROVE_OPTION) {
                    log.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        }));
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(log));
        setPreferredSize(new Dimension(320, 200));
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class WindowsCanWriteFileChooserUI extends com.sun.java.swing.plaf.windows.WindowsFileChooserUI{
    protected WindowsCanWriteFileChooserUI(JFileChooser chooser) {
        super(chooser);
    }
    public static ComponentUI createUI(JComponent c) {
        return new WindowsCanWriteFileChooserUI((JFileChooser)c);
    }
    private BasicDirectoryModel model2 = null;
    @Override public void createModel() {
        if(model2!=null) {
            model2.invalidateFileCache();
        }
        model2 = new BasicDirectoryModel(getFileChooser()) {
            @Override public boolean renameFile(File oldFile, File newFile) {
                return oldFile.canWrite()?super.renameFile(oldFile, newFile):false;
            }
        };
    }
    @Override public BasicDirectoryModel getModel() {
        return model2;
    }
}

class CanWriteFileChooserUI extends javax.swing.plaf.metal.MetalFileChooserUI{
    protected CanWriteFileChooserUI(JFileChooser chooser) {
        super(chooser);
    }
    public static ComponentUI createUI(JComponent c) {
        return new CanWriteFileChooserUI((JFileChooser)c);
    }
    private BasicDirectoryModel model2 = null;
    @Override public void createModel() {
        if(model2!=null) {
            model2.invalidateFileCache();
        }
        model2 = new BasicDirectoryModel(getFileChooser()) {
            @Override public boolean renameFile(File oldFile, File newFile) {
                return oldFile.canWrite()?super.renameFile(oldFile, newFile):false;
            }
        };
    }
    @Override public BasicDirectoryModel getModel() {
        return model2;
    }
}