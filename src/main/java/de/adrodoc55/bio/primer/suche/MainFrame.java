package de.adrodoc55.bio.primer.suche;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import de.adrodoc55.bio.primer.suche.SequenceMatcher.IllegalPrimerException;
import de.adrodoc55.bio.primer.suche.SequenceMatcher.IllegalSequenceException;
import de.adrodoc55.common.gui.ColorListCellRenderer;
import de.adrodoc55.common.gui.Highlight;

public class MainFrame extends JFrame {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    private static final long serialVersionUID = 1L;

    private JScrollPane sequenceScrollPane;
    private JTextArea sequenceTextArea;

    private JPanel searchPanel;
    private JTextField primerTextField;
    private JButton btnSearch;
    private JCheckBox autoSearchCheckBox;

    private JPanel colorPanel;
    private JLabel lblPrimer;
    private JComboBox<Color> primerCb;
    private JLabel lblCompPrimer;
    private JComboBox<Color> compPrimerCb;
    private JLabel lblInvPrimer;
    private JComboBox<Color> invPrimerCb;
    private JLabel lblInvCompPrimer;
    private JComboBox<Color> invCompPrimerCb;

    public MainFrame() {
        super("Primer Suche");
        init();
        Toolkit kit = Toolkit.getDefaultToolkit();
        URL resource = getClass().getClassLoader().getResource("dna_helix-512.png");
        Image img = kit.createImage(resource);
        setIconImage(img);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void search() {
        try {
            Highlighter seqHighlighter = getSequenceTextArea().getHighlighter();
            try {
                seqHighlighter.removeAllHighlights();

                String sequence = getSequenceTextArea().getText();
                String primer = getPrimerTextField().getText();
                List<Highlight> matches = SequenceMatcher.getMatches(sequence, primer,
                        (Color) getPrimerCb().getSelectedItem());

                String complementaryPrimer = SequenceMatcher.getComplementary(primer);
                matches.addAll(SequenceMatcher.getMatches(sequence, complementaryPrimer,
                        (Color) getCompPrimerCb().getSelectedItem()));

                String reversedPrimer = new StringBuilder(primer).reverse().toString();
                matches.addAll(SequenceMatcher.getMatches(sequence, reversedPrimer,
                        (Color) getInvPrimerCb().getSelectedItem()));

                String reversedComplementaryPrimer = new StringBuilder(complementaryPrimer).reverse().toString();
                matches.addAll(SequenceMatcher.getMatches(sequence, reversedComplementaryPrimer,
                        (Color) getInvCompPrimerCb().getSelectedItem()));

                for (Highlight h : matches) {
                    try {
                        seqHighlighter.addHighlight(h.getStart(), h.getEnd(), h.getPainter());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (IllegalSequenceException ex) {
                HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                List<Integer> indecies = ex.getIndecies();
                for (int i : indecies) {
                    seqHighlighter.addHighlight(i, i + 1, painter);
                }
                throw ex;
            } catch (IllegalPrimerException ex) {
                Highlighter primerHighlighter = getPrimerTextField().getHighlighter();
                HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                List<Integer> indecies = ex.getIndecies();
                for (int i : indecies) {
                    primerHighlighter.addHighlight(i, i + 1, painter);
                }
                throw ex;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage(), "Exception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void init() {
        getContentPane().add(getSequenceScrollPane(), BorderLayout.CENTER);
        getContentPane().add(getSearchPanel(), BorderLayout.SOUTH);
        getContentPane().add(getOptionPanel(), BorderLayout.EAST);
    }

    private JScrollPane getSequenceScrollPane() {
        if (sequenceScrollPane == null) {
            sequenceScrollPane = new JScrollPane();
            sequenceScrollPane.setViewportView(getSequenceTextArea());
        }
        return sequenceScrollPane;
    }

    private JTextArea getSequenceTextArea() {
        if (sequenceTextArea == null) {
            sequenceTextArea = new JTextArea();
            sequenceTextArea.setColumns(80);
            sequenceTextArea.setRows(10);
            sequenceTextArea.setLineWrap(true);
        }
        return sequenceTextArea;
    }

    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new JPanel();
            searchPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            searchPanel.add(getPrimerTextField());
            searchPanel.add(getBtnSearch());
            searchPanel.add(getAutoSearchCheckBox());
        }
        return searchPanel;
    }

    private static final int MAX_AUTO_SUCHE = 2000;
    private JPanel optionPanel;

    private JTextField getPrimerTextField() {
        if (primerTextField == null) {
            primerTextField = new JTextField();
            primerTextField.setColumns(50);
            primerTextField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (getAutoSearchCheckBox().isSelected()) {
                        if (getSequenceTextArea().getText().length() > MAX_AUTO_SUCHE) {
                            JOptionPane
                                    .showMessageDialog(MainFrame.this,
                                            "Auto Suche kann nicht bei Sequenzen mit mehr als " + MAX_AUTO_SUCHE
                                                    + " Zeichen verwendet werden!",
                                            "Auto Suche", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        search();
                        return;
                    }

                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        search();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }
            });
        }
        return primerTextField;
    }

    private JButton getBtnSearch() {
        if (btnSearch == null) {
            btnSearch = new JButton("Suche");
            btnSearch.addActionListener(e -> {
                search();
            });
        }
        return btnSearch;
    }

    private JCheckBox getAutoSearchCheckBox() {
        if (autoSearchCheckBox == null) {
            autoSearchCheckBox = new JCheckBox("Auto Suche");
        }
        return autoSearchCheckBox;
    }

    private Component getColorPanel() {
        if (colorPanel == null) {
            colorPanel = new JPanel();
            GridBagLayout gbl_colorPanel = new GridBagLayout();
            gbl_colorPanel.columnWeights = new double[] { 1.0, 0.0 };
            colorPanel.setLayout(gbl_colorPanel);
            GridBagConstraints gbc_lblPrimer = new GridBagConstraints();
            gbc_lblPrimer.anchor = GridBagConstraints.NORTHWEST;
            gbc_lblPrimer.insets = new Insets(5, 5, 5, 5);
            gbc_lblPrimer.gridx = 0;
            gbc_lblPrimer.gridy = 0;
            colorPanel.add(getLblPrimer(), gbc_lblPrimer);
            GridBagConstraints gbc_primerCb = new GridBagConstraints();
            gbc_primerCb.anchor = GridBagConstraints.NORTH;
            gbc_primerCb.insets = new Insets(5, 5, 5, 5);
            gbc_primerCb.fill = GridBagConstraints.HORIZONTAL;
            gbc_primerCb.gridx = 1;
            gbc_primerCb.gridy = 0;
            colorPanel.add(getPrimerCb(), gbc_primerCb);
            GridBagConstraints gbc_lblInvPrimer = new GridBagConstraints();
            gbc_lblInvPrimer.anchor = GridBagConstraints.NORTHWEST;
            gbc_lblInvPrimer.insets = new Insets(5, 5, 5, 5);
            gbc_lblInvPrimer.gridx = 0;
            gbc_lblInvPrimer.gridy = 1;
            colorPanel.add(getLblInvPrimer(), gbc_lblInvPrimer);
            GridBagConstraints gbc_invPrimerCb = new GridBagConstraints();
            gbc_invPrimerCb.anchor = GridBagConstraints.NORTH;
            gbc_invPrimerCb.insets = new Insets(5, 5, 5, 5);
            gbc_invPrimerCb.fill = GridBagConstraints.HORIZONTAL;
            gbc_invPrimerCb.gridx = 1;
            gbc_invPrimerCb.gridy = 1;
            colorPanel.add(getInvPrimerCb(), gbc_invPrimerCb);
            GridBagConstraints gbc_lblCompPrimer = new GridBagConstraints();
            gbc_lblCompPrimer.insets = new Insets(5, 5, 5, 5);
            gbc_lblCompPrimer.anchor = GridBagConstraints.NORTHWEST;
            gbc_lblCompPrimer.gridx = 0;
            gbc_lblCompPrimer.gridy = 2;
            colorPanel.add(getLblCompPrimer(), gbc_lblCompPrimer);
            GridBagConstraints gbc_compPrimerCb = new GridBagConstraints();
            gbc_compPrimerCb.insets = new Insets(5, 5, 5, 5);
            gbc_compPrimerCb.anchor = GridBagConstraints.NORTH;
            gbc_compPrimerCb.fill = GridBagConstraints.HORIZONTAL;
            gbc_compPrimerCb.gridx = 1;
            gbc_compPrimerCb.gridy = 2;
            colorPanel.add(getCompPrimerCb(), gbc_compPrimerCb);
            GridBagConstraints gbc_lblInvCompPrimer = new GridBagConstraints();
            gbc_lblInvCompPrimer.anchor = GridBagConstraints.WEST;
            gbc_lblInvCompPrimer.insets = new Insets(5, 5, 5, 5);
            gbc_lblInvCompPrimer.gridx = 0;
            gbc_lblInvCompPrimer.gridy = 3;
            colorPanel.add(getLblInvCompPrimer(), gbc_lblInvCompPrimer);
            GridBagConstraints gbc_invCompPrimerCb = new GridBagConstraints();
            gbc_invCompPrimerCb.insets = new Insets(5, 5, 5, 5);
            gbc_invCompPrimerCb.fill = GridBagConstraints.HORIZONTAL;
            gbc_invCompPrimerCb.gridx = 1;
            gbc_invCompPrimerCb.gridy = 3;
            colorPanel.add(getInvCompPrimerCb(), gbc_invCompPrimerCb);

            colorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        }
        return colorPanel;
    }

    private static Color[] getColors() {
        Color[] result = { Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK,
                Color.RED, Color.YELLOW };
        return result;
    }

    private JLabel getLblPrimer() {
        if (lblPrimer == null) {
            lblPrimer = new JLabel("Primer");
        }
        return lblPrimer;
    }

    private JComboBox<Color> getPrimerCb() {
        if (primerCb == null) {
            primerCb = new JComboBox<Color>(getColors());
            primerCb.setSelectedIndex(1);
            primerCb.setRenderer(new ColorListCellRenderer());
        }
        return primerCb;
    }

    private JLabel getLblInvPrimer() {
        if (lblInvPrimer == null) {
            lblInvPrimer = new JLabel("inv. Primer");
        }
        return lblInvPrimer;
    }

    private JComboBox<Color> getInvPrimerCb() {
        if (invPrimerCb == null) {
            invPrimerCb = new JComboBox<Color>(getColors());
            invPrimerCb.setSelectedIndex(5);
            invPrimerCb.setRenderer(new ColorListCellRenderer());
        }
        return invPrimerCb;
    }

    private JLabel getLblCompPrimer() {
        if (lblCompPrimer == null) {
            lblCompPrimer = new JLabel("comp. Primer");
        }
        return lblCompPrimer;
    }

    private JComboBox<Color> getCompPrimerCb() {
        if (compPrimerCb == null) {
            compPrimerCb = new JComboBox<Color>(getColors());
            compPrimerCb.setSelectedIndex(8);
            compPrimerCb.setRenderer(new ColorListCellRenderer());
        }
        return compPrimerCb;
    }

    private JLabel getLblInvCompPrimer() {
        if (lblInvCompPrimer == null) {
            lblInvCompPrimer = new JLabel("inv. comp. Primer");
        }
        return lblInvCompPrimer;
    }

    private JComboBox<Color> getInvCompPrimerCb() {
        if (invCompPrimerCb == null) {
            invCompPrimerCb = new JComboBox<Color>(getColors());
            invCompPrimerCb.setSelectedIndex(6);
            invCompPrimerCb.setRenderer(new ColorListCellRenderer());
        }
        return invCompPrimerCb;
    }

    private JPanel getOptionPanel() {
        if (optionPanel == null) {
            optionPanel = new JPanel();
            optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
            optionPanel.add(getColorPanel());
        }
        return optionPanel;
    }
}
