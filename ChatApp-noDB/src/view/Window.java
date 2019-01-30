package view;

import javax.swing.JFrame;

//Basic window class
public class Window extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public Window(){
        this.setTitle("ChatSystem");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(false);
        this.setResizable(false);
        this.setVisible(false);
    }

}