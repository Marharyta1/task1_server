import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Server {

    private static Socket socket;

    public static void main(String[] args) {
        try {

            int port = 25000;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port 25000");

            while (true) {

                socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ServerThread implements Runnable {
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public ServerThread(Socket socket) {
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String message = br.readLine();
                System.out.println("got message: " + message);

                ClientMessage clientMessage = new ClientMessage();
                clientMessage.parseToString(message);
                TimeUnit.SECONDS.sleep(1);
                System.out.println("message from " + clientMessage.getName() + ": " + clientMessage.getMessage());

                String returnMessage = "{ \"message\" : \"Hello," + clientMessage.getName() + "!\" }\n";

                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(returnMessage);
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        protected class ClientMessage {

            public void parseToString (String source) {
                String [] fields = source.split("\"");
                for (int i = 1; i< fields.length-2; i+=2) {
                    if(fields[i].equals("name"))
                        setName(fields[i+2]);
                    else if (fields[i].equals("message"))
                        setMessage(fields[i+2]);
                }
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            private String name;
            private String message;
        }
    }
}
