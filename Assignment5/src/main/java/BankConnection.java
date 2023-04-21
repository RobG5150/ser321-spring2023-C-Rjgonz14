import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BankConnection {
    protected Bank bank;
    private Socket socket;
    protected ObjectInputStream iStream;
    protected ObjectOutputStream oStream;
    public  BankConnection(Bank bankIn, Socket sock, ObjectInputStream is, ObjectOutputStream os){
        this.bank = bankIn;
        this.socket = sock;
        this.iStream = is;
        this.oStream = os;
    }
}
