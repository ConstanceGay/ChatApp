package view;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends Window {
	
	private static final long serialVersionUID = 1L;
	protected JPanel loginPanel;
    protected JTextArea welcomeTextArea;
    protected JTextArea loginTextArea;
    protected JTextArea SelectConnectTextArea;
    protected JTextField loginTextField; //field to enter login
    protected JButton loginButton;
    protected JRadioButton lanRadioButton;
    protected JRadioButton wanRadioButton;
    protected ButtonGroup selectConnectButtonGroup; //radiobuttons

    public LoginWindow() {
        selectConnectButtonGroup = new ButtonGroup();
        
        selectConnectButtonGroup.add(lanRadioButton);
        selectConnectButtonGroup.add(wanRadioButton);
        add(loginPanel);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
    	/*
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	int width = gd.getDisplayMode().getWidth();
    	int height = gd.getDisplayMode().getHeight();
    	
    	int new_width = (int) (gd.getDisplayMode().getWidth() * 0.17);
    	int new_height = (int) (gd.getDisplayMode().getHeight() * 0.2);
    	
    	System.out.println(width +" "+ height+"\n");
    	System.out.println(new_width +" "+ new_height);
    	
    	setSize(new_height,new_width);*/
    	setSize(500,500);
    	setLocationRelativeTo(null);
    	
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        final JPanel spacer1 = new JPanel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(spacer1, gbc);
        
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(spacer2, gbc);
        
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        loginPanel.add(spacer3, gbc);
        
        welcomeTextArea = new JTextArea();
        welcomeTextArea.setEditable(false);
        welcomeTextArea.setBackground(getBackground());
        Font welcomeTextAreaFont = this.$$$getFont$$$("Eras Demi ITC", -1, 24, welcomeTextArea.getFont());
        if (welcomeTextAreaFont != null) welcomeTextArea.setFont(welcomeTextAreaFont);
        welcomeTextArea.setForeground(new Color(-12236470));
        welcomeTextArea.setText("ChatSystem");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        loginPanel.add(welcomeTextArea, gbc);
        
        loginTextArea = new JTextArea();
        loginTextArea.setEditable(false);
        loginTextArea.setText("Enter your username :");
        loginTextArea.setBackground(getBackground());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        loginPanel.add(loginTextArea, gbc);
        
        loginTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginTextField, gbc);
        
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(90, 0, 0, 0);
        loginPanel.add(spacer4, gbc);
        
        SelectConnectTextArea = new JTextArea();
        SelectConnectTextArea.setEditable(false);
        SelectConnectTextArea.setBackground(getBackground());
        SelectConnectTextArea.setText("Select a connexion mode :");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        loginPanel.add(SelectConnectTextArea, gbc);
        
        lanRadioButton = new JRadioButton();
        lanRadioButton.setText("LAN - UDP Connexion");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(lanRadioButton, gbc);
        
        wanRadioButton = new JRadioButton();
        wanRadioButton.setText(" WAN - Presence Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(wanRadioButton, gbc);
        final JPanel spacer5 = new JPanel();
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        loginPanel.add(spacer5, gbc);
        loginButton = new JButton();
        loginButton.setText("Enter");
        
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(loginButton, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        loginPanel.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        loginPanel.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        loginPanel.add(spacer8, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return loginPanel;
    }

}

