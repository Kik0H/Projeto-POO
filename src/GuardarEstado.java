import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GuardarEstado {
    public static final String FILE_PATH = "DomusControl_data.ser";

    public static void guardarEstado(DomusControl motor) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(motor);
        }
    }

    public static DomusControl carregarEstado() throws IOException, ClassNotFoundException {
        File file = new File(FILE_PATH);

        if(!file.exists()){
            return new DomusControl();
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))){
            DomusControl c =(DomusControl) ois.readObject();
            return c;
        }
    }
}
