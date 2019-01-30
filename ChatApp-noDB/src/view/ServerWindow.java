package view;

import javax.swing.*;
import java.awt.*;

public class ServerWindow extends Window {
	
	private static final long serialVersionUID = 1L;
	protected JPanel loginPanel;
    public JTextArea TextArea;
    public JButton disconnectButton;

    public ServerWindow() {
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
    	this.setTitle("Distant Server");
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	int width = (int) (gd.getDisplayMode().getWidth() * 0.2);
    	//int height = (int) (gd.getDisplayMode().getHeight() * 0.2);
    	
    	//int width = 700;
    	int height =(int)(width * 0.5);
    	setSize(width,height);
    	setLocationRelativeTo(null);
    	
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        
        TextArea = new JTextArea();
        TextArea.setEditable(false);
        TextArea.setText("Server connect�");
        TextArea.setBackground(getBackground());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        loginPanel.add(TextArea, gbc);
    
        disconnectButton = new JButton();
        disconnectButton.setText("Disconnect");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(disconnectButton, gbc);
    }

   
    public JComponent $$$getRootComponent$$$() {
        return loginPanel;
    }

}

