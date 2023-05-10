import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.InputMismatchException;

public class Client {
    public static void main(String[] args){
        ClientFrame cf = new ClientFrame();
        cf.setTitle("雙人聊天室");
    }
}

class ClientFrame extends JFrame{
    public ClientPanel cp ;

    public ClientFrame(){
        setSize(700,700);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cp = new ClientPanel(null);
        add(cp);
        setVisible(true);
    }
}

class ClientPanel extends JPanel implements ActionListener{
    public ClientFrame cf;
    public Connection cc ;

    public JLabel l1 = new JLabel("地址：");
    public JLabel l2 = new JLabel("端口：");
    public JLabel l3 = new JLabel("id:");
    
    public JTextField t1 = new JTextField("");
    public JTextField t2 = new JTextField("11999");
    public JTextField t3 = new JTextField("");

    public JButton b1 = new JButton("連接");

    public JTextArea ta1 = new JTextArea("");
    public JTextArea ta2 = new JTextArea("");
    public JTextArea ta3 = new JTextArea("");

    public JScrollPane s1 = new JScrollPane();
    public JScrollPane s2 = new JScrollPane();
    public JScrollPane s3 = new JScrollPane();

    public JButton b2 = new JButton("發送:");

    
    public ClientPanel(ClientFrame cf){
        this.cf = cf;
        cc = new Connection(this);

        setLayout(null);
        l1.setBounds(5,5,70,20);
        add(l1);
        t1.setBounds(80, 5, 100, 20);
        add(t1);
        l2.setBounds(190,5,70,20);
        add(l2);
        t2.setBounds(270, 5, 150, 20);
        add(t2);
        l3.setBounds(330,5,130,20);
        add(l3);
        t3.setBounds(470, 5, 50, 20);
        add(t3);
        b1.setBounds(560, 5, 100, 20);
        add(b1);

        s1.setBounds(5, 30, 670, 500);
        add(s1);
        s1.getViewport().add(ta1);
        ta1.setEditable(false);

        s2.setBounds(5, 540, 400, 110);
        add(s2);
        s2.getViewport().add(ta2);

        b2.setBounds(410, 540, 100, 110);
        add(b2);

        s3.setBounds(515, 540, 155, 110);
        add(s3);
        s3.getViewport().add(ta3);
        ta3.setEditable(false);

        b1.addActionListener(this);
        b2.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == b1){
            Connect();
        }
        else if(e.getSource() == b2){
            sendMessage();
        }
    }
    private void Connect() {
        try{
            String address = t1.getText();
            int port = Integer.parseInt(t2.getText());
            if(port<0 || port>65535){
                throw new InputMismatchException();
            }
            String ID = t3.getText();
            if(Integer.parseInt(ID)<0||Integer.parseInt(ID)>1023){
                throw new InputMismatchException();
            }
            cc.connect(address,port,ID);
        }
        catch (NumberFormatException e){
            SendLogs("格式錯誤");
        }
        catch (InputMismatchException e){
            SendLogs("端口不在正常範圍或者ＩＤ範圍錯誤");
        }
    }

    public void SendLogs(String s){

    }
    public void AcceptMessage(String s){

    }
    public void sendMessage(){

    }
    public void InserMessage(boolean IsMe,String s){

    }
class Connection{

}




    
    
}