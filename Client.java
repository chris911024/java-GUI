import javax.swing.*;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
        ta3.setText(ta3.getText() + "\n" + "我:" + s);
    }
    public void AcceptMessage(String s){
        String S =s.replace("\\n","\n");
        InserMessage(false,S);
    }
    public void sendMessage(){
        String str = ta2.getText().replace("\n","\\n");
        cc.SendData(Connection.Message, str);
        InserMessage(true,ta2.getText());
        ta2.setText("");
    }
    public void InserMessage(boolean IsMe,String s){
        if(!IsMe){
            ta1.setText(ta1.getText() + "\n" + "對方" + s);
        }
        else{
            ta1.setText(ta1.getText() + "\n" + "我:" + s);
        }
    }
class Connection{
    Socket sk;
    ClientPancl cp;
    
    InputStream is;
    OutputStream os;
    BufferedWriter bw;
    BufferedReader br;

    AccrptThread at;

    public static final String Message = "M";
    public static final String Status = "S";
    public static final String Exception = "E";

    public static final String StatusID = "ID";
    public static final String StatusSUCCESS = "SU";
    public static final String StatusPARTYDISCONNECT = "PD";
    public static final String StatusPARTYCONNECT = "PC";

    public Connection(ClientPancl cp){
        this.cp=cp;
    }
    public Connection(ClientPanel clientPanel) {
    }
    public void connect(String address, int port,String ID){
        try{
            sk = new Socket(address, port);
            is = sk.getInputStream();
            os = sk.getOutputStream();
            bw = new BufferedWriter( new OutputStreamWriter(os, StandardCharsets.UTF_8));
            br = new BufferedReader( new InputStreamReader(is,StandardCharsets.UTF_8));
            SendData(Status,StatusID + ID);
            at = new AccrptThread(this);
        }
        catch (Exception e){
            String s = e.getMessage();
            if(s.equals("Commection refused connect")){
                cp.SendLogs("無法連接伺服器，請確認伺服器是否開啟，請稍後再試");
            }else if (s.equals("commection timed out :connect")){
                cp.SendLogs("無法連接伺服器，請確認伺服器是否開啟，請稍後再試");
            }else{
                cp.SendLogs("其他錯誤原因:"+e);
            }
        }
    }
    private void SendData(String Type, String Connect) {
        try{
            bw.write(Type + "/n");
            bw.write(Connect + "/n");
            bw.flush();
        }catch(Exception e){
            cp.SendLogs("發生錯誤" + e.getMessage());
        }
    }
    public void AcceptMessage(String s){
        cp.AcceptMessage(s);
    }
    public void AcceptStatus(String s){
        switch (s){
            case StatusSUCCESS:
                cp.SendLogs("連接服務器成功");
                ((Frame) cp.cf).setTitle("已連接至服務器-對方未在線");
                break;
            case StatusPARTYCONNECT:
            cp.SendLogs("對方已經連線");
            ((Frame) cp.cf).setTitle("已連接至服務器-對方在線");
                break;
            case StatusPARTYDISCONNECT:
            cp.SendLogs("對方未連線");
            ((Frame) cp.cf).setTitle("已連接至服務器-對方在線");
            break;
        }
    }
    public void AcceptException(String s){
        cp.SendLogs("服務器異常" + s);
    }
}
class AcceptThread extends Thread{
    Connection cc;

    public AcceptThread(Connection cc){
        this.cc = cc;
        start();
    }
    @Override
    public void run(){
        while(true){
            if(cc.sk != null){
                try{
                    String s = cc.br.readLine();
                    if(s != null){
                        switch(s){
                            case Connection.Status:{
                                String ss = cc.br.readLine();
                                 while ( ss == null){
                                ss = cc.br.readLine();
                                }
                                cc.AcceptStatus(ss);
                                break;
                            }
                            case Connection.Message:{
                                String ss = cc.br.readLine();
                                while (ss == null){
                                    ss = cc.br.readLine();
                                }
                                cc.AcceptMessage(ss);
                                break;
                            }
                            case Connection.Exception:{
                                String ss = cc.br.readLine();
                                while( ss == null){
                                    ss = cc.br.readLine();
                                }
                                cc.AcceptException(ss);
                                break;
                            }
                        }
                    }
                } catch(IOException e){
                    if(e.getMessage().equals("connection reset")){
                        cc.cp.SendLogs("服務器已經斷開，可能是因為服務器已經關閉");
                    }
                    else{
                        System.out.println("IO錯誤" + e);
                }
                    break;
                }
            }
        }
    }
}
}