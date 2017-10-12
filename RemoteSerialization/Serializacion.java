import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import static java.lang.System.out;


public class Serializacion {

    public static void main(String[] args) throws  FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException{
        if (args[0] == null || args[0].length() != 8)
            System.out.printf("Debe pasar como parametro su DNI sin letra");
        else{
            try{
                FileInputStream fichero = new FileInputStream("fichero.salida");
                ObjectInputStream in = new ObjectInputStream(fichero);

                ClaseASerializar serializado = new ClaseASerializar();
                serializado = (ClaseASerializar)in.readObject();
                String ruta = serializado.obtenerDirecciónClase();
                System.out.printf("La ruta donde encontraras el bytecode de la clase es: \n" + serializado.obtenerRutaCompleta() + "\n\n");

                in.close();
                fichero.close();     

                //Ahora habria que mover el archivo descargado a la ruta de trabajo actual

                URL classLoaderUrls = new URL(ruta);
                URL[] classLoaderUrl1 = {classLoaderUrls};

                URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrl1);
                
                Class clase = urlClassLoader.loadClass(serializado.obtenerNombreClase());

                Object objeto = clase.newInstance();

                System.out.println("Los atributos de la clase son: \n");
                for(Field f: objeto.getClass().getDeclaredFields()) {
                    System.out.println("- " + f.getName());
                }
                System.out.println("\n Los metodos de la clase son: \n");
                for(Method m: clase.getDeclaredMethods()){
                    System.out.println("- " + m.getName());
                }

                try{

                    Method lMethod = clase.getDeclaredMethod("computa", String.class);
                    int result = (Integer)lMethod.invoke(objeto, args[0]);
                    System.out.println("\n Resultado obtenido del computo con el dni: " + result + "\n");

                } catch(NoSuchMethodException e) {
                    System.out.println(e.toString());
                } catch (IllegalAccessException x) {
                    System.out.println(x.toString());
                }
            }
            catch(Exception e){
                System.out.println(e.toString());
            }
        }   
    }
}
