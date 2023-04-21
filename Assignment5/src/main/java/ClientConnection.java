import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    protected Client client;
    private Socket socket;
    protected ObjectInputStream iStream;
    protected ObjectOutputStream oStream;
    public  ClientConnection(Client cli, Socket sock, ObjectInputStream is, ObjectOutputStream os){
        this.client = cli;
        this.socket = sock;
        this.iStream = is;
        this.oStream = os;
    }
}