package bsu.rfe.java.Lab7.B3.Mashkantsev;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import java.util.ArrayList;


public class InstantMessenger {
    private String sender;
    private MainFrame frame;
    private String message;
    ArrayList<MessageListener> listeners = new ArrayList<MessageListener>();


    public InstantMessenger(final MainFrame frame) {
        this.frame = frame;
        startServer();
    }


    public void sendMessage(String message,Peer sender) {
        try{
            // Убеждаемся, что поля не пустые
            if   (sender.GetSender().isEmpty()) { JOptionPane.showMessageDialog(frame,
                    "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if   (sender.GetdestinationAddress().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if   (message.isEmpty()) { JOptionPane.showMessageDialog(frame,
                    "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
           // frame.appendMessage("Я -> "+ sender.GetdestinationAddress() + ": " + message);
            // Создаем сокет для соединения
            final Socket socket =  new Socket(sender.GetdestinationAddress(), frame.getServerPort());
            // Открываем поток вывода данных
            final DataOutputStream out =  new DataOutputStream(socket.getOutputStream());
            // Записываем в поток имя
            out.writeUTF(sender.GetSender());
            // Записываем в поток сообщение
            out.writeUTF(message);
            // Закрываем сокет
            socket.close();
            frame.appendMessage("Я -> "+ sender.GetdestinationAddress() + ": " + message);
            // Очищаем текстовую область ввода сообщения
            frame.getTextAreaOutgoing().setText("");
        }catch(UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch(IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Не удалось отправить сообщение",
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startServer(){
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final ServerSocket serverSocket =  new ServerSocket(frame.getServerPort());
                    while(!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream( socket.getInputStream());
                        // Читаем имя отправителя
                        final String senderName = in.readUTF();
                        // Читаем сообщение
                        final String message = in.readUTF();
                        // Закрываем соединение
                        socket.close();
                        // Выделяем IP-адрес
                        final String address =  ((InetSocketAddress) socket
                                .getRemoteSocketAddress())
                                .getAddress()
                                .getHostAddress();
                        // Выводим сообщение в текстовую область
                        frame.appendMessage_2(message, senderName,address);
                    }
                } catch(IOException e) {
                    e.printStackTrace(); JOptionPane.showMessageDialog(frame,
                            "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }

    public void addMessageListener(MessageListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }
    public void removeMessageListener(MessageListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }
    private void notifyListeners(Peer sender, String message) {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                listener.messageReceived(sender, message);
            }
        }
    }
}
