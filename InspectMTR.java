import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class InspectMTR {
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        URL url = file.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[] { url }, InspectMTR.class.getClassLoader());

        String[] classesToInspect = {
                "org.mtr.mod.Init",
                "org.mtr.core.simulation.Simulator"
        };

        for (String className : classesToInspect) {
            try {
                System.out.println("Inspecting " + className + "...");
                Class<?> clazz = classLoader.loadClass(className);
                for (Method m : clazz.getDeclaredMethods()) {
                    System.out.println("  Method: " + m.toString());
                }
                for (Field f : clazz.getDeclaredFields()) {
                    System.out.println("  Field: " + f.toString());
                }
            } catch (Exception e) {
                System.out.println("  Error: " + e.getMessage());
            }
        }
    }
}
