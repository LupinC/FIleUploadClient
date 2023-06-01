import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class FileUploadClient extends JFrame {
    private static final String SERVER_IP = "10.0.0.12"; //73 106 183 226
    private static final int SERVER_PORT = 443;

    private JTextField fileTextField;
    private JButton uploadButton;

    public FileUploadClient() {
        setTitle("File Upload Client");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fileTextField = new JTextField();
        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(fileTextField, BorderLayout.CENTER);
        panel.add(uploadButton, BorderLayout.EAST);

        add(panel, BorderLayout.CENTER);
    }

    private void uploadFile() {
        String filePath = fileTextField.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the file path.");
            return;
        }

        DataOutputStream dos = null;

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
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

