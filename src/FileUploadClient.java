import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FileUploadClient extends JFrame {
    private static final int SERVER_PORT = 443;

    private JTextField fileTextField;
    private JTextField ipTextField;
    private JButton uploadButton;

    public FileUploadClient() {
        setTitle("File Upload Client");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        fileTextField = new JTextField();
        ipTextField = new JTextField();
        uploadButton = new JButton("Upload");

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        JPanel ipPanel = new JPanel(new BorderLayout());
        ipPanel.add(new JLabel("IP Address: "), BorderLayout.WEST);
        ipPanel.add(ipTextField, BorderLayout.CENTER);

        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(new JLabel("File Path: "), BorderLayout.WEST);
        filePanel.add(fileTextField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(uploadButton, BorderLayout.CENTER);

        add(ipPanel);
        add(filePanel);
        add(buttonPanel);
    }

    private void uploadFile() {
        String filePath = fileTextField.getText().trim();
        String serverIp = ipTextField.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the file path.");
            return;
        }

        if (serverIp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the IP Address.");
            return;
        }

        DataOutputStream dos = null;

        try {
            Socket socket = new Socket(serverIp, SERVER_PORT);
            System.out.println("Connected to server");

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer, 0, buffer.length)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            dos.flush();
            dos.close();
            fis.close();
            socket.close();

            JOptionPane.showMessageDialog(this, "File uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileUploadClient client = new FileUploadClient();
            client.setVisible(true);
        });
    }
}
