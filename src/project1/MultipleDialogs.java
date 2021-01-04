package project1;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MultipleDialogs {
    @SuppressWarnings("serial")
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame mainFrame = new JFrame("Main JFrame");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setPreferredSize(new Dimension(400, 300));

            JPanel panel = new JPanel();
            panel.add(new JButton(new AbstractAction("Two Dialogs") {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialog1 = new JDialog(mainFrame, "Dialog 1 -- modal", ModalityType.APPLICATION_MODAL);
                    dialog1.setPreferredSize(new Dimension(200, 100));
                    dialog1.pack();
                    dialog1.setLocationByPlatform(true);
                    JButton click = new JButton("클릭");
                    dialog1.add(click);
                    click.addActionListener(this);
                    

                    JDialog dialog2 = new JDialog(dialog1, "Dialog 2 -- nonmodal", ModalityType.MODELESS);
                    dialog2.setPreferredSize(new Dimension(200, 100));
                    dialog2.pack();
                    dialog2.setLocationByPlatform(true);
                    dialog2.setVisible(true);

                    dialog1.setVisible(true);
                }
            }));

            mainFrame.add(panel);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}