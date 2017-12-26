package UI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ExpTrkLogin extends JFrame
{
    private JLabel userNameLabel = new JLabel("User Name");
    private JLabel uPwdLabel = new JLabel("Password");
    private JTextField uNameText = new JTextField();
    private JPasswordField uPwdText = new JPasswordField();
    private JButton signin = new JButton("Sign In");
    private JLabel invalidSignon = new JLabel();
    private JPanel loginPanel = new JPanel();
    private ExpTrkScreenDetails screenDetails = new ExpTrkScreenDetails();
    private Dimension fSize = new Dimension(300,300);

    public ExpTrkLogin()
    {
        //Get the center of the screen to position the frame
        screenDetails.getCenterPosition(fSize);

        setTitle("Expense Tracker - Login");
        setPreferredSize(fSize);
        setLocation(screenDetails.getCenterPosition(fSize).width, screenDetails.getCenterPosition(fSize).height);
        setResizable(false);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SigninListener signinListener = new SigninListener(this);
        signin.addActionListener(signinListener);

        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        loginPanel.add(userNameLabel,c);

        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 256;
        loginPanel.add(uNameText,c);

        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 0;
        loginPanel.add(uPwdLabel,c);

        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 256;
        loginPanel.add(uPwdText,c);


        c.gridx = 0;
        c.gridy = 0;
        add(loginPanel);

        signin.setOpaque(true);
        signin.setForeground(Color.BLUE);

        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(signin,c);

        invalidSignon.setVisible(false);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(invalidSignon,c);

        pack();
    }

    private class SigninListener implements ActionListener
    {

        ExpTrkLogin parent;

        public SigninListener(ExpTrkLogin lg)
        {
            parent = lg;
        }
        public void actionPerformed(ActionEvent e)
        {
            if (parent.uNameText.getText().equals("") || parent.uPwdText.getPassword().toString().equals(""))
            {
                   parent.invalidSignon.setText("Invalid Credentials");
                   parent.invalidSignon.setVisible(true);
            }
            else
            {
                parent.dispose();
               // ExpDetailsGUI tranDetail = new ExpDetailsGUI();
               // tranDetail.setVisible(true);
            }
        }
    }
}
