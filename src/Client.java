import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.QueueMeta;
import edu.cug.aliyun.MNSClientBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class Client {
    private JFrame frame;
    JTextArea contentArea;
    JComboBox comboBox;

    public static void main(String[] args)  throws UnknownHostException{
        Client window = new Client();
        window.frame.setVisible(true);

    }

    public Client() throws UnknownHostException {
        initialize();
        updateQueryList();
    }

    private void initialize() throws UnknownHostException {
        frame = new JFrame();
        frame.setBounds(620, 300, 567, 436);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        comboBox = new JComboBox();                          //队列列表
        comboBox.setToolTipText("");
        comboBox.setBounds(110, 14, 280, 27);
        frame.getContentPane().add(comboBox);

        final JButton loginButton = new JButton("创建列表");               //创建列表按钮
        loginButton.setFont(new Font("宋体", Font.PLAIN, 12));
        loginButton.setBounds(473, 14, 83, 27);
        frame.getContentPane().add(loginButton);

        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(400,  14, 70, 27);
        frame.getContentPane().add(scrollPane_3);

        final JTextArea createArea = new JTextArea();
        scrollPane_3.setViewportView(createArea);
        createArea.setLineWrap(true);

        JButton sendButton = new JButton("\u53D1\u9001");                //发送文本按钮
        sendButton.setFont(new Font("宋体", Font.PLAIN, 18));
        sendButton.setBounds(45, 349, 113, 27);
        frame.getContentPane().add(sendButton);

        JButton recevieButton = new JButton("接收");                //发送文本按钮
        recevieButton.setFont(new Font("宋体", Font.PLAIN, 18));
        recevieButton.setBounds(345, 349, 113, 27);
        frame.getContentPane().add(recevieButton);

        JLabel titleLabel = new JLabel("队列列表");
        titleLabel.setBackground(Color.yellow);
        titleLabel.setFont(new Font("宋体", Font.PLAIN, 20));
        titleLabel.setBounds(24, 13, 180, 24);
        frame.getContentPane().add(titleLabel);

        JLabel titleLabel1 = new JLabel("发送消息面版：");
        titleLabel1.setBackground(Color.yellow);
        titleLabel1.setFont(new Font("宋体", Font.PLAIN, 16));
        titleLabel1.setBounds(24, 55, 180, 24);
        frame.getContentPane().add(titleLabel1);

        JLabel titleLabel2 = new JLabel("接收消息面版：");
        titleLabel2.setBackground(Color.yellow);
        titleLabel2.setFont(new Font("宋体", Font.PLAIN, 16));
        titleLabel2.setBounds(284, 55, 180, 24);
        frame.getContentPane().add(titleLabel2);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(45, 276, 435, 66);
        frame.getContentPane().add(scrollPane);

        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        scrollPane.setViewportView(contentArea);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(45,  87, 200, 166);
        frame.getContentPane().add(scrollPane_1);

        final JTextArea messageArea = new JTextArea();
        scrollPane_1.setViewportView(messageArea);
        messageArea.setLineWrap(true);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(285,  87, 200, 166);
        frame.getContentPane().add(scrollPane_2);

        final JTextArea receiveMessageArea = new JTextArea();
        scrollPane_2.setViewportView(receiveMessageArea);
        messageArea.setLineWrap(true);

        sendButton.addActionListener(new ActionListener() {               //给发送文本按钮添加事件sendmessage
            public void actionPerformed(ActionEvent e) {
                String messageAreaText = messageArea.getText();
                if(messageAreaText == "") return;
                String recieverQueueName = comboBox.getSelectedItem().toString();
                try {
                    MNSClient mnsClient = MNSClientBuilder.getInstance();
                    CloudQueue queue = mnsClient.getQueueRef(recieverQueueName);// replace with your queue name

                    Message message = new Message();
                    message.setMessageBody(messageAreaText); // use your own message body here
                    Message putMsg = queue.putMessage(message);
                    System.out.println("消息发送成功");
                    contentArea.append("消息发送成功，消息内容为: " + messageAreaText + "\n");
                    messageArea.setText("");
                    mnsClient.close();

                }catch (ClientException ce)
                {
                    System.out.println("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.");
                    ce.printStackTrace();
                } catch (ServiceException se)
                {
                    if (se.getErrorCode().equals("QueueNotExist"))
                    {
                        System.out.println("Queue is not exist.Please create before use");
                    } else if (se.getErrorCode().equals("TimeExpired"))
                    {
                        System.out.println("The request is time expired. Please check your local machine timeclock");
                    }
            /*
            you can get more MNS service error code from following link:
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
                    se.printStackTrace();
                } catch (Exception ex)
                {
                    System.out.println("Unknown exception happened!");
                    ex.printStackTrace();
                }

            }
        });

        loginButton.addActionListener(new ActionListener() {              //创建队列
            public void actionPerformed(ActionEvent e) {
                String createAreaText = createArea.getText();
                if(createAreaText == "") return;
                MNSClient mnsClient = MNSClientBuilder.getInstance();
                try{
                    QueueMeta qMeta = new QueueMeta();
                    qMeta.setQueueName(createAreaText);
                    qMeta.setPollingWaitSeconds(30);//use long polling when queue is empty.
                    CloudQueue cQueue = mnsClient.createQueue(qMeta);
                    System.out.println("Create queue successfully. URL: " + cQueue.getQueueURL());
                    contentArea.append("队列创建成功，URL为: " +  cQueue.getQueueURL() + "\n");
                    updateQueryList();
                }catch (ClientException ce)
                {
                    System.out.println("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.");
                    ce.printStackTrace();
                } catch (ServiceException se)
                {
                    if (se.getErrorCode().equals("QueueNotExist"))
                    {
                        System.out.println("Queue is not exist.Please create before use");
                    } else if (se.getErrorCode().equals("TimeExpired"))
                    {
                        System.out.println("The request is time expired. Please check your local machine timeclock");
                    }
            /*
            you can get more MNS service error code in following link.
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
                    se.printStackTrace();
                } catch (Exception ex)
                {
                    System.out.println("Unknown exception happened!");
                    ex.printStackTrace();
                }

                mnsClient.close();
            }
        });

        recevieButton.addActionListener(new ActionListener() {               //给发送文本按钮添加事件sendmessage
            public void actionPerformed(ActionEvent e) {
                String recieverQueueName = comboBox.getSelectedItem().toString();
                MNSClient mnsClient = MNSClientBuilder.getInstance();
                try {
                    CloudQueue queue = mnsClient.getQueueRef(recieverQueueName);// replace with your queue name
                    for (int i = 0; i < 10; i++) {
                        Message popMsg = queue.popMessage();
                        receiveMessageArea.append(popMsg.getMessageBodyAsString() + "\n");
                        queue.deleteMessage(popMsg.getReceiptHandle());
                        System.out.println("成功接收消息，并在队列中删除\n");
                        contentArea.append("消息接收成功，消息内容为: " + popMsg.getMessageBodyAsString() + "\n");
                    }
                }catch (ClientException ce)
                {
                    System.out.println("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.");
                    ce.printStackTrace();
                } catch (ServiceException se)
                {
                    if (se.getErrorCode().equals("QueueNotExist"))
                    {
                        System.out.println("Queue is not exist.Please create queue before use");
                    } else if (se.getErrorCode().equals("TimeExpired"))
                    {
                        System.out.println("The request is time expired. Please check your local machine timeclock");
                    }
            /*
            you can get more MNS service error code in following link.
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
                    se.printStackTrace();
                } catch (Exception ex)
                {
                    System.out.println("Unknown exception happened!");
                    ex.printStackTrace();
                }

                mnsClient.close();
            }
        });
    }

    public void updateQueryList(){
        MNSClient mnsClient = MNSClientBuilder.getInstance();
        if(comboBox!=null){
            try
            {
                // List Queue
                String marker = null;
                do {
                    PagingListResult<QueueMeta> list = new PagingListResult<QueueMeta>();
                    try {
                        list = mnsClient.listQueue("", marker, 1);
                    } catch (ClientException ex) {
                        ex.printStackTrace();
                    } catch (ServiceException ex) {
                        ex.printStackTrace();
                    }
                    List<QueueMeta> queues = list.getResult();
                    marker = list.getMarker();

                    for (QueueMeta queue : queues) {
                        comboBox.addItem(queue.getQueueName());
                    }
                } while (marker != null && marker != "");

            } catch (ClientException ce)
            {
                System.out.println("Something wrong with the network connection between client and MNS service."
                        + "Please check your network and DNS availablity.");
                ce.printStackTrace();
            } catch (ServiceException se)
            {
                if (se.getErrorCode().equals("QueueNotExist"))
                {
                    System.out.println("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired"))
                {
                    System.out.println("The request is time expired. Please check your local machine timeclock");
                }
            /*
            you can get more MNS service error code in following link.
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
                se.printStackTrace();
            } catch (Exception e)
            {
                System.out.println("Unknown exception happened!");
                e.printStackTrace();
            }
        }
    }
}
