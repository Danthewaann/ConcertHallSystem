package concerthallsystem;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Black
 */

public class MessagePanel 
{
    public MessagePanel(){}
    
    public static void displayMessage(JFrame frame, String messageText, String messageTitle)
    {
        JOptionPane.showMessageDialog(
            frame, messageText, messageTitle, JOptionPane.PLAIN_MESSAGE
        );
    }
    
    public static String inputDialog(JFrame frame, String messageText, String messageTitle)
    {
        return JOptionPane.showInputDialog(
            frame, messageText, messageTitle, JOptionPane.PLAIN_MESSAGE               
        ).trim();
    }        
    
    public static int optionDialog(JFrame frame, String messageText, String messageTitle, Object[] options)
    {
        return JOptionPane.showOptionDialog(
            frame, messageText, messageTitle, JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );       
    }
}
