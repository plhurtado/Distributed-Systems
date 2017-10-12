import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;


public class AnilloToken extends Thread {
    int pid; 
    int retardo; 
    int ID;
    int portNo;
    ServerSocket servidor;
    Socket socket;
    Timer reloj;
    Interfaz guiBox;
    boolean activo = true;
    boolean inicio = true;


    AnilloToken(int ID, int portNo, Interfaz guiElement) {
        this.pid = ID;
        this.portNo = portNo;
        this.guiBox = guiElement;
        this.ID = ID;

        retardo = ID*4* 1000;
    }

    public void timer() throws IOException{
    	servidor = new ServerSocket(portNo); 
        servidor.setSoTimeout(retardo);
    }
    
    public String eleccion() {
        String msg = "Eleccion - " + pid;
        guiBox.outputStatus("Proceso " + pid + " : " + msg);
        return msg;
    }
    public void run() {
        try {
            timer();
            enviar();
       } catch (IOException ex) {
            Logger.getLogger(AnilloToken.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean comenzarEleccion(boolean bool){
    	 String msg = eleccion();
         pintarMsg(msg);
         bool = false;
         return bool;
    	}
    
    public void enviar() throws IOException {
        while (activo) {
            try {
                if (pid == 1 && inicio == true) {
                	inicio = comenzarEleccion(inicio);
                }
                socket = servidor.accept(); 
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receiveMsg = br.readLine(); 
                System.out.println("Proceso: " + pid + " " + receiveMsg);
                descifrarMsg(receiveMsg);

            } catch (SocketTimeoutException x) {
                String message = eleccion(); 
                pintarMsg(message);
    
            } finally {
                    socket.close();
            }
        }
    }

    public int nuevoPort(int ID) {
        int portSig = ID + 50000;
        if (ID == 6) {
            portSig = 50000;
        }
        return portSig + 1;
    }

    public int ultimoPort(int ID) {
        int portUlti = ID;
        if (portUlti == 1) {
            portUlti = 7;
        }
        return portUlti - 1;
    }
    
    @SuppressWarnings({ "unused", "deprecation" })
	public void descifrarMsg(String texto) {
        String msg = texto, cabecera, proStart = "any", proStart2;
        int cordPro = 0;
        StringTokenizer dscMsg = new StringTokenizer(texto);
        cabecera = dscMsg.nextToken();
        switch (cabecera) {

        case "Eleccion":
            if (texto.length() > 12) {
                proStart = texto.substring(11, 12);
            }
            if (!proStart.equals(String.valueOf(pid))) {
                pintarMsg(msg + "," + pid);
                guiBox.outputStatus("Proceso " + pid + " : " + msg + "," + pid);
            } else {
                for (int k = 11; k < texto.length(); k++) {
                    if (cordPro < Integer.parseInt(texto.substring(k, k + 1))) {
                        cordPro = Integer.parseInt(texto.substring(k, k + 1));
                    }
                    k++;
                }
                msg = "Coordinador - " + cordPro;
                pintarMsg(msg);
                guiBox.outputStatus("Proceso " + pid + " : " + msg);
            }
        break;
        case "Coordinador":
            proStart2 = texto.substring(texto.length() - 1);
            if (!proStart2.equals(String.valueOf(pid))) {
                pintarMsg(msg);
                guiBox.outputStatus("Proceso " + pid + " : " + msg);
            } else {
                if (reloj == null) {
                    reloj = new Timer();
                    reloj.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String mssg = "Activo - " + pid;
                            pintarMsg(mssg);
                        }
                    }, 0, 4 * 1000);
                }
            }
        break;
        case "Activo":
            String creatorProcess = texto.substring(texto.length() - 1);
            if (reloj != null && pid < Integer.parseInt(creatorProcess)) {
                reloj.cancel();
            }
            if (ultimoPort(Integer.parseInt(creatorProcess)) != pid) {
                pintarMsg(texto);
            } else {
                pintarMsg("Proceso Activo");
            }
        break;
        case "Muerto":
            try {
                servidor.close();
                if (reloj != null) {
                    reloj.cancel();
                }
                activo = false;
                guiBox.outputStatus("Proceso " + pid + ": Muerto");
                this.stop();
            } catch (IOException ex) {
                Logger.getLogger(AnilloToken.class.getName()).log(Level.SEVERE, null, ex);
            }
        break;
        }
    }
    
    @SuppressWarnings("static-access")
	public void pintarMsg(String msg) {
        try {
            int port = nuevoPort(ID);
            socket = new Socket("127.0.0.1", port);
            PrintWriter toProcess = new PrintWriter(socket.getOutputStream());
            this.sleep(150);
            toProcess.println(msg);
            toProcess.close();
            socket.close();
        } catch (ConnectException ex) {
            if (ID == 6) {
                ID = 0;
            }
            ID++;
            pintarMsg(msg);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AnilloToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AnilloToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AnilloToken.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

