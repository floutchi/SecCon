package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.time.chrono.Era;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseFileTask implements TaskManager {

    Matcher matcher;
    ClientHandler clientHandler;

    public EraseFileTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }


    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASEFILE) ([a-zA-Z0-9].{50,200})$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        var name = matcher.group(2);
        String filePath = Program.PATH + "\\" + name;
        File f = new File(filePath);
        if(f.exists()) {
            try {
                secureDelete(f);
            } catch (IOException e) {
                e.printStackTrace();
                clientHandler.sendMessage("ERASE_ERROR");
            }
            clientHandler.sendMessage("ERASE_OK");
        } else {
            clientHandler.sendMessage("ERASE_ERROR");
        }

    }

    public void secureDelete(File file) throws IOException {
        if (file.exists()) {
            long length = file.length();
            SecureRandom random = new SecureRandom();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(0);
            raf.getFilePointer();
            byte[] data = new byte[64];
            int pos = 0;
            while (pos < length) {
                random.nextBytes(data);
                raf.write(data);
                pos += data.length;
            }
            raf.close();
            file.delete();
        }
    }


}
