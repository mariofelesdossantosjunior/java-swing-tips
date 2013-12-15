package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;

public class MainPanel extends JPanel {
    private final JPanel breadcrumb = makePanel(11);
    private final JTree tree = new JTree();
    public MainPanel() {
        super(new BorderLayout());
        tree.setSelectionRow(0);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(node == null || node.isLeaf()) {
                    return;
                }else{
                    initBreadcrumbList(breadcrumb, tree);
                    breadcrumb.revalidate();
                    breadcrumb.repaint();
                }
            }
        });

        initBreadcrumbList(breadcrumb, tree);
        add(new JLayer<JPanel>(breadcrumb, new BreadcrumbLayerUI()), BorderLayout.NORTH);

        JComponent c = makeBreadcrumbList(tree, Arrays.asList("aaa", "bb", "c"));
        add(c, BorderLayout.SOUTH);
        add(new JScrollPane(tree));
        setPreferredSize(new Dimension(320, 240));
    }
    private static JPanel makePanel(int overlap) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING, -overlap, 0));
        p.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        p.setOpaque(false);
        return p;
    }
    private static void initBreadcrumbList(JPanel p, JTree tree) {
        p.removeAll();
        ButtonGroup bg = new ButtonGroup();
        TreePath tp = tree.getSelectionPath();
        Object[] list = tp.getPath();
        ArrayList<Object> al = new ArrayList<>();
        for(int i=0;i<list.length;i++) {
            al.add(list[i]);
            TreePath cur = new TreePath(al.toArray());
            AbstractButton b = makeButton(tree, cur, Color.ORANGE);
            p.add(b);
            bg.add(b);
        }
    }
    private static JComponent makeBreadcrumbList(JTree tree, List<String> list) {
        JPanel p = makePanel(5);
        ButtonGroup bg = new ButtonGroup();
        for(String title: list) {
            AbstractButton b = makeButton(null, new TreePath(title), Color.PINK);
            p.add(b);
            bg.add(b);
        }
        return p;
    }
    private static AbstractButton makeButton(final JTree tree, final TreePath path, Color color) {
        final ToggleButtonBarCellIcon icon = new ToggleButtonBarCellIcon();
        AbstractButton b = new JRadioButton(path.getLastPathComponent().toString()) {
            @Override public boolean contains(int x, int y) {
                if(icon==null || icon.area==null) {
                    return super.contains(x, y);
                }else{
                    return icon.area.contains(x, y);
                }
            }
        };
        if(tree!=null) {
            b.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    JRadioButton r = (JRadioButton)e.getSource();
                    tree.setSelectionPath(path);
                    r.setSelected(true);
                }
            });
        }
        b.setIcon(icon);
        b.setVerticalAlignment(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setBackground(color);
        return b;
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

//http://terai.xrea.jp/Swing/ToggleButtonBar.html
class ToggleButtonBarCellIcon implements Icon {
    public Shape area;
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Container parent = c.getParent();
        if(parent==null) {
            return;
        }
        int h = c.getHeight()-1;
        int h2 = h/2;
        int w = c.getWidth()-1-h2;
        x += h2;

        Graphics2D g2 = (Graphics2D)g.create();
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Path2D.Float p = new Path2D.Float();
        if(c==parent.getComponent(0)) {
            //:first-child
            p.moveTo(x, y);
            p.lineTo(x + w - h2, y);
            p.lineTo(x + w,      y + h2);
            p.lineTo(x + w - h2, y + h);
            p.lineTo(x,          y + h);
        }else{
            p.moveTo(x - h2,     y);
            p.lineTo(x + w - h2, y);
            p.lineTo(x + w,      y + h2);
            p.lineTo(x + w - h2, y + h);
            p.lineTo(x - h2,     y + h);
            p.lineTo(x,          y + h2);
        }
        p.closePath();
        area = p;

        Color bgc = parent.getBackground();
        Color borderColor = Color.GRAY.brighter();
        if(c instanceof AbstractButton) {
            ButtonModel m = ((AbstractButton)c).getModel();
            if(m.isSelected() || m.isRollover()) {
                bgc = c.getBackground();
                borderColor = Color.GRAY;
            }
        }
        g2.setPaint(bgc);
        g2.fill(area);
        g2.setPaint(borderColor);
        g2.draw(area);
        g2.dispose();
    }
    @Override public int getIconWidth()  {
        return 100;
    }
    @Override public int getIconHeight() {
        return 21;
    }
}

class BreadcrumbLayerUI extends LayerUI<JPanel> {
    private Shape shape;
    @Override public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if(shape!=null) {
            Graphics2D g2 = (Graphics2D)g.create();
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(Color.GRAY);
            g2.draw(shape);
            g2.dispose();
        }
    }
    @Override public void installUI(JComponent c) {
        super.installUI(c);
        ((JLayer)c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
    @Override public void uninstallUI(JComponent c) {
        ((JLayer)c).setLayerEventMask(0);
        super.uninstallUI(c);
    }
    private void update(MouseEvent e, JLayer<? extends JPanel> l) {
        int id = e.getID();
        Shape s = null;
        if(id==MouseEvent.MOUSE_ENTERED || id==MouseEvent.MOUSE_MOVED) {
            Component c = e.getComponent();
            if(c instanceof AbstractButton) {
                AbstractButton b = (AbstractButton)c;
                if(b.getIcon() instanceof ToggleButtonBarCellIcon) {
                    ToggleButtonBarCellIcon icon = (ToggleButtonBarCellIcon)b.getIcon();
                    Rectangle r = c.getBounds();
                    AffineTransform at = AffineTransform.getTranslateInstance(r.x, r.y);
                    s = at.createTransformedShape(icon.area);
                }
            }
        }
        if(s!=shape) {
            shape = s;
            l.getView().repaint();
        }
    }
    @Override protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        update(e, l);
    }
    @Override protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        update(e, l);
    }
}